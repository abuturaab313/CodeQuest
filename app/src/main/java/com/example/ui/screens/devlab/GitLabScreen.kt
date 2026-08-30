package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.models.GitExerciseEntity
import com.example.domain.services.GitRepoState
import com.example.domain.services.GitSimulatorEngine
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitLabScreen(
  exercise: GitExerciseEntity,
  onNavigateBack: () -> Unit,
  onComplete: (xp: Int, coins: Int) -> Unit
) {
  var gitState by remember {
    mutableStateOf(
      GitSimulatorEngine.initScenario(
        branch = exercise.initialBranch,
        branches = exercise.parseBranches(),
        workingFiles = exercise.parseWorkingFiles(),
        stagedFiles = exercise.parseStagedFiles(),
        conflictFile = if (exercise.expectedAction == "RESOLVE_CONFLICT") "app_config.py" else null
      )
    )
  }

  var selectedCommitMessageIndex by remember { mutableStateOf(0) }
  var showNewBranchDialog by remember { mutableStateOf(false) }
  var newBranchInput by remember { mutableStateOf("") }
  var isConflictResolved by remember { mutableStateOf(false) }
  var isExerciseCompleted by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "GIT LAB: ${exercise.title}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            Text(
              text = "Branch: ${gitState.currentBranch} • ${exercise.concept}",
              style = MaterialTheme.typography.labelSmall,
              color = QuestCyan
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("gitlab_btn_back")
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
    ) {
      // Concept Card & Objective
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountTree, contentDescription = null, tint = QuestPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("MISSION OBJECTIVE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = QuestPrimary)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(exercise.taskPrompt, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(6.dp))
          Text(exercise.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
      }

      // Visual Git Workflow Diagram (Working Dir -> Staging Area -> Commits)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Working Directory Column
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Working Directory", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            if (gitState.workingDirectory.isEmpty()) {
              Text("Clean", style = MaterialTheme.typography.bodySmall, color = QuestGreen)
            } else {
              gitState.workingDirectory.forEach { file ->
                Text("• $file", style = MaterialTheme.typography.bodySmall, color = if (file.contains("conflict")) QuestRed else QuestGold)
              }
            }
          }
        }

        // Staging Area Column
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Staging Area (Index)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            if (gitState.stagingArea.isEmpty()) {
              Text("Empty", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
              gitState.stagingArea.forEach { file ->
                Text("✓ $file", style = MaterialTheme.typography.bodySmall, color = QuestGreen, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Branch & HEAD Column
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Branches", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            gitState.branches.forEach { b ->
              val isHead = b == gitState.currentBranch
              Text(
                text = "${if (isHead) "* " else "  "}$b",
                style = MaterialTheme.typography.bodySmall,
                color = if (isHead) QuestCyan else Color.Gray,
                fontWeight = if (isHead) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      // Conflict Visualizer if active
      if (gitState.isConflictPresent && !isConflictResolved) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          colors = CardDefaults.cardColors(containerColor = QuestRed.copy(alpha = 0.1f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = QuestRed)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Merge Conflict Detected in app_config.py", fontWeight = FontWeight.Bold, color = QuestRed)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              color = Color(0xFF1E1E2E),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = exercise.conflictFileContent.ifBlank { "<<<<<<< HEAD\nAPP_TITLE = \"CodeQuest Student Portal\"\n=======\nAPP_TITLE = \"CodeQuest Developer Studio\"\n>>>>>>> feature-ui" },
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(10.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = {
                val (newState, _) = GitSimulatorEngine.executeResolveConflict(gitState, "app_config.py")
                gitState = newState
                isConflictResolved = true
                isExerciseCompleted = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = QuestGreen),
              modifier = Modifier.testTag("gitlab_btn_resolve_conflict")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Accept Incoming Change & Resolve Conflict")
            }
          }
        }
      }

      // Commit Message Training Section if expected action is COMMIT or BOSS
      val commitOptions = exercise.parseCommitMessageOptions()
      if (commitOptions.isNotEmpty() && !isExerciseCompleted) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Commit Message Crafting:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            commitOptions.forEachIndexed { index, msg ->
              val isSelected = selectedCommitMessageIndex == index
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { selectedCommitMessageIndex = index }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { selectedCommitMessageIndex = index }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = msg,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }

      // Terminal Output Box
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181825)),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
        ) {
          gitState.terminalOutput.forEach { logLine ->
            Text(
              text = logLine,
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = if (logLine.startsWith("$")) QuestCyan else if (logLine.contains("error") || logLine.contains("conflict")) QuestRed else Color(0xFFCDD6F4)
            )
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }

      // Action Control Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Command Buttons Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                val (newState, _) = GitSimulatorEngine.executeGitStatus(gitState)
                gitState = newState
                if (exercise.expectedAction == "STATUS") isExerciseCompleted = true
              },
              modifier = Modifier
                .weight(1f)
                .testTag("gitlab_btn_status"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
            ) {
              Text("git status", fontSize = 12.sp)
            }

            Button(
              onClick = {
                val (newState, _) = GitSimulatorEngine.executeGitAdd(gitState, ".")
                gitState = newState
                if (exercise.expectedAction == "STAGE") isExerciseCompleted = true
              },
              modifier = Modifier
                .weight(1f)
                .testTag("gitlab_btn_add"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestGreen)
            ) {
              Text("git add .", fontSize = 12.sp)
            }

            Button(
              onClick = {
                val msg = commitOptions.getOrNull(selectedCommitMessageIndex) ?: "Save changes"
                val (newState, _) = GitSimulatorEngine.executeGitCommit(gitState, msg)
                gitState = newState
                if (exercise.expectedAction == "COMMIT" || exercise.expectedAction == "BOSS") {
                  isExerciseCompleted = true
                }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("gitlab_btn_commit"),
              colors = ButtonDefaults.buttonColors(containerColor = QuestGold),
              enabled = gitState.stagingArea.isNotEmpty()
            ) {
              Text("git commit", fontSize = 12.sp, color = Color.Black)
            }
          }

          // Branch & Complete Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = { showNewBranchDialog = true },
              modifier = Modifier
                .weight(1f)
                .testTag("gitlab_btn_branch")
            ) {
              Icon(Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("git branch", fontSize = 12.sp)
            }

            if (isExerciseCompleted) {
              Button(
                onClick = { onComplete(exercise.xpReward, exercise.coinReward) },
                colors = ButtonDefaults.buttonColors(containerColor = QuestGreen),
                modifier = Modifier
                  .weight(1.5f)
                  .testTag("gitlab_btn_complete")
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Complete (+${exercise.xpReward} XP)")
              }
            }
          }
        }
      }
    }
  }

  // New Branch Dialog
  if (showNewBranchDialog) {
    AlertDialog(
      onDismissRequest = { showNewBranchDialog = false },
      title = { Text("Create & Switch Branch") },
      text = {
        OutlinedTextField(
          value = newBranchInput,
          onValueChange = { newBranchInput = it },
          label = { Text("Branch Name (e.g. feature-login)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (newBranchInput.isNotBlank()) {
              val (newState, _) = GitSimulatorEngine.executeGitBranch(gitState, newBranchInput)
              gitState = newState
              if (exercise.expectedAction == "BRANCH") isExerciseCompleted = true
            }
            showNewBranchDialog = false
          }
        ) {
          Text("Checkout -b")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewBranchDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
