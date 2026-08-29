package com.example

import com.example.data.models.DailyQuestEntity
import com.example.data.models.QuestType
import com.example.data.models.UserEntity
import com.example.domain.services.CurrencyService
import com.example.domain.services.HeartService
import com.example.domain.services.ProgressionService
import com.example.domain.services.QuestService
import com.example.domain.services.StreakService
import com.example.domain.services.XPService
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationServicesTest {

  private val xpService = XPService()
  private val heartService = HeartService()
  private val streakService = StreakService()
  private val questService = QuestService()
  private val currencyService = CurrencyService()
  private val progressionService = ProgressionService(
    xpService, heartService, streakService, questService, currencyService
  )

  // 1. XP Progression Formula Tests
  @Test
  fun testXpLevelCalculation() {
    assertEquals(1, xpService.calculateLevel(0))
    assertEquals(1, xpService.calculateLevel(50))
    assertEquals(1, xpService.calculateLevel(99))
    assertEquals(2, xpService.calculateLevel(100))
    assertEquals(2, xpService.calculateLevel(249))
    assertEquals(3, xpService.calculateLevel(250))
    assertEquals(4, xpService.calculateLevel(450))
    assertEquals(5, xpService.calculateLevel(700))
  }

  @Test
  fun testLevelUpDetection() {
    val resultNoLevelUp = xpService.checkLevelUp(20, 50)
    assertFalse(resultNoLevelUp.didLevelUp)
    assertEquals(1, resultNoLevelUp.oldLevel)
    assertEquals(1, resultNoLevelUp.newLevel)

    val resultLevelUp = xpService.checkLevelUp(90, 110)
    assertTrue(resultLevelUp.didLevelUp)
    assertEquals(1, resultLevelUp.oldLevel)
    assertEquals(2, resultLevelUp.newLevel)
    assertEquals(50, resultLevelUp.coinReward)
    assertNotNull(resultLevelUp.unlockedFeature)
  }

  @Test
  fun testLevelProgressBreakdown() {
    val progress = xpService.calculateProgress(150)
    assertEquals(2, progress.level)
    assertEquals(50, progress.currentXpInLevel) // 150 - 100 base threshold
    assertEquals(150, progress.xpNeededForNextLevel) // 250 - 100
    assertEquals(50f / 150f, progress.progressPercent, 0.001f)
  }

  // 2. Heart Management Tests
  @Test
  fun testHeartConsumption() {
    val (success, remaining) = heartService.consumeHeart(5)
    assertTrue(success)
    assertEquals(4, remaining)

    val (failSuccess, failRemaining) = heartService.consumeHeart(0)
    assertFalse(failSuccess)
    assertEquals(0, failRemaining)
  }

  @Test
  fun testHeartRegenerationOverTime() {
    val now = System.currentTimeMillis()
    val thirtyMinutesAgo = now - (30 * 60 * 1000L) // 2 hearts elapsed (15 min interval)

    val result = heartService.calculateRegeneration(
      currentHearts = 2,
      lastRegenEpochMs = thirtyMinutesAgo,
      currentTimeMs = now
    )

    assertTrue(result.didRegenerate)
    assertEquals(4, result.currentHearts)
  }

  // 3. Streak Progression Tests
  @Test
  fun testStreakConsecutiveDays() {
    val today = LocalDate.of(2026, 8, 28).toEpochDay()
    val yesterday = today - 1

    val result = streakService.recordActivity(
      currentStreak = 4,
      longestStreak = 10,
      lastActiveEpochDay = yesterday,
      todayEpochDay = today
    )

    assertTrue(result.isExtendedToday)
    assertEquals(5, result.newStreak)
    assertEquals(10, result.newLongestStreak)
  }

  @Test
  fun testStreakBrokenAfterMissingDay() {
    val today = LocalDate.of(2026, 8, 28).toEpochDay()
    val threeDaysAgo = today - 3

    val result = streakService.recordActivity(
      currentStreak = 14,
      longestStreak = 14,
      lastActiveEpochDay = threeDaysAgo,
      todayEpochDay = today
    )

    assertTrue(result.isExtendedToday)
    assertEquals(1, result.newStreak) // resets to 1
    assertEquals(14, result.newLongestStreak) // preserves longest
  }

  @Test
  fun testStreakSameDayNoDoubleIncrement() {
    val today = LocalDate.of(2026, 8, 28).toEpochDay()

    val result = streakService.recordActivity(
      currentStreak = 7,
      longestStreak = 7,
      lastActiveEpochDay = today,
      todayEpochDay = today
    )

    assertFalse(result.isExtendedToday)
    assertEquals(7, result.newStreak)
  }

  // 4. Daily Quests Tests
  @Test
  fun testQuestProgressAndClaim() {
    val today = LocalDate.of(2026, 8, 28).toEpochDay()
    val quests = questService.generateDailyQuests(today)
    assertEquals(3, quests.size)

    val updatedQuests = questService.incrementProgress(
      quests = quests,
      type = QuestType.LESSONS_COMPLETED,
      increment = 2
    )

    val lessonQuest = updatedQuests.first { it.questType == QuestType.LESSONS_COMPLETED }
    assertTrue(lessonQuest.isCompleted)
    assertFalse(lessonQuest.isClaimed)

    val claimResult = questService.claimQuest(lessonQuest)
    assertNotNull(claimResult)
    assertTrue(claimResult!!.updatedQuest.isClaimed)
    assertEquals(lessonQuest.xpReward, claimResult.xpGained)
    assertEquals(lessonQuest.coinReward, claimResult.coinsGained)
  }

  // 5. Currency Tests
  @Test
  fun testCurrencyTransactions() {
    val balance = 100
    val withBonus = currencyService.addCoins(balance, 50)
    assertEquals(150, withBonus)

    assertTrue(currencyService.canAfford(withBonus, 75))
    assertFalse(currencyService.canAfford(withBonus, 200))

    val (success, newBalance) = currencyService.spendCoins(withBonus, 50)
    assertTrue(success)
    assertEquals(100, newBalance)
  }
}
