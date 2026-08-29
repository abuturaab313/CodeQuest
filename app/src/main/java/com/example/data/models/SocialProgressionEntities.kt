package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_events")
data class EventEntity(
  @PrimaryKey val id: String, // e.g. "evt_bug_hunter_1"
  val title: String,
  val description: String,
  val currentProgress: Int = 0,
  val targetProgress: Int = 15,
  val xpReward: Int = 500,
  val coinReward: Int = 100,
  val badgeReward: String = "Bug Hunter Badge",
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false,
  val endEpochMs: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
)

@Entity(tableName = "friends")
data class FriendEntity(
  @PrimaryKey val id: String,
  val username: String,
  val avatarId: String,
  val xp: Int,
  val streakDays: Int,
  val leagueName: String = "Bronze",
  val isFollowed: Boolean = false,
  val lastXpBoostEpochMs: Long = 0L
)

@Entity(tableName = "unlocked_cosmetics")
data class UnlockedCosmeticEntity(
  @PrimaryKey val id: String, // e.g. "avatar_ninja", "theme_cyberpunk", "title_bug_slayer", "streak_freeze"
  val category: String, // "AVATAR", "THEME", "TITLE", "STREAK_FREEZE"
  val quantity: Int = 1, // Useful for consumable streak freeze cards
  val isEquipped: Boolean = false
)

@Entity(tableName = "leaderboard_competitors")
data class LeaderboardCompetitorEntity(
  @PrimaryKey val id: String,
  val leagueName: String, // "Bronze", "Silver", "Gold", "Crystal", "Obsidian"
  val username: String,
  val avatarId: String,
  val xp: Int,
  val isPlayer: Boolean = false
)

@Entity(tableName = "daily_reward_claims")
data class DailyRewardClaimEntity(
  @PrimaryKey val dayIndex: Int, // 1 to 7
  val xpReward: Int,
  val coinReward: Int,
  val isClaimed: Boolean = false,
  val claimedEpochDay: Long = 0L
)
