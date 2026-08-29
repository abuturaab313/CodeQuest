package com.example.domain.ai

import com.example.data.models.AIQuizQuestion
import com.example.data.models.LearnerLevel
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.AIResponse
import com.example.domain.ai.models.LearningContext

class IntelligentLocalAIProvider : AIProvider {

  override val providerName: String = "Local Heuristic Coach"
  override val isAvailable: Boolean = true

  override suspend fun generateMentorResponse(
    mode: AIMentorMode,
    context: LearningContext
  ): Result<AIResponse> {
    return try {
      val response = when (mode) {
        AIMentorMode.HINT -> generateProgressiveHint(context)
        AIMentorMode.DEBUG -> generateDebugAnalysis(context)
        AIMentorMode.EXPLAIN -> generateExplanation(context)
        AIMentorMode.REVIEW -> generateCodeReview(context)
        AIMentorMode.CONCEPT -> generateConceptCoach(context)
        AIMentorMode.QUIZ -> generateTopicQuiz(context)
      }
      Result.success(response)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun generateProgressiveHint(context: LearningContext): AIResponse {
    val level = context.hintLevelRequested.coerceIn(1, 5)
    val code = context.currentCode
    val isBeginner = context.learnerLevel == LearnerLevel.BEGINNER

    val (whatsWrong, why, tryThis, thinkAbout, nextStep) = when {
      // Index error or range len
      code.contains("range(len(") && (code.contains("+ 1") || code.contains("+1")) -> {
        when (level) {
          1 -> HintTuple("Loop boundary discrepancy", "The iteration counter travels beyond the elements present.", "Review the upper bound passed into range().", "If a list has 3 items, what are the valid index numbers?", "Check len(list) vs range(len(list)).")
          2 -> HintTuple("Index boundary alert", "List indexes are zero-based (0 to length - 1).", "Look specifically at `range(len(...) + 1)`.", "What index is reached on the final iteration?", "Remove the extra `+ 1` from the range statement.")
          3 -> HintTuple("Upper bound out of range", "`range(len(items))` already stops before `len(items)` naturally.", "Iterate simply using `range(len(items))`.", "Why is `items[len(items)]` an invalid access in Python?", "Adjust the loop header to standard range.")
          4 -> HintTuple("Loop range pattern", "Use standard sequence length iteration.", "```python\nfor i in range(len(elements)):\n    # access elements[i]\n```", "Can you iterate elements directly instead of indices?", "Replace range addition.")
          else -> HintTuple("IndexError Fix", "Adding +1 causes the loop to query index = length.", "Change `range(len(x) + 1)` to `range(len(x))`.", "Always remember Python length N has items at 0..N-1.", "Run your code to test.")
        }
      }
      // Infinite loop or while without increment
      code.contains("while ") && !code.contains("+=") && !code.contains("-=") && !code.contains("break") -> {
        when (level) {
          1 -> HintTuple("Loop termination condition", "The while condition might never evaluate to False.", "Ensure the state changes inside the loop block.", "What causes your while loop to eventually stop?", "Add state modification.")
          2 -> HintTuple("Missing variable update", "The loop variable stays identical on every turn.", "Check if your loop counter is incremented.", "How does the condition change after one loop pass?", "Increment your counter variable inside the loop.")
          3 -> HintTuple("Infinite Loop Risk", "Without modifying the controlling variable, the loop runs infinitely.", "Add `counter += 1` inside the while loop body.", "Where should the increment line be positioned?", "Indent the increment properly.")
          4 -> HintTuple("Standard While Loop structure", "Increment state before next iteration check.", "```python\nwhile count < limit:\n    # work\n    count += 1\n```", "Is a `for` loop cleaner here?", "Test with small input.")
          else -> HintTuple("Full While Loop Guide", "Increment `i` on each iteration to guarantee termination.", "Add `i += 1` at the end of the loop block.", "Observe how many times it loops with print(i).", "Verify termination condition.")
        }
      }
      // Missing return statement
      code.contains("def ") && !code.contains("return") && (context.exercisePrompt?.contains("return", ignoreCase = true) == true || context.challengeDescription?.contains("return", ignoreCase = true) == true) -> {
        when (level) {
          1 -> HintTuple("Function output missing", "The test runner expects a returned value rather than printing.", "Make sure your function explicitly returns the calculated result.", "What is the difference between `print()` and `return`?", "Add a return statement.")
          2 -> HintTuple("Return value needed", "Functions in Python evaluate to `None` if they lack a return statement.", "Look at the end of your function body for a `return` keyword.", "Does the caller receive the answer?", "Return the final variable.")
          3 -> HintTuple("Use return instead of print", "The grading harness inspects the return value of your function.", "Replace `print(result)` with `return result`.", "Where does the caller store the returned data?", "Check the last line of the function.")
          4 -> HintTuple("Function return syntax", "Return the evaluated expression.", "```python\ndef solution(data):\n    answer = ...\n    return answer\n```", "Is your return indented inside the def block?", "Add return keyword.")
          else -> HintTuple("Explicit Return Fix", "Add `return result` at the conclusion of your function body.", "Change `print(...)` to `return ...`.", "Ensure return is indented under def.", "Execute test suite.")
        }
      }
      // General progressive fallback
      else -> {
        when (level) {
          1 -> HintTuple("Analyze problem inputs & outputs", "Break the task down into distinct computational steps.", "Identify what inputs are given and what output format is required.", "What is the first sub-step needed?", "Draft the first line of logic.")
          2 -> HintTuple("Data structure selection", "Verify you are using the right types (lists, integers, strings, dicts).", "Check variable initialization before entering logic.", "Are your variables named clearly?", "Initialize required tracking variables.")
          3 -> HintTuple("Control flow logic", "Trace the conditions and branches step by step.", "Use `if/else` to handle edge cases like empty inputs or zeros.", "What happens on corner cases?", "Add conditional guards if needed.")
          4 -> HintTuple("Algorithmic pseudocode", "1. Initialize variables\n2. Loop through input\n3. Apply transformation\n4. Return result", "Implement each pseudocode step in Python.", "Are all loops bounded properly?", "Run tests on partial logic.")
          else -> HintTuple("Detailed code structure", "Combine input processing, transformation, and output return.", "Check indentation (4 spaces) and syntax colons.", "Test your code with small sample inputs in the terminal.", "Run verification tests.")
        }
      }
    }

    return AIResponse(
      mode = AIMentorMode.HINT,
      headline = "Code Coach Hint (Level $level of 5)",
      whatsWrong = whatsWrong,
      why = why,
      tryThis = tryThis,
      thinkAbout = thinkAbout,
      optionalNextStep = nextStep,
      hintLevel = level,
      maxHintLevel = 5,
      providerUsed = providerName
    )
  }

  private fun generateDebugAnalysis(context: LearningContext): AIResponse {
    val error = context.recentError ?: context.stderr ?: ""
    val code = context.currentCode

    return when {
      error.contains("SyntaxError", ignoreCase = true) || error.contains("invalid syntax", ignoreCase = true) -> {
        val missingColon = code.lines().any { line ->
          val trimmed = line.trim()
          (trimmed.startsWith("if ") || trimmed.startsWith("for ") || trimmed.startsWith("while ") || trimmed.startsWith("def ") || trimmed.startsWith("elif ") || trimmed.startsWith("else")) && !trimmed.endsWith(":")
        }
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Syntax Error Detected",
          whatsWrong = if (missingColon) "A colon `:` is missing at the end of a control statement (if/for/while/def)." else "Python syntax rules were violated.",
          why = "In Python, compound statements (loops, conditionals, functions) require a trailing colon `:` to open a new block.",
          tryThis = if (missingColon) "Add a colon `:` at the end of each header line (e.g. `if condition:`)." else "Check line brackets, quotation marks, and keyword spelling.",
          thinkAbout = "Does every `def`, `for`, `if`, and `else` line end with a colon?",
          optionalNextStep = "Scan your code lines from top to bottom.",
          providerUsed = providerName
        )
      }
      error.contains("IndentationError", ignoreCase = true) -> {
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Indentation Mismatch",
          whatsWrong = "Indentation levels are inconsistent or missing inside a block.",
          why = "Python uses whitespace indentation (standard 4 spaces) rather than curly braces to define code blocks.",
          tryThis = "Ensure all statements inside `def`, `for`, `while`, and `if` are indented by 4 spaces.",
          thinkAbout = "Did you mix tab characters with space keys?",
          optionalNextStep = "Select lines and align them evenly.",
          providerUsed = providerName
        )
      }
      error.contains("IndexError", ignoreCase = true) -> {
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Index Out of Range",
          whatsWrong = "Your code attempted to query an index position that does not exist in the list or string.",
          why = "Python indexing is zero-based (0 to length - 1). Accessing index = length triggers an `IndexError`.",
          tryThis = "Check your loop upper limit. If iterating indices, use `range(len(items))` without adding extra offsets.",
          thinkAbout = "What is `len(list)` vs the highest valid index?",
          optionalNextStep = "Add a print statement `print('Current index:', i)` before list access.",
          providerUsed = providerName
        )
      }
      error.contains("ZeroDivisionError", ignoreCase = true) -> {
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Division by Zero",
          whatsWrong = "A division (`/` or `//` or `%`) operation was executed with a denominator of 0.",
          why = "Mathematical division by zero is undefined.",
          tryThis = "Add a check: `if denominator != 0:` before dividing, or return an error message when divisor is 0.",
          thinkAbout = "What should your function return when the second argument is zero?",
          optionalNextStep = "Wrap the calculation in a conditional guard.",
          providerUsed = providerName
        )
      }
      error.contains("NameError", ignoreCase = true) -> {
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Undefined Variable Name",
          whatsWrong = "A variable or function identifier was referenced before it was defined or imported.",
          why = "Python evaluates variables in local and global scope; if not found, it raises `NameError`.",
          tryThis = "Check variable spelling and ensure the variable is created above the line that uses it.",
          thinkAbout = "Did you define the variable inside a function or loop that has not executed yet?",
          optionalNextStep = "Verify spelling in both declaration and usage.",
          providerUsed = providerName
        )
      }
      else -> {
        AIResponse(
          mode = AIMentorMode.DEBUG,
          headline = "Diagnostic Analysis",
          whatsWrong = if (context.testSummary != null) "Test assertions failed on specific test cases." else "Code executed but did not produce expected behavior.",
          why = "The logic flow diverged from the expected algorithm or handled edge cases differently.",
          tryThis = "Insert temporary diagnostic prints `print(f'Debug: {val}')` to trace variable values.",
          thinkAbout = "What happens with edge cases such as empty input, zero, negative numbers, or single elements?",
          optionalNextStep = "Step through the logic manually with pen and paper on a small test input.",
          providerUsed = providerName
        )
      }
    }
  }

  private fun generateExplanation(context: LearningContext): AIResponse {
    val topic = context.activeConcept
    return AIResponse(
      mode = AIMentorMode.EXPLAIN,
      headline = "Understanding $topic in Python",
      whatsWrong = "Topic: $topic",
      why = when (topic.uppercase()) {
        "LOOPS", "FOR_LOOPS", "WHILE_LOOPS" -> "Loops allow a block of code to repeat automatically for every element in a sequence or while a condition remains True."
        "CONDITIONS", "IF_STATEMENTS" -> "Conditionals make decisions. `if`, `elif`, and `else` branch execution based on Boolean truth values."
        "FUNCTIONS" -> "Functions are reusable blocks of code that accept parameters, perform computation, and `return` results to callers."
        "LISTS", "INDEXING" -> "Lists are ordered, mutable collections of items accessed by zero-based integer indices."
        else -> "Variables store state, operators manipulate values, and control flow guides program execution step by step."
      },
      tryThis = when (topic.uppercase()) {
        "LOOPS", "FOR_LOOPS" -> "```python\n# Iterate over items\nfor num in [1, 2, 3]:\n    print(num * 2)\n```"
        "CONDITIONS" -> "```python\nif score >= 90:\n    grade = 'A'\nelse:\n    grade = 'B'\n```"
        "FUNCTIONS" -> "```python\ndef square(n):\n    return n * n\n```"
        else -> "```python\nx = 10\ny = 20\ntotal = x + y\n```"
      },
      thinkAbout = "How does this concept make your code more modular and reusable?",
      optionalNextStep = "Try combining this concept with a small coding challenge in Code Lab.",
      providerUsed = providerName
    )
  }

  private fun generateCodeReview(context: LearningContext): AIResponse {
    val code = context.currentCode
    val mustFix = mutableListOf<String>()
    val optional = mutableListOf<String>()

    if (code.lines().any { it.startsWith("\t") }) {
      mustFix.add("Replace tabs with 4 spaces to adhere to PEP 8 indentation standards.")
    }
    if (code.contains("except:") && !code.contains("except Exception:")) {
      optional.add("Avoid bare `except:`; specify the exact exception type (e.g. `except ValueError:`).")
    }
    if (code.split("\n").any { it.length > 88 }) {
      optional.add("Keep lines under 88 characters for better mobile and desktop readability.")
    }
    if (!code.contains("def ") && context.currentCode.length > 100) {
      optional.add("Consider structuring repeated code into dedicated helper functions.")
    }
    if (code.contains("range(len(")) {
      optional.add("Pythonic style: prefer `for item in sequence:` over `for i in range(len(sequence)):` unless indices are strictly necessary.")
    }

    if (mustFix.isEmpty()) {
      mustFix.add("Core syntax conforms well to Python guidelines. No breaking flaws found.")
    }
    if (optional.isEmpty()) {
      optional.add("Code formatting is clean and readable. Good variable naming!")
    }

    return AIResponse(
      mode = AIMentorMode.REVIEW,
      headline = "Code Review Summary",
      whatsWrong = "Code Quality & Structure Assessment",
      why = "Clean, readable code with descriptive identifiers prevents bugs and eases team collaboration.",
      tryThis = "Review the prioritized checklist below.",
      thinkAbout = "Is variable naming immediately obvious to someone reading this for the first time?",
      optionalNextStep = "Apply PEP 8 improvements where applicable.",
      mustFixItems = mustFix,
      optionalImprovementItems = optional,
      providerUsed = providerName
    )
  }

  private fun generateConceptCoach(context: LearningContext): AIResponse {
    return AIResponse(
      mode = AIMentorMode.CONCEPT,
      headline = "Concept Coach: ${context.activeConcept}",
      whatsWrong = "Key Learning Objective: ${context.activeConcept}",
      why = "Mastering ${context.activeConcept} provides building blocks for complex algorithms and real-world projects.",
      tryThis = "Review the core mechanics in the Code Lab and test hypotheses in interactive terminal mode.",
      thinkAbout = "Can you describe the problem-solving steps out loud before typing?",
      optionalNextStep = "Launch a targeted Practice Session on this topic.",
      providerUsed = providerName
    )
  }

  private fun generateTopicQuiz(context: LearningContext): AIResponse {
    val topic = context.activeConcept
    val questions = listOf(
      AIQuizQuestion(
        id = "q1",
        question = "In Python, what is the index of the first element in a list?",
        options = listOf("1", "0", "-1", "undefined"),
        correctOptionIndex = 1,
        explanation = "Python lists are zero-indexed, so the initial item is always at index 0.",
        conceptKey = topic
      ),
      AIQuizQuestion(
        id = "q2",
        question = "What does `range(1, 5)` generate?",
        options = listOf("1, 2, 3, 4, 5", "1, 2, 3, 4", "0, 1, 2, 3, 4", "2, 3, 4, 5"),
        correctOptionIndex = 1,
        explanation = "The stop parameter in range is exclusive, producing integers from 1 up to 4.",
        conceptKey = topic
      ),
      AIQuizQuestion(
        id = "q3",
        question = "Which keyword exits a loop immediately?",
        options = listOf("pass", "continue", "break", "return"),
        correctOptionIndex = 2,
        explanation = "`break` terminates the enclosing loop immediately.",
        conceptKey = topic
      )
    )

    return AIResponse(
      mode = AIMentorMode.QUIZ,
      headline = "Quick Knowledge Check: $topic",
      quizQuestions = questions,
      providerUsed = providerName
    )
  }

  private data class HintTuple(
    val whatsWrong: String,
    val why: String,
    val tryThis: String,
    val thinkAbout: String,
    val nextStep: String
  )
}
