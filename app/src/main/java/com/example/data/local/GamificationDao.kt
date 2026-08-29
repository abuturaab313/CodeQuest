package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.AchievementEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.SkillMasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
  // Daily Quests
  @Query("SELECT * FROM daily_quests ORDER BY id ASC")
  fun getAllDailyQuests(): Flow<List<DailyQuestEntity>>

  @Query("SELECT * FROM daily_quests WHERE epochDay = :epochDay ORDER BY id ASC")
  suspend fun getDailyQuestsForDayOnce(epochDay: Long): List<DailyQuestEntity>

  @Query("UPDATE daily_quests SET currentValue = min(targetValue, currentValue + :increment), isCompleted = CASE WHEN (currentValue + :increment) >= targetValue THEN 1 ELSE isCompleted END WHERE questType = :questType AND isCompleted = 0")
  suspend fun incrementQuestProgress(questType: String, increment: Int)

  @Query("UPDATE daily_quests SET isClaimed = 1 WHERE id = :questId")
  suspend fun claimQuestReward(questId: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDailyQuests(quests: List<DailyQuestEntity>)

  @Query("DELETE FROM daily_quests WHERE epochDay < :epochDay")
  suspend fun deleteOldQuests(epochDay: Long)

  // Achievements
  @Query("SELECT * FROM achievements ORDER BY id ASC")
  fun getAllAchievements(): Flow<List<AchievementEntity>>

  @Query("UPDATE achievements SET currentCount = min(targetCount, currentCount + :increment), isUnlocked = CASE WHEN (currentCount + :increment) >= targetCount THEN 1 ELSE isUnlocked END, unlockedAtEpochMs = CASE WHEN (currentCount + :increment) >= targetCount AND isUnlocked = 0 THEN :currentTime ELSE unlockedAtEpochMs END WHERE category = :category AND isUnlocked = 0")
  suspend fun updateAchievementProgress(category: String, increment: Int, currentTime: Long = System.currentTimeMillis())

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAchievements(achievements: List<AchievementEntity>)

  // Skills
  @Query("SELECT * FROM skill_mastery WHERE language = :language")
  fun getSkillsForLanguage(language: String): Flow<List<SkillMasteryEntity>>

  @Query("SELECT * FROM skill_mastery WHERE language = :language")
  suspend fun getSkillsForLanguageOnce(language: String): List<SkillMasteryEntity>

  @Query("UPDATE skill_mastery SET totalAttempted = totalAttempted + 1, totalPassed = totalPassed + :passed, masteryPercentage = min(100, ((totalPassed + :passed) * 100) / (totalAttempted + 1)) WHERE id = :skillId")
  suspend fun recordSkillAttempt(skillId: String, passed: Int)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSkills(skills: List<SkillMasteryEntity>)

  // Projects
  @Query("SELECT * FROM projects WHERE language = :language")
  fun getProjectsForLanguage(language: String): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE language = :language")
  suspend fun getProjectsForLanguageOnce(language: String): List<ProjectEntity>

  @Query("SELECT * FROM projects WHERE id = :projectId")
  suspend fun getProjectById(projectId: String): ProjectEntity?

  @Query("UPDATE projects SET isCompleted = 1 WHERE id = :projectId")
  suspend fun markProjectCompleted(projectId: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjects(projects: List<ProjectEntity>)
}
