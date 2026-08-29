package com.example.ui.components.project

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.models.ProjectEntity
import com.example.data.repository.ProjectSubmissionResult
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.XpGold

@Composable
fun ProjectCompletionDialog(
  project: ProjectEntity,
  submissionResult: ProjectSubmissionResult,
  onDismiss: () -> Unit,
  onBackToProjects: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("dialog_project_completion"),
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Trophy icon with glowing circular background
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(QuestGold.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Project Mastered",
            tint = QuestGold,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Project Completed!",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "You built and tested '${project.title}' successfully!",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Badge Reveal Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = QuestGold.copy(alpha = 0.08f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(QuestGold),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "New Badge Unlocked",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = project.badgeName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = QuestGold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reward Summary Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = XpGold.copy(alpha = 0.12f),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("+${project.xpReward} XP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = XpGold)
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = QuestGold.copy(alpha = 0.12f),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = QuestGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("+${project.coinReward} Coins", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = QuestGold)
            }
          }
        }

        if (submissionResult.streakResult != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = StreakFlame.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFlame, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("${submissionResult.streakResult.newStreak}-Day Coding Streak!", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StreakFlame)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onBackToProjects,
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("btn_complete_project_continue")
      ) {
        Text("Return to Project Lab", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
      }
    }
  )
}
