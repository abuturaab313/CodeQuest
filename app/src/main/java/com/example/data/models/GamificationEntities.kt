package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class QuestType {
  LESSONS_COMPLETED,
  XP_EARNED,
  CHALLENGES_SOLVED,
  STREAK_MAINTAINED,
  PERFECT_LESSON
}

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val questType: QuestType,
  val targetValue: Int,
  val currentValue: Int = 0,
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false,
  val xpReward: Int = 50,
  val coinReward: Int = 20,
  val epochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
)

@Entity(tableName = "achievements")
data class AchievementEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val category: String, // STREAK, MASTERY, SPEED, PROJECT, XP
  val targetCount: Int,
  val currentCount: Int = 0,
  val isUnlocked: Boolean = false,
  val unlockedAtEpochMs: Long = 0,
  val xpReward: Int = 100,
  val coinReward: Int = 20,
  val isHidden: Boolean = false
)

@Entity(tableName = "skill_mastery")
data class SkillMasteryEntity(
  @PrimaryKey val id: String, // e.g. "py_variables"
  val language: String,
  val skillName: String,
  val masteryPercentage: Int = 0, // 0 to 100
  val totalAttempted: Int = 0,
  val totalPassed: Int = 0
)
