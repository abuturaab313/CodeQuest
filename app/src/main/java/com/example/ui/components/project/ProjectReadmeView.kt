package com.example.ui.components.project

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.models.ProjectEntity
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.XpGold

@Composable
fun ProjectReadmeView(
  project: ProjectEntity,
  readmeContent: String?,
  modifier: Modifier = Modifier
) {
  val skills = remember(project) { project.parseSkills() }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(QuestPrimary.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = project.difficulty,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = QuestPrimary
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = project.estimatedTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = project.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = project.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (skills.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Key Skills Practiced:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              skills.forEach { skill ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surface
                ) {
                  Text(
                    text = skill,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Rewards Banner
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = QuestGold.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("+${project.xpReward} XP", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = XpGold)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = QuestGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(project.badgeName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = QuestGold)
          }
        }
      }
    }

    // Project Specification / README Text
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("README.md & Specification", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          }
          Spacer(modifier = Modifier.height(10.dp))

          val displayContent = readmeContent?.ifBlank { project.instructions } ?: project.instructions
          Text(
            text = displayContent,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("project_readme_text")
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
