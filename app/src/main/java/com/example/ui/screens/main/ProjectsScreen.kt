package com.example.ui.screens.main

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ProjectEntity
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryContainer
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold

@Composable
fun ProjectsScreen(
  projects: List<ProjectEntity>,
  onOpenProject: (ProjectEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("All") }
  var selectedLanguageFilter by remember { mutableStateOf("All") }

  val filteredProjects = remember(projects, selectedFilter, selectedLanguageFilter) {
    var list = projects
    if (selectedLanguageFilter != "All") {
      list = list.filter { it.language.equals(selectedLanguageFilter, ignoreCase = true) }
    }
    when (selectedFilter) {
      "In Progress" -> list.filter { it.isUnlocked && !it.isCompleted }
      "Completed" -> list.filter { it.isCompleted }
      "Beginner" -> list.filter { it.difficulty.equals("Beginner", ignoreCase = true) }
      "Intermediate" -> list.filter { it.difficulty.equals("Intermediate", ignoreCase = true) }
      "Advanced" -> list.filter { it.difficulty.equals("Advanced", ignoreCase = true) }
      else -> list
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Project Lab",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Build complete software projects across multi-file workspaces.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Language and Status Filter Chips
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val langFilters = listOf("All", "Python", "JavaScript", "Java", "C", "Cpp")
          items(langFilters) { lang ->
            val label = when (lang) {
              "Cpp" -> "C++"
              else -> lang
            }
            FilterChip(
              selected = selectedLanguageFilter == lang,
              onClick = { selectedLanguageFilter = lang },
              label = { Text(label, fontWeight = FontWeight.SemiBold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuestPrimary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val filters = listOf("All", "In Progress", "Completed", "Beginner", "Intermediate", "Advanced")
          items(filters) { filter ->
            FilterChip(
              selected = selectedFilter == filter,
              onClick = { selectedFilter = filter },
              label = { Text(filter) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuestPrimaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }
      }
    }

    if (filteredProjects.isEmpty()) {
      item {
        GameCard {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
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
              text = "No projects in this category",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    } else {
      items(filteredProjects) { project ->
        ProjectCard(
          project = project,
          onClick = { onOpenProject(project) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ProjectCard(
  project: ProjectEntity,
  onClick: () -> Unit
) {
  val skills = remember(project) { project.parseSkills() }
  val tasks = remember(project) { project.parseTasks() }

  GameCard(
    borderColor = when {
      project.isCompleted -> QuestSuccess.copy(alpha = 0.4f)
      project.isUnlocked -> QuestPrimary.copy(alpha = 0.35f)
      else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    },
    onClick = if (project.isUnlocked) onClick else null
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
        .testTag("project_card_${project.id}")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = QuestPrimary.copy(alpha = 0.15f)
          ) {
            Text(
              text = project.language.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(
                color = QuestPrimary,
                fontWeight = FontWeight.Black
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(QuestPrimary.copy(alpha = 0.12f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = project.difficulty,
              style = MaterialTheme.typography.labelSmall.copy(
                color = QuestPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = project.estimatedTime,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "+${project.xpReward} XP",
            style = MaterialTheme.typography.labelLarge.copy(
              color = XpGold,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "+${project.coinReward}",
            style = MaterialTheme.typography.labelMedium.copy(
              color = QuestGold,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = project.title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = project.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (skills.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          skills.take(3).forEach { skill ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
              Text(
                text = skill,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = project.badgeName,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        if (project.isCompleted) {
          OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Replay,
              contentDescription = null,
              tint = QuestSuccess,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Review Workspace", style = MaterialTheme.typography.labelSmall.copy(color = QuestSuccess, fontWeight = FontWeight.Bold))
          }
        } else if (project.isUnlocked) {
          Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(34.dp).testTag("btn_open_project_${project.id}")
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Open Lab", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Locked",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Locked",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
