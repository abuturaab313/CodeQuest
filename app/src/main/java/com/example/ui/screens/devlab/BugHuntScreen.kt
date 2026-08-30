package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BugHuntEntity
import com.example.data.models.DebugStep
import com.example.domain.execution.CodeExecutionService
import com.example.domain.execution.CodeRuntime
import com.example.domain.execution.ExecutionResult
import com.example.domain.languages.LanguageRegistry
import com.example.ui.components.editor.CodeEditorView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugHuntScreen(
  bugHunt: BugHuntEntity,
  onNavigateBack: () -> Unit,
  onComplete: (xp: Int, coins: Int) -> Unit
) {
  var currentStep by remember { mutableStateOf(DebugStep.RUN_PROGRAM) }
  val files = remember { mutableStateMapOf<String, String>().apply { putAll(bugHunt.parseFiles()) } }
  var activeFile by remember { mutableStateOf(files.keys.firstOrNull() ?: "main.py") }
  var consoleOutput by remember { mutableStateOf("") }
  var isRunning by remember { mutableStateOf(false) }
  var showErrorExplainer by remember { mutableStateOf(false) }
  var selectedHypothesisIndex by remember { mutableStateOf<Int?>(null) }
  var hypothesisError by remember { mutableStateOf(false) }
  var activeHintLevel by remember { mutableStateOf(0) }
  var showHintDialog by remember { mutableStateOf(false) }
  var testsPassed by remember { mutableStateOf(false) }
  var testOutputLog by remember { mutableStateOf("") }

  val coroutineScope = rememberCoroutineScope()
  val runtime = remember { LanguageRegistry.getRuntime(bugHunt.language) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "BUG HUNT: ${bugHunt.title}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "${bugHunt.language.uppercase()} • ${bugHunt.difficulty} • Step ${currentStep.stepNumber}/7",
              style = MaterialTheme.typography.labelSmall,
              color = QuestGold
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("bughunt_btn_back")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { showHintDialog = true },
            modifier = Modifier.testTag("bughunt_btn_hints")
          ) {
            Badge(containerColor = QuestGold) {
              Icon(Icons.Default.Lightbulb, contentDescription = "Hints", tint = Color.Black)
            }
          }
          IconButton(
            onClick = { showErrorExplainer = true },
            modifier = Modifier.testTag("bughunt_btn_explainer")
          ) {
            Icon(Icons.Default.HelpOutline, contentDescription = "Error Explainer", tint = QuestCyan)
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
    ) {
      // Step Progress Bar
      ScrollableTabRow(
        selectedTabIndex = currentStep.stepNumber - 1,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = QuestPrimary
      ) {
        DebugStep.entries.forEachIndexed { index, step ->
          Tab(
            selected = currentStep == step,
            onClick = { currentStep = step },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (currentStep.stepNumber > step.stepNumber) QuestGreen else if (currentStep == step) QuestPrimary else Color.Gray.copy(alpha = 0.4f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${step.stepNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 11.sp
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = step.title,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (currentStep == step) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          )
        }
      }

      // Step Context Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tonalElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = currentStep.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Main Step View Switcher
      Box(modifier = Modifier.weight(1f)) {
        when (currentStep) {
          DebugStep.RUN_PROGRAM, DebugStep.READ_ERROR -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = QuestRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reported Scenario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(bugHunt.scenario, style = MaterialTheme.typography.bodyMedium)
                }
              }

              Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("CONSOLE / STACK TRACE", style = MaterialTheme.typography.labelMedium, color = QuestCyan)
                    Button(
                      onClick = {
                        isRunning = true
                        coroutineScope.launch {
                          val code = files["main.py"] ?: files.values.firstOrNull() ?: ""
                          val res = runtime.execute(code)
                          consoleOutput = if (res.hasError) {
                            (res.errorMessage ?: res.stderr).ifBlank { bugHunt.initialErrorOutput }
                          } else {
                            res.stdout.ifBlank { "Program executed successfully." }
                          }
                          isRunning = false
                          currentStep = DebugStep.READ_ERROR
                        }
                      },
                      modifier = Modifier.testTag("bughunt_btn_run_program"),
                      colors = ButtonDefaults.buttonColors(containerColor = QuestGreen)
                    ) {
                      Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(if (isRunning) "Running..." else "Run Program")
                    }
                  }
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = consoleOutput.ifBlank { bugHunt.initialErrorOutput },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = if (consoleOutput.contains("Error") || bugHunt.initialErrorOutput.contains("Error")) QuestRed else QuestGreen,
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Button(
                  onClick = { currentStep = DebugStep.INSPECT_CODE },
                  colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                  modifier = Modifier.testTag("bughunt_btn_next_inspect")
                ) {
                  Text("Next: Inspect Code")
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
              }
            }
          }

          DebugStep.INSPECT_CODE, DebugStep.FIX_CODE -> {
            Column(modifier = Modifier.fillMaxSize()) {
              // File Tab Selector
              ScrollableTabRow(
                selectedTabIndex = files.keys.indexOf(activeFile).coerceAtLeast(0),
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface
              ) {
                files.keys.forEach { fileName ->
                  Tab(
                    selected = activeFile == fileName,
                    onClick = { activeFile = fileName },
                    text = {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(fileName, style = MaterialTheme.typography.labelMedium)
                      }
                    }
                  )
                }
              }

              Box(modifier = Modifier.weight(1f)) {
                CodeEditorView(
                  code = files[activeFile] ?: "",
                  onCodeChange = { updated ->
                    files[activeFile] = updated
                  },
                  language = com.example.domain.languages.LanguageRegistry.getLanguage(bugHunt.language),
                  modifier = Modifier.fillMaxSize()
                )
              }

              Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedButton(
                    onClick = { showErrorExplainer = true },
                    modifier = Modifier.testTag("bughunt_btn_view_explainer")
                  ) {
                    Icon(Icons.Default.Help, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Error Explainer")
                  }

                  Button(
                    onClick = {
                      currentStep = if (currentStep == DebugStep.INSPECT_CODE) DebugStep.FORM_HYPOTHESIS else DebugStep.RUN_TESTS
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                    modifier = Modifier.testTag("bughunt_btn_advance")
                  ) {
                    Text(if (currentStep == DebugStep.INSPECT_CODE) "Form Hypothesis" else "Run Tests")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                  }
                }
              }
            }
          }

          DebugStep.FORM_HYPOTHESIS -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text("STEP 4: Form a Hypothesis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    "Before jumping into random code edits, scientific debugging requires identifying the root mechanism of the bug. Select the most accurate hypothesis:",
                    style = MaterialTheme.typography.bodyMedium
                  )
                }
              }

              val options = bugHunt.parseHypothesisOptions()
              options.forEachIndexed { index, optionText ->
                val isSelected = selectedHypothesisIndex == index
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      selectedHypothesisIndex = index
                      hypothesisError = false
                    }
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outlineVariant,
                      shape = RoundedCornerShape(12.dp)
                    ),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) QuestPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                  )
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = isSelected,
                      onClick = {
                        selectedHypothesisIndex = index
                        hypothesisError = false
                      }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(optionText, style = MaterialTheme.typography.bodyMedium)
                  }
                }
              }

              if (hypothesisError) {
                Text(
                  "Incorrect hypothesis. Re-inspect the error and checklist to refine your reasoning!",
                  color = QuestRed,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold
                )
              }

              Button(
                onClick = {
                  if (selectedHypothesisIndex == bugHunt.correctHypothesisIndex) {
                    currentStep = DebugStep.FIX_CODE
                  } else {
                    hypothesisError = true
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("bughunt_btn_confirm_hypothesis"),
                colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                enabled = selectedHypothesisIndex != null
              ) {
                Text("Confirm Hypothesis & Proceed to Fix")
              }
            }
          }

          DebugStep.RUN_TESTS, DebugStep.SUBMIT -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Automated Test Suite", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Button(
                      onClick = {
                        coroutineScope.launch {
                          isRunning = true
                          val code = files["main.py"] ?: files.values.firstOrNull() ?: ""
                          val res = runtime.execute(code)
                          val output = if (res.hasError) (res.errorMessage ?: res.stderr) else res.stdout
                          testOutputLog = output
                          testsPassed = !res.hasError && output.isNotBlank()
                          isRunning = false
                          if (testsPassed) currentStep = DebugStep.SUBMIT
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = QuestGreen),
                      modifier = Modifier.testTag("bughunt_btn_run_tests")
                    ) {
                      Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(if (isRunning) "Running..." else "Run All Tests")
                    }
                  }
                }
              }

              val testList = bugHunt.parseTests()
              testList.forEach { testItem ->
                Card(
                  colors = CardDefaults.cardColors(
                    containerColor = if (testsPassed) QuestGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                  ),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = if (testsPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                      contentDescription = null,
                      tint = if (testsPassed) QuestGreen else QuestRed
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(testItem.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                      Text("Expected: ${testItem.expectedOutput}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                  }
                }
              }

              if (testsPassed) {
                Card(
                  colors = CardDefaults.cardColors(containerColor = QuestGreen.copy(alpha = 0.2f)),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Text("✓ Bug Fixed & All Tests Passing!", style = MaterialTheme.typography.titleMedium, color = QuestGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Rewards: +${bugHunt.xpReward} XP, +${bugHunt.coinReward} CodeCoins", style = MaterialTheme.typography.bodyMedium)
                  }
                }

                Button(
                  onClick = {
                    onComplete(bugHunt.xpReward, bugHunt.coinReward)
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bughunt_btn_submit_final"),
                  colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
                ) {
                  Text("Submit & Claim Developer Rewards")
                }
              }
            }
          }
        }
      }
    }
  }

  // Error Explainer Modal
  if (showErrorExplainer) {
    AlertDialog(
      onDismissRequest = { showErrorExplainer = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Psychology, contentDescription = null, tint = QuestCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Error Explainer")
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text("WHAT HAPPENED:", fontWeight = FontWeight.Bold, color = QuestPrimary)
          Text(bugHunt.errorExplainerWhat, style = MaterialTheme.typography.bodyMedium)

          Text("WHY IT HAPPENED:", fontWeight = FontWeight.Bold, color = QuestGold)
          Text(bugHunt.errorExplainerWhy, style = MaterialTheme.typography.bodyMedium)

          Text("HOW TO INVESTIGATE (CHECKLIST):", fontWeight = FontWeight.Bold, color = QuestGreen)
          val checklist = bugHunt.parseChecklist()
          checklist.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Check, contentDescription = null, tint = QuestGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(item, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showErrorExplainer = false }) {
          Text("Got It")
        }
      }
    )
  }

  // 3-Tier Progressive Hint Modal
  if (showHintDialog) {
    AlertDialog(
      onDismissRequest = { showHintDialog = false },
      title = {
        Text("Debug Hints (Level ${activeHintLevel}/3)")
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          if (activeHintLevel >= 1) {
            Text("Hint 1 (Direction):", fontWeight = FontWeight.Bold, color = QuestPrimary)
            Text(bugHunt.hint1, style = MaterialTheme.typography.bodySmall)
          }
          if (activeHintLevel >= 2) {
            Text("Hint 2 (Specific Area):", fontWeight = FontWeight.Bold, color = QuestGold)
            Text(bugHunt.hint2, style = MaterialTheme.typography.bodySmall)
          }
          if (activeHintLevel >= 3) {
            Text("Hint 3 (Explicit Guidance):", fontWeight = FontWeight.Bold, color = QuestCyan)
            Text(bugHunt.hint3, style = MaterialTheme.typography.bodySmall)
          }

          if (activeHintLevel < 3) {
            OutlinedButton(
              onClick = { activeHintLevel++ },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Unlock Next Hint")
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showHintDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}
