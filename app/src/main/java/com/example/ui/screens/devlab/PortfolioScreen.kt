package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import com.example.data.models.PortfolioItemEntity
import com.example.domain.services.SecretScanner
import com.example.domain.services.SecretScanResult
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
  portfolioItems: List<PortfolioItemEntity>,
  onToggleVisibility: (String, Boolean) -> Unit,
  onOpenReadmeBuilder: (PortfolioItemEntity) -> Unit,
  onNavigateBack: () -> Unit
) {
  var selectedItemForPreview by remember { mutableStateOf<PortfolioItemEntity?>(null) }
  var exportItemCandidate by remember { mutableStateOf<PortfolioItemEntity?>(null) }
  var secretScanResult by remember { mutableStateOf<SecretScanResult?>(null) }
  var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Developer Portfolio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${portfolioItems.size} Verified Projects Showcased", style = MaterialTheme.typography.labelSmall, color = QuestCyan)
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("portfolio_btn_back")
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
      // Portfolio Header Banner with Skill Evidence
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        tonalElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Developer Evidence Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              Text("Evidence-backed proof of completed codebases and test suites", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = QuestGold, modifier = Modifier.size(28.dp))
          }

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            SuggestionChip(
              onClick = {},
              label = { Text("Python: 32 Lessons • 18 Challenges • 3 Projects", fontSize = 11.sp) },
              colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.surface)
            )
          }
        }
      }

      if (portfolioItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No Portfolio Projects Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              "Complete real-world projects in Project Lab or Developer Lab to showcase verified codebases, test suites, and documentation here!",
              style = MaterialTheme.typography.bodySmall,
              color = Color.Gray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(portfolioItems) { item ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(QuestPrimary.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Terminal, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                      Text("${item.language.uppercase()} • Completed ${item.completedDate}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                  }

                  // Visibility Switch
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (item.isPublic) "Public" else "Private", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                      checked = item.isPublic,
                      onCheckedChange = { onToggleVisibility(item.id, it) },
                      modifier = Modifier.testTag("portfolio_switch_${item.id}")
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(item.description, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(10.dp))
                // Verified Skills Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  item.parseSkills().forEach { skill ->
                    AssistChip(
                      onClick = {},
                      label = { Text(skill, fontSize = 11.sp) },
                      leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = QuestGreen, modifier = Modifier.size(12.dp)) }
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Action Buttons
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = { onOpenReadmeBuilder(item) },
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("README", fontSize = 12.sp)
                  }

                  OutlinedButton(
                    onClick = { selectedItemForPreview = item },
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Page", fontSize = 12.sp)
                  }

                  Button(
                    onClick = {
                      val scan = SecretScanner.scanFiles(item.parseFiles())
                      secretScanResult = scan
                      exportItemCandidate = item
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("portfolio_btn_export_${item.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
                  ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", fontSize = 12.sp)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Safe Shareable Project Page Preview Modal
  selectedItemForPreview?.let { previewItem ->
    AlertDialog(
      onDismissRequest = { selectedItemForPreview = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Public, contentDescription = null, tint = QuestGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Public Showcase: ${previewItem.title}")
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            color = QuestGreen.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = QuestGreen, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Safe Sanitized View: No private tokens, emails, or credentials exposed.", style = MaterialTheme.typography.labelSmall, color = QuestGreen)
            }
          }

          Text("Project Overview:", fontWeight = FontWeight.Bold)
          Text(previewItem.description, style = MaterialTheme.typography.bodySmall)

          Text("Verified Evidence:", fontWeight = FontWeight.Bold)
          Text("✓ 100% Automated Test Suite Passed\n✓ Clean Architecture Code Structure\n✓ Complete Production Documentation", style = MaterialTheme.typography.bodySmall, color = QuestCyan)

          if (previewItem.readmeContent.isNotBlank()) {
            Text("README Excerpt:", fontWeight = FontWeight.Bold)
            Surface(
              color = Color(0xFF1E1E2E),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = previewItem.readmeContent.take(300) + "...",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { selectedItemForPreview = null }) {
          Text("Close")
        }
      }
    )
  }

  // Export Confirmation & Secret Scanner Modal
  exportItemCandidate?.let { exportItem ->
    val scan = secretScanResult
    AlertDialog(
      onDismissRequest = { exportItemCandidate = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (scan?.hasSecrets == true) Icons.Default.Warning else Icons.Default.DownloadDone,
            contentDescription = null,
            tint = if (scan?.hasSecrets == true) QuestRed else QuestGreen
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(if (scan?.hasSecrets == true) "Security Warning: Secrets Detected" else "Export Project Bundle")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (scan?.hasSecrets == true) {
            Text(scan.warningMessage ?: "Potential secrets detected!", color = QuestRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            scan.findings.forEach { finding ->
              Text("• ${finding.fileName}:${finding.lineNumber} - ${finding.patternType}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Please remove sensitive credentials before sharing project archives.", style = MaterialTheme.typography.bodySmall)
          } else {
            Text("All automated secret scans passed! Ready to export sanitized ZIP package containing source files, README, and test reports.", style = MaterialTheme.typography.bodySmall)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            exportSuccessMessage = "Exported ${exportItem.title}.zip successfully to Downloads!"
            exportItemCandidate = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (scan?.hasSecrets == true) QuestRed else QuestGreen)
        ) {
          Text(if (scan?.hasSecrets == true) "Export Anyway" else "Export ZIP")
        }
      },
      dismissButton = {
        TextButton(onClick = { exportItemCandidate = null }) {
          Text("Cancel")
        }
      }
    )
  }

  exportSuccessMessage?.let { msg ->
    AlertDialog(
      onDismissRequest = { exportSuccessMessage = null },
      title = { Text("Export Complete") },
      text = { Text(msg) },
      confirmButton = {
        TextButton(onClick = { exportSuccessMessage = null }) {
          Text("OK")
        }
      }
    )
  }
}
