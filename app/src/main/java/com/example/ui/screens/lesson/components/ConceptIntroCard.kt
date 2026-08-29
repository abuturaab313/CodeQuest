package com.example.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LessonEntity
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.CodeBg
import com.example.ui.theme.CodeString
import com.example.ui.theme.QuestPrimary

@Composable
fun ConceptIntroCard(
  lesson: LessonEntity,
  onStartExercises: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp)
  ) {
    // Header Banner
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = QuestPrimary.copy(alpha = 0.1f),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, QuestPrimary.copy(alpha = 0.3f))
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(QuestPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = "KEY CONCEPT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = QuestPrimary
          )
          Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Concept Summary
    if (lesson.conceptSummary.isNotBlank()) {
      GameCard {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "What You'll Learn",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = lesson.conceptSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Concept Code Snippet
    if (lesson.conceptSnippet.isNotBlank()) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CodeBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "SYNTAX PREVIEW",
            style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = lesson.conceptSnippet,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              color = CodeString
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (lesson.conceptExplanation.isNotBlank()) {
      Text(
        text = lesson.conceptExplanation,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    GameButton(
      text = "Start Exercises",
      onClick = onStartExercises,
      style = GameButtonStyle.PRIMARY,
      icon = Icons.Default.PlayArrow,
      testTag = "start_exercises_button"
    )
  }
}
