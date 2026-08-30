package com.example

import com.example.domain.ai.AIRateLimiter
import com.example.domain.ai.ContextManager
import com.example.domain.ai.MentorPrompts
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.LearningContext
import com.example.domain.execution.ExecutionErrorType
import com.example.domain.execution.ExecutionOptions
import com.example.domain.execution.SafePythonSandboxEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Milestone 12: Comprehensive Security & Sandbox Hardening Audit Test Suite.
 */
class SecurityAuditTest {

  private val sandboxEngine = SafePythonSandboxEngine()

  @Test
  fun testInfiniteLoopStepBudgetEnforcement() {
    val infiniteLoopCode = """
      count = 0
      while True:
          count = count + 1
    """.trimIndent()

    val options = ExecutionOptions(maxStepBudget = 200, timeoutMs = 1000)
    val result = sandboxEngine.execute(
      code = infiniteLoopCode,
      rawInput = "",
      options = options
    )

    assertFalse("Execution should fail due to step limit", result.isSuccess)
    assertEquals("Should return TIME_LIMIT_EXCEEDED", ExecutionErrorType.TIME_LIMIT_EXCEEDED, result.errorType)
    assertTrue("Error should mention step budget or infinite loop", result.stderr.contains("TimeLimitExceeded") || result.stderr.contains("step"))
  }

  @Test
  fun testOutputLimitMemoryProtection() {
    val spamPrintCode = """
      for i in range(100):
          print("A" * 500)
    """.trimIndent()

    val options = ExecutionOptions(maxOutputChars = 500)
    val result = sandboxEngine.execute(
      code = spamPrintCode,
      rawInput = "",
      options = options
    )

    assertTrue("Output should not exceed the max buffer limit", result.stdout.length <= 1000)
    if (!result.isSuccess) {
      assertEquals(ExecutionErrorType.OUTPUT_LIMIT_EXCEEDED, result.errorType)
    }
  }

  @Test
  fun testForbiddenKeywordsBlocked() {
    val dangerousCodes = listOf(
      "import os; os.system('ls')",
      "exec('print(123)')",
      "eval('1 + 2')",
      "open('/etc/passwd', 'r')",
      "__import__('sys').exit()",
      "import subprocess; subprocess.Popen(['ls'])",
      "import socket; s = socket.socket()",
      "x = globals()",
      "y = getattr(object, '__class__')"
    )

    for (code in dangerousCodes) {
      val result = sandboxEngine.execute(code = code)
      assertFalse("Forbidden code '$code' must be rejected", result.isSuccess)
      assertEquals("Error type should be SECURITY_VIOLATION", ExecutionErrorType.SECURITY_VIOLATION, result.errorType)
      assertTrue("Stderr should contain Security Exception", result.stderr.contains("Security Exception") || result.stderr.contains("restricted keyword"))
    }
  }

  @Test
  fun testContextManagerSanitizesSystemPaths() {
    val contextManager = ContextManager()
    val rawContext = LearningContext(
      currentCode = "print('Hello World')",
      recentError = "Error at /data/data/com.example/files/runtime.py line 45: crash",
      stderr = "Traceback at /var/lib/container/python3.py: unexpected failure"
    )

    val sanitized = contextManager.buildSanitizedContext(rawContext)
    assertFalse("Sanitized error should not leak Android internal /data/data/ path", sanitized.recentError?.contains("/data/data/") == true)
    assertFalse("Sanitized stderr should not leak container /var/lib/ path", sanitized.stderr?.contains("/var/lib/") == true)
  }

  @Test
  fun testAIRateLimiterPreventsSpam() {
    val rateLimiter = AIRateLimiter()

    // 5 requests in rapid succession should pass within budget
    var firstCall = rateLimiter.checkRateLimit("HINT")
    assertTrue("First call should be allowed", firstCall.isAllowed)

    // Simulate exhausting tokens
    for (i in 1..10) {
      rateLimiter.checkRateLimit("HINT")
    }

    // Next call should be throttled
    val throttled = rateLimiter.checkRateLimit("HINT")
    assertFalse("Excessive rapid calls should be throttled", throttled.isAllowed)
    assertNotNull("Should provide throttle reason", throttled.reason)
  }

  @Test
  fun testMentorPromptBoundsAndNoDirectSolutionLeak() {
    val context = LearningContext(
      exercisePrompt = "Write a function to return the square of a number",
      currentCode = "def square(n):\n    return n + n",
      recentError = "Test failed: square(3) returned 6, expected 9",
      hintLevelRequested = 1
    )

    val prompt = MentorPrompts.buildPrompt(AIMentorMode.HINT, context)
    assertTrue("Prompt should include Code Coach persona instruction", prompt.contains("Code Coach"))
    assertTrue("Prompt should instruct never to dump full working solutions", prompt.contains("Never dump full working solutions"))
    assertTrue("Prompt should specify hint level 1 goal", prompt.contains("Level 1 Hint"))
  }
}
