package com.example.domain.learning

import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity

class PrerequisiteService {

  /**
   * Evaluates if a lesson can be accessed based on explicit unlock status or prerequisites.
   */
  fun canAccessLesson(
    lesson: LessonEntity,
    allLessons: List<LessonEntity>,
    userXp: Int
  ): Boolean {
    // If explicitly unlocked in database, allow
    if (lesson.isUnlocked) return true

    // Check prerequisite lesson ID
    val prereqId = lesson.prerequisiteLessonId
    if (prereqId != null) {
      val prereqLesson = allLessons.find { it.id == prereqId }
      if (prereqLesson != null && prereqLesson.isCompleted) {
        return true
      }
    }

    // Default sequential fallback: if previous lesson in same world is completed
    val worldLessons = allLessons.filter { it.worldId == lesson.worldId }.sortedBy { it.lessonNumber }
    val currentIndex = worldLessons.indexOfFirst { it.id == lesson.id }
    if (currentIndex > 0) {
      val previousLesson = worldLessons[currentIndex - 1]
      return previousLesson.isCompleted
    }

    // First lesson in unlocked world is unlocked
    if (currentIndex == 0 && lesson.lessonNumber == 1) {
      return true
    }

    return false
  }

  /**
   * Evaluates if a world is accessible.
   */
  fun canAccessWorld(
    world: WorldEntity,
    userXp: Int,
    allWorlds: List<WorldEntity>,
    allLessons: List<LessonEntity>
  ): Boolean {
    if (world.isUnlocked) return true
    if (userXp < world.requiredXp) return false

    // World 1 is always unlocked
    if (world.worldNumber == 1) return true

    // Previous world must have completed its boss level
    val prevWorldNumber = world.worldNumber - 1
    val prevWorld = allWorlds.find { it.worldNumber == prevWorldNumber }
    if (prevWorld != null) {
      val prevBoss = allLessons.find { it.worldId == prevWorld.id && it.lessonType == LessonType.BOSS }
      if (prevBoss != null) {
        return prevBoss.isCompleted
      }
    }

    return true
  }

  /**
   * Determines if a boss challenge in a world is ready to be fought (all standard lessons in chapter completed).
   */
  fun isBossUnlocked(
    worldId: String,
    allLessons: List<LessonEntity>
  ): Boolean {
    val worldStandardLessons = allLessons.filter { it.worldId == worldId && it.lessonType == LessonType.LESSON }
    return worldStandardLessons.isNotEmpty() && worldStandardLessons.all { it.isCompleted }
  }
}
