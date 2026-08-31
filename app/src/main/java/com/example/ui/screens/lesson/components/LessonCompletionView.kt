package com.example.ui.screens.lesson.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LessonEntity
import com.example.domain.learning.LessonScoringResult
import com.example.ui.audio.LocalSoundManager
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.QuestSuccess
import com.example.ui.theme.XpGold
import kotlinx.coroutines.delay

@Composable
fun LessonCompletionView(
  lesson: LessonEntity,
  scoringResult: LessonScoringResult,
  onContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  val soundManager = LocalSoundManager.current
  var animationStep by remember { mutableStateOf(0) }

  LaunchedEffect(Unit) {
    delay(300)
    soundManager?.playLevelComplete()
    animationStep = 1
    
    // Animate stars
    for (i in 1..scoringResult.stars) {
      delay(400)
      soundManager?.playStarEarned()
      animationStep = 1 + i
    }
    
    delay(500)
    soundManager?.playXpEarned()
    animationStep = 5
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(QuestPrimaryDark, QuestPrimary, QuestPrimaryDark)
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Trophy Icon with pop animation
      val trophyScale by animateFloatAsState(
        targetValue = if (animationStep >= 1) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "trophy_scale"
      )

      Box(
        modifier = Modifier
          .size(120.dp)
          .scale(trophyScale)
          .clip(CircleShape)
          .background(XpGold.copy(alpha = 0.2f))
          .border(4.dp, XpGold, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.EmojiEvents,
          contentDescription = "Victory",
          tint = XpGold,
          modifier = Modifier.size(70.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = if (scoringResult.isPerfectRun) "PERFECT RUN!" else "QUEST COMPLETED!",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
        color = XpGold,
        modifier = Modifier.scale(trophyScale)
      )
      
      Spacer(modifier = Modifier.height(8.dp))
      
      Text(
        text = lesson.title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Animated Stars Display
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 1..3) {
          val isFilled = i <= scoringResult.stars
          val shouldShow = animationStep > i
          
          val starScale by animateFloatAsState(
            targetValue = if (shouldShow) 1f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
            label = "star_scale_$i"
          )
          
          Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
            // Silhouette
            Icon(
              Icons.Default.Star,
              contentDescription = null,
              tint = Color.Black.copy(alpha = 0.3f),
              modifier = Modifier.size(50.dp)
            )
            
            // Popped Star
            if (shouldShow && isFilled) {
              Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = XpGold,
                modifier = Modifier
                  .size(60.dp)
                  .scale(starScale)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))

      // Stats Board
      val statsAlpha by animateFloatAsState(
        targetValue = if (animationStep >= 5) 1f else 0f,
        animationSpec = tween(500),
        label = "stats_alpha"
      )

      Column(modifier = Modifier.fillMaxWidth().scale(statsAlpha)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // XP Earned
          Surface(
            modifier = Modifier.weight(1f).height(90.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(2.dp, XpGold.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "XP GAINED",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = XpGold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = XpGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "+${scoringResult.totalXp}",
                  style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                  color = Color.White
                )
              }
            }
          }

          // Accuracy
          Surface(
            modifier = Modifier.weight(1f).height(90.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(2.dp, QuestSuccess.copy(alpha = 0.5f))
          ) {
            Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "ACCURACY",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = QuestSuccess
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "${scoringResult.accuracyPercentage}%",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
              )
            }
          }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary text
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = Color.White.copy(alpha = 0.1f)
        ) {
          Text(
            text = scoringResult.performanceSummary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      GameButton(
        text = "CONTINUE",
        onClick = onContinue,
        style = GameButtonStyle.PRIMARY,
        icon = Icons.Default.PlayArrow,
        modifier = Modifier.fillMaxWidth().height(60.dp).scale(statsAlpha),
        testTag = "continue_journey_button"
      )
      
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
