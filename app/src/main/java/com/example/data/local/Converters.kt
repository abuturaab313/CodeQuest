package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.models.ExerciseType
import com.example.data.models.LessonType
import com.example.data.models.QuestType

class Converters {
  @TypeConverter
  fun fromLessonType(value: LessonType): String = value.name

  @TypeConverter
  fun toLessonType(value: String): LessonType = try {
    LessonType.valueOf(value)
  } catch (e: Exception) {
    LessonType.LESSON
  }

  @TypeConverter
  fun fromExerciseType(value: ExerciseType): String = value.name

  @TypeConverter
  fun toExerciseType(value: String): ExerciseType = try {
    ExerciseType.valueOf(value)
  } catch (e: Exception) {
    ExerciseType.MULTIPLE_CHOICE
  }

  @TypeConverter
  fun fromQuestType(value: QuestType): String = value.name

  @TypeConverter
  fun toQuestType(value: String): QuestType = try {
    QuestType.valueOf(value)
  } catch (e: Exception) {
    QuestType.LESSONS_COMPLETED
  }
}
