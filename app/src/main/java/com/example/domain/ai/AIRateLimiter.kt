package com.example.domain.ai

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class AIRateLimiter(
  private val maxRequestsPerMinute: Int = 15,
  private val minIntervalMs: Long = 1500L
) {
  private val requestTimestamps = ConcurrentHashMap<String, MutableList<Long>>()
  private val lastRequestTime = ConcurrentHashMap<String, Long>()
  private val totalRequestsCount = AtomicInteger(0)

  data class RateLimitResult(
    val isAllowed: Boolean,
    val reason: String? = null,
    val retryAfterSeconds: Int = 0
  )

  /**
   * Checks whether an AI call is permitted for this user & feature key.
   */
  fun checkRateLimit(featureKey: String = "default"): RateLimitResult {
    val now = System.currentTimeMillis()
    val lastTime = lastRequestTime[featureKey] ?: 0L

    if (now - lastTime < minIntervalMs) {
      val waitSec = ((minIntervalMs - (now - lastTime)) / 1000).toInt() + 1
      return RateLimitResult(
        isAllowed = false,
        reason = "Please pause a moment before requesting another hint.",
        retryAfterSeconds = waitSec
      )
    }

    val timestamps = requestTimestamps.computeIfAbsent(featureKey) { mutableListOf() }
    synchronized(timestamps) {
      // Remove timestamps older than 60 seconds
      timestamps.removeAll { now - it > 60_000L }

      if (timestamps.size >= maxRequestsPerMinute) {
        val oldest = timestamps.firstOrNull() ?: now
        val waitSec = (((oldest + 60_000L) - now) / 1000).toInt().coerceAtLeast(1)
        return RateLimitResult(
          isAllowed = false,
          reason = "Coach is cooling down. Please try again in $waitSec seconds.",
          retryAfterSeconds = waitSec
        )
      }

      timestamps.add(now)
      lastRequestTime[featureKey] = now
      totalRequestsCount.incrementAndGet()
    }

    return RateLimitResult(isAllowed = true)
  }

  fun resetLimits() {
    requestTimestamps.clear()
    lastRequestTime.clear()
  }
}
