package com.example.data.repository

import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class FreiohRepository(private val database: AppDatabase) {

    val allPosts: Flow<List<VideoPost>> = database.postDao().getAllPosts()
    val allStories: Flow<List<StoryItem>> = database.postDao().getAllStories()
    val allConversations: Flow<List<Conversation>> = database.chatDao().getAllConversations()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        // Seed Posts
        val initialPosts = listOf(
            VideoPost(
                id = "post_1",
                authorUsername = "SETTIGATION",
                authorDisplayName = "SETTIGATION",
                authorAvatarUrl = "settigation_logo",
                authorFollowed = false,
                isVerified = true,
                caption = "They look away when you type your password. 👀 ...more",
                musicTrackTitle = "settigation · Original sound",
                musicArtist = "SETTIGATION",
                soundCoverUrl = "settigation_logo",
                videoCoverDrawableId = null,
                videoGradientColors = listOf(0xFF0F0F14, 0xFF14141E, 0xFF0D0D12),
                likesCount = 7698,
                isLiked = false,
                commentsCount = 148,
                savesCount = 4059,
                isSaved = false,
                sharesCount = 495,
                feedCategory = "For You",
                overlayText = ""
            ),
            VideoPost(
                id = "post_2",
                authorUsername = "maya.moves",
                authorDisplayName = "Maya Rodriguez ⚡",
                authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                authorFollowed = true,
                isVerified = true,
                caption = "Night city freestyle session in Tokyo! Drop a 🔥 if you want the full choreography tutorial! #dance #freestyle #tokyonights #freioh",
                musicTrackTitle = "Cyber Funk 2026 (Remix)",
                musicArtist = "DJ Neon Pulse",
                soundCoverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=150",
                videoCoverDrawableId = R.drawable.video_feed_cover_2_1787941990845,
                videoGradientColors = listOf(0xFF8E2DE2, 0xFF4A00E0),
                likesCount = 429300,
                isLiked = true,
                commentsCount = 8940,
                savesCount = 61200,
                isSaved = true,
                sharesCount = 19400,
                feedCategory = "For You",
                overlayText = "Tokyo Night Energy ⚡✨"
            ),
            VideoPost(
                id = "post_3",
                authorUsername = "chef_marco",
                authorDisplayName = "Marco's Kitchen",
                authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                authorFollowed = false,
                isVerified = true,
                caption = "The secret to the crispiest smash burger crust you will ever taste 🍔🧀 recipe in pinned comment! #foodie #burger #chef #cooking",
                musicTrackTitle = "Sizzle & Beats (Original Mix)",
                musicArtist = "Gourmet Sounds",
                soundCoverUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=150",
                videoCoverDrawableId = R.drawable.video_feed_cover_3_1787942004795,
                videoGradientColors = listOf(0xFFD31027, 0xFFEA384D),
                likesCount = 88400,
                isLiked = false,
                commentsCount = 1420,
                savesCount = 28900,
                isSaved = false,
                sharesCount = 5300,
                feedCategory = "For You",
                overlayText = "Crispy Smash Burger Masterclass 🍔"
            )
        )
        database.postDao().insertPosts(initialPosts)

        // Seed initial comments for post_1
        val initialComments = listOf(
            CommentItem(
                postId = "post_1",
                username = "space_cadet",
                userDisplayName = "Space Cadet",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                text = "Bro held that wrench with pure conviction 😭 wrench is the ultimate tool",
                likesCount = 1420,
                isLiked = true,
                isCreatorPinned = true,
                isVerified = true,
                timeAgo = "1h ago"
            ),
            CommentItem(
                postId = "post_1",
                username = "meme_lord99",
                userDisplayName = "Meme Supreme",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                text = "The algorithm knows what we truly desire 💀 pure peak cinema right here",
                likesCount = 680,
                isLiked = false,
                isVerified = false,
                timeAgo = "3h ago"
            ),
            CommentItem(
                postId = "post_1",
                username = "pixel_artisan",
                userDisplayName = "Pixel Artisan",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                text = "Animations on Freioh are so smooth! 🔥 Love this short!",
                likesCount = 290,
                isLiked = false,
                isVerified = true,
                timeAgo = "5h ago"
            )
        )
        database.postDao().insertComments(initialComments)

        // Seed Stories
        val initialStories = listOf(
            StoryItem(
                id = "story_user",
                username = "You",
                userDisplayName = "Your Story",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                caption = "Creating new content on Freioh! 🚀",
                isViewed = false,
                isUserStory = true
            ),
            StoryItem(
                id = "story_1",
                username = "maya.moves",
                userDisplayName = "Maya Rodriguez",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                caption = "Behind the scenes rehearsal in studio A 💃🎵",
                isViewed = false
            ),
            StoryItem(
                id = "story_2",
                username = "chef_marco",
                userDisplayName = "Marco Chef",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                caption = "Fresh ingredients just arrived at the kitchen 🍅🌿",
                isViewed = false
            ),
            StoryItem(
                id = "story_3",
                username = "ant / ish",
                userDisplayName = "Antish Official",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                caption = "New animation dropping tonight at 8 PM! Stay tuned 👾",
                isViewed = true
            ),
            StoryItem(
                id = "story_4",
                username = "tech_pulse",
                userDisplayName = "Tech Pulse",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                caption = "Testing the latest smartphone camera rigs 📱🎥",
                isViewed = true
            )
        )
        database.postDao().insertStories(initialStories)

        // Seed Conversations with E2EE
        val initialConversations = listOf(
            Conversation(
                id = "conv_1",
                title = "Maya Rodriguez",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                isGroup = false,
                participantHandles = listOf("@maya.moves"),
                lastMessage = "Hey! Let's do a dance collab video this weekend? 🎶",
                lastMessageTime = "10:42 AM",
                unreadCount = 2,
                isOnline = true,
                isVerified = true,
                isE2EEncrypted = true
            ),
            Conversation(
                id = "conv_2",
                title = "🚀 Freioh Creators Squad",
                avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                isGroup = true,
                participantHandles = listOf("@maya.moves", "@chef_marco", "@antish", "@pixel_artisan"),
                lastMessage = "Marco: The new video editing filters look awesome!",
                lastMessageTime = "09:15 AM",
                unreadCount = 4,
                isOnline = true,
                isVerified = true,
                isE2EEncrypted = true
            ),
            Conversation(
                id = "conv_3",
                title = "Chef Marco",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                isGroup = false,
                participantHandles = listOf("@chef_marco"),
                lastMessage = "Sent you the secret burger sauce ingredient list 🍔",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = false,
                isVerified = true,
                isE2EEncrypted = true
            )
        )
        database.chatDao().insertConversations(initialConversations)

        // Seed initial messages for conv_1
        val initialMessagesConv1 = listOf(
            ChatMessage(
                conversationId = "conv_1",
                senderName = "Maya Rodriguez",
                senderHandle = "@maya.moves",
                text = "Hey! Saw your latest post on Freioh, it's gaining huge traction! 🌟",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                isFromMe = false,
                isEncrypted = true
            ),
            ChatMessage(
                conversationId = "conv_1",
                senderName = "You",
                senderHandle = "@freioh_official",
                text = "Thank you so much Maya! The community feedback has been incredible! 😊",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                isFromMe = true,
                isEncrypted = true
            ),
            ChatMessage(
                conversationId = "conv_1",
                senderName = "Maya Rodriguez",
                senderHandle = "@maya.moves",
                text = "Hey! Let's do a dance collab video this weekend? 🎶 Tap the video call icon whenever you're free to brainstorm!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                isFromMe = false,
                isEncrypted = true
            )
        )
        database.chatDao().insertMessages(initialMessagesConv1)

        // Seed User Profile
        val initialProfile = UserProfile(
            handle = "@freioh_official",
            displayName = "Freioh Creator",
            bio = "Official Freioh account 🌟 Short videos, stories, and end-to-end encrypted chats. Available free across Mobile & Desktop! 🚀✨",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            isVerified = true,
            followingCount = 142,
            followersCount = 289400,
            likesTotalCount = 1420000,
            website = "freioh.app",
            email = "creator@freioh.app",
            authProvider = "Google"
        )
        database.userDao().insertProfile(initialProfile)
    }

    suspend fun toggleLike(post: VideoPost) {
        val updated = post.copy(
            isLiked = !post.isLiked,
            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
        )
        database.postDao().updatePost(updated)
    }

    suspend fun toggleSave(post: VideoPost) {
        val updated = post.copy(
            isSaved = !post.isSaved,
            savesCount = if (post.isSaved) post.savesCount - 1 else post.savesCount + 1
        )
        database.postDao().updatePost(updated)
    }

    suspend fun toggleFollow(post: VideoPost) {
        val updated = post.copy(
            authorFollowed = !post.authorFollowed
        )
        database.postDao().updatePost(updated)
    }

    suspend fun addComment(postId: String, text: String, username: String, displayName: String, avatarUrl: String) {
        val comment = CommentItem(
            postId = postId,
            username = username,
            userDisplayName = displayName,
            avatarUrl = avatarUrl,
            text = text,
            likesCount = 0,
            isLiked = false,
            isCreatorPinned = false,
            isVerified = true,
            timeAgo = "Just now"
        )
        database.postDao().insertComment(comment)
    }

    suspend fun toggleCommentLike(comment: CommentItem) {
        val updated = comment.copy(
            isLiked = !comment.isLiked,
            likesCount = if (comment.isLiked) comment.likesCount - 1 else comment.likesCount + 1
        )
        database.postDao().updateComment(comment)
    }

    fun getCommentsForPost(postId: String): Flow<List<CommentItem>> {
        return database.postDao().getCommentsForPost(postId)
    }

    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> {
        return database.chatDao().getMessagesForConversation(convId)
    }

    suspend fun sendMessage(conversationId: String, text: String, senderName: String, senderHandle: String, messageType: String = "TEXT") {
        val msg = ChatMessage(
            conversationId = conversationId,
            senderName = senderName,
            senderHandle = senderHandle,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            isEncrypted = true,
            messageType = messageType
        )
        database.chatDao().insertMessage(msg)

        val conv = database.chatDao().getConversationById(conversationId)
        if (conv != null) {
            database.chatDao().updateConversation(
                conv.copy(
                    lastMessage = if (messageType == "VOICE") "🎤 Voice message" else text,
                    lastMessageTime = "Just now"
                )
            )
        }
    }

    suspend fun createGroupConversation(
        title: String,
        handles: List<String>,
        avatarUrl: String = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150"
    ): Conversation {
        val conv = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            avatarUrl = avatarUrl,
            isGroup = true,
            participantHandles = handles,
            lastMessage = "Group created with end-to-end encryption 🔒",
            lastMessageTime = "Just now",
            unreadCount = 0,
            isOnline = true,
            isVerified = true,
            isE2EEncrypted = true
        )
        database.chatDao().insertConversation(conv)
        return conv
    }

    suspend fun publishNewPost(post: VideoPost) {
        database.postDao().insertPost(post)
    }

    suspend fun publishNewStory(story: StoryItem) {
        database.postDao().insertStory(story)
    }

    fun getUserProfile(handle: String = "@freioh_official"): Flow<UserProfile?> {
        return database.userDao().getProfile(handle)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        database.userDao().updateProfile(profile)
    }
}
