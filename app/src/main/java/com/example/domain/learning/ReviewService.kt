package com.example.domain.learning

import com.example.data.models.ExerciseEntity
import com.example.data.models.UserMistakeEntity

data class MistakeTopicSummary(
  val topic: String,
  val count: Int
)

class ReviewService {

  /**
   * Builds a UserMistakeEntity when an exercise is answered incorrectly.
   */
  fun createMistakeRecord(
    exercise: ExerciseEntity,
    wrongAnswer: String
  ): UserMistakeEntity {
    return UserMistakeEntity(
      id = "mistake_${exercise.id}",
      exerciseId = exercise.id,
      lessonId = exercise.lessonId,
      topic = exercise.topic,
      prompt = exercise.prompt,
      explanation = exercise.explanation,
      starterCode = exercise.starterCode,
      solutionCode = exercise.solutionCode,
      optionsJson = exercise.optionsJson,
      correctAnswersJson = exercise.correctAnswersJson,
      exerciseType = exercise.type,
      wrongAnswerProvided = wrongAnswer,
      errorCount = 1,
      isResolved = false,
      recordedEpochMs = System.currentTimeMillis()
    )
  }

  /**
   * Converts a recorded mistake into an active ExerciseEntity for the review session.
   */
  fun convertToReviewExercise(mistake: UserMistakeEntity, orderIndex: Int): ExerciseEntity {
    return ExerciseEntity(
      id = "rev_${mistake.exerciseId}",
      lessonId = mistake.lessonId,
      orderIndex = orderIndex,
      type = mistake.exerciseType,
      prompt = mistake.prompt,
      explanation = mistake.explanation,
      starterCode = mistake.starterCode,
      solutionCode = mistake.solutionCode,
      expectedOutput = "",
      optionsJson = mistake.optionsJson,
      correctAnswersJson = mistake.correctAnswersJson,
      hintsJson = "[]",
      testCasesJson = "[]",
      topic = mistake.topic
    )
  }

  /**
   * Summarizes mistakes by topic for the practice screen dashboard.
   */
  fun summarizeMistakeTopics(mistakes: List<UserMistakeEntity>): List<MistakeTopicSummary> {
    return mistakes
      .filter { !it.isResolved }
      .groupBy { it.topic }
      .map { (topic, list) -> MistakeTopicSummary(topic, list.size) }
      .sortedByDescending { it.count }
  }
}
