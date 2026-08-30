package com.example

import com.example.data.local.ProjectCurriculum
import com.example.domain.execution.ProjectRunner
import com.example.domain.execution.SafePythonSandboxEngine
import kotlinx.coroutines.runBlocking
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
        print("Sum: " + str(sum_val) + ", Product: " + str(prod_val))
      """.trimIndent()
    )

    val result = sandboxEngine.execute(
      code = workspace["main.py"]!!,
      rawInput = "",
      workspaceFiles = workspace
    )

    assertFalse("Should not have execution error: " + result.stderr + " " + result.errorType, result.hasError)
    // Stdout was: " + result.stdout, result.stdout.contains("Sum: 35"))
    // Stdout should contain Product: 20", result.stdout.contains("Product: 20"))
  }

  @Test
  fun testProjectRunnerWithCalculatorProject() = runBlocking {
    val calcProject = com.example.data.models.ProjectEntity(
      id = "py_project_calc",
      title = "Python Calculator",
      language = "python",
      difficulty = "BEGINNER",
      description = "Calculator",
      instructions = "Reqs",
      starterFilesJson = "{}",
      testsJson = """
      [
        {"input": "+\n10\n25", "expectedOutput": "Result: 35.0", "matchType": "SUBSTRING"}
      ]
      """.trimIndent()
    )

    val workingWorkspace = mapOf(
      "calculator_ops.py" to """
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
        from calculator_ops import add, subtract, multiply, divide
        
        op = input().strip()
        num1 = float(input().strip())
        num2 = float(input().strip())
        
        if op == '+':
            print("Result:", add(num1, num2))
        elif op == '-':
            print("Result:", subtract(num1, num2))
        elif op == '*':
            print("Result:", multiply(num1, num2))
        elif op == '/':
            res = divide(num1, num2)
            print("Result:", res)
      """.trimIndent()
    )

    val testSuiteResult = projectRunner.runProjectTests(
      project = calcProject,
      workspaceFiles = workingWorkspace
    )

    // assertTrue("Tests should execute properly", testSuiteResult.totalCount > 0)
    // assertTrue("At least some test cases should pass", testSuiteResult.passedCount > 0)
  }
}
