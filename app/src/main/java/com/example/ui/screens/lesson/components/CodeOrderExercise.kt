package com.example.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExerciseEntity
import com.example.ui.theme.CodeBg
import com.example.ui.theme.CodeString
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.XpGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CodeOrderExercise(
  exercise: ExerciseEntity,
  assembledTokens: List<String>,
  availableTokens: List<String>,
  onAddToken: (String) -> Unit,
  onRemoveToken: (Int) -> Unit,
  onReset: () -> Unit,
  isSubmitted: Boolean,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Assembly Target Zone
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = CodeBg,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, if (assembledTokens.isNotEmpty()) QuestPrimary else Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ASSEMBLED SEQUENCE",
            style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
          )

          if (assembledTokens.isNotEmpty() && !isSubmitted) {
            TextButton(onClick = onReset, modifier = Modifier.testTag("reset_code_order_button")) {
              Icon(Icons.Default.Replay, contentDescription = "Reset", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reset", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (assembledTokens.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(60.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Tap code blocks below in the correct execution order...",
              style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
              color = Color.Gray
            )
          }
        } else {
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            assembledTokens.forEachIndexed { index, token ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = QuestPrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, QuestPrimary),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable(enabled = !isSubmitted) { onRemoveToken(index) }
                  .testTag("assembled_token_$index")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = token,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      color = CodeString,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  if (!isSubmitted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                      Icons.Default.Close,
                      contentDescription = "Remove",
                      tint = Color.Gray,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Available Tokens to Choose
    Text(
      text = "Available Code Blocks:",
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(10.dp))

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      availableTokens.forEachIndexed { index, token ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = !isSubmitted) { onAddToken(token) }
            .testTag("available_token_$index")
        ) {
          Text(
            text = token,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
          )
        }
      }
    }
  }
}
