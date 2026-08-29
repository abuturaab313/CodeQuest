package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.ReviewQueueEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.LessonNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewQueueDao {
  @Query("SELECT * FROM review_queue ORDER BY nextReviewEpochMs ASC")
  fun observeReviewQueue(): Flow<List<ReviewQueueEntity>>

  @Query("SELECT * FROM review_queue ORDER BY nextReviewEpochMs ASC")
  suspend fun getReviewQueueOnce(): List<ReviewQueueEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertReviewItem(item: ReviewQueueEntity)

  @Query("DELETE FROM review_queue WHERE conceptId = :conceptId")
  suspend fun deleteReviewItem(conceptId: String)
}

@Dao
interface BookmarkDao {
  @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAtEpochMs DESC")
  fun observeAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE lessonId = :lessonId LIMIT 1")
  suspend fun getBookmark(lessonId: String): BookmarkEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE lessonId = :lessonId")
  suspend fun deleteBookmark(lessonId: String)
}

@Dao
interface LessonNoteDao {
  @Query("SELECT * FROM lesson_notes ORDER BY updatedAtEpochMs DESC")
  fun observeAllNotes(): Flow<List<LessonNoteEntity>>

  @Query("SELECT * FROM lesson_notes WHERE lessonId = :lessonId LIMIT 1")
  suspend fun getNote(lessonId: String): LessonNoteEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertNote(note: LessonNoteEntity)

  @Query("DELETE FROM lesson_notes WHERE lessonId = :lessonId")
  suspend fun deleteNote(lessonId: String)
}
