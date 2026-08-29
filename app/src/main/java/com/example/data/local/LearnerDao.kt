package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.AIFeedbackEntity
import com.example.data.models.DailyPracticeSessionEntity
import com.example.data.models.LearnerMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnerDao {

  @Query("SELECT * FROM learner_memories WHERE id = :id LIMIT 1")
  fun getLearnerMemory(id: String = "default_user_memory"): Flow<LearnerMemoryEntity?>

  @Query("SELECT * FROM learner_memories WHERE id = :id LIMIT 1")
  suspend fun getLearnerMemoryOnce(id: String = "default_user_memory"): LearnerMemoryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveLearnerMemory(memory: LearnerMemoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun logFeedback(feedback: AIFeedbackEntity)

  @Query("SELECT * FROM daily_practice_sessions WHERE epochDay = :epochDay LIMIT 1")
  fun getDailyPracticeSession(epochDay: Long): Flow<DailyPracticeSessionEntity?>

  @Query("SELECT * FROM daily_practice_sessions WHERE epochDay = :epochDay LIMIT 1")
  suspend fun getDailyPracticeSessionOnce(epochDay: Long): DailyPracticeSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveDailyPracticeSession(session: DailyPracticeSessionEntity)

  @Query("UPDATE daily_practice_sessions SET completedSteps = :completedSteps, isCompleted = :isCompleted WHERE epochDay = :epochDay")
  suspend fun updateDailyPracticeProgress(epochDay: Long, completedSteps: Int, isCompleted: Boolean)
}
