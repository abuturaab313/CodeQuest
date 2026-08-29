package com.example.domain.ai

import com.example.domain.ai.models.LearningContext

class ContextManager {

  companion object {
    private const val MAX_CODE_LENGTH = 1800
    private const val MAX_FILE_CHARS = 1000
  }

  fun buildSanitizedContext(
    rawContext: LearningContext,
    workspaceFiles: Map<String, String> = emptyMap()
  ): LearningContext {
    return rawContext.copy(
      currentCode = trimCode(rawContext.currentCode, MAX_CODE_LENGTH),
      recentError = sanitizeError(rawContext.recentError),
      stderr = sanitizeError(rawContext.stderr),
      stdout = sanitizeError(rawContext.stdout),
      relevantFileSnippets = trimFiles(workspaceFiles)
    )
  }

  private fun trimCode(code: String?, maxLen: Int = MAX_CODE_LENGTH): String {
    if (code.isNullOrBlank()) return ""
    return if (code.length <= maxLen) code
    else "...(truncated)\n" + code.takeLast(maxLen)
  }

  private fun sanitizeError(error: String?): String? {
    if (error.isNullOrBlank()) return null
    // Remove internal system paths or long stack traces
    var clean = error.take(500)
    if (clean.contains("/data/data/") || clean.contains("/var/lib/")) {
      clean = clean.replace(Regex("/[a-zA-Z0-9_/\\\\-\\.]+"), "[SYSTEM_PATH]")
    }
    return clean
  }

  private fun trimFiles(files: Map<String, String>): Map<String, String> {
    return files.mapValues { (_, content) ->
      if (content.length > MAX_FILE_CHARS) {
        "...(truncated)\n" + content.takeLast(MAX_FILE_CHARS)
      } else {
        content
      }
    }
  }
}
