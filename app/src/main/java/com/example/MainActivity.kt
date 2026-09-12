package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.components.FreiohBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TikTokBlack
import com.example.ui.viewmodel.FreiohViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {

    private val viewModel: FreiohViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                FreiohApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreiohApp(viewModel: FreiohViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val conversations by viewModel.allConversations.collectAsState()
    val videoCallState by viewModel.videoCallState.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()
    val storyProgress by viewModel.storyProgress.collectAsState()
    val isStoreDialogOpen by viewModel.isStoreDialogOpen.collectAsState()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isInEditorScreen by viewModel.isInEditorScreen.collectAsState()

    val totalUnread = remember(conversations) {
        conversations.sumOf { it.unreadCount }
    }

    val appBgColor = if (isDarkMode) TikTokBlack else Color.White

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(appBgColor),
        containerColor = appBgColor,
        bottomBar = {
            // Hide bottom bar when recording video, active video call, viewing stories, or in settings
            if (!videoCallState.isInCall && activeStory == null && !isRecording && !isInEditorScreen && !isSettingsOpen) {
                FreiohBottomNavBar(
                    currentTab = currentTab,
                    unreadMessageCount = totalUnread,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = if (!videoCallState.isInCall && activeStory == null && !isRecording && !isInEditorScreen && !isSettingsOpen) {
                        innerPadding.calculateBottomPadding()
                    } else {
                        androidx.compose.ui.unit.Dp(0f)
                    }
                )
        ) {
            // MAIN TAB CONTENT
            when (currentTab) {
                MainTab.HOME -> {
                    FeedScreen(
                        viewModel = viewModel,
                        onOpenSearch = { viewModel.selectTab(MainTab.FRIENDS) },
                        onOpenLive = { viewModel.openStory(viewModel.allStories.value.firstOrNull() ?: return@FeedScreen) }
                    )
                }

                MainTab.FRIENDS -> {
                    DiscoverSearchScreen(
                        viewModel = viewModel
                    )
                }

                MainTab.CREATE -> {
                    CameraStudioScreen(
                        viewModel = viewModel,
                        onClose = { viewModel.selectTab(MainTab.HOME) }
                    )
                }

                MainTab.INBOX -> {
                    InboxScreen(
                        viewModel = viewModel
                    )
                }

                MainTab.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel
                    )
                }
            }

            // FULLSCREEN STORY VIEWER OVERLAY
            if (activeStory != null) {
                StoryViewerScreen(
                    story = activeStory!!,
                    progress = storyProgress,
                    onClose = { viewModel.closeStory() },
                    onSendReply = { reply ->
                        viewModel.sendMessage(
                            text = "Replied to story: $reply",
                            messageType = "TEXT"
                        )
                        viewModel.closeStory()
                    }
                )
            }

            // FULLSCREEN ACTIVE VIDEO CALL OVERLAY (E2EE)
            if (videoCallState.isInCall) {
                VideoCallScreen(
                    callState = videoCallState,
                    viewModel = viewModel
                )
            }

            // STORE & PLATFORMS INFO DIALOG
            if (isStoreDialogOpen) {
                StorePlatformsDialog(
                    onDismiss = { viewModel.closeStoreDialog() }
                )
            }

            // AUTH / LOGIN MODAL
            if (isAuthModalOpen) {
                AuthModalDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeAuthModal() }
                )
            }

            // SETTINGS AND PRIVACY OVERLAY
            if (isSettingsOpen) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeSettings() }
                )
            }
        }
    }
}
