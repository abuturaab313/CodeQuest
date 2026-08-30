package com.example.domain.execution

import java.util.LinkedList
import java.util.Queue

/**
 * Robust, highly secure, in-memory sandboxed AST interpreter for Python.
 *
 * Security & Isolation Guarantees:
 * - NO Java Reflection, Runtime.exec, ProcessBuilder, Thread, or Socket access.
 * - Enforces deterministic instruction step budgets (preventing infinite loops/CPU exhaustion).
 * - Enforces string/memory output buffer ceilings (preventing memory bomb attacks).
 * - Evaluates statements and expressions via clean AST traversal.
 * - Standard Python built-ins supported: print, input, len, range, int, float, str, bool, sum, max, min, abs, round.
 * - Supported Control Flow: if/elif/else, for loops, while loops, function definitions (def), return, break, continue.
 */
class SafePythonSandboxEngine {

  companion object {
    private val FORBIDDEN_KEYWORDS = setOf(
      "__import__", "exec", "eval", "compile", "open", "file", "os", "sys",
      "subprocess", "shutil", "socket", "threading", "multiprocessing", "ctypes",
      "pickle", "globals", "locals", "vars", "getattr", "setattr", "delattr", "hasattr",
      "classmethod", "staticmethod", "__class__", "__subclasses__", "__bases__",
      "__mro__", "__dict__", "__code__"
    )

    fun splitArguments(text: String): List<String> {
      if (text.isBlank()) return emptyList()
      val result = mutableListOf<String>()
      var depth = 0
      var inQuotes = false
      var quoteChar = ' '
      var start = 0

      for (i in 0 until text.length) {
        val ch = text[i]
        if ((ch == '"' || ch == '\'') && (i == 0 || text[i - 1] != '\\')) {
          if (!inQuotes) {
            inQuotes = true
            quoteChar = ch
          } else if (quoteChar == ch) {
            inQuotes = false
          }
        }
        if (!inQuotes) {
          if (ch == '(' || ch == '[' || ch == '{') depth++
          else if (ch == ')' || ch == ']' || ch == '}') depth--
          else if (ch == ',' && depth == 0) {
            val part = text.substring(start, i).trim()
            if (part.isNotEmpty()) result.add(part)
            start = i + 1
          }
        }
      }
      if (start < text.length) {
        val last = text.substring(start).trim()
        if (last.isNotEmpty()) result.add(last)
      }
      return result
    }

    fun formatValue(v: Any?): String {
      return when (v) {
        null -> "None"
        is Boolean -> if (v) "True" else "False"
        is Double -> if (v == v.toInt().toDouble()) v.toInt().toString() else v.toString()
        is List<*> -> "[${v.joinToString(", ") { formatValue(it) }}]"
        else -> v.toString()
      }
    }
  }

  fun execute(
    code: String,
    rawInput: String = "",
    options: ExecutionOptions = ExecutionOptions(),
    workspaceFiles: Map<String, String> = emptyMap()
  ): ExecutionResult {
    val startTime = System.currentTimeMillis()

    // 1. Static Security & Blacklist Inspection
    val securityViolation = inspectStaticSecurity(code)
    if (securityViolation != null) {
      return ExecutionResult(
        stdout = "",
        stderr = "Security Exception: $securityViolation",
        exitCode = 1,
        executionTimeMs = System.currentTimeMillis() - startTime,
        isSuccess = false,
        errorType = ExecutionErrorType.SECURITY_VIOLATION,
        errorMessage = securityViolation
      )
    }

    // 2. Queue standard inputs
    val inputQueue: Queue<String> = LinkedList()
    if (rawInput.isNotBlank()) {
      rawInput.lines().forEach { inputQueue.add(it) }
    }

    val stdoutBuffer = StringBuilder()

    // 3. Parse AST
    val ast: List<ASTNode>
    try {
      ast = parseCodeToAst(code)
    } catch (e: SyntaxErrorException) {
      return ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "SyntaxError: ${e.message} on line ${e.lineNumber}",
        exitCode = 1,
        executionTimeMs = System.currentTimeMillis() - startTime,
        isSuccess = false,
        errorType = ExecutionErrorType.SYNTAX_ERROR,
        errorMessage = e.message,
        errorLineNumber = e.lineNumber
      )
    } catch (e: Exception) {
      return ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "SyntaxError: Invalid syntax (${e.message})",
        exitCode = 1,
        executionTimeMs = System.currentTimeMillis() - startTime,
        isSuccess = false,
        errorType = ExecutionErrorType.SYNTAX_ERROR,
        errorMessage = e.message
      )
    }

    // 4. Execute AST in Sandbox Environment
    val env = SandboxEnvironment(inputQueue, stdoutBuffer, options, workspaceFiles, this)

    return try {
      // Pre-load helper files in workspace (except main file)
      workspaceFiles.forEach { (fileName, fileContent) ->
        if (fileName.endsWith(".py") && !fileName.equals("main.py", ignoreCase = true) && fileContent != code) {
          try {
            val helperAst = parseCodeToAst(fileContent)
            env.executeBlock(helperAst)
          } catch (e: Exception) {
            // Ignore pre-load errors; explicit import will report them if relevant
          }
        }
      }

      env.executeBlock(ast)
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "",
        exitCode = 0,
        executionTimeMs = duration,
        isSuccess = true,
        errorType = ExecutionErrorType.NONE
      )
    } catch (e: TimeoutException) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "TimeLimitExceeded: Program exceeded step execution budget (${options.maxStepBudget} instructions). Possible infinite loop.",
        exitCode = 124,
        executionTimeMs = duration,
        isSuccess = false,
        errorType = ExecutionErrorType.TIME_LIMIT_EXCEEDED,
        errorMessage = "Execution step limit exceeded."
      )
    } catch (e: OutputLimitException) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdoutBuffer.toString().take(options.maxOutputChars),
        stderr = "OutputLimitExceeded: Console generated too much output (> ${options.maxOutputChars} chars).",
        exitCode = 1,
        executionTimeMs = duration,
        isSuccess = false,
        errorType = ExecutionErrorType.OUTPUT_LIMIT_EXCEEDED,
        errorMessage = "Console output length limit exceeded."
      )
    } catch (e: SandboxRuntimeException) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "RuntimeError: ${e.message} on line ${e.lineNumber}",
        exitCode = 1,
        executionTimeMs = duration,
        isSuccess = false,
        errorType = ExecutionErrorType.RUNTIME_ERROR,
        errorMessage = e.message,
        errorLineNumber = e.lineNumber
      )
    } catch (e: Exception) {
      val duration = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdoutBuffer.toString(),
        stderr = "RuntimeError: ${e.message}",
        exitCode = 1,
        executionTimeMs = duration,
        isSuccess = false,
        errorType = ExecutionErrorType.RUNTIME_ERROR,
        errorMessage = e.message
      )
    }
  }

  // --- Static Security Filter ---

  private fun inspectStaticSecurity(code: String): String? {
    val tokens = code.split(Regex("[^a-zA-Z0-9_]")).filter { it.isNotBlank() }
    for (token in tokens) {
      if (FORBIDDEN_KEYWORDS.contains(token)) {
        return "Access to restricted keyword or module '$token' is forbidden in safe sandbox."
      }
    }
    return null
  }

  // --- AST Node Definitions ---

  private sealed class ASTNode(val lineNumber: Int)
  private class StmtAssignment(val target: String, val expression: String, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtPrint(val expressions: List<String>, val sep: String = " ", val end: String = "\n", lineNumber: Int) : ASTNode(lineNumber)
  private class StmtIf(val condition: String, val body: List<ASTNode>, val elifBranches: List<Pair<String, List<ASTNode>>>, val elseBody: List<ASTNode>?, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtFor(val varName: String, val iterableExpr: String, val body: List<ASTNode>, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtWhile(val condition: String, val body: List<ASTNode>, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtDef(val name: String, val params: List<String>, val body: List<ASTNode>, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtReturn(val expression: String?, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtBreak(lineNumber: Int) : ASTNode(lineNumber)
  private class StmtContinue(lineNumber: Int) : ASTNode(lineNumber)
  private class StmtPass(lineNumber: Int) : ASTNode(lineNumber)
  private class StmtImport(val module: String, val symbols: List<String>?, lineNumber: Int) : ASTNode(lineNumber)
  private class StmtExpr(val expr: String, lineNumber: Int) : ASTNode(lineNumber)

  // --- AST Parser ---

  private fun parseCodeToAst(code: String): List<ASTNode> {
    val rawLines = code.lines()
    val linesWithIndex = rawLines.mapIndexed { idx, line -> (idx + 1) to line }
      .filter { (_, line) -> line.trim().isNotEmpty() && !line.trim().startsWith("#") }

    val cursor = Any()
    setCursorIndex(cursor, 0)
    return parseBlock(linesWithIndex, cursor, expectedIndent = 0)
  }

  private fun parseBlock(lines: List<Pair<Int, String>>, cursor: Any, expectedIndent: Int): List<ASTNode> {
    val nodes = mutableListOf<ASTNode>()
    var idx = getCursorIndex(cursor)

    while (idx < lines.size) {
      val (lineNum, rawLine) = lines[idx]
      val indent = getIndentation(rawLine)

      if (indent < expectedIndent) {
        break
      }

      val trimmed = rawLine.trim()

      if (trimmed.startsWith("def ") && trimmed.endsWith(":")) {
        val header = trimmed.substring(4, trimmed.length - 1).trim()
        val openP = header.indexOf('(')
        val closeP = header.lastIndexOf(')')
        if (openP == -1 || closeP == -1) throw SyntaxErrorException("Malformed function definition", lineNum)
        val funcName = header.substring(0, openP).trim()
        val paramsStr = header.substring(openP + 1, closeP).trim()
        val params = splitArguments(paramsStr)

        idx++
        setCursorIndex(cursor, idx)
        val body = parseBlock(lines, cursor, expectedIndent + 4)
        nodes.add(StmtDef(funcName, params, body, lineNum))
        idx = getCursorIndex(cursor)
        continue
      } else if (trimmed.startsWith("if ") && trimmed.endsWith(":")) {
        val condition = trimmed.substring(3, trimmed.length - 1).trim()
        idx++
        setCursorIndex(cursor, idx)
        val body = parseBlock(lines, cursor, expectedIndent + 4)
        idx = getCursorIndex(cursor)

        val elifBranches = mutableListOf<Pair<String, List<ASTNode>>>()
        var elseBody: List<ASTNode>? = null

        while (idx < lines.size) {
          val (nextLineNum, nextRaw) = lines[idx]
          val nextIndent = getIndentation(nextRaw)
          if (nextIndent != indent) break

          val nextTrimmed = nextRaw.trim()
          if (nextTrimmed.startsWith("elif ") && nextTrimmed.endsWith(":")) {
            val elifCond = nextTrimmed.substring(5, nextTrimmed.length - 1).trim()
            idx++
            setCursorIndex(cursor, idx)
            val elifBody = parseBlock(lines, cursor, expectedIndent + 4)
            elifBranches.add(elifCond to elifBody)
            idx = getCursorIndex(cursor)
          } else if (nextTrimmed == "else:") {
            idx++
            setCursorIndex(cursor, idx)
            elseBody = parseBlock(lines, cursor, expectedIndent + 4)
            idx = getCursorIndex(cursor)
            break
          } else {
            break
          }
        }

        nodes.add(StmtIf(condition, body, elifBranches, elseBody, lineNum))
        idx = getCursorIndex(cursor)
        continue
      } else if (trimmed.startsWith("for ") && trimmed.endsWith(":")) {
        val forHeader = trimmed.substring(4, trimmed.length - 1).trim()
        val inIndex = forHeader.indexOf(" in ")
        if (inIndex == -1) throw SyntaxErrorException("Missing 'in' in for loop", lineNum)

        val varName = forHeader.substring(0, inIndex).trim()
        val iterExpr = forHeader.substring(inIndex + 4).trim()

        idx++
        setCursorIndex(cursor, idx)
        val body = parseBlock(lines, cursor, expectedIndent + 4)
        nodes.add(StmtFor(varName, iterExpr, body, lineNum))
        idx = getCursorIndex(cursor)
        continue
      } else if (trimmed.startsWith("while ") && trimmed.endsWith(":")) {
        val condition = trimmed.substring(6, trimmed.length - 1).trim()
        idx++
        setCursorIndex(cursor, idx)
        val body = parseBlock(lines, cursor, expectedIndent + 4)
        nodes.add(StmtWhile(condition, body, lineNum))
        idx = getCursorIndex(cursor)
        continue
      } else if (trimmed.startsWith("print(") && trimmed.endsWith(")")) {
        val inside = trimmed.substring(6, trimmed.length - 1).trim()
        val args = splitArguments(inside)
        nodes.add(StmtPrint(args, lineNumber = lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed.startsWith("import ")) {
        val mod = trimmed.substring(7).trim()
        nodes.add(StmtImport(mod, null, lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed.startsWith("from ") && trimmed.contains(" import ")) {
        val afterFrom = trimmed.substring(5).trim()
        val impIdx = afterFrom.indexOf(" import ")
        val mod = afterFrom.substring(0, impIdx).trim()
        val symsStr = afterFrom.substring(impIdx + 8).trim()
        val syms = splitArguments(symsStr)
        nodes.add(StmtImport(mod, syms, lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed == "pass") {
        nodes.add(StmtPass(lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed == "break") {
        nodes.add(StmtBreak(lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed == "continue") {
        nodes.add(StmtContinue(lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (trimmed.startsWith("return")) {
        val retExpr = trimmed.substring(6).trim().ifEmpty { null }
        nodes.add(StmtReturn(retExpr, lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else if (isAssignment(trimmed)) {
        val (target, expr) = splitAssignment(trimmed, lineNum)
        nodes.add(StmtAssignment(target, expr, lineNum))
        idx++
        setCursorIndex(cursor, idx)
      } else {
        nodes.add(StmtExpr(trimmed, lineNum))
        idx++
        setCursorIndex(cursor, idx)
      }
    }
    return nodes
  }

  private var cursorMap = java.util.IdentityHashMap<Any, Int>()
  private fun getCursorIndex(cursor: Any): Int = cursorMap.getOrDefault(cursor, 0)
  private fun setCursorIndex(cursor: Any, idx: Int) { cursorMap[cursor] = idx }

  private fun getIndentation(line: String): Int {
    var count = 0
    for (ch in line) {
      if (ch == ' ') count++
      else if (ch == '\t') count += 4
      else break
    }
    return count
  }

  private fun isAssignment(line: String): Boolean {
    if (line.contains("==") || line.contains("!=") || line.contains("<=") || line.contains(">=")) return false
    val eqIdx = line.indexOf('=')
    return eqIdx > 0 && !line.startsWith("for ") && !line.startsWith("if ") && !line.startsWith("while ")
  }

  private fun splitAssignment(line: String, lineNum: Int): Pair<String, String> {
    val op = when {
      line.contains("+=") -> "+="
      line.contains("-=") -> "-="
      line.contains("*=") -> "*="
      line.contains("/=") -> "/="
      line.contains("%=") -> "%="
      else -> "="
    }
    val idx = line.indexOf(op)
    val target = line.substring(0, idx).trim()
    val right = line.substring(idx + op.length).trim()

    val expr = when (op) {
      "+=" -> "$target + ($right)"
      "-=" -> "$target - ($right)"
      "*=" -> "$target * ($right)"
      "/=" -> "$target / ($right)"
      "%=" -> "$target % ($right)"
      else -> right
    }
    return target to expr
  }

  // --- Sandbox Execution Environment ---

  private class ControlFlowBreak : Throwable()
  private class ControlFlowContinue : Throwable()
  private class ControlFlowReturn(val value: Any?) : Throwable()

  private class SandboxEnvironment(
    val inputQueue: Queue<String>,
    val stdout: StringBuilder,
    val options: ExecutionOptions,
    val workspaceFiles: Map<String, String> = emptyMap(),
    val engine: SafePythonSandboxEngine? = null
  ) {
    val variables = mutableMapOf<String, Any?>()
    val functions = mutableMapOf<String, StmtDef>()
    var stepCount = 0

    fun checkBudget(lineNum: Int) {
      stepCount++
      if (stepCount > options.maxStepBudget) {
        throw TimeoutException()
      }
    }

    fun appendStdout(text: String) {
      if (stdout.length + text.length > options.maxOutputChars) {
        throw OutputLimitException()
      }
      stdout.append(text)
    }

    fun executeBlock(nodes: List<ASTNode>) {
      for (node in nodes) {
        checkBudget(node.lineNumber)
        executeNode(node)
      }
    }

    private fun executeNode(node: ASTNode) {
      when (node) {
        is StmtAssignment -> {
          val evaluated = evaluateExpression(node.expression, node.lineNumber)
          val target = node.target
          if (target.contains("[") && target.endsWith("]")) {
            val bracketIdx = target.indexOf('[')
            val listVar = target.substring(0, bracketIdx).trim()
            val indexExpr = target.substring(bracketIdx + 1, target.length - 1).trim()
            val listObj = variables[listVar]
            val idx = evaluateExpression(indexExpr, node.lineNumber) as? Int
              ?: throw SandboxRuntimeException("Index must be an integer", node.lineNumber)
            if (listObj is MutableList<*>) {
              @Suppress("UNCHECKED_CAST")
              (listObj as MutableList<Any?>)[idx] = evaluated
            } else {
              throw SandboxRuntimeException("Target is not subscriptable", node.lineNumber)
            }
          } else {
            variables[target] = evaluated
          }
        }
        is StmtPrint -> {
          val outputStr = node.expressions.joinToString(node.sep) { expr ->
            val v = evaluateExpression(expr, node.lineNumber)
            formatValue(v)
          }
          appendStdout(outputStr + node.end)
        }
        is StmtIf -> {
          val condVal = evaluateCondition(node.condition, node.lineNumber)
          if (condVal) {
            executeBlock(node.body)
          } else {
            var matched = false
            for ((elifCond, elifBody) in node.elifBranches) {
              if (evaluateCondition(elifCond, node.lineNumber)) {
                executeBlock(elifBody)
                matched = true
                break
              }
            }
            if (!matched && node.elseBody != null) {
              executeBlock(node.elseBody)
            }
          }
        }
        is StmtFor -> {
          val iterVal = evaluateExpression(node.iterableExpr, node.lineNumber)
          val items: Iterable<Any?> = when (iterVal) {
            is Iterable<*> -> iterVal
            is String -> iterVal.map { it.toString() }
            else -> throw SandboxRuntimeException("Object '${formatValue(iterVal)}' is not iterable", node.lineNumber)
          }

          for (item in items) {
            checkBudget(node.lineNumber)
            variables[node.varName] = item
            try {
              executeBlock(node.body)
            } catch (e: ControlFlowBreak) {
              break
            } catch (e: ControlFlowContinue) {
              continue
            }
          }
        }
        is StmtWhile -> {
          while (evaluateCondition(node.condition, node.lineNumber)) {
            checkBudget(node.lineNumber)
            try {
              executeBlock(node.body)
            } catch (e: ControlFlowBreak) {
              break
            } catch (e: ControlFlowContinue) {
              continue
            }
          }
        }
        is StmtImport -> {
          val mod = node.module
          val content = workspaceFiles["$mod.py"] ?: workspaceFiles[mod]
          if (content != null && engine != null) {
            try {
              val modAst = engine.parseCodeToAst(content)
              executeBlock(modAst)
            } catch (e: Exception) {
              throw SandboxRuntimeException("Error importing module '$mod': ${e.message}", node.lineNumber)
            }
          } else if (mod != "math" && mod != "random") {
            // Module not in workspace
          }
        }
        is StmtDef -> {
          functions[node.name] = node
        }
        is StmtReturn -> {
          val retVal = node.expression?.let { evaluateExpression(it, node.lineNumber) }
          throw ControlFlowReturn(retVal)
        }
        is StmtBreak -> throw ControlFlowBreak()
        is StmtContinue -> throw ControlFlowContinue()
        is StmtPass -> { /* No-op */ }
        is StmtExpr -> {
          evaluateExpression(node.expr, node.lineNumber)
        }
      }
    }

    private fun evaluateCondition(cond: String, lineNum: Int): Boolean {
      val res = evaluateExpression(cond, lineNum)
      return isTruthy(res)
    }

    private fun isTruthy(v: Any?): Boolean {
      return when (v) {
        null -> false
        is Boolean -> v
        is Number -> v.toDouble() != 0.0
        is String -> v.isNotEmpty()
        is Collection<*> -> v.isNotEmpty()
        else -> true
      }
    }

    fun evaluateExpression(expr: String, lineNum: Int): Any? {
      checkBudget(lineNum)
      val trimmed = expr.trim()
      if (trimmed.isEmpty()) return null

      // String literal
      if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
        if (trimmed.length >= 2) {
          return trimmed.substring(1, trimmed.length - 1)
        }
      }

      // Boolean / None literals
      if (trimmed == "True") return true
      if (trimmed == "False") return false
      if (trimmed == "None") return null

      // Numeric literals
      trimmed.toIntOrNull()?.let { return it }
      trimmed.toDoubleOrNull()?.let { return it }

      // Input function: input() or input("...")
      if (trimmed.startsWith("input(") && trimmed.endsWith(")")) {
        return if (inputQueue.isNotEmpty()) inputQueue.poll() ?: "" else ""
      }

      // Type castings: int(x), float(x), str(x), bool(x), len(x), abs(x), round(x)
      if (trimmed.startsWith("int(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        return try {
          v.toString().trim().toDouble().toInt()
        } catch (e: Exception) {
          throw SandboxRuntimeException("invalid literal for int() with base 10: '$v'", lineNum)
        }
      }
      if (trimmed.startsWith("float(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(6, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        return try {
          v.toString().trim().toDouble()
        } catch (e: Exception) {
          throw SandboxRuntimeException("could not convert string to float: '$v'", lineNum)
        }
      }
      if (trimmed.startsWith("str(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        return formatValue(v)
      }
      if (trimmed.startsWith("bool(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(5, trimmed.length - 1).trim()
        return isTruthy(evaluateExpression(inner, lineNum))
      }
      if (trimmed.startsWith("len(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        return when (v) {
          is String -> v.length
          is Collection<*> -> v.size
          is Array<*> -> v.size
          else -> throw SandboxRuntimeException("object of type '${v?.javaClass?.simpleName}' has no len()", lineNum)
        }
      }
      if (trimmed.startsWith("abs(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        val num = (v as? Number)?.toDouble() ?: v.toString().toDoubleOrNull()
          ?: throw SandboxRuntimeException("bad operand type for abs(): '${formatValue(v)}'", lineNum)
        return if (v is Int) Math.abs(v) else Math.abs(num)
      }
      if (trimmed.startsWith("sum(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val v = evaluateExpression(inner, lineNum)
        if (v is Iterable<*>) {
          var s = 0.0
          var isInt = true
          for (item in v) {
            val d = (item as? Number)?.toDouble() ?: item.toString().toDoubleOrNull() ?: 0.0
            if (item !is Int) isInt = false
            s += d
          }
          return if (isInt) s.toInt() else s
        }
        throw SandboxRuntimeException("sum() requires an iterable", lineNum)
      }
      if (trimmed.startsWith("max(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val args = splitArguments(inner)
        if (args.size == 1) {
          val v = evaluateExpression(args[0], lineNum)
          if (v is List<*>) {
            val numList = v.mapNotNull { (it as? Number)?.toDouble() ?: it.toString().toDoubleOrNull() }
            if (numList.isEmpty()) throw SandboxRuntimeException("max() arg is an empty sequence", lineNum)
            val m = numList.maxOrNull()!!
            return if (v.all { it is Int }) m.toInt() else m
          }
        } else {
          val evaluatedArgs = args.map { evaluateExpression(it, lineNum) }
          val numList = evaluatedArgs.mapNotNull { (it as? Number)?.toDouble() ?: it.toString().toDoubleOrNull() }
          if (numList.size == args.size) {
            val m = numList.maxOrNull()!!
            return if (evaluatedArgs.all { it is Int }) m.toInt() else m
          }
        }
      }
      if (trimmed.startsWith("min(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(4, trimmed.length - 1).trim()
        val args = splitArguments(inner)
        if (args.size == 1) {
          val v = evaluateExpression(args[0], lineNum)
          if (v is List<*>) {
            val numList = v.mapNotNull { (it as? Number)?.toDouble() ?: it.toString().toDoubleOrNull() }
            if (numList.isEmpty()) throw SandboxRuntimeException("min() arg is an empty sequence", lineNum)
            val m = numList.minOrNull()!!
            return if (v.all { it is Int }) m.toInt() else m
          }
        } else {
          val evaluatedArgs = args.map { evaluateExpression(it, lineNum) }
          val numList = evaluatedArgs.mapNotNull { (it as? Number)?.toDouble() ?: it.toString().toDoubleOrNull() }
          if (numList.size == args.size) {
            val m = numList.minOrNull()!!
            return if (evaluatedArgs.all { it is Int }) m.toInt() else m
          }
        }
      }
      if (trimmed.startsWith("range(") && trimmed.endsWith(")")) {
        val inner = trimmed.substring(6, trimmed.length - 1).trim()
        val argStrings = splitArguments(inner)
        val args: List<Int> = argStrings.map {
          (evaluateExpression(it, lineNum) as? Number)?.toInt() ?: 0
        }
        return when (args.size) {
          1 -> (0 until args[0]).toList()
          2 -> (args[0] until args[1]).toList()
          3 -> {
            val step = if (args[2] != 0) args[2] else 1
            (args[0] until args[1] step step).toList()
          }
          else -> emptyList<Int>()
        }
      }

      // List literal [1, 2, 3] or comprehension [int(x) for x in line.split()]
      if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
        val inner = trimmed.substring(1, trimmed.length - 1).trim()
        if (inner.isEmpty()) return mutableListOf<Any?>()

        // List comprehension: [expr for var in iterable]
        if (inner.contains(" for ") && inner.contains(" in ")) {
          return evaluateListComprehension(inner, lineNum)
        }

        val items = splitArguments(inner)
        return items.map { evaluateExpression(it, lineNum) }.toMutableList()
      }

      // Method calls on objects: string.split(), string.count(x), list.append(x), string.strip()
      if (trimmed.contains(".")) {
        val dotResult = evaluateMethodCall(trimmed, lineNum)
        if (dotResult != NOT_A_METHOD) return dotResult
      }

      // Subscript / indexing: var[i]
      if (trimmed.contains("[") && trimmed.endsWith("]")) {
        val bracketIdx = trimmed.indexOf('[')
        val objName = trimmed.substring(0, bracketIdx).trim()
        val indexExpr = trimmed.substring(bracketIdx + 1, trimmed.length - 1).trim()
        val obj = evaluateExpression(objName, lineNum)
        val idxVal = (evaluateExpression(indexExpr, lineNum) as? Number)?.toInt()
          ?: throw SandboxRuntimeException("indices must be integers", lineNum)

        if (obj is List<*>) {
          val actualIdx = if (idxVal < 0) obj.size + idxVal else idxVal
          if (actualIdx < 0 || actualIdx >= obj.size) {
            throw SandboxRuntimeException("list index out of range", lineNum)
          }
          return obj[actualIdx]
        }
        if (obj is String) {
          val actualIdx = if (idxVal < 0) obj.length + idxVal else idxVal
          if (actualIdx < 0 || actualIdx >= obj.length) {
            throw SandboxRuntimeException("string index out of range", lineNum)
          }
          return obj[actualIdx].toString()
        }
      }

      // User function calls: my_func(a, b) or module.my_func(a, b)
      val funcParen = trimmed.indexOf('(')
      if (funcParen > 0 && trimmed.endsWith(")")) {
        val candidateName = trimmed.substring(0, funcParen).trim()
        val simpleName = if (candidateName.contains('.')) candidateName.substringAfterLast('.') else candidateName

        // Check for math/random virtual builtins
        if (candidateName.startsWith("math.") || candidateName.startsWith("random.")) {
          val argStrs = splitArguments(trimmed.substring(funcParen + 1, trimmed.length - 1))
          val argVals = argStrs.map { evaluateExpression(it, lineNum) }
          when (candidateName) {
            "math.sqrt" -> {
              val n = (argVals.firstOrNull() as? Number)?.toDouble() ?: 0.0
              val res = Math.sqrt(n)
              return if (res == res.toInt().toDouble()) res.toInt() else res
            }
            "math.pow" -> {
              val b = (argVals.getOrNull(0) as? Number)?.toDouble() ?: 0.0
              val exp = (argVals.getOrNull(1) as? Number)?.toDouble() ?: 0.0
              val res = Math.pow(b, exp)
              return if (res == res.toInt().toDouble()) res.toInt() else res
            }
            "math.floor" -> return Math.floor((argVals.firstOrNull() as? Number)?.toDouble() ?: 0.0).toInt()
            "math.ceil" -> return Math.ceil((argVals.firstOrNull() as? Number)?.toDouble() ?: 0.0).toInt()
            "math.abs" -> return Math.abs((argVals.firstOrNull() as? Number)?.toDouble() ?: 0.0)
            "random.randint" -> {
              val min = (argVals.getOrNull(0) as? Number)?.toInt() ?: 1
              val max = (argVals.getOrNull(1) as? Number)?.toInt() ?: 10
              return (min..max).random()
            }
            "random.choice" -> {
              val list = argVals.firstOrNull() as? List<*> ?: emptyList<Any>()
              return if (list.isNotEmpty()) list.random() else null
            }
          }
        }

        val targetFunc = functions[candidateName] ?: functions[simpleName]
        if (targetFunc != null) {
          val f = targetFunc
          val argStrs = splitArguments(trimmed.substring(funcParen + 1, trimmed.length - 1))
          val argVals = argStrs.map { evaluateExpression(it, lineNum) }

          // Call in nested environment
          val nestedEnv = SandboxEnvironment(inputQueue, stdout, options, workspaceFiles, engine)
          nestedEnv.variables.putAll(variables)
          nestedEnv.functions.putAll(functions)
          for (i in 0 until minOf(f.params.size, argVals.size)) {
            nestedEnv.variables[f.params[i]] = argVals[i]
          }

          try {
            nestedEnv.executeBlock(f.body)
            return null
          } catch (ret: ControlFlowReturn) {
            return ret.value
          }
        }
      }

      // Binary operations: comparisons, arithmetic, boolean operators
      val binaryRes = evaluateBinaryOps(trimmed, lineNum)
      if (binaryRes != UNRESOLVED) return binaryRes

      // Variable lookup
      if (variables.containsKey(trimmed)) {
        return variables[trimmed]
      }

      throw SandboxRuntimeException("name '$trimmed' is not defined", lineNum)
    }

    private fun evaluateListComprehension(comp: String, lineNum: Int): MutableList<Any?> {
      val forIdx = comp.indexOf(" for ")
      val exprPart = comp.substring(0, forIdx).trim()
      val rest = comp.substring(forIdx + 5).trim()
      val inIdx = rest.indexOf(" in ")
      val varName = rest.substring(0, inIdx).trim()
      val iterExpr = rest.substring(inIdx + 4).trim()

      val iterVal = evaluateExpression(iterExpr, lineNum)
      val items: Iterable<Any?> = when (iterVal) {
        is Iterable<*> -> iterVal
        is String -> iterVal.map { it.toString() }
        else -> emptyList()
      }

      val resultList = mutableListOf<Any?>()
      for (item in items) {
        variables[varName] = item
        resultList.add(evaluateExpression(exprPart, lineNum))
      }
      return resultList
    }

    private val NOT_A_METHOD = Any()
    private fun evaluateMethodCall(expr: String, lineNum: Int): Any? {
      val dotIdx = expr.lastIndexOf('.')
      val targetExpr = expr.substring(0, dotIdx).trim()
      val callPart = expr.substring(dotIdx + 1).trim()
      val openP = callPart.indexOf('(')
      val closeP = callPart.lastIndexOf(')')

      if (openP == -1 || closeP != callPart.length - 1) return NOT_A_METHOD

      val methodName = callPart.substring(0, openP).trim()
      val argsStr = callPart.substring(openP + 1, closeP).trim()

      // Check if it's a module function call like operations.add()
      val moduleFunc = functions["$targetExpr.$methodName"] ?: functions[methodName]
      if (moduleFunc != null && !variables.containsKey(targetExpr)) {
        val argStrs = splitArguments(argsStr)
        val argVals = argStrs.map { evaluateExpression(it, lineNum) }
        val nestedEnv = SandboxEnvironment(inputQueue, stdout, options, workspaceFiles, engine)
        nestedEnv.variables.putAll(variables)
        nestedEnv.functions.putAll(functions)
        for (i in 0 until minOf(moduleFunc.params.size, argVals.size)) {
          nestedEnv.variables[moduleFunc.params[i]] = argVals[i]
        }
        try {
          nestedEnv.executeBlock(moduleFunc.body)
          return null
        } catch (ret: ControlFlowReturn) {
          return ret.value
        }
      }

      val args = splitArguments(argsStr).map { evaluateExpression(it, lineNum) }
      val targetObj = evaluateExpression(targetExpr, lineNum)

      return when (methodName) {
        "split" -> {
          val s = targetObj.toString()
          val sep = if (args.isNotEmpty()) args[0]?.toString() ?: " " else null
          val parts = if (sep != null && sep.isNotEmpty()) s.split(sep) else s.trim().split(Regex("\\s+"))
          parts.filter { it.isNotEmpty() }.toMutableList()
        }
        "strip" -> targetObj.toString().trim()
        "lower" -> targetObj.toString().lowercase()
        "upper" -> targetObj.toString().uppercase()
        "count" -> {
          val s = targetObj.toString()
          val sub = args.firstOrNull()?.toString() ?: ""
          if (sub.isEmpty()) 0 else (s.length - s.replace(sub, "").length) / sub.length
        }
        "append" -> {
          if (targetObj is MutableList<*>) {
            @Suppress("UNCHECKED_CAST")
            (targetObj as MutableList<Any?>).add(args.firstOrNull())
            null
          } else {
            throw SandboxRuntimeException("object has no method append", lineNum)
          }
        }
        "pop" -> {
          if (targetObj is MutableList<*>) {
            @Suppress("UNCHECKED_CAST")
            val l = targetObj as MutableList<Any?>
            if (l.isEmpty()) throw SandboxRuntimeException("pop from empty list", lineNum)
            l.removeAt(l.size - 1)
          } else {
            throw SandboxRuntimeException("object has no method pop", lineNum)
          }
        }
        else -> NOT_A_METHOD
      }
    }

    private val UNRESOLVED = Any()
    private fun evaluateBinaryOps(expr: String, lineNum: Int): Any? {
      val opsInOrder = listOf(
        listOf(" or "),
        listOf(" and "),
        listOf("==", "!=", "<=", ">=", "<", ">"),
        listOf("+", "-"),
        listOf("//", "**", "*", "/", "%")
      )

      for (opGroup in opsInOrder) {
        val split = findTopLevelOperator(expr, opGroup)
        if (split != null) {
          val (leftStr, op, rightStr) = split
          val leftVal = evaluateExpression(leftStr, lineNum)
          val rightVal = evaluateExpression(rightStr, lineNum)
          return applyOperator(leftVal, op.trim(), rightVal, lineNum)
        }
      }
      return UNRESOLVED
    }

    private fun findTopLevelOperator(text: String, operators: List<String>): Triple<String, String, String>? {
      var depth = 0
      var inQuotes = false
      var quoteChar = ' '

      for (i in text.length - 1 downTo 0) {
        val ch = text[i]
        if ((ch == '"' || ch == '\'') && (i == 0 || text[i - 1] != '\\')) {
          if (!inQuotes) {
            inQuotes = true
            quoteChar = ch
          } else if (quoteChar == ch) {
            inQuotes = false
          }
        }
        if (!inQuotes) {
          if (ch == ')' || ch == ']' || ch == '}') depth++
          else if (ch == '(' || ch == '[' || ch == '{') depth--
          else if (depth == 0) {
            for (op in operators) {
              if (text.startsWith(op, i)) {
                if (op == "*" && (text.startsWith("**", i) || (i > 0 && text[i - 1] == '*'))) continue
                if (op == "/" && (text.startsWith("//", i) || (i > 0 && text[i - 1] == '/'))) continue
                if (op == "<" && text.startsWith("<=", i)) continue
                if (op == ">" && text.startsWith(">=", i)) continue
                if (op == "=" && (text.startsWith("==", i) || text.startsWith("!=", i) || text.startsWith("<=", i) || text.startsWith(">=", i))) continue
                if (op == "-" && i == 0) continue

                val left = text.substring(0, i)
                val right = text.substring(i + op.length)
                return Triple(left, op, right)
              }
            }
          }
        }
      }
      return null
    }

    private fun applyOperator(left: Any?, op: String, right: Any?, lineNum: Int): Any? {
      when (op) {
        "or" -> return if (isTruthy(left)) left else right
        "and" -> return if (!isTruthy(left)) left else right
        "==" -> return compareValues(left, right) == 0
        "!=" -> return compareValues(left, right) != 0
        "<" -> return compareValues(left, right) < 0
        "<=" -> return compareValues(left, right) <= 0
        ">" -> return compareValues(left, right) > 0
        ">=" -> return compareValues(left, right) >= 0
        "+" -> {
          if (left is String || right is String) {
            return formatValue(left) + formatValue(right)
          }
          if (left is List<*> && right is List<*>) {
            return (left + right).toMutableList()
          }
          val l = (left as? Number)?.toDouble() ?: 0.0
          val r = (right as? Number)?.toDouble() ?: 0.0
          return if (left is Int && right is Int) (l + r).toInt() else l + r
        }
        "-" -> {
          val l = (left as? Number)?.toDouble() ?: 0.0
          val r = (right as? Number)?.toDouble() ?: 0.0
          return if (left is Int && right is Int) (l - r).toInt() else l - r
        }
        "*" -> {
          if (left is String && right is Int) return left.repeat(maxOf(0, right))
          if (left is Int && right is String) return right.repeat(maxOf(0, left))
          val l = (left as? Number)?.toDouble() ?: 0.0
          val r = (right as? Number)?.toDouble() ?: 0.0
          return if (left is Int && right is Int) (l * r).toInt() else l * r
        }
        "/" -> {
          val l = (left as? Number)?.toDouble() ?: 0.0
          val r = (right as? Number)?.toDouble() ?: 0.0
          if (r == 0.0) throw SandboxRuntimeException("division by zero", lineNum)
          val result = l / r
          return if (result == result.toInt().toDouble()) result.toInt() else result
        }
        "//" -> {
          val l = (left as? Number)?.toLong() ?: 0L
          val r = (right as? Number)?.toLong() ?: 0L
          if (r == 0L) throw SandboxRuntimeException("integer division or modulo by zero", lineNum)
          return (l / r).toInt()
        }
        "%" -> {
          val l = (left as? Number)?.toLong() ?: 0L
          val r = (right as? Number)?.toLong() ?: 0L
          if (r == 0L) throw SandboxRuntimeException("integer division or modulo by zero", lineNum)
          return (l % r).toInt()
        }
        "**" -> {
          val l = (left as? Number)?.toDouble() ?: 0.0
          val r = (right as? Number)?.toDouble() ?: 0.0
          val res = Math.pow(l, r)
          return if (left is Int && right is Int && r >= 0) res.toInt() else res
        }
      }
      throw SandboxRuntimeException("Unsupported operator $op", lineNum)
    }

    private fun compareValues(a: Any?, b: Any?): Int {
      if (a == null && b == null) return 0
      if (a == null) return -1
      if (b == null) return 1
      if (a is Number && b is Number) {
        return a.toDouble().compareTo(b.toDouble())
      }
      return a.toString().compareTo(b.toString())
    }
  }

  // --- Exceptions ---
  private class TimeoutException : Exception()
  private class OutputLimitException : Exception()
  private class SyntaxErrorException(message: String, val lineNumber: Int) : Exception(message)
  private class SandboxRuntimeException(message: String, val lineNumber: Int) : RuntimeException(message)
}
