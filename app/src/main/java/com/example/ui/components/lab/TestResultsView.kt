package com.example.ui.components.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.execution.ChallengeTestResult
import com.example.domain.execution.TestSuiteResult
import com.example.ui.components.editor.SyntaxTheme
import com.example.ui.theme.QuestGreen
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed

@Composable
fun TestResultsView(
  testSuiteResult: TestSuiteResult?,
  isRunningTests: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = SyntaxTheme.Background,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF313244))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF181825))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Test Results",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = SyntaxTheme.TextDefault
        )

        Spacer(modifier = Modifier.weight(1f))

        if (testSuiteResult != null && !isRunningTests) {
          val badgeColor = if (testSuiteResult.allPassed) QuestGreen else QuestRed
          Surface(
            color = badgeColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "${testSuiteResult.passedCount} / ${testSuiteResult.totalCount} Passed",
              color = badgeColor,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Progress bar if loaded
      if (testSuiteResult != null && testSuiteResult.totalCount > 0) {
        val progress = testSuiteResult.passedCount.toFloat() / testSuiteResult.totalCount
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(3.dp),
          color = if (testSuiteResult.allPassed) QuestGreen else QuestRed,
          trackColor = Color(0xFF313244)
        )
      }

      // 2. Body List
      if (isRunningTests) {
        Box(
          modifier = Modifier.fillMaxSize().padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Running test suite against code...",
            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = SyntaxTheme.Keyword)
          )
        }
      } else if (testSuiteResult == null || testSuiteResult.results.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize().padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Click 'Run Tests' to validate your solution.",
            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = SyntaxTheme.LineNumber)
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          itemsIndexed(testSuiteResult.results) { index, testResult ->
            TestCaseCard(index = index + 1, test = testResult)
          }
        }
      }
    }
  }
}

@Composable
private fun TestCaseCard(
  index: Int,
  test: ChallengeTestResult
) {
  var isExpanded by remember { mutableStateOf(!test.passed) }

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF181825)),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (test.passed) QuestGreen.copy(alpha = 0.3f) else QuestRed.copy(alpha = 0.3f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header item
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          if (test.passed) Icons.Default.CheckCircle else Icons.Default.Error,
          contentDescription = if (test.passed) "Passed" else "Failed",
          tint = if (test.passed) QuestGreen else QuestRed,
          modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = "Test Case $index",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = SyntaxTheme.TextDefault
        )

        if (test.isHidden) {
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = Color(0xFF313244),
            shape = RoundedCornerShape(4.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Lock, contentDescription = "Hidden", tint = SyntaxTheme.LineNumber, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Hidden", color = SyntaxTheme.LineNumber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
          text = "${test.executionTimeMs}ms",
          style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SyntaxTheme.LineNumber)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
          if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = "Expand",
          tint = SyntaxTheme.LineNumber,
          modifier = Modifier.size(16.dp)
        )
      }

      // Expandable details
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF11111B))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (test.failureReason != null) {
            Text(
              text = "Failure: ${test.failureReason}",
              style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = QuestRed, fontWeight = FontWeight.SemiBold)
            )
          }

          // Input block
          Column {
            Text("Input:", style = TextStyle(fontSize = 10.sp, color = SyntaxTheme.LineNumber, fontWeight = FontWeight.Bold))
            Surface(
              color = Color(0xFF181825),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
              Text(
                text = test.input.ifBlank { "(No input)" },
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SyntaxTheme.TextDefault),
                modifier = Modifier.padding(6.dp)
              )
            }
          }

          // Expected output block
          Column {
            Text("Expected Output:", style = TextStyle(fontSize = 10.sp, color = SyntaxTheme.LineNumber, fontWeight = FontWeight.Bold))
            Surface(
              color = Color(0xFF181825),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
              Text(
                text = test.expectedOutput,
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = QuestGreen),
                modifier = Modifier.padding(6.dp)
              )
            }
          }

          // Actual output block
          Column {
            Text("Actual Output:", style = TextStyle(fontSize = 10.sp, color = SyntaxTheme.LineNumber, fontWeight = FontWeight.Bold))
            Surface(
              color = Color(0xFF181825),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
              Text(
                text = test.actualOutput.ifBlank { "(No output)" },
                style = TextStyle(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = if (test.passed) QuestGreen else QuestRed
                ),
                modifier = Modifier.padding(6.dp)
              )
            }
          }
        }
      }
    }
  }
}
