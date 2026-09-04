package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- Chat Models and Serialization ---

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object ChatSerializationHelper {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val chatListType = Types.newParameterizedType(List::class.java, ChatMessage::class.java)
    private val adapter = moshi.adapter<List<ChatMessage>>(chatListType)

    fun toJson(messages: List<ChatMessage>): String {
        return try {
            adapter.toJson(messages) ?: "[]"
        } catch (e: Exception) {
            "[]"
        }
    }

    fun fromJson(json: String): List<ChatMessage> {
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// --- Room Entities ---

@Entity(tableName = "victory_logs")
data class VictoryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "REFLECT", "TRIGGER", or "CBT"
    val notes: String = "",
    val triggerContext: String = "",
    val automaticThought: String = "",
    val identifiedDistortion: String = "",
    val reframedTruth: String = "",
    val scriptureReference: String = "",
    val userId: String = "", // Binds Log securely to an authenticated session
    val userPath: String = "", // Tracks which focus path the log belongs to (TOUGH_DAY, SUBSTANCE_RECOVERY, MENTAL_HEALTH, TESTIMONY_VICTORY)
    val authorName: String = "" // Anonymized or formatted name of the creator (e.g. John D.)
)

@Entity(tableName = "freedom_goals")
data class FreedomGoal(
    @PrimaryKey val id: Int = 1, // Single-entry configuration
    val startDate: Long = System.currentTimeMillis(),
    val struggleType: String = "Substance Use", // "Substance Use", "Anxiety", "Depression", "Fear", "Other"
    val customDeclaration: String = "I am a new creation in Christ; the old has gone, the new is here!",
    val userId: String = "" // Binds Goal counter securely to an authenticated session
)

@Entity(tableName = "saved_chats")
data class SavedChat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val messagesJson: String, // ChatMessage list serialized as JSON string
    val userPath: String,     // category path
    val userId: String,        // authenticated session user ID
    val isAutoSaved: Boolean = false
)

// --- DAOs ---

@Dao
interface VictoryLogDao {
    @Query("SELECT * FROM victory_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<VictoryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VictoryLog)

    @Query("DELETE FROM victory_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)
    
    @Delete
    suspend fun deleteLog(log: VictoryLog)
}

@Dao
interface FreedomGoalDao {
    @Query("SELECT * FROM freedom_goals WHERE id = 1")
    fun getFreedomGoalFlow(): Flow<FreedomGoal?>

    @Query("SELECT * FROM freedom_goals WHERE id = 1")
    suspend fun getFreedomGoal(): FreedomGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFreedomGoal(goal: FreedomGoal)
}

@Dao
interface SavedChatDao {
    @Query("SELECT * FROM saved_chats ORDER BY timestamp DESC")
    fun getAllSavedChats(): Flow<List<SavedChat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedChat(chat: SavedChat): Long

    @Query("DELETE FROM saved_chats WHERE id = :id")
    suspend fun deleteSavedChatById(id: Int)
}

// --- App Database ---

@Database(entities = [VictoryLog::class, FreedomGoal::class, SavedChat::class], version = 6, exportSchema = false)
abstract class OverComerDatabase : RoomDatabase() {
    abstract val victoryLogDao: VictoryLogDao
    abstract val freedomGoalDao: FreedomGoalDao
    abstract val savedChatDao: SavedChatDao

    companion object {
        @Volatile
        private var INSTANCE: OverComerDatabase? = null

        fun getDatabase(context: Context): OverComerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OverComerDatabase::class.java,
                    "overcomer_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository abstraction ---

class OverComerRepository(private val db: OverComerDatabase) {
    val victoryLogs: Flow<List<VictoryLog>> = db.victoryLogDao.getAllLogs()
    val freedomGoal: Flow<FreedomGoal?> = db.freedomGoalDao.getFreedomGoalFlow()
    val savedChats: Flow<List<SavedChat>> = db.savedChatDao.getAllSavedChats()

    suspend fun insertLog(log: VictoryLog) {
        db.victoryLogDao.insertLog(log)
    }

    suspend fun deleteLogById(id: Int) {
        db.victoryLogDao.deleteLogById(id)
    }

    suspend fun getFreedomGoal(): FreedomGoal? {
        return db.freedomGoalDao.getFreedomGoal()
    }

    suspend fun updateFreedomGoal(goal: FreedomGoal) {
        db.freedomGoalDao.insertFreedomGoal(goal)
    }

    suspend fun insertSavedChat(chat: SavedChat): Long {
        return db.savedChatDao.insertSavedChat(chat)
    }

    suspend fun deleteSavedChatById(id: Int) {
        db.savedChatDao.deleteSavedChatById(id)
    }
}
