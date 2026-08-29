package com.example.domain.ai

import com.example.data.models.ExerciseType

enum class LearnerLevel(val displayName: String) {
  BEGINNER("Beginner"),
  INTERMEDIATE("Intermediate"),
  ADVANCED("Advanced");

  companion object {
    fun fromString(value: String): LearnerLevel {
      return when (value.uppercase()) {
        "INTERMEDIATE" -> INTERMEDIATE
        "ADVANCED" -> ADVANCED
        else -> BEGINNER
      }
    }
  }
}

enum class MentorMode(val title: String, val emoji: String, val description: String) {
  HINT("Hint", "💡", "Get a progressive clue without spoiling the answer"),
  DEBUG("Debug", "🐛", "Analyze the error and get guided fixes"),
  EXPLAIN("Explain", "📚", "Understand concepts in simple terms"),
  CODE_REVIEW("Review", "🔍", "Get feedback on structure, style and bugs"),
  CONCEPT_COACH("Concept", "🎓", "Explore the concept with mini examples"),
  QUIZ_ME("Quiz Me", "🧠", "Test your understanding with practice questions"),
  SHOW_EXAMPLE("Example", "✨", "See a helpful small code pattern")
}

data class LearningContext(
  val learnerLevel: LearnerLevel = LearnerLevel.BEGINNER,
  val selectedLanguage: String = "Python",
  val currentCourse: String? = null,
  val currentWorldTitle: String? = null,
  val currentChapterTitle: String? = null,
  val currentLessonTitle: String? = null,
  val currentTaskPrompt: String? = null,
  val currentExerciseType: ExerciseType? = null,
  val currentChallengeTitle: String? = null,
  val currentProjectTitle: String? = null,
  val activeFileName: String? = null,
  val currentCode: String = "",
  val workspaceFiles: Map<String, String> = emptyMap(),
  val recentErrorMessage: String? = null,
  val testResultsSummary: String? = null,
  val passedTestsCount: Int = 0,
  val totalTestsCount: Int = 0,
  val failedConcepts: List<String> = emptyList(),
  val skillMasteryPercentage: Map<String, Int> = emptyMap(),
  val previousHintsUsed: Int = 0,
  val starterCode: String? = null,
  val solutionRequirements: String? = null
)

data class AIReviewFinding(
  val isMustFix: Boolean,
  val title: String,
  val detail: String,
  val lineHint: String? = null
)

data class GeneratedPracticeQuiz(
  val topic: String,
  val question: String,
  val codeSnippet: String? = null,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String
)

data class AIStructuredResponse(
  val mode: MentorMode,
  val headline: String,
  val whatsWrong: String? = null,
  val why: String? = null,
  val tryThis: String? = null,
  val thinkAbout: String? = null,
  val optionalNextStep: String? = null,
  val codeExample: String? = null,
  val reviewFindings: List<AIReviewFinding> = emptyList(),
  val practiceQuiz: GeneratedPracticeQuiz? = null,
  val rawText: String = "",
  val hintLevel: Int = 1,
  val isAiGenerated: Boolean = true,
  val isFallback: Boolean = false
)
