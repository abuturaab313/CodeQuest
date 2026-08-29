package com.example.ui.components.lab

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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.services.SubmissionResult
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestPrimary

@Composable
fun SubmissionSuccessDialog(
  submissionResult: SubmissionResult,
  challengeTitle: String,
  onDismiss: () -> Unit,
  onNextChallenge: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onNextChallenge,
        colors = ButtonDefaults.buttonColors(containerColor = QuestGreen),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(46.dp)
      ) {
        Text("Continue Learning", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(42.dp)
      ) {
        Text("Stay in Editor")
      }
    },
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Success Icon Circle
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(QuestGreen.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = QuestGreen,
            modifier = Modifier.size(44.dp)
          )
        }

        // Title & Challenge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Challenge Solved!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = challengeTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }

        // Perfect Bonus Pill if applicable
        if (submissionResult.isPerfect) {
          Surface(
            color = QuestGold.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold.copy(alpha = 0.3f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.WorkspacePremium, contentDescription = "Perfect", tint = QuestGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Perfect Solved (+10 XP Bonus)",
                color = QuestGold,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // Rewards Breakdown Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // XP Gained
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "XP", tint = QuestPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "+${submissionResult.xpAwarded}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = QuestPrimary
                )
              }
              Text(
                text = "XP Earned",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Divider
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Coins Gained
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = QuestGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "+${submissionResult.coinsAwarded}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = QuestGold
                )
              }
              Text(
                text = "CodeCoins",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Tests Passed Summary
        Text(
          text = "All ${submissionResult.testSuiteResult.totalCount} tests passed in ${submissionResult.testSuiteResult.totalExecutionTimeMs}ms",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  )
}
