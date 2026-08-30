package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.QuestSuccess

@Composable
fun OnboardingScreen(
    onComplete: (experience: String, goal: String, path: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedExperience by remember { mutableStateOf("BEGINNER") }
    var selectedGoal by remember { mutableStateOf("LEARN_BASICS") }
    var selectedPath by remember { mutableStateOf("PYTHON") }

    val totalSteps = 5
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

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentStep,
                    label = "onboarding_step",
                    transitionSpec = {
                        if (targetState > initialState) {
                            (fadeIn() + slideInHorizontally { it }).togetherWith(fadeOut() + slideOutHorizontally { -it })
                        } else {
                            (fadeIn() + slideInHorizontally { -it }).togetherWith(fadeOut() + slideOutHorizontally { it })
                        }
                    }
                ) { step ->
                    when (step) {
                        0 -> WelcomeStep()
                        1 -> ExperienceStep(selected = selectedExperience, onSelect = { selectedExperience = it })
                        2 -> GoalStep(selected = selectedGoal, onSelect = { selectedGoal = it })
                        3 -> PathStep(selected = selectedPath, onSelect = { selectedPath = it })
                        4 -> ReadyStep()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0 && currentStep < 4) {
                    GameButton(
                        text = "Back",
                        onClick = { currentStep -= 1 },
                        style = GameButtonStyle.GHOST,
                        modifier = Modifier.weight(0.35f),
                        testTag = "onboarding_back_button"
                    )
                }
                
                val buttonText = when (currentStep) {
                    0 -> "Get Started"
                    4 -> "Start Learning"
                    else -> "Continue"
                }
                
                GameButton(
                    text = buttonText,
                    onClick = {
                        if (currentStep < 4) {
                            currentStep += 1
                        } else {
                            onComplete(selectedExperience, selectedGoal, selectedPath)
                        }
                    },
                    style = GameButtonStyle.PRIMARY,
                    modifier = Modifier.weight(if (currentStep > 0 && currentStep < 4) 0.65f else 1f),
                    testTag = "onboarding_next_button"
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(QuestPrimary.copy(alpha = 0.1f))
                .border(2.dp, QuestPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = null,
                tint = QuestPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to CodeQuest",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your journey to becoming a master developer starts here. Play challenges, build real projects, and level up your career.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun ExperienceStep(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        Triple("BEGINNER", "I'm a Beginner", "I'm just starting my coding journey"),
        Triple("SOME", "I have some experience", "I know some basics but want more"),
        Triple("INTERMEDIATE", "I'm Intermediate", "I can build simple apps or scripts"),
        Triple("ADVANCED", "I'm Advanced", "I want to master complex architectures")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "What's your level?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(24.dp))
        options.forEach { (key, title, desc) ->
            SelectionCard(
                title = title,
                subtitle = desc,
                isSelected = selected == key,
                icon = Icons.Default.School,
                onClick = { onSelect(key) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GoalStep(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        Triple("LEARN", "Learn New Skills", "Expand my knowledge base"),
        Triple("PRACTICE", "Daily Practice", "Keep my skills sharp"),
        Triple("INTERVIEW", "Interview Prep", "Get ready for technical rounds"),
        Triple("PROBLEM_SOLVING", "Problem Solving", "Logic and algorithm challenges"),
        Triple("BETTER_PROG", "Better Programming", "Clean code and architecture")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "What's your goal?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(24.dp))
        options.forEach { (key, title, desc) ->
            SelectionCard(
                title = title,
                subtitle = desc,
                isSelected = selected == key,
                icon = Icons.Default.Star,
                onClick = { onSelect(key) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PathStep(selected: String, onSelect: (String) -> Unit) {
    val options = listOf(
        Triple("PYTHON", "Python Path", "AI, Data Science, and Backend"),
        Triple("WEB", "Web Development", "Frontend and Full-Stack Mastery"),
        Triple("MOBILE", "Mobile Development", "Build Android and iOS Apps"),
        Triple("SYSTEMS", "Systems Engineering", "C++, Rust, and Performance"),
        Triple("DATA", "Data & SQL", "Master databases and analytics")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Choose your path",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(24.dp))
        options.forEach { (key, title, desc) ->
            SelectionCard(
                title = title,
                subtitle = desc,
                isSelected = selected == key,
                icon = Icons.Default.Explore,
                onClick = { onSelect(key) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ReadyStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(QuestSuccess.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = QuestSuccess,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've customized your experience based on your choices. Ready to begin your first quest?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
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
    GameCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isSelected) QuestPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) QuestPrimary.copy(alpha = 0.05f) else Color.Transparent)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) QuestPrimary else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = QuestPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
