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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.models.ExerciseEntity
import com.example.ui.theme.CodeBg
import com.example.ui.theme.CodeString
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.XpGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInBlankExercise(
  exercise: ExerciseEntity,
  currentValue: String,
  onValueChange: (String) -> Unit,
  isSubmitted: Boolean,
  modifier: Modifier = Modifier
) {
  val options = exercise.parseOptions()
  val starter = exercise.starterCode.ifEmpty { "___" }

  Column(modifier = modifier.fillMaxWidth()) {
    // Code Container with Fill Slot
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = CodeBg,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF334155))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "CODE SNIPPET",
          style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Render code preview with replacement
        val filledPreview = if (currentValue.isNotBlank()) {
          starter.replace("___", currentValue)
        } else {
          starter
        }

        Text(
          text = filledPreview,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            color = if (currentValue.isNotBlank()) XpGold else CodeString,
            fontWeight = FontWeight.SemiBold
          ),
          lineHeight = 24.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Direct Input Field
    OutlinedTextField(
      value = currentValue,
      onValueChange = { if (!isSubmitted) onValueChange(it) },
      placeholder = { Text("Type missing token here...") },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("fill_blank_input"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true,
      enabled = !isSubmitted,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = QuestPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline
      ),
      textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    )

    // Option Chips if available
    if (options.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Or tap a token to insert:",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        options.forEach { option ->
          val isSelected = currentValue == option
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable(enabled = !isSubmitted) {
                onValueChange(option)
              }
              .testTag("token_chip_$option")
          ) {
            Text(
              text = option,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }
  }
}
