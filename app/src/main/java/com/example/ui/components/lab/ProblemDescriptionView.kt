package com.example.ui.components.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CodingChallengeEntity
import com.example.ui.components.editor.SyntaxTheme
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed

@Composable
fun ProblemDescriptionView(
  challenge: CodingChallengeEntity,
  hintsUnlockedCount: Int,
  onUnlockNextHint: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val examples = remember(challenge) { challenge.parseExamples() }
  val hints = remember(challenge) { challenge.parseHints() }

  val difficultyColor = when (challenge.difficulty.uppercase()) {
    "EASY", "BEGINNER" -> QuestGreen
    "MEDIUM", "INTERMEDIATE" -> QuestGold
    "HARD", "ADVANCED" -> QuestRed
    else -> QuestPrimary
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = SyntaxTheme.Background,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF313244))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Header Badges & Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Difficulty Badge
        Surface(
          color = difficultyColor.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = challenge.difficulty.uppercase(),
            color = difficultyColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        // Category Badge
        Surface(
          color = QuestPrimary.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = challenge.category.replace('_', ' '),
            color = QuestPrimary,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        // XP & Coin Badges
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Star, contentDescription = "XP", tint = QuestPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text("+${challenge.xpReward} XP", color = QuestPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

          Spacer(modifier = Modifier.width(8.dp))

          Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = QuestGold, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(2.dp))
          Text("+${challenge.coinReward}", color = QuestGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Title
      Text(
        text = challenge.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = SyntaxTheme.TextDefault
      )

      // Description Body
      Text(
        text = challenge.description,
        style = MaterialTheme.typography.bodyMedium,
        color = SyntaxTheme.TextDefault.copy(alpha = 0.9f),
        lineHeight = 22.sp
      )

      // 2. Input / Output Format
      if (challenge.inputDescription.isNotBlank() || challenge.outputDescription.isNotBlank() || challenge.solutionRequirements.isNotBlank()) {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF181825)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (challenge.inputDescription.isNotBlank()) {
              Text(
                text = "Input Format",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SyntaxTheme.Keyword
              )
              Text(
                text = challenge.inputDescription,
                style = MaterialTheme.typography.bodySmall,
                color = SyntaxTheme.TextDefault
              )
            }

            if (challenge.outputDescription.isNotBlank()) {
              Text(
                text = "Output Format",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SyntaxTheme.Keyword
              )
              Text(
                text = challenge.outputDescription,
                style = MaterialTheme.typography.bodySmall,
                color = SyntaxTheme.TextDefault
              )
            }

            if (challenge.solutionRequirements.isNotBlank()) {
              Text(
                text = "Requirements / Constraints",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SyntaxTheme.Keyword
              )
              Text(
                text = challenge.solutionRequirements,
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SyntaxTheme.Operator)
              )
            }
          }
        }
      }

      // 3. Examples
      if (examples.isNotEmpty()) {
        Text(
          text = "Examples",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = SyntaxTheme.TextDefault
        )

        for ((idx, ex) in examples.withIndex()) {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF181825)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "Example ${idx + 1}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SyntaxTheme.Builtin
              )

              if (ex.input.isNotBlank()) {
                Text("Input:", style = TextStyle(fontSize = 10.sp, color = SyntaxTheme.LineNumber, fontWeight = FontWeight.Bold))
                Surface(
                  color = Color(0xFF11111B),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = ex.input,
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SyntaxTheme.TextDefault),
                    modifier = Modifier.padding(6.dp)
                  )
                }
              }

              Text("Output:", style = TextStyle(fontSize = 10.sp, color = SyntaxTheme.LineNumber, fontWeight = FontWeight.Bold))
              Surface(
                color = Color(0xFF11111B),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = ex.output,
                  style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SyntaxTheme.StringLiteral),
                  modifier = Modifier.padding(6.dp)
                )
              }

              if (ex.explanation.isNotBlank()) {
                Text(
                  text = "Explanation: ${ex.explanation}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = SyntaxTheme.TextDefault.copy(alpha = 0.8f)
                )
              }
            }
          }
        }
      }

      // 4. Hints Section
      if (hints.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lightbulb, contentDescription = "Hints", tint = QuestGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Hints ($hintsUnlockedCount / ${hints.size} Unlocked)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = SyntaxTheme.TextDefault
            )

            Spacer(modifier = Modifier.weight(1f))

            if (hintsUnlockedCount < hints.size) {
              OutlinedButton(
                onClick = onUnlockNextHint,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = QuestGold)
              ) {
                Text("Unlock Hint ${hintsUnlockedCount + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          for (i in 0 until hintsUnlockedCount) {
            hints.getOrNull(i)?.let { hintText ->
              Surface(
                color = QuestGold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Text(
                    text = "💡 Hint ${i + 1}: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = QuestGold
                  )
                  Text(
                    text = hintText,
                    style = MaterialTheme.typography.bodySmall,
                    color = SyntaxTheme.TextDefault
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
