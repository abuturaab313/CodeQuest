package com.example.domain.learning

data class ProgressiveHint(
  val level: Int,
  val title: String,
  val hintText: String,
  val xpPenalty: Int
)

class HintService {

  /**
   * Constructs progressive hint levels from a list of hint strings and explanation.
   * Level 1: Conceptual Clue
   * Level 2: Specific Guidance
   * Level 3: Code/Syntax Approach
   * Level 4: Full Solution & Explanation
   */
  fun buildProgressiveHints(
    rawHints: List<String>,
    explanation: String = ""
  ): List<ProgressiveHint> {
    val hintsList = mutableListOf<ProgressiveHint>()

    if (rawHints.isNotEmpty()) {
      hintsList.add(
        ProgressiveHint(
          level = 1,
          title = "Level 1: Concept Clue",
          hintText = rawHints[0],
          xpPenalty = 3
        )
      )
    }

    if (rawHints.size >= 2) {
      hintsList.add(
        ProgressiveHint(
          level = 2,
          title = "Level 2: Specific Clue",
          hintText = rawHints[1],
          xpPenalty = 5
        )
      )
    }

    if (rawHints.size >= 3) {
      hintsList.add(
        ProgressiveHint(
          level = 3,
          title = "Level 3: Syntax Approach",
          hintText = rawHints[2],
          xpPenalty = 8
        )
      )
    }

    if (explanation.isNotBlank()) {
      hintsList.add(
        ProgressiveHint(
          level = hintsList.size + 1,
          title = "Full Explanation",
          hintText = explanation,
          xpPenalty = 10
        )
      )
    }

    if (hintsList.isEmpty()) {
      hintsList.add(
        ProgressiveHint(
          level = 1,
          title = "General Hint",
          hintText = "Read the instructions carefully and review the expected data types and syntax.",
          xpPenalty = 0
        )
      )
    }

    return hintsList
  }

  /**
   * Calculates total XP penalty based on hints accessed.
   * Never drops base lesson XP below half.
   */
  fun calculateHintXpDeduction(hintsUsedCount: Int, baseReward: Int): Int {
    val rawDeduction = hintsUsedCount * 4
    val maxDeduction = (baseReward * 0.4).toInt()
    return minOf(rawDeduction, maxDeduction)
  }
}
