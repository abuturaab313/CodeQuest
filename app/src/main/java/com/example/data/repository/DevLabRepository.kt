package com.example.data.repository

import com.example.data.local.DevLabDao
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class DevLabRepository(
  private val devLabDao: DevLabDao
) {
  // Bug Hunts
  fun getAllBugHunts(): Flow<List<BugHuntEntity>> = devLabDao.getAllBugHunts()

  suspend fun getBugHuntById(id: String): BugHuntEntity? = devLabDao.getBugHuntById(id)

  suspend fun completeBugHunt(id: String) {
    devLabDao.markBugHuntCompleted(id)
    incrementStat { it.copy(bugsFixedCount = it.bugsFixedCount + 1) }
  }

  // Test First Challenges
  fun getAllTestFirstChallenges(): Flow<List<TestFirstChallengeEntity>> = devLabDao.getAllTestFirstChallenges()

  suspend fun getTestFirstChallengeById(id: String): TestFirstChallengeEntity? = devLabDao.getTestFirstChallengeById(id)

  suspend fun completeTestFirst(id: String) {
    devLabDao.markTestFirstCompleted(id)
    incrementStat { it.copy(testsPassedCount = it.testsPassedCount + 4) }
  }

  // Git Exercises
  fun getAllGitExercises(): Flow<List<GitExerciseEntity>> = devLabDao.getAllGitExercises()

  suspend fun getGitExerciseById(id: String): GitExerciseEntity? = devLabDao.getGitExerciseById(id)

  suspend fun completeGitExercise(id: String, isCommit: Boolean = false, isBranch: Boolean = false, isConflict: Boolean = false) {
    devLabDao.markGitExerciseCompleted(id)
    incrementStat {
      it.copy(
        gitExercisesCompleted = it.gitExercisesCompleted + 1,
        commitsCreatedCount = if (isCommit) it.commitsCreatedCount + 1 else it.commitsCreatedCount,
        branchesCreatedCount = if (isBranch) it.branchesCreatedCount + 1 else it.branchesCreatedCount,
        conflictsResolvedCount = if (isConflict) it.conflictsResolvedCount + 1 else it.conflictsResolvedCount
      )
    }
  }

  // Code Reviews
  fun getAllCodeReviews(): Flow<List<CodeReviewEntity>> = devLabDao.getAllCodeReviews()

  suspend fun getCodeReviewById(id: String): CodeReviewEntity? = devLabDao.getCodeReviewById(id)

  suspend fun completeCodeReview(id: String, isRefactor: Boolean) {
    devLabDao.markCodeReviewCompleted(id)
    incrementStat {
      if (isRefactor) {
        it.copy(refactorsCompleted = it.refactorsCompleted + 1)
      } else {
        it.copy(codeReviewsCompleted = it.codeReviewsCompleted + 1)
      }
    }
  }

  // Project Issues
  fun getIssuesForProject(projectId: String): Flow<List<ProjectIssueEntity>> = devLabDao.getIssuesForProject(projectId)

  suspend fun updateIssueStatus(issueId: String, status: IssueStatus) {
    devLabDao.updateIssueStatus(issueId, status)
  }

  // Portfolio
  fun getAllPortfolioItems(): Flow<List<PortfolioItemEntity>> = devLabDao.getAllPortfolioItems()

  suspend fun savePortfolioItem(item: PortfolioItemEntity) {
    devLabDao.insertPortfolioItem(item)
    incrementStat { it.copy(realWorldProjectsCompleted = it.realWorldProjectsCompleted + 1) }
  }

  suspend fun updatePortfolioVisibility(id: String, isPublic: Boolean) {
    devLabDao.updatePortfolioVisibility(id, isPublic)
  }

  suspend fun updatePortfolioReadme(id: String, readme: String) {
    devLabDao.updatePortfolioReadme(id, readme)
  }

  // Project Versions & Recovery
  fun getVersionsForProject(projectId: String): Flow<List<ProjectVersionEntity>> = devLabDao.getVersionsForProject(projectId)

  suspend fun saveVersion(projectId: String, versionNumber: Int, description: String, files: Map<String, String>) {
    val json = JSONObject(files).toString()
    devLabDao.insertProjectVersion(
      ProjectVersionEntity(
        projectId = projectId,
        versionNumber = versionNumber,
        description = description,
        filesJson = json
      )
    )
  }

  suspend fun getUnsavedRecovery(projectId: String): UnsavedRecoveryEntity? = devLabDao.getUnsavedRecovery(projectId)

  suspend fun saveUnsavedRecovery(projectId: String, files: Map<String, String>) {
    val json = JSONObject(files).toString()
    devLabDao.saveUnsavedRecovery(UnsavedRecoveryEntity(projectId, json))
  }

  suspend fun clearUnsavedRecovery(projectId: String) {
    devLabDao.clearUnsavedRecovery(projectId)
  }

  // Developer Stats
  fun getDeveloperStats(): Flow<DeveloperStatsEntity?> = devLabDao.getDeveloperStats()

  private suspend fun incrementStat(transform: (DeveloperStatsEntity) -> DeveloperStatsEntity) {
    val current = devLabDao.getDeveloperStats()
    // default
    val base = DeveloperStatsEntity(userId = 1L)
    val updated = transform(base)
    devLabDao.insertOrUpdateDeveloperStats(updated)
  }
}
