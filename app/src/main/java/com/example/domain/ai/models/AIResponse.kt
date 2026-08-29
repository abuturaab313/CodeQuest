package com.example.domain.ai.models

import com.example.data.models.AIQuizQuestion

data class AIResponse(
  val mode: AIMentorMode,
  val headline: String,
  val whatsWrong: String? = null,
  val why: String? = null,
  val tryThis: String? = null,
  val thinkAbout: String? = null,
  val optionalNextStep: String? = null,
  val codeExample: String? = null,
  val hintLevel: Int = 1,
  val maxHintLevel: Int = 5,
  val mustFixItems: List<String> = emptyList(),
  val optionalImprovementItems: List<String> = emptyList(),
  val quizQuestions: List<AIQuizQuestion> = emptyList(),
  val rawMarkdown: String = "",
  val isAIGenerated: Boolean = true,
  val isPracticeOnly: Boolean = true,
  val providerUsed: String = "CodeCoach"
) {
  fun formatStructuredSummary(): String {
    val sb = StringBuilder()
    if (!whatsWrong.isNullOrBlank()) {
      sb.append("⚠️ **WHAT'S WRONG:**\n").append(whatsWrong).append("\n\n")
    }
    if (!why.isNullOrBlank()) {
      sb.append("💡 **WHY:**\n").append(why).append("\n\n")
    }
    if (!tryThis.isNullOrBlank()) {
      sb.append("🛠️ **TRY THIS:**\n").append(tryThis).append("\n\n")
    }
    if (!thinkAbout.isNullOrBlank()) {
      sb.append("🤔 **THINK ABOUT:**\n").append(thinkAbout).append("\n\n")
    }
    if (!optionalNextStep.isNullOrBlank()) {
      sb.append("🚀 **OPTIONAL NEXT STEP:**\n").append(optionalNextStep).append("\n")
    }
    if (sb.isEmpty()) {
      return rawMarkdown
    }
    return sb.toString().trim()
  }
}
