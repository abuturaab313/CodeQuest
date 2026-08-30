package com.example.ui.screens.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.repository.ProjectRepository
import com.example.data.repository.ProjectSubmissionResult
import com.example.domain.execution.ExecutionResult
import com.example.domain.execution.ProjectTestSuiteResult
import com.example.domain.languages.LanguageRegistry
import com.example.ui.MainViewModel
import com.example.ui.audio.LocalSoundManager
import com.example.ui.components.editor.CodeEditorView
import com.example.ui.components.project.DeleteFileDialog
import com.example.ui.components.project.NewFileDialog
import com.example.ui.components.project.ProjectCompletionDialog
import com.example.ui.components.project.ProjectConsoleView
import com.example.ui.components.project.ProjectFileTabs
import com.example.ui.components.project.ProjectReadmeView
import com.example.ui.components.project.ProjectTasksView
import com.example.ui.components.project.ProjectTestsView
import com.example.ui.components.project.RenameFileDialog
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class WorkspaceTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  CODE("Code", Icons.Default.Code),
  TASKS("Tasks", Icons.Default.Checklist),
  CONSOLE("Console", Icons.Default.Terminal),
  TESTS("Tests", Icons.Default.Science),
  GUIDE("Guide", Icons.Default.Description)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectWorkspaceScreen(
  projectId: String,
  viewModel: MainViewModel,
  onNavigateBack: () -> Unit
) {
  val projectRepo = remember { viewModel.projectRepository }
  val coroutineScope = rememberCoroutineScope()
  val soundManager = LocalSoundManager.current

  val project by projectRepo.observeProjectById(projectId).collectAsState(initial = null)
  val files by projectRepo.getFilesForProject(projectId).collectAsState(initial = emptyList())
  val progress by projectRepo.getProjectProgress(projectId).collectAsState(initial = null)

  var activeFileName by remember { mutableStateOf("main.py") }
  var currentCode by remember { mutableStateOf("") }
  var hasUnsavedChanges by remember { mutableStateOf(false) }
  var saveStatusText by remember { mutableStateOf("Saved") }

  var selectedTab by remember { mutableStateOf(WorkspaceTab.CODE) }
  var isExecuting by remember { mutableStateOf(false) }
  var isRunningTests by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }

  var lastExecutionResult by remember { mutableStateOf<ExecutionResult?>(null) }
  var lastTestSuiteResult by remember { mutableStateOf<ProjectTestSuiteResult?>(null) }
  var submissionResult by remember { mutableStateOf<ProjectSubmissionResult?>(null) }

  // Dialog States
  var showNewFileDialog by remember { mutableStateOf(false) }
  var fileToRename by remember { mutableStateOf<String?>(null) }
  var fileToDelete by remember { mutableStateOf<String?>(null) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showLeaveConfirmDialog by remember { mutableStateOf(false) }
  var showMoreMenu by remember { mutableStateOf(false) }
  var showCommitDialog by remember { mutableStateOf(false) }
  var showHistoryDialog by remember { mutableStateOf(false) }
  var versionHistoryList by remember { mutableStateOf<List<com.example.data.models.ProjectVersionEntity>>(emptyList()) }

  // Debounced auto-save job
  var autoSaveJob by remember { mutableStateOf<Job?>(null) }

  // Initialize starter files if needed
  LaunchedEffect(project) {
    project?.let { proj ->
      projectRepo.ensureStarterFiles(proj)
    }
  }

  // Update active file content when files list changes or activeFileName changes
  LaunchedEffect(files, activeFileName) {
    if (files.isNotEmpty()) {
      val target = files.find { it.fileName == activeFileName } ?: files.find { it.fileName == "main.py" } ?: files.first()
      if (target.fileName != activeFileName) {
        activeFileName = target.fileName
      }
      if (!hasUnsavedChanges || currentCode.isEmpty()) {
        currentCode = target.fileContent
        hasUnsavedChanges = false
        saveStatusText = "Saved"
      }
    }
  }

  val starterFilesMap = remember(project) { project?.parseStarterFiles() ?: emptyMap() }
  val completedTaskIds = remember(progress) { progress?.parseCompletedTaskIds()?.toSet() ?: emptySet() }
  val currentFileEntity = remember(files, activeFileName) { files.find { it.fileName == activeFileName } }

  // Auto-save logic
  fun triggerAutoSave(newCode: String) {
    currentCode = newCode
    hasUnsavedChanges = true
    saveStatusText = "Unsaved"
    autoSaveJob?.cancel()
    autoSaveJob = coroutineScope.launch {
      delay(1500)
      saveStatusText = "Saving..."
      projectRepo.saveFile(projectId, activeFileName, newCode)
      hasUnsavedChanges = false
      saveStatusText = "Saved"
    }
  }

  // Explicit Save
  fun saveCurrentFileNow() {
    autoSaveJob?.cancel()
    coroutineScope.launch {
      saveStatusText = "Saving..."
      projectRepo.saveFile(projectId, activeFileName, currentCode)
      hasUnsavedChanges = false
      saveStatusText = "Saved"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = project?.title ?: "Project Workspace",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
              )
              Spacer(modifier = Modifier.width(8.dp))
              project?.let { proj ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = QuestPrimary.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = proj.difficulty,
                    style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(if (hasUnsavedChanges) QuestGold else QuestSuccess)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = saveStatusText,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (hasUnsavedChanges) QuestGold else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              )
            }
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              if (hasUnsavedChanges) {
                showLeaveConfirmDialog = true
              } else {
                onNavigateBack()
              }
            },
            modifier = Modifier.testTag("btn_back_project")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Projects"
            )
          }
        },
        actions = {
          // Run Button
          Button(
            onClick = {
              saveCurrentFileNow()
              isExecuting = true
              selectedTab = WorkspaceTab.CONSOLE
              coroutineScope.launch {
                val res = projectRepo.executeProject(
                  projectId = projectId,
                  entryFileName = "main.py",
                  rawInput = ""
                )
                lastExecutionResult = res
                isExecuting = false
              }
            },
            enabled = !isExecuting,
            colors = ButtonDefaults.buttonColors(
              containerColor = QuestPrimary,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(36.dp).testTag("btn_run_project")
          ) {
            if (isExecuting) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Run", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Submit Project Button
          Button(
            onClick = {
              saveCurrentFileNow()
              isSubmitting = true
              isRunningTests = true
              coroutineScope.launch {
                project?.let { proj ->
                  val subRes = projectRepo.submitProject(proj)
                  submissionResult = subRes
                  lastTestSuiteResult = subRes.testSuiteResult
                  if (!subRes.isCompleted) {
                    selectedTab = WorkspaceTab.TESTS
                  }
                }
                isSubmitting = false
                isRunningTests = false
              }
            },
            enabled = !isSubmitting && !isRunningTests,
            colors = ButtonDefaults.buttonColors(
              containerColor = QuestSuccess,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(36.dp).testTag("btn_submit_project")
          ) {
            if (isSubmitting) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Submit", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }

          // Portfolio Toggle (Custom Projects Only)
          project?.takeIf { it.isCustom }?.let { proj ->
            IconButton(
              onClick = { viewModel.toggleProjectPortfolio(proj.id, !proj.isPortfolio) },
              modifier = Modifier.testTag("btn_toggle_portfolio")
            ) {
              Icon(
                imageVector = Icons.Default.Work,
                contentDescription = "Toggle Portfolio",
                tint = if (proj.isPortfolio) QuestGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
              )
            }
          }

          // Code Coach Button
          IconButton(
            onClick = {
              val allWorkspaceFiles = files.associate { it.fileName to (if (it.fileName == activeFileName) currentCode else it.fileContent) }
              val coachContext = com.example.domain.ai.models.LearningContext(
                sourceScreen = "PROJECT",
                projectTitle = project?.title,
                activeFileName = activeFileName,
                workspaceFileNames = files.map { it.fileName },
                currentCode = currentCode,
                recentError = lastExecutionResult?.stderr ?: lastExecutionResult?.errorMessage,
                testSummary = lastTestSuiteResult?.let { "${it.passedCount} of ${it.totalCount} tests passed" },
                activeConcept = "MULTI_FILE_PROJECT"
              )
              viewModel.openCodeCoach(coachContext, allWorkspaceFiles)
            },
            modifier = Modifier.testTag("btn_open_coach_project")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Code Coach",
              tint = QuestPrimary
            )
          }

          // More Options Menu
          IconButton(onClick = { showMoreMenu = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
          }

          DropdownMenu(
            expanded = showMoreMenu,
            onDismissRequest = { showMoreMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Save File Now") },
              leadingIcon = { Icon(Icons.Default.Save, contentDescription = null) },
              onClick = {
                showMoreMenu = false
                saveCurrentFileNow()
              }
            )
            DropdownMenuItem(
              text = { Text("Reset Current File") },
              leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
              onClick = {
                showMoreMenu = false
                val starter = starterFilesMap[activeFileName] ?: ""
                currentCode = starter
                triggerAutoSave(starter)
              }
            )
            DropdownMenuItem(
              text = { Text("Commit Version") },
              leadingIcon = { Icon(Icons.Default.Save, contentDescription = null) },
              onClick = {
                showMoreMenu = false
                saveCurrentFileNow()
                showCommitDialog = true
              }
            )
            DropdownMenuItem(
              text = { Text("Version History") },
              leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
              onClick = {
                showMoreMenu = false
                coroutineScope.launch {
                  versionHistoryList = viewModel.getProjectVersions(projectId)
                  showHistoryDialog = true
                }
              }
            )
            DropdownMenuItem(
              text = { Text("Reset Entire Project", color = MaterialTheme.colorScheme.error) },
              leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
              onClick = {
                showMoreMenu = false
                showResetConfirmDialog = true
              }
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Top Tab Navigation Bar
      PrimaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = QuestPrimary,
        modifier = Modifier.testTag("tab_row_workspace")
      ) {
        WorkspaceTab.entries.forEach { tab ->
          val isSelected = selectedTab == tab
          Tab(
            selected = isSelected,
            onClick = { 
              soundManager?.playTap()
              selectedTab = tab 
            },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = tab.icon,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = tab.title,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
                if (tab == WorkspaceTab.TASKS && completedTaskIds.isNotEmpty()) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(QuestSuccess)
                  )
                }
              }
            },
            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
          )
        }
      }

      // Tab Content Views
      Box(modifier = Modifier.weight(1f)) {
        when (selectedTab) {
          WorkspaceTab.CODE -> {
            Column(modifier = Modifier.fillMaxSize()) {
              // File Explorer Tabs Strip
              ProjectFileTabs(
                files = files,
                activeFileName = activeFileName,
                hasUnsavedChanges = hasUnsavedChanges,
                onSelectFile = { selectedName ->
                  if (selectedName != activeFileName) {
                    saveCurrentFileNow()
                    activeFileName = selectedName
                    val target = files.find { it.fileName == selectedName }
                    if (target != null) {
                      currentCode = target.fileContent
                      hasUnsavedChanges = false
                      saveStatusText = "Saved"
                    }
                  }
                },
                onNewFile = { showNewFileDialog = true },
                onRenameFile = { fileName -> fileToRename = fileName },
                onDeleteFile = { fileName -> fileToDelete = fileName }
              )

              // Code Editor with Full Syntax Highlighting and Toolbar
              CodeEditorView(
                code = currentCode,
                onCodeChange = { updated ->
                  triggerAutoSave(updated)
                },
                language = LanguageRegistry.PYTHON,
                starterCode = starterFilesMap[activeFileName] ?: "",
                modifier = Modifier
                  .weight(1f)
                  .testTag("project_code_editor")
              )
            }
          }

          WorkspaceTab.TASKS -> {
            project?.let { proj ->
              ProjectTasksView(
                project = proj,
                completedTaskIds = completedTaskIds,
                onRunTaskTests = { task ->
                  saveCurrentFileNow()
                  isRunningTests = true
                  selectedTab = WorkspaceTab.TESTS
                  coroutineScope.launch {
                    val suiteRes = projectRepo.runProjectTests(proj)
                    lastTestSuiteResult = suiteRes
                    isRunningTests = false
                  }
                }
              )
            }
          }

          WorkspaceTab.CONSOLE -> {
            ProjectConsoleView(
              executionResult = lastExecutionResult,
              isExecuting = isExecuting,
              onRunCodeWithInput = { inputStr ->
                saveCurrentFileNow()
                isExecuting = true
                coroutineScope.launch {
                  val res = projectRepo.executeProject(
                    projectId = projectId,
                    entryFileName = "main.py",
                    rawInput = inputStr
                  )
                  lastExecutionResult = res
                  isExecuting = false
                }
              },
              onClearConsole = {
                lastExecutionResult = null
              }
            )
          }

          WorkspaceTab.TESTS -> {
            ProjectTestsView(
              testSuiteResult = lastTestSuiteResult,
              isRunningTests = isRunningTests,
              onRunAllTests = {
                saveCurrentFileNow()
                isRunningTests = true
                coroutineScope.launch {
                  project?.let { proj ->
                    val res = projectRepo.runProjectTests(proj)
                    lastTestSuiteResult = res
                  }
                  isRunningTests = false
                }
              }
            )
          }

          WorkspaceTab.GUIDE -> {
            val readmeFile = files.find { it.fileName.endsWith(".md", ignoreCase = true) }
            project?.let { proj ->
              ProjectReadmeView(
                project = proj,
                readmeContent = readmeFile?.fileContent ?: proj.instructions
              )
            }
          }
        }
      }
    }
  }

  // New File Creation Dialog
  if (showNewFileDialog) {
    NewFileDialog(
      onDismiss = { showNewFileDialog = false },
      onCreate = { newName ->
        showNewFileDialog = false
        coroutineScope.launch {
          val success = projectRepo.createFile(projectId, newName, "# Module: $newName\n\n")
          if (success) {
            activeFileName = newName
            currentCode = "# Module: $newName\n\n"
            hasUnsavedChanges = false
            saveStatusText = "Saved"
          }
        }
      }
    )
  }

  // Rename File Dialog
  fileToRename?.let { oldName ->
    RenameFileDialog(
      currentName = oldName,
      onDismiss = { fileToRename = null },
      onRename = { newName ->
        fileToRename = null
        coroutineScope.launch {
          val success = projectRepo.renameFile(projectId, oldName, newName)
          if (success && activeFileName == oldName) {
            activeFileName = newName
          }
        }
      }
    )
  }

  // Delete File Dialog
  fileToDelete?.let { targetToDelete ->
    DeleteFileDialog(
      fileName = targetToDelete,
      onDismiss = { fileToDelete = null },
      onConfirmDelete = {
        fileToDelete = null
        coroutineScope.launch {
          val success = projectRepo.deleteFile(projectId, targetToDelete)
          if (success && activeFileName == targetToDelete) {
            activeFileName = "main.py"
          }
        }
      }
    )
  }

  // Reset Entire Project Dialog
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Reset Entire Project?") },
      text = { Text("All your custom edits will be discarded and files restored to initial starter state.") },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            coroutineScope.launch {
              project?.let { proj ->
                projectRepo.resetProjectToStarterFiles(proj)
                activeFileName = "main.py"
                currentCode = starterFilesMap["main.py"] ?: ""
                hasUnsavedChanges = false
                saveStatusText = "Saved"
                lastExecutionResult = null
                lastTestSuiteResult = null
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Reset Project")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Confirm Leave Dialog with Unsaved Edits
  if (showLeaveConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLeaveConfirmDialog = false },
      title = { Text("Unsaved Changes") },
      text = { Text("You have unsaved changes in your workspace. Do you want to save before leaving?") },
      confirmButton = {
        Button(
          onClick = {
            showLeaveConfirmDialog = false
            saveCurrentFileNow()
            onNavigateBack()
          }
        ) {
          Text("Save & Exit")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showLeaveConfirmDialog = false
            onNavigateBack()
          }
        ) {
          Text("Discard & Exit")
        }
      }
    )
  }

  // Commit Dialog
  if (showCommitDialog) {
    var commitMsg by remember { mutableStateOf("") }
    AlertDialog(
      onDismissRequest = { showCommitDialog = false },
      title = { Text("Commit Version") },
      text = {
        androidx.compose.material3.OutlinedTextField(
          value = commitMsg,
          onValueChange = { commitMsg = it },
          label = { Text("Commit Message") },
          modifier = Modifier.fillMaxWidth()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.commitProjectVersion(projectId, commitMsg.ifBlank { "Update version" }) {
              showCommitDialog = false
            }
          }
        ) {
          Text("Commit")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCommitDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Version History Dialog
  if (showHistoryDialog) {
    AlertDialog(
      onDismissRequest = { showHistoryDialog = false },
      title = { Text("Version History") },
      text = {
        if (versionHistoryList.isEmpty()) {
          Text("No commits yet.")
        } else {
          Column(
            modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            versionHistoryList.forEach { v ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("Version ${v.versionNumber}", fontWeight = FontWeight.Bold)
                  Text(v.description, style = MaterialTheme.typography.bodyMedium)
                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedButton(
                    onClick = {
                      viewModel.restoreProjectVersion(projectId, v.versionNumber) {
                        showHistoryDialog = false
                        activeFileName = "main.py"
                      }
                    },
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text("Restore this version")
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showHistoryDialog = false }) { Text("Close") }
      }
    )
  }

  // Project Completion Modal Celebration
  submissionResult?.let { subRes ->
    if (subRes.isCompleted) {
      project?.let { proj ->
        ProjectCompletionDialog(
          project = proj,
          submissionResult = subRes,
          onDismiss = { submissionResult = null },
          onBackToProjects = {
            submissionResult = null
            onNavigateBack()
          }
        )
      }
    }
  }
}
