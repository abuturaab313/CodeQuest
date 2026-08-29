package com.example.domain.ai

import android.util.Log
import com.example.BuildConfig
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.AIResponse
import com.example.domain.ai.models.LearningContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAIProvider(
  private val fallbackProvider: AIProvider = IntelligentLocalAIProvider()
) : AIProvider {

  override val providerName: String = "Gemini AI"

  override val isAvailable: Boolean
    get() {
      val key = getApiKey()
      return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

  private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  private fun getApiKey(): String {
    return try {
      BuildConfig.GEMINI_API_KEY
    } catch (_: Exception) {
      ""
    }
  }

  override suspend fun generateMentorResponse(
    mode: AIMentorMode,
    context: LearningContext
  ): Result<AIResponse> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (!isAvailable) {
      Log.d("GeminiAIProvider", "Gemini API key not set; using local intelligent coach fallback.")
      return@withContext fallbackProvider.generateMentorResponse(mode, context)
    }

    try {
      val prompt = MentorPrompts.buildPrompt(mode, context)
      val requestJson = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          val contentObj = JSONObject().apply {
            val partsArray = JSONArray().apply {
              put(JSONObject().put("text", prompt))
            }
            put("parts", partsArray)
          }
          put(contentObj)
        }
        put("contents", contentsArray)
        
        val genConfig = JSONObject().apply {
          put("temperature", 0.4)
          put("maxOutputTokens", 800)
        }
        put("generationConfig", genConfig)
      }

      val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

      val httpRequest = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      val response = httpClient.newCall(httpRequest).execute()
      val responseBodyString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        Log.w("GeminiAIProvider", "Gemini request failed (HTTP ${response.code}): $responseBodyString. Falling back to local coach.")
        return@withContext fallbackProvider.generateMentorResponse(mode, context)
      }

      val parsedResponse = parseGeminiResponse(responseBodyString, mode, context)
      Result.success(parsedResponse)
    } catch (e: Exception) {
      Log.e("GeminiAIProvider", "Error in Gemini call: ${e.message}. Using fallback provider.", e)
      fallbackProvider.generateMentorResponse(mode, context)
    }
  }

  private suspend fun parseGeminiResponse(
    jsonString: String,
    mode: AIMentorMode,
    context: LearningContext
  ): AIResponse {
    try {
      val root = JSONObject(jsonString)
      val candidates = root.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text") ?: ""

      if (text.isBlank()) {
        return fallbackProvider.generateMentorResponse(mode, context).getOrNull()
          ?: AIResponse(mode = mode, headline = "Code Coach Insight", rawMarkdown = "Keep experimenting and check variable types!")
      }

      // Parse structured sections
      var whatsWrong: String? = null
      var why: String? = null
      var tryThis: String? = null
      var thinkAbout: String? = null
      var nextStep: String? = null

      val lines = text.lines()
      var currentSection = ""
      val sectionBuffers = mutableMapOf<String, StringBuilder>()

      for (line in lines) {
        val trimmed = line.trim()
        when {
          trimmed.startsWith("WHAT'S WRONG:", ignoreCase = true) || trimmed.startsWith("WHATS WRONG:", ignoreCase = true) -> {
            currentSection = "WHAT"
            sectionBuffers.getOrPut(currentSection) { StringBuilder() }.append(trimmed.substringAfter(":", "").trim())
          }
          trimmed.startsWith("WHY:", ignoreCase = true) -> {
            currentSection = "WHY"
            sectionBuffers.getOrPut(currentSection) { StringBuilder() }.append(trimmed.substringAfter(":", "").trim())
          }
          trimmed.startsWith("TRY THIS:", ignoreCase = true) -> {
            currentSection = "TRY"
            sectionBuffers.getOrPut(currentSection) { StringBuilder() }.append(trimmed.substringAfter(":", "").trim())
          }
          trimmed.startsWith("THINK ABOUT:", ignoreCase = true) -> {
            currentSection = "THINK"
            sectionBuffers.getOrPut(currentSection) { StringBuilder() }.append(trimmed.substringAfter(":", "").trim())
          }
          trimmed.startsWith("OPTIONAL NEXT STEP:", ignoreCase = true) || trimmed.startsWith("NEXT STEP:", ignoreCase = true) -> {
            currentSection = "NEXT"
            sectionBuffers.getOrPut(currentSection) { StringBuilder() }.append(trimmed.substringAfter(":", "").trim())
          }
          currentSection.isNotEmpty() -> {
            sectionBuffers[currentSection]?.append("\n")?.append(line)
          }
        }
      }

      whatsWrong = sectionBuffers["WHAT"]?.toString()?.trim()?.takeIf { it.isNotBlank() }
      why = sectionBuffers["WHY"]?.toString()?.trim()?.takeIf { it.isNotBlank() }
      tryThis = sectionBuffers["TRY"]?.toString()?.trim()?.takeIf { it.isNotBlank() }
      thinkAbout = sectionBuffers["THINK"]?.toString()?.trim()?.takeIf { it.isNotBlank() }
      nextStep = sectionBuffers["NEXT"]?.toString()?.trim()?.takeIf { it.isNotBlank() }

      return AIResponse(
        mode = mode,
        headline = "Code Coach (${mode.title})",
        whatsWrong = whatsWrong,
        why = why,
        tryThis = tryThis,
        thinkAbout = thinkAbout,
        optionalNextStep = nextStep,
        rawMarkdown = text,
        hintLevel = context.hintLevelRequested,
        maxHintLevel = 5,
        providerUsed = "Gemini 3.5 Flash"
      )
    } catch (_: Exception) {
      return AIResponse(
        mode = mode,
        headline = "Code Coach (${mode.title})",
        rawMarkdown = jsonString,
        providerUsed = "Gemini 3.5 Flash"
      )
    }
  }
}
