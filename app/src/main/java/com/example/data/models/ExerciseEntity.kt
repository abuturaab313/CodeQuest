package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseType {
  MULTIPLE_CHOICE,
  TRUE_FALSE,
  FILL_IN_BLANK,
  CODE_ORDER,
  PREDICT_OUTPUT,
  FIND_BUG,
  WRITE_CODE,
  COMPLETE_CODE,
  MATCH_CONCEPTS,
  BOSS_CHALLENGE
}

data class TestCase(
  val id: String,
  val input: String,
  val expectedOutput: String,
  val isHidden: Boolean = false,
  val description: String = ""
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
  @PrimaryKey val id: String,
  val lessonId: String,
  val orderIndex: Int,
  val type: ExerciseType,
  val prompt: String,
  val explanation: String = "",
  val starterCode: String = "",
  val solutionCode: String = "",
  val expectedOutput: String = "",
  val optionsJson: String = "[]", // JSON array of string options
  val correctAnswersJson: String = "[]", // JSON array of correct answer tokens/strings
  val hintsJson: String = "[]", // JSON array of progressive hints
  val testCasesJson: String = "[]", // JSON array of TestCase objects
  val topic: String = "Fundamentals"
) {
  fun parseOptions(): List<String> {
    return try {
      val json = org.json.JSONArray(optionsJson)
      List(json.length()) { json.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseCorrectAnswers(): List<String> {
    return try {
      val json = org.json.JSONArray(correctAnswersJson)
      List(json.length()) { json.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseHints(): List<String> {
    return try {
      val json = org.json.JSONArray(hintsJson)
      List(json.length()) { json.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  /**
   * For MATCH_CONCEPTS, parses options in the format:
   * "LeftItem -> RightItem" or JSON pairs
   */
  fun parseMatchingPairs(): List<Pair<String, String>> {
    val options = parseOptions()
    return options.mapNotNull { item ->
      if (item.contains("->")) {
        val parts = item.split("->", limit = 2)
        parts[0].trim() to parts[1].trim()
      } else {
        null
      }
    }
  }
}
