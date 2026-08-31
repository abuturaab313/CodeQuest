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
import androidx.compose.runtime.CompositionLocalProvider
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
import com.example.ui.audio.LocalSoundManager
import com.example.ui.components.GameHudBar
import com.example.ui.components.LevelUpDialog
import com.example.ui.components.QuickSettingsDialog
import com.example.ui.components.RewardToast
import com.example.ui.components.ai.CodeCoachSheet
import com.example.ui.screens.CodeLabScreen
import com.example.ui.screens.lesson.LessonScreen
import com.example.ui.screens.main.LearnScreen
import com.example.ui.screens.main.PracticeScreen
import com.example.ui.screens.main.ProfileScreen
import com.example.ui.screens.main.ProjectsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.project.ProjectWorkspaceScreen
import com.example.ui.screens.devlab.DeveloperLabScreen
import com.example.ui.screens.devlab.BugHuntScreen
import com.example.ui.screens.devlab.GitLabScreen
import com.example.ui.screens.devlab.TestFirstScreen
import com.example.ui.screens.devlab.CodeReviewScreen
import com.example.ui.screens.devlab.ReadmeBuilderScreen
import com.example.ui.screens.devlab.PortfolioScreen
import com.example.data.models.BugHuntEntity
import com.example.data.models.GitExerciseEntity
import com.example.data.models.TestFirstChallengeEntity
import com.example.data.models.CodeReviewEntity
import com.example.data.models.PortfolioItemEntity
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestPrimaryContainer
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
  MAP("Quest Map", Icons.Default.Explore, "nav_tab_map"),
  PRACTICE("Practice", Icons.Default.AutoAwesome, "nav_tab_practice"),
  PROJECTS("Dev Lab", Icons.Default.Science, "nav_tab_projects"),
  PROFILE("Profile", Icons.Default.Person, "nav_tab_profile")
}

@Composable
fun CodeQuestApp(
  viewModel: MainViewModel = viewModel()
) {
  val soundManager = viewModel.soundManagerInstance

  CompositionLocalProvider(LocalSoundManager provides soundManager) {
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

  // Milestone 11 DevLab flows
  val bugHunts by viewModel.bugHunts.collectAsStateWithLifecycle()
  val testFirstChallenges by viewModel.testFirstChallenges.collectAsStateWithLifecycle()
  val gitExercises by viewModel.gitExercises.collectAsStateWithLifecycle()
  val codeReviews by viewModel.codeReviews.collectAsStateWithLifecycle()
  val portfolioItems by viewModel.portfolioItems.collectAsStateWithLifecycle()
  val developerStats by viewModel.developerStats.collectAsStateWithLifecycle()

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

  var selectedTab by remember { mutableStateOf(MainTab.MAP) }
  var activeLesson by remember { mutableStateOf<LessonEntity?>(null) }
  var activeExercises by remember { mutableStateOf<List<ExerciseEntity>>(emptyList()) }
  var activeLessonProgress by remember { mutableStateOf<com.example.data.models.LessonProgressEntity?>(null) }
  var activeChallengeId by remember { mutableStateOf<String?>(null) }
  var activeProjectId by remember { mutableStateOf<String?>(null) }
  var showCreateProjectScreen by remember { mutableStateOf(false) }

  // Milestone 11 Active Navigation States
  var activeBugHunt by remember { mutableStateOf<BugHuntEntity?>(null) }
  var activeGitExercise by remember { mutableStateOf<GitExerciseEntity?>(null) }
  var activeTestFirstChallenge by remember { mutableStateOf<TestFirstChallengeEntity?>(null) }
  var activeCodeReview by remember { mutableStateOf<CodeReviewEntity?>(null) }
  var showReadmeBuilder by remember { mutableStateOf(false) }
  var readmeTargetProjectName by remember { mutableStateOf("My Real-World Project") }
  var showPortfolioScreen by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()

  // Bug Hunt Screen
  if (activeBugHunt != null) {
    BugHuntScreen(
      bugHunt = activeBugHunt!!,
      onNavigateBack = { activeBugHunt = null },
      onComplete = { xp, coins ->
        viewModel.completeBugHunt(activeBugHunt!!.id, xp, coins)
        activeBugHunt = null
      }
    )
    return@CompositionLocalProvider
  }

  // Git Lab Screen
  if (activeGitExercise != null) {
    GitLabScreen(
      exercise = activeGitExercise!!,
      onNavigateBack = { activeGitExercise = null },
      onComplete = { xp, coins ->
        val ex = activeGitExercise!!
        viewModel.completeGitExercise(
          id = ex.id,
          xp = xp,
          coins = coins,
          isCommit = ex.expectedAction == "COMMIT" || ex.expectedAction == "BOSS",
          isBranch = ex.expectedAction == "BRANCH" || ex.expectedAction == "BOSS",
          isConflict = ex.expectedAction == "RESOLVE_CONFLICT" || ex.expectedAction == "BOSS"
        )
        activeGitExercise = null
      }
    )
    return@CompositionLocalProvider
  }

  // Test First Screen
  if (activeTestFirstChallenge != null) {
    TestFirstScreen(
      challenge = activeTestFirstChallenge!!,
      onNavigateBack = { activeTestFirstChallenge = null },
      onComplete = { xp, coins ->
        viewModel.completeTestFirst(activeTestFirstChallenge!!.id, xp, coins)
        activeTestFirstChallenge = null
      }
    )
    return@CompositionLocalProvider
  }

  // Code Review Screen
  if (activeCodeReview != null) {
    CodeReviewScreen(
      review = activeCodeReview!!,
      onNavigateBack = { activeCodeReview = null },
      onComplete = { xp, coins ->
        viewModel.completeCodeReview(activeCodeReview!!.id, activeCodeReview!!.isRefactorChallenge, xp, coins)
        activeCodeReview = null
      }
    )
    return@CompositionLocalProvider
  }

  // README Builder Screen
  if (showReadmeBuilder) {
    ReadmeBuilderScreen(
      initialProjectName = readmeTargetProjectName,
      onNavigateBack = { showReadmeBuilder = false },
      onSaveReadme = { _ ->
        showReadmeBuilder = false
      }
    )
    return@CompositionLocalProvider
  }

  // Portfolio Screen
  if (showPortfolioScreen) {
    PortfolioScreen(
      portfolioItems = portfolioItems,
      onToggleVisibility = { id, isPublic ->
        viewModel.updatePortfolioVisibility(id, isPublic)
      },
      onOpenReadmeBuilder = { item ->
        readmeTargetProjectName = item.title
        showReadmeBuilder = true
      },
      onNavigateBack = { showPortfolioScreen = false }
    )
    return@CompositionLocalProvider
  }

  // Create Project Screen
  if (showCreateProjectScreen) {
    com.example.ui.screens.project.CreateProjectScreen(
      onNavigateBack = { showCreateProjectScreen = false },
      onCreateProject = { title, desc, lang, template, diff, readme ->
        viewModel.createCustomProject(title, desc, lang, template, diff, readme) { newProject ->
          showCreateProjectScreen = false
          activeProjectId = newProject.id
        }
      }
    )
    return@CompositionLocalProvider
  }

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
      },
      onResetOnboarding = {
        viewModel.resetOnboarding()
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
      onComplete = { experience, goal, path ->
        viewModel.completeOnboarding(experience, goal, path)
      }
    )
    return@CompositionLocalProvider
  }

  // If an active Project Workspace is opened, show ProjectWorkspaceScreen
  if (activeProjectId != null) {
    ProjectWorkspaceScreen(
      projectId = activeProjectId!!,
      viewModel = viewModel,
      onNavigateBack = { activeProjectId = null }
    )
    return@CompositionLocalProvider
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
    return@CompositionLocalProvider
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
        activeLesson?.let { currentLessonItem ->
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
          viewModel.playSuccessSound()
        }
      },
      onDeductHeart = {
        viewModel.deductHeart()
      },
      onRecordMistake = { exercise, wrongAnswer ->
        viewModel.recordMistake(exercise, wrongAnswer)
      },
      onCorrectAnswer = { viewModel.playCorrectSound() },
      onWrongAnswer = { viewModel.playWrongSound() },
      onPlayTap = { viewModel.playTapSound() },
      onSaveProgress = { progress ->
        viewModel.saveLessonProgress(progress)
      }
    )
    return@CompositionLocalProvider
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
          viewModel.playTapSound()
          if ((user?.currentHearts ?: 0) < 5) {
            viewModel.restoreHearts()
          }
        },
        onStreakClick = {
          viewModel.playTapSound()
          selectedTab = MainTab.MAP
        },
        onCoinsClick = {
          viewModel.playTapSound()
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
            onClick = { 
              selectedTab = tab 
              viewModel.playTapSound()
            },
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
        MainTab.MAP -> LearnScreen(
          user = user,
          playerProgress = playerProgress,
          dailyQuests = dailyQuests,
          onClaimQuest = { quest ->
            viewModel.claimQuest(quest)
          },
          onOpenSettings = {
            viewModel.openSettings()
          },
          onNavigateToProfile = {
            selectedTab = MainTab.PROFILE
          },
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

        MainTab.PROJECTS -> DeveloperLabScreen(
          bugHunts = bugHunts,
          testFirstChallenges = testFirstChallenges,
          gitExercises = gitExercises,
          codeReviews = codeReviews,
          projects = projects,
          portfolioItems = portfolioItems,
          stats = developerStats,
          onOpenBugHunt = { hunt -> activeBugHunt = hunt },
          onOpenTestFirst = { challenge -> activeTestFirstChallenge = challenge },
          onOpenGitLab = { ex -> activeGitExercise = ex },
          onOpenCodeReview = { review -> activeCodeReview = review },
          onOpenProject = { project -> activeProjectId = project.id },
          onCreateNewProject = { showCreateProjectScreen = true },
          onOpenPortfolio = { showPortfolioScreen = true },
          onOpenReadmeBuilder = { project ->
            readmeTargetProjectName = project?.title ?: "My Real-World Project"
            showReadmeBuilder = true
          }
        )

        MainTab.PROFILE -> ProfileScreen(
          user = user,
          achievements = achievements,
          skills = skills,
          onUpgradeAccount = { email, username ->
            viewModel.upgradeAccount(email, username)
          },
          unlockedCosmetics = unlockedCosmetics,
          developerStats = developerStats
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
}
