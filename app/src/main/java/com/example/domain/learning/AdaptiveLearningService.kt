package com.example.domain.learning

import com.example.data.models.LearnerMemoryEntity
import com.example.data.models.MasteryTier
import com.example.data.models.SkillMasteryEntity
import org.json.JSONArray

class AdaptiveLearningService {

  /**
   * Computes normalized mastery percentage (0-100) taking into account accuracy,
   * total attempts, and hint penalties.
   */
  fun calculateMasteryScore(
    totalAttempted: Int,
    totalPassed: Int,
    hintsUsedCount: Int = 0
  ): Int {
    if (totalAttempted <= 0) return 0
    val rawAccuracy = (totalPassed.toDouble() / totalAttempted.toDouble()) * 100.0
    // Small hint penalty: each hint reduces score by 3% down to min 0
    val penalty = (hintsUsedCount * 3).coerceAtMost(30)
    val score = (rawAccuracy - penalty).toInt().coerceIn(0, 100)
    return score
  }

  fun getMasteryLevel(percentage: Int): MasteryTier {
    return MasteryTier.fromPercentage(percentage)
  }

  /**
   * Identifies weak concepts that need reinforcement.
   */
  fun detectWeakSkills(skills: List<SkillMasteryEntity>): List<SkillMasteryEntity> {
    return skills.filter {
      val level = MasteryTier.fromPercentage(it.masteryPercentage)
      level == MasteryTier.NEEDS_PRACTICE || level == MasteryTier.DEVELOPING || (it.totalAttempted > 0 && it.masteryPercentage < 65)
    }
  }

  /**
   * Identifies mastered concepts.
   */
  fun detectMasteredSkills(skills: List<SkillMasteryEntity>): List<SkillMasteryEntity> {
    return skills.filter {
      val level = MasteryTier.fromPercentage(it.masteryPercentage)
      level == MasteryTier.STRONG || level == MasteryTier.MASTERED
    }
  }

  /**
   * Computes recommended challenge difficulty based on overall skill mastery and success rate.
   */
  fun computeRecommendedDifficulty(skills: List<SkillMasteryEntity>): String {
    if (skills.isEmpty()) return "EASY"
    val avgMastery = skills.map { it.masteryPercentage }.average()
    return when {
      avgMastery >= 80 -> "HARD"
      avgMastery >= 50 -> "MEDIUM"
      else -> "EASY"
    }
  }

  /**
   * Updates LearnerMemoryEntity with fresh insights from recent skill performance.
   */
  fun updateLearnerMemory(
    currentMemory: LearnerMemoryEntity?,
    skills: List<SkillMasteryEntity>,
    additionalHintsRequested: Int = 0,
    additionalReviewsRequested: Int = 0
  ): LearnerMemoryEntity {
    val existing = currentMemory ?: LearnerMemoryEntity(
      id = "default_user_memory",
      userId = "default_user",
      conceptKey = "GLOBAL",
      conceptTitle = "Overall Profile"
    )
    val weakSkills = detectWeakSkills(skills).map { it.skillName }
    val masteredSkills = detectMasteredSkills(skills).map { it.skillName }
    val recommendedDiff = computeRecommendedDifficulty(skills)

    val weakSkillsJson = JSONArray(weakSkills).toString()
    val masteredSkillsJson = JSONArray(masteredSkills).toString()

    return existing.copy(
      totalHintsRequested = existing.totalHintsRequested + additionalHintsRequested,
      totalCodeReviewsRequested = existing.totalCodeReviewsRequested + additionalReviewsRequested,
      totalAIAssistanceSessions = existing.totalAIAssistanceSessions + (if (additionalHintsRequested > 0 || additionalReviewsRequested > 0) 1 else 0),
      weakSkillsJson = weakSkillsJson,
      masteredSkillsJson = masteredSkillsJson,
      recommendedDifficulty = recommendedDiff,
      lastSessionEpochMs = System.currentTimeMillis()
    )
  }
}
