package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.ExerciseEntity
import com.example.data.models.LessonEntity
import com.example.ui.MainViewModel
import com.example.ui.components.GameHudBar
import com.example.ui.components.LevelUpDialog
import com.example.ui.components.QuickSettingsDialog
import com.example.ui.components.RewardToast
import com.example.ui.components.ai.CodeCoachSheet
import com.example.ui.screens.CodeLabScreen
import com.example.ui.screens.lesson.LessonScreen
import com.example.ui.screens.main.HomeScreen
import com.example.ui.screens.main.LearnScreen
import com.example.ui.screens.main.PracticeScreen
import com.example.ui.screens.main.ProfileScreen
import com.example.ui.screens.main.ProjectsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.project.ProjectWorkspaceScreen
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryContainer
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
  HOME("Home", Icons.Default.Home, "nav_tab_home"),
  LEARN("Learn", Icons.Default.Explore, "nav_tab_learn"),
  PRACTICE("Practice", Icons.Default.AutoAwesome, "nav_tab_practice"),
  PROJECTS("Projects", Icons.Default.Science, "nav_tab_projects"),
  PROFILE("Profile", Icons.Default.Person, "nav_tab_profile")
}

@Composable
fun CodeQuestApp(
  viewModel: MainViewModel = viewModel()
) {
  val user by viewModel.userProfile.collectAsStateWithLifecycle()
  val playerProgress by viewModel.playerProgress.collectAsStateWithLifecycle()
  val dailyQuests by viewModel.dailyQuests.collectAsStateWithLifecycle()
  val achievements by viewModel.achievements.collectAsStateWithLifecycle()
  val worlds by viewModel.worlds.collectAsStateWithLifecycle()
  val lessons by viewModel.lessons.collectAsStateWithLifecycle()
  val challenges by viewModel.challenges.collectAsStateWithLifecycle()
  val skills by viewModel.skills.collectAsStateWithLifecycle()
  val projects by viewModel.projects.collectAsStateWithLifecycle()
  val unresolvedMistakes by viewModel.unresolvedMistakes.collectAsStateWithLifecycle()

  val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
  val dailyPracticeState by viewModel.dailyPracticeState.collectAsStateWithLifecycle()
  val learnerMemories by viewModel.learnerMemories.collectAsStateWithLifecycle()

  // Milestone 7 flows
  val weeklyEvents by viewModel.weeklyEvents.collectAsStateWithLifecycle()
  val friends by viewModel.friends.collectAsStateWithLifecycle()
  val unlockedCosmetics by viewModel.unlockedCosmetics.collectAsStateWithLifecycle()
  val dailyRewardClaims by viewModel.dailyRewardClaims.collectAsStateWithLifecycle()
  val competitorsBronze by viewModel.competitorsBronze.collectAsStateWithLifecycle()
  val competitorsSilver by viewModel.competitorsSilver.collectAsStateWithLifecycle()
  val competitorsGold by viewModel.competitorsGold.collectAsStateWithLifecycle()
  val competitorsCrystal by viewModel.competitorsCrystal.collectAsStateWithLifecycle()
  val competitorsObsidian by viewModel.competitorsObsidian.collectAsStateWithLifecycle()

  val bookmarkedLessons by viewModel.bookmarks.collectAsStateWithLifecycle()
  val lessonNotes by viewModel.notes.collectAsStateWithLifecycle()

  val showCoachSheet by viewModel.showCoachSheet.collectAsStateWithLifecycle()
  val activeCoachContext by viewModel.activeCoachContext.collectAsStateWithLifecycle()
  val activeCoachWorkspaceFiles by viewModel.activeCoachWorkspaceFiles.collectAsStateWithLifecycle()

  val activeLevelUp by viewModel.activeLevelUp.collectAsStateWithLifecycle()
  val activeReward by viewModel.activeReward.collectAsStateWithLifecycle()
  val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(MainTab.HOME) }
  var activeLesson by remember { mutableStateOf<LessonEntity?>(null) }
  var activeExercises by remember { mutableStateOf<List<ExerciseEntity>>(emptyList()) }
  var activeLessonProgress by remember { mutableStateOf<com.example.data.models.LessonProgressEntity?>(null) }
  var activeChallengeId by remember { mutableStateOf<String?>(null) }
  var activeProjectId by remember { mutableStateOf<String?>(null) }

  val coroutineScope = rememberCoroutineScope()

  // Level Up Modal
  if (activeLevelUp != null) {
    LevelUpDialog(
      levelUp = activeLevelUp!!,
      onDismiss = { viewModel.dismissLevelUp() }
    )
  }

  // Quick Settings Dialog
  if (showSettingsDialog) {
    QuickSettingsDialog(
      user = user,
      onDismiss = { viewModel.closeSettings() },
      onUpdateSettings = { sound, haptics, dark, reducedMotion ->
        viewModel.updateSettings(sound, haptics, dark, reducedMotion)
      },
      onRefillHearts = {
        viewModel.restoreHearts()
      },
      onResetProgress = {
        viewModel.resetAllProgress()
      }
    )
  }

  // Global Code Coach Modal Sheet
  if (showCoachSheet && activeCoachContext != null) {
    com.example.ui.components.ai.CodeCoachSheet(
      aiService = viewModel.aiService,
      context = activeCoachContext!!,
      workspaceFiles = activeCoachWorkspaceFiles,
      onDismiss = { viewModel.closeCodeCoach() }
    )
  }

  // If user hasn't finished onboarding, show full-bleed Onboarding flow
  if (user != null && !user!!.hasCompletedOnboarding) {
    OnboardingScreen(
      onComplete = { experience, language, dailyGoal ->
        viewModel.completeOnboarding(experience, language, dailyGoal)
      }
    )
    return
  }

  // If an active Project Workspace is opened, show ProjectWorkspaceScreen
  if (activeProjectId != null) {
    ProjectWorkspaceScreen(
      projectId = activeProjectId!!,
      viewModel = viewModel,
      onNavigateBack = { activeProjectId = null }
    )
    return
  }

  // If an active coding challenge is opened, show the Code Lab Screen
  if (activeChallengeId != null) {
    CodeLabScreen(
      challengeId = activeChallengeId!!,
      viewModel = viewModel,
      onNavigateBack = { activeChallengeId = null },
      onNavigateToNextChallenge = { nextId ->
        activeChallengeId = nextId
      }
    )
    return
  }

  // If an active lesson is being taken, show the Lesson Player Screen
  if (activeLesson != null) {
    val currentActiveLessonId = activeLesson!!.id
    val isBookmarked = bookmarkedLessons.any { it.lessonId == currentActiveLessonId }
    val initialNoteText = lessonNotes.find { it.lessonId == currentActiveLessonId }?.noteText ?: ""

    LessonScreen(
      lesson = activeLesson!!,
      exercises = activeExercises,
      currentHearts = user?.currentHearts ?: 5,
      initialProgress = activeLessonProgress,
      isBookmarked = isBookmarked,
      onToggleBookmark = {
        viewModel.toggleBookmark(currentActiveLessonId, activeLesson!!.title, activeLesson!!.worldId)
      },
      initialNoteText = initialNoteText,
      onSaveNote = { text ->
        viewModel.saveNoteForLesson(currentActiveLessonId, activeLesson!!.title, text)
      },
      onOpenCodeCoach = { coachContext ->
        viewModel.openCodeCoach(coachContext)
      },
      onClose = {
        activeLesson = null
        activeExercises = emptyList()
        activeLessonProgress = null
      },
      onCompleteLesson = { totalExercises, correctCount, mistakeCount, hintsUsed, baseXp, baseCoins ->
        val currentLessonItem = activeLesson!!
        val currentId = currentLessonItem.id
        // Calculate sequential next lesson ID in curriculum
        val currentIndex = lessons.indexOfFirst { it.id == currentId }
        val nextId = if (currentIndex in 0 until lessons.size - 1) {
          lessons[currentIndex + 1].id
        } else {
          null
        }

        viewModel.completeLesson(
          lessonId = currentId,
          totalExercises = totalExercises,
          correctCount = correctCount,
          mistakeCount = mistakeCount,
          hintsUsedCount = hintsUsed,
          baseXp = baseXp,
          baseCoins = baseCoins,
          nextLessonId = nextId
        )
        activeLesson = null
        activeExercises = emptyList()
        activeLessonProgress = null
      },
      onDeductHeart = {
        viewModel.deductHeart()
      },
      onRecordMistake = { exercise, wrongAnswer ->
        viewModel.recordMistake(exercise, wrongAnswer)
      },
      onSaveProgress = { progress ->
        viewModel.saveLessonProgress(progress)
      }
    )
    return
  }

  // Main Gaming Hub with Top HUD and Bottom Navigation Bar
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding(),
    topBar = {
      GameHudBar(
        user = user,
        onHeartsClick = {
          if ((user?.currentHearts ?: 0) < 5) {
            viewModel.restoreHearts()
          }
        },
        onStreakClick = {
          selectedTab = MainTab.HOME
        },
        onCoinsClick = {
          selectedTab = MainTab.PROFILE
        }
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .navigationBarsPadding()
          .testTag("main_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        MainTab.entries.forEach { tab ->
          val isSelected = selectedTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTab = tab },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title
              )
            },
            label = {
              Text(
                text = tab.title,
                style = MaterialTheme.typography.labelSmall
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
              selectedTextColor = QuestPrimary,
              indicatorColor = QuestPrimaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag(tab.tag)
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (selectedTab) {
        MainTab.HOME -> HomeScreen(
          playerProgress = playerProgress,
          user = user,
          dailyQuests = dailyQuests,
          achievements = achievements,
          recommendations = recommendations,
          dailyPracticeState = dailyPracticeState?.first,
          onContinueLearning = { targetLesson ->
            val lessonToStart = targetLesson
              ?: lessons.firstOrNull { it.isUnlocked && !it.isCompleted }
              ?: lessons.firstOrNull()
            if (lessonToStart != null) {
              coroutineScope.launch {
                val ex = viewModel.getExercisesForLesson(lessonToStart.id)
                val prog = viewModel.getLessonProgress(lessonToStart.id)
                activeExercises = ex
                activeLessonProgress = prog
                activeLesson = lessonToStart
              }
            }
          },
          onStartDailyChallenge = {
            val challengeLesson = lessons.firstOrNull { it.id == "py_w1_l7" } ?: lessons.firstOrNull()
            if (challengeLesson != null) {
              coroutineScope.launch {
                val ex = viewModel.getExercisesForLesson(challengeLesson.id)
                val prog = viewModel.getLessonProgress(challengeLesson.id)
                activeExercises = ex
                activeLessonProgress = prog
                activeLesson = challengeLesson
              }
            }
          },
          onClaimQuest = { quest ->
            viewModel.claimQuest(quest)
          },
          onOpenSettings = {
            viewModel.openSettings()
          },
          onNavigateToProfile = {
            selectedTab = MainTab.PROFILE
          }
        )

        MainTab.LEARN -> LearnScreen(
          worlds = worlds,
          lessons = lessons,
          onSelectLesson = { lesson ->
            coroutineScope.launch {
              val ex = viewModel.getExercisesForLesson(lesson.id)
              val prog = viewModel.getLessonProgress(lesson.id)
              activeExercises = ex
              activeLessonProgress = prog
              activeLesson = lesson
            }
          }
        )

        MainTab.PRACTICE -> PracticeScreen(
          challenges = challenges,
          onOpenCodingLab = { challengeId ->
            activeChallengeId = challengeId
          },
          unresolvedMistakes = unresolvedMistakes,
          learnerMemories = learnerMemories,
          dailyPracticeState = dailyPracticeState,
          onOpenCodeCoach = { ctx -> viewModel.openCodeCoach(ctx) },
          onClaimDailyReward = { viewModel.claimDailyPracticeReward() },
          onAdvanceDailyStep = { viewModel.advanceDailyPracticeStep() },
          onStartPractice = { practiceId ->
            val targetChallenge = challenges.firstOrNull()
            if (targetChallenge != null) {
              activeChallengeId = targetChallenge.id
            } else {
              val targetLesson = lessons.firstOrNull()
              if (targetLesson != null) {
                coroutineScope.launch {
                  val ex = viewModel.getExercisesForLesson(targetLesson.id)
                  val prog = viewModel.getLessonProgress(targetLesson.id)
                  activeExercises = ex
                  activeLessonProgress = prog
                  activeLesson = targetLesson
                }
              }
            }
          },
          weeklyEvents = weeklyEvents,
          friends = friends,
          unlockedCosmetics = unlockedCosmetics,
          dailyRewardClaims = dailyRewardClaims,
          competitorsBronze = competitorsBronze,
          competitorsSilver = competitorsSilver,
          competitorsGold = competitorsGold,
          competitorsCrystal = competitorsCrystal,
          competitorsObsidian = competitorsObsidian,
          userProfile = user,
          onClaimEventReward = { eventId -> viewModel.claimEventReward(eventId) },
          onPurchaseCosmetic = { itemId, costCoins, category -> viewModel.purchaseCosmetic(itemId, costCoins, category) },
          onEquipCosmetic = { itemId, category -> viewModel.equipCosmetic(itemId, category) },
          onSendXpBoost = { friendId -> viewModel.sendXpBoostToFriend(friendId) },
          onRestoreBrokenStreak = { viewModel.restoreBrokenStreak() },
          onFollowFriend = { friendId, follow -> viewModel.followOrUnfollowFriend(friendId, follow) },
          onTickLeaderboard = { viewModel.tickLeaderboardScores() },
          onClaimWeeklyDailyReward = { dayIndex -> viewModel.claimDailyReward(dayIndex) }
        )

        MainTab.PROJECTS -> ProjectsScreen(
          projects = projects,
          onOpenProject = { project ->
            activeProjectId = project.id
          }
        )

        MainTab.PROFILE -> ProfileScreen(
          user = user,
          achievements = achievements,
          skills = skills,
          onUpgradeAccount = { email, username ->
            viewModel.upgradeAccount(email, username)
          },
          unlockedCosmetics = unlockedCosmetics
        )
      }

      // Code Coach Bottom Sheet overlay
      if (showCoachSheet && activeCoachContext != null) {
        com.example.ui.components.ai.CodeCoachSheet(
          aiService = viewModel.aiService,
          context = activeCoachContext!!,
          workspaceFiles = activeCoachWorkspaceFiles,
          onDismiss = { viewModel.closeCodeCoach() }
        )
      }

      // Top Floating Toast for Rewards
      RewardToast(
        reward = activeReward,
        onDismiss = { viewModel.dismissReward() },
        modifier = Modifier.align(Alignment.TopCenter)
      )
    }
  }
}
