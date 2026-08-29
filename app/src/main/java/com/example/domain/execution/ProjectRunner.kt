package com.example.domain.execution

import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectTask
import com.example.data.models.ProjectTest
import kotlin.math.abs

class ProjectRunner(
  private val sandboxEngine: SafePythonSandboxEngine = SafePythonSandboxEngine()
) {

  fun executeProject(
    mainFileContent: String,
    workspaceFiles: Map<String, String>,
    rawInput: String = "",
    options: ExecutionOptions = ExecutionOptions()
  ): ExecutionResult {
    return sandboxEngine.execute(
      code = mainFileContent,
      rawInput = rawInput,
      options = options,
      workspaceFiles = workspaceFiles
    )
  }

  fun runProjectTests(
    project: ProjectEntity,
    workspaceFiles: Map<String, String>,
    options: ExecutionOptions = ExecutionOptions()
  ): ProjectTestSuiteResult {
    val startTime = System.currentTimeMillis()
    val tests = project.parseTests()
    val tasks = project.parseTasks()
    val mainCode = workspaceFiles["main.py"] ?: workspaceFiles.values.firstOrNull() ?: ""

    val results = mutableListOf<ProjectTestResult>()
    var passedCount = 0
    var firstFailureSummary: String? = null

    for (test in tests) {
      val testOptions = options.copy(timeoutMs = test.timeoutMs)
      val execResult = sandboxEngine.execute(
        code = mainCode,
        rawInput = test.input,
        options = testOptions,
        workspaceFiles = workspaceFiles
      )

      val passed: Boolean
      val failureReason: String?

      if (execResult.hasError) {
        passed = false
        failureReason = execResult.errorMessage ?: execResult.stderr.ifBlank { "Execution error" }
      } else {
        val actual = execResult.stdout
        val expected = test.expectedOutput
        val compPassed = compareOutputs(actual, expected, test.comparisonMode)

        if (compPassed) {
          passed = true
          failureReason = null
        } else {
          passed = false
          failureReason = if (test.isHidden) {
            "Hidden test verification failed."
          } else {
            "Expected output containing '${expected.trim()}' but got '${actual.trim()}'"
          }
        }
      }

      if (passed) {
        passedCount++
      } else if (firstFailureSummary == null) {
        firstFailureSummary = failureReason
      }

      val displayInput = if (test.isHidden) "[Hidden Test Input]" else test.input
      val displayExpected = if (test.isHidden) "[Hidden Expected Output]" else test.expectedOutput
      val displayActual = if (test.isHidden && !passed) "[Hidden Test Output]" else execResult.stdout

      results.add(
        ProjectTestResult(
          testId = test.id,
          taskId = test.taskId,
          title = test.title,
          input = displayInput,
          expectedOutput = displayExpected,
          actualOutput = displayActual,
          passed = passed,
          isHidden = test.isHidden,
          failureReason = failureReason,
          executionTimeMs = execResult.executionTimeMs,
          errorType = execResult.errorType
        )
      )
    }

    // Determine task completion based on tests associated with each task
    val taskPassedMap = mutableMapOf<String, Boolean>()
    for (task in tasks) {
      if (task.testIds.isEmpty()) {
        // If task has no specific test IDs, check tests where test.taskId == task.id
        val taskTests = results.filter { it.taskId == task.id }
        taskPassedMap[task.id] = taskTests.isNotEmpty() && taskTests.all { it.passed }
      } else {
        val relevantResults = results.filter { task.testIds.contains(it.testId) }
        taskPassedMap[task.id] = relevantResults.isNotEmpty() && relevantResults.all { it.passed }
      }
    }

    val totalTime = System.currentTimeMillis() - startTime
    val allPassed = passedCount == tests.size && tests.isNotEmpty()

    return ProjectTestSuiteResult(
      allPassed = allPassed,
      passedCount = passedCount,
      totalCount = tests.size,
      taskPassedMap = taskPassedMap,
      results = results,
      totalExecutionTimeMs = totalTime,
      failureSummary = firstFailureSummary
    )
  }

  private fun compareOutputs(actual: String, expected: String, mode: String): Boolean {
    val cleanActual = actual.trim()
    val cleanExpected = expected.trim()

    return when (mode.uppercase()) {
      "EXACT" -> actual == expected
      "TRIMMED" -> cleanActual == cleanExpected
      "CONTAINS" -> cleanActual.contains(cleanExpected, ignoreCase = true)
      "NUMERIC" -> {
        val actNum = cleanActual.filter { it.isDigit() || it == '.' || it == '-' }.toDoubleOrNull()
        val expNum = cleanExpected.filter { it.isDigit() || it == '.' || it == '-' }.toDoubleOrNull()
        if (actNum != null && expNum != null) {
          abs(actNum - expNum) < 1e-4
        } else {
          cleanActual.contains(cleanExpected, ignoreCase = true)
        }
      }
      else -> cleanActual.contains(cleanExpected, ignoreCase = true)
    }
  }
}
