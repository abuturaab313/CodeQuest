package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryDark
import com.example.ui.theme.QuestPrimaryLight
import com.example.ui.theme.QuestSecondary
import com.example.ui.theme.QuestSuccess

@Composable
fun OnboardingScreen(
  onComplete: (experience: String, language: String, dailyGoal: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(0) }
  var selectedExperience by remember { mutableStateOf("COMPLETE_BEGINNER") }
  var selectedLanguage by remember { mutableStateOf("python") }
  var selectedGoalMinutes by remember { mutableIntStateOf(15) }

  val totalSteps = 4
  val progress = (currentStep + 1) / totalSteps.toFloat()

  Surface(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Step Progress Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .weight(1f)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = QuestPrimary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "${currentStep + 1} / $totalSteps",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Box(modifier = Modifier.weight(1f)) {
        AnimatedContent(targetState = currentStep, label = "onboarding_step") { step ->
          when (step) {
            0 -> WelcomeStep(onStart = { currentStep = 1 })
            1 -> ExperienceStep(
              selected = selectedExperience,
              onSelect = { selectedExperience = it }
            )
            2 -> LanguageStep(
              selected = selectedLanguage,
              onSelect = { selectedLanguage = it }
            )
            3 -> DailyGoalStep(
              selected = selectedGoalMinutes,
              onSelect = { selectedGoalMinutes = it }
            )
          }
        }
      }

      // Bottom Navigation Button
      if (currentStep > 0) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (currentStep > 1) {
            GameButton(
              text = "Back",
              onClick = { currentStep -= 1 },
              style = GameButtonStyle.GHOST,
              modifier = Modifier.weight(0.35f),
              testTag = "onboarding_back_button"
            )
          }
          GameButton(
            text = if (currentStep == 3) "Start My Quest" else "Continue",
            onClick = {
              if (currentStep < 3) {
                currentStep += 1
              } else {
                onComplete(selectedExperience, selectedLanguage, selectedGoalMinutes)
              }
            },
            style = GameButtonStyle.PRIMARY,
            modifier = Modifier.weight(if (currentStep > 1) 0.65f else 1f),
            testTag = "onboarding_continue_button"
          )
        }
      }
    }
  }
}

@Composable
private fun WelcomeStep(onStart: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(110.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(QuestPrimary.copy(alpha = 0.2f), Color.Transparent)
          )
        )
        .border(2.dp, QuestPrimary, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.RocketLaunch,
        contentDescription = "CodeQuest Emblem",
        tint = QuestPrimary,
        modifier = Modifier.size(54.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "CodeQuest",
      style = MaterialTheme.typography.displayLarge.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Learn to Code. Play. Build. Level Up.",
      style = MaterialTheme.typography.titleLarge.copy(
        color = QuestPrimary,
        fontWeight = FontWeight.Bold
      ),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Master real programming through an interactive game world of hands-on challenges, live syntax checks, automated tests, and daily quests.",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(40.dp))

    GameButton(
      text = "Begin Adventure",
      onClick = onStart,
      style = GameButtonStyle.PRIMARY,
      icon = Icons.Default.AutoAwesome,
      testTag = "onboarding_start_button"
    )
  }
}

@Composable
private fun ExperienceStep(
  selected: String,
  onSelect: (String) -> Unit
) {
  val options = listOf(
    Triple("COMPLETE_BEGINNER", "Complete Beginner", "Never written a single line of code"),
    Triple("BEGINNER", "Beginner", "Know basic concepts like variables & if-conditions"),
    Triple("INTERMEDIATE", "Intermediate", "Comfortable with loops, functions, and data structures"),
    Triple("ADVANCED", "Advanced", "Looking for complex algorithms, design & projects")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = "What is your coding experience?",
      style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "We will calibrate the learning path and challenges to your level.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    options.forEach { (key, title, subtitle) ->
      val isSelected = selected == key
      SelectionCard(
        title = title,
        subtitle = subtitle,
        isSelected = isSelected,
        icon = Icons.Default.School,
        onClick = { onSelect(key) }
      )
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
private fun LanguageStep(
  selected: String,
  onSelect: (String) -> Unit
) {
  val languages = listOf(
    Triple("python", "Python", "Beginner-friendly, AI, Data Science & Backend"),
    Triple("javascript", "JavaScript", "Modern Web, Interactive UI & Full-Stack"),
    Triple("html_css", "HTML & CSS", "Visual Web Design, Layouts & Styling"),
    Triple("java", "Java", "Enterprise Systems, OOP & Android Foundations"),
    Triple("cpp", "C & C++", "High Performance, Game Engines & Systems"),
    Triple("sql", "SQL", "Database Querying, Relational Data & Analytics")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = "Choose your starting language",
      style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "You can switch courses or explore other languages at any time.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    languages.forEach { (id, name, desc) ->
      val isSelected = selected == id
      val icon = when (id) {
        "python" -> Icons.Default.Code
        "javascript" -> Icons.Default.Javascript
        "html_css" -> Icons.Default.Html
        "java" -> Icons.Default.DataObject
        "cpp" -> Icons.Default.Memory
        else -> Icons.Default.Storage
      }

      SelectionCard(
        title = name,
        subtitle = desc,
        isSelected = isSelected,
        icon = icon,
        onClick = { onSelect(id) }
      )
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
private fun DailyGoalStep(
  selected: Int,
  onSelect: (Int) -> Unit
) {
  val goals = listOf(
    Triple(5, "Casual Pace", "5 minutes / day • 1 lesson"),
    Triple(15, "Regular Adventurer", "15 minutes / day • 2-3 lessons (Recommended)"),
    Triple(30, "Serious Hacker", "30 minutes / day • 5 lessons + coding challenges")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = "Set your daily coding target",
      style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Consistency builds mastery. Daily quests and streaks will help keep you engaged.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    goals.forEach { (minutes, title, subtitle) ->
      val isSelected = selected == minutes
      SelectionCard(
        title = title,
        subtitle = subtitle,
        isSelected = isSelected,
        icon = Icons.Default.Timer,
        onClick = { onSelect(minutes) }
      )
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
private fun SelectionCard(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  icon: ImageVector,
  onClick: () -> Unit
) {
  val borderColor = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
  val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

  GameCard(
    modifier = Modifier.fillMaxWidth(),
    borderColor = borderColor,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(backgroundColor)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(if (isSelected) QuestPrimary else MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (isSelected) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Selected",
          tint = QuestPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}
