package com.example

import com.example.data.local.ProjectCurriculum
import com.example.domain.execution.ComparisonMode
import com.example.domain.execution.ProjectRunner
import com.example.domain.execution.ProjectTest
import com.example.domain.execution.SafePythonSandboxEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectRunnerTest {

  private val sandboxEngine = SafePythonSandboxEngine()
  private val projectRunner = ProjectRunner(sandboxEngine)

  @Test
  fun testMultiFilePythonExecution() = runBlocking {
    val workspace = mapOf(
      "operations.py" to """
        def add(a, b):
            return a + b
        
        def multiply(a, b):
            return a * b
      """.trimIndent(),
      "main.py" to """
        import operations
        
        sum_val = operations.add(10, 25)
        prod_val = operations.multiply(4, 5)
        print(f"Sum: {sum_val}, Product: {prod_val}")
      """.trimIndent()
    )

    val result = sandboxEngine.execute(
      code = workspace["main.py"]!!,
      rawInput = "",
      workspaceFiles = workspace
    )

    assertFalse("Should not have execution error", result.hasError)
    assertTrue("Stdout should contain Sum: 35", result.stdout.contains("Sum: 35"))
    assertTrue("Stdout should contain Product: 20", result.stdout.contains("Product: 20"))
  }

  @Test
  fun testProjectRunnerWithCalculatorProject() = runBlocking {
    val calcProject = ProjectCurriculum.PROJECT_CALCULATOR

    // Create a working implementation of calculator
    val workingWorkspace = mapOf(
      "calculator.py" to """
        def add(a, b):
            return a + b
        
        def subtract(a, b):
            return a - b
            
        def multiply(a, b):
            return a * b
            
        def divide(a, b):
            if b == 0:
                return "Error: Division by zero"
            return a / b
      """.trimIndent(),
      "main.py" to """
        import calculator
        
        num1 = float(input())
        op = input().strip()
        num2 = float(input())
        
        if op == '+':
            print(calculator.add(num1, num2))
        elif op == '-':
            print(calculator.subtract(num1, num2))
        elif op == '*':
            print(calculator.multiply(num1, num2))
        elif op == '/':
            print(calculator.divide(num1, num2))
        else:
            print("Invalid operator")
      """.trimIndent()
    )

    val testSuiteResult = projectRunner.runAllTests(
      project = calcProject,
      workspaceFiles = workingWorkspace
    )

    assertTrue("All calculator tests should pass with valid code", testSuiteResult.allPassed)
    assertEquals(calcProject.parseTests().size, testSuiteResult.passedCount)
  }

  @Test
  fun testComparisonModes() {
    val exactTest = ProjectTest(
      id = "t1",
      title = "Exact Test",
      expectedOutput = "42",
      comparisonMode = "EXACT"
    )
    val resultExact = projectRunner.runTest(
      test = exactTest,
      workspaceFiles = mapOf("main.py" to "print(42)")
    )
    assertTrue(resultExact.passed)

    val containsTest = ProjectTest(
      id = "t2",
      title = "Contains Test",
      expectedOutput = "Hello",
      comparisonMode = "CONTAINS"
    )
    val resultContains = projectRunner.runTest(
      test = containsTest,
      workspaceFiles = mapOf("main.py" to "print('>>> Hello World <<<')")
    )
    assertTrue(resultContains.passed)
  }
}
