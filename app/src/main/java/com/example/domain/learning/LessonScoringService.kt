package com.example.domain.learning

data class LessonScoringResult(
  val scorePercentage: Int,
  val stars: Int,
  val isPerfectRun: Boolean,
  val baseXp: Int,
  val bonusXp: Int,
  val totalXp: Int,
  val coinsEarned: Int,
  val accuracyPercentage: Int,
  val performanceSummary: String
)

class LessonScoringService {

  /**
   * Calculates comprehensive score, stars, perfect run detection, and rewards for a completed lesson.
   */
  fun calculateLessonScore(
    totalExercises: Int,
    correctCount: Int,
    mistakeCount: Int,
    hintsUsedCount: Int,
    baseXp: Int,
    baseCoins: Int,
    currentStreak: Int = 1
  ): LessonScoringResult {
    val totalAttempts = maxOf(totalExercises, correctCount + mistakeCount)
    val accuracy = if (totalAttempts > 0) {
      ((correctCount.toDouble() / totalAttempts.toDouble()) * 100).toInt().coerceIn(0, 100)
    } else {
      100
    }

    // Score deduction for mistakes (-12% per mistake) and hints (-4% per hint)
    val penalty = (mistakeCount * 12) + (hintsUsedCount * 4)
    val scorePercentage = (100 - penalty).coerceIn(40, 100)

    val isPerfectRun = mistakeCount == 0 && hintsUsedCount == 0

    val stars = when {
      isPerfectRun || scorePercentage >= 95 -> 3
      scorePercentage >= 75 -> 2
      else -> 1
    }

    // Bonuses
    var bonusXp = 0
    if (isPerfectRun) {
      bonusXp += 25 // Perfect lesson bonus!
    }
    if (currentStreak >= 3) {
      bonusXp += 5 // Streak bonus
    }

    val totalXp = baseXp + bonusXp
    val totalCoins = baseCoins + (if (isPerfectRun) 10 else 0)

    val summary = when {
      isPerfectRun -> "Flawless Execution! 🌟 Zero mistakes and zero hints."
      stars == 3 -> "Outstanding Mastery! ⚡ High accuracy and great speed."
      stars == 2 -> "Great Progress! 👍 You conquered the core concepts."
      else -> "Lesson Cleared! 🚀 Keep practicing to turn mistakes into mastery."
    }

    return LessonScoringResult(
      scorePercentage = scorePercentage,
      stars = stars,
      isPerfectRun = isPerfectRun,
      baseXp = baseXp,
      bonusXp = bonusXp,
      totalXp = totalXp,
      coinsEarned = totalCoins,
      accuracyPercentage = accuracy,
      performanceSummary = summary
    )
  }
}
