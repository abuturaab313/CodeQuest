package com.example.domain.services

data class LevelProgress(
  val level: Int,
  val currentXpInLevel: Int,
  val xpNeededForNextLevel: Int,
  val progressPercent: Float,
  val totalXp: Int
)

data class LevelUpResult(
  val didLevelUp: Boolean,
  val oldLevel: Int,
  val newLevel: Int,
  val coinReward: Int,
  val unlockedFeature: String? = null
)

class XPService {

  /**
   * Calculates the level given total accumulated XP using a progressive curve.
   * Level 1: 0 XP
   * Level 2: 100 XP
   * Level 3: 250 XP (+150)
   * Level 4: 450 XP (+200)
   * Level 5: 700 XP (+250)
   * Level 6: 1000 XP (+300)
   */
  fun calculateLevel(totalXp: Int): Int {
    if (totalXp <= 0) return 1
    var level = 1
    while (getXpThresholdForLevel(level + 1) <= totalXp) {
      level++
      if (level >= 100) break // safety ceiling
    }
    return level
  }

  /**
   * Total cumulative XP required to reach the given level.
   */
  fun getXpThresholdForLevel(level: Int): Int {
    if (level <= 1) return 0
    return 25 * (level - 1) * (level + 2)
  }

  /**
   * XP needed to advance from the given level to the next.
   */
  fun getXpDeltaForLevel(level: Int): Int {
    return 50 * level + 50
  }

  /**
   * Calculates breakdown of current progress inside the current level.
   */
  fun calculateProgress(totalXp: Int): LevelProgress {
    val level = calculateLevel(totalXp)
    val baseThreshold = getXpThresholdForLevel(level)
    val nextThreshold = getXpThresholdForLevel(level + 1)
    val currentInLevel = (totalXp - baseThreshold).coerceAtLeast(0)
    val neededInLevel = (nextThreshold - baseThreshold).coerceAtLeast(1)
    val percent = (currentInLevel.toFloat() / neededInLevel).coerceIn(0f, 1f)

    return LevelProgress(
      level = level,
      currentXpInLevel = currentInLevel,
      xpNeededForNextLevel = neededInLevel,
      progressPercent = percent,
      totalXp = totalXp
    )
  }

  /**
   * Detects if an XP gain triggered a level-up.
   */
  fun checkLevelUp(oldTotalXp: Int, newTotalXp: Int): LevelUpResult {
    val oldLevel = calculateLevel(oldTotalXp)
    val newLevel = calculateLevel(newTotalXp)

    return if (newLevel > oldLevel) {
      val levelDiff = newLevel - oldLevel
      LevelUpResult(
        didLevelUp = true,
        oldLevel = oldLevel,
        newLevel = newLevel,
        coinReward = levelDiff * 50,
        unlockedFeature = when (newLevel) {
          2 -> "Daily Quests & Challenges"
          3 -> "World 2: Logic Lab"
          4 -> "Speed Coding Mode"
          5 -> "Project Studio"
          else -> "Mastery Badge Level $newLevel"
        }
      )
    } else {
      LevelUpResult(
        didLevelUp = false,
        oldLevel = oldLevel,
        newLevel = newLevel,
        coinReward = 0
      )
    }
  }
}
