package com.example.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AchievementEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.LessonEntity
import com.example.data.models.UserEntity
import com.example.domain.services.DayActivity
import com.example.domain.services.PlayerProgress
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestIndigo
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.QuestPrimaryLight
import com.example.ui.theme.QuestSecondary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.XpGold

@Composable
fun HomeScreen(
  playerProgress: PlayerProgress?,
  user: UserEntity?,
  dailyQuests: List<DailyQuestEntity>,
  achievements: List<AchievementEntity>,
  onContinueLearning: (LessonEntity?) -> Unit,
  onStartDailyChallenge: () -> Unit,
  onClaimQuest: (DailyQuestEntity) -> Unit,
  onOpenSettings: () -> Unit,
  onNavigateToProfile: () -> Unit,
  recommendations: List<com.example.data.models.LearningRecommendation> = emptyList(),
  dailyPracticeState: com.example.data.models.DailyPracticeSessionEntity? = null,
  onOpenCodeCoach: (com.example.domain.ai.models.LearningContext) -> Unit = {},
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // Player Header Bar with Avatar, Level, XP Bar & Settings
      PlayerHeaderSection(
        progress = playerProgress,
        user = user,
        onOpenSettings = onOpenSettings
      )
    }

    // Modern AAA Game Stats Row: Hearts, Streaks, and Coins
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Hearts Card
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = borderStroke(HeartRose.copy(alpha = 0.4f), 1.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = "Hearts",
              tint = HeartRose,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${user?.currentHearts ?: 5}/5 HEARTS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = HeartRose
              )
              Text(
                text = if ((user?.currentHearts ?: 5) < 5) "Refilling..." else "Full Power",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Streak Card
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = borderStroke(StreakFlame.copy(alpha = 0.4f), 1.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = "Streak",
              tint = StreakFlame,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${user?.streakDays ?: 1} DAYS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = StreakFlame
              )
              Text(
                text = "Streak active",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Coins Card
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = borderStroke(XpGold.copy(alpha = 0.4f), 1.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Toll,
              contentDescription = "Coins",
              tint = XpGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${user?.coins ?: 100} COINS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = XpGold
              )
              Text(
                text = "Shop Available",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // "One Next Step" Continue Learning Hero Card - Maximum visual dominance
    item {
      ContinueLearningHeroCard(
        user = user,
        progress = playerProgress,
        onContinue = { onContinueLearning(playerProgress?.currentLesson) }
      )
    }

    // Active Project Progression Card
    item {
      ActiveProjectTrackerCard(
        language = user?.selectedLanguage ?: "python"
      )
    }

    // Adaptive AI Coach Recommendation Banner
    recommendations.firstOrNull()?.let { rec ->
      item {
        GameCard(
          borderColor = QuestPrimary.copy(alpha = 0.5f),
          onClick = {
            val ctx = com.example.domain.ai.models.LearningContext(
              sourceScreen = "HOME",
              activeConcept = rec.targetId
            )
            onOpenCodeCoach(ctx)
          }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(QuestPrimary.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AI COACH TIP",
                  style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Black)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = QuestSuccess.copy(alpha = 0.15f)) {
                  Text(
                    text = rec.type.name.replace('_', ' '),
                    style = MaterialTheme.typography.labelSmall.copy(color = QuestSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }

              Text(
                text = rec.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = rec.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
              onClick = {
                val ctx = com.example.domain.ai.models.LearningContext(
                  sourceScreen = "HOME",
                  activeConcept = rec.targetId
                )
                onOpenCodeCoach(ctx)
              },
              colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("Let's Go", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }

    item {
      // Streak & 7-Day Activity Matrix
      StreakWeeklyCard(progress = playerProgress)
    }

    item {
      // Daily Code Challenge
      DailyChallengeCard(onStart = onStartDailyChallenge)
    }

    item {
      // Daily Quests Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Today's Quests",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Resets Daily",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Daily Quests List
    items(dailyQuests) { quest ->
      QuestCard(quest = quest, onClaim = { onClaimQuest(quest) })
    }

    // Recent Achievements Section
    item {
      RecentAchievementsSection(
        achievements = achievements,
        onViewAll = onNavigateToProfile
      )
    }

    item {
      // Player Profile Summary Card
      PlayerProfileSummaryCard(
        progress = playerProgress,
        onViewProfile = onNavigateToProfile
      )
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

// Utility border generator
private fun borderStroke(color: Color, width: androidx.compose.ui.unit.Dp): androidx.compose.foundation.BorderStroke {
  return androidx.compose.foundation.BorderStroke(width, color)
}

@Composable
private fun ActiveProjectTrackerCard(language: String) {
  val (projectTitle, projectDesc) = when (language.lowercase()) {
    "python" -> Pair("Smart Terminal RPG Battle Simulator", "Construct an interactive CLI adventure featuring automated combat loops and item stores.")
    else -> Pair("Interactive Code Playground App", "Build your customized code sandbox workspace to write clean logic.")
  }

  GameCard(borderColor = Color(0xFFFF9800).copy(alpha = 0.4f)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(Color(0xFFE65100).copy(alpha = 0.08f), Color(0xFFFF9800).copy(alpha = 0.15f))
          )
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFF9800).copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "BUILD CAPSTONE PROJECT",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFFEF6C00),
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black
                )
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = projectTitle,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = projectDesc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF9800))
            .clickable { /* Handled in workspace tab */ },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = "Terminal",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun PlayerHeaderSection(
  progress: PlayerProgress?,
  user: UserEntity?,
  onOpenSettings: () -> Unit
) {
  val level = progress?.level ?: 1
  val currentXpInLevel = progress?.currentLevelXp ?: 0
  val xpNeededForNext = progress?.xpNeededForNextLevel ?: 100
  val progressPercent = progress?.levelProgressPercent ?: 0f

  val animatedProgress by animateFloatAsState(
    targetValue = progressPercent.coerceIn(0f, 1f),
    label = "xp_bar_anim"
  )

  GameCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Avatar + User Info
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(QuestPrimary.copy(alpha = 0.15f))
              .border(2.dp, QuestPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Avatar",
              tint = QuestPrimary,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = user?.username ?: "Code Quester",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = QuestPrimary.copy(alpha = 0.1f),
                modifier = Modifier.padding(end = 6.dp)
              ) {
                Text(
                  text = user?.experienceLevel?.uppercase() ?: "BEGINNER",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = QuestPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
              Text(
                text = "${user?.selectedLanguage?.replaceFirstChar { it.uppercase() } ?: "Python"} Explorer",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Settings Button
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier
            .size(40.dp)
            .testTag("home_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Game Settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Level & XP Progress Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(QuestPrimary)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "LEVEL $level",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp
              )
            )
          }
        }

        Text(
          text = "$currentXpInLevel / $xpNeededForNext XP",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = QuestPrimary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
      )
    }
  }
}

@Composable
private fun StreakWeeklyCard(progress: PlayerProgress?) {
  val streak = progress?.currentStreak ?: 1
  val weekly = progress?.weeklyActivity ?: emptyList()

  GameCard(
    borderColor = StreakFlame.copy(alpha = 0.35f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(StreakFlame.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = "Streak",
              tint = StreakFlame,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "$streak DAY STREAK",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = StreakFlame,
                letterSpacing = 0.5.sp
              )
            )
            Text(
              text = if (progress?.isStreakActiveToday == true) "Streak extended today! 🔥" else "Complete 1 lesson today to keep your streak!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 7-Day DayOfWeek Visualizer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        weekly.forEach { day ->
          DayBubble(day = day)
        }
      }
    }
  }
}

@Composable
private fun DayBubble(day: DayActivity) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = day.dayShortName.take(1),
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (day.isToday) FontWeight.Black else FontWeight.Medium,
        color = if (day.isToday) StreakFlame else MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(
          when {
            day.isCompleted -> StreakFlame
            day.isToday -> StreakFlame.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.surfaceVariant
          }
        )
        .border(
          width = if (day.isToday) 1.5.dp else 0.dp,
          color = if (day.isToday) StreakFlame else Color.Transparent,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (day.isCompleted) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      } else {
        Text(
          text = "${day.dayNumber}",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = if (day.isToday) StreakFlame else MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
  }
}

@Composable
private fun ContinueLearningHeroCard(
  user: UserEntity?,
  progress: PlayerProgress?,
  onContinue: () -> Unit
) {
  val currentLesson = progress?.currentLesson
  val worldTitle = progress?.currentWorldTitle ?: "World 1 — Code Origin"
  val completedCount = progress?.completedLessonsCount ?: 0
  val totalCount = progress?.totalLessonsCount?.coerceAtLeast(1) ?: 8
  val progressFraction = completedCount.toFloat() / totalCount
  
  val pathTitle = when(user?.selectedLanguage?.uppercase()) {
    "PYTHON" -> "Python Mastery"
    "WEB" -> "Web Development"
    "MOBILE" -> "Mobile Engineer"
    "SYSTEMS" -> "Systems Architect"
    "DATA" -> "Data Scientist"
    else -> "Code Mastery"
  }

  GameCard(
    borderColor = QuestPrimary.copy(alpha = 0.4f)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(QuestPrimary, QuestPrimaryDark)
          )
        )
        .padding(20.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = pathTitle,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.2f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = user?.experienceLevel?.uppercase() ?: "BEGINNER",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (completedCount == 0) "Start Your Coding Quest" else "Continue Level",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
          color = Color.White
        )
        Text(
          text = if (currentLesson != null) "$worldTitle • Level ${currentLesson.lessonNumber}: ${currentLesson.title}" else "$worldTitle",
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "$completedCount / $totalCount Levels Complete",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White.copy(alpha = 0.85f)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = null,
              tint = XpGold,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "+${currentLesson?.xpReward ?: 25} XP",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = XpGold
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { progressFraction.coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = XpGold,
          trackColor = Color.White.copy(alpha = 0.25f),
          strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(18.dp))

        GameButton(
          text = if (completedCount == 0) "Begin Level 1" else "Continue →",
          onClick = onContinue,
          style = GameButtonStyle.PRIMARY,
          icon = Icons.Default.PlayArrow,
          testTag = "home_continue_button"
        )
      }
    }
  }
}

@Composable
private fun DailyChallengeCard(onStart: () -> Unit) {
  GameCard(
    borderColor = QuestSecondary.copy(alpha = 0.35f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(QuestSecondary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = QuestSecondary,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "DAILY CHALLENGE",
            style = MaterialTheme.typography.labelSmall.copy(
              color = QuestSecondary,
              fontWeight = FontWeight.Black
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• +75 XP",
            style = MaterialTheme.typography.labelSmall.copy(
              color = XpGold,
              fontWeight = FontWeight.Bold
            )
          )
        }
        Text(
          text = "Fix the Syntax Bug",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Find and fix quote errors in print statements",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onStart,
        colors = ButtonDefaults.buttonColors(containerColor = QuestSecondary, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .height(36.dp)
          .testTag("daily_challenge_button")
      ) {
        Text("Play", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}

@Composable
private fun QuestCard(
  quest: DailyQuestEntity,
  onClaim: () -> Unit
) {
  val progress = (quest.currentValue.toFloat() / quest.targetValue.coerceAtLeast(1)).coerceIn(0f, 1f)

  GameCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = quest.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = quest.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "+${quest.xpReward} XP",
            style = MaterialTheme.typography.labelLarge.copy(color = XpGold, fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.Toll,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "+${quest.coinReward}",
            style = MaterialTheme.typography.labelMedium.copy(color = XpGold, fontWeight = FontWeight.Bold)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .weight(1f)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = if (quest.isCompleted) QuestSuccess else QuestPrimary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = "${quest.currentValue}/${quest.targetValue}",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (quest.isCompleted && !quest.isClaimed) {
          Spacer(modifier = Modifier.width(10.dp))
          Button(
            onClick = onClaim,
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .height(32.dp)
              .testTag("claim_quest_button_${quest.id}")
          ) {
            Text("Claim", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }
        } else if (quest.isClaimed) {
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Claimed",
            tint = QuestSuccess,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun RecentAchievementsSection(
  achievements: List<AchievementEntity>,
  onViewAll: () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Recent Achievements",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      TextButton(
        onClick = onViewAll,
        modifier = Modifier.testTag("achievements_view_all_button")
      ) {
        Text("View All", color = QuestPrimary, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(achievements.take(4)) { ach ->
        AchievementMiniBadge(achievement = ach)
      }
    }
  }
}

@Composable
private fun AchievementMiniBadge(achievement: AchievementEntity) {
  Surface(
    modifier = Modifier
      .width(140.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = if (achievement.isUnlocked) XpGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp)
      ),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (achievement.isUnlocked) XpGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (achievement.category) {
            "STREAK" -> Icons.Default.LocalFireDepartment
            "PROJECT" -> Icons.Default.MilitaryTech
            else -> Icons.Default.Terminal
          },
          contentDescription = null,
          tint = if (achievement.isUnlocked) XpGold else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = achievement.title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
      )

      Text(
        text = if (achievement.isUnlocked) "Unlocked" else "${achievement.currentCount}/${achievement.targetCount}",
        style = MaterialTheme.typography.labelSmall,
        color = if (achievement.isUnlocked) QuestSuccess else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun PlayerProfileSummaryCard(
  progress: PlayerProgress?,
  onViewProfile: () -> Unit
) {
  GameCard(
    onClick = onViewProfile
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Player Stats",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(
          onClick = onViewProfile,
          modifier = Modifier.testTag("home_view_profile_button")
        ) {
          Text("Full Profile ➔", color = QuestPrimary, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        StatItem(title = "Level", value = "${progress?.level ?: 1}", icon = Icons.Default.School, color = QuestPrimary)
        StatItem(title = "Total XP", value = "${progress?.totalXp ?: 0}", icon = Icons.Default.Stars, color = XpGold)
        StatItem(title = "Streak", value = "${progress?.currentStreak ?: 1}d", icon = Icons.Default.LocalFireDepartment, color = StreakFlame)
        StatItem(title = "Completed", value = "${progress?.completedLessonsCount ?: 0}", icon = Icons.Default.CheckCircle, color = QuestSuccess)
      }
    }
  }
}

@Composable
private fun StatItem(
  title: String,
  value: String,
  icon: ImageVector,
  color: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun AdaptiveRecommendationsSection(
  recommendations: List<com.example.data.models.LearningRecommendation>,
  onSelect: (com.example.data.models.LearningRecommendation) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = QuestPrimary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Recommended for You",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = QuestPrimary.copy(alpha = 0.12f)
      ) {
        Text(
          text = "Adaptive AI",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = QuestPrimary,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(recommendations) { rec ->
        AdaptiveRecommendationCard(recommendation = rec, onClick = { onSelect(rec) })
      }
    }
  }
}

@Composable
fun AdaptiveRecommendationCard(
  recommendation: com.example.data.models.LearningRecommendation,
  onClick: () -> Unit
) {
  val accentColor = when (recommendation.type) {
    com.example.data.models.RecommendationType.PRACTICE_WEAK_SKILL -> StreakFlame
    com.example.data.models.RecommendationType.DEBUG_MISTAKE -> HeartRose
    com.example.data.models.RecommendationType.NEXT_LESSON -> QuestPrimary
    com.example.data.models.RecommendationType.TRY_CHALLENGE -> QuestIndigo
    com.example.data.models.RecommendationType.START_PROJECT -> QuestSuccess
  }

  Surface(
    modifier = Modifier
      .width(260.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("rec_card_${recommendation.id}"),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.4f)),
    tonalElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = accentColor.copy(alpha = 0.15f)
        ) {
          Text(
            text = recommendation.type.name.replace('_', ' '),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = accentColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "+${recommendation.xpReward} XP",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = XpGold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = recommendation.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
      )

      Text(
        text = recommendation.reason,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        modifier = Modifier.padding(top = 2.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = recommendation.difficulty,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
          onClick = onClick,
          colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(30.dp)
        ) {
          Text("Start", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
      }
    }
  }
}

@Composable
fun DailyPracticeWorkoutCard(
  plan: com.example.data.models.DailyPracticeSessionEntity,
  tasks: List<com.example.domain.learning.PracticeStep>,
  onStartTask: (com.example.domain.learning.PracticeStep) -> Unit,
  onClaimReward: () -> Unit
) {
  val isAllCompleted = plan.isCompleted
  val progressFraction = (plan.completedSteps.toFloat() / plan.totalSteps.toFloat()).coerceIn(0f, 1f)

  GameCard(
    borderColor = if (isAllCompleted) QuestSuccess else QuestPrimary.copy(alpha = 0.5f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(QuestPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = QuestPrimary,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = plan.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = plan.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text("+${plan.xpReward} XP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = XpGold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Progress: ${plan.completedSteps}/${plan.totalSteps} Steps",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "${(progressFraction * 100).toInt()}%",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = QuestPrimary
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { progressFraction },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = if (isAllCompleted) QuestSuccess else QuestPrimary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Task items
      tasks.forEachIndexed { index, task ->
        val isCurrent = index == plan.completedSteps && !isAllCompleted
        val isDone = task.isCompleted

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = when {
            isDone -> QuestSuccess.copy(alpha = 0.08f)
            isCurrent -> QuestPrimary.copy(alpha = 0.08f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          },
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
              isDone -> QuestSuccess.copy(alpha = 0.5f)
              isCurrent -> QuestPrimary
              else -> Color.Transparent
            }
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(enabled = !isDone) { onStartTask(task) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else if (isCurrent) Icons.Default.PlayArrow else Icons.Default.Code,
                contentDescription = null,
                tint = if (isDone) QuestSuccess else if (isCurrent) QuestPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "${index + 1}. ${task.title}",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = task.description,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }
            }

            if (isCurrent) {
              Button(
                onClick = { onStartTask(task) },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Text("Start", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }
            }
          }
        }
      }

      if (isAllCompleted) {
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onClaimReward,
          colors = ButtonDefaults.buttonColors(containerColor = QuestSuccess, contentColor = Color.White),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Claim Daily Practice Rewards (+${plan.xpReward} XP)", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
