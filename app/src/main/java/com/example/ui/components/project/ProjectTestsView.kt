package com.example.ui.components.project

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.execution.ProjectTestResult
import com.example.domain.execution.ProjectTestSuiteResult
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestRed
import com.example.ui.theme.QuestSuccess

@Composable
fun ProjectTestsView(
  testSuiteResult: ProjectTestSuiteResult?,
  isRunningTests: Boolean,
  onRunAllTests: () -> Unit,
  modifier: Modifier = Modifier
) {
  val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Automated Test Suite",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Verify modular interactions and edge cases",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = onRunAllTests,
          enabled = !isRunningTests,
          colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_run_tests")
        ) {
          if (isRunningTests) {
            CircularProgressIndicator(
              color = Color.White,
              modifier = Modifier.size(16.dp),
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Testing...")
          } else {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Run Tests")
          }
        }
      }
    }

    if (testSuiteResult != null) {
      item {
        TestSuiteSummaryBanner(testSuiteResult = testSuiteResult)
      }

      items(testSuiteResult.results) { testResult ->
        val isExpanded = expandedMap[testResult.testId] ?: !testResult.passed

        ProjectTestCaseCard(
          testResult = testResult,
          isExpanded = isExpanded,
          onToggleExpand = {
            expandedMap[testResult.testId] = !isExpanded
          }
        )
      }
    } else {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = null,
              tint = QuestPrimary,
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Ready to test your code",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap 'Run Tests' above to execute all unit tests and verify your checkpoints.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun TestSuiteSummaryBanner(testSuiteResult: ProjectTestSuiteResult) {
  val progress = if (testSuiteResult.totalCount > 0) {
    testSuiteResult.passedCount.toFloat() / testSuiteResult.totalCount.toFloat()
  } else 0f

  val bannerBg = if (testSuiteResult.allPassed) QuestSuccess.copy(alpha = 0.12f) else QuestRed.copy(alpha = 0.1f)
  val bannerBorder = if (testSuiteResult.allPassed) QuestSuccess.copy(alpha = 0.4f) else QuestRed.copy(alpha = 0.3f)

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = bannerBg,
    border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (testSuiteResult.allPassed) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (testSuiteResult.allPassed) QuestSuccess else QuestRed,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (testSuiteResult.allPassed) "All Tests Passed!" else "Some Tests Failed",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (testSuiteResult.allPassed) QuestSuccess else QuestRed
          )
        }

        Text(
          text = "${testSuiteResult.passedCount} / ${testSuiteResult.totalCount} Passed",
          style = MaterialTheme.typography.labelLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = if (testSuiteResult.allPassed) QuestSuccess else QuestRed,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      if (testSuiteResult.failureSummary != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Issue: ${testSuiteResult.failureSummary}",
          style = MaterialTheme.typography.bodySmall,
          color = QuestRed
        )
      }
    }
  }
}

@Composable
private fun ProjectTestCaseCard(
  testResult: ProjectTestResult,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit
) {
  val cardBg = if (testResult.passed) {
    MaterialTheme.colorScheme.surface
  } else {
    QuestRed.copy(alpha = 0.05f)
  }

  val borderColor = if (testResult.passed) {
    QuestSuccess.copy(alpha = 0.3f)
  } else {
    QuestRed.copy(alpha = 0.4f)
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onToggleExpand)
      .testTag("test_card_${testResult.testId}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(if (testResult.passed) QuestSuccess else QuestRed),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (testResult.passed) Icons.Default.Check else Icons.Default.Close,
              contentDescription = if (testResult.passed) "Passed" else "Failed",
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = testResult.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (testResult.isHidden) {
              Text(
                text = "Hidden Verification Test",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${testResult.executionTimeMs}ms",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          if (testResult.input.isNotBlank()) {
            Text(
              text = "Input:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF141A24),
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 6.dp)
            ) {
              Text(
                text = testResult.input.trim(),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = Color(0xFFE2E2E6),
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          if (testResult.expectedOutput.isNotBlank()) {
            Text(
              text = "Expected Output:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF141A24),
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 6.dp)
            ) {
              Text(
                text = testResult.expectedOutput.trim(),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = QuestSuccess,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Text(
            text = "Actual Output:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF141A24),
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 6.dp)
          ) {
            Text(
              text = testResult.actualOutput.ifBlank { "[No output]" },
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = if (testResult.passed) Color(0xFFE2E2E6) else QuestRed,
              modifier = Modifier.padding(8.dp)
            )
          }

          if (testResult.failureReason != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Failure Reason: ${testResult.failureReason}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = QuestRed
              )
            )
          }
        }
      }
    }
  }
}
