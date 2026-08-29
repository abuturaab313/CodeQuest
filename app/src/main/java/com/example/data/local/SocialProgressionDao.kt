package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.EventEntity
import com.example.data.models.FriendEntity
import com.example.data.models.UnlockedCosmeticEntity
import com.example.data.models.LeaderboardCompetitorEntity
import com.example.data.models.DailyRewardClaimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialProgressionDao {

  // Weekly Events
  @Query("SELECT * FROM weekly_events ORDER BY id ASC")
  fun getAllEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM weekly_events WHERE id = :id")
  suspend fun getEventById(id: String): EventEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<EventEntity>)

  @Update
  suspend fun updateEvent(event: EventEntity)

  // Friends & Social
  @Query("SELECT * FROM friends ORDER BY xp DESC")
  fun getAllFriends(): Flow<List<FriendEntity>>

  @Query("SELECT * FROM friends WHERE isFollowed = 1")
  suspend fun getFollowedFriendsOnce(): List<FriendEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFriends(friends: List<FriendEntity>)

  @Query("UPDATE friends SET isFollowed = :isFollowed WHERE id = :friendId")
  suspend fun updateFriendFollowStatus(friendId: String, isFollowed: Boolean)

  @Query("UPDATE friends SET lastXpBoostEpochMs = :lastBoostTime WHERE id = :friendId")
  suspend fun updateFriendBoostTime(friendId: String, lastBoostTime: Long)

  // Cosmetic Shop
  @Query("SELECT * FROM unlocked_cosmetics")
  fun getAllUnlockedCosmetics(): Flow<List<UnlockedCosmeticEntity>>

  @Query("SELECT * FROM unlocked_cosmetics WHERE id = :id")
  suspend fun getUnlockedCosmeticById(id: String): UnlockedCosmeticEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUnlockedCosmetic(cosmetic: UnlockedCosmeticEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUnlockedCosmetics(cosmetics: List<UnlockedCosmeticEntity>)

  @Query("UPDATE unlocked_cosmetics SET isEquipped = 0 WHERE category = :category")
  suspend fun unequipAllInCategory(category: String)

  @Query("UPDATE unlocked_cosmetics SET isEquipped = 1 WHERE id = :id")
  suspend fun equipCosmetic(id: String)

  // Leaderboard / Leagues
  @Query("SELECT * FROM leaderboard_competitors WHERE leagueName = :leagueName ORDER BY xp DESC")
  fun getCompetitorsForLeague(leagueName: String): Flow<List<LeaderboardCompetitorEntity>>

  @Query("SELECT * FROM leaderboard_competitors WHERE leagueName = :leagueName ORDER BY xp DESC")
  suspend fun getCompetitorsForLeagueOnce(leagueName: String): List<LeaderboardCompetitorEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompetitors(competitors: List<LeaderboardCompetitorEntity>)

  @Query("UPDATE leaderboard_competitors SET xp = xp + :additionalXp WHERE id = :id")
  suspend fun updateCompetitorXp(id: String, additionalXp: Int)

  @Query("DELETE FROM leaderboard_competitors WHERE leagueName = :leagueName")
  suspend fun deleteCompetitorsForLeague(leagueName: String)

  // Daily Reward Calendar
  @Query("SELECT * FROM daily_reward_claims ORDER BY dayIndex ASC")
  fun getDailyRewardClaims(): Flow<List<DailyRewardClaimEntity>>

  @Query("SELECT * FROM daily_reward_claims ORDER BY dayIndex ASC")
  suspend fun getDailyRewardClaimsOnce(): List<DailyRewardClaimEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDailyRewardClaims(claims: List<DailyRewardClaimEntity>)

  @Query("UPDATE daily_reward_claims SET isClaimed = 1, claimedEpochDay = :epochDay WHERE dayIndex = :dayIndex")
  suspend fun claimReward(dayIndex: Int, epochDay: Long)

  @Query("UPDATE daily_reward_claims SET isClaimed = 0, claimedEpochDay = 0")
  suspend fun resetAllDailyRewards()
}
