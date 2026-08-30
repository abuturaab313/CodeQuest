package com.example.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AchievementEntity
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.UserEntity
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.*
import com.example.data.models.UnlockedCosmeticEntity

@Composable
fun ProfileScreen(
  user: UserEntity?,
  achievements: List<AchievementEntity>,
  skills: List<SkillMasteryEntity>,
  onUpgradeAccount: (email: String, username: String) -> Unit,
  modifier: Modifier = Modifier,
  unlockedCosmetics: List<UnlockedCosmeticEntity> = emptyList(),
  developerStats: com.example.data.models.DeveloperStatsEntity? = null
) {
  var showUpgradeDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // User Profile Header Card
      ProfileHeaderCard(
        user = user,
        unlockedCosmetics = unlockedCosmetics,
        onUpgradeClick = { showUpgradeDialog = true }
      )
    }

    item {
      // Developer Journey Summary Card
      DeveloperJourneyCard(stats = developerStats)
    }

    item {
      // Language Progress Breakdown
      LanguageBreakdownCard(skills = skills, totalXp = user?.xp ?: 0)
    }

    item {
      // Skill Tree Mastery Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = QuestPrimary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Skill Tree Mastery",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }

    // Skill Tree Mastery Progress Items
    items(skills) { skill ->
      SkillMasteryRow(skill = skill)
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Achievements Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = XpGold,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Badges & Achievements",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }

    // Achievements Items
    items(achievements) { ach ->
      AchievementCard(achievement = ach)
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showUpgradeDialog) {
    UpgradeAccountDialog(
      onDismiss = { showUpgradeDialog = false },
      onConfirm = { email, username ->
        onUpgradeAccount(email, username)
        showUpgradeDialog = false
      }
    )
  }
}

@Composable
private fun ProfileHeaderCard(
  user: UserEntity?,
  unlockedCosmetics: List<UnlockedCosmeticEntity>,
  onUpgradeClick: () -> Unit
) {
  val currentLevel = user?.level ?: 1
  val currentXp = user?.xp ?: 0
  val nextLevelXp = currentLevel * 150
  val xpProgress = (currentXp % 150) / 150f

  GameCard(borderColor = QuestPrimary.copy(alpha = 0.3f)) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              MaterialTheme.colorScheme.surfaceVariant,
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(QuestPrimary)
          .border(2.dp, QuestPrimaryLight, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (user?.avatarId) {
            "avatar_ninja" -> Icons.Default.AutoAwesome
            "avatar_coder" -> Icons.Default.Code
            "avatar_space" -> Icons.Default.Star
            else -> Icons.Default.AccountCircle
          },
          contentDescription = "Avatar",
          tint = Color.White,
          modifier = Modifier.size(48.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = user?.username ?: "Code Adventurer",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      val equippedTitleItem = unlockedCosmetics.firstOrNull { it.category == "TITLE" && it.isEquipped }
      val equippedTitle = equippedTitleItem?.id
      if (!equippedTitle.isNullOrBlank()) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = QuestGold.copy(alpha = 0.18f),
          border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold),
          modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        ) {
          Text(
            text = equippedTitle.replace("title_", "").replace('_', ' ').uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = XpGold, letterSpacing = 1.sp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Text(
        text = if (user?.isGuest == true) "Guest Account (Local Progress Safe)" else (user?.email ?: "Adventurer"),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Level & XP Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "LEVEL $currentLevel",
          style = MaterialTheme.typography.labelLarge.copy(
            color = QuestPrimary,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "$currentXp / $nextLevelXp XP",
          style = MaterialTheme.typography.labelMedium.copy(
            color = XpGold,
            fontWeight = FontWeight.Bold
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { xpProgress.coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = QuestPrimary,
        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        strokeCap = StrokeCap.Round
      )

      if (user?.isGuest == true) {
        Spacer(modifier = Modifier.height(16.dp))
        GameButton(
          text = "Save Progress to Account",
          onClick = onUpgradeClick,
          style = GameButtonStyle.SECONDARY,
          icon = Icons.Default.WorkspacePremium,
          testTag = "upgrade_account_button"
        )
      }
    }
  }
}

@Composable
private fun SkillMasteryRow(skill: SkillMasteryEntity) {
  GameCard {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = skill.skillName,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${skill.masteryPercentage}%",
          style = MaterialTheme.typography.labelLarge.copy(
            color = QuestPrimary,
            fontWeight = FontWeight.Bold
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { (skill.masteryPercentage / 100f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = QuestPrimary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
      )
    }
  }
}

@Composable
private fun AchievementCard(achievement: AchievementEntity) {
  GameCard(
    borderColor = if (achievement.isUnlocked) XpGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
        .testTag("achievement_${achievement.id}"),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(if (achievement.isUnlocked) XpGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
          contentDescription = null,
          tint = if (achievement.isUnlocked) XpGold else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = achievement.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = achievement.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      if (achievement.isUnlocked) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Unlocked",
          tint = QuestSuccess,
          modifier = Modifier.size(20.dp)
        )
      } else {
        Text(
          text = "+${achievement.xpReward} XP",
          style = MaterialTheme.typography.labelSmall.copy(color = XpGold, fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}

@Composable
private fun UpgradeAccountDialog(
  onDismiss: () -> Unit,
  onConfirm: (email: String, username: String) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Upgrade to Full Account", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Save your XP, streak, and level progress securely to the cloud.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Adventurer Name") },
          modifier = Modifier.fillMaxWidth().testTag("upgrade_username_input")
        )
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          modifier = Modifier.fillMaxWidth().testTag("upgrade_email_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (email.isNotBlank() && username.isNotBlank()) {
            onConfirm(email, username)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary, contentColor = Color.White),
        modifier = Modifier.testTag("upgrade_confirm_button")
      ) {
        Text("Save & Sync", fontWeight = FontWeight.Bold)
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
private fun LanguageBreakdownCard(
  skills: List<com.example.data.models.SkillMasteryEntity>,
  totalXp: Int
) {
  val languages = listOf(
    Triple("python", "Python", Color(0xFF3572A5)),
    Triple("javascript", "JavaScript", Color(0xFFF7DF1E)),
    Triple("java", "Java", Color(0xFFB07219)),
    Triple("c", "C", Color(0xFF555555)),
    Triple("cpp", "C++", Color(0xFFF34B7D))
  )

  GameCard(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = QuestPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Language Fluency",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "$totalXp Total XP",
          style = MaterialTheme.typography.labelMedium.copy(color = XpGold, fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      languages.forEach { (langId, langName, langColor) ->
        val langSkills = skills.filter { it.language.equals(langId, ignoreCase = true) }
        val avgMastery = if (langSkills.isNotEmpty()) {
          langSkills.map { it.masteryPercentage }.average().toInt()
        } else {
          0
        }

        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(langColor)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = langName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "$avgMastery% Fluency",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          LinearProgressIndicator(
            progress = { (avgMastery / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = langColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun DeveloperJourneyCard(
  stats: com.example.data.models.DeveloperStatsEntity?
) {
  GameCard(borderColor = QuestPrimary.copy(alpha = 0.3f)) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Developer Lab Journey",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "Real-World Skills",
          style = MaterialTheme.typography.labelSmall.copy(color = QuestCyan, fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        DevStatMini(
          label = "Bugs Fixed",
          value = "${stats?.bugsFixedCount ?: 0}",
          icon = Icons.Default.BugReport,
          tint = com.example.ui.theme.QuestRed
        )
        DevStatMini(
          label = "Tests Written",
          value = "${stats?.testsPassedCount ?: 0}",
          icon = Icons.Default.TaskAlt,
          tint = com.example.ui.theme.QuestGreen
        )
        DevStatMini(
          label = "Git Commits",
          value = "${stats?.commitsCreatedCount ?: 0}",
          icon = Icons.Default.CallSplit,
          tint = QuestCyan
        )
        DevStatMini(
          label = "Code Reviews",
          value = "${stats?.codeReviewsCompleted ?: 0}",
          icon = Icons.Default.RateReview,
          tint = QuestPrimary
        )
      }
    }
  }
}

@Composable
private fun DevStatMini(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(tint.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Color.Gray))
  }
}
