package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
  @PrimaryKey val id: String, // e.g. "python"
  val title: String,
  val description: String,
  val language: String,
  val totalWorlds: Int,
  val iconName: String,
  val orderIndex: Int,
  val isAvailable: Boolean = true,
  val estimatedHours: Int = 10,
  val difficulty: String = "Beginner"
)

@Entity(tableName = "worlds")
data class WorldEntity(
  @PrimaryKey val id: String, // e.g. "py_w1"
  val courseId: String,
  val worldNumber: Int,
  val title: String,
  val subtitle: String,
  val themeColorHex: String,
  val iconName: String,
  val requiredXp: Int = 0,
  val isUnlocked: Boolean = true,
  val topicsJson: String = "[]"
)

@Entity(tableName = "chapters")
data class ChapterEntity(
  @PrimaryKey val id: String, // e.g. "py_c1"
  val worldId: String,
  val chapterNumber: Int,
  val title: String,
  val description: String
)

enum class LessonType {
  LESSON,
  CHALLENGE,
  BOSS,
  PROJECT
}

@Entity(tableName = "lessons")
data class LessonEntity(
  @PrimaryKey val id: String, // e.g. "py_l1"
  val chapterId: String,
  val worldId: String,
  val lessonNumber: Int,
  val title: String,
  val description: String,
  val lessonType: LessonType = LessonType.LESSON,
  val xpReward: Int = 25,
  val coinReward: Int = 10,
  val isUnlocked: Boolean = false,
  val isCompleted: Boolean = false,
  val starsEarned: Int = 0, // 0 to 3
  val conceptSummary: String = "",
  val conceptSnippet: String = "",
  val conceptExplanation: String = "",
  val estimatedMinutes: Int = 3,
  val learningObjectivesJson: String = "[]",
  val prerequisiteLessonId: String? = null
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
  @PrimaryKey val lessonId: String,
  val currentExerciseIndex: Int = 0,
  val totalExercises: Int = 0,
  val correctCount: Int = 0,
  val incorrectCount: Int = 0,
  val hintsUsedCount: Int = 0,
  val scorePercentage: Int = 0,
  val isPerfectRun: Boolean = false,
  val isCompleted: Boolean = false,
  val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_mistakes")
data class UserMistakeEntity(
  @PrimaryKey val id: String, // e.g. "mistake_${exerciseId}"
  val exerciseId: String,
  val lessonId: String,
  val topic: String, // e.g. "Variables", "Strings", "Syntax", "print()", "Loops", "Conditions"
  val prompt: String,
  val explanation: String,
  val starterCode: String = "",
  val solutionCode: String = "",
  val optionsJson: String = "[]",
  val correctAnswersJson: String = "[]",
  val exerciseType: ExerciseType = ExerciseType.MULTIPLE_CHOICE,
  val wrongAnswerProvided: String = "",
  val errorCount: Int = 1,
  val isResolved: Boolean = false,
  val recordedEpochMs: Long = System.currentTimeMillis()
)
