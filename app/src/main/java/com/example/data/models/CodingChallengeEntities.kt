package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class ComparisonMode {
  EXACT,
  TRIMMED,
  NUMERIC_FLOAT,
  CASE_INSENSITIVE,
  LINE_BY_LINE_TRIMMED
}

data class ChallengeExample(
  val input: String,
  val output: String,
  val explanation: String = ""
)

data class ChallengeTestCase(
  val id: String,
  val input: String,
  val expectedOutput: String,
  val isHidden: Boolean = false,
  val comparisonMode: ComparisonMode = ComparisonMode.TRIMMED,
  val description: String = "",
  val timeoutMs: Long = 2000L
)

@Entity(tableName = "coding_challenges")
data class CodingChallengeEntity(
  @PrimaryKey val id: String, // e.g. "py_c_hello_world"
  val lessonId: String? = null,
  val title: String,
  val description: String,
  val difficulty: String = "EASY", // EASY, MEDIUM, HARD
  val languageId: String = "python",
  val category: String = "BEGINNER", // BEGINNER, MATH, STRINGS, LOOPS, LISTS, DEBUGGING, ALGORITHMS
  val starterCode: String = "",
  val solutionRequirements: String = "",
  val inputDescription: String = "",
  val outputDescription: String = "",
  val examplesJson: String = "[]",
  val publicTestsJson: String = "[]",
  val hiddenTestsJson: String = "[]",
  val hintsJson: String = "[]",
  val xpReward: Int = 50,
  val coinReward: Int = 15,
  val timeLimitMs: Long = 2000L,
  val orderIndex: Int = 0,
  val isUnlocked: Boolean = true,
  val isCompleted: Boolean = false,
  val bestScore: Int = 0
) {
  fun parseExamples(): List<ChallengeExample> {
    return try {
      val json = JSONArray(examplesJson)
      List(json.length()) { i ->
        val obj = json.getJSONObject(i)
        ChallengeExample(
          input = obj.optString("input", ""),
          output = obj.optString("output", ""),
          explanation = obj.optString("explanation", "")
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parsePublicTests(): List<ChallengeTestCase> {
    return parseTests(publicTestsJson, false)
  }

  fun parseHiddenTests(): List<ChallengeTestCase> {
    return parseTests(hiddenTestsJson, true)
  }

  private fun parseTests(jsonString: String, defaultHidden: Boolean): List<ChallengeTestCase> {
    return try {
      val json = JSONArray(jsonString)
      List(json.length()) { i ->
        val obj = json.getJSONObject(i)
        val modeStr = obj.optString("comparisonMode", "TRIMMED")
        val mode = try {
          ComparisonMode.valueOf(modeStr)
        } catch (e: Exception) {
          ComparisonMode.TRIMMED
        }
        ChallengeTestCase(
          id = obj.optString("id", "test_$i"),
          input = obj.optString("input", ""),
          expectedOutput = obj.optString("expectedOutput", ""),
          isHidden = obj.optBoolean("isHidden", defaultHidden),
          comparisonMode = mode,
          description = obj.optString("description", ""),
          timeoutMs = obj.optLong("timeoutMs", timeLimitMs)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseHints(): List<String> {
    return try {
      val json = JSONArray(hintsJson)
      List(json.length()) { json.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
  @PrimaryKey val challengeId: String,
  val draftCode: String,
  val lastSubmittedCode: String = "",
  val isCompleted: Boolean = false,
  val attemptsCount: Int = 0,
  val hintsUsedCount: Int = 0,
  val bestExecutionTimeMs: Long = 0L,
  val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "submission_records")
data class SubmissionRecordEntity(
  @PrimaryKey val id: String, // e.g. "sub_167891230_py_c1"
  val challengeId: String,
  val timestampEpochMs: Long = System.currentTimeMillis(),
  val codeSnippet: String,
  val verdict: String, // "PASSED", "WRONG_ANSWER", "RUNTIME_ERROR", "TIME_LIMIT_EXCEEDED", "SYNTAX_ERROR"
  val passedTests: Int,
  val totalTests: Int,
  val executionTimeMs: Long,
  val xpEarned: Int
)
