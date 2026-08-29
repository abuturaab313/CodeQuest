package com.example.domain.ai

import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.AIResponse
import com.example.domain.ai.models.LearningContext

interface AIProvider {
  val providerName: String
  val isAvailable: Boolean
  suspend fun generateMentorResponse(mode: AIMentorMode, context: LearningContext): Result<AIResponse>
}
