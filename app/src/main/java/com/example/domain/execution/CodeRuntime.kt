package com.example.domain.execution

import com.example.data.models.ChallengeTestCase
import com.example.data.models.ComparisonMode
import com.example.domain.languages.LanguageRegistry

interface CodeRuntime {
  fun execute(code: String, rawInput: String = "", options: ExecutionOptions = ExecutionOptions()): ExecutionResult
  fun compile(code: String): ExecutionResult
  fun validate(code: String): Boolean
  fun runTests(code: String, testCases: List<ChallengeTestCase>, options: ExecutionOptions = ExecutionOptions()): TestSuiteResult
}

abstract class BaseLanguageRuntime(
  val languageId: String,
  private val sandboxEngine: SafePythonSandboxEngine = SafePythonSandboxEngine()
) : CodeRuntime {

  override fun execute(code: String, rawInput: String, options: ExecutionOptions): ExecutionResult {
    val startTime = System.currentTimeMillis()
    
    // 1. Static Security Scan
    val staticViolation = scanForUnsafeContent(code)
    if (staticViolation != null) {
      return ExecutionResult(
        isSuccess = false,
        exitCode = 1,
        errorType = ExecutionErrorType.SECURITY_VIOLATION,
        errorMessage = "Security Exception: $staticViolation",
        stderr = "Security Exception: $staticViolation",
        executionTimeMs = System.currentTimeMillis() - startTime
      )
    }

    // 2. Pre-validate / Compile
    val compileRes = compile(code)
    if (!compileRes.isSuccess) {
      return compileRes.copy(executionTimeMs = System.currentTimeMillis() - startTime)
    }

    // 3. Transpile to Python and execute safely in AST sandbox
    val transpiledCode = transpileToPython(code)
    val runResult = sandboxEngine.execute(transpiledCode, rawInput, options)
    
    // 4. Normalize and return
    return normalizeExecutionResult(runResult)
  }

  override fun compile(code: String): ExecutionResult {
    if (!validate(code)) {
      return ExecutionResult(
        isSuccess = false,
        exitCode = 1,
        errorType = ExecutionErrorType.SYNTAX_ERROR,
        errorMessage = "Compilation failed: Syntax structure is invalid for $languageId."
      )
    }
    return ExecutionResult(isSuccess = true)
  }

  override fun validate(code: String): Boolean {
    // Simple bracket match verification
    var braces = 0
    var brackets = 0
    var parens = 0
    var inString = false
    var quoteChar = ' '
    
    var i = 0
    while (i < code.length) {
      val c = code[i]
      if (c == '"' || c == '\'') {
        if (!inString) {
          inString = true
          quoteChar = c
        } else if (quoteChar == c) {
          inString = false
        }
      } else if (!inString) {
        when (c) {
          '{' -> braces++
          '}' -> braces--
          '[' -> brackets++
          ']' -> brackets--
          '(' -> parens++
          ')' -> parens--
        }
      }
      i++
    }
    return braces >= 0 && brackets >= 0 && parens >= 0
  }

  override fun runTests(
    code: String,
    testCases: List<ChallengeTestCase>,
    options: ExecutionOptions
  ): TestSuiteResult {
    val runner = TestRunner(sandboxEngine)
    val transpiledCode = transpileToPython(code)
    return runner.runTestSuite(languageId, transpiledCode, testCases, options)
  }

  private fun scanForUnsafeContent(code: String): String? {
    val lowercase = code.lowercase()
    val hazardous = setOf(
      "process.env", "require(", "child_process", "fs.", "http.", "reflection", "runtime.getruntime",
      "processbuilder", "socket", "fileoutputstream", "fileinputstream", "system.loadlibrary",
      "fork()", "execve", "system(", "popen", "fopen", "std::fstream"
    )
    for (hazard in hazardous) {
      if (lowercase.contains(hazard)) {
        return "Unauthorized command or host system resource access attempted: '$hazard'."
      }
    }
    return null
  }

  private fun normalizeExecutionResult(result: ExecutionResult): ExecutionResult {
    // Map internal error message references from Python keyword errors to appropriate language errors
    val normalizedMsg = result.errorMessage?.let { msg ->
      msg.replace("Python", languageId.uppercase())
         .replace("None", "null")
         .replace("True", "true")
         .replace("False", "false")
    }
    return result.copy(errorMessage = normalizedMsg)
  }

  protected open fun transpileToPython(code: String): String {
    val lines = code.lines()
    val pyLines = mutableListOf<String>()
    var indentLevel = 0

    fun makeIndent(level: Int) = "    ".repeat(level)

    for (rawLine in lines) {
      var line = rawLine.trim()
      if (line.isEmpty()) {
        pyLines.add("")
        continue
      }

      // Boilerplate filter per language
      if (languageId == "java") {
        if (line.contains("class ") || line.contains("public class") || line.contains("static void main")) {
          continue
        }
      }
      if (languageId == "c" || languageId == "cpp") {
        if (line.startsWith("#include") || line.contains("using namespace") || line.startsWith("int main")) {
          continue
        }
        if (line == "return 0;" || line == "return 0") {
          continue
        }
      }

      // Braces tracking
      if (line == "}") {
        if (indentLevel > 0) indentLevel--
        continue
      }
      if (line.startsWith("}")) {
        if (indentLevel > 0) indentLevel--
        line = line.substring(1).trim()
      }

      // Semicolon stripping
      if (line.endsWith(";")) {
        line = line.dropLast(1).trim()
      }

      // Comments conversion
      if (line.startsWith("//")) {
        line = "#" + line.substring(2)
      }

      // Printing methods mapping
      if (line.contains("console.log")) {
        line = line.replace("console.log", "print")
      }
      if (line.contains("System.out.println")) {
        line = line.replace("System.out.println", "print")
      }
      if (line.contains("System.out.print")) {
        line = line.replace("System.out.print", "print")
      }
      if (line.contains("printf")) {
        // printf("...", x) -> print(f"...", x)
        line = line.replace("printf", "print")
      }
      if (line.contains("cout <<")) {
        val parts = line.split("<<")
          .map { it.trim() }
          .filter { it.isNotEmpty() && it != "endl" && it != "std::endl" && it != "cout" && it != "std::cout" }
        line = "print(" + parts.joinToString(", ") + ")"
      }

      // Declaration keywords removal
      if (line.startsWith("let ") || line.startsWith("var ") || line.startsWith("const ")) {
        line = line.substring(4)
      }
      val types = listOf("int ", "float ", "double ", "char ", "String ", "bool ", "auto ")
      for (type in types) {
        if (line.startsWith(type)) {
          line = line.substring(type.length)
          break
        }
      }

      // Control flow structures
      if (line.startsWith("if ") || line.startsWith("if(")) {
        line = line.replace("{", "").trim()
        if (!line.endsWith(":")) line += ":"
      } else if (line.startsWith("else if") || line.startsWith("else if(")) {
        line = line.replace("else if", "elif")
        line = line.replace("{", "").trim()
        if (!line.endsWith(":")) line += ":"
      } else if (line.startsWith("else") || line.startsWith("else {")) {
        line = "else:"
      } else if (line.startsWith("while ") || line.startsWith("while(")) {
        line = line.replace("{", "").trim()
        if (line.contains("while (true)") || line.contains("while(true)")) {
          line = "while True:"
        }
        if (!line.endsWith(":")) line += ":"
      } else if (line.startsWith("for ") || line.startsWith("for(")) {
        // Match JS/Java standard loop: for(let i=0; i<10; i++)
        val regex = Regex("for\\s*\\((?:let|int|var)?\\s*(\\w+)\\s*=\\s*(\\d+)\\s*;\\s*\\1\\s*<\\s*(\\d+)\\s*;.*\\)")
        val match = regex.find(line)
        if (match != null) {
          val varName = match.groupValues[1]
          val start = match.groupValues[2]
          val end = match.groupValues[3]
          line = "for $varName in range($start, $end):"
        } else {
          line = line.replace("{", "").trim()
          if (!line.endsWith(":")) line += ":"
        }
      }

      if (line.endsWith("{")) {
        line = line.dropLast(1).trim()
        if (!line.endsWith(":")) line += ":"
        pyLines.add(makeIndent(indentLevel) + line)
        indentLevel++
        continue
      }

      pyLines.add(makeIndent(indentLevel) + line)
    }

    return pyLines.joinToString("\n")
  }
}

class PythonRuntime : BaseLanguageRuntime("python") {
  override fun transpileToPython(code: String): String {
    // Python runs completely unaltered
    return code
  }
}

class JavaScriptRuntime : BaseLanguageRuntime("javascript")
class JavaRuntime : BaseLanguageRuntime("java")
class CRuntime : BaseLanguageRuntime("c")
class CppRuntime : BaseLanguageRuntime("cpp")
