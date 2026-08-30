package com.example.data.repository

import com.example.data.local.CodeQuestDatabase
import com.example.data.local.ProjectDao
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.ProjectProgressEntity
import com.example.data.models.QuestType
import com.example.domain.execution.ExecutionOptions
import com.example.domain.execution.ExecutionResult
import com.example.domain.execution.ProjectRunner
import com.example.domain.execution.ProjectTestSuiteResult
import com.example.domain.services.LevelUpResult
import com.example.domain.services.StreakUpdateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray

data class ProjectSubmissionResult(
  val isCompleted: Boolean,
  val allTestsPassed: Boolean,
  val testSuiteResult: ProjectTestSuiteResult,
  val xpEarned: Int = 0,
  val coinsEarned: Int = 0,
  val badgeEarned: String? = null,
  val levelUpResult: LevelUpResult? = null,
  val streakResult: StreakUpdateResult? = null,
  val completedTasksCount: Int = 0,
  val totalTasksCount: Int = 0
)

class ProjectRepository(
  private val database: CodeQuestDatabase,
  private val questRepository: CodeQuestRepository,
  private val projectRunner: ProjectRunner = ProjectRunner()
) {
  private val projectDao: ProjectDao = database.projectDao()

  fun getProjectsForLanguage(language: String): Flow<List<ProjectEntity>> =
    projectDao.getProjectsForLanguage(language)

  fun getAllProjects(): Flow<List<ProjectEntity>> =
    projectDao.getAllProjects()

  suspend fun createCustomProject(
    title: String,
    description: String,
    language: String,
    template: String,
    difficulty: String,
    readmeContent: String
  ): ProjectEntity {
    val projectId = "custom_project_${System.currentTimeMillis()}"
    val starterFiles = mutableMapOf<String, String>()
    
    when (template) {
      "BLANK" -> {
        starterFiles["main.py"] = "# main.py\n\nprint('Hello, Custom Project!')\n"
      }
      "CLI" -> {
        starterFiles["main.py"] = "import sys\n\ndef main():\n    print('CLI Application Started')\n    print(f'Arguments: {sys.argv}')\n\nif __name__ == '__main__':\n    main()\n"
        starterFiles["utils.py"] = "def helper():\n    return 'Helper function'\n"
      }
      "PACKAGE" -> {
        starterFiles["main.py"] = "from my_package import core\n\nprint(core.run())\n"
        starterFiles["my_package/core.py"] = "def run():\n    return 'Package running'\n"
        starterFiles["my_package/__init__.py"] = ""
      }
      "TESTING" -> {
        starterFiles["main.py"] = "def add(a, b):\n    return a + b\n"
        starterFiles["test_main.py"] = "from main import add\n\ndef test_add():\n    assert add(2, 2) == 4\n    print('All tests passed')\n\nif __name__ == '__main__':\n    test_add()\n"
      }
      "AUTOMATION" -> {
        starterFiles["main.py"] = "import os\n\ndef process_files():\n    print('Processing...')\n\nif __name__ == '__main__':\n    process_files()\n"
      }
      "DATA" -> {
        starterFiles["main.py"] = "import json\n\ndef analyze():\n    data = {'key': 'value'}\n    print(json.dumps(data, indent=2))\n\nif __name__ == '__main__':\n    analyze()\n"
      }
      "GAME" -> {
        starterFiles["main.py"] = "def game_loop():\n    print('Game Over')\n\nif __name__ == '__main__':\n    game_loop()\n"
      }
      else -> {
        starterFiles["main.py"] = "# main.py\n"
      }
    }
    starterFiles["README.md"] = readmeContent

    val project = ProjectEntity(
      id = projectId,
      title = title,
      language = language,
      difficulty = difficulty,
      description = description,
      instructions = "",
      starterFilesJson = org.json.JSONObject(starterFiles as Map<*, *>).toString(),
      isCustom = true,
      projectType = template
    )
    
    projectDao.insertProject(project)
    
    // Initialize files and progress
    val fileEntities = starterFiles.map { (name, content) ->
      ProjectFileEntity(
        projectId = project.id,
        fileName = name,
        fileContent = content,
        isMain = name == "main.py" || name == "index.js",
        isReadOnly = false,
        lastModifiedEpochMs = System.currentTimeMillis()
      )
    }
    projectDao.insertFiles(fileEntities)
          
    val initialProgress = ProjectProgressEntity(
      projectId = project.id,
      activeFileName = if (starterFiles.containsKey("main.py")) "main.py" else starterFiles.keys.firstOrNull() ?: "main.py",
      completedTaskIdsJson = "[]",
      isCompleted = project.isCompleted,
      attemptsCount = 0,
      hintsUsedCount = 0,
      lastUpdatedEpochMs = System.currentTimeMillis()
    )
    projectDao.saveProjectProgress(initialProgress)
    
    return project
  }

  suspend fun getProjectById(projectId: String): ProjectEntity? =
    projectDao.getProjectById(projectId)

  fun observeProjectById(projectId: String): Flow<ProjectEntity?> =
    projectDao.observeProjectById(projectId)

  suspend fun toggleProjectPortfolio(projectId: String, isPortfolio: Boolean) {
    projectDao.setProjectPortfolio(projectId, isPortfolio)
  }

  fun getFilesForProject(projectId: String): Flow<List<ProjectFileEntity>> =
    projectDao.getFilesForProject(projectId)

  fun getProjectProgress(projectId: String): Flow<ProjectProgressEntity?> =
    projectDao.observeProjectProgress(projectId)

  suspend fun ensureStarterFiles(project: ProjectEntity) {
    val existingFiles = projectDao.getFilesForProjectOnce(project.id)
    if (existingFiles.isEmpty()) {
      val starterFiles = project.parseStarterFiles()
      val fileEntities = starterFiles.map { (name, content) ->
        ProjectFileEntity(
          projectId = project.id,
          fileName = name,
          fileContent = content,
          isMain = name == "main.py" || name == "index.js",
          isReadOnly = name.endsWith(".md", ignoreCase = true),
          lastModifiedEpochMs = System.currentTimeMillis()
        )
      }
      projectDao.insertFiles(fileEntities)
      
      val initialProgress = ProjectProgressEntity(
        projectId = project.id,
        activeFileName = if (starterFiles.containsKey("main.py")) "main.py" else starterFiles.keys.firstOrNull() ?: "main.py",
        completedTaskIdsJson = "[]",
        isCompleted = project.isCompleted,
        attemptsCount = 0,
        hintsUsedCount = 0,
        lastUpdatedEpochMs = System.currentTimeMillis()
      )
      projectDao.saveProjectProgress(initialProgress)
    }
  }

  suspend fun saveFile(projectId: String, fileName: String, content: String) {
    val existing = projectDao.getFile(projectId, fileName)
    val entity = existing?.copy(
      fileContent = content,
      lastModifiedEpochMs = System.currentTimeMillis()
    ) ?: ProjectFileEntity(
      projectId = projectId,
      fileName = fileName,
      fileContent = content,
      isMain = fileName == "main.py",
      isReadOnly = fileName.endsWith(".md", ignoreCase = true),
      lastModifiedEpochMs = System.currentTimeMillis()
    )
    projectDao.insertOrUpdateFile(entity)
  }

  suspend fun createFile(projectId: String, fileName: String, initialContent: String = ""): Boolean {
    val trimmedName = fileName.trim()
    if (trimmedName.isBlank() || trimmedName.contains("/") || trimmedName.contains("\\")) {
      return false
    }
    val existing = projectDao.getFile(projectId, trimmedName)
    if (existing != null) {
      return false
    }
    val newFile = ProjectFileEntity(
      projectId = projectId,
      fileName = trimmedName,
      fileContent = initialContent,
      isMain = false,
      isReadOnly = trimmedName.endsWith(".md", ignoreCase = true),
      lastModifiedEpochMs = System.currentTimeMillis()
    )
    projectDao.insertOrUpdateFile(newFile)
    return true
  }

  suspend fun renameFile(projectId: String, oldName: String, newName: String): Boolean {
    val trimmedNew = newName.trim()
    if (trimmedNew.isBlank() || oldName == "main.py" || trimmedNew == "main.py" || trimmedNew.contains("/") || trimmedNew.contains("\\")) {
      return false
    }
    val existingOld = projectDao.getFile(projectId, oldName) ?: return false
    val existingNew = projectDao.getFile(projectId, trimmedNew)
    if (existingNew != null) return false

    projectDao.deleteFile(projectId, oldName)
    projectDao.insertOrUpdateFile(
      existingOld.copy(
        fileName = trimmedNew,
        isReadOnly = trimmedNew.endsWith(".md", ignoreCase = true),
        lastModifiedEpochMs = System.currentTimeMillis()
      )
    )
    return true
  }

  suspend fun deleteFile(projectId: String, fileName: String): Boolean {
    if (fileName == "main.py") return false // Protected entry point
    projectDao.deleteFile(projectId, fileName)
    return true
  }

  suspend fun commitProjectVersion(projectId: String, description: String) {
    val filesList = projectDao.getFilesForProjectOnce(projectId)
    val filesMap = filesList.associate { it.fileName to it.fileContent }
    val latestVersion = projectDao.getLatestVersionNumber(projectId) ?: 0
    
    val version = com.example.data.models.ProjectVersionEntity(
      projectId = projectId,
      versionNumber = latestVersion + 1,
      timestampEpochMs = System.currentTimeMillis(),
      description = description,
      filesJson = org.json.JSONObject(filesMap as Map<*, *>).toString()
    )
    projectDao.insertProjectVersion(version)
  }

  suspend fun getProjectVersions(projectId: String): List<com.example.data.models.ProjectVersionEntity> {
    return projectDao.getProjectVersions(projectId)
  }

  suspend fun restoreProjectVersion(projectId: String, versionNumber: Int) {
    val versions = projectDao.getProjectVersions(projectId)
    val targetVersion = versions.find { it.versionNumber == versionNumber } ?: return
    
    try {
      val obj = org.json.JSONObject(targetVersion.filesJson)
      val starterMap = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val k = keys.next()
        starterMap[k] = obj.getString(k)
      }
      projectDao.resetProjectToStarterFiles(projectId, starterMap)
    } catch (e: Exception) {
      // Ignored
    }
  }

  suspend fun resetSingleFile(projectId: String, fileName: String, starterContent: String) {
    saveFile(projectId, fileName, starterContent)
  }

  suspend fun resetProjectToStarterFiles(project: ProjectEntity) {
    val starterFiles = project.parseStarterFiles()
    projectDao.resetProjectToStarterFiles(project.id, starterFiles)
    val progress = ProjectProgressEntity(
      projectId = project.id,
      activeFileName = "main.py",
      completedTaskIdsJson = "[]",
      isCompleted = false,
      attemptsCount = 0,
      hintsUsedCount = 0,
      lastUpdatedEpochMs = System.currentTimeMillis()
    )
    projectDao.saveProjectProgress(progress)
  }

  suspend fun updateActiveFile(projectId: String, fileName: String) {
    val current = projectDao.getProjectProgress(projectId) ?: ProjectProgressEntity(projectId = projectId)
    projectDao.saveProjectProgress(current.copy(activeFileName = fileName, lastUpdatedEpochMs = System.currentTimeMillis()))
  }

  suspend fun executeProject(
    projectId: String,
    entryFileName: String = "main.py",
    rawInput: String = "",
    options: ExecutionOptions = ExecutionOptions()
  ): ExecutionResult {
    val filesList = projectDao.getFilesForProjectOnce(projectId)
    val filesMap = filesList.associate { it.fileName to it.fileContent }
    val entryContent = filesMap[entryFileName] ?: filesMap["main.py"] ?: filesList.firstOrNull()?.fileContent ?: ""

    return projectRunner.executeProject(
      mainFileContent = entryContent,
      workspaceFiles = filesMap,
      rawInput = rawInput,
      options = options
    )
  }

  suspend fun runProjectTests(
    project: ProjectEntity,
    options: ExecutionOptions = ExecutionOptions()
  ): ProjectTestSuiteResult {
    val filesList = projectDao.getFilesForProjectOnce(project.id)
    val filesMap = filesList.associate { it.fileName to it.fileContent }

    val suiteResult = projectRunner.runProjectTests(
      project = project,
      workspaceFiles = filesMap,
      options = options
    )

    // Update progress tasks
    val currentProgress = projectDao.getProjectProgress(project.id) ?: ProjectProgressEntity(projectId = project.id)
    val completedTaskIds = suiteResult.taskPassedMap.filter { it.value }.keys.toList()
    val updatedProgress = currentProgress.copy(
      completedTaskIdsJson = JSONArray(completedTaskIds).toString(),
      attemptsCount = currentProgress.attemptsCount + 1,
      lastUpdatedEpochMs = System.currentTimeMillis()
    )
    projectDao.saveProjectProgress(updatedProgress)

    return suiteResult
  }

  suspend fun submitProject(project: ProjectEntity): ProjectSubmissionResult {
    val suiteResult = runProjectTests(project)
    val tasks = project.parseTasks()
    val completedTasksCount = suiteResult.taskPassedMap.values.count { it }

    if (suiteResult.allPassed) {
      val wasAlreadyCompleted = project.isCompleted
      if (!wasAlreadyCompleted) {
        projectDao.markProjectCompleted(project.id)

        // Award rewards
        val levelUp = questRepository.addRewards(project.xpReward, project.coinReward)

        // Record streak
        val user = database.userDao().getUserProfileOnce()
        val streakResult = if (user != null) {
          val res = questRepository.streakService.recordActivity(
            currentStreak = user.streakDays,
            longestStreak = user.longestStreak,
            lastActiveEpochDay = user.lastActiveEpochDay
          )
          database.userDao().updateStreak(res.newStreak, res.newLongestStreak, res.todayEpochDay)
          res
        } else null

        // Quests & achievements
        database.gamificationDao().incrementQuestProgress(QuestType.CHALLENGES_SOLVED.name, 1)
        database.gamificationDao().updateAchievementProgress("PROJECT", 1)
        database.gamificationDao().updateAchievementProgress("COMPLETION", 1)

        // Update skill masteries associated with project
        val skills = project.parseSkills()
        skills.forEach { skill ->
          val skillId = "py_${skill.lowercase().replace(" ", "_").replace("/", "_")}"
          database.gamificationDao().recordSkillAttempt(skillId, 1)
        }

        return ProjectSubmissionResult(
          isCompleted = true,
          allTestsPassed = true,
          testSuiteResult = suiteResult,
          xpEarned = project.xpReward,
          coinsEarned = project.coinReward,
          badgeEarned = project.badgeName,
          levelUpResult = levelUp,
          streakResult = streakResult,
          completedTasksCount = tasks.size,
          totalTasksCount = tasks.size
        )
      } else {
        return ProjectSubmissionResult(
          isCompleted = true,
          allTestsPassed = true,
          testSuiteResult = suiteResult,
          xpEarned = 0,
          coinsEarned = 0,
          badgeEarned = null,
          completedTasksCount = tasks.size,
          totalTasksCount = tasks.size
        )
      }
    } else {
      return ProjectSubmissionResult(
        isCompleted = false,
        allTestsPassed = false,
        testSuiteResult = suiteResult,
        completedTasksCount = completedTasksCount,
        totalTasksCount = tasks.size
      )
    }
  }
}
