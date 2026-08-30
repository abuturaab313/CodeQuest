package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DevLabDao {

  // Bug Hunt
  @Query("SELECT * FROM bug_hunts")
  fun getAllBugHunts(): Flow<List<BugHuntEntity>>

  @Query("SELECT * FROM bug_hunts WHERE id = :id")
  suspend fun getBugHuntById(id: String): BugHuntEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBugHunts(hunts: List<BugHuntEntity>)

  @Query("UPDATE bug_hunts SET isCompleted = 1 WHERE id = :id")
  suspend fun markBugHuntCompleted(id: String)

  // Test First Challenges
  @Query("SELECT * FROM test_first_challenges")
  fun getAllTestFirstChallenges(): Flow<List<TestFirstChallengeEntity>>

  @Query("SELECT * FROM test_first_challenges WHERE id = :id")
  suspend fun getTestFirstChallengeById(id: String): TestFirstChallengeEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTestFirstChallenges(challenges: List<TestFirstChallengeEntity>)

  @Query("UPDATE test_first_challenges SET isCompleted = 1 WHERE id = :id")
  suspend fun markTestFirstCompleted(id: String)

  // Git Exercises
  @Query("SELECT * FROM git_exercises ORDER BY lessonNumber ASC")
  fun getAllGitExercises(): Flow<List<GitExerciseEntity>>

  @Query("SELECT * FROM git_exercises WHERE id = :id")
  suspend fun getGitExerciseById(id: String): GitExerciseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGitExercises(exercises: List<GitExerciseEntity>)

  @Query("UPDATE git_exercises SET isCompleted = 1 WHERE id = :id")
  suspend fun markGitExerciseCompleted(id: String)

  // Code Reviews
  @Query("SELECT * FROM code_reviews")
  fun getAllCodeReviews(): Flow<List<CodeReviewEntity>>

  @Query("SELECT * FROM code_reviews WHERE id = :id")
  suspend fun getCodeReviewById(id: String): CodeReviewEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCodeReviews(reviews: List<CodeReviewEntity>)

  @Query("UPDATE code_reviews SET isCompleted = 1 WHERE id = :id")
  suspend fun markCodeReviewCompleted(id: String)

  // Project Issues
  @Query("SELECT * FROM project_issues WHERE projectId = :projectId ORDER BY issueNumber ASC")
  fun getIssuesForProject(projectId: String): Flow<List<ProjectIssueEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjectIssues(issues: List<ProjectIssueEntity>)

  @Query("UPDATE project_issues SET status = :status WHERE id = :issueId")
  suspend fun updateIssueStatus(issueId: String, status: IssueStatus)

  // Portfolio Items
  @Query("SELECT * FROM portfolio_items")
  fun getAllPortfolioItems(): Flow<List<PortfolioItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPortfolioItem(item: PortfolioItemEntity)

  @Query("UPDATE portfolio_items SET isPublic = :isPublic WHERE id = :id")
  suspend fun updatePortfolioVisibility(id: String, isPublic: Boolean)

  @Query("UPDATE portfolio_items SET readmeContent = :readme WHERE id = :id")
  suspend fun updatePortfolioReadme(id: String, readme: String)

  // Project Version History
  @Query("SELECT * FROM project_versions WHERE projectId = :projectId ORDER BY versionNumber DESC")
  fun getVersionsForProject(projectId: String): Flow<List<ProjectVersionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjectVersion(version: ProjectVersionEntity)

  // Unsaved Recovery
  @Query("SELECT * FROM unsaved_recovery WHERE projectId = :projectId")
  suspend fun getUnsavedRecovery(projectId: String): UnsavedRecoveryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUnsavedRecovery(recovery: UnsavedRecoveryEntity)

  @Query("DELETE FROM unsaved_recovery WHERE projectId = :projectId")
  suspend fun clearUnsavedRecovery(projectId: String)

  // Developer Stats
  @Query("SELECT * FROM developer_stats WHERE userId = :userId")
  fun getDeveloperStats(userId: Long = 1L): Flow<DeveloperStatsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateDeveloperStats(stats: DeveloperStatsEntity)
}
