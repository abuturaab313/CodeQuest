package com.example.domain.learning

import com.example.data.models.CodingChallengeEntity
import com.example.data.models.DailyPracticeSessionEntity
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.UserMistakeEntity
import org.json.JSONArray
import org.json.JSONObject

data class PracticeStep(
  val stepIndex: Int,
  val title: String,
  val type: String, // "CONCEPT_CHECK", "BUG_FIX", "CODING_CHALLENGE", "MASTERY_DRILL"
  val description: String,
  val targetId: String? = null,
  val isCompleted: Boolean = false
)

class PersonalizedPracticeService {

  fun generateDailySession(
    epochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
    weakSkills: List<SkillMasteryEntity>,
    mistakes: List<UserMistakeEntity>,
    challenges: List<CodingChallengeEntity>
  ): DailyPracticeSessionEntity {
    val weakSkillName = weakSkills.firstOrNull()?.skillName ?: "Python Fundamentals"
    val mistake = mistakes.firstOrNull()
    val challenge = challenges.firstOrNull { !it.isCompleted } ?: challenges.firstOrNull()

    val steps = mutableListOf<JSONObject>()

    // Step 1: Weak Concept Quiz / Concept Check
    steps.add(JSONObject().apply {
      put("stepIndex", 0)
      put("title", "Concept Check: $weakSkillName")
      put("type", "CONCEPT_CHECK")
      put("description", "Quick intuitive review question with Code Coach")
      put("targetId", weakSkillName)
      put("isCompleted", false)
    })

    // Step 2: Bug Fix Drill
    steps.add(JSONObject().apply {
      put("stepIndex", 1)
      put("title", if (mistake != null) "Debug: ${mistake.topic}" else "Syntax & Error Diagnostic")
      put("type", "BUG_FIX")
      put("description", if (mistake != null) mistake.prompt.take(60) else "Analyze and fix a common code bug")
      put("targetId", mistake?.id ?: "sample_bug")
      put("isCompleted", false)
    })

    // Step 3: Coding Challenge
    steps.add(JSONObject().apply {
      put("stepIndex", 2)
      put("title", "Coding Challenge: ${challenge?.title ?: "Hello World"}")
      put("type", "CODING_CHALLENGE")
      put("description", challenge?.description?.take(60) ?: "Implement a solution and pass public test cases")
      put("targetId", challenge?.id ?: "py_c_hello_world")
      put("isCompleted", false)
    })

    // Step 4: Mastery Check
    steps.add(JSONObject().apply {
      put("stepIndex", 3)
      put("title", "Mastery Reflection & Code Review")
      put("type", "MASTERY_DRILL")
      put("description", "Review your solution structure with Code Coach for bonus XP")
      put("targetId", "mastery_check")
      put("isCompleted", false)
    })

    return DailyPracticeSessionEntity(
      epochDay = epochDay,
      title = "Daily Personalized Practice",
      description = "Personalized 4-step workout targeting $weakSkillName",
      isCompleted = false,
      totalSteps = 4,
      completedSteps = 0,
      xpReward = 150,
      coinReward = 40,
      practiceItemsJson = JSONArray(steps).toString()
    )
  }

  fun parseSteps(session: DailyPracticeSessionEntity): List<PracticeStep> {
    return try {
      val arr = JSONArray(session.practiceItemsJson)
      List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        PracticeStep(
          stepIndex = obj.optInt("stepIndex", i),
          title = obj.optString("title", "Practice Step ${i + 1}"),
          type = obj.optString("type", "PRACTICE"),
          description = obj.optString("description", ""),
          targetId = obj.optString("targetId", null),
          isCompleted = i < session.completedSteps || session.isCompleted
        )
      }
    } catch (e: Exception) {
      emptyList()
    }
  }
}
