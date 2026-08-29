package com.example.domain.learning

import com.example.data.models.SkillMasteryEntity

class SkillTrackingService {

  /**
   * Updates skill mastery after an exercise attempt.
   */
  fun calculateUpdatedMastery(
    currentSkill: SkillMasteryEntity,
    wasCorrect: Boolean
  ): SkillMasteryEntity {
    val newTotal = currentSkill.totalAttempted + 1
    val newPassed = currentSkill.totalPassed + if (wasCorrect) 1 else 0
    val newPercentage = if (newTotal > 0) {
      ((newPassed.toDouble() / newTotal.toDouble()) * 100).toInt().coerceIn(0, 100)
    } else {
      0
    }

    return currentSkill.copy(
      totalAttempted = newTotal,
      totalPassed = newPassed,
      masteryPercentage = newPercentage
    )
  }

  /**
   * Filters out skills that need practice (mastery < 70% or total attempted < 3).
   */
  fun identifyWeakSkills(skills: List<SkillMasteryEntity>): List<SkillMasteryEntity> {
    return skills.filter { it.masteryPercentage < 70 || (it.totalAttempted > 0 && it.masteryPercentage < 80) }
  }
}
