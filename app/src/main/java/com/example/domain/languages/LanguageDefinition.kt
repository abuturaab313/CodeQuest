package com.example.domain.languages

data class LanguageDefinition(
  val id: String,
  val name: String,
  val displayName: String,
  val version: String,
  val icon: String,
  val fileExtension: String,
  val syntaxDefinition: String,
  val executionSupported: Boolean,
  val testingSupported: Boolean,
  val projectSupported: Boolean,
  val curriculumAvailable: Boolean,
  val starterTemplate: String,
  val commentSyntax: String,
  val keywords: Set<String> = emptySet(),
  val builtins: Set<String> = emptySet(),
  val commonSymbols: List<String> = listOf("(", ")", "[", "]", "{", "}", "\"", "'", ":", "=", "+", "-", "*", "/", ",", ".")
)

object LanguageRegistry {

  val PYTHON = LanguageDefinition(
    id = "python",
    name = "Python",
    displayName = "Python",
    version = "3.11",
    icon = "python",
    fileExtension = ".py",
    syntaxDefinition = "python",
    executionSupported = true,
    testingSupported = true,
    projectSupported = true,
    curriculumAvailable = true,
    starterTemplate = "print(\"Hello, World!\")\n",
    commentSyntax = "#",
    keywords = setOf(
      "and", "as", "assert", "async", "await", "break", "class", "continue",
      "def", "del", "elif", "else", "except", "finally", "for", "from",
      "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
      "or", "pass", "raise", "return", "try", "while", "with", "yield",
      "True", "False", "None"
    ),
    builtins = setOf(
      "print", "input", "len", "range", "int", "str", "float", "bool",
      "list", "dict", "set", "tuple", "sum", "max", "min", "abs",
      "round", "type", "sorted", "reversed", "enumerate", "zip",
      "append", "pop", "split", "strip", "join", "replace", "count"
    ),
    commonSymbols = listOf(":", "=", "( )", "[ ]", "{ }", "\" \"", "' '", " + ", " - ", " * ", " / ", " == ", " != ", ", ", "    ")
  )

  val JAVASCRIPT = LanguageDefinition(
    id = "javascript",
    name = "JavaScript",
    displayName = "JavaScript",
    version = "ES2022",
    icon = "javascript",
    fileExtension = ".js",
    syntaxDefinition = "javascript",
    executionSupported = true,
    testingSupported = true,
    projectSupported = true,
    curriculumAvailable = true,
    starterTemplate = "console.log(\"Hello, World!\");\n",
    commentSyntax = "//",
    keywords = setOf(
      "break", "case", "catch", "class", "const", "continue", "debugger",
      "default", "delete", "do", "else", "export", "extends", "finally",
      "for", "function", "if", "import", "in", "instanceof", "let",
      "new", "return", "super", "switch", "this", "throw", "try",
      "typeof", "var", "void", "while", "with", "yield", "true", "false", "null", "undefined"
    ),
    builtins = setOf("console", "log", "Math", "JSON", "parseInt", "parseFloat", "Array", "Object", "String", "Number"),
    commonSymbols = listOf(";", "=", "( )", "{ }", "[ ]", "\" \"", "' '", " + ", " - ", " * ", " / ", " === ", " !== ", " => ", ", ")
  )

  val JAVA = LanguageDefinition(
    id = "java",
    name = "Java",
    displayName = "Java",
    version = "17",
    icon = "java",
    fileExtension = ".java",
    syntaxDefinition = "java",
    executionSupported = true,
    testingSupported = true,
    projectSupported = true,
    curriculumAvailable = true,
    starterTemplate = "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n",
    commentSyntax = "//",
    keywords = setOf("public", "class", "static", "void", "main", "int", "String", "boolean", "if", "else", "for", "while", "return", "new"),
    builtins = setOf("System", "out", "println", "print", "Math", "Scanner")
  )

  val C = LanguageDefinition(
    id = "c",
    name = "C",
    displayName = "C",
    version = "C11",
    icon = "c",
    fileExtension = ".c",
    syntaxDefinition = "c",
    executionSupported = true,
    testingSupported = true,
    projectSupported = true,
    curriculumAvailable = true,
    starterTemplate = "#include <stdio.h>\n\nint main() {\n    printf(\"Hello, World!\\n\");\n    return 0;\n}\n",
    commentSyntax = "//",
    keywords = setOf("int", "main", "return", "include", "if", "else", "for", "while", "char", "float", "double", "struct", "void"),
    builtins = setOf("printf", "scanf", "NULL", "size_t")
  )

  val CPP = LanguageDefinition(
    id = "cpp",
    name = "C++",
    displayName = "C++",
    version = "C++20",
    icon = "cpp",
    fileExtension = ".cpp",
    syntaxDefinition = "cpp",
    executionSupported = true,
    testingSupported = true,
    projectSupported = true,
    curriculumAvailable = true,
    starterTemplate = "#include <iostream>\n\nint main() {\n    std::cout << \"Hello, World!\\n\";\n    return 0;\n}\n",
    commentSyntax = "//",
    keywords = setOf("int", "main", "return", "include", "using", "namespace", "std", "if", "else", "for", "while", "cout", "cin", "endl"),
    builtins = setOf("cout", "cin", "endl", "vector", "string")
  )

  val SUPPORTED_LANGUAGES = listOf(PYTHON, JAVASCRIPT, JAVA, C, CPP)
  private val languages = SUPPORTED_LANGUAGES

  fun getLanguage(id: String): LanguageDefinition {
    return languages.find { it.id.equals(id, ignoreCase = true) }
      ?: if (id.equals("c", ignoreCase = true)) C else PYTHON
  }

  fun getAllLanguages(): List<LanguageDefinition> = languages

  fun getRuntime(id: String): com.example.domain.execution.CodeRuntime {
    return com.example.domain.execution.LanguageRuntimeFactory.getRuntime(id)
  }
}
