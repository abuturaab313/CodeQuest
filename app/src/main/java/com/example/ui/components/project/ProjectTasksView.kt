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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectHint
import com.example.data.models.ProjectTask
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold

@Composable
fun ProjectTasksView(
  project: ProjectEntity,
  completedTaskIds: Set<String>,
  onRunTaskTests: (ProjectTask) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val tasks = remember(project) { project.parseTasks() }
  val hints = remember(project) { project.parseHints() }
  val expandedHints = remember { mutableStateMapOf<String, Boolean>() }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Project Tasks & Checkpoints",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${completedTaskIds.size} of ${tasks.size} Checkpoints Cleared",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (completedTaskIds.size == tasks.size && tasks.isNotEmpty()) QuestSuccess.copy(alpha = 0.15f) else QuestPrimary.copy(alpha = 0.12f)
        ) {
          Text(
            text = if (completedTaskIds.size == tasks.size && tasks.isNotEmpty()) "Ready to Submit!" else "In Progress",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = if (completedTaskIds.size == tasks.size && tasks.isNotEmpty()) QuestSuccess else QuestPrimary
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    items(tasks) { task ->
      val isCompleted = completedTaskIds.contains(task.id)
      val taskHints = hints.filter { it.taskId == task.id }

      ProjectTaskCard(
        task = task,
        isCompleted = isCompleted,
        hints = taskHints,
        isHintsExpanded = expandedHints[task.id] == true,
        onToggleHints = {
          expandedHints[task.id] = !(expandedHints[task.id] ?: false)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ProjectTaskCard(
  task: ProjectTask,
  isCompleted: Boolean,
  hints: List<ProjectHint>,
  isHintsExpanded: Boolean,
  onToggleHints: () -> Unit
) {
  val borderColor = if (isCompleted) QuestSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
  val cardBackground = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBackground),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}")
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
              .size(28.dp)
              .clip(CircleShape)
              .background(if (isCompleted) QuestSuccess else QuestPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            if (isCompleted) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            } else {
              Text(
                text = "${task.checkpoint}",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = QuestPrimary
                )
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "+${task.xpReward} XP",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = XpGold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = task.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (task.hint.isNotBlank() || hints.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggleHints)
            .background(QuestGold.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isHintsExpanded) "Hide Hint" else "Reveal Hint",
            style = MaterialTheme.typography.labelMedium.copy(
              color = QuestGold,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = if (isHintsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(16.dp)
          )
        }

        AnimatedVisibility(visible = isHintsExpanded) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
              .padding(12.dp)
          ) {
            if (task.hint.isNotBlank()) {
              Text(
                text = "Hint: ${task.hint}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            hints.forEach { h ->
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "• ${h.title}: ${h.content}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
