package com.example.domain.services

data class DocCheckItem(
  val sectionName: String,
  val isPresent: Boolean,
  val description: String,
  val feedback: String
)

data class DocumentationQualityResult(
  val scorePercent: Int,
  val status: String,
  val checks: List<DocCheckItem>,
  val suggestions: List<String>
)

object DocumentationValidator {

  fun evaluateReadme(readmeText: String): DocumentationQualityResult {
    val trimmed = readmeText.trim()
    if (trimmed.isBlank()) {
      return DocumentationQualityResult(
        scorePercent = 0,
        status = "Empty Documentation",
        checks = emptyList(),
        suggestions = listOf("Add project title, description, features, installation, and usage guide.")
      )
    }

    val lower = trimmed.lowercase()

    val hasTitle = trimmed.startsWith("#") || lower.contains("# project") || lower.contains("title")
    val hasDescription = lower.contains("overview") || lower.contains("description") || lower.contains("about")
    val hasFeatures = lower.contains("features") || lower.contains("capabilities") || lower.contains("requirements")
    val hasInstallation = lower.contains("installation") || lower.contains("setup") || lower.contains("install")
    val hasUsage = lower.contains("usage") || lower.contains("run") || lower.contains("quickstart") || lower.contains("example")
    val hasTests = lower.contains("test") || lower.contains("testing") || lower.contains("pytest")
    val hasAuthor = lower.contains("author") || lower.contains("license") || lower.contains("credits")

    val checks = listOf(
      DocCheckItem(
        sectionName = "Project Title & Header",
        isPresent = hasTitle,
        description = "Identifies the project clearly with H1 heading.",
        feedback = if (hasTitle) "Clear project heading present." else "Missing top-level project header (# Title)."
      ),
      DocCheckItem(
        sectionName = "Description & Overview",
        isPresent = hasDescription,
        description = "Explains the problem solved and core purpose.",
        feedback = if (hasDescription) "Project purpose is explained." else "Add a concise description explaining what the app does."
      ),
      DocCheckItem(
        sectionName = "Features List",
        isPresent = hasFeatures,
        description = "Outlines key functional capabilities.",
        feedback = if (hasFeatures) "Features are documented." else "Include bullet points listing primary features."
      ),
      DocCheckItem(
        sectionName = "Installation / Setup",
        isPresent = hasInstallation,
        description = "Provides step-by-step setup instructions.",
        feedback = if (hasInstallation) "Installation steps provided." else "Add installation and environment requirements."
      ),
      DocCheckItem(
        sectionName = "Usage Guide",
        isPresent = hasUsage,
        description = "Demonstrates how to run and use the project.",
        feedback = if (hasUsage) "Usage guidelines included." else "Include practical CLI or GUI run examples."
      ),
      DocCheckItem(
        sectionName = "Testing Instructions",
        isPresent = hasTests,
        description = "Details how to execute automated test suites.",
        feedback = if (hasTests) "Testing instructions provided." else "Explain how to run tests (e.g. pytest, npm test)."
      ),
      DocCheckItem(
        sectionName = "Author & License",
        isPresent = hasAuthor,
        description = "Lists ownership, attribution, and license info.",
        feedback = if (hasAuthor) "Author attribution present." else "Consider adding author and license information."
      )
    )

    val passedCount = checks.count { it.isPresent }
    val score = (passedCount.toDouble() / checks.size.toDouble() * 100).toInt()

    val status = when {
      score >= 85 -> "Production Ready"
      score >= 60 -> "Good Draft"
      else -> "Needs Expansion"
    }

    val suggestions = checks.filter { !it.isPresent }.map { it.feedback }

    return DocumentationQualityResult(
      scorePercent = score,
      status = status,
      checks = checks,
      suggestions = suggestions
    )
  }
}
