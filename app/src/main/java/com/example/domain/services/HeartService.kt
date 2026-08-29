package com.example.domain.services

data class HeartRegenResult(
  val currentHearts: Int,
  val maxHearts: Int,
  val lastRegenEpochMs: Long,
  val didRegenerate: Boolean,
  val minutesUntilNextRegen: Int
)

class HeartService(
  val maxHearts: Int = 5,
  val regenIntervalMs: Long = 15 * 60 * 1000L // 15 minutes per heart
) {

  /**
   * Calculates regenerated hearts based on elapsed time since last regeneration.
   */
  fun calculateRegeneration(
    currentHearts: Int,
    lastRegenEpochMs: Long,
    currentTimeMs: Long = System.currentTimeMillis()
  ): HeartRegenResult {
    if (currentHearts >= maxHearts) {
      return HeartRegenResult(
        currentHearts = maxHearts,
        maxHearts = maxHearts,
        lastRegenEpochMs = currentTimeMs,
        didRegenerate = false,
        minutesUntilNextRegen = 0
      )
    }

    val elapsed = (currentTimeMs - lastRegenEpochMs).coerceAtLeast(0L)
    val regeneratedCount = (elapsed / regenIntervalMs).toInt()

    if (regeneratedCount > 0) {
      val newHearts = minOf(maxHearts, currentHearts + regeneratedCount)
      val newLastRegen = if (newHearts >= maxHearts) {
        currentTimeMs
      } else {
        lastRegenEpochMs + (regeneratedCount * regenIntervalMs)
      }
      val remainingMs = if (newHearts < maxHearts) {
        regenIntervalMs - ((currentTimeMs - newLastRegen) % regenIntervalMs)
      } else {
        0L
      }

      return HeartRegenResult(
        currentHearts = newHearts,
        maxHearts = maxHearts,
        lastRegenEpochMs = newLastRegen,
        didRegenerate = true,
        minutesUntilNextRegen = (remainingMs / 60000L).toInt()
      )
    } else {
      val remainingMs = regenIntervalMs - elapsed
      return HeartRegenResult(
        currentHearts = currentHearts,
        maxHearts = maxHearts,
        lastRegenEpochMs = lastRegenEpochMs,
        didRegenerate = false,
        minutesUntilNextRegen = (remainingMs / 60000L).toInt().coerceAtLeast(1)
      )
    }
  }

  /**
   * Consumes a single heart on failed submission during normal challenges.
   */
  fun consumeHeart(currentHearts: Int): Pair<Boolean, Int> {
    return if (currentHearts > 0) {
      Pair(true, currentHearts - 1)
    } else {
      Pair(false, 0)
    }
  }

  /**
   * Restores full hearts (e.g. daily reward, leveling up, or practice milestone).
   */
  fun restoreFullHearts(): Int {
    return maxHearts
  }
}
