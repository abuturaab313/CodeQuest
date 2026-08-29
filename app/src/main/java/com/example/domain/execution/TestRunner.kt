package com.example.domain.execution

import com.example.data.models.ChallengeTestCase
import com.example.data.models.ComparisonMode
import kotlin.math.abs

class TestRunner(
  private val executionEngine: SafePythonSandboxEngine = SafePythonSandboxEngine()
) {

  fun runTestSuite(
    languageId: String,
    code: String,
    testCases: List<ChallengeTestCase>,
    options: ExecutionOptions = ExecutionOptions()
  ): TestSuiteResult {
    val startTime = System.currentTimeMillis()
    val results = mutableListOf<ChallengeTestResult>()
    var passedCount = 0
    var failureSummary: String? = null

    for (test in testCases) {
      val testOptions = options.copy(timeoutMs = test.timeoutMs)
      val execResult = executionEngine.execute(code, test.input, testOptions)

      val passed: Boolean
      val failureReason: String?

      if (execResult.hasError) {
        passed = false
        failureReason = execResult.errorMessage ?: execResult.stderr.ifBlank { "Execution failed" }
      } else {
        val actual = execResult.stdout
        val expected = test.expectedOutput
        val comparisonPassed = compareOutputs(actual, expected, test.comparisonMode)

        if (comparisonPassed) {
          passed = true
          failureReason = null
        } else {
          passed = false
          failureReason = if (test.isHidden) {
            "Hidden test failed."
          } else {
            "Output mismatch: expected '${expected.trim()}' but got '${actual.trim()}'"
          }
        }
      }

      if (passed) {
        passedCount++
      } else if (failureSummary == null) {
        failureSummary = failureReason
      }

      // Safeguard hidden test info: do not expose hidden input or expected output to client
      val displayInput = if (test.isHidden) "[Hidden Test Input]" else test.input
      val displayExpected = if (test.isHidden) "[Hidden Expected Output]" else test.expectedOutput
      val displayActual = if (test.isHidden && !passed) "[Hidden Test Output]" else execResult.stdout

      results.add(
        ChallengeTestResult(
          testId = test.id,
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

    val totalTime = System.currentTimeMillis() - startTime
    return TestSuiteResult(
      allPassed = passedCount == testCases.size,
      passedCount = passedCount,
      totalCount = testCases.size,
      results = results,
      totalExecutionTimeMs = totalTime,
      failureSummary = failureSummary
    )
  }

  private fun compareOutputs(actual: String, expected: String, mode: ComparisonMode): Boolean {
    return when (mode) {
      ComparisonMode.EXACT -> actual == expected
      ComparisonMode.TRIMMED -> actual.trim() == expected.trim()
      ComparisonMode.CASE_INSENSITIVE -> actual.trim().equals(expected.trim(), ignoreCase = true)
      ComparisonMode.NUMERIC_FLOAT -> {
        val actNum = actual.trim().toDoubleOrNull()
        val expNum = expected.trim().toDoubleOrNull()
        if (actNum != null && expNum != null) {
          abs(actNum - expNum) < 1e-4
        } else {
          actual.trim() == expected.trim()
        }
      }
      ComparisonMode.LINE_BY_LINE_TRIMMED -> {
        val actLines = actual.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val expLines = expected.lines().map { it.trim() }.filter { it.isNotEmpty() }
        actLines == expLines
      }
    }
  }
}
