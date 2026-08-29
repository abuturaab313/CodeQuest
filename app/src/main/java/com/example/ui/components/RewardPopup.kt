package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.services.GamificationReward
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.XpGold
import kotlinx.coroutines.delay

@Composable
fun RewardToast(
  reward: GamificationReward?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  LaunchedEffect(reward) {
    if (reward != null) {
      delay(2500)
      onDismiss()
    }
  }

  AnimatedVisibility(
    visible = reward != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (reward != null) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp)
          .clip(RoundedCornerShape(20.dp))
          .border(1.5.dp, XpGold, RoundedCornerShape(20.dp))
          .testTag("reward_toast"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          val iconVector = when (reward) {
            is GamificationReward.XpReward -> Icons.Default.Stars
            is GamificationReward.CoinReward -> Icons.Default.Toll
            is GamificationReward.StreakReward -> Icons.Default.LocalFireDepartment
            is GamificationReward.AchievementReward -> Icons.Default.EmojiEvents
            is GamificationReward.LevelUpReward -> Icons.Default.Stars
          }

          val iconColor = when (reward) {
            is GamificationReward.StreakReward -> StreakFlame
            is GamificationReward.AchievementReward -> QuestPrimary
            else -> XpGold
          }

          val mainText = when (reward) {
            is GamificationReward.XpReward -> "+${reward.amount} XP Earned!"
            is GamificationReward.CoinReward -> "+${reward.amount} CodeCoins"
            is GamificationReward.StreakReward -> "${reward.streakDays} Day Streak Maintained!"
            is GamificationReward.AchievementReward -> "Achievement: ${reward.title}"
            is GamificationReward.LevelUpReward -> "Level ${reward.newLevel} Reached!"
          }

          val subText = when (reward) {
            is GamificationReward.XpReward -> reward.reason
            is GamificationReward.CoinReward -> reward.reason
            is GamificationReward.StreakReward -> "Keep the learning streak alive!"
            is GamificationReward.AchievementReward -> "+${reward.xpEarned} XP Bonus"
            is GamificationReward.LevelUpReward -> "+${reward.coinsEarned} Coins"
          }

          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = iconVector,
              contentDescription = null,
              tint = iconColor,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = mainText,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = subText,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
