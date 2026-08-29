package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class ProjectDifficulty {
  BEGINNER,
  INTERMEDIATE,
  ADVANCED
}

data class ProjectTask(
  val id: String,
  val title: String,
  val description: String,
  val checkpoint: Int = 1,
  val xpReward: Int = 25,
  val hint: String = "",
  val testIds: List<String> = emptyList(),
  val isCompleted: Boolean = false
)

data class ProjectTest(
  val id: String,
  val taskId: String? = null,
  val title: String,
  val input: String = "",
  val expectedOutput: String = "",
  val comparisonMode: String = "CONTAINS", // EXACT, TRIMMED, CONTAINS, NUMERIC
  val isHidden: Boolean = false,
  val functionTarget: String? = null,
  val expectedReturn: String? = null,
  val timeoutMs: Long = 2500L
)

data class ProjectHint(
  val id: String,
  val taskId: String? = null,
  val level: Int = 1,
  val title: String,
  val content: String
)

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val title: String,
  val language: String = "python",
  val difficulty: String = "BEGINNER", // BEGINNER, INTERMEDIATE, ADVANCED
  val estimatedTime: String = "25 min",
  val skillsJson: String = "[]",
  val prerequisitesJson: String = "[]",
  val description: String,
  val instructions: String,
  val starterFilesJson: String, // JSON Map<filename, content>
  val tasksJson: String = "[]", // JSON List<ProjectTask>
  val testsJson: String = "[]", // JSON List<ProjectTest>
  val hintsJson: String = "[]", // JSON List<ProjectHint>
  val xpReward: Int = 250,
  val coinReward: Int = 50,
  val badgeName: String = "Project Builder",
  val badgeIcon: String = "code",
  val completionCriteria: String = "Pass all project requirements and test cases",
  val isUnlocked: Boolean = true,
  val isCompleted: Boolean = false,
  val completedAtEpochMs: Long = 0L
) {
  fun parseSkills(): List<String> {
    return try {
      val array = JSONArray(skillsJson)
      List(array.length()) { array.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parsePrerequisites(): List<String> {
    return try {
      val array = JSONArray(prerequisitesJson)
      List(array.length()) { array.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseStarterFiles(): Map<String, String> {
    return try {
      val obj = JSONObject(starterFilesJson)
      val map = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        map[key] = obj.getString(key)
      }
      map
    } catch (e: Exception) {
      emptyMap()
    }
  }

  fun parseTasks(): List<ProjectTask> {
    return try {
      val array = JSONArray(tasksJson)
      List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        val testIdsArr = obj.optJSONArray("testIds")
        val testIds = if (testIdsArr != null) {
          List(testIdsArr.length()) { testIdsArr.getString(it) }
        } else emptyList()

        ProjectTask(
          id = obj.getString("id"),
          title = obj.getString("title"),
          description = obj.getString("description"),
          checkpoint = obj.optInt("checkpoint", 1),
          xpReward = obj.optInt("xpReward", 25),
          hint = obj.optString("hint", ""),
          testIds = testIds,
          isCompleted = obj.optBoolean("isCompleted", false)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseTests(): List<ProjectTest> {
    return try {
      val array = JSONArray(testsJson)
      List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        ProjectTest(
          id = obj.getString("id"),
          taskId = obj.optString("taskId").takeIf { it.isNotBlank() },
          title = obj.getString("title"),
          input = obj.optString("input", ""),
          expectedOutput = obj.optString("expectedOutput", ""),
          comparisonMode = obj.optString("comparisonMode", "CONTAINS"),
          isHidden = obj.optBoolean("isHidden", false),
          functionTarget = obj.optString("functionTarget").takeIf { it.isNotBlank() },
          expectedReturn = obj.optString("expectedReturn").takeIf { it.isNotBlank() },
          timeoutMs = obj.optLong("timeoutMs", 2500L)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseHints(): List<ProjectHint> {
    return try {
      val array = JSONArray(hintsJson)
      List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        ProjectHint(
          id = obj.getString("id"),
          taskId = obj.optString("taskId").takeIf { it.isNotBlank() },
          level = obj.optInt("level", 1),
          title = obj.getString("title"),
          content = obj.getString("content")
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

@Entity(
  tableName = "project_files",
  primaryKeys = ["projectId", "fileName"]
)
data class ProjectFileEntity(
  val projectId: String,
  val fileName: String,
  val fileContent: String,
  val isMain: Boolean = false,
  val isReadOnly: Boolean = false,
  val lastModifiedEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_progress")
data class ProjectProgressEntity(
  @PrimaryKey val projectId: String,
  val activeFileName: String = "main.py",
  val completedTaskIdsJson: String = "[]",
  val isCompleted: Boolean = false,
  val attemptsCount: Int = 0,
  val hintsUsedCount: Int = 0,
  val timeSpentSeconds: Long = 0,
  val lastUpdatedEpochMs: Long = System.currentTimeMillis()
) {
  fun parseCompletedTaskIds(): List<String> {
    return try {
      val array = JSONArray(completedTaskIdsJson)
      List(array.length()) { array.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }
}
