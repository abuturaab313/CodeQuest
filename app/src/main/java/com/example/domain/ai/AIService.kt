package com.example.domain.ai

import com.example.data.local.LearnerMemoryDao
import com.example.data.models.AIFeedbackEntity
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.AIResponse
import com.example.domain.ai.models.LearningContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

sealed class AIRequestState {
  data object Idle : AIRequestState()
  data class Loading(val message: String = "Analyzing your code...") : AIRequestState()
  data class Success(val response: AIResponse) : AIRequestState()
  data class Error(val message: String, val canRetry: Boolean = true) : AIRequestState()
}

class AIService(
  private val primaryProvider: AIProvider = GeminiAIProvider(),
  private val fallbackProvider: AIProvider = IntelligentLocalAIProvider(),
  private val contextManager: ContextManager = ContextManager(),
  private val rateLimiter: AIRateLimiter = AIRateLimiter(),
  private val memoryDao: LearnerMemoryDao? = null
) {
  private val _requestState = MutableStateFlow<AIRequestState>(AIRequestState.Idle)
  val requestState: StateFlow<AIRequestState> = _requestState.asStateFlow()

  // In-memory response cache for repeated identical questions
  private val responseCache = ConcurrentHashMap<String, AIResponse>()

  fun resetState() {
    _requestState.value = AIRequestState.Idle
  }

  suspend fun requestMentorGuidance(
    mode: AIMentorMode,
    rawContext: LearningContext,
    workspaceFiles: Map<String, String> = emptyMap()
  ): Result<AIResponse> {
    // 1. Check Rate Limit
    val limitCheck = rateLimiter.checkRateLimit(mode.name)
    if (!limitCheck.isAllowed) {
      val errorMsg = limitCheck.reason ?: "Please wait before making another request."
      _requestState.value = AIRequestState.Error(errorMsg, canRetry = true)
      return Result.failure(Exception(errorMsg))
    }

    // 2. Set Loading State with contextual message
    val loadingMsg = when (mode) {
      AIMentorMode.DEBUG -> "Analyzing your runtime error and code structure..."
      AIMentorMode.HINT -> "Crafting a Level ${rawContext.hintLevelRequested} nudge..."
      AIMentorMode.REVIEW -> "Reviewing code style and best practices..."
      AIMentorMode.EXPLAIN -> "Preparing concept breakdown..."
      AIMentorMode.QUIZ -> "Generating knowledge check..."
      AIMentorMode.CONCEPT -> "Preparing concept coach guide..."
    }
    _requestState.value = AIRequestState.Loading(loadingMsg)

    // 3. Build & Sanitize Context
    val sanitizedContext = contextManager.buildSanitizedContext(rawContext, workspaceFiles)

    // Check cache
    val cacheKey = "${mode.name}_${sanitizedContext.activeConcept}_${sanitizedContext.hintLevelRequested}_${sanitizedContext.currentCode.hashCode()}_${sanitizedContext.recentError.hashCode()}"
    responseCache[cacheKey]?.let { cached ->
      _requestState.value = AIRequestState.Success(cached)
      return Result.success(cached)
    }

    // 4. Try Primary Provider, then Fallback
    val providerToUse = if (primaryProvider.isAvailable) primaryProvider else fallbackProvider
    var result = providerToUse.generateMentorResponse(mode, sanitizedContext)

    if (result.isFailure && providerToUse != fallbackProvider) {
      // Fallback to local
      result = fallbackProvider.generateMentorResponse(mode, sanitizedContext)
    }

    return result.fold(
      onSuccess = { response ->
        responseCache[cacheKey] = response
        _requestState.value = AIRequestState.Success(response)
        
        // Log interaction asynchronously to memory
        recordInteraction(sanitizedContext, mode, response)
        Result.success(response)
      },
      onFailure = { error ->
        val userFriendlyMessage = "Code Coach is temporarily unavailable: ${error.message ?: "Network error"}"
        _requestState.value = AIRequestState.Error(userFriendlyMessage, canRetry = true)
        Result.failure(error)
      }
    )
  }

  suspend fun submitFeedback(
    contextKey: String,
    mode: String,
    userQuery: String,
    responseSummary: String,
    wasHelpful: Boolean
  ) {
    try {
      memoryDao?.insertAIFeedback(
        AIFeedbackEntity(
          promptMode = mode,
          contextTopic = responseSummary.take(200),
          contextKey = contextKey,
          wasHelpful = wasHelpful,
          problemSolvedAfter = false
        )
      )
    } catch (_: Exception) {
      // Non-blocking logging
    }
  }

  private suspend fun recordInteraction(
    context: LearningContext,
    mode: AIMentorMode,
    response: AIResponse
  ) {
    try {
      memoryDao?.insertAIFeedback(
        AIFeedbackEntity(
          promptMode = mode.name,
          contextTopic = response.headline + " | " + (response.whatsWrong ?: ""),
          contextKey = context.getPrimaryContextLabel(),
          wasHelpful = false,
          problemSolvedAfter = false
        )
      )
    } catch (_: Exception) {
      // Best-effort recording
    }
  }
}
