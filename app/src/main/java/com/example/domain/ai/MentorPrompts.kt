package com.example.domain.ai

import com.example.data.models.LearnerLevel
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.LearningContext

object MentorPrompts {

  private const val SYSTEM_INSTRUCTION = """
You are "Code Coach", a personal programming mentor in the gamified CodeQuest Android learning app.
Your mission is to guide learners to solve their own problems instead of writing the code for them.
Never dump full working solutions unless explicitly asked in solution review mode.
Structure your advice constructively and concisely.
Always tailor explanations to the learner's skill level.
"""

  fun buildPrompt(mode: AIMentorMode, context: LearningContext): String {
    val levelGuidance = when (context.learnerLevel) {
      LearnerLevel.BEGINNER -> "Learner Level: BEGINNER. Use plain English, simple analogies, avoid heavy jargon, keep code examples minimal."
      LearnerLevel.NOVICE -> "Learner Level: NOVICE. Introduce basic concepts with clear examples, avoid heavy jargon."
      LearnerLevel.INTERMEDIATE -> "Learner Level: INTERMEDIATE. Introduce technical terminology, explain standard idioms, focus on edge cases."
      LearnerLevel.ADVANCED -> "Learner Level: ADVANCED. Discuss algorithmic complexity, architecture, pythonic style, and edge case trade-offs."
      LearnerLevel.EXPERT -> "Learner Level: EXPERT. Discuss deep architecture, design patterns, performance optimizations, and language quirks."
    }

    val contextSummary = StringBuilder()
    contextSummary.append("Target Context: ${context.getPrimaryContextLabel()}\n")
    if (!context.exercisePrompt.isNullOrBlank()) {
      contextSummary.append("Exercise Prompt: ${context.exercisePrompt}\n")
    }
    if (!context.challengeDescription.isNullOrBlank()) {
      contextSummary.append("Challenge Goal: ${context.challengeDescription}\n")
    }
    if (!context.currentTaskDescription.isNullOrBlank()) {
      contextSummary.append("Project Task: ${context.currentTaskDescription}\n")
    }
    if (context.relevantFileSnippets.isNotEmpty()) {
      contextSummary.append("Relevant Workspace Files:\n")
      context.relevantFileSnippets.forEach { (name, snippet) ->
        contextSummary.append("--- $name ---\n$snippet\n")
      }
    }
    if (context.currentCode.isNotBlank()) {
      contextSummary.append("Learner Code (${context.activeFileName ?: "solution.py"}):\n```python\n${context.currentCode}\n```\n")
    }
    if (!context.recentError.isNullOrBlank()) {
      contextSummary.append("Runtime Error/Diagnostic:\n${context.recentError}\n")
    }
    if (!context.testSummary.isNullOrBlank()) {
      contextSummary.append("Test Suite Summary:\n${context.testSummary}\n")
    }

    return when (mode) {
      AIMentorMode.HINT -> buildHintPrompt(context, levelGuidance, contextSummary.toString())
      AIMentorMode.DEBUG -> buildDebugPrompt(levelGuidance, contextSummary.toString())
      AIMentorMode.EXPLAIN -> buildExplainPrompt(levelGuidance, contextSummary.toString())
      AIMentorMode.REVIEW -> buildReviewPrompt(levelGuidance, contextSummary.toString())
      AIMentorMode.CONCEPT -> buildConceptPrompt(levelGuidance, contextSummary.toString())
      AIMentorMode.QUIZ -> buildQuizPrompt(levelGuidance, contextSummary.toString())
    }
  }

  private fun buildHintPrompt(context: LearningContext, levelGuidance: String, contextInfo: String): String {
    val hintLevel = context.hintLevelRequested.coerceIn(1, 5)
    val hintGoal = when (hintLevel) {
      1 -> "Level 1 Hint: High-level conceptual clue only. Do not touch specifics of code lines."
      2 -> "Level 2 Hint: Point toward the relevant variable, condition, or part of the problem where the discrepancy occurs."
      3 -> "Level 3 Hint: Explain the algorithmic approach or thought pattern in words."
      4 -> "Level 4 Hint: Provide brief pseudocode or outline of the logic steps."
      5 -> "Level 5 Hint: Provide detailed line-by-line explanation with a tiny syntax snippet."
      else -> "Level 1 Hint: Conceptual clue."
    }

    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Requested Hint Level: $hintLevel / 5 ($hintGoal)

Context:
$contextInfo

Format your response strictly as:
WHAT'S WRONG: (1-2 sentences on the root issue if applicable, or state the goal)
WHY: (1-2 sentences explaining why this occurs)
TRY THIS: ($hintGoal)
THINK ABOUT: (A guiding question to prompt self-reflection)
OPTIONAL NEXT STEP: (What to do next)
""".trimIndent()
  }

  private fun buildDebugPrompt(levelGuidance: String, contextInfo: String): String {
    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Goal: Analyze the learner's error without dumping the complete solution.

Context:
$contextInfo

Format your response strictly as:
WHAT'S WRONG: (Specific bug or error diagnosis)
WHY: (Why Python threw this error or test failed)
TRY THIS: (Specific adjustment or line check to fix the error)
THINK ABOUT: (Guiding question)
OPTIONAL NEXT STEP: (Next debugging action)
""".trimIndent()
  }

  private fun buildExplainPrompt(levelGuidance: String, contextInfo: String): String {
    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Goal: Explain the programming concepts involved in this exercise clearly and visually.

Context:
$contextInfo

Format your response strictly as:
WHAT'S WRONG: (Concept being used)
WHY: (How this concept works in Python under the hood)
TRY THIS: (A clean minimal example demonstrating the concept)
THINK ABOUT: (A quick mental check)
OPTIONAL NEXT STEP: (How to apply it here)
""".trimIndent()
  }

  private fun buildReviewPrompt(levelGuidance: String, contextInfo: String): String {
    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Goal: Perform a friendly, constructive Code Review.
Separate MUST FIX from OPTIONAL IMPROVEMENT.

Context:
$contextInfo

Format your response strictly as:
WHAT'S WRONG: (Summary of code quality and correctness)
WHY: (Review details regarding naming, readability, complexity, potential bugs)
TRY THIS: (MUST FIX items vs OPTIONAL IMPROVEMENT items)
THINK ABOUT: (Architecture or clean code principle)
OPTIONAL NEXT STEP: (Next challenge or refinement)
""".trimIndent()
  }

  private fun buildConceptPrompt(levelGuidance: String, contextInfo: String): String {
    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Goal: Be a Concept Coach. Explain the core concept, provide a 3-line example, ask a mini check question, and recommend practice.

Context:
$contextInfo

Format your response strictly as:
WHAT'S WRONG: (Topic introduction)
WHY: (Detailed yet simple breakdown of the mechanism)
TRY THIS: (Short code snippet example)
THINK ABOUT: (Mini question for the user to answer in their head)
OPTIONAL NEXT STEP: (Recommended practice problem)
""".trimIndent()
  }

  private fun buildQuizPrompt(levelGuidance: String, contextInfo: String): String {
    return """
$SYSTEM_INSTRUCTION

$levelGuidance

Goal: Generate a quick 3-question multiple choice quiz on the learner's current topic.
Return valid JSON format with questions, 4 options each, and index of correct option (0-3).

Context:
$contextInfo
""".trimIndent()
  }
}
