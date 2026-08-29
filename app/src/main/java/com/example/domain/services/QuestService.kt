package com.example.domain.services

import com.example.data.models.DailyQuestEntity
import com.example.data.models.QuestType
import java.time.LocalDate

data class QuestClaimResult(
  val updatedQuest: DailyQuestEntity,
  val xpGained: Int,
  val coinsGained: Int
)

class QuestService {

  fun getTodayEpochDay(): Long {
    return LocalDate.now().toEpochDay()
  }

  /**
   * Returns fresh daily quests tailored for today.
   */
  fun generateDailyQuests(epochDay: Long = getTodayEpochDay()): List<DailyQuestEntity> {
    return listOf(
      DailyQuestEntity(
        id = "quest_lessons_${epochDay}",
        title = "Code Scholar",
        description = "Complete 2 lessons or challenges",
        questType = QuestType.LESSONS_COMPLETED,
        targetValue = 2,
        currentValue = 0,
        isCompleted = false,
        isClaimed = false,
        xpReward = 60,
        coinReward = 20,
        epochDay = epochDay
      ),
      DailyQuestEntity(
        id = "quest_xp_${epochDay}",
        title = "XP Surge",
        description = "Earn 80 XP from learning milestones",
        questType = QuestType.XP_EARNED,
        targetValue = 80,
        currentValue = 0,
        isCompleted = false,
        isClaimed = false,
        xpReward = 75,
        coinReward = 25,
        epochDay = epochDay
      ),
      DailyQuestEntity(
        id = "quest_challenges_${epochDay}",
        title = "Bug Hunter",
        description = "Solve 2 coding challenges or tests",
        questType = QuestType.CHALLENGES_SOLVED,
        targetValue = 2,
        currentValue = 0,
        isCompleted = false,
        isClaimed = false,
        xpReward = 90,
        coinReward = 30,
        epochDay = epochDay
      )
    )
  }

  /**
   * Updates existing quests progress and marks them completed if target is met.
   */
  fun incrementProgress(
    quests: List<DailyQuestEntity>,
    type: QuestType,
    increment: Int
  ): List<DailyQuestEntity> {
    return quests.map { quest ->
      if (quest.questType == type && !quest.isCompleted) {
        val newVal = (quest.currentValue + increment).coerceAtMost(quest.targetValue)
        quest.copy(
          currentValue = newVal,
          isCompleted = newVal >= quest.targetValue
        )
      } else {
        quest
      }
    }
  }

  /**
   * Claims a completed quest.
   */
  fun claimQuest(quest: DailyQuestEntity): QuestClaimResult? {
    if (!quest.isCompleted || quest.isClaimed) return null
    return QuestClaimResult(
      updatedQuest = quest.copy(isClaimed = true),
      xpGained = quest.xpReward,
      coinsGained = quest.coinReward
    )
  }
}
