package com.example.ui.components.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.models.ProjectFileEntity
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed

@Composable
fun ProjectFileTabs(
  files: List<ProjectFileEntity>,
  activeFileName: String,
  hasUnsavedChanges: Boolean,
  onSelectFile: (String) -> Unit,
  onNewFile: () -> Unit,
  onRenameFile: (String) -> Unit,
  onDeleteFile: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color(0xFF1E1E2E),
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState)
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      files.forEach { file ->
        val isActive = file.fileName == activeFileName
        var showMenu by remember { mutableStateOf(false) }

        FileTabItem(
          file = file,
          isActive = isActive,
          hasUnsavedChanges = isActive && hasUnsavedChanges,
          onClick = { onSelectFile(file.fileName) },
          onOpenMenu = { showMenu = true }
        )

        if (showMenu) {
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            if (!file.isMain && !file.isReadOnly) {
              DropdownMenuItem(
                text = { Text("Rename") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                  showMenu = false
                  onRenameFile(file.fileName)
                }
              )
              DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                  showMenu = false
                  onDeleteFile(file.fileName)
                }
              )
            } else {
              DropdownMenuItem(
                text = { Text(if (file.isMain) "Entry file (Protected)" else "Documentation (Read-only)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                onClick = { showMenu = false }
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))
      }

      // Add New File Button
      IconButton(
        onClick = onNewFile,
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF2B2B3D))
          .testTag("btn_add_file")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "New File",
          tint = Color(0xFFC4C6D0),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun FileTabItem(
  file: ProjectFileEntity,
  isActive: Boolean,
  hasUnsavedChanges: Boolean,
  onClick: () -> Unit,
  onOpenMenu: () -> Unit
) {
  val backgroundColor = if (isActive) Color(0xFF2B2B3D) else Color(0xFF14141F)
  val borderColor = if (isActive) QuestPrimary else Color.Transparent
  val textColor = if (isActive) Color(0xFFE2E2E6) else Color(0xFF8C8D99)

  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag("file_tab_${file.fileName}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = when {
        file.fileName.endsWith(".py") -> Icons.Default.Code
        file.fileName.endsWith(".md") -> Icons.Default.Description
        else -> Icons.Default.Code
      },
      contentDescription = null,
      tint = when {
        file.fileName.endsWith(".py") -> QuestPrimary
        file.fileName.endsWith(".md") -> QuestGold
        else -> Color(0xFF8C8D99)
      },
      modifier = Modifier.size(14.dp)
    )

    Spacer(modifier = Modifier.width(6.dp))

    Text(
      text = file.fileName,
      style = MaterialTheme.typography.labelMedium.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp
      ),
      color = textColor
    )

    if (hasUnsavedChanges) {
      Spacer(modifier = Modifier.width(6.dp))
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(QuestGold)
      )
    }

    if (file.isReadOnly) {
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Read Only",
        tint = Color(0xFF6C6D79),
        modifier = Modifier.size(10.dp)
      )
    }

    if (!file.isMain && !file.isReadOnly) {
      Spacer(modifier = Modifier.width(4.dp))
      IconButton(
        onClick = onOpenMenu,
        modifier = Modifier.size(16.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options",
          tint = Color(0xFF8C8D99),
          modifier = Modifier.size(12.dp)
        )
      }
    }
  }
}

@Composable
fun NewFileDialog(
  onDismiss: () -> Unit,
  onCreate: (String) -> Unit
) {
  var fileName by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create New File") },
    text = {
      Column {
        Text(
          text = "Enter a file name for your project module (e.g., utils.py, helpers.py):",
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = fileName,
          onValueChange = {
            fileName = it
            errorText = null
          },
          label = { Text("File Name") },
          placeholder = { Text("module_name.py") },
          singleLine = true,
          isError = errorText != null,
          supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_new_filename")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val trimmed = fileName.trim()
          when {
            trimmed.isBlank() -> errorText = "File name cannot be empty."
            trimmed.contains("/") || trimmed.contains("\\") -> errorText = "No path separators allowed."
            !trimmed.contains(".") -> {
              // Auto append .py if extension omitted
              onCreate("$trimmed.py")
            }
            else -> onCreate(trimmed)
          }
        },
        modifier = Modifier.testTag("btn_confirm_create_file")
      ) {
        Text("Create")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun RenameFileDialog(
  currentName: String,
  onDismiss: () -> Unit,
  onRename: (String) -> Unit
) {
  var newName by remember { mutableStateOf(currentName) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Rename File") },
    text = {
      Column {
        Text("Rename '$currentName' to:")
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = newName,
          onValueChange = {
            newName = it
            errorText = null
          },
          singleLine = true,
          isError = errorText != null,
          supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
          modifier = Modifier.fillMaxWidth().testTag("input_rename_file")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val trimmed = newName.trim()
          when {
            trimmed.isBlank() -> errorText = "File name cannot be empty."
            trimmed == currentName -> onDismiss()
            trimmed.contains("/") || trimmed.contains("\\") -> errorText = "No path separators allowed."
            else -> onRename(trimmed)
          }
        },
        modifier = Modifier.testTag("btn_confirm_rename_file")
      ) {
        Text("Rename")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun DeleteFileDialog(
  fileName: String,
  onDismiss: () -> Unit,
  onConfirmDelete: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Delete File") },
    text = { Text("Are you sure you want to delete '$fileName'? This action cannot be undone.") },
    confirmButton = {
      Button(
        onClick = onConfirmDelete,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        modifier = Modifier.testTag("btn_confirm_delete_file")
      ) {
        Text("Delete")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
