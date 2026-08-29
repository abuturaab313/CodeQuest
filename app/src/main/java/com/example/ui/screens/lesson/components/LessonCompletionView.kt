package com.example.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LessonEntity
import com.example.domain.learning.LessonScoringResult
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold

@Composable
fun LessonCompletionView(
  lesson: LessonEntity,
  scoringResult: LessonScoringResult,
  onContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Trophy Icon
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(XpGold.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        Icons.Default.Stars,
        contentDescription = "Victory",
        tint = XpGold,
        modifier = Modifier.size(50.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = if (scoringResult.isPerfectRun) "PERFECT RUN!" else "LESSON COMPLETED!",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
      color = if (scoringResult.isPerfectRun) XpGold else QuestPrimary
    )

    Text(
      text = lesson.title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Stars Display
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      for (i in 1..3) {
        val filled = i <= scoringResult.stars
        Icon(
          Icons.Default.Star,
          contentDescription = null,
          tint = if (filled) XpGold else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.size(36.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Score & Accuracy Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Accuracy Card
      GameCard(modifier = Modifier.weight(1f)) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "ACCURACY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = QuestPrimary)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${scoringResult.accuracyPercentage}%",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // XP Earned Card
      GameCard(modifier = Modifier.weight(1f)) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "XP GAINED",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = XpGold)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "+${scoringResult.totalXp}",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = XpGold
            )
          }
        }
      }

      // Coins Card
      GameCard(modifier = Modifier.weight(1f)) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "COINS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = QuestSuccess)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = QuestSuccess, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "+${scoringResult.coinsEarned}",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = QuestSuccess
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Performance Summary Banner
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          if (scoringResult.isPerfectRun) Icons.Default.AutoAwesome else Icons.Default.CheckCircle,
          contentDescription = null,
          tint = if (scoringResult.isPerfectRun) XpGold else QuestSuccess,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = scoringResult.performanceSummary,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    GameButton(
      text = "Continue Journey",
      onClick = onContinue,
      style = GameButtonStyle.PRIMARY,
      icon = Icons.Default.PlayArrow,
      testTag = "continue_journey_button"
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}
