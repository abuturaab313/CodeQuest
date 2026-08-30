package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserEntity
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.QuestPrimaryLight
import com.example.ui.theme.QuestSecondary
import com.example.ui.theme.QuestSecondaryDark
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.XpGold

import com.example.ui.audio.LocalSoundManager

@Composable
fun GameHudBar(
  user: UserEntity?,
  modifier: Modifier = Modifier,
  onStreakClick: () -> Unit = {},
  onHeartsClick: () -> Unit = {},
  onCoinsClick: () -> Unit = {}
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Level & XP Pill
      val currentXp = user?.xp ?: 0
      val currentLevel = user?.level ?: 1
      val xpForNext = currentLevel * 150
      val xpProgress = (currentXp % 150) / 150f

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(QuestPrimary),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "$currentLevel",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = "XP",
              tint = XpGold,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "$currentXp XP",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          LinearProgressIndicator(
            progress = { xpProgress.coerceIn(0f, 1f) },
            modifier = Modifier
              .width(54.dp)
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = QuestPrimary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            strokeCap = StrokeCap.Round
          )
        }
      }

      // Streak Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onStreakClick)
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("streak_hud_pill")
      ) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = "Streak",
          tint = StreakFlame,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${user?.streakDays ?: 1}",
          style = MaterialTheme.typography.labelLarge.copy(
            color = StreakFlame,
            fontWeight = FontWeight.Black
          )
        )
      }

      // Hearts Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onHeartsClick)
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("hearts_hud_pill")
      ) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Hearts",
          tint = HeartRose,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${user?.currentHearts ?: 5}",
          style = MaterialTheme.typography.labelLarge.copy(
            color = HeartRose,
            fontWeight = FontWeight.Black
          )
        )
      }

      // Coins Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable(onClick = onCoinsClick)
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("coins_hud_pill")
      ) {
        Icon(
          imageVector = Icons.Default.Toll,
          contentDescription = "Coins",
          tint = XpGold,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${user?.coins ?: 100}",
          style = MaterialTheme.typography.labelLarge.copy(
            color = XpGold,
            fontWeight = FontWeight.Black
          )
        )
      }
    }
  }
}

enum class GameButtonStyle {
  PRIMARY,
  SECONDARY,
  GHOST,
  DANGER
}

@Composable
fun GameButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  style: GameButtonStyle = GameButtonStyle.PRIMARY,
  enabled: Boolean = true,
  icon: ImageVector? = null,
  testTag: String = "game_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "button_press")
  val soundManager = LocalSoundManager.current

  val backgroundBrush = when (style) {
    GameButtonStyle.PRIMARY -> Brush.horizontalGradient(listOf(QuestPrimaryLight, QuestPrimaryDark))
    GameButtonStyle.SECONDARY -> Brush.horizontalGradient(listOf(QuestSecondary, QuestSecondaryDark))
    GameButtonStyle.DANGER -> Brush.horizontalGradient(listOf(HeartRose, Color(0xFF93000A)))
    GameButtonStyle.GHOST -> Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
  }

  val contentColor = when (style) {
    GameButtonStyle.GHOST -> MaterialTheme.colorScheme.primary
    else -> Color.White
  }

  val borderModifier = if (style == GameButtonStyle.GHOST) {
    Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .scale(scale)
      .fillMaxWidth()
      .height(52.dp)
      .clip(RoundedCornerShape(16.dp))
      .then(borderModifier)
      .background(if (enabled) backgroundBrush else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray)))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = {
          soundManager?.playTap()
          onClick()
        }
      )
      .minimumInteractiveComponentSize()
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        ),
        color = contentColor
      )
    }
  }
}

@Composable
fun GameCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val soundManager = LocalSoundManager.current
  val clickableModifier = if (onClick != null) {
    Modifier.clickable {
      soundManager?.playTap()
      onClick()
    }
  } else {
    Modifier
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(cornerRadius))
      .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
      .then(clickableModifier),
    shape = RoundedCornerShape(cornerRadius),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
  ) {
    content()
  }
}
