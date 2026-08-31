package com.example.ui.screens.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ExerciseEntity
import com.example.data.models.ExerciseType
import com.example.data.models.LessonEntity
import com.example.data.models.LessonProgressEntity
import com.example.data.models.LessonType
import com.example.domain.execution.DefaultCodeExecutionService
import com.example.domain.learning.AnswerValidator
import com.example.domain.learning.HintService
import com.example.domain.learning.LessonScoringResult
import com.example.domain.learning.LessonScoringService
import com.example.domain.learning.ValidationResult
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.screens.lesson.components.BossBattleHeader
import com.example.ui.screens.lesson.components.CodeOrderExercise
import com.example.ui.screens.lesson.components.ConceptIntroCard
import com.example.ui.screens.lesson.components.FillInBlankExercise
import com.example.ui.screens.lesson.components.LessonCompletionView
import com.example.ui.screens.lesson.components.MatchConceptsExercise
import com.example.ui.screens.lesson.components.MultipleChoiceExercise
import com.example.ui.screens.lesson.components.TrueFalseExercise
import com.example.ui.theme.CodeBg
import com.example.ui.theme.CodeString
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
  lesson: LessonEntity,
  exercises: List<ExerciseEntity>,
  onClose: () -> Unit,
  onCompleteLesson: (totalExercises: Int, correctCount: Int, mistakeCount: Int, hintsUsed: Int, baseXp: Int, baseCoins: Int) -> Unit,
  onDeductHeart: () -> Unit,
  onRecordMistake: (ExerciseEntity, String) -> Unit,
  onCorrectAnswer: () -> Unit = {},
  onWrongAnswer: () -> Unit = {},
  onPlayTap: () -> Unit = {},
  onSaveProgress: (LessonProgressEntity) -> Unit,
  initialProgress: LessonProgressEntity? = null,
  currentHearts: Int,
  onOpenCodeCoach: (com.example.domain.ai.models.LearningContext) -> Unit = {},
  isBookmarked: Boolean = false,
  onToggleBookmark: () -> Unit = {},
  initialNoteText: String = "",
  onSaveNote: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  // Mode state: Concept Intro -> Interactive Exercises -> Completion Screen
  var isShowingConceptIntro by remember {
    mutableStateOf(lesson.conceptSummary.isNotBlank() && (initialProgress == null || initialProgress.currentExerciseIndex == 0))
  }
  var exerciseIndex by remember {
    mutableIntStateOf(initialProgress?.currentExerciseIndex ?: 0)
  }
  var correctCount by remember {
    mutableIntStateOf(initialProgress?.correctCount ?: 0)
  }
  var mistakeCount by remember {
    mutableIntStateOf(initialProgress?.incorrectCount ?: 0)
  }
  var hintsUsedCount by remember {
    mutableIntStateOf(initialProgress?.hintsUsedCount ?: 0)
  }

  // Completion state
  var completionScoringResult by remember { mutableStateOf<LessonScoringResult?>(null) }

  // Personal Note State & Exit Confirmation
  var showNoteDialog by remember { mutableStateOf(false) }
  var showQuitConfirmDialog by remember { mutableStateOf(false) }
  var noteText by remember { mutableStateOf(initialNoteText) }

  // Intercept back button to show the quit confirmation dialog
  BackHandler(enabled = true) {
    if (completionScoringResult != null) {
      onClose()
    } else {
      showQuitConfirmDialog = true
    }
  }

  // Answer states for active exercise
  var selectedAnswer by remember { mutableStateOf<String?>(null) }
  var typedCode by remember { mutableStateOf("") }
  var outputLog by remember { mutableStateOf<String?>(null) }
  var isAnswerSubmitted by remember { mutableStateOf(false) }
  var isProcessingClick by remember { mutableStateOf(false) }
  var validationResult by remember { mutableStateOf<ValidationResult?>(null) }
  var currentHintIndex by remember { mutableIntStateOf(0) }

  // State collections for complex exercise types
  val assembledTokens = remember { mutableStateListOf<String>() }
  val availableTokens = remember { mutableStateListOf<String>() }
  val matchedPairs = remember { mutableStateMapOf<String, String>() }

  val coroutineScope = rememberCoroutineScope()
  val executionService = remember { DefaultCodeExecutionService() }
  val answerValidator = remember { AnswerValidator() }
  val hintService = remember { HintService() }
  val scoringService = remember { LessonScoringService() }

  val currentExercise = exercises.getOrNull(exerciseIndex)
  val totalExercises = exercises.size.coerceAtLeast(1)
  val progress = ((exerciseIndex + 1).toFloat() / totalExercises.toFloat()).coerceIn(0f, 1f)

  // Initialize or reload exercise state
  fun loadExercise(index: Int) {
    exerciseIndex = index
    selectedAnswer = null
    outputLog = null
    isAnswerSubmitted = false
    validationResult = null
    currentHintIndex = 0
    assembledTokens.clear()
    availableTokens.clear()
    matchedPairs.clear()

    val ex = exercises.getOrNull(index)
    if (ex != null) {
      typedCode = ex.starterCode
      if (ex.type == ExerciseType.CODE_ORDER) {
        val options = ex.parseOptions()
        availableTokens.addAll(options.shuffled())
      }
    }

    // Save ongoing lesson state
    onSaveProgress(
      LessonProgressEntity(
        lessonId = lesson.id,
        currentExerciseIndex = index,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = mistakeCount,
        hintsUsedCount = hintsUsedCount,
        isCompleted = false,
        lastUpdatedEpochMs = System.currentTimeMillis()
      )
    )
  }

  var hasInitializedFirstExercise by remember { mutableStateOf(false) }

  // Initialize on first composition
  LaunchedEffect(exercises, isShowingConceptIntro) {
    if (exercises.isNotEmpty() && !isShowingConceptIntro && !hasInitializedFirstExercise) {
      hasInitializedFirstExercise = true
      loadExercise(exerciseIndex)
    }
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    color = MaterialTheme.colorScheme.background
  ) {
    val scoring = completionScoringResult
    if (scoring != null) {
      // Completed Victory Screen
      LessonCompletionView(
        lesson = lesson,
        scoringResult = scoring,
        onContinue = {
          onCompleteLesson(
            totalExercises,
            correctCount,
            mistakeCount,
            hintsUsedCount,
            lesson.xpReward,
            lesson.coinReward
          )
          onClose()
        }
      )
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Navigation Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = {
              if (completionScoringResult != null) {
                onClose()
              } else {
                showQuitConfirmDialog = true
              }
            },
            modifier = Modifier.testTag("lesson_close_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }

          LinearProgressIndicator(
            progress = { if (isShowingConceptIntro) 0.05f else progress },
            modifier = Modifier
              .weight(1f)
              .height(10.dp)
              .padding(horizontal = 12.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = QuestPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            IconButton(
              onClick = onToggleBookmark,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isBookmarked) QuestPrimary.copy(alpha = 0.2f) else QuestPrimary.copy(alpha = 0.08f))
                .testTag("btn_bookmark_lesson")
            ) {
              Icon(
                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = QuestPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = { showNoteDialog = true },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(QuestPrimary.copy(alpha = 0.08f))
                .testTag("btn_notes_lesson")
            ) {
              Icon(
                imageVector = Icons.Default.NoteAdd,
                contentDescription = "Personal Notes",
                tint = QuestPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = {
                val coachContext = com.example.domain.ai.models.LearningContext(
                  sourceScreen = "LESSON",
                  lessonTitle = lesson.title,
                  exercisePrompt = currentExercise?.prompt,
                  exerciseType = currentExercise?.type?.name,
                  starterCode = currentExercise?.starterCode,
                  currentCode = typedCode.ifBlank { currentExercise?.starterCode ?: "" },
                  recentError = outputLog,
                  activeConcept = currentExercise?.topic ?: "PYTHON_BASICS"
                )
                onOpenCodeCoach(coachContext)
              },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(QuestPrimary.copy(alpha = 0.12f))
                .testTag("btn_open_coach_lesson")
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Code Coach",
                tint = QuestPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(HeartRose.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Hearts",
                tint = HeartRose,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "$currentHearts",
                style = MaterialTheme.typography.labelLarge.copy(
                  color = HeartRose,
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Boss Battle Header if Boss Level
        if (lesson.lessonType == LessonType.BOSS && !isShowingConceptIntro) {
          BossBattleHeader(
            bossName = lesson.title,
            currentPhase = exerciseIndex + 1,
            totalPhases = totalExercises
          )
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Scrollable Main Content Area
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          if (isShowingConceptIntro) {
            // STEP 1: CONCEPT INTRO
            ConceptIntroCard(
              lesson = lesson,
              onStartExercises = {
                isShowingConceptIntro = false
                loadExercise(0)
              }
            )
          } else if (currentExercise != null) {
            // STEP 2: INTERACTIVE EXERCISES
            Text(
              text = "Exercise ${exerciseIndex + 1} of $totalExercises",
              style = MaterialTheme.typography.labelMedium.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = currentExercise.prompt,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Exercise Components
            when (currentExercise.type) {
              ExerciseType.MULTIPLE_CHOICE, ExerciseType.PREDICT_OUTPUT, ExerciseType.FIND_BUG, ExerciseType.BOSS_CHALLENGE -> {
                MultipleChoiceExercise(
                  exercise = currentExercise,
                  selectedAnswer = selectedAnswer,
                  onSelectAnswer = { 
                    selectedAnswer = it 
                    onPlayTap()
                  },
                  isSubmitted = isAnswerSubmitted
                )
              }

              ExerciseType.TRUE_FALSE -> {
                TrueFalseExercise(
                  selectedAnswer = selectedAnswer,
                  onSelectAnswer = { 
                    selectedAnswer = it 
                    onPlayTap()
                  },
                  isSubmitted = isAnswerSubmitted
                )
              }

              ExerciseType.FILL_IN_BLANK -> {
                FillInBlankExercise(
                  exercise = currentExercise,
                  currentValue = selectedAnswer ?: "",
                  onValueChange = { 
                    selectedAnswer = it 
                    onPlayTap()
                  },
                  isSubmitted = isAnswerSubmitted
                )
              }

              ExerciseType.CODE_ORDER -> {
                CodeOrderExercise(
                  exercise = currentExercise,
                  assembledTokens = assembledTokens,
                  availableTokens = availableTokens,
                  onAddToken = { token ->
                    availableTokens.remove(token)
                    assembledTokens.add(token)
                    selectedAnswer = assembledTokens.joinToString(", ")
                    onPlayTap()
                  },
                  onRemoveToken = { idx ->
                    val token = assembledTokens.removeAt(idx)
                    availableTokens.add(token)
                    selectedAnswer = assembledTokens.joinToString(", ")
                    onPlayTap()
                  },
                  onReset = {
                    assembledTokens.clear()
                    availableTokens.clear()
                    availableTokens.addAll(currentExercise.parseOptions().shuffled())
                    selectedAnswer = null
                    onPlayTap()
                  },
                  isSubmitted = isAnswerSubmitted
                )
              }

              ExerciseType.MATCH_CONCEPTS -> {
                MatchConceptsExercise(
                  exercise = currentExercise,
                  matchedPairs = matchedPairs,
                  onPairMatched = { concept, def ->
                    matchedPairs[concept] = def
                    selectedAnswer = matchedPairs.entries.joinToString(";") { "${it.key}:${it.value}" }
                    onPlayTap()
                  },
                  onUnmatch = { concept ->
                    matchedPairs.remove(concept)
                    selectedAnswer = matchedPairs.entries.joinToString(";") { "${it.key}:${it.value}" }
                    onPlayTap()
                  },
                  isSubmitted = isAnswerSubmitted
                )
              }

              ExerciseType.WRITE_CODE, ExerciseType.COMPLETE_CODE -> {
                Column {
                  Text(
                    text = "Code Sandbox Terminal",
                    style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
                  )
                  Spacer(modifier = Modifier.height(6.dp))

                  OutlinedTextField(
                    value = typedCode,
                    onValueChange = {
                      typedCode = it
                      selectedAnswer = it
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(180.dp)
                      .testTag("code_editor_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = CodeBg,
                      unfocusedContainerColor = CodeBg,
                      focusedTextColor = CodeString,
                      unfocusedTextColor = CodeString
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  GameButton(
                    text = "Run Code in Sandbox",
                    onClick = {
                      coroutineScope.launch {
                        val res = executionService.execute(code = typedCode, languageId = "python")
                        outputLog = if (res.isSuccess) res.stdout else res.stderr
                      }
                    },
                    style = GameButtonStyle.SECONDARY,
                    icon = Icons.Default.Terminal,
                    testTag = "run_sandbox_button"
                  )

                  if (outputLog != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(10.dp),
                      color = Color.Black
                    ) {
                      Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                          text = "CONSOLE OUTPUT:",
                          style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                          text = outputLog ?: "",
                          style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = Color.White)
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Progressive Hints
            val progressiveHints = remember(currentExercise) {
              hintService.buildProgressiveHints(currentExercise.parseHints(), currentExercise.explanation)
            }

            if (progressiveHints.isNotEmpty()) {
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    if (currentHintIndex < progressiveHints.size) {
                      currentHintIndex += 1
                      hintsUsedCount += 1
                    }
                  },
                shape = RoundedCornerShape(14.dp),
                color = XpGold.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, XpGold.copy(alpha = 0.3f))
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Lightbulb, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    if (currentHintIndex == 0) {
                      Text(
                        text = "Need a Hint?",
                        style = MaterialTheme.typography.labelLarge.copy(color = XpGold, fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = "Tap to unlock progressive guidance clues",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    } else {
                      val activeHint = progressiveHints[currentHintIndex - 1]
                      Text(
                        text = activeHint.title,
                        style = MaterialTheme.typography.labelMedium.copy(color = XpGold, fontWeight = FontWeight.Bold)
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = activeHint.hintText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      if (currentHintIndex < progressiveHints.size) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = "Tap again for deeper clue",
                          style = MaterialTheme.typography.labelSmall.copy(color = XpGold, fontWeight = FontWeight.SemiBold)
                        )
                      }
                    }
                  }
                }
              }
            }

            // Answer Result Banner
            AnimatedVisibility(visible = isAnswerSubmitted && validationResult != null) {
              val res = validationResult
              if (res != null) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (res.isCorrect) QuestSuccess.copy(alpha = 0.12f) else HeartRose.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (res.isCorrect) QuestSuccess else HeartRose)
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          if (res.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                          contentDescription = null,
                          tint = if (res.isCorrect) QuestSuccess else HeartRose,
                          modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = if (res.isCorrect) "Awesome! Correct Answer" else "Not quite right",
                          style = MaterialTheme.typography.titleMedium.copy(
                            color = if (res.isCorrect) QuestSuccess else HeartRose,
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = res.feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      val explanation = currentExercise?.explanation ?: ""
                      if (explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = explanation,
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

  // Bottom Action Button
        if (!isShowingConceptIntro && currentExercise != null) {
          key(exerciseIndex) {
            GameButton(
              text = when {
                !isAnswerSubmitted -> "Check Answer"
                exerciseIndex < totalExercises - 1 -> "Next Exercise"
                lesson.lessonType == LessonType.BOSS -> "Defeat Boss"
                else -> "Complete Level"
              },
              onClick = {
                if (isProcessingClick) return@GameButton
                
                if (!isAnswerSubmitted) {
                  isProcessingClick = true
                  try {
                    val userSubmission = selectedAnswer ?: typedCode
                    val result = answerValidator.validate(
                      type = currentExercise.type,
                      submittedAnswer = userSubmission,
                      correctAnswers = currentExercise.parseCorrectAnswers(),
                      options = currentExercise.parseOptions(),
                      solutionCode = currentExercise.solutionCode,
                      expectedOutput = currentExercise.expectedOutput
                    )

                    validationResult = result
                    isAnswerSubmitted = true
                    
                    if (result.isCorrect) {
                      correctCount += 1
                      onCorrectAnswer()
                    } else {
                      mistakeCount += 1
                      onDeductHeart()
                      onRecordMistake(currentExercise, userSubmission)
                      onWrongAnswer()
                    }
                  } finally {
                    isProcessingClick = false
                  }
                } else {
                  // Advance or Complete
                  isProcessingClick = true
                  try {
                    if (exerciseIndex < totalExercises - 1) {
                      loadExercise(exerciseIndex + 1)
                    } else {
                      // Compute final score and transition to victory screen
                      val scoring = scoringService.calculateLessonScore(
                        totalExercises = totalExercises,
                        correctCount = correctCount,
                        mistakeCount = mistakeCount,
                        hintsUsedCount = hintsUsedCount,
                        baseXp = lesson.xpReward,
                        baseCoins = lesson.coinReward
                      )
                      completionScoringResult = scoring
                    }
                  } finally {
                    isProcessingClick = false
                  }
                }
              },
              style = if (isAnswerSubmitted && validationResult?.isCorrect == false) GameButtonStyle.DANGER else GameButtonStyle.PRIMARY,
              modifier = Modifier.padding(top = 12.dp),
              testTag = "lesson_action_button"
            )
          }
        }
      }
    }

    if (showQuitConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showQuitConfirmDialog = false },
        icon = {
          Icon(Icons.Default.Warning, contentDescription = null, tint = HeartRose, modifier = Modifier.size(32.dp))
        },
        title = {
          Text(
            text = "Quit Session?",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        text = {
          Text(
            text = "Wait, don't go! You'll lose your current streak bonuses and unsaved exercise state if you quit now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showQuitConfirmDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
            modifier = Modifier.testTag("btn_keep_learning")
          ) {
            Text("Keep Learning")
          }
        },
        dismissButton = {
          TextButton(
            onClick = {
              showQuitConfirmDialog = false
              onClose()
            },
            modifier = Modifier.testTag("btn_stop_session")
          ) {
            Text("Stop Session", color = HeartRose)
          }
        }
      )
    }

    if (showNoteDialog) {
      AlertDialog(
        onDismissRequest = { showNoteDialog = false },
        title = {
          Text(
            text = "Personal Note-Taking Pad",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        text = {
          Column {
            Text(
              text = "Jot down definitions, rules, syntax quirks, or reminders for '${lesson.title}'. These notes are synchronized offline and stored in your profile.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = noteText,
              onValueChange = { noteText = it },
              placeholder = { Text("Write your personal study notes here...") },
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("notes_input_field"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              onSaveNote(noteText)
              showNoteDialog = false
            },
            modifier = Modifier.testTag("btn_save_note_confirm")
          ) {
            Text("Save Note")
          }
        },
        dismissButton = {
          TextButton(
            onClick = { showNoteDialog = false },
            modifier = Modifier.testTag("btn_save_note_cancel")
          ) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
