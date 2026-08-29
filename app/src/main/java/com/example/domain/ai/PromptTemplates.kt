package com.example.domain.ai

object PromptTemplates {

  fun buildSystemInstruction(level: LearnerLevel, language: String): String {
    val levelGuidance = when (level) {
      LearnerLevel.BEGINNER -> "The learner is a BEGINNER. Use friendly, simple language, relatable analogies, and clear short explanations without heavy jargon. Never dump full solutions immediately."
      LearnerLevel.INTERMEDIATE -> "The learner is INTERMEDIATE. You can use standard technical terms (parameters, indexing, time complexity, scope). Focus on guiding logic and edge cases."
      LearnerLevel.ADVANCED -> "The learner is ADVANCED. Discuss algorithmic efficiency, edge cases, idioms, and design tradeoffs cleanly."
    }

    return """
      You are "CODE COACH", an expert AI programming mentor built for CodeQuest ($language).
      $levelGuidance
      
      CORE MENTORSHIP RULES:
      1. Guide the learner to discover answers themselves.
      2. Do NOT dump the complete final working code solution unless in explicit review/solution mode.
      3. Always be encouraging, direct, concise, and structured.
      4. Never invent fake APIs or modify official course structures.
      5. Strictly format your response following the requested structured tags or JSON format.
    """.trimIndent()
  }

  fun buildPrompt(context: LearningContext, mode: MentorMode, hintLevel: Int = 1): String {
    val contextSummary = StringBuilder().apply {
      appendLine("LEARNER LEVEL: ${context.learnerLevel.displayName}")
      appendLine("LANGUAGE: ${context.selectedLanguage}")
      if (!context.currentLessonTitle.isNullOrBlank()) appendLine("LESSON: ${context.currentLessonTitle}")
      if (!context.currentChallengeTitle.isNullOrBlank()) appendLine("CHALLENGE: ${context.currentChallengeTitle}")
      if (!context.currentProjectTitle.isNullOrBlank()) appendLine("PROJECT: ${context.currentProjectTitle}")
      if (!context.activeFileName.isNullOrBlank()) appendLine("ACTIVE FILE: ${context.activeFileName}")
      if (!context.currentTaskPrompt.isNullOrBlank()) appendLine("TASK OBJECTIVE:\n${context.currentTaskPrompt}")
      if (!context.solutionRequirements.isNullOrBlank()) appendLine("REQUIREMENTS:\n${context.solutionRequirements}")
      if (context.currentCode.isNotBlank()) appendLine("CURRENT USER CODE:\n```${context.selectedLanguage.lowercase()}\n${context.currentCode}\n```")
      if (context.workspaceFiles.isNotEmpty()) {
        appendLine("OTHER WORKSPACE FILES:")
        context.workspaceFiles.forEach { (name, content) ->
          appendLine("File: $name\n```\n$content\n```")
        }
      }
      if (!context.recentErrorMessage.isNullOrBlank()) appendLine("RECENT ERROR / DIAGNOSTIC:\n${context.recentErrorMessage}")
      if (!context.testResultsSummary.isNullOrBlank()) appendLine("TEST RUNNER STATUS:\n${context.testResultsSummary} (${context.passedTestsCount}/${context.totalTestsCount} passed)")
      if (context.failedConcepts.isNotEmpty()) appendLine("STRUGGLING WITH CONCEPTS: ${context.failedConcepts.joinToString(", ")}")
    }.toString()

    return when (mode) {
      MentorMode.HINT -> """
        $contextSummary
        
        TASK: Provide a progressive Hint (Level $hintLevel of 5).
        Level 1: Conceptual clue / high-level intuition.
        Level 2: Point toward the specific part of the code or problem.
        Level 3: Explain the recommended approach or strategy.
        Level 4: Provide pseudocode or minimal syntax structure.
        Level 5: Detailed line-by-line explanation of the fix.
        
        STRUCTURE YOUR OUTPUT AS:
        WHAT'S WRONG: (1-2 sentences on what to look for)
        TRY THIS: (Actionable guidance for Level $hintLevel)
        THINK ABOUT: (Guiding question for self-reflection)
      """.trimIndent()

      MentorMode.DEBUG -> """
        $contextSummary
        
        TASK: Diagnose the user's code error or failing test case.
        Do NOT immediately rewrite the full program.
        
        STRUCTURE YOUR OUTPUT EXACTLY AS:
        WHAT'S WRONG: (What went wrong in their code or logic)
        WHY: (Why this happens in ${context.selectedLanguage})
        TRY THIS: (Where to look and specific next step to try)
        THINK ABOUT: (Guiding question on the underlying concept)
      """.trimIndent()

      MentorMode.EXPLAIN -> """
        $contextSummary
        
        TASK: Explain the core programming concept behind this task in clear, simple terms suitable for a ${context.learnerLevel.displayName}.
        
        STRUCTURE YOUR OUTPUT AS:
        CONCEPT: (Clear definition of the concept)
        HOW IT WORKS: (Brief explanation with short 2-3 line code example)
        TRY THIS: (How to apply it right now to this problem)
      """.trimIndent()

      MentorMode.CODE_REVIEW -> """
        $contextSummary
        
        TASK: Conduct a concise Code Review of the user's code.
        Analyze: Correctness, readability, style/naming, potential bugs, edge cases.
        
        STRUCTURE YOUR OUTPUT AS:
        OVERALL: (1 sentence summary)
        MUST FIX:
        - [Issue 1]: (Explanation + where in code)
        OPTIONAL IMPROVEMENT:
        - [Tip 1]: (Best practice or cleaner idiom)
        NEXT STEP: (Encouraging closing tip)
      """.trimIndent()

      MentorMode.CONCEPT_COACH -> """
        $contextSummary
        
        TASK: Act as Concept Coach for the primary concept involved in this task.
        Provide:
        1. Simple intuitive explanation
        2. Clean 2-line code example
        3. A mini mental check question
        4. Practice recommendation
      """.trimIndent()

      MentorMode.QUIZ_ME -> """
        $contextSummary
        
        TASK: Generate 1 interactive multiple-choice practice question testing the concept the user is currently working on or struggling with (${context.failedConcepts.firstOrNull() ?: "current task"}).
        
        Output valid JSON with the schema:
        {
          "topic": "Concept Name",
          "question": "Question text?",
          "codeSnippet": "optional short code or null",
          "options": ["Option A", "Option B", "Option C", "Option D"],
          "correctOptionIndex": 0,
          "explanation": "Why this answer is correct"
        }
      """.trimIndent()

      MentorMode.SHOW_EXAMPLE -> """
        $contextSummary
        
        TASK: Provide a small, distinct example pattern (not solving the user's exact problem directly, but demonstrating the mechanism).
        
        STRUCTURE YOUR OUTPUT AS:
        PATTERN: (Name of pattern)
        EXAMPLE: (3-4 lines of code)
        KEY TAKEAWAY: (1 sentence on how to apply it)
      """.trimIndent()
    }
  }
}
