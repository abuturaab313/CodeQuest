package com.example.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestPrimary

@Composable
fun MultipleChoiceExercise(
  exercise: ExerciseEntity,
  selectedAnswer: String?,
  onSelectAnswer: (String) -> Unit,
  isSubmitted: Boolean,
  modifier: Modifier = Modifier
) {
  val options = exercise.parseOptions()
  val optionLetters = listOf("A", "B", "C", "D", "E", "F")

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    options.forEachIndexed { index, option ->
      val isSelected = selectedAnswer == option
      val letter = optionLetters.getOrElse(index) { "${index + 1}" }

      GameCard(
        borderColor = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        onClick = {
          if (!isSubmitted) {
            onSelectAnswer(option)
          }
        },
        modifier = Modifier.testTag("option_choice_$index")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) QuestPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Choice Badge
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isSelected) QuestPrimary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            } else {
              Text(
                text = letter,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge.copy(
              fontFamily = if (exercise.type == ExerciseType.PREDICT_OUTPUT) FontFamily.Monospace else FontFamily.Default,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
              fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
