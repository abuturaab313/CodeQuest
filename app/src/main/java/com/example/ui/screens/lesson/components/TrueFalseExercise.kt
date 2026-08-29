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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GameCard
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess

@Composable
fun TrueFalseExercise(
  selectedAnswer: String?,
  onSelectAnswer: (String) -> Unit,
  isSubmitted: Boolean,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // TRUE Option
    val isTrueSelected = selectedAnswer?.equals("True", ignoreCase = true) == true
    GameCard(
      borderColor = if (isTrueSelected) QuestSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
      onClick = {
        if (!isSubmitted) onSelectAnswer("True")
      },
      modifier = Modifier
        .weight(1f)
        .testTag("true_choice_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(if (isTrueSelected) QuestSuccess.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
          .padding(vertical = 24.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (isTrueSelected) QuestSuccess else QuestSuccess.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Check,
            contentDescription = "True",
            tint = if (isTrueSelected) Color.White else QuestSuccess,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "TRUE",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
          color = if (isTrueSelected) QuestSuccess else MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // FALSE Option
    val isFalseSelected = selectedAnswer?.equals("False", ignoreCase = true) == true
    GameCard(
      borderColor = if (isFalseSelected) HeartRose else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
      onClick = {
        if (!isSubmitted) onSelectAnswer("False")
      },
      modifier = Modifier
        .weight(1f)
        .testTag("false_choice_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(if (isFalseSelected) HeartRose.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
          .padding(vertical = 24.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (isFalseSelected) HeartRose else HeartRose.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Close,
            contentDescription = "False",
            tint = if (isFalseSelected) Color.White else HeartRose,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "FALSE",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
          color = if (isFalseSelected) HeartRose else MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
