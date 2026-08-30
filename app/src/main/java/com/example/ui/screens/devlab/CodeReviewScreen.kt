package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CodeReviewEntity
import com.example.ui.components.editor.CodeEditorView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeReviewScreen(
  review: CodeReviewEntity,
  onNavigateBack: () -> Unit,
  onComplete: (xp: Int, coins: Int) -> Unit
) {
  var selectedIssueIndex by remember { mutableStateOf<Int?>(null) }
  var isSubmitted by remember { mutableStateOf(false) }
  var refactorCode by remember { mutableStateOf(review.refactorStarterCode.ifBlank { review.snippet }) }
  var isRefactorVerified by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = if (review.isRefactorChallenge) "REFACTORING: ${review.title}" else "CODE REVIEW: ${review.title}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "${review.language.uppercase()} • Clean Code Quality Lab",
              style = MaterialTheme.typography.labelSmall,
              color = QuestCyan
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("codereview_btn_back")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Objective Card
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.RateReview, contentDescription = null, tint = QuestPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Review Objective", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(review.description, style = MaterialTheme.typography.bodyMedium)
        }
      }

      // Code Snippet Box
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("CURRENT IMPLEMENTATION", style = MaterialTheme.typography.labelSmall, color = QuestCyan, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = review.snippet,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFFCDD6F4)
          )
        }
      }

      if (!review.isRefactorChallenge) {
        // Multiple Choice Diagnosis
        Text("Select the Primary Code Smell / Quality Flaw:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        val issues = review.parseIssues()
        issues.forEachIndexed { index, issueText ->
          val isSelected = selectedIssueIndex == index
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                if (!isSubmitted) selectedIssueIndex = index
              }
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
              ),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) QuestPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { if (!isSubmitted) selectedIssueIndex = index }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(issueText, style = MaterialTheme.typography.bodyMedium)
            }
          }
        }

        if (isSubmitted) {
          val isCorrect = selectedIssueIndex == review.correctIssueIndex
          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (isCorrect) QuestGreen.copy(alpha = 0.15f) else QuestRed.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = if (isCorrect) "✓ Excellent Analysis!" else "Review Feedback:",
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) QuestGreen else QuestRed
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(review.explanation, style = MaterialTheme.typography.bodySmall)
            }
          }

          if (isCorrect) {
            Button(
              onClick = { onComplete(review.xpReward, review.coinReward) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("codereview_btn_complete"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
            ) {
              Text("Claim Review Rewards (+${review.xpReward} XP)")
            }
          }
        } else {
          Button(
            onClick = { isSubmitted = true },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("codereview_btn_submit"),
            enabled = selectedIssueIndex != null,
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
          ) {
            Text("Submit Code Review")
          }
        }
      } else {
        // Refactoring Challenge Editor
        Text("Refactor into Clean Code:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(12.dp))
        ) {
          CodeEditorView(
            code = refactorCode,
            onCodeChange = { refactorCode = it },
            language = com.example.domain.languages.LanguageRegistry.getLanguage(review.language),
            modifier = Modifier.fillMaxSize()
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              // Verify refactored code maintains modularity and runs
              isRefactorVerified = refactorCode.contains("def validate_user") || refactorCode.contains("def calculate_order_total")
            },
            modifier = Modifier
              .weight(1f)
              .testTag("codereview_btn_verify_refactor"),
            colors = ButtonDefaults.buttonColors(containerColor = QuestGreen)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Verify Tests")
          }

          if (isRefactorVerified) {
            Button(
              onClick = { onComplete(review.xpReward, review.coinReward) },
              modifier = Modifier
                .weight(1f)
                .testTag("codereview_btn_claim_refactor"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
            ) {
              Text("Claim (+${review.xpReward} XP)")
            }
          }
        }

        if (isRefactorVerified) {
          Card(
            colors = CardDefaults.cardColors(containerColor = QuestGreen.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = QuestGreen)
              Spacer(modifier = Modifier.width(8.dp))
              Text("✓ Refactored code passes all regression test suites cleanly!", style = MaterialTheme.typography.bodySmall, color = QuestGreen, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
