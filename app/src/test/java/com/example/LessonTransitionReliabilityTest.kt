package com.example

import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonProgressEntity
import com.example.domain.learning.AnswerValidator
import com.example.domain.learning.HintService
import com.example.domain.learning.LessonScoringService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LessonTransitionReliabilityTest {

  private val validator = AnswerValidator()
  private val scoringService = LessonScoringService()
  private val hintService = HintService()

  @Test
  fun testThirtyConsecutiveQuestionTransitionsWithoutCrash() {
    // Generate 30 distinct exercises covering all exercise types
    val exerciseTypes = listOf(
      ExerciseType.MULTIPLE_CHOICE,
      ExerciseType.PREDICT_OUTPUT,
      ExerciseType.TRUE_FALSE,
      ExerciseType.FILL_IN_BLANK,
      ExerciseType.CODE_ORDER,
      ExerciseType.MATCH_CONCEPTS,
      ExerciseType.FIND_BUG,
      ExerciseType.WRITE_CODE,
      ExerciseType.COMPLETE_CODE,
      ExerciseType.BOSS_CHALLENGE
    )

    val exercises = (0 until 30).map { i ->
      val type = exerciseTypes[i % exerciseTypes.size]
      when (type) {
        ExerciseType.MULTIPLE_CHOICE -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "What is the output of print($i)?",
          type = type,
          optionsJson = "[\"$i\", \"${i + 1}\", \"${i + 2}\", \"None\"]",
          correctAnswersJson = "[\"$i\"]",
          hintsJson = "[\"Look at what value was printed.\", \"The parameter is $i.\"]",
          explanation = "Printing $i outputs string representation $i"
        )
        ExerciseType.PREDICT_OUTPUT -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Predict output of x = $i; print(x * 2)",
          type = type,
          optionsJson = "[\"${i * 2}\", \"${i + 2}\", \"$i$i\", \"Error\"]",
          correctAnswersJson = "[\"${i * 2}\"]",
          hintsJson = "[\"Multiply the number by 2.\"]",
          explanation = "$i multiplied by 2 is ${i * 2}"
        )
        ExerciseType.TRUE_FALSE -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Is $i >= 0?",
          type = type,
          optionsJson = "[\"True\", \"False\"]",
          correctAnswersJson = "[\"True\"]",
          hintsJson = "[\"All non-negative integers are >= 0.\"]",
          explanation = "$i is a non-negative integer"
        )
        ExerciseType.FILL_IN_BLANK -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Complete: print(___)",
          type = type,
          starterCode = "print(___)",
          correctAnswersJson = "[\"$i\", \"'$i'\"]",
          expectedOutput = "$i",
          hintsJson = "[\"Insert $i inside print.\"]",
          explanation = "Fill in with $i"
        )
        ExerciseType.CODE_ORDER -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Order the tokens to print $i",
          type = type,
          optionsJson = "[\"print(\", \"$i\", \")\"]",
          correctAnswersJson = "[\"print(\", \"$i\", \")\"]",
          hintsJson = "[\"Start with function call print.\"]",
          explanation = "Correct syntax is print($i)"
        )
        ExerciseType.MATCH_CONCEPTS -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Match the variable types",
          type = type,
          optionsJson = "[\"num -> integer\", \"msg -> string\"]",
          correctAnswersJson = "[\"num:integer;msg:string\"]",
          hintsJson = "[\"num is a number, msg is a string.\"]",
          explanation = "num is integer, msg is string"
        )
        ExerciseType.FIND_BUG -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Find the bug in print('$i'",
          type = type,
          optionsJson = "[\"Missing closing parenthesis\", \"Invalid quote\", \"Keyword error\"]",
          correctAnswersJson = "[\"Missing closing parenthesis\"]",
          hintsJson = "[\"Count open and closed parentheses.\"]",
          explanation = "Every opened parenthesis must be closed"
        )
        ExerciseType.WRITE_CODE, ExerciseType.COMPLETE_CODE -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Write code to set x = $i",
          type = type,
          starterCode = "x = ",
          solutionCode = "x = $i",
          correctAnswersJson = "[\"x = $i\"]",
          expectedOutput = "$i",
          hintsJson = "[\"Assign the integer value.\"]",
          explanation = "Assign $i to variable x"
        )
        ExerciseType.BOSS_CHALLENGE -> ExerciseEntity(
          id = "ex_$i",
          lessonId = "lesson_test",
          orderIndex = i,
          prompt = "Boss Phase $i: Final challenge",
          type = type,
          optionsJson = "[\"Option 1\", \"Option 2\", \"Correct Phase Answer\"]",
          solutionCode = "Correct Phase Answer",
          correctAnswersJson = "[\"Correct Phase Answer\"]",
          hintsJson = "[\"Select the correct phase answer.\"]",
          explanation = "Boss phase defeated"
        )
      }
    }

    var correctCount = 0
    var mistakeCount = 0
    var hintsUsed = 0

    // Simulate 30 sequential questions answered and transitioned
    for (index in 0 until 30) {
      val exercise = exercises[index]
      assertNotNull(exercise)

      // Test progressive hint building
      val hints = hintService.buildProgressiveHints(
        rawHints = exercise.parseHints(),
        explanation = exercise.explanation
      )
      assertTrue(hints.isNotEmpty())
      hintsUsed += 1

      // 1. Submit answer
      val answerToSubmit = when (exercise.type) {
        ExerciseType.MULTIPLE_CHOICE, ExerciseType.PREDICT_OUTPUT -> exercise.parseCorrectAnswers().first()
        ExerciseType.TRUE_FALSE -> "True"
        ExerciseType.FILL_IN_BLANK -> "$index"
        ExerciseType.CODE_ORDER -> listOf("print(", "$index", ")").joinToString(", ")
        ExerciseType.MATCH_CONCEPTS -> "num:integer;msg:string"
        ExerciseType.FIND_BUG -> "Missing closing parenthesis"
        ExerciseType.WRITE_CODE, ExerciseType.COMPLETE_CODE -> "x = $index"
        ExerciseType.BOSS_CHALLENGE -> "Correct Phase Answer"
      }

      val result = validator.validate(
        type = exercise.type,
        submittedAnswer = answerToSubmit,
        correctAnswers = exercise.parseCorrectAnswers(),
        options = exercise.parseOptions(),
        solutionCode = exercise.solutionCode,
        expectedOutput = exercise.expectedOutput
      )

      assertNotNull(result)
      assertTrue(result.feedbackMessage.isNotBlank())

      if (result.isCorrect) {
        correctCount++
      } else {
        println("Exercise $index type ${exercise.type} failed: ${result.feedbackMessage}, submitted: $answerToSubmit, correct: ${exercise.correctAnswersJson}")
        mistakeCount++
      }

      // Simulate saving progress at this index
      val progressEntity = LessonProgressEntity(
        lessonId = "lesson_test",
        currentExerciseIndex = index + 1,
        totalExercises = 30,
        correctCount = correctCount,
        incorrectCount = mistakeCount,
        hintsUsedCount = hintsUsed,
        isCompleted = index == 29,
        lastUpdatedEpochMs = System.currentTimeMillis()
      )

      assertEquals(index + 1, progressEntity.currentExerciseIndex)
      assertEquals(30, progressEntity.totalExercises)
    }

    assertEquals(30, correctCount)
    assertEquals(0, mistakeCount)

    // 1. Calculate scoring for run with hints
    val scoreWithHints = scoringService.calculateLessonScore(
      totalExercises = 30,
      correctCount = correctCount,
      mistakeCount = mistakeCount,
      hintsUsedCount = hintsUsed,
      baseXp = 50,
      baseCoins = 20,
      currentStreak = 5
    )

    assertEquals(100, scoreWithHints.accuracyPercentage)
    assertEquals(1, scoreWithHints.stars)
    assertFalse(scoreWithHints.isPerfectRun)
    assertTrue(scoreWithHints.totalXp >= 50)
    assertTrue(scoreWithHints.coinsEarned >= 20)

    // 2. Calculate scoring for perfect run without hints
    val perfectScore = scoringService.calculateLessonScore(
      totalExercises = 30,
      correctCount = correctCount,
      mistakeCount = 0,
      hintsUsedCount = 0,
      baseXp = 50,
      baseCoins = 20,
      currentStreak = 5
    )

    assertEquals(3, perfectScore.stars)
    assertTrue(perfectScore.isPerfectRun)
    assertEquals(100, perfectScore.accuracyPercentage)
    assertTrue(perfectScore.totalXp > 50)
    assertTrue(perfectScore.coinsEarned > 20)
  }
}
