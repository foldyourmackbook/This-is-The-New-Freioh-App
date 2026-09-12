package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoPost
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FeedCategory
import com.example.ui.viewmodel.FreiohViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    viewModel: FreiohViewModel,
    onOpenSearch: () -> Unit,
    onOpenLive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.allPosts.collectAsState()
    val currentCategory by viewModel.currentFeedCategory.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.videoProgress.collectAsState()
    val heartParticles by viewModel.heartParticles.collectAsState()
    val activeCommentPostId by viewModel.activeCommentPostId.collectAsState()
    val currentComments by viewModel.currentPostComments.collectAsState()
    val activeSharePost by viewModel.activeSharePost.collectAsState()

    // Filter posts for active category or fallback to all
    val displayPosts = remember(posts, currentCategory) {
        val filtered = posts.filter { it.feedCategory == currentCategory.label }
        if (filtered.isNotEmpty()) filtered else posts
    }

    val pagerState = rememberPagerState(pageCount = {
        if (displayPosts.isEmpty()) 1 else displayPosts.size
    })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setVideoIndex(pagerState.currentPage)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (displayPosts.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { page -> if (page < displayPosts.size) displayPosts[page].id else page }
            ) { page ->
                val post = displayPosts[page]
                val isThisPlaying = isPlaying && pagerState.currentPage == page

                VideoFeedItem(
                    post = post,
                    isPlaying = isThisPlaying,
                    progress = if (pagerState.currentPage == page) progress else 0f,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onDoubleTap = { x, y -> viewModel.onDoubleTapVideo(post, x, y) },
                    onLikeToggle = { viewModel.togglePostLike(post) },
                    onSaveToggle = { viewModel.togglePostSave(post) },
                    onFollowToggle = { viewModel.toggleFollowAuthor(post) },
                    onOpenComments = { viewModel.openCommentsSheet(post.id) },
                    onOpenShare = { viewModel.openShareSheet(post) },
                    onAvatarClick = { viewModel.viewOtherUserProfile(post) },
                    onSoundClick = { viewModel.openSoundDrawer() }
                )
            }
        } else {
            // Empty state placeholder
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No videos found. Be the first to create one!", color = Color.White)
            }
        }

        // TOP HEADER BAR (LIVE, Category Tabs: Community, Following, For You, Search)
        TopFeedBar(
            currentCategory = currentCategory,
            onSelectCategory = { viewModel.selectFeedCategory(it) },
            onLiveClick = onOpenLive,
            onSearchClick = onOpenSearch,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
        )

        // Floating Hearts Layer (from double taps)
        heartParticles.forEach { particle ->
            HeartParticleView(particle = particle)
        }

        // Comment Sheet
        if (activeCommentPostId != null) {
            CommentBottomSheet(
                comments = currentComments,
                onClose = { viewModel.closeCommentsSheet() },
                onSendComment = { text -> viewModel.addComment(text) },
                onToggleCommentLike = { comment -> viewModel.toggleCommentLike(comment) }
            )
        }

        // Share Sheet
        if (activeSharePost != null) {
            ShareBottomSheet(
                post = activeSharePost!!,
                contacts = viewModel.sampleContacts,
                onClose = { viewModel.closeShareSheet() },
                onSendDirect = { contact ->
                    viewModel.sendMessage("Shared a video: ${activeSharePost!!.caption}")
                }
            )
        }
    }
}

@Composable
private fun TopFeedBar(
    currentCategory: FeedCategory,
    onSelectCategory: (FeedCategory) -> Unit,
    onLiveClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // LIVE STREAMING INDICATOR BADGE
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onLiveClick)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("top_bar_live"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3B5C))
                )
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp
                )
            }
        }

        // CENTER CATEGORY TABS
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryTabItem(
                category = FeedCategory.COMMUNITY,
                isSelected = currentCategory == FeedCategory.COMMUNITY,
                onClick = { onSelectCategory(FeedCategory.COMMUNITY) }
            )
            CategoryTabItem(
                category = FeedCategory.FOLLOWING,
                isSelected = currentCategory == FeedCategory.FOLLOWING,
                onClick = { onSelectCategory(FeedCategory.FOLLOWING) }
            )
            CategoryTabItem(
                category = FeedCategory.FOR_YOU,
                isSelected = currentCategory == FeedCategory.FOR_YOU,
                onClick = { onSelectCategory(FeedCategory.FOR_YOU) }
            )
        }

        // SEARCH ICON
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(36.dp)
                .testTag("top_bar_search")
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CategoryTabItem(
    category: FeedCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = category.label,
            color = if (isSelected) Color.White else TikTokGray.copy(alpha = 0.85f),
            fontSize = if (isSelected) 17.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        // White active underline indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.5.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        } else {
            Spacer(modifier = Modifier.height(2.5.dp))
        }
    }
}
