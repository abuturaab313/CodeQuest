package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class LearnerLevel(val displayName: String, val threshold: Int) {
  BEGINNER("Beginner", 0),
  NOVICE("Novice", 10),
  INTERMEDIATE("Intermediate", 30),
  ADVANCED("Advanced", 100),
  EXPERT("Expert", 250);

  companion object {
    fun fromScore(masteryScore: Int, completedChallengesCount: Int): LearnerLevel {
      val score = completedChallengesCount * 2 + (masteryScore / 10)
      return entries.sortedByDescending { it.threshold }.firstOrNull { score >= it.threshold } ?: BEGINNER
    }
  }
}

enum class MasteryTier(val label: String, val minScore: Int, val maxScore: Int) {
  NEEDS_PRACTICE("Needs Practice", 0, 39),
  DEVELOPING("Developing", 40, 59),
  COMPETENT("Competent", 60, 79),
  STRONG("Strong", 80, 94),
  MASTERED("Mastered", 95, 100);

  companion object {
    fun fromPercentage(percentage: Int): MasteryTier {
      val clamped = percentage.coerceIn(0, 100)
      return entries.firstOrNull { clamped in it.minScore..it.maxScore } ?: DEVELOPING
    }
  }
}

@Entity(tableName = "learner_memories")
data class LearnerMemoryEntity(
  @PrimaryKey val id: String, // Usually combination of userId and conceptKey
  val userId: String,
  val conceptKey: String,
  val conceptTitle: String,
  val masteryScore: Int = 50,
  val totalAttempts: Int = 0,
  val successfulAttempts: Int = 0,
  val failedAttempts: Int = 0,
  val hintsUsedCount: Int = 0,
  val consecutiveCorrect: Int = 0,
  val recentMistakesJson: String = "[]", // JSON array of mistake pattern strings
  val lastAttemptTimestamp: Long = System.currentTimeMillis(),
  val learnerLevel: String = LearnerLevel.BEGINNER.name,
  
  // Global User Stats (used when id == "default_user_memory")
  val totalHintsRequested: Int = 0,
  val totalCodeReviewsRequested: Int = 0,
  val totalAIAssistanceSessions: Int = 0,
  val weakSkillsJson: String = "[]",
  val masteredSkillsJson: String = "[]",
  val recommendedDifficulty: String = "EASY",
  val lastSessionEpochMs: Long = System.currentTimeMillis()
) {
  fun isWeakSkill(): Boolean {
    return masteryScore < 60
  }
  fun parseRecentMistakes(): List<String> {
    return try {
      val arr = JSONArray(recentMistakesJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

@Entity(tableName = "ai_feedback")
data class AIFeedbackEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val promptMode: String, // HINT, DEBUG, EXPLAIN, CODE_REVIEW, etc.
  val contextTopic: String,
  val contextKey: String? = null,
  val wasHelpful: Boolean = false,
  val problemSolvedAfter: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_practice_sessions")
data class DailyPracticeSessionEntity(
  @PrimaryKey val epochDay: Long, // Unique per day
  val title: String = "Daily Personalized Practice",
  val description: String = "Tailored 4-step training based on your recent activity",
  val isCompleted: Boolean = false,
  val totalSteps: Int = 4,
  val completedSteps: Int = 0,
  val xpReward: Int = 150,
  val coinReward: Int = 40,
  val practiceItemsJson: String = "[]" // JSON array of practice items
)

data class DailyPracticePlan(
  val id: String,
  val title: String,
  val description: String,
  val xpReward: Int,
  val coinReward: Int = 0,
  val totalTasksCount: Int,
  val completedTasksCount: Int = 0,
  val isCompleted: Boolean = false
)

data class AIQuizQuestion(
  val id: String,
  val question: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String,
  val conceptKey: String = ""
)

enum class RecommendationType {
  NEXT_LESSON,
  PRACTICE_WEAK_SKILL,
  DEBUG_MISTAKE,
  START_PROJECT,
  TRY_CHALLENGE
}

data class LearningRecommendation(
  val id: String,
  val type: RecommendationType,
  val title: String,
  val subtitle: String,
  val reason: String,
  val targetId: String,
  val xpReward: Int,
  val estimatedTime: Int? = null,
  val difficulty: String = "Easy",
  val iconName: String = "target"
)
