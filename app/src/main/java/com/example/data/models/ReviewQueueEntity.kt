package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_queue")
data class ReviewQueueEntity(
  @PrimaryKey val conceptId: String, // e.g. "py_loops"
  val conceptName: String,
  val lastReviewedEpochMs: Long,
  val nextReviewEpochMs: Long,
  val intervalDays: Int, // Spaced intervals: 1, 2, 3, 7, 14 days
  val performanceScore: Int, // 0 to 100 based on accuracy
  val difficulty: String = "Medium"
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey val lessonId: String,
  val lessonTitle: String,
  val worldId: String,
  val bookmarkedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_notes")
data class LessonNoteEntity(
  @PrimaryKey val lessonId: String,
  val lessonTitle: String,
  val noteText: String,
  val updatedAtEpochMs: Long = System.currentTimeMillis()
)
