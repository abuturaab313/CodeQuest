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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.services.DocumentationValidator
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadmeBuilderScreen(
  initialProjectName: String = "My Real-World Project",
  initialReadme: String = "",
  onNavigateBack: () -> Unit,
  onSaveReadme: (String) -> Unit
) {
  var projectName by remember { mutableStateOf(initialProjectName) }
  var description by remember { mutableStateOf("A high-performance modular application built in Python for seamless data processing and automated testing.") }
  var features by remember { mutableStateOf("- Multi-file modular architecture\n- Input sanitization & robust error handling\n- Automated unit test suite with 100% pass rate\n- Safe and deterministic execution engine") }
  var installation by remember { mutableStateOf("```bash\ngit clone https://github.com/developer/project.git\ncd project\npip install -r requirements.txt\n```") }
  var usage by remember { mutableStateOf("```bash\npython main.py\n```") }
  var tests by remember { mutableStateOf("```bash\npytest tests/\n```") }
  var author by remember { mutableStateOf("Alex Dev <alex@codequest.dev> • MIT License") }

  var isMarkdownPreviewMode by remember { mutableStateOf(false) }
  var showQualityDialog by remember { mutableStateOf(false) }

  val compiledMarkdown = remember(projectName, description, features, installation, usage, tests, author) {
    """# $projectName

## 📖 Overview
$description

## 🚀 Features
$features

## 🛠️ Installation & Setup
$installation

## 💻 Usage
$usage

## 🧪 Testing
$tests

## 👤 Author & License
$author
"""
  }

  val qualityResult = remember(compiledMarkdown) {
    DocumentationValidator.evaluateReadme(compiledMarkdown)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("README Builder & Quality Lab", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Doc Quality: ${qualityResult.scorePercent}% (${qualityResult.status})", style = MaterialTheme.typography.labelSmall, color = if (qualityResult.scorePercent >= 80) QuestGreen else QuestGold)
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("readme_btn_back")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { showQualityDialog = true },
            modifier = Modifier.testTag("readme_btn_quality_check")
          ) {
            Icon(Icons.Default.Verified, contentDescription = "Quality Check", tint = QuestCyan)
          }
          IconButton(
            onClick = { onSaveReadme(compiledMarkdown) },
            modifier = Modifier.testTag("readme_btn_save")
          ) {
            Icon(Icons.Default.Save, contentDescription = "Save", tint = QuestGreen)
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
      // Quality Score Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        tonalElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
              progress = { qualityResult.scorePercent / 100f },
              modifier = Modifier.size(24.dp),
              color = if (qualityResult.scorePercent >= 80) QuestGreen else QuestGold,
              strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Documentation Completeness: ${qualityResult.scorePercent}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }

          Row {
            FilterChip(
              selected = !isMarkdownPreviewMode,
              onClick = { isMarkdownPreviewMode = false },
              label = { Text("Sections") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            FilterChip(
              selected = isMarkdownPreviewMode,
              onClick = { isMarkdownPreviewMode = true },
              label = { Text("Markdown") }
            )
          }
        }
      }

      // Main Content Area
      if (!isMarkdownPreviewMode) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Title") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description & Problem Solved") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )

          OutlinedTextField(
            value = features,
            onValueChange = { features = it },
            label = { Text("Key Features") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
          )

          OutlinedTextField(
            value = installation,
            onValueChange = { installation = it },
            label = { Text("Installation / Setup Steps") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )

          OutlinedTextField(
            value = usage,
            onValueChange = { usage = it },
            label = { Text("Usage Examples") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )

          OutlinedTextField(
            value = tests,
            onValueChange = { tests = it },
            label = { Text("Testing Guide") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )

          OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author & License") },
            modifier = Modifier.fillMaxWidth()
          )

          Button(
            onClick = { onSaveReadme(compiledMarkdown) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("readme_btn_save_bottom"),
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save README to Project")
          }
        }
      } else {
        // Raw Markdown Preview
        Card(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(16.dp)
          ) {
            Text(
              text = compiledMarkdown,
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              color = Color(0xFFCDD6F4)
            )
          }
        }
      }
    }
  }

  // Quality Check Details Modal
  if (showQualityDialog) {
    AlertDialog(
      onDismissRequest = { showQualityDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Checklist, contentDescription = null, tint = QuestGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Documentation Quality Audit")
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          qualityResult.checks.forEach { check ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (check.isPresent) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (check.isPresent) QuestGreen else QuestRed,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(check.sectionName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text(check.feedback, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showQualityDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}
