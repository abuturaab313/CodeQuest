package com.example.ui.components.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AIQuizQuestion
import com.example.domain.ai.AIRequestState
import com.example.domain.ai.AIService
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.AIResponse
import com.example.domain.ai.models.LearningContext
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestIndigo
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryContainer
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.XpGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeCoachSheet(
  aiService: AIService,
  context: LearningContext,
  workspaceFiles: Map<String, String> = emptyMap(),
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val requestState by aiService.requestState.collectAsState()
  val scope = rememberCoroutineScope()

  var selectedMode by remember { mutableStateOf(if (context.recentError != null) AIMentorMode.DEBUG else AIMentorMode.HINT) }
  var currentHintLevel by remember { mutableIntStateOf(context.hintLevelRequested.coerceIn(1, 5)) }
  var feedbackSubmitted by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = modifier.testTag("code_coach_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(QuestPrimary, QuestIndigo))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Code Coach",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CODE COACH",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = QuestPrimary.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "AI Mentor",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = QuestPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = context.getPrimaryContextLabel(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("btn_close_coach")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Coach",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Mode Selector Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val modes = listOf(
          Pair(AIMentorMode.HINT, Icons.Default.Lightbulb),
          Pair(AIMentorMode.DEBUG, Icons.Default.BugReport),
          Pair(AIMentorMode.EXPLAIN, Icons.Default.MenuBook),
          Pair(AIMentorMode.REVIEW, Icons.Default.Code),
          Pair(AIMentorMode.CONCEPT, Icons.Default.School),
          Pair(AIMentorMode.QUIZ, Icons.Default.Psychology)
        )

        items(modes) { (mode, icon) ->
          FilterChip(
            selected = selectedMode == mode,
            onClick = {
              selectedMode = mode
              feedbackSubmitted = false
              scope.launch {
                aiService.requestMentorGuidance(
                  mode = mode,
                  rawContext = context.copy(hintLevelRequested = currentHintLevel),
                  workspaceFiles = workspaceFiles
                )
              }
            },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(mode.title)
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = QuestPrimary,
              selectedLabelColor = Color.White,
              selectedLeadingIconColor = Color.White
            )
          )
        }
      }

      // If Mode is HINT, show Progressive Hint Ladder
      if (selectedMode == AIMentorMode.HINT) {
        Spacer(modifier = Modifier.height(12.dp))
        HintLadderBar(
          currentLevel = currentHintLevel,
          onSelectLevel = { newLevel ->
            currentHintLevel = newLevel
            feedbackSubmitted = false
            scope.launch {
              aiService.requestMentorGuidance(
                mode = AIMentorMode.HINT,
                rawContext = context.copy(hintLevelRequested = newLevel),
                workspaceFiles = workspaceFiles
              )
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Content Area based on AIRequestState
      when (val state = requestState) {
        is AIRequestState.Idle -> {
          InitialCoachPrompt(
            mode = selectedMode,
            hasError = context.recentError != null,
            onRequest = {
              feedbackSubmitted = false
              scope.launch {
                aiService.requestMentorGuidance(
                  mode = selectedMode,
                  rawContext = context.copy(hintLevelRequested = currentHintLevel),
                  workspaceFiles = workspaceFiles
                )
              }
            }
          )
        }

        is AIRequestState.Loading -> {
          LoadingCoachCard(message = state.message)
        }

        is AIRequestState.Error -> {
          ErrorCoachCard(
            message = state.message,
            onRetry = {
              scope.launch {
                aiService.requestMentorGuidance(
                  mode = selectedMode,
                  rawContext = context.copy(hintLevelRequested = currentHintLevel),
                  workspaceFiles = workspaceFiles
                )
              }
            },
            onContinue = onDismiss
          )
        }

        is AIRequestState.Success -> {
          CoachResponseView(
            response = state.response,
            onNextHint = {
              if (currentHintLevel < 5) {
                currentHintLevel += 1
                feedbackSubmitted = false
                scope.launch {
                  aiService.requestMentorGuidance(
                    mode = AIMentorMode.HINT,
                    rawContext = context.copy(hintLevelRequested = currentHintLevel),
                    workspaceFiles = workspaceFiles
                  )
                }
              }
            },
            feedbackSubmitted = feedbackSubmitted,
            onSendFeedback = { helpful ->
              feedbackSubmitted = true
              scope.launch {
                aiService.submitFeedback(
                  contextKey = context.getPrimaryContextLabel(),
                  mode = selectedMode.name,
                  userQuery = "Mode: ${selectedMode.name}, Level: $currentHintLevel",
                  responseSummary = state.response.headline,
                  wasHelpful = helpful
                )
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun HintLadderBar(
  currentLevel: Int,
  onSelectLevel: (Int) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Progressive Hint Ladder (Level $currentLevel of 5)",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = when (currentLevel) {
            1 -> "Conceptual Clue"
            2 -> "Locate Discrepancy"
            3 -> "Algorithmic Approach"
            4 -> "Pseudocode Outline"
            else -> "Detailed Syntax"
          },
          style = MaterialTheme.typography.labelSmall,
          color = QuestPrimary
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        for (i in 1..5) {
          val isCurrent = i == currentLevel
          val isPassed = i < currentLevel

          Box(
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                when {
                  isCurrent -> QuestPrimary
                  isPassed -> QuestPrimary.copy(alpha = 0.3f)
                  else -> MaterialTheme.colorScheme.surface
                }
              )
              .clickable { onSelectLevel(i) },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Lvl $i",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InitialCoachPrompt(
  mode: AIMentorMode,
  hasError: Boolean,
  onRequest: () -> Unit
) {
  GameCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = if (hasError) Icons.Default.BugReport else Icons.Default.Lightbulb,
        contentDescription = null,
        tint = if (hasError) StreakFlame else QuestGold,
        modifier = Modifier.size(44.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = when (mode) {
          AIMentorMode.DEBUG -> "Diagnose Runtime Error"
          AIMentorMode.HINT -> "Need a gentle nudge?"
          AIMentorMode.EXPLAIN -> "Understand Concept"
          AIMentorMode.REVIEW -> "Inspect Code Quality"
          AIMentorMode.CONCEPT -> "Concept Coach"
          AIMentorMode.QUIZ -> "Quick Knowledge Check"
        },
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = mode.shortDescription,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onRequest,
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("btn_ask_coach_action")
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Ask Code Coach (${mode.title})",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}

@Composable
private fun LoadingCoachCard(message: String) {
  GameCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      CircularProgressIndicator(
        color = QuestPrimary,
        modifier = Modifier.size(36.dp),
        strokeWidth = 3.dp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun ErrorCoachCard(
  message: String,
  onRetry: () -> Unit,
  onContinue: () -> Unit
) {
  GameCard(borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Warning,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Coach Offline or Busy",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.error
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onContinue,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Continue Without AI")
        }

        Button(
          onClick = onRetry,
          colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Try Again")
        }
      }
    }
  }
}

@Composable
private fun CoachResponseView(
  response: AIResponse,
  onNextHint: () -> Unit,
  feedbackSubmitted: Boolean,
  onSendFeedback: (Boolean) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Response Header Card
    GameCard {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = response.headline,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = QuestPrimary.copy(alpha = 0.12f)
          ) {
            Text(
              text = response.providerUsed,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = QuestPrimary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Structured Breakdown
        if (!response.whatsWrong.isNullOrBlank()) {
          SectionBlock(
            icon = Icons.Default.Warning,
            title = "WHAT'S HAPPENING",
            body = response.whatsWrong,
            iconTint = StreakFlame
          )
        }

        if (!response.why.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          SectionBlock(
            icon = Icons.Default.Lightbulb,
            title = "WHY",
            body = response.why,
            iconTint = QuestGold
          )
        }

        if (!response.tryThis.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          SectionBlock(
            icon = Icons.Default.Code,
            title = "TRY THIS",
            body = response.tryThis,
            iconTint = QuestPrimary
          )
        }

        if (!response.thinkAbout.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          SectionBlock(
            icon = Icons.Default.HelpOutline,
            title = "THINK ABOUT",
            body = response.thinkAbout,
            iconTint = QuestIndigo
          )
        }

        if (!response.optionalNextStep.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          SectionBlock(
            icon = Icons.Default.AutoAwesome,
            title = "NEXT STEP",
            body = response.optionalNextStep,
            iconTint = QuestSuccess
          )
        }

        // Code Review Lists
        if (response.mustFixItems.isNotEmpty() || response.optionalImprovementItems.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          if (response.mustFixItems.isNotEmpty()) {
            Text(
              text = "🚨 MUST FIX",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.error
            )
            response.mustFixItems.forEach { item ->
              Text(
                text = "• $item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
              )
            }
          }

          if (response.optionalImprovementItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "✨ OPTIONAL IMPROVEMENTS",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = QuestPrimary
            )
            response.optionalImprovementItems.forEach { item ->
              Text(
                text = "• $item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
              )
            }
          }
        }
      }
    }

    // Quiz Questions Card if QUIZ mode
    if (response.quizQuestions.isNotEmpty()) {
      QuizCardsBlock(questions = response.quizQuestions)
    }

    // Next Level Hint Button
    if (response.mode == AIMentorMode.HINT && response.hintLevel < 5) {
      OutlinedButton(
        onClick = onNextHint,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Request Level ${response.hintLevel + 1} Hint (More Direct)")
      }
    }

    // Feedback Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (feedbackSubmitted) "Thanks for your feedback!" else "Was this advice helpful?",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (!feedbackSubmitted) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = { onSendFeedback(true) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ThumbUp,
              contentDescription = "Helpful",
              tint = QuestSuccess,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = { onSendFeedback(false) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ThumbDown,
              contentDescription = "Not Helpful",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SectionBlock(
  icon: ImageVector,
  title: String,
  body: String,
  iconTint: Color
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
          ),
          color = iconTint
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = body,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun QuizCardsBlock(questions: List<AIQuizQuestion>) {
  val selectedAnswers = remember { mutableStateMapOf<String, Int>() }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "🧠 Mini Knowledge Check (Practice Only)",
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    questions.forEachIndexed { qIdx, question ->
      val selectedOption = selectedAnswers[question.id]
      val isAnswered = selectedOption != null

      GameCard {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "${qIdx + 1}. ${question.question}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(8.dp))

          question.options.forEachIndexed { optIdx, optionText ->
            val isSelected = selectedOption == optIdx
            val isCorrect = optIdx == question.correctOptionIndex

            val backgroundColor = when {
              isAnswered && isCorrect -> QuestSuccess.copy(alpha = 0.15f)
              isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
              isSelected -> QuestPrimary.copy(alpha = 0.12f)
              else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
              isAnswered && isCorrect -> QuestSuccess
              isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
              isSelected -> QuestPrimary
              else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .clickable(enabled = !isAnswered) {
                  selectedAnswers[question.id] = optIdx
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = optionText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          if (isAnswered) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = question.explanation,
              style = MaterialTheme.typography.labelSmall,
              color = QuestPrimary
            )
          }
        }
      }
    }
  }
}
