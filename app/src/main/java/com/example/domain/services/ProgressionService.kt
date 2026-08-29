package com.example.domain.services

import com.example.data.models.DailyQuestEntity
import com.example.data.models.LessonEntity
import com.example.data.models.UserEntity

data class PlayerProgress(
  val userId: Int,
  val username: String,
  val avatarId: String,
  val experienceLevel: String,
  val selectedLanguage: String,
  val level: Int,
  val totalXp: Int,
  val currentLevelXp: Int,
  val xpNeededForNextLevel: Int,
  val levelProgressPercent: Float,
  val hearts: Int,
  val maxHearts: Int,
  val minutesUntilHeartRegen: Int,
  val codeCoins: Int,
  val currentStreak: Int,
  val longestStreak: Int,
  val isStreakActiveToday: Boolean,
  val weeklyActivity: List<DayActivity>,
  val completedLessonsCount: Int,
  val totalLessonsCount: Int,
  val currentWorldTitle: String,
  val currentLesson: LessonEntity?,
  val dailyQuests: List<DailyQuestEntity>,
  val completedQuestsCount: Int
)

sealed class GamificationReward {
  data class XpReward(val amount: Int, val reason: String = "Lesson Complete") : GamificationReward()
  data class CoinReward(val amount: Int, val reason: String = "Quest Claimed") : GamificationReward()
  data class StreakReward(val streakDays: Int) : GamificationReward()
  data class LevelUpReward(val newLevel: Int, val coinsEarned: Int, val unlockedFeature: String?) : GamificationReward()
  data class AchievementReward(val title: String, val xpEarned: Int) : GamificationReward()
}

class ProgressionService(
  val xpService: XPService = XPService(),
  val heartService: HeartService = HeartService(),
  val streakService: StreakService = StreakService(),
  val questService: QuestService = QuestService(),
  val currencyService: CurrencyService = CurrencyService()
) {

  fun buildPlayerProgress(
    user: UserEntity,
    lessons: List<LessonEntity>,
    quests: List<DailyQuestEntity>,
    worldTitle: String = "World 1 — Code Origin"
  ): PlayerProgress {
    val levelProg = xpService.calculateProgress(user.xp)
    val heartRegen = heartService.calculateRegeneration(
      currentHearts = user.currentHearts,
      lastRegenEpochMs = user.lastHeartRegenEpochMs
    )
    val todayEpoch = streakService.getTodayEpochDay()
    val weekly = streakService.getWeeklyActivity(
      lastActiveEpochDay = user.lastActiveEpochDay,
      currentStreak = user.streakDays
    )
    val completedLessons = lessons.count { it.isCompleted }
    val currentLesson = lessons.firstOrNull { it.isUnlocked && !it.isCompleted } ?: lessons.firstOrNull()
    val completedQuests = quests.count { it.isCompleted }

    return PlayerProgress(
      userId = user.id,
      username = user.username,
      avatarId = user.avatarId,
      experienceLevel = user.experienceLevel,
      selectedLanguage = user.selectedLanguage,
      level = levelProg.level,
      totalXp = user.xp,
      currentLevelXp = levelProg.currentXpInLevel,
      xpNeededForNextLevel = levelProg.xpNeededForNextLevel,
      levelProgressPercent = levelProg.progressPercent,
      hearts = heartRegen.currentHearts,
      maxHearts = user.maxHearts,
      minutesUntilHeartRegen = heartRegen.minutesUntilNextRegen,
      codeCoins = user.coins,
      currentStreak = user.streakDays,
      longestStreak = user.longestStreak,
      isStreakActiveToday = user.lastActiveEpochDay == todayEpoch,
      weeklyActivity = weekly,
      completedLessonsCount = completedLessons,
      totalLessonsCount = lessons.size,
      currentWorldTitle = worldTitle,
      currentLesson = currentLesson,
      dailyQuests = quests,
      completedQuestsCount = completedQuests
    )
  }
}
