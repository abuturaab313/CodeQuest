package com.example.domain.services

data class SecretFinding(
  val fileName: String,
  val lineNumber: Int,
  val patternType: String,
  val lineSnippet: String
)

data class SecretScanResult(
  val hasSecrets: Boolean,
  val findings: List<SecretFinding>,
  val warningMessage: String?
)

object SecretScanner {

  private val SENSITIVE_PATTERNS = listOf(
    Regex("(?i)api[_-]?key\\s*=\\s*['\"][a-zA-Z0-9_\\-]{8,}['\"]") to "Hardcoded API Key",
    Regex("(?i)secret\\s*=\\s*['\"][a-zA-Z0-9_\\-]{8,}['\"]") to "Hardcoded Secret",
    Regex("(?i)password\\s*=\\s*['\"][^'\"\\s]{6,}['\"]") to "Hardcoded Password",
    Regex("(?i)token\\s*=\\s*['\"][a-zA-Z0-9_\\-]{8,}['\"]") to "Hardcoded Auth Token",
    Regex("AIzaSy[a-zA-Z0-9_\\-]{33}") to "Google Cloud API Key",
    Regex("ghp_[a-zA-Z0-9]{36}") to "GitHub Personal Access Token",
    Regex("sk_live_[a-zA-Z0-9]{24}") to "Stripe Live Secret Key"
  )

  fun scanFiles(files: Map<String, String>): SecretScanResult {
    val findings = mutableListOf<SecretFinding>()

    for ((fileName, content) in files) {
      val lines = content.lines()
      for ((index, line) in lines.withIndex()) {
        for ((regex, typeName) in SENSITIVE_PATTERNS) {
          if (regex.containsMatchIn(line)) {
            findings.add(
              SecretFinding(
                fileName = fileName,
                lineNumber = index + 1,
                patternType = typeName,
                lineSnippet = line.trim()
              )
            )
          }
        }
      }
    }

    val hasSecrets = findings.isNotEmpty()
    val warning = if (hasSecrets) {
      "Your project may contain sensitive credentials or API keys (${findings.size} finding(s) detected). Please review before exporting!"
    } else null

    return SecretScanResult(
      hasSecrets = hasSecrets,
      findings = findings,
      warningMessage = warning
    )
  }
}
