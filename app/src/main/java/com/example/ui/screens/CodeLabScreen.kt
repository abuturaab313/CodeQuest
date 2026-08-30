package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import com.example.domain.ai.models.LearningContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.SubmissionRecordEntity
import com.example.domain.execution.ExecutionResult
import com.example.domain.execution.TestSuiteResult
import com.example.domain.languages.LanguageRegistry
import com.example.domain.services.SubmissionResult
import com.example.ui.MainViewModel
import com.example.ui.components.editor.CodeEditorView
import com.example.ui.components.editor.SyntaxTheme
import com.example.ui.components.lab.ConsoleOutputView
import com.example.ui.components.lab.ProblemDescriptionView
import com.example.ui.components.lab.SubmissionSuccessDialog
import com.example.ui.components.lab.TestResultsView
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LabTab {
  PROBLEM,
  EDITOR,
  CONSOLE,
  TESTS,
  HISTORY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeLabScreen(
  challengeId: String,
  viewModel: MainViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToNextChallenge: (String) -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  var challenge by remember { mutableStateOf<CodingChallengeEntity?>(null) }
  var userCode by remember { mutableStateOf("") }
  var selectedTab by remember { mutableStateOf(LabTab.PROBLEM) }
  var isFullscreen by remember { mutableStateOf(false) }
  var hintsUnlockedCount by remember { mutableIntStateOf(0) }
  var selectedLanguageId by remember { mutableStateOf("python") }
  var showUnsavedChangesDialog by remember { mutableStateOf(false) }
  var pendingLanguageSwitchId by remember { mutableStateOf<String?>(null) }

  // Execution & Testing State
  var isRunningCode by remember { mutableStateOf(false) }
  var isRunningTests by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }
  var executionResult by remember { mutableStateOf<ExecutionResult?>(null) }
  var testSuiteResult by remember { mutableStateOf<TestSuiteResult?>(null) }
  var activeSubmissionResult by remember { mutableStateOf<SubmissionResult?>(null) }

  // Submissions Flow
  val submissionHistory by viewModel.getSubmissionsForChallenge(challengeId)
    .collectAsState(initial = emptyList())

  // 1. Initial Load & Restore Draft
  LaunchedEffect(challengeId) {
    val loaded = viewModel.getChallengeById(challengeId)
    challenge = loaded
    if (loaded != null) {
      selectedLanguageId = loaded.languageId
      val progress = viewModel.getChallengeProgress(challengeId)
      userCode = if (progress != null && progress.draftCode.isNotBlank()) {
        hintsUnlockedCount = progress.hintsUsedCount
        progress.draftCode
      } else {
        loaded.starterCode
      }
    }
  }

  // 2. Debounced Autosave on Code Change
  LaunchedEffect(userCode) {
    if (challenge != null && userCode.isNotBlank()) {
      delay(800) // 800ms debounce
      viewModel.saveChallengeDraft(challengeId, userCode, hintsUnlockedCount)
    }
  }

  val activeChallenge = challenge
  if (activeChallenge == null) {
    Box(
      modifier = Modifier.fillMaxSize().background(SyntaxTheme.Background),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = QuestPrimary)
    }
    return
  }

  val languageDef = remember(selectedLanguageId) {
    LanguageRegistry.getLanguage(selectedLanguageId)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = activeChallenge.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              color = MaterialTheme.colorScheme.onSurface
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = languageDef.name,
                style = MaterialTheme.typography.labelSmall,
                color = QuestPrimary
              )
              Text("•", color = MaterialTheme.colorScheme.outline)
              Text(
                text = activeChallenge.difficulty.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = {
              val coachContext = LearningContext(
                sourceScreen = "CODE_LAB",
                challengeTitle = activeChallenge.title,
                challengeDescription = activeChallenge.description,
                starterCode = activeChallenge.starterCode,
                currentCode = userCode,
                recentError = executionResult?.stderr ?: executionResult?.errorMessage,
                testSummary = testSuiteResult?.let { "${it.passedCount} of ${it.totalCount} tests passed" },
                activeConcept = activeChallenge.category
              )
              viewModel.openCodeCoach(coachContext)
            },
            modifier = Modifier.testTag("btn_open_coach_codelab")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Code Coach",
              tint = QuestPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      // Bottom Action Toolbar (Run, Test, Submit)
      CodeLabBottomBar(
        isRunningCode = isRunningCode,
        isRunningTests = isRunningTests,
        isSubmitting = isSubmitting,
        onRunCode = {
          scope.launch {
            isRunningCode = true
            selectedTab = LabTab.CONSOLE
            val res = viewModel.executeUserCode(userCode, selectedLanguageId)
            executionResult = res
            isRunningCode = false
          }
        },
        onRunTests = {
          scope.launch {
            isRunningTests = true
            selectedTab = LabTab.TESTS
            val res = viewModel.runPublicTests(activeChallenge, userCode, selectedLanguageId)
            testSuiteResult = res
            isRunningTests = false
          }
        },
        onSubmit = {
          scope.launch {
            isSubmitting = true
            val res = viewModel.submitChallenge(activeChallenge, userCode, hintsUnlockedCount, selectedLanguageId)
            testSuiteResult = res.testSuiteResult
            if (res.isPassed) {
              activeSubmissionResult = res
            } else {
              selectedTab = LabTab.TESTS
            }
            isSubmitting = false
          }
        }
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Primary Tab Row
      PrimaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = QuestPrimary
      ) {
        Tab(
          selected = selectedTab == LabTab.PROBLEM,
          onClick = { selectedTab = LabTab.PROBLEM },
          text = { Text("Problem", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Description, contentDescription = "Problem", modifier = Modifier.size(16.dp)) }
        )
        Tab(
          selected = selectedTab == LabTab.EDITOR,
          onClick = { selectedTab = LabTab.EDITOR },
          text = { Text("Editor", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Code, contentDescription = "Editor", modifier = Modifier.size(16.dp)) }
        )
        Tab(
          selected = selectedTab == LabTab.CONSOLE,
          onClick = { selectedTab = LabTab.CONSOLE },
          text = { Text("Console", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Terminal, contentDescription = "Console", modifier = Modifier.size(16.dp)) }
        )
        Tab(
          selected = selectedTab == LabTab.TESTS,
          onClick = { selectedTab = LabTab.TESTS },
          text = { Text("Tests", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.PlaylistPlay, contentDescription = "Tests", modifier = Modifier.size(16.dp)) }
        )
        Tab(
          selected = selectedTab == LabTab.HISTORY,
          onClick = { selectedTab = LabTab.HISTORY },
          text = { Text("History", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(16.dp)) }
        )
      }

      // Tab Body Content
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
      ) {
        when (selectedTab) {
          LabTab.PROBLEM -> {
            ProblemDescriptionView(
              challenge = activeChallenge,
              hintsUnlockedCount = hintsUnlockedCount,
              onUnlockNextHint = {
                val hints = activeChallenge.parseHints()
                if (hintsUnlockedCount < hints.size) {
                  hintsUnlockedCount++
                  viewModel.saveChallengeDraft(challengeId, userCode, hintsUnlockedCount)
                }
              }
            )
          }
          LabTab.EDITOR -> {
            Column(modifier = Modifier.fillMaxSize()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 8.dp)
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                  .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = QuestPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "LANGUAGE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box {
                  var showDropdown by remember { mutableStateOf(false) }
                  Button(
                    onClick = { showDropdown = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Text(
                      text = "${LanguageRegistry.getLanguage(selectedLanguageId).name} ▼",
                      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                  }

                  DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false }
                  ) {
                    val availableLanguages = LanguageRegistry.getAllLanguages()
                    availableLanguages.forEach { lang ->
                      DropdownMenuItem(
                        text = { Text(lang.name) },
                        onClick = {
                          showDropdown = false
                          if (selectedLanguageId != lang.id) {
                            pendingLanguageSwitchId = lang.id
                            val currentStarterTemplate = LanguageRegistry.getLanguage(selectedLanguageId).starterTemplate
                            if (userCode.trim() != currentStarterTemplate.trim() && userCode.isNotBlank()) {
                              showUnsavedChangesDialog = true
                            } else {
                              selectedLanguageId = lang.id
                              userCode = lang.starterTemplate
                            }
                          }
                        }
                      )
                    }
                  }
                }
              }

              CodeEditorView(
                code = userCode,
                onCodeChange = { userCode = it },
                language = languageDef,
                starterCode = LanguageRegistry.getLanguage(selectedLanguageId).starterTemplate,
                isFullscreen = isFullscreen,
                onToggleFullscreen = { isFullscreen = !isFullscreen }
              )
            }
          }
          LabTab.CONSOLE -> {
            ConsoleOutputView(
              executionResult = executionResult,
              isRunning = isRunningCode,
              onRunCustomInput = { customInput ->
                scope.launch {
                  isRunningCode = true
                  val res = viewModel.executeUserCode(userCode, selectedLanguageId, customInput)
                  executionResult = res
                  isRunningCode = false
                }
              },
              onClearConsole = { executionResult = null }
            )
          }
          LabTab.TESTS -> {
            TestResultsView(
              testSuiteResult = testSuiteResult,
              isRunningTests = isRunningTests
            )
          }
          LabTab.HISTORY -> {
            SubmissionHistoryView(
              submissions = submissionHistory,
              onLoadSubmission = { oldCode ->
                userCode = oldCode
                selectedTab = LabTab.EDITOR
              }
            )
          }
        }
      }
    }
  }

  // Celebratory Success Modal
  activeSubmissionResult?.let { result ->
    SubmissionSuccessDialog(
      submissionResult = result,
      challengeTitle = activeChallenge.title,
      onDismiss = { activeSubmissionResult = null },
      onNextChallenge = {
        activeSubmissionResult = null
        onNavigateBack()
      }
    )
  }

  if (showUnsavedChangesDialog) {
    val targetLangName = pendingLanguageSwitchId?.let { LanguageRegistry.getLanguage(it).name } ?: "selected language"
    AlertDialog(
      onDismissRequest = { showUnsavedChangesDialog = false },
      title = { Text("UNSAVED CHANGES") },
      text = { Text("You have unsaved changes in your current code. Would you like to save them as a draft before switching to $targetLangName?") },
      confirmButton = {
        TextButton(
          onClick = {
            showUnsavedChangesDialog = false
            val langToSwitch = pendingLanguageSwitchId
            if (langToSwitch != null) {
              viewModel.saveChallengeDraft(challengeId, userCode, hintsUnlockedCount)
              selectedLanguageId = langToSwitch
              userCode = LanguageRegistry.getLanguage(langToSwitch).starterTemplate
            }
          }
        ) {
          Text("SAVE")
        }
      },
      dismissButton = {
        Row {
          TextButton(
            onClick = {
              showUnsavedChangesDialog = false
              val langToSwitch = pendingLanguageSwitchId
              if (langToSwitch != null) {
                selectedLanguageId = langToSwitch
                userCode = LanguageRegistry.getLanguage(langToSwitch).starterTemplate
              }
            }
          ) {
            Text("DISCARD", color = MaterialTheme.colorScheme.error)
          }
          Spacer(modifier = Modifier.width(8.dp))
          TextButton(onClick = { showUnsavedChangesDialog = false }) {
            Text("CANCEL")
          }
        }
      }
    )
  }
}

@Composable
private fun CodeLabBottomBar(
  isRunningCode: Boolean,
  isRunningTests: Boolean,
  isSubmitting: Boolean,
  onRunCode: () -> Unit,
  onRunTests: () -> Unit,
  onSubmit: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 8.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Run Code Button
      OutlinedButton(
        onClick = onRunCode,
        enabled = !isRunningCode && !isRunningTests && !isSubmitting,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f).height(44.dp).testTag("run_code_button")
      ) {
        if (isRunningCode) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Run", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      // 2. Run Tests Button
      OutlinedButton(
        onClick = onRunTests,
        enabled = !isRunningCode && !isRunningTests && !isSubmitting,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = QuestPrimary),
        modifier = Modifier.weight(1.2f).height(44.dp).testTag("run_tests_button")
      ) {
        if (isRunningTests) {
          CircularProgressIndicator(color = QuestPrimary, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.Default.PlaylistPlay, contentDescription = "Test", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Test Suite", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      // 3. Submit Button
      Button(
        onClick = onSubmit,
        enabled = !isRunningCode && !isRunningTests && !isSubmitting,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = QuestGreen),
        modifier = Modifier.weight(1.3f).height(44.dp).testTag("submit_challenge_button")
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.Default.Send, contentDescription = "Submit", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Submit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun SubmissionHistoryView(
  submissions: List<SubmissionRecordEntity>,
  onLoadSubmission: (String) -> Unit
) {
  if (submissions.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No submissions yet. Submit your code to see history.",
        style = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
      )
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(submissions) { sub ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            val isPassed = sub.verdict == "PASSED"
            val badgeColor = if (isPassed) QuestGreen else QuestRed

            Surface(
              color = badgeColor.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = sub.verdict,
                color = badgeColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${sub.passedTests} / ${sub.totalTests} Tests (${sub.executionTimeMs}ms)",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
              )
              if (sub.xpEarned > 0) {
                Text(
                  text = "+${sub.xpEarned} XP Earned",
                  style = MaterialTheme.typography.labelSmall,
                  color = QuestPrimary
                )
              }
            }

            OutlinedButton(
              onClick = { onLoadSubmission(sub.codeSnippet) },
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Load", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }

}
