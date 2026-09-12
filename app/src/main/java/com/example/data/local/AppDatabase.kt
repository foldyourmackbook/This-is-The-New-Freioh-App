package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toLongList(value: String?): List<Long> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").mapNotNull { it.trim().toLongOrNull() }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split("||").filter { it.isNotEmpty() }
    }
}

@Dao
interface PostDao {
    @Query("SELECT * FROM video_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE feedCategory = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE authorUsername = :username ORDER BY timestamp DESC")
    fun getPostsByAuthor(username: String): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE isLiked = 1 ORDER BY timestamp DESC")
    fun getLikedPosts(): Flow<List<VideoPost>>

    @Query("SELECT * FROM video_posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedPosts(): Flow<List<VideoPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: VideoPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<VideoPost>)

    @Update
    suspend fun updatePost(post: VideoPost)

    @Delete
    suspend fun deletePost(post: VideoPost)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY isCreatorPinned DESC, likesCount DESC")
    fun getCommentsForPost(postId: String): Flow<List<CommentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentItem>)

    @Update
    suspend fun updateComment(comment: CommentItem)

    // Stories
    @Query("SELECT * FROM stories ORDER BY isUserStory DESC, timestamp DESC")
    fun getAllStories(): Flow<List<StoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryItem>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY unreadCount DESC, id ASC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    suspend fun getConversationById(conversationId: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    // Messages
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE handle = :handle LIMIT 1")
    fun getProfile(handle: String): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)
}

@Database(
    entities = [
        VideoPost::class,
        StoryItem::class,
        CommentItem::class,
        Conversation::class,
        ChatMessage::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun chatDao(): ChatDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freioh_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
