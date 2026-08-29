package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.services.LevelUpResult
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.QuestSecondary
import com.example.ui.theme.XpGold

@Composable
fun LevelUpDialog(
  levelUp: LevelUpResult,
  onDismiss: () -> Unit
) {
  var animationPlayed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (animationPlayed) 1f else 0.7f,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "levelup_scale"
  )

  LaunchedEffect(Unit) {
    animationPlayed = true
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .scale(scale)
        .fillMaxWidth()
        .clip(RoundedCornerShape(28.dp))
        .border(2.dp, XpGold, RoundedCornerShape(28.dp))
        .testTag("levelup_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                QuestPrimaryDark.copy(alpha = 0.95f),
                MaterialTheme.colorScheme.surface
              )
            )
          )
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Starburst Badge
        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(XpGold, Color(0xFFE65100))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Level Up Star",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "LEVEL UP!",
          style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
          ),
          color = XpGold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Level ${levelUp.oldLevel}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "➔",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = XpGold
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Level ${levelUp.newLevel}",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Black,
              color = QuestPrimary
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rewards Card
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Coin Reward
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(XpGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Toll,
                  contentDescription = null,
                  tint = XpGold,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "+${levelUp.coinReward} CodeCoins",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Level-up milestone bonus",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Unlocked Feature
            if (levelUp.unlockedFeature != null) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(QuestSecondary.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = QuestSecondary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Unlocked: ${levelUp.unlockedFeature}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "New gameplay content available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GameButton(
          text = "Claim & Continue",
          onClick = onDismiss,
          style = GameButtonStyle.PRIMARY,
          testTag = "levelup_claim_button"
        )
      }
    }
  }
}
