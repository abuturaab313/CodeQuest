package com.example.data.local

import com.example.data.models.AchievementEntity
import com.example.data.models.ChapterEntity
import com.example.data.models.CourseEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.LessonEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.QuestType
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.UserEntity
import com.example.data.models.WorldEntity

object InitialData {
  fun defaultUser(): UserEntity = UserEntity(
    id = 1,
    username = "Alex Dev",
    email = "",
    isGuest = true,
    avatarId = "robot_explorer",
    experienceLevel = "BEGINNER",
    selectedLanguage = "python",
    xp = 0,
    level = 1,
    currentHearts = 5,
    maxHearts = 5,
    lastHeartRegenEpochMs = System.currentTimeMillis(),
    streakDays = 1,
    longestStreak = 1,
    lastActiveEpochDay = java.time.LocalDate.now().toEpochDay(),
    coins = 100,
    gems = 15,
    soundEnabled = true,
    hapticsEnabled = true,
    darkMode = true,
    hasCompletedOnboarding = true,
    dailyGoalMinutes = 15
  )

  fun defaultCourses(): List<CourseEntity> = PythonCurriculum.getCourses()

  fun defaultWorlds(): List<WorldEntity> = PythonCurriculum.getWorlds()

  fun defaultChapters(): List<ChapterEntity> = PythonCurriculum.getChapters()

  fun defaultLessons(): List<LessonEntity> = PythonCurriculum.getLessons()

  fun defaultExercises(): List<ExerciseEntity> = PythonCurriculum.getExercises()

  fun defaultDailyQuests(): List<DailyQuestEntity> {
    val epochDay = java.time.LocalDate.now().toEpochDay()
    return listOf(
      DailyQuestEntity("quest_1_${epochDay}", "Code Scholar", "Complete 2 lessons or challenges", QuestType.LESSONS_COMPLETED, 2, 0, false, false, 60, 20, epochDay),
      DailyQuestEntity("quest_2_${epochDay}", "XP Surge", "Earn 80 XP from learning milestones", QuestType.XP_EARNED, 80, 0, false, false, 75, 25, epochDay),
      DailyQuestEntity("quest_3_${epochDay}", "Bug Hunter", "Solve 2 coding challenges or tests", QuestType.CHALLENGES_SOLVED, 2, 0, false, false, 90, 30, epochDay)
    )
  }

  fun defaultAchievements(): List<AchievementEntity> = listOf(
    AchievementEntity("ach_first_code", "First Code", "Complete your first coding exercise.", "terminal", "LEARNING", 1, 0, false, 0, 50, 20),
    AchievementEntity("ach_first_project", "First Project", "Complete your first project.", "stars", "PROJECTS", 1, 0, false, 0, 150, 30),
    AchievementEntity("ach_bug_hunter", "Bug Hunter", "Fix 10 debugging challenges.", "bug_report", "DEBUGGING", 10, 0, false, 0, 200, 50),
    AchievementEntity("ach_streak_starter", "Streak Starter", "Reach a 7-day coding streak.", "local_fire_department", "STREAK", 7, 0, false, 0, 100, 25),
    AchievementEntity("ach_streak_master", "Streak Master", "Reach a 30-day coding streak.", "whatshot", "STREAK", 30, 0, false, 0, 500, 100),
    AchievementEntity("ach_perfect_ten", "Perfect Ten", "Complete 10 challenges with perfect solutions (0 hints used).", "check_circle", "CHALLENGES", 10, 0, false, 0, 300, 75),
    AchievementEntity("ach_python_beginner", "Python Beginner", "Complete Python World 1: Origin Conquered.", "military_tech", "LEARNING", 10, 0, false, 0, 200, 50),
    AchievementEntity("ach_python_explorer", "Python Explorer", "Complete Python World 2: Intermediate Quest.", "emoji_events", "LEARNING", 8, 0, false, 0, 400, 100),
    AchievementEntity("ach_code_marathon", "Code Marathon", "Earn 1,000 XP in a week.", "timer", "XP", 1000, 0, false, 0, 300, 75),
    AchievementEntity("ach_project_builder", "Project Builder", "Complete 5 projects in Project Lab.", "science", "PROJECTS", 5, 0, false, 0, 400, 100),
    AchievementEntity("ach_loop_master", "Loop Master", "Reach 90% mastery in Loops & Iteration.", "replay", "MASTERY", 90, 0, false, 0, 200, 50),
    AchievementEntity("ach_function_forge", "Function Forge", "Reach 90% mastery in Functions & Scope.", "code", "MASTERY", 90, 0, false, 0, 200, 50)
  )

  fun defaultSkills(): List<SkillMasteryEntity> = listOf(
    SkillMasteryEntity("py_syntax", "python", "Basic Syntax", 25, 4, 1),
    SkillMasteryEntity("py_variables", "python", "Variables & Types", 0, 0, 0),
    SkillMasteryEntity("py_conditionals", "python", "Conditions & Logic", 0, 0, 0),
    SkillMasteryEntity("py_loops", "python", "Loops & Iteration", 0, 0, 0),
    SkillMasteryEntity("py_functions", "python", "Functions & Scope", 0, 0, 0),
    SkillMasteryEntity("py_collections", "python", "Lists & Dicts", 0, 0, 0)
  )

  fun defaultProjects(): List<ProjectEntity> = ProjectCurriculum.getProjects()
}
