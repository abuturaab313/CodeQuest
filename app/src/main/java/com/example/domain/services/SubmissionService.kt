package com.example.domain.services

import com.example.data.local.ChallengeDao
import com.example.data.models.ChallengeProgressEntity
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.SubmissionRecordEntity
import com.example.data.models.UserEntity
import com.example.domain.execution.CodeExecutionService
import com.example.domain.execution.ExecutionErrorType
import com.example.domain.execution.TestSuiteResult

data class SubmissionResult(
  val isPassed: Boolean,
  val xpAwarded: Int,
  val bonusXpAwarded: Int,
  val coinsAwarded: Int,
  val isPerfect: Boolean,
  val testSuiteResult: TestSuiteResult,
  val failureReason: String? = null
)

class SubmissionService(
  private val executionService: CodeExecutionService,
  private val challengeDao: ChallengeDao,
  private val xpService: XPService = XPService(),
  private val currencyService: CurrencyService = CurrencyService(),
  private val questService: QuestService = QuestService(),
  private val streakService: StreakService = StreakService()
) {

  suspend fun submitCode(
    challenge: CodingChallengeEntity,
    code: String,
    hintsUsedCount: Int,
    currentUser: UserEntity?,
    languageId: String? = null
  ): Pair<SubmissionResult, UserEntity?> {
    val publicTests = challenge.parsePublicTests()
    val hiddenTests = challenge.parseHiddenTests()

    val targetLanguage = languageId ?: challenge.languageId

    val testSuiteResult = executionService.runFullValidation(
      code = code,
      languageId = targetLanguage,
      publicTests = publicTests,
      hiddenTests = hiddenTests
    )

    val isPassed = testSuiteResult.allPassed
    val isPerfect = isPassed && hintsUsedCount == 0

    var xpAwarded = 0
    var bonusXpAwarded = 0
    var coinsAwarded = 0
    var updatedUser = currentUser

    val verdict = if (isPassed) {
      "PASSED"
    } else {
      val firstFailed = testSuiteResult.results.firstOrNull { !it.passed }
      when (firstFailed?.errorType) {
        ExecutionErrorType.SYNTAX_ERROR -> "SYNTAX_ERROR"
        ExecutionErrorType.TIME_LIMIT_EXCEEDED -> "TIME_LIMIT_EXCEEDED"
        ExecutionErrorType.OUTPUT_LIMIT_EXCEEDED -> "OUTPUT_LIMIT_EXCEEDED"
        ExecutionErrorType.SECURITY_VIOLATION -> "SECURITY_VIOLATION"
        ExecutionErrorType.RUNTIME_ERROR -> "RUNTIME_ERROR"
        else -> "WRONG_ANSWER"
      }
    }

    if (isPassed) {
      val baseXp = challenge.xpReward
      bonusXpAwarded = if (isPerfect) 10 else 0
      xpAwarded = baseXp + bonusXpAwarded
      coinsAwarded = challenge.coinReward

      // Apply user updates
      if (updatedUser != null) {
        val streakResult = streakService.recordActivity(
          currentStreak = updatedUser.streakDays,
          longestStreak = updatedUser.longestStreak,
          lastActiveEpochDay = updatedUser.lastActiveEpochDay
        )

        updatedUser = updatedUser.copy(
          xp = updatedUser.xp + xpAwarded,
          coins = updatedUser.coins + coinsAwarded,
          streakDays = streakResult.newStreak,
          longestStreak = streakResult.newLongestStreak,
          lastActiveEpochDay = streakResult.todayEpochDay
        )
      }

      // Mark challenge complete in DB
      challengeDao.markChallengeCompleted(challenge.id)
    }

    // Save Progress
    val existingProgress = challengeDao.getProgressForChallenge(challenge.id)
    val updatedProgress = ChallengeProgressEntity(
      challengeId = challenge.id,
      draftCode = code,
      lastSubmittedCode = code,
      isCompleted = isPassed || (existingProgress?.isCompleted == true),
      attemptsCount = (existingProgress?.attemptsCount ?: 0) + 1,
      hintsUsedCount = hintsUsedCount,
      bestExecutionTimeMs = testSuiteResult.totalExecutionTimeMs,
      lastUpdatedEpochMs = System.currentTimeMillis()
    )
    challengeDao.saveProgress(updatedProgress)

    // Save Submission Record
    val submissionId = "sub_${System.currentTimeMillis()}_${challenge.id.takeLast(6)}"
    val submission = SubmissionRecordEntity(
      id = submissionId,
      challengeId = challenge.id,
      timestampEpochMs = System.currentTimeMillis(),
      codeSnippet = code.take(500),
      verdict = verdict,
      passedTests = testSuiteResult.passedCount,
      totalTests = testSuiteResult.totalCount,
      executionTimeMs = testSuiteResult.totalExecutionTimeMs,
      xpEarned = xpAwarded
    )
    challengeDao.insertSubmission(submission)

    val result = SubmissionResult(
      isPassed = isPassed,
      xpAwarded = xpAwarded,
      bonusXpAwarded = bonusXpAwarded,
      coinsAwarded = coinsAwarded,
      isPerfect = isPerfect,
      testSuiteResult = testSuiteResult,
      failureReason = testSuiteResult.failureSummary
    )

    return result to updatedUser
  }
}
