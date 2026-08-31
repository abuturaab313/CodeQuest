package com.example.domain.learning

import com.example.data.models.ExerciseType

data class ValidationResult(
  val isCorrect: Boolean,
  val feedbackMessage: String,
  val normalizedSubmitted: String = ""
)

class AnswerValidator {

  /**
   * Validates a submitted answer against exercise data.
   */
  fun validate(
    type: ExerciseType,
    submittedAnswer: String,
    correctAnswers: List<String>,
    options: List<String> = emptyList(),
    solutionCode: String = "",
    expectedOutput: String = ""
  ): ValidationResult {
    val cleanSubmitted = submittedAnswer.trim()

    if (cleanSubmitted.isEmpty()) {
      return ValidationResult(
        isCorrect = false,
        feedbackMessage = "Please provide an answer before checking.",
        normalizedSubmitted = cleanSubmitted
      )
    }

    return when (type) {
      ExerciseType.MULTIPLE_CHOICE -> validateMultipleChoice(cleanSubmitted, correctAnswers, options)
      ExerciseType.TRUE_FALSE -> validateTrueFalse(cleanSubmitted, correctAnswers)
      ExerciseType.FILL_IN_BLANK -> validateFillInBlank(cleanSubmitted, correctAnswers)
      ExerciseType.CODE_ORDER -> validateCodeOrder(cleanSubmitted, correctAnswers)
      ExerciseType.PREDICT_OUTPUT -> validatePredictOutput(cleanSubmitted, correctAnswers, expectedOutput)
      ExerciseType.FIND_BUG -> validateFindBug(cleanSubmitted, correctAnswers, options)
      ExerciseType.COMPLETE_CODE -> validateCompleteCode(cleanSubmitted, correctAnswers, solutionCode)
      ExerciseType.MATCH_CONCEPTS -> validateConceptMatching(cleanSubmitted, correctAnswers, options)
      ExerciseType.WRITE_CODE, ExerciseType.BOSS_CHALLENGE -> validateWriteCode(cleanSubmitted, solutionCode, expectedOutput, correctAnswers)
    }
  }

  private fun validateMultipleChoice(
    submitted: String,
    correctAnswers: List<String>,
    options: List<String>
  ): ValidationResult {
    // 1. Direct text match with one of the correct answers
    val directMatch = correctAnswers.any { it.trim().equals(submitted, ignoreCase = true) }
    if (directMatch) {
      return ValidationResult(true, "Correct! Great job.", submitted)
    }

    // 2. Letter match: "A", "B", "C", "D"
    val letterIndex = when (submitted.uppercase()) {
      "A" -> 0
      "B" -> 1
      "C" -> 2
      "D" -> 3
      else -> -1
    }
    if (letterIndex in options.indices) {
      val optionText = options[letterIndex].trim()
      val matches = correctAnswers.any { it.trim().equals(optionText, ignoreCase = true) }
      if (matches) {
        return ValidationResult(true, "Correct! Great job.", optionText)
      }
    }

    return ValidationResult(
      isCorrect = false,
      feedbackMessage = "That's not quite right. Review the choices and try again!",
      normalizedSubmitted = submitted
    )
  }

  private fun validateTrueFalse(
    submitted: String,
    correctAnswers: List<String>
  ): ValidationResult {
    val subBool = when (submitted.lowercase()) {
      "true", "t", "1", "yes" -> "True"
      "false", "f", "0", "no" -> "False"
      else -> submitted
    }
    val correctBool = correctAnswers.firstOrNull()?.let {
      when (it.lowercase()) {
        "true", "t", "1", "yes" -> "True"
        "false", "f", "0", "no" -> "False"
        else -> it
      }
    } ?: "True"

    val isCorrect = subBool.equals(correctBool, ignoreCase = true)
    return ValidationResult(
      isCorrect = isCorrect,
      feedbackMessage = if (isCorrect) "Correct answer!" else "Not quite. Think carefully about the statement.",
      normalizedSubmitted = subBool
    )
  }

  private fun validateFillInBlank(
    submitted: String,
    correctAnswers: List<String>
  ): ValidationResult {
    val normSub = normalizeCodeToken(submitted)
    val isCorrect = correctAnswers.any { normAns ->
      normalizeCodeToken(normAns) == normSub ||
        normAns.trim() == submitted ||
        normAns.trim().replace("\"", "'") == submitted.replace("\"", "'")
    }

    return ValidationResult(
      isCorrect = isCorrect,
      feedbackMessage = if (isCorrect) "Perfect fit!" else "The token did not match. Check spelling and syntax.",
      normalizedSubmitted = normSub
    )
  }

  private fun validateCodeOrder(
    submitted: String,
    correctAnswers: List<String>
  ): ValidationResult {
    // submitted can be comma-separated tokens, e.g. "print, (, 'Hello', )"
    // or concatenated tokens e.g. "print('Hello')"
    val userTokens = submitted.split(",", "\n", " ").map { it.trim() }.filter { it.isNotEmpty() }
    val expectedTokens = if (correctAnswers.size == 1 && (correctAnswers[0].contains(",") || correctAnswers[0].contains(" "))) {
      correctAnswers[0].split(",", "\n", " ").map { it.trim() }.filter { it.isNotEmpty() }
    } else {
      correctAnswers.map { it.trim() }
    }

    val exactSequenceMatch = userTokens == expectedTokens
    val joinedUser = userTokens.joinToString("")
    val joinedExpected = expectedTokens.joinToString("")
    val joinedMatch = joinedUser == joinedExpected

    val isCorrect = exactSequenceMatch || joinedMatch

    return ValidationResult(
      isCorrect = isCorrect,
      feedbackMessage = if (isCorrect) "Code blocks ordered correctly!" else "The order of code blocks is incorrect. Trace how the computer reads it.",
      normalizedSubmitted = joinedUser
    )
  }

  private fun validatePredictOutput(
    submitted: String,
    correctAnswers: List<String>,
    expectedOutput: String
  ): ValidationResult {
    val cleanSub = normalizeOutput(submitted)
    val cleanExpected = normalizeOutput(expectedOutput)

    val matchesExpected = cleanExpected.isNotEmpty() && cleanSub == cleanExpected
    val matchesCorrectList = correctAnswers.any { normalizeOutput(it) == cleanSub }

    val isCorrect = matchesExpected || matchesCorrectList
    return ValidationResult(
      isCorrect = isCorrect,
      feedbackMessage = if (isCorrect) "Exact output match! You understand how this executes." else "Output did not match expected console text.",
      normalizedSubmitted = cleanSub
    )
  }

  private fun validateFindBug(
    submitted: String,
    correctAnswers: List<String>,
    options: List<String>
  ): ValidationResult {
    return validateMultipleChoice(submitted, correctAnswers, options)
  }

  private fun validateCompleteCode(
    submitted: String,
    correctAnswers: List<String>,
    solutionCode: String
  ): ValidationResult {
    val normSub = normalizeCodeToken(submitted)
    val isCorrect = correctAnswers.any { normalizeCodeToken(it) == normSub || it.trim().equals(submitted, ignoreCase = false) }
    return ValidationResult(
      isCorrect = isCorrect,
      feedbackMessage = if (isCorrect) "Code completed accurately!" else "Code does not solve the exercise. Check variable names or syntax.",
      normalizedSubmitted = normSub
    )
  }

  private fun validateConceptMatching(
    submitted: String,
    correctAnswers: List<String>,
    options: List<String>
  ): ValidationResult {
    // submitted: e.g. "int:Integer,str:String,list:List"
    // parse expected pairs
    val expectedPairs = mutableMapOf<String, String>()
    
    // Parse from options with "Left -> Right"
    options.forEach { opt ->
      if (opt.contains("->")) {
        val parts = opt.split("->", limit = 2)
        expectedPairs[parts[0].trim().lowercase()] = parts[1].trim().lowercase()
      }
    }

    // Also parse from correctAnswers if formatted as "Left:Right" or "Left->Right"
    correctAnswers.flatMap { it.split(";", ",") }.forEach { ans ->
      val trimmed = ans.trim()
      if (trimmed.contains("->")) {
        val parts = trimmed.split("->", limit = 2)
        expectedPairs[parts[0].trim().lowercase()] = parts[1].trim().lowercase()
      } else if (trimmed.contains(":")) {
        val parts = trimmed.split(":", limit = 2)
        expectedPairs[parts[0].trim().lowercase()] = parts[1].trim().lowercase()
      }
    }

    if (expectedPairs.isEmpty()) {
      return ValidationResult(true, "Concepts matched!", submitted)
    }

    // Parse user submission pairs
    val userPairs = submitted.split(";", ",").mapNotNull { item ->
      if (item.contains(":") || item.contains("->")) {
        val delimiter = if (item.contains(":")) ":" else "->"
        val parts = item.split(delimiter, limit = 2)
        parts[0].trim().lowercase() to parts[1].trim().lowercase()
      } else {
        null
      }
    }.toMap()

    val allMatched = expectedPairs.all { (concept, definition) ->
      userPairs[concept] == definition
    }

    return ValidationResult(
      isCorrect = allMatched,
      feedbackMessage = if (allMatched) "All concept pairs matched perfectly!" else "Some pairs are incorrect. Reconnect the concepts to their definitions.",
      normalizedSubmitted = submitted
    )
  }

  private fun validateWriteCode(
    submitted: String,
    solutionCode: String,
    expectedOutput: String,
    correctAnswers: List<String>
  ): ValidationResult {
    val normSub = normalizeCode(submitted)
    val normSol = normalizeCode(solutionCode)

    if (normSol.isNotEmpty() && normSub == normSol) {
      return ValidationResult(true, "Solution matches perfectly!", submitted)
    }

    val answerMatch = correctAnswers.any { normalizeCode(it) == normSub }
    if (answerMatch) {
      return ValidationResult(true, "Solution matches perfectly!", submitted)
    }

    // Check if code contains expected keywords (e.g. print) and string constants
    if (expectedOutput.isNotEmpty() && submitted.contains(expectedOutput)) {
      return ValidationResult(true, "Code outputs the correct value!", submitted)
    }

    return ValidationResult(
      isCorrect = false,
      feedbackMessage = "Code output or structure did not meet the objective. Review starter hints.",
      normalizedSubmitted = normSub
    )
  }

  private fun normalizeCodeToken(token: String): String {
    return token.trim().replace("\\s+".toRegex(), " ")
  }

  private fun normalizeOutput(output: String): String {
    return output.replace("\r\n", "\n")
      .lines()
      .map { it.trimEnd() }
      .filter { it.isNotEmpty() }
      .joinToString("\n")
      .trim()
  }

  private fun normalizeCode(code: String): String {
    return code.replace("\r\n", "\n")
      .lines()
      .map { it.trim() }
      .filter { it.isNotEmpty() && !it.startsWith("#") }
      .joinToString("\n")
  }
}
