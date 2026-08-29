package com.example.ui.screens.lesson.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HeartRose
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.XpGold

@Composable
fun BossBattleHeader(
  bossName: String,
  currentPhase: Int,
  totalPhases: Int,
  modifier: Modifier = Modifier
) {
  val remainingHp = (totalPhases - currentPhase).toFloat() / totalPhases.toFloat()
  val animatedHp by animateFloatAsState(
    targetValue = remainingHp.coerceIn(0f, 1f),
    animationSpec = tween(600),
    label = "boss_hp"
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFF1E1B4B),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, HeartRose.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(HeartRose),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "BOSS BATTLE",
              style = MaterialTheme.typography.labelSmall.copy(
                color = HeartRose,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = bossName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = HeartRose.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, HeartRose)
        ) {
          Text(
            text = "Phase $currentPhase / $totalPhases",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Boss HP Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "HP ${(animatedHp * 100).toInt()}%",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.LightGray),
          modifier = Modifier.width(55.dp)
        )
        LinearProgressIndicator(
          progress = { animatedHp },
          modifier = Modifier
            .weight(1f)
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = HeartRose,
          trackColor = Color(0xFF374151),
          strokeCap = StrokeCap.Round
        )
      }
    }
  }
}
