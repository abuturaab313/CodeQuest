package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.models.UserEntity
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimary

@Composable
fun QuickSettingsDialog(
  user: UserEntity?,
  onDismiss: () -> Unit,
  onUpdateSettings: (sound: Boolean, haptics: Boolean, dark: Boolean, reducedMotion: Boolean) -> Unit,
  onRefillHearts: () -> Unit,
  onResetProgress: () -> Unit,
  onResetOnboarding: () -> Unit
) {
  var sound by remember { mutableStateOf(user?.soundEnabled ?: true) }
  var haptics by remember { mutableStateOf(user?.hapticsEnabled ?: true) }
  var dark by remember { mutableStateOf(user?.darkMode ?: true) }
  var reducedMotion by remember { mutableStateOf(user?.reducedMotion ?: false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        .testTag("settings_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Game Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("settings_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sound FX Setting
        SettingToggleRow(
          title = "Sound Effects",
          description = "Celebration chimes & audio cues",
          icon = Icons.Default.VolumeUp,
          checked = sound,
          onCheckedChange = {
            sound = it
            onUpdateSettings(sound, haptics, dark, reducedMotion)
          },
          testTag = "setting_toggle_sound"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Haptic Feedback Setting
        SettingToggleRow(
          title = "Haptic Vibration",
          description = "Tactile feedback on correct/wrong answers",
          icon = Icons.Default.Vibration,
          checked = haptics,
          onCheckedChange = {
            haptics = it
            onUpdateSettings(sound, haptics, dark, reducedMotion)
          },
          testTag = "setting_toggle_haptics"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dark Mode Setting
        SettingToggleRow(
          title = "Dark Theme",
          description = "Optimized for night coding focus",
          icon = Icons.Default.DarkMode,
          checked = dark,
          onCheckedChange = {
            dark = it
            onUpdateSettings(sound, haptics, dark, reducedMotion)
          },
          testTag = "setting_toggle_dark"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Reduced Motion Setting
        SettingToggleRow(
          title = "Reduced Motion",
          description = "Disable intensive layout animations & effects",
          icon = Icons.Default.GraphicEq,
          checked = reducedMotion,
          onCheckedChange = {
            reducedMotion = it
            onUpdateSettings(sound, haptics, dark, reducedMotion)
          },
          testTag = "setting_toggle_reduced_motion"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Refill Hearts Button
        Button(
          onClick = {
            onRefillHearts()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = HeartRose.copy(alpha = 0.15f),
            contentColor = HeartRose
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("settings_refill_hearts_button")
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Refill Hearts to 5/5", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset Progress Button (for testing)
        Button(
          onClick = {
            onResetOnboarding()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = QuestPrimary.copy(alpha = 0.1f),
            contentColor = QuestPrimary
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("settings_replay_onboarding_button")
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Replay Onboarding Flow", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset Progress Button (for testing)
        Button(
          onClick = {
            onResetProgress()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("settings_reset_progress_button")
        ) {
          Icon(
            imageVector = Icons.Default.RestartAlt,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Reset Progress (Test Mode)", fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
private fun SettingToggleRow(
  title: String,
  description: String,
  icon: ImageVector,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(QuestPrimary.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = QuestPrimary,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = QuestPrimary
      ),
      modifier = Modifier.testTag(testTag)
    )
  }
}
