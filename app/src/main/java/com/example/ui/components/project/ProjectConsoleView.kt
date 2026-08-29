package com.example.ui.components.project

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.execution.ExecutionErrorType
import com.example.domain.execution.ExecutionResult
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed
import com.example.ui.theme.QuestSuccess

@Composable
fun ProjectConsoleView(
  executionResult: ExecutionResult?,
  isExecuting: Boolean,
  onRunCodeWithInput: (String) -> Unit,
  onClearConsole: () -> Unit,
  modifier: Modifier = Modifier
) {
  var stdinText by remember { mutableStateOf("") }
  val outputScrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F141C))
      .padding(12.dp)
  ) {
    // Header Bar with status & action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = null,
          tint = QuestPrimary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Interactive Terminal",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = Color(0xFFE2E2E6)
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (executionResult != null) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (executionResult.hasError) QuestRed.copy(alpha = 0.2f) else QuestSuccess.copy(alpha = 0.2f)
          ) {
            Text(
              text = if (executionResult.hasError) "Exit code: ${executionResult.exitCode}" else "Success (${executionResult.executionTimeMs}ms)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = if (executionResult.hasError) QuestRed else QuestSuccess
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
        }

        IconButton(
          onClick = onClearConsole,
          modifier = Modifier.size(28.dp).testTag("btn_clear_console")
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear Output",
            tint = Color(0xFF8C8D99),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // Terminal Screen Window
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF141A24))
        .padding(12.dp)
    ) {
      if (isExecuting) {
        Row(
          modifier = Modifier.align(Alignment.Center),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = QuestPrimary,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Executing workspace code...",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = Color(0xFFC4C6D0)
          )
        }
      } else if (executionResult == null) {
        Text(
          text = "Press 'Run Code' or enter standard input below to execute your project.",
          style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
          color = Color(0xFF6C7086)
        )
      } else {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(outputScrollState)
        ) {
          if (executionResult.stdout.isNotBlank()) {
            Text(
              text = executionResult.stdout,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 18.sp
              ),
              color = Color(0xFFE2E2E6),
              modifier = Modifier.testTag("console_stdout")
            )
          }

          if (executionResult.hasError) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF33141E),
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Runtime Error (${executionResult.errorType.name}):",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = QuestRed
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = executionResult.errorMessage ?: executionResult.stderr.ifBlank { "An unexpected execution error occurred." },
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFB4AB)
                  ),
                  modifier = Modifier.testTag("console_stderr")
                )
              }
            }
          }

          if (!executionResult.hasError && executionResult.stdout.isBlank()) {
            Text(
              text = "[Process finished with exit code 0 - No output produced]",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = Color(0xFF6C7086)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Stdin Input Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = stdinText,
        onValueChange = { stdinText = it },
        placeholder = {
          Text(
            "Enter CLI input (e.g. 1, 42, +)...",
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = Color(0xFF6C7086)
          )
        },
        singleLine = false,
        maxLines = 3,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
          onSend = {
            if (stdinText.isNotBlank()) {
              onRunCodeWithInput(stdinText)
            }
          }
        ),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF141A24),
          unfocusedContainerColor = Color(0xFF141A24),
          focusedBorderColor = QuestPrimary,
          unfocusedBorderColor = Color(0xFF2B3342),
          focusedTextColor = Color(0xFFE2E2E6),
          unfocusedTextColor = Color(0xFFE2E2E6)
        ),
        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        modifier = Modifier
          .weight(1f)
          .testTag("input_stdin")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = {
          onRunCodeWithInput(stdinText)
        },
        enabled = !isExecuting,
        colors = ButtonDefaults.buttonColors(
          containerColor = QuestPrimary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .height(50.dp)
          .testTag("btn_send_stdin")
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Send Input",
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
