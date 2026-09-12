package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "video_posts")
data class VideoPost(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val authorUsername: String,
    val authorDisplayName: String,
    val authorAvatarUrl: String,
    val authorFollowed: Boolean = false,
    val isVerified: Boolean = true,
    val caption: String,
    val musicTrackTitle: String,
    val musicArtist: String,
    val soundCoverUrl: String,
    val videoCoverDrawableId: Int? = null,
    val videoGradientColors: List<Long> = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
    val likesCount: Long = 18100,
    val isLiked: Boolean = false,
    val commentsCount: Long = 359,
    val savesCount: Long = 4524,
    val isSaved: Boolean = false,
    val sharesCount: Long = 2461,
    val feedCategory: String = "For You", // "For You", "Following", "Community", "LIVE"
    val timestamp: Long = System.currentTimeMillis(),
    val isStory: Boolean = false,
    val overlayText: String = "",
    val filterEffect: String = "Normal"
)

@Entity(tableName = "stories")
data class StoryItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val username: String,
    val userDisplayName: String,
    val avatarUrl: String,
    val mediaGradientColors: List<Long> = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val isUserStory: Boolean = false
)

@Entity(tableName = "comments")
data class CommentItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val username: String,
    val userDisplayName: String,
    val avatarUrl: String,
    val text: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isCreatorPinned: Boolean = false,
    val isVerified: Boolean = false,
    val timeAgo: String = "2h ago"
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val avatarUrl: String,
    val isGroup: Boolean = false,
    val participantHandles: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: String = "Just now",
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isVerified: Boolean = false,
    val isE2EEncrypted: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val senderName: String,
    val senderHandle: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val isEncrypted: Boolean = true,
    val messageType: String = "TEXT", // "TEXT", "VIDEO_SNIP", "VOICE", "IMAGE"
    val mediaUrl: String? = null
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val handle: String = "@freioh_official",
    val displayName: String = "Freioh Creator",
    val bio: String = "🌟 Welcome to Freioh! Discover viral short videos, connect in encrypted chats & create your stories. 🚀✨",
    val avatarUrl: String = "",
    val isVerified: Boolean = true,
    val followingCount: Int = 248,
    val followersCount: Int = 184200,
    val likesTotalCount: Long = 952000,
    val isFollowedByMe: Boolean = false,
    val website: String = "freioh.app",
    val email: String = "creator@freioh.app",
    val authProvider: String = "Google" // "Google", "Facebook", "Email", "Guest"
)

data class ContactPerson(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val isOnline: Boolean = true,
    val mutualFriends: Int = 5
)

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val usageCount: String,
    val isTrending: Boolean = true
)
