package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
  @PrimaryKey val id: Int = 1,
  val username: String = "Code Adventurer",
  val email: String = "",
  val isGuest: Boolean = true,
  val avatarId: String = "robot_explorer",
  val experienceLevel: String = "BEGINNER", // COMPLETE_BEGINNER, BEGINNER, INTERMEDIATE, ADVANCED
  val selectedLanguage: String = "python", // python, javascript, html_css, java, c, cpp, sql
  val xp: Int = 0,
  val level: Int = 1,
  val currentHearts: Int = 5,
  val maxHearts: Int = 5,
  val lastHeartRegenEpochMs: Long = System.currentTimeMillis(),
  val streakDays: Int = 1,
  val longestStreak: Int = 1,
  val lastActiveEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
  val coins: Int = 50,
  val gems: Int = 10,
  val soundEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true,
  val darkMode: Boolean = true,
  val reducedMotion: Boolean = false,
  val hasCompletedOnboarding: Boolean = false,
  val dailyGoalMinutes: Int = 15
)
