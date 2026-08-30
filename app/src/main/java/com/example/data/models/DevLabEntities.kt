package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

// ----------------------------------------------------
// Bug Hunt & Debugging Workflow Models
// ----------------------------------------------------

enum class BugType {
  SYNTAX_ERROR,
  LOGIC_ERROR,
  RUNTIME_ERROR,
  INPUT_VALIDATION,
  OFF_BY_ONE,
  INCORRECT_CONDITION,
  DATA_HANDLING,
  MISSING_RETURN,
  WRONG_ARGUMENTS,
  FILE_HANDLING
}

enum class DebugStep(val stepNumber: Int, val title: String, val description: String) {
  RUN_PROGRAM(1, "Run Program", "Execute the code to reproduce the failure."),
  READ_ERROR(2, "Read Error", "Analyze the error message and stack trace carefully."),
  INSPECT_CODE(3, "Inspect Code", "Trace where the unexpected value or exception occurs."),
  FORM_HYPOTHESIS(4, "Form Hypothesis", "Identify the likely root cause before editing."),
  FIX_CODE(5, "Fix Code", "Apply targeted minimal corrections to resolve the issue."),
  RUN_TESTS(6, "Run Tests", "Verify edge cases and ensure no regressions."),
  SUBMIT(7, "Submit", "Finalize and claim rewards.")
}

@Entity(tableName = "bug_hunts")
data class BugHuntEntity(
  @PrimaryKey val id: String,
  val title: String,
  val scenario: String,
  val language: String = "python",
  val difficulty: String = "BEGINNER", // BEGINNER, INTERMEDIATE, ADVANCED
  val bugType: BugType = BugType.LOGIC_ERROR,
  val filesJson: String, // Map<filename, content>
  val initialErrorOutput: String,
  val errorExplainerWhat: String,
  val errorExplainerWhy: String,
  val errorExplainerChecklistJson: String, // List<String>
  val hypothesisOptionsJson: String, // List<String>
  val correctHypothesisIndex: Int,
  val hint1: String, // Conceptual direction
  val hint2: String, // Specific area
  val hint3: String, // Explicit guidance
  val solutionCode: String,
  val testsJson: String, // List<ProjectTest>
  val xpReward: Int = 120,
  val coinReward: Int = 30,
  val isCompleted: Boolean = false
) {
  fun parseFiles(): Map<String, String> {
    return try {
      val obj = JSONObject(filesJson)
      val map = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val k = keys.next()
        map[k] = obj.getString(k)
      }
      map
    } catch (e: Exception) {
      emptyMap()
    }
  }

  fun parseChecklist(): List<String> {
    return try {
      val arr = JSONArray(errorExplainerChecklistJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseHypothesisOptions(): List<String> {
    return try {
      val arr = JSONArray(hypothesisOptionsJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseTests(): List<ProjectTest> {
    return try {
      val arr = JSONArray(testsJson)
      List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        ProjectTest(
          id = obj.getString("id"),
          title = obj.getString("title"),
          input = obj.optString("input", ""),
          expectedOutput = obj.optString("expectedOutput", ""),
          comparisonMode = obj.optString("comparisonMode", "CONTAINS"),
          isHidden = obj.optBoolean("isHidden", false)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

// ----------------------------------------------------
// Test-Driven Challenges (Test First)
// ----------------------------------------------------

@Entity(tableName = "test_first_challenges")
data class TestFirstChallengeEntity(
  @PrimaryKey val id: String,
  val title: String,
  val requirementDescription: String,
  val language: String = "python",
  val difficulty: String = "BEGINNER",
  val starterFilesJson: String, // Map<filename, content>
  val requirementsChecklistJson: String, // List<String>
  val acceptanceCriteria: String,
  val testsJson: String, // List<ProjectTest>
  val estimatedCoveragePercent: Int = 100,
  val xpReward: Int = 150,
  val coinReward: Int = 35,
  val isCompleted: Boolean = false
) {
  fun parseFiles(): Map<String, String> {
    return try {
      val obj = JSONObject(starterFilesJson)
      val map = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val k = keys.next()
        map[k] = obj.getString(k)
      }
      map
    } catch (e: Exception) {
      emptyMap()
    }
  }

  fun parseRequirements(): List<String> {
    return try {
      val arr = JSONArray(requirementsChecklistJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseTests(): List<ProjectTest> {
    return try {
      val arr = JSONArray(testsJson)
      List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        ProjectTest(
          id = obj.getString("id"),
          title = obj.getString("title"),
          input = obj.optString("input", ""),
          expectedOutput = obj.optString("expectedOutput", ""),
          comparisonMode = obj.optString("comparisonMode", "CONTAINS"),
          isHidden = obj.optBoolean("isHidden", false)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

// ----------------------------------------------------
// Git Simulator & Curriculum Models
// ----------------------------------------------------

data class GitCommit(
  val hash: String,
  val message: String,
  val author: String = "Alex Dev <alex@codequest.dev>",
  val timestamp: String = "Just now",
  val changedFiles: List<String> = emptyList(),
  val parentHash: String? = null
)

@Entity(tableName = "git_exercises")
data class GitExerciseEntity(
  @PrimaryKey val id: String,
  val lessonNumber: Int,
  val title: String,
  val concept: String,
  val description: String,
  val taskPrompt: String,
  val initialWorkingFilesJson: String, // List<String>
  val initialStagedFilesJson: String, // List<String>
  val initialBranch: String = "main",
  val branchesJson: String = "[\"main\"]",
  val initialCommitsJson: String = "[]",
  val expectedAction: String, // STAGE, COMMIT, BRANCH, MERGE, RESOLVE_CONFLICT
  val commitMessageOptionsJson: String = "[]", // for commit training
  val bestCommitMessageIndex: Int = 0,
  val conflictFileContent: String = "",
  val resolvedFileContent: String = "",
  val xpReward: Int = 80,
  val coinReward: Int = 20,
  val isCompleted: Boolean = false
) {
  fun parseWorkingFiles(): List<String> {
    return try {
      val arr = JSONArray(initialWorkingFilesJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseStagedFiles(): List<String> {
    return try {
      val arr = JSONArray(initialStagedFilesJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseBranches(): List<String> {
    return try {
      val arr = JSONArray(branchesJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      listOf("main")
    }
  }

  fun parseCommitMessageOptions(): List<String> {
    return try {
      val arr = JSONArray(commitMessageOptionsJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

// ----------------------------------------------------
// Code Review & Refactoring Models
// ----------------------------------------------------

@Entity(tableName = "code_reviews")
data class CodeReviewEntity(
  @PrimaryKey val id: String,
  val title: String,
  val language: String = "python",
  val snippet: String,
  val isRefactorChallenge: Boolean = false,
  val description: String,
  val issuesOptionsJson: String, // List<String>
  val correctIssueIndex: Int,
  val explanation: String,
  val refactorStarterCode: String = "",
  val refactorTestsJson: String = "[]",
  val xpReward: Int = 100,
  val coinReward: Int = 25,
  val isCompleted: Boolean = false
) {
  fun parseIssues(): List<String> {
    return try {
      val arr = JSONArray(issuesOptionsJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseTests(): List<ProjectTest> {
    return try {
      val arr = JSONArray(refactorTestsJson)
      List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        ProjectTest(
          id = obj.getString("id"),
          title = obj.getString("title"),
          input = obj.optString("input", ""),
          expectedOutput = obj.optString("expectedOutput", ""),
          comparisonMode = obj.optString("comparisonMode", "CONTAINS"),
          isHidden = obj.optBoolean("isHidden", false)
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

// ----------------------------------------------------
// Project Issues & Task Board
// ----------------------------------------------------

enum class IssueStatus {
  TODO,
  IN_PROGRESS,
  DONE
}

@Entity(tableName = "project_issues")
data class ProjectIssueEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val issueNumber: Int,
  val title: String,
  val description: String,
  val difficulty: String = "BEGINNER",
  val skillsJson: String = "[]",
  val status: IssueStatus = IssueStatus.TODO,
  val affectedFile: String = "main.py",
  val testId: String = "",
  val xpReward: Int = 50
) {
  fun parseSkills(): List<String> {
    return try {
      val arr = JSONArray(skillsJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }
}

// ----------------------------------------------------
// Portfolio & Export Models
// ----------------------------------------------------

@Entity(tableName = "portfolio_items")
data class PortfolioItemEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val title: String,
  val language: String,
  val description: String,
  val skillsJson: String,
  val completedDate: String,
  val isPublic: Boolean = false,
  val readmeContent: String = "",
  val testSummary: String = "All tests passing",
  val badgeName: String = "Project Master",
  val badgeIcon: String = "military_tech",
  val sourceCodeSnapshotJson: String = "{}"
) {
  fun parseSkills(): List<String> {
    return try {
      val arr = JSONArray(skillsJson)
      List(arr.length()) { arr.getString(it) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun parseFiles(): Map<String, String> {
    return try {
      val obj = JSONObject(sourceCodeSnapshotJson)
      val map = mutableMapOf<String, String>()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val k = keys.next()
        map[k] = obj.getString(k)
      }
      map
    } catch (e: Exception) {
      emptyMap()
    }
  }
}

// ----------------------------------------------------
// Project Version History & Unsaved Recovery
// ----------------------------------------------------

@Entity(
  tableName = "project_versions",
  primaryKeys = ["projectId", "versionNumber"]
)
data class ProjectVersionEntity(
  val projectId: String,
  val versionNumber: Int,
  val timestampEpochMs: Long = System.currentTimeMillis(),
  val description: String = "Autosave snapshot",
  val filesJson: String // Map<filename, content>
)

@Entity(tableName = "unsaved_recovery")
data class UnsavedRecoveryEntity(
  @PrimaryKey val projectId: String,
  val filesJson: String,
  val savedTimestampEpochMs: Long = System.currentTimeMillis()
)

// ----------------------------------------------------
// Developer Profile Stats
// ----------------------------------------------------

@Entity(tableName = "developer_stats")
data class DeveloperStatsEntity(
  @PrimaryKey val userId: Long = 1L,
  val bugsFixedCount: Int = 0,
  val testsPassedCount: Int = 0,
  val gitExercisesCompleted: Int = 0,
  val commitsCreatedCount: Int = 0,
  val branchesCreatedCount: Int = 0,
  val conflictsResolvedCount: Int = 0,
  val codeReviewsCompleted: Int = 0,
  val refactorsCompleted: Int = 0,
  val realWorldProjectsCompleted: Int = 0,
  val readmeScore: Int = 0
)
