package com.example.domain.execution

import com.example.data.models.ChallengeTestCase
import com.example.domain.languages.LanguageRegistry

/**
 * Unified code execution service abstraction.
 * Decouples the UI and editor from the underlying execution runtime or remote sandboxed backend.
 */
interface CodeExecutionService {

  suspend fun execute(
    code: String,
    languageId: String,
    rawInput: String = "",
    options: ExecutionOptions = ExecutionOptions()
  ): ExecutionResult

  suspend fun runPublicTests(
    code: String,
    languageId: String,
    testCases: List<ChallengeTestCase>,
    options: ExecutionOptions = ExecutionOptions()
  ): TestSuiteResult

  suspend fun runFullValidation(
    code: String,
    languageId: String,
    publicTests: List<ChallengeTestCase>,
    hiddenTests: List<ChallengeTestCase>,
    options: ExecutionOptions = ExecutionOptions()
  ): TestSuiteResult
}

/**
 * Python execution adapter handling sandboxed client-side execution or delegating to remote backends.
 */
class PythonExecutionAdapter(
  private val sandboxEngine: SafePythonSandboxEngine = SafePythonSandboxEngine(),
  private val testRunner: TestRunner = TestRunner(sandboxEngine)
) {

  fun execute(code: String, rawInput: String, options: ExecutionOptions): ExecutionResult {
    return sandboxEngine.execute(code, rawInput, options)
  }

  fun runTests(code: String, testCases: List<ChallengeTestCase>, options: ExecutionOptions): TestSuiteResult {
    return testRunner.runTestSuite("python", code, testCases, options)
  }
}

/**
 * Pluggable adapter ready for production remote sandboxing (Docker/microVMs/AWS Lambda).
 */
class BackendSandboxExecutionAdapter(
  private val backendEndpoint: String? = null,
  private val isConfigured: Boolean = false
) {
  fun executeRemote(code: String, language: String, input: String): ExecutionResult {
    if (!isConfigured || backendEndpoint == null) {
      return ExecutionResult(
        isSuccess = false,
        exitCode = 1,
        errorType = ExecutionErrorType.UNAVAILABLE_ENVIRONMENT,
        errorMessage = "Secure remote execution is not configured for $language. Local sandboxed environment is active."
      )
    }
    // Remote payload invocation logic will connect here in production
    return ExecutionResult(isSuccess = true, stdout = "Remote execution placeholder")
  }
}

/**
 * Default implementation of CodeExecutionService.
 */
class DefaultCodeExecutionService : CodeExecutionService {

  private val runtimes: Map<String, CodeRuntime> = mapOf(
    "python" to PythonRuntime(),
    "javascript" to JavaScriptRuntime(),
    "java" to JavaRuntime(),
    "c" to CRuntime(),
    "cpp" to CppRuntime()
  )

  override suspend fun execute(
    code: String,
    languageId: String,
    rawInput: String,
    options: ExecutionOptions
  ): ExecutionResult {
    val lang = LanguageRegistry.getLanguage(languageId)
    val runtime = runtimes[languageId.lowercase()]
    
    if (runtime == null || !lang.executionSupported) {
      return ExecutionResult(
        isSuccess = false,
        exitCode = 1,
        errorType = ExecutionErrorType.UNSUPPORTED_LANGUAGE,
        errorMessage = "Secure code execution is not configured for ${lang.name}."
      )
    }

    return runtime.execute(code, rawInput, options)
  }

  override suspend fun runPublicTests(
    code: String,
    languageId: String,
    testCases: List<ChallengeTestCase>,
    options: ExecutionOptions
  ): TestSuiteResult {
    val lang = LanguageRegistry.getLanguage(languageId)
    val runtime = runtimes[languageId.lowercase()]

    if (runtime == null || !lang.executionSupported) {
      return TestSuiteResult(
        allPassed = false,
        passedCount = 0,
        totalCount = testCases.size,
        results = emptyList(),
        totalExecutionTimeMs = 0L,
        failureSummary = "Secure code execution is not configured for ${lang.name}."
      )
    }

    return runtime.runTests(code, testCases, options)
  }

  override suspend fun runFullValidation(
    code: String,
    languageId: String,
    publicTests: List<ChallengeTestCase>,
    hiddenTests: List<ChallengeTestCase>,
    options: ExecutionOptions
  ): TestSuiteResult {
    val lang = LanguageRegistry.getLanguage(languageId)
    val runtime = runtimes[languageId.lowercase()]
    val allTests = publicTests + hiddenTests

    if (runtime == null || !lang.executionSupported) {
      return TestSuiteResult(
        allPassed = false,
        passedCount = 0,
        totalCount = allTests.size,
        results = emptyList(),
        totalExecutionTimeMs = 0L,
        failureSummary = "Secure code execution is not configured for ${lang.name}."
      )
    }

    return runtime.runTests(code, allTests, options)
  }
}
