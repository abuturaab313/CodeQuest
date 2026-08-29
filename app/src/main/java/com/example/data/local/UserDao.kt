package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM user_profile WHERE id = 1")
  fun getUserProfile(): Flow<UserEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 1")
  suspend fun getUserProfileOnce(): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE user_profile SET xp = xp + :gainedXp, coins = coins + :gainedCoins, level = :newLevel WHERE id = 1")
  suspend fun addRewardsAndLevel(gainedXp: Int, gainedCoins: Int, newLevel: Int)

  @Query("UPDATE user_profile SET xp = xp + :gainedXp, coins = coins + :gainedCoins WHERE id = 1")
  suspend fun addRewards(gainedXp: Int, gainedCoins: Int)

  @Query("UPDATE user_profile SET currentHearts = :hearts, lastHeartRegenEpochMs = :lastRegenMs WHERE id = 1")
  suspend fun updateHeartsWithTimestamp(hearts: Int, lastRegenMs: Long)

  @Query("UPDATE user_profile SET currentHearts = :hearts WHERE id = 1")
  suspend fun updateHearts(hearts: Int)

  @Query("UPDATE user_profile SET streakDays = :streak, longestStreak = :longest, lastActiveEpochDay = :epochDay WHERE id = 1")
  suspend fun updateStreak(streak: Int, longest: Int, epochDay: Long)

  @Query("UPDATE user_profile SET hasCompletedOnboarding = 1, experienceLevel = :experience, selectedLanguage = :language, dailyGoalMinutes = :goal WHERE id = 1")
  suspend fun completeOnboarding(experience: String, language: String, goal: Int)

  @Query("UPDATE user_profile SET isGuest = 0, email = :email, username = :username WHERE id = 1")
  suspend fun upgradeAccount(email: String, username: String)

  @Query("UPDATE user_profile SET soundEnabled = :sound, hapticsEnabled = :haptics, darkMode = :dark, reducedMotion = :reducedMotion WHERE id = 1")
  suspend fun updateSettings(sound: Boolean, haptics: Boolean, dark: Boolean, reducedMotion: Boolean)
}
