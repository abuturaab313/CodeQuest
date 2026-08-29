package com.example.domain.ai

import java.util.concurrent.ConcurrentLinkedQueue

class RateLimiter(
  private val maxRequestsPerMinute: Int = 12,
  private val maxRequestsPerHour: Int = 120
) {
  private val minuteWindow = ConcurrentLinkedQueue<Long>()
  private val hourWindow = ConcurrentLinkedQueue<Long>()

  @Synchronized
  fun acquirePermission(): RateLimitResult {
    val now = System.currentTimeMillis()
    val oneMinuteAgo = now - 60_000L
    val oneHourAgo = now - 3600_000L

    // Clean up expired timestamps
    while (minuteWindow.peek()?.let { it < oneMinuteAgo } == true) {
      minuteWindow.poll()
    }
    while (hourWindow.peek()?.let { it < oneHourAgo } == true) {
      hourWindow.poll()
    }

    if (minuteWindow.size >= maxRequestsPerMinute) {
      val oldest = minuteWindow.peek() ?: now
      val waitSeconds = ((oldest + 60_000L - now) / 1000L).coerceAtLeast(1)
      return RateLimitResult.RateLimited(
        reason = "Too many requests in a short period. Please wait $waitSeconds seconds.",
        retryAfterSeconds = waitSeconds
      )
    }

    if (hourWindow.size >= maxRequestsPerHour) {
      return RateLimitResult.RateLimited(
        reason = "Hourly coaching limit reached. Feel free to continue practicing with built-in hints!",
        retryAfterSeconds = 60
      )
    }

    minuteWindow.offer(now)
    hourWindow.offer(now)
    return RateLimitResult.Allowed
  }

  fun reset() {
    minuteWindow.clear()
    hourWindow.clear()
  }

sealed class RateLimitResult {
  object Allowed : RateLimitResult()
  data class RateLimited(val reason: String, val retryAfterSeconds: Long) : RateLimitResult()
}
}
