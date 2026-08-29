package com.example.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExerciseEntity
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold

@Composable
fun MatchConceptsExercise(
  exercise: ExerciseEntity,
  matchedPairs: Map<String, String>,
  onPairMatched: (concept: String, definition: String) -> Unit,
  onUnmatch: (concept: String) -> Unit,
  isSubmitted: Boolean,
  modifier: Modifier = Modifier
) {
  val rawPairs = exercise.parseMatchingPairs()
  val concepts = remember(rawPairs) { rawPairs.map { it.first } }
  val definitions = remember(rawPairs) { rawPairs.map { it.second }.shuffled() }

  var selectedConcept by remember { mutableStateOf<String?>(null) }

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "Select a concept on the left, then tap its matching definition on the right:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Left Column: Concepts
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "CONCEPTS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = QuestPrimary)
        )
        concepts.forEachIndexed { index, concept ->
          val isPaired = matchedPairs.containsKey(concept)
          val isSelected = selectedConcept == concept

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              isPaired -> QuestSuccess.copy(alpha = 0.15f)
              isSelected -> QuestPrimary.copy(alpha = 0.2f)
              else -> MaterialTheme.colorScheme.surfaceVariant
            },
            border = androidx.compose.foundation.BorderStroke(
              1.5.dp,
              when {
                isPaired -> QuestSuccess
                isSelected -> QuestPrimary
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable(enabled = !isSubmitted) {
                if (isPaired) {
                  onUnmatch(concept)
                } else {
                  selectedConcept = concept
                }
              }
              .testTag("concept_item_$index")
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isPaired) {
                Icon(Icons.Default.Check, contentDescription = null, tint = QuestSuccess, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
              }
              Text(
                text = concept,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Right Column: Definitions
      Column(
        modifier = Modifier.weight(1.2f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "DEFINITIONS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = QuestPrimary)
        )
        definitions.forEachIndexed { index, definition ->
          val isUsed = matchedPairs.values.contains(definition)

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUsed) QuestSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
              1.5.dp,
              if (isUsed) QuestSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable(enabled = !isSubmitted && !isUsed && selectedConcept != null) {
                val c = selectedConcept
                if (c != null) {
                  onPairMatched(c, definition)
                  selectedConcept = null
                }
              }
              .testTag("definition_item_$index")
          ) {
            Text(
              text = definition,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(12.dp)
            )
          }
        }
      }
    }
  }
}
