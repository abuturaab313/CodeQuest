package com.example.domain.services

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class DayActivity(
  val dayShortName: String, // MON, TUE, WED, THU, FRI, SAT, SUN
  val dayNumber: Int,
  val isCompleted: Boolean,
  val isToday: Boolean,
  val isPast: Boolean
)

data class StreakUpdateResult(
  val newStreak: Int,
  val newLongestStreak: Int,
  val todayEpochDay: Long,
  val isExtendedToday: Boolean,
  val isMilestone: Boolean
)

class StreakService {

  fun getTodayEpochDay(): Long {
    return LocalDate.now().toEpochDay()
  }

  /**
   * Processes meaningful learning activity to update streaks.
   */
  fun recordActivity(
    currentStreak: Int,
    longestStreak: Int,
    lastActiveEpochDay: Long,
    todayEpochDay: Long = getTodayEpochDay()
  ): StreakUpdateResult {
    return when {
      // Already recorded activity today
      lastActiveEpochDay == todayEpochDay -> {
        StreakUpdateResult(
          newStreak = currentStreak,
          newLongestStreak = maxOf(longestStreak, currentStreak),
          todayEpochDay = todayEpochDay,
          isExtendedToday = false,
          isMilestone = isMilestoneStreak(currentStreak)
        )
      }
      // Consecutive day (yesterday was last active)
      lastActiveEpochDay == todayEpochDay - 1 -> {
        val updatedStreak = currentStreak + 1
        StreakUpdateResult(
          newStreak = updatedStreak,
          newLongestStreak = maxOf(longestStreak, updatedStreak),
          todayEpochDay = todayEpochDay,
          isExtendedToday = true,
          isMilestone = isMilestoneStreak(updatedStreak)
        )
      }
      // Streak broken (missed more than 1 day)
      else -> {
        val resetStreak = 1
        StreakUpdateResult(
          newStreak = resetStreak,
          newLongestStreak = maxOf(longestStreak, resetStreak),
          todayEpochDay = todayEpochDay,
          isExtendedToday = true,
          isMilestone = false
        )
      }
    }
  }

  /**
   * Generates a 7-day visualization (Monday to Sunday) for the current week.
   */
  fun getWeeklyActivity(
    lastActiveEpochDay: Long,
    currentStreak: Int,
    referenceDate: LocalDate = LocalDate.now()
  ): List<DayActivity> {
    val monday = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val todayEpoch = referenceDate.toEpochDay()
    val days = mutableListOf<DayActivity>()

    for (i in 0..6) {
      val dayDate = monday.plusDays(i.toLong())
      val dayEpoch = dayDate.toEpochDay()
      val isToday = dayEpoch == todayEpoch
      val isPast = dayEpoch < todayEpoch

      // If day is within the active streak range ending on lastActiveEpochDay
      val isCompleted = if (lastActiveEpochDay > 0) {
        val streakStartEpoch = lastActiveEpochDay - (currentStreak - 1).coerceAtLeast(0)
        dayEpoch in streakStartEpoch..lastActiveEpochDay
      } else {
        false
      }

      val shortName = when (dayDate.dayOfWeek) {
        DayOfWeek.MONDAY -> "MON"
        DayOfWeek.TUESDAY -> "TUE"
        DayOfWeek.WEDNESDAY -> "WED"
        DayOfWeek.THURSDAY -> "THU"
        DayOfWeek.FRIDAY -> "FRI"
        DayOfWeek.SATURDAY -> "SAT"
        DayOfWeek.SUNDAY -> "SUN"
      }

      days.add(
        DayActivity(
          dayShortName = shortName,
          dayNumber = dayDate.dayOfMonth,
          isCompleted = isCompleted,
          isToday = isToday,
          isPast = isPast
        )
      )
    }

    return days
  }

  fun isMilestoneStreak(streak: Int): Boolean {
    return streak in listOf(3, 7, 14, 30, 50, 100, 365)
  }
}
