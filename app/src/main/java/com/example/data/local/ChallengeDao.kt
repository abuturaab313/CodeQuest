package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.ChallengeProgressEntity
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.SubmissionRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

  @Query("SELECT * FROM coding_challenges ORDER BY orderIndex ASC")
  fun getAllChallenges(): Flow<List<CodingChallengeEntity>>

  @Query("SELECT * FROM coding_challenges ORDER BY orderIndex ASC")
  suspend fun getAllChallengesOnce(): List<CodingChallengeEntity>

  @Query("SELECT * FROM coding_challenges WHERE category = :category ORDER BY orderIndex ASC")
  fun getChallengesByCategory(category: String): Flow<List<CodingChallengeEntity>>

  @Query("SELECT * FROM coding_challenges WHERE id = :challengeId")
  suspend fun getChallengeById(challengeId: String): CodingChallengeEntity?

  @Query("SELECT * FROM coding_challenges WHERE lessonId = :lessonId LIMIT 1")
  suspend fun getChallengeByLessonId(lessonId: String): CodingChallengeEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChallenges(challenges: List<CodingChallengeEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChallenge(challenge: CodingChallengeEntity)

  @Query("UPDATE coding_challenges SET isCompleted = 1, isUnlocked = 1 WHERE id = :challengeId")
  suspend fun markChallengeCompleted(challengeId: String)

  @Query("UPDATE coding_challenges SET isUnlocked = 1 WHERE id = :challengeId")
  suspend fun unlockChallenge(challengeId: String)

  // Challenge Progress / Autosave
  @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId")
  suspend fun getProgressForChallenge(challengeId: String): ChallengeProgressEntity?

  @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId")
  fun observeProgressForChallenge(challengeId: String): Flow<ChallengeProgressEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProgress(progress: ChallengeProgressEntity)

  // Submissions History
  @Query("SELECT * FROM submission_records WHERE challengeId = :challengeId ORDER BY timestampEpochMs DESC LIMIT 20")
  fun getSubmissionsForChallenge(challengeId: String): Flow<List<SubmissionRecordEntity>>

  @Query("SELECT * FROM submission_records ORDER BY timestampEpochMs DESC LIMIT 50")
  fun getAllSubmissions(): Flow<List<SubmissionRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubmission(submission: SubmissionRecordEntity)
}
