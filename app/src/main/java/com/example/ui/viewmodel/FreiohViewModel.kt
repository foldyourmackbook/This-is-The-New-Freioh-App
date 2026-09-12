package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FreiohRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class MainTab {
    HOME,
    FRIENDS,
    CREATE,
    INBOX,
    PROFILE
}

enum class FeedCategory(val label: String) {
    LIVE("LIVE"),
    COMMUNITY("Community"),
    FOLLOWING("Following"),
    FOR_YOU("For You")
}

data class VideoCallState(
    val isInCall: Boolean = false,
    val callTitle: String = "",
    val callerAvatar: String = "",
    val isGroupCall: Boolean = false,
    val participantNames: List<String> = emptyList(),
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isFrontCamera: Boolean = true,
    val isSpeakerOn: Boolean = true,
    val callDurationSeconds: Int = 0,
    val isEncrypted: Boolean = true
)

data class HeartParticle(
    val id: String = UUID.randomUUID().toString(),
    val x: Float,
    val y: Float,
    val rotation: Float = (-25..25).random().toFloat(),
    val scale: Float = 1.0f
)

class FreiohViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FreiohRepository

    val allPosts: StateFlow<List<VideoPost>>
    val allStories: StateFlow<List<StoryItem>>
    val allConversations: StateFlow<List<Conversation>>
    val userProfile: StateFlow<UserProfile?>

    // Navigation State
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedProfileTab = MutableStateFlow(com.example.ui.screens.ProfileTab.POSTS)
    val selectedProfileTab: StateFlow<com.example.ui.screens.ProfileTab> = _selectedProfileTab.asStateFlow()

    // Feed State
    private val _currentFeedCategory = MutableStateFlow(FeedCategory.FOR_YOU)
    val currentFeedCategory: StateFlow<FeedCategory> = _currentFeedCategory.asStateFlow()

    private val _currentVideoIndex = MutableStateFlow(0)
    val currentVideoIndex: StateFlow<Int> = _currentVideoIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _videoProgress = MutableStateFlow(0.35f)
    val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

    // Floating Hearts Animation State (from double taps)
    private val _heartParticles = MutableStateFlow<List<HeartParticle>>(emptyList())
    val heartParticles: StateFlow<List<HeartParticle>> = _heartParticles.asStateFlow()

    // Comments Sheet
    private val _activeCommentPostId = MutableStateFlow<String?>(null)
    val activeCommentPostId: StateFlow<String?> = _activeCommentPostId.asStateFlow()

    val currentPostComments: StateFlow<List<CommentItem>>

    // Share Sheet
    private val _activeSharePost = MutableStateFlow<VideoPost?>(null)
    val activeSharePost: StateFlow<VideoPost?> = _activeSharePost.asStateFlow()

    // Story Viewer
    private val _activeStory = MutableStateFlow<StoryItem?>(null)
    val activeStory: StateFlow<StoryItem?> = _activeStory.asStateFlow()
    private val _storyProgress = MutableStateFlow(0f)
    val storyProgress: StateFlow<Float> = _storyProgress.asStateFlow()
    private var storyTimerJob: Job? = null

    // Video Recording & Editor Studio
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _selectedSpeed = MutableStateFlow("1x")
    val selectedSpeed: StateFlow<String> = _selectedSpeed.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Normal")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _maxDurationMode = MutableStateFlow("15s")
    val maxDurationMode: StateFlow<String> = _maxDurationMode.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isSoundDrawerOpen = MutableStateFlow(false)
    val isSoundDrawerOpen: StateFlow<Boolean> = _isSoundDrawerOpen.asStateFlow()

    private val _selectedSound = MutableStateFlow<AudioTrack?>(null)
    val selectedSound: StateFlow<AudioTrack?> = _selectedSound.asStateFlow()

    private val _isInEditorScreen = MutableStateFlow(false)
    val isInEditorScreen: StateFlow<Boolean> = _isInEditorScreen.asStateFlow()

    private val _overlayText = MutableStateFlow("")
    val overlayText: StateFlow<String> = _overlayText.asStateFlow()

    private val _postCaption = MutableStateFlow("")
    val postCaption: StateFlow<String> = _postCaption.asStateFlow()

    private var recordTimerJob: Job? = null

    // Chat & Messaging
    private val _activeConversation = MutableStateFlow<Conversation?>(null)
    val activeConversation: StateFlow<Conversation?> = _activeConversation.asStateFlow()

    val currentChatMessages: StateFlow<List<ChatMessage>>

    private val _isCreatingGroup = MutableStateFlow(false)
    val isCreatingGroup: StateFlow<Boolean> = _isCreatingGroup.asStateFlow()

    // Video Call
    private val _videoCallState = MutableStateFlow(VideoCallState())
    val videoCallState: StateFlow<VideoCallState> = _videoCallState.asStateFlow()
    private var callTimerJob: Job? = null

    // Contacts
    val sampleContacts = listOf(
        ContactPerson("c1", "Maya Rodriguez", "@maya.moves", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", isVerified = true, isOnline = true),
        ContactPerson("c2", "Chef Marco", "@chef_marco", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150", isVerified = true, isOnline = true),
        ContactPerson("c3", "Antish Official", "@antish", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", isVerified = true, isOnline = false),
        ContactPerson("c4", "Pixel Artisan", "@pixel_artisan", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150", isVerified = true, isOnline = true),
        ContactPerson("c5", "Elena Vance", "@elena_vibe", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", isVerified = false, isOnline = true),
        ContactPerson("c6", "Alex Turner", "@alex_beats", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", isVerified = false, isOnline = false)
    )

    // Trending Audio Tracks
    val trendingSounds = listOf(
        AudioTrack("s1", "Cyber Funk 2026 (Remix)", "DJ Neon Pulse", "0:30", "1.2M videos", isTrending = true),
        AudioTrack("s2", "original sound - ant / ish", "ant / ish", "0:15", "840K videos", isTrending = true),
        AudioTrack("s3", "Sizzle & Beats (Original Mix)", "Gourmet Sounds", "0:45", "320K videos", isTrending = true),
        AudioTrack("s4", "Midnight City Neon Walk", "SynthWave Dreams", "0:60", "95K videos", isTrending = false),
        AudioTrack("s5", "Lo-Fi Coffee Chill Beats", "Aesthetic Chill", "1:00", "2.1M videos", isTrending = true)
    )

    // Store & Platforms Dialog
    private val _isStoreDialogOpen = MutableStateFlow(false)
    val isStoreDialogOpen: StateFlow<Boolean> = _isStoreDialogOpen.asStateFlow()

    // Settings Screen
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Dark / Light Theme Mode
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    // Auth Screen
    private val _isAuthModalOpen = MutableStateFlow(false)
    val isAuthModalOpen: StateFlow<Boolean> = _isAuthModalOpen.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentAuthProvider = MutableStateFlow("Google")
    val currentAuthProvider: StateFlow<String> = _currentAuthProvider.asStateFlow()

    // Edit Profile Modal
    private val _isEditProfileOpen = MutableStateFlow(false)
    val isEditProfileOpen: StateFlow<Boolean> = _isEditProfileOpen.asStateFlow()

    // Inspecting Other User's Profile
    private val _viewingUserProfile = MutableStateFlow<UserProfile?>(null)
    val viewingUserProfile: StateFlow<UserProfile?> = _viewingUserProfile.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FreiohRepository(database)

        allPosts = repository.allPosts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allStories = repository.allStories.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allConversations = repository.allConversations.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        userProfile = repository.getUserProfile().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        currentPostComments = _activeCommentPostId.flatMapLatest { postId ->
            if (postId == null) flowOf(emptyList())
            else repository.getCommentsForPost(postId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        currentChatMessages = _activeConversation.flatMapLatest { conv ->
            if (conv == null) flowOf(emptyList())
            else repository.getMessagesForConversation(conv.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Continuous progress simulation loop for video playback
        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value && _activeStory.value == null && !_isInEditorScreen.value && !_videoCallState.value.isInCall) {
                    _videoProgress.value = (_videoProgress.value + 0.02f) % 1.0f
                }
                delay(200)
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        if (tab == MainTab.HOME) {
            _isPlaying.value = true
        } else {
            _isPlaying.value = false
        }
    }

    fun selectProfileTab(tab: com.example.ui.screens.ProfileTab) {
        _selectedProfileTab.value = tab
    }

    fun openSavedTab() {
        selectTab(MainTab.PROFILE)
        _selectedProfileTab.value = com.example.ui.screens.ProfileTab.SAVED
    }

    fun selectFeedCategory(cat: FeedCategory) {
        _currentFeedCategory.value = cat
        _currentVideoIndex.value = 0
    }

    fun setVideoIndex(index: Int) {
        _currentVideoIndex.value = index
        _videoProgress.value = 0f
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun onDoubleTapVideo(post: VideoPost, tapX: Float, tapY: Float) {
        // Add animated floating heart particle at tap location
        val newParticle = HeartParticle(x = tapX, y = tapY)
        _heartParticles.value = _heartParticles.value + newParticle

        // Auto clean particle after 1.2s
        viewModelScope.launch {
            delay(1200)
            _heartParticles.value = _heartParticles.value.filter { it.id != newParticle.id }
        }

        // Trigger like if not already liked
        if (!post.isLiked) {
            togglePostLike(post)
        }
    }

    fun togglePostLike(post: VideoPost) {
        viewModelScope.launch {
            repository.toggleLike(post)
        }
    }

    fun togglePostSave(post: VideoPost) {
        viewModelScope.launch {
            repository.toggleSave(post)
        }
    }

    fun toggleFollowAuthor(post: VideoPost) {
        viewModelScope.launch {
            repository.toggleFollow(post)
        }
    }

    // Comment Handling
    fun openCommentsSheet(postId: String) {
        _activeCommentPostId.value = postId
    }

    fun closeCommentsSheet() {
        _activeCommentPostId.value = null
    }

    fun addComment(text: String) {
        val postId = _activeCommentPostId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value
            repository.addComment(
                postId = postId,
                text = text.trim(),
                username = user?.handle?.removePrefix("@") ?: "you",
                displayName = user?.displayName ?: "You",
                avatarUrl = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
            )
        }
    }

    fun toggleCommentLike(comment: CommentItem) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    // Share Handling
    fun openShareSheet(post: VideoPost) {
        _activeSharePost.value = post
    }

    fun closeShareSheet() {
        _activeSharePost.value = null
    }

    // Stories Handling
    fun openStory(story: StoryItem) {
        _activeStory.value = story
        _storyProgress.value = 0f
        startStoryTimer()
    }

    fun closeStory() {
        storyTimerJob?.cancel()
        _activeStory.value = null
        _storyProgress.value = 0f
    }

    private fun startStoryTimer() {
        storyTimerJob?.cancel()
        storyTimerJob = viewModelScope.launch {
            for (i in 0..100) {
                _storyProgress.value = i / 100f
                delay(50) // 5 seconds total
            }
            // Move to next story or close
            val stories = allStories.value
            val currentIdx = stories.indexOfFirst { it.id == _activeStory.value?.id }
            if (currentIdx != -1 && currentIdx < stories.size - 1) {
                openStory(stories[currentIdx + 1])
            } else {
                closeStory()
            }
        }
    }

    // Camera Studio & Video Editing
    fun toggleRecording() {
        if (!_isRecording.value) {
            // Start recording
            _isRecording.value = true
            _recordingDurationSeconds.value = 0
            recordTimerJob = viewModelScope.launch {
                val maxSeconds = when (_maxDurationMode.value) {
                    "15s" -> 15
                    "60s" -> 60
                    else -> 600
                }
                while (_isRecording.value && _recordingDurationSeconds.value < maxSeconds) {
                    delay(1000)
                    _recordingDurationSeconds.value += 1
                }
                if (_isRecording.value) {
                    stopRecordingAndEnterEditor()
                }
            }
        } else {
            // Stop recording
            stopRecordingAndEnterEditor()
        }
    }

    private fun stopRecordingAndEnterEditor() {
        _isRecording.value = false
        recordTimerJob?.cancel()
        _isInEditorScreen.value = true
    }

    fun setSpeed(speed: String) {
        _selectedSpeed.value = speed
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setMaxDuration(duration: String) {
        _maxDurationMode.value = duration
    }

    fun flipCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun openSoundDrawer() {
        _isSoundDrawerOpen.value = true
    }

    fun closeSoundDrawer() {
        _isSoundDrawerOpen.value = false
    }

    fun selectSound(track: AudioTrack) {
        _selectedSound.value = track
        _isSoundDrawerOpen.value = false
    }

    fun setOverlayText(text: String) {
        _overlayText.value = text
    }

    fun setPostCaption(caption: String) {
        _postCaption.value = caption
    }

    fun discardRecording() {
        _isInEditorScreen.value = false
        _isRecording.value = false
        _recordingDurationSeconds.value = 0
        _overlayText.value = ""
        _postCaption.value = ""
    }

    fun publishVideo(asStory: Boolean = false) {
        viewModelScope.launch {
            val user = userProfile.value
            val captionText = if (_postCaption.value.isNotBlank()) _postCaption.value else "My new creative video on #Freioh 🚀✨"
            val sound = _selectedSound.value

            if (asStory) {
                val newStory = StoryItem(
                    id = UUID.randomUUID().toString(),
                    username = "You",
                    userDisplayName = user?.displayName ?: "Your Story",
                    avatarUrl = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    mediaGradientColors = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
                    caption = captionText,
                    isViewed = false,
                    isUserStory = true
                )
                repository.publishNewStory(newStory)
            } else {
                val newPost = VideoPost(
                    id = UUID.randomUUID().toString(),
                    authorUsername = user?.handle?.removePrefix("@") ?: "you",
                    authorDisplayName = user?.displayName ?: "You",
                    authorAvatarUrl = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    authorFollowed = true,
                    isVerified = true,
                    caption = captionText,
                    musicTrackTitle = sound?.title ?: "original sound - ${user?.displayName ?: "You"}",
                    musicArtist = sound?.artist ?: (user?.displayName ?: "You"),
                    soundCoverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150",
                    videoCoverDrawableId = null,
                    videoGradientColors = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
                    likesCount = 1,
                    isLiked = true,
                    commentsCount = 0,
                    savesCount = 0,
                    isSaved = false,
                    sharesCount = 0,
                    feedCategory = "For You",
                    overlayText = _overlayText.value,
                    filterEffect = _selectedFilter.value
                )
                repository.publishNewPost(newPost)
            }

            discardRecording()
            selectTab(MainTab.HOME)
        }
    }

    // Chat & Messaging
    fun openConversation(conv: Conversation) {
        _activeConversation.value = conv
    }

    fun closeConversation() {
        _activeConversation.value = null
    }

    fun sendMessage(text: String, messageType: String = "TEXT") {
        val conv = _activeConversation.value ?: return
        if (text.isBlank() && messageType == "TEXT") return
        viewModelScope.launch {
            val user = userProfile.value
            repository.sendMessage(
                conversationId = conv.id,
                text = text.trim(),
                senderName = user?.displayName ?: "You",
                senderHandle = user?.handle ?: "@freioh_official",
                messageType = messageType
            )
        }
    }

    fun openCreateGroupModal() {
        _isCreatingGroup.value = true
    }

    fun closeCreateGroupModal() {
        _isCreatingGroup.value = false
    }

    fun createGroupChat(title: String, selectedHandles: List<String>) {
        if (title.isBlank() || selectedHandles.isEmpty()) return
        viewModelScope.launch {
            val newConv = repository.createGroupConversation(title.trim(), selectedHandles)
            _isCreatingGroup.value = false
            _activeConversation.value = newConv
        }
    }

    // Video Calling
    fun startVideoCall(conversation: Conversation) {
        _videoCallState.value = VideoCallState(
            isInCall = true,
            callTitle = conversation.title,
            callerAvatar = conversation.avatarUrl,
            isGroupCall = conversation.isGroup,
            participantNames = if (conversation.isGroup) listOf("You", "Maya R.", "Chef Marco", "Antish") else listOf("You", conversation.title),
            isMuted = false,
            isCameraOff = false,
            isFrontCamera = true,
            isSpeakerOn = true,
            callDurationSeconds = 0,
            isEncrypted = true
        )
        startCallTimer()
    }

    fun endVideoCall() {
        callTimerJob?.cancel()
        _videoCallState.value = VideoCallState(isInCall = false)
    }

    fun toggleCallMute() {
        _videoCallState.value = _videoCallState.value.copy(isMuted = !_videoCallState.value.isMuted)
    }

    fun toggleCallCamera() {
        _videoCallState.value = _videoCallState.value.copy(isCameraOff = !_videoCallState.value.isCameraOff)
    }

    fun flipCallCamera() {
        _videoCallState.value = _videoCallState.value.copy(isFrontCamera = !_videoCallState.value.isFrontCamera)
    }

    fun toggleCallSpeaker() {
        _videoCallState.value = _videoCallState.value.copy(isSpeakerOn = !_videoCallState.value.isSpeakerOn)
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_videoCallState.value.isInCall) {
                delay(1000)
                _videoCallState.value = _videoCallState.value.copy(
                    callDurationSeconds = _videoCallState.value.callDurationSeconds + 1
                )
            }
        }
    }

    // Store & Platforms Dialog
    fun openStoreDialog() {
        _isStoreDialogOpen.value = true
    }

    fun closeStoreDialog() {
        _isStoreDialogOpen.value = false
    }

    // Settings
    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    // Auth
    fun openAuthModal() {
        _isAuthModalOpen.value = true
    }

    fun closeAuthModal() {
        _isAuthModalOpen.value = false
    }

    fun loginWith(provider: String, username: String = "Freioh Creator") {
        _currentAuthProvider.value = provider
        _isLoggedIn.value = true
        _isAuthModalOpen.value = false
        viewModelScope.launch {
            val user = userProfile.value?.copy(
                authProvider = provider,
                displayName = if (provider == "Facebook") "Freioh VIP Creator" else username
            )
            if (user != null) {
                repository.updateUserProfile(user)
            }
        }
    }

    // Profile Modals & Inspection
    fun openEditProfile() {
        _isEditProfileOpen.value = true
    }

    fun closeEditProfile() {
        _isEditProfileOpen.value = false
    }

    fun saveProfile(displayName: String, bio: String, website: String) {
        viewModelScope.launch {
            val user = userProfile.value?.copy(
                displayName = displayName.trim(),
                bio = bio.trim(),
                website = website.trim()
            )
            if (user != null) {
                repository.updateUserProfile(user)
            }
            _isEditProfileOpen.value = false
        }
    }

    fun viewOtherUserProfile(post: VideoPost) {
        _viewingUserProfile.value = UserProfile(
            handle = "@${post.authorUsername.replace(" ", "_")}",
            displayName = post.authorDisplayName,
            bio = "Official Creator on Freioh 🎬✨ Follow for daily viral shorts!",
            avatarUrl = post.authorAvatarUrl,
            isVerified = post.isVerified,
            followingCount = 89,
            followersCount = (post.likesCount / 4).toInt(),
            likesTotalCount = post.likesCount * 3,
            isFollowedByMe = post.authorFollowed,
            website = "linktr.ee/${post.authorUsername.replace(" ", "")}"
        )
    }

    fun closeOtherUserProfile() {
        _viewingUserProfile.value = null
    }

    fun toggleFollowViewedUser() {
        val user = _viewingUserProfile.value ?: return
        _viewingUserProfile.value = user.copy(
            isFollowedByMe = !user.isFollowedByMe,
            followersCount = if (user.isFollowedByMe) user.followersCount - 1 else user.followersCount + 1
        )
    }
}
