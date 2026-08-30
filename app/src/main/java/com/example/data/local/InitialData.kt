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

  fun defaultCourses(): List<CourseEntity> {
    val existing = PythonCurriculum.getCourses().toMutableList()
    val allCourses = listOf(
      JavaScriptCurriculum.getCourse(),
      JavaCurriculum.getCourse(),
      CCurriculum.getCourse(),
      CppCurriculum.getCourse()
    )
    for (c in allCourses) {
      val idx = existing.indexOfFirst { it.id == c.id }
      if (idx >= 0) {
        existing[idx] = c
      } else {
        existing.add(c)
      }
    }
    return existing
  }

  fun defaultWorlds(): List<WorldEntity> =
    PythonCurriculum.getWorlds() +
    JavaScriptCurriculum.getWorlds() +
    JavaCurriculum.getWorlds() +
    CCurriculum.getWorlds() +
    CppCurriculum.getWorlds()

  fun defaultChapters(): List<ChapterEntity> =
    PythonCurriculum.getChapters() +
    JavaScriptCurriculum.getChapters() +
    JavaCurriculum.getChapters() +
    CCurriculum.getChapters() +
    CppCurriculum.getChapters()

  fun defaultLessons(): List<LessonEntity> =
    PythonCurriculum.getLessons() +
    JavaScriptCurriculum.getLessons() +
    JavaCurriculum.getLessons() +
    CCurriculum.getLessons() +
    CppCurriculum.getLessons()

  fun defaultExercises(): List<ExerciseEntity> =
    PythonCurriculum.getExercises() +
    JavaScriptCurriculum.getExercises() +
    JavaCurriculum.getExercises() +
    CCurriculum.getExercises() +
    CppCurriculum.getExercises()

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
    AchievementEntity("ach_js_master", "JS Architect", "Conquer JavaScript DOM Guardian boss.", "javascript", "LEARNING", 5, 0, false, 0, 350, 80),
    AchievementEntity("ach_java_master", "Java OOP Titan", "Conquer Java Class Master boss.", "coffee", "LEARNING", 5, 0, false, 0, 400, 90),
    AchievementEntity("ach_c_sentinel", "Memory Sentinel", "Conquer C Memory Sentinel boss.", "memory", "LEARNING", 5, 0, false, 0, 450, 100),
    AchievementEntity("ach_polyglot", "Polyglot Legend", "Solve challenges across 3 different languages.", "translate", "POLYGLOT", 3, 0, false, 0, 600, 150),
    AchievementEntity("ach_code_marathon", "Code Marathon", "Earn 1,000 XP in a week.", "timer", "XP", 1000, 0, false, 0, 300, 75),
    AchievementEntity("ach_project_builder", "Project Builder", "Complete 5 projects in Project Lab.", "science", "PROJECTS", 5, 0, false, 0, 400, 100),
    AchievementEntity("ach_loop_master", "Loop Master", "Reach 90% mastery in Loops & Iteration.", "replay", "MASTERY", 90, 0, false, 0, 200, 50),
    AchievementEntity("ach_function_forge", "Function Forge", "Reach 90% mastery in Functions & Scope.", "code", "MASTERY", 90, 0, false, 0, 200, 50),
    // Milestone 11 Developer Milestones
    AchievementEntity("ach_dev_first_bug_fix", "First Bug Fix", "Diagnose and resolve your first Bug Hunt.", "bug_report", "DEVELOPER", 1, 0, false, 0, 100, 25),
    AchievementEntity("ach_dev_first_test", "First Test", "Pass all suites in a Test-First challenge.", "task_alt", "DEVELOPER", 1, 0, false, 0, 100, 25),
    AchievementEntity("ach_dev_first_commit", "First Commit", "Stage changes and create a descriptive commit in Git Lab.", "commit", "DEVELOPER", 1, 0, false, 0, 100, 25),
    AchievementEntity("ach_dev_first_branch", "First Branch", "Create an isolated feature branch.", "call_split", "DEVELOPER", 1, 0, false, 0, 100, 25),
    AchievementEntity("ach_dev_first_merge", "First Merge", "Integrate a feature branch into main.", "merge_type", "DEVELOPER", 1, 0, false, 0, 120, 30),
    AchievementEntity("ach_dev_first_conflict", "Conflict Resolver", "Resolve your first merge conflict cleanly.", "build", "DEVELOPER", 1, 0, false, 0, 150, 40),
    AchievementEntity("ach_dev_first_readme", "Documentation Master", "Build a high-quality README passing all quality checks.", "description", "DEVELOPER", 1, 0, false, 0, 120, 30),
    AchievementEntity("ach_dev_first_refactor", "Code Craftsman", "Refactor code while preserving 100% test compatibility.", "auto_fix_high", "DEVELOPER", 1, 0, false, 0, 150, 40),
    AchievementEntity("ach_dev_first_real_world", "Real-World Developer", "Complete a real-world multi-issue project.", "rocket_launch", "DEVELOPER", 1, 0, false, 0, 250, 60),
    AchievementEntity("ach_dev_first_portfolio", "Portfolio Creator", "Publish your first verified project showcase.", "work", "DEVELOPER", 1, 0, false, 0, 200, 50)
  )

  fun defaultSkills(): List<SkillMasteryEntity> = listOf(
    SkillMasteryEntity("py_syntax", "python", "Basic Syntax", 25, 4, 1),
    SkillMasteryEntity("py_variables", "python", "Variables & Types", 0, 0, 0),
    SkillMasteryEntity("py_conditionals", "python", "Conditions & Logic", 0, 0, 0),
    SkillMasteryEntity("py_loops", "python", "Loops & Iteration", 0, 0, 0),
    SkillMasteryEntity("py_functions", "python", "Functions & Scope", 0, 0, 0),
    SkillMasteryEntity("py_collections", "python", "Lists & Dicts", 0, 0, 0),
    SkillMasteryEntity("js_syntax", "javascript", "JS ES6 Syntax", 0, 0, 0),
    SkillMasteryEntity("js_dom", "javascript", "DOM & Events", 0, 0, 0),
    SkillMasteryEntity("java_oop", "java", "OOP & Classes", 0, 0, 0),
    SkillMasteryEntity("c_pointers", "c", "Pointers & Memory", 0, 0, 0),
    SkillMasteryEntity("cpp_classes", "cpp", "STL & Objects", 0, 0, 0)
  )

  fun defaultProjects(): List<ProjectEntity> = ProjectCurriculum.getProjects()
}
