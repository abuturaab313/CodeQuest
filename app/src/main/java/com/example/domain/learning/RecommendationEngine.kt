package com.example.domain.learning

import com.example.data.models.CodingChallengeEntity
import com.example.data.models.LearningRecommendation
import com.example.data.models.LessonEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.RecommendationType
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.UserMistakeEntity

class RecommendationEngine {

  /**
   * Generates smart, personalized recommendations based on the learner's actual
   * progress, weak concepts, unresolved mistakes, and available projects.
   */
  fun generateRecommendations(
    unlockedLessons: List<LessonEntity>,
    completedLessons: List<LessonEntity>,
    challenges: List<CodingChallengeEntity>,
    projects: List<ProjectEntity>,
    unresolvedMistakes: List<UserMistakeEntity>,
    skills: List<SkillMasteryEntity>
  ): List<LearningRecommendation> {
    val list = mutableListOf<LearningRecommendation>()

    // 1. Weak Skill Remediation (Highest Priority if weak skills exist)
    val weakSkills = skills.filter { it.masteryPercentage < 65 && it.totalAttempted > 0 }
    if (weakSkills.isNotEmpty()) {
      val weakest = weakSkills.minByOrNull { it.masteryPercentage }!!
      // Find matching challenge or practice
      val matchingChallenge = challenges.firstOrNull { it.category.equals(weakest.skillName, ignoreCase = true) || it.title.contains(weakest.skillName, ignoreCase = true) }
      list.add(
        LearningRecommendation(
          id = "rec_weak_${weakest.id}",
          type = RecommendationType.PRACTICE_WEAK_SKILL,
          title = "Strengthen ${weakest.skillName}",
          subtitle = "Current mastery: ${weakest.masteryPercentage}%",
          reason = "You've had lower accuracy on ${weakest.skillName}. Practice with guided AI hints to level up!",
          targetId = matchingChallenge?.id ?: weakest.id,
          xpReward = 60,
          difficulty = "Medium",
          iconName = "target"
        )
      )
    }

    // 2. Unresolved Mistake Debugging
    if (unresolvedMistakes.isNotEmpty()) {
      val mistake = unresolvedMistakes.first()
      list.add(
        LearningRecommendation(
          id = "rec_mistake_${mistake.id}",
          type = RecommendationType.DEBUG_MISTAKE,
          title = "Fix: ${mistake.topic}",
          subtitle = "${unresolvedMistakes.size} mistakes to review",
          reason = "Review and fix previous exercise mistakes with Code Coach diagnostic feedback.",
          targetId = mistake.id,
          xpReward = 40,
          difficulty = "Easy",
          iconName = "bug"
        )
      )
    }

    // 3. Next Curriculum Lesson
    val nextLesson = unlockedLessons.firstOrNull { !it.isCompleted }
    if (nextLesson != null) {
      list.add(
        LearningRecommendation(
          id = "rec_lesson_${nextLesson.id}",
          type = RecommendationType.NEXT_LESSON,
          title = nextLesson.title,
          subtitle = "Next in Adventure Map",
          reason = "Continue your main quest line and unlock new worlds.",
          targetId = nextLesson.id,
          xpReward = nextLesson.xpReward,
          difficulty = "Normal",
          iconName = "play"
        )
      )
    }

    // 4. Project Lab Recommendation (If user completed at least 3 lessons)
    if (completedLessons.size >= 3 && projects.isNotEmpty()) {
      val firstProject = projects.firstOrNull { !it.isCompleted } ?: projects.first()
      list.add(
        LearningRecommendation(
          id = "rec_project_${firstProject.id}",
          type = RecommendationType.START_PROJECT,
          title = "Build: ${firstProject.title}",
          subtitle = "Multi-file Project Lab",
          reason = "You have solid foundational concepts. Build a real multi-file software application!",
          targetId = firstProject.id,
          xpReward = firstProject.xpReward,
          difficulty = firstProject.difficulty,
          iconName = "terminal"
        )
      )
    }

    // 5. Featured Coding Challenge
    val uncompletedChallenge = challenges.firstOrNull { !it.isCompleted }
    if (uncompletedChallenge != null && list.size < 4) {
      list.add(
        LearningRecommendation(
          id = "rec_challenge_${uncompletedChallenge.id}",
          type = RecommendationType.TRY_CHALLENGE,
          title = uncompletedChallenge.title,
          subtitle = "${uncompletedChallenge.difficulty} • ${uncompletedChallenge.category}",
          reason = "Sharpen your algorithmic thinking and test your solution with live automated test suites.",
          targetId = uncompletedChallenge.id,
          xpReward = uncompletedChallenge.xpReward,
          difficulty = uncompletedChallenge.difficulty,
          iconName = "code"
        )
      )
    }

    return list.take(4)
  }
}
