package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.TestFirstChallengeEntity
import com.example.domain.languages.LanguageRegistry
import com.example.ui.components.editor.CodeEditorView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestFirstScreen(
  challenge: TestFirstChallengeEntity,
  onNavigateBack: () -> Unit,
  onComplete: (xp: Int, coins: Int) -> Unit
) {
  val files = remember { mutableStateMapOf<String, String>().apply { putAll(challenge.parseFiles()) } }
  var activeFile by remember { mutableStateOf(files.keys.firstOrNull() ?: "user_service.py") }
  var isRunning by remember { mutableStateOf(false) }
  var isTestExplorerExpanded by remember { mutableStateOf(true) }
  var passingTestsCount by remember { mutableStateOf(0) }
  var testRunDone by remember { mutableStateOf(false) }
  var showCoverageInfo by remember { mutableStateOf(false) }

  val tests = remember { challenge.parseTests() }
  val requirements = remember { challenge.parseRequirements() }
  val coroutineScope = rememberCoroutineScope()
  val runtime = remember { LanguageRegistry.getRuntime(challenge.language) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "TEST FIRST: ${challenge.title}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "TDD Workflow • ${passingTestsCount}/${tests.size} Tests Passing",
              style = MaterialTheme.typography.labelSmall,
              color = if (passingTestsCount == tests.size) QuestGreen else QuestGold
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("testfirst_btn_back")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { showCoverageInfo = true },
            modifier = Modifier.testTag("testfirst_btn_coverage")
          ) {
            Icon(Icons.Default.Speed, contentDescription = "Test Coverage", tint = QuestCyan)
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
      // Educational Coverage Meter Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = QuestCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Suite Coverage: ${challenge.estimatedCoveragePercent}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
          Text(
            text = if (passingTestsCount == tests.size) "✓ All Passing" else "✗ Requirements Incomplete",
            style = MaterialTheme.typography.labelSmall,
            color = if (passingTestsCount == tests.size) QuestGreen else QuestRed,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Requirements & Acceptance Criteria (Collapsible)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text("ACCEPTANCE CRITERIA", style = MaterialTheme.typography.labelSmall, color = QuestPrimary, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(4.dp))
          Text(challenge.acceptanceCriteria, style = MaterialTheme.typography.bodySmall)

          Spacer(modifier = Modifier.height(8.dp))
          Text("Requirements Checklist:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          requirements.forEach { req ->
            Row(
              modifier = Modifier.padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (passingTestsCount == tests.size) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (passingTestsCount == tests.size) QuestGreen else Color.Gray,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(req, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }

      // File Tabs
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

      // Editor Area
      Box(modifier = Modifier.weight(1f)) {
        CodeEditorView(
          code = files[activeFile] ?: "",
          onCodeChange = { updated ->
            files[activeFile] = updated
          },
          language = com.example.domain.languages.LanguageRegistry.getLanguage(challenge.language),
          modifier = Modifier.fillMaxSize()
        )
      }

      // Test Explorer Bottom Sheet Panel
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { isTestExplorerExpanded = !isTestExplorerExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Rule, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("TEST EXPLORER (${passingTestsCount}/${tests.size})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Icon(
              imageVector = if (isTestExplorerExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null
            )
          }

          if (isTestExplorerExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            tests.forEach { testItem ->
              val isPass = testRunDone && passingTestsCount == tests.size
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isPass) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                  contentDescription = null,
                  tint = if (isPass) QuestGreen else Color.Gray,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(testItem.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                  Text("Expected output contains: ${testItem.expectedOutput}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                coroutineScope.launch {
                  isRunning = true
                  val code = files["main.py"] ?: files.values.firstOrNull() ?: ""
                  val res = runtime.execute(code)
                  testRunDone = true
                  val pass = !res.hasError && res.stdout.isNotBlank()
                  passingTestsCount = if (pass) tests.size else 0
                  isRunning = false
                }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("testfirst_btn_run_tests"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestGreen)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (isRunning) "Running Tests..." else "Run All Tests")
            }

            if (passingTestsCount == tests.size) {
              Button(
                onClick = { onComplete(challenge.xpReward, challenge.coinReward) },
                colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                modifier = Modifier
                  .weight(1f)
                  .testTag("testfirst_btn_complete")
              ) {
                Text("Claim (+${challenge.xpReward} XP)")
              }
            }
          }
        }
      }
    }
  }

  // Coverage Concept Dialog
  if (showCoverageInfo) {
    AlertDialog(
      onDismissRequest = { showCoverageInfo = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Speed, contentDescription = null, tint = QuestCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Understanding Test Coverage")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("What is Test Coverage?", fontWeight = FontWeight.Bold, color = QuestPrimary)
          Text(
            "Test coverage measures the percentage of code executed while running automated tests. High coverage gives developers confidence that edge cases, exception handlers, and happy paths won't fail in production.",
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text("Test-Driven Development (TDD) Rule:", fontWeight = FontWeight.Bold, color = QuestGold)
          Text(
            "Write the failing test FIRST, then implement the minimal code needed to make it pass, and finally refactor for clean design.",
            style = MaterialTheme.typography.bodySmall
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { showCoverageInfo = false }) {
          Text("Got It")
        }
      }
    )
  }
}
