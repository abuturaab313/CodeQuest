package com.example.domain.ai.models

import com.example.data.models.LearnerLevel

enum class AIMentorMode(val title: String, val iconName: String, val shortDescription: String) {
  HINT("Hint", "Lightbulb", "Get a progressive nudge without spoiling the solution"),
  DEBUG("Debug", "BugReport", "Analyze runtime errors and diagnose root causes"),
  EXPLAIN("Explain", "MenuBook", "Understand the underlying syntax and concept"),
  REVIEW("Code Review", "FindInPage", "Inspect style, readability, and best practices"),
  CONCEPT("Concept Coach", "School", "Deep dive with examples and mini-check questions"),
  QUIZ("Quiz Me", "Psychology", "Quick 3-question knowledge check on weak topics")
}

data class LearningContext(
  val userId: String = "user_default",
  val learnerLevel: LearnerLevel = LearnerLevel.BEGINNER,
  val sourceScreen: String = "CODE_LAB", // LESSON, CODE_LAB, PROJECT, PRACTICE, HOME
  
  // Lesson Context
  val courseTitle: String? = "Python Fundamentals",
  val worldTitle: String? = null,
  val chapterTitle: String? = null,
  val lessonTitle: String? = null,
  val exercisePrompt: String? = null,
  val exerciseType: String? = null,
  
  // Challenge Context
  val challengeTitle: String? = null,
  val challengeDescription: String? = null,
  val starterCode: String? = null,
  
  // Project Context
  val projectTitle: String? = null,
  val currentTaskId: String? = null,
  val currentTaskDescription: String? = null,
  val activeFileName: String? = null,
  val workspaceFileNames: List<String> = emptyList(),
  val relevantFileSnippets: Map<String, String> = emptyMap(),
  
  // Code & Execution Context
  val currentCode: String = "",
  val recentError: String? = null,
  val stderr: String? = null,
  val stdout: String? = null,
  val testSummary: String? = null, // e.g. "2 of 3 tests passed. Test 2 failed: expected 15, got 10"
  
  // Adaptive Learning Signals
  val activeConcept: String = "GENERAL_PYTHON",
  val weakConcepts: List<String> = emptyList(),
  val skillMasteryPercentage: Int = 50,
  val hintLevelRequested: Int = 1, // 1 to 5
  val previousHintsUsedCount: Int = 0
) {
  fun getPrimaryContextLabel(): String {
    return when {
      !projectTitle.isNullOrBlank() -> "Project: $projectTitle ${activeFileName?.let { "($it)" } ?: ""}"
      !challengeTitle.isNullOrBlank() -> "Challenge: $challengeTitle"
      !lessonTitle.isNullOrBlank() -> "Lesson: $lessonTitle"
      else -> "Concept: $activeConcept"
    }
  }
}
