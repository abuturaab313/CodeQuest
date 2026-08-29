package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.LessonEntity
import com.example.data.models.LessonProgressEntity
import com.example.data.models.UserMistakeEntity
import com.example.data.models.WorldEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
  @Query("SELECT * FROM courses ORDER BY orderIndex ASC")
  fun getAllCourses(): Flow<List<CourseEntity>>

  @Query("SELECT * FROM courses WHERE id = :courseId")
  suspend fun getCourseById(courseId: String): CourseEntity?

  @Query("SELECT * FROM worlds WHERE courseId = :courseId ORDER BY worldNumber ASC")
  fun getWorldsForCourse(courseId: String): Flow<List<WorldEntity>>

  @Query("SELECT * FROM worlds ORDER BY worldNumber ASC")
  fun getAllWorlds(): Flow<List<WorldEntity>>

  @Query("SELECT * FROM chapters WHERE worldId = :worldId ORDER BY chapterNumber ASC")
  fun getChaptersForWorld(worldId: String): Flow<List<ChapterEntity>>

  @Query("SELECT * FROM lessons WHERE worldId = :worldId ORDER BY lessonNumber ASC")
  fun getLessonsForWorld(worldId: String): Flow<List<LessonEntity>>

  @Query("SELECT * FROM lessons ORDER BY lessonNumber ASC")
  fun getAllLessons(): Flow<List<LessonEntity>>

  @Query("SELECT * FROM lessons ORDER BY lessonNumber ASC")
  suspend fun getAllLessonsOnce(): List<LessonEntity>

  @Query("SELECT * FROM lessons WHERE id = :lessonId")
  suspend fun getLessonById(lessonId: String): LessonEntity?

  @Query("SELECT * FROM exercises WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
  fun getExercisesForLesson(lessonId: String): Flow<List<ExerciseEntity>>

  @Query("SELECT * FROM exercises WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
  suspend fun getExercisesForLessonOnce(lessonId: String): List<ExerciseEntity>

  @Query("UPDATE lessons SET isCompleted = 1, starsEarned = max(starsEarned, :stars) WHERE id = :lessonId")
  suspend fun markLessonCompleted(lessonId: String, stars: Int)

  @Query("UPDATE lessons SET isUnlocked = 1 WHERE id = :lessonId")
  suspend fun unlockLesson(lessonId: String)

  @Query("UPDATE worlds SET isUnlocked = 1 WHERE id = :worldId")
  suspend fun unlockWorld(worldId: String)

  // Lesson In-Progress State
  @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
  suspend fun getLessonProgress(lessonId: String): LessonProgressEntity?

  @Query("SELECT * FROM lesson_progress")
  fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveLessonProgress(progress: LessonProgressEntity)

  @Query("DELETE FROM lesson_progress WHERE lessonId = :lessonId")
  suspend fun clearLessonProgress(lessonId: String)

  // User Mistakes & Review Mode
  @Query("SELECT * FROM user_mistakes WHERE isResolved = 0 ORDER BY recordedEpochMs DESC")
  fun getUnresolvedMistakes(): Flow<List<UserMistakeEntity>>

  @Query("SELECT * FROM user_mistakes WHERE isResolved = 0 ORDER BY recordedEpochMs DESC")
  suspend fun getUnresolvedMistakesOnce(): List<UserMistakeEntity>

  @Query("SELECT COUNT(*) FROM user_mistakes WHERE isResolved = 0")
  fun getUnresolvedMistakesCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun recordMistake(mistake: UserMistakeEntity)

  @Query("UPDATE user_mistakes SET isResolved = 1 WHERE id = :mistakeId")
  suspend fun markMistakeResolved(mistakeId: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCourses(courses: List<CourseEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWorlds(worlds: List<WorldEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChapters(chapters: List<ChapterEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLessons(lessons: List<LessonEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercises(exercises: List<ExerciseEntity>)
}
