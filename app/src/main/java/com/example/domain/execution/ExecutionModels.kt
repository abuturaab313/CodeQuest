package com.example.domain.execution

enum class ExecutionErrorType {
  NONE,
  SYNTAX_ERROR,
  RUNTIME_ERROR,
  TIME_LIMIT_EXCEEDED,
  OUTPUT_LIMIT_EXCEEDED,
  MEMORY_LIMIT_EXCEEDED,
  SECURITY_VIOLATION,
  UNSUPPORTED_LANGUAGE,
  UNAVAILABLE_ENVIRONMENT,
  WRONG_ANSWER
}

data class ExecutionOptions(
  val timeoutMs: Long = 2000L,
  val maxOutputChars: Int = 8192,
  val maxStepBudget: Int = 100000
)

data class ChallengeTestResult(
  val testId: String,
  val input: String,
  val expectedOutput: String,
  val actualOutput: String,
  val passed: Boolean,
  val isHidden: Boolean,
  val failureReason: String? = null,
  val executionTimeMs: Long = 0L,
  val errorType: ExecutionErrorType = ExecutionErrorType.NONE
)

data class ExecutionResult(
  val isSuccess: Boolean = true,
  val stdout: String = "",
  val stderr: String = "",
  val exitCode: Int = 0,
  val executionTimeMs: Long = 0L,
  val memoryUsageKb: Long = 0L,
  val errorType: ExecutionErrorType = ExecutionErrorType.NONE,
  val errorMessage: String? = null,
  val errorLineNumber: Int? = null,
  val testResults: List<ChallengeTestResult> = emptyList()
) {
  val hasError: Boolean get() = !isSuccess || errorType != ExecutionErrorType.NONE
}

data class TestSuiteResult(
  val allPassed: Boolean,
  val passedCount: Int,
  val totalCount: Int,
  val results: List<ChallengeTestResult>,
  val totalExecutionTimeMs: Long,
  val failureSummary: String? = null
)

data class ProjectTestResult(
  val testId: String,
  val taskId: String? = null,
  val title: String,
  val input: String = "",
  val expectedOutput: String = "",
  val actualOutput: String = "",
  val passed: Boolean,
  val isHidden: Boolean = false,
  val failureReason: String? = null,
  val executionTimeMs: Long = 0L,
  val errorType: ExecutionErrorType = ExecutionErrorType.NONE
)

data class ProjectTestSuiteResult(
  val allPassed: Boolean,
  val passedCount: Int,
  val totalCount: Int,
  val taskPassedMap: Map<String, Boolean> = emptyMap(),
  val results: List<ProjectTestResult> = emptyList(),
  val totalExecutionTimeMs: Long = 0L,
  val failureSummary: String? = null
)
