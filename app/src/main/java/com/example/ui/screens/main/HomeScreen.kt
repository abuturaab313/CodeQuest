package com.example.ui.screens.main

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AchievementEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.LessonEntity
import com.example.data.models.UserEntity
import com.example.domain.services.PlayerProgress
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
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
      // Compressed HUD: Avatar, Level, Hearts, Streak, Settings
      CompactHeaderSection(
        progress = playerProgress,
        user = user,
        onOpenSettings = onOpenSettings,
        onNavigateToProfile = onNavigateToProfile
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }

    // MAIN ACTION: Current Quest Hero
    item {
      CurrentQuestHeroCard(
        user = user,
        progress = playerProgress,
        onContinue = { onContinueLearning(playerProgress?.currentLesson) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }

    // Optional: Secondary Tasks (Daily Quests)
    if (dailyQuests.isNotEmpty()) {
      item {
        Text(
          text = "SIDE QUESTS",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
      }

      items(dailyQuests) { quest ->
        QuestCard(quest = quest, onClaim = { onClaimQuest(quest) })
      }
    }
    
    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun CompactHeaderSection(
  progress: PlayerProgress?,
  user: UserEntity?,
  onOpenSettings: () -> Unit,
  onNavigateToProfile: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // User Profile short
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.clickable { onNavigateToProfile() }
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(QuestPrimary.copy(alpha = 0.15f))
          .border(2.dp, QuestPrimary, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Avatar",
          tint = QuestPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = user?.username ?: "Code Quester",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Level ${progress?.level ?: 1}",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = QuestPrimary
        )
      }
    }

    // HUD Stats
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Hearts
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Favorite, contentDescription = "Hearts", tint = HeartRose, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("${user?.currentHearts ?: 5}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = HeartRose)
      }
      
      // Streak
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = StreakFlame, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("${user?.streakDays ?: 1}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = StreakFlame)
      }
      
      // Coins
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Toll, contentDescription = "Coins", tint = XpGold, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("${user?.coins ?: 0}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = XpGold)
      }

      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier.size(32.dp).testTag("home_settings_button")
      ) {
        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun CurrentQuestHeroCard(
  user: UserEntity?,
  progress: PlayerProgress?,
  onContinue: () -> Unit
) {
  val currentLesson = progress?.currentLesson
  val worldTitle = progress?.currentWorldTitle ?: "World 1"
  val completedCount = progress?.completedLessonsCount ?: 0
  
  GameCard(
    borderColor = QuestPrimary.copy(alpha = 0.5f)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.verticalGradient(listOf(QuestPrimary, QuestPrimaryDark)))
        .padding(24.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // World Tag
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color.White.copy(alpha = 0.2f),
          modifier = Modifier.padding(bottom = 16.dp)
        ) {
          Text(
            text = worldTitle.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }

        // Level Title
        if (currentLesson != null) {
          Text(
            text = "LEVEL ${currentLesson.lessonNumber}",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, color = XpGold)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = currentLesson.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "\"${currentLesson.description.take(60)}...\"",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        } else {
          Text(
            text = "QUEST COMPLETE",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main CTA
        GameButton(
          text = if (completedCount == 0) "START QUEST" else "▶ CONTINUE QUEST",
          onClick = onContinue,
          style = GameButtonStyle.PRIMARY,
          modifier = Modifier.fillMaxWidth().height(60.dp),
          testTag = "home_continue_button"
        )
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
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = quest.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      if (quest.isCompleted && !quest.isClaimed) {
        Button(
          onClick = onClaim,
          colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(36.dp).testTag("claim_quest_button_${quest.id}")
        ) {
          Text("Claim", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
      } else if (quest.isClaimed) {
        Icon(Icons.Default.CheckCircle, contentDescription = "Claimed", tint = QuestSuccess, modifier = Modifier.size(24.dp))
      } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("+${quest.xpReward}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = XpGold))
          Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(14.dp))
        }
      }
    }
  }
}
