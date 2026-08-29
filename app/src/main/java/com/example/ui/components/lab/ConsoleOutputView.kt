package com.example.ui.components.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.execution.ExecutionErrorType
import com.example.domain.execution.ExecutionResult
import com.example.ui.components.editor.SyntaxTheme
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed

@Composable
fun ConsoleOutputView(
  executionResult: ExecutionResult?,
  isRunning: Boolean,
  onRunCustomInput: (String) -> Unit,
  onClearConsole: () -> Unit,
  modifier: Modifier = Modifier
) {
  var customInput by remember { mutableStateOf("") }
  var showCustomInput by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = SyntaxTheme.Background,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF313244))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Console Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF181825))
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Terminal,
          contentDescription = "Terminal",
          tint = QuestPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Console Output",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = SyntaxTheme.TextDefault
        )

        if (executionResult != null && !isRunning) {
          Spacer(modifier = Modifier.width(8.dp))
          val statusColor = if (executionResult.isSuccess) QuestGreen else QuestRed
          val statusText = if (executionResult.isSuccess) "Finished (${executionResult.executionTimeMs}ms)"
          else "Error (${executionResult.errorType.name.replace('_', ' ')})"

          Surface(
            color = statusColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = statusText,
              color = statusColor,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
          onClick = { showCustomInput = !showCustomInput },
          colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = QuestPrimary)
        ) {
          Text(if (showCustomInput) "Hide Input" else "Custom Input", fontSize = 11.sp)
        }

        IconButton(onClick = onClearConsole, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SyntaxTheme.LineNumber, modifier = Modifier.size(14.dp))
        }
      }

      // 2. Custom Input Drawer (Optional)
      if (showCustomInput) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF11111B))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = customInput,
            onValueChange = { customInput = it },
            placeholder = { Text("Enter custom program stdin...", fontSize = 11.sp) },
            singleLine = true,
            textStyle = TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SyntaxTheme.TextDefault),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = QuestPrimary,
              unfocusedBorderColor = Color(0xFF313244),
              focusedContainerColor = Color(0xFF181825),
              unfocusedContainerColor = Color(0xFF181825)
            ),
            modifier = Modifier.weight(1f).height(40.dp)
          )

          Button(
            onClick = { onRunCustomInput(customInput) },
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.height(38.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 3. Output Logs Content
      val vScroll = rememberScrollState()
      val hScroll = rememberScrollState()

      SelectionContainer(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(SyntaxTheme.Background)
          .verticalScroll(vScroll)
          .horizontalScroll(hScroll)
          .padding(12.dp)
      ) {
        when {
          isRunning -> {
            Text(
              text = "Executing program in sandboxed environment...",
              style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = SyntaxTheme.Keyword
              )
            )
          }
          executionResult == null -> {
            Text(
              text = "Click 'Run Code' or 'Run Tests' to see output here.",
              style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = SyntaxTheme.LineNumber
              )
            )
          }
          executionResult.hasError -> {
            Column {
              if (executionResult.stdout.isNotEmpty()) {
                Text(
                  text = executionResult.stdout,
                  style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = SyntaxTheme.TextDefault
                  )
                )
                Spacer(modifier = Modifier.height(8.dp))
              }
              Text(
                text = executionResult.stderr.ifBlank { executionResult.errorMessage ?: "Unknown error" },
                style = TextStyle(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = QuestRed
                )
              )
            }
          }
          else -> {
            Text(
              text = executionResult.stdout.ifBlank { "(Program executed with no console output)" },
              style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = if (executionResult.stdout.isBlank()) SyntaxTheme.LineNumber else SyntaxTheme.StringLiteral
              )
            )
          }
        }
      }
    }
  }
}
