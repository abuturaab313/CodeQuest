package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.AIFeedbackEntity
import com.example.data.models.LearnerMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnerMemoryDao {

  @Query("SELECT * FROM learner_memories WHERE userId = :userId ORDER BY masteryScore ASC")
  fun observeAllMemories(userId: String): Flow<List<LearnerMemoryEntity>>

  @Query("SELECT * FROM learner_memories WHERE userId = :userId ORDER BY masteryScore ASC")
  suspend fun getAllMemories(userId: String): List<LearnerMemoryEntity>

  @Query("SELECT * FROM learner_memories WHERE userId = :userId AND conceptKey = :conceptKey LIMIT 1")
  suspend fun getMemory(userId: String, conceptKey: String): LearnerMemoryEntity?

  @Query("SELECT * FROM learner_memories WHERE userId = :userId AND masteryScore < 60 ORDER BY masteryScore ASC")
  suspend fun getWeakSkills(userId: String): List<LearnerMemoryEntity>

  @Query("SELECT * FROM learner_memories WHERE userId = :userId AND masteryScore >= 90")
  suspend fun getMasteredSkills(userId: String): List<LearnerMemoryEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertMemory(memory: LearnerMemoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertMemories(memories: List<LearnerMemoryEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAIFeedback(feedback: AIFeedbackEntity): Long

  @Query("UPDATE ai_feedback SET wasHelpful = :wasHelpful WHERE id = :id")
  suspend fun updateFeedbackHelpfulness(id: Long, wasHelpful: Boolean)

  @Query("UPDATE ai_feedback SET problemSolvedAfter = 1 WHERE contextKey = :contextKey")
  suspend fun markContextSolved(contextKey: String)

  @Query("SELECT * FROM ai_feedback ORDER BY timestamp DESC LIMIT 20")
  suspend fun getRecentAIFeedback(): List<AIFeedbackEntity>
}
