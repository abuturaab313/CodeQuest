package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CodeQuestDatabase
import com.example.data.models.AchievementEntity
import com.example.data.models.ChallengeProgressEntity
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.DailyPracticePlan
import com.example.data.models.DailyQuestEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.LearnerMemoryEntity
import com.example.data.models.LessonEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.SubmissionRecordEntity
import com.example.data.models.UserEntity
import com.example.data.models.WorldEntity
import com.example.data.models.EventEntity
import com.example.data.models.FriendEntity
import com.example.data.models.UnlockedCosmeticEntity
import com.example.data.models.LeaderboardCompetitorEntity
import com.example.data.models.DailyRewardClaimEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.CodeQuestRepository
import com.example.data.repository.LessonCompletionResult
import com.example.domain.ai.models.LearningContext
import com.example.domain.execution.ExecutionResult
import com.example.domain.execution.TestSuiteResult
import com.example.domain.services.GamificationReward
import com.example.domain.services.LevelUpResult
import com.example.domain.services.PlayerProgress
import com.example.domain.services.SubmissionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = CodeQuestDatabase.getDatabase(application)
  val repository = CodeQuestRepository(database)
  val projectRepository = com.example.data.repository.ProjectRepository(database, repository)
  val devLabRepository = com.example.data.repository.DevLabRepository(database.devLabDao())
  private val authRepository = AuthRepository(database.userDao())
  val aiService get() = repository.aiService

  // Developer Lab StateFlows
  val bugHunts = devLabRepository.getAllBugHunts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val testFirstChallenges = devLabRepository.getAllTestFirstChallenges()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val gitExercises = devLabRepository.getAllGitExercises()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val codeReviews = devLabRepository.getAllCodeReviews()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val portfolioItems = devLabRepository.getAllPortfolioItems()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val developerStats = devLabRepository.getDeveloperStats()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  init {
    viewModelScope.launch {
      repository.checkAndInitializeData()
      // Initialize default learner memory seeds if empty
      initializeLearnerMemory()
    }
  }

  private suspend fun initializeLearnerMemory() {
    val existing = database.learnerMemoryDao().getAllMemories("user_default")
    if (existing.isEmpty()) {
      val seeds = listOf(
        LearnerMemoryEntity(id = "user_default_VARIABLES", userId = "user_default", conceptKey = "VARIABLES", conceptTitle = "Variables & Types", masteryScore = 80, totalAttempts = 4, successfulAttempts = 4),
        LearnerMemoryEntity(id = "user_default_CONDITIONS", userId = "user_default", conceptKey = "CONDITIONS", conceptTitle = "Conditionals (if/else)", masteryScore = 65, totalAttempts = 5, successfulAttempts = 3),
        LearnerMemoryEntity(id = "user_default_LOOPS", userId = "user_default", conceptKey = "LOOPS", conceptTitle = "Loops & Iteration", masteryScore = 45, totalAttempts = 6, successfulAttempts = 2, failedAttempts = 4, recentMistakesJson = "[\"Off-by-one index error\",\"Infinite loop condition\"]"),
        LearnerMemoryEntity(id = "user_default_FUNCTIONS", userId = "user_default", conceptKey = "FUNCTIONS", conceptTitle = "Functions & Scope", masteryScore = 70, totalAttempts = 4, successfulAttempts = 3)
      )
      database.learnerMemoryDao().upsertMemories(seeds)
    }
  }

  val userProfile: StateFlow<UserEntity?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val dailyQuests: StateFlow<List<DailyQuestEntity>> = repository.dailyQuests
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val achievements: StateFlow<List<AchievementEntity>> = repository.achievements
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val worlds: StateFlow<List<WorldEntity>> = repository.allWorlds
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val lessons: StateFlow<List<LessonEntity>> = repository.allLessons
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val challenges: StateFlow<List<CodingChallengeEntity>> = repository.challenges
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val skills: StateFlow<List<SkillMasteryEntity>> = repository.getSkillsForLanguage("python")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val projects: StateFlow<List<ProjectEntity>> = projectRepository.getAllProjects()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unresolvedMistakes: StateFlow<List<com.example.data.models.UserMistakeEntity>> = repository.unresolvedMistakes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val learnerMemories: StateFlow<List<LearnerMemoryEntity>> = database.learnerMemoryDao()
    .observeAllMemories("user_default")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Milestone 7 state flows
  val weeklyEvents: StateFlow<List<EventEntity>> = repository.weeklyEvents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val friends: StateFlow<List<FriendEntity>> = repository.friends
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unlockedCosmetics: StateFlow<List<UnlockedCosmeticEntity>> = repository.unlockedCosmetics
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val dailyRewardClaims: StateFlow<List<DailyRewardClaimEntity>> = repository.dailyRewardClaims
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val competitorsBronze: StateFlow<List<LeaderboardCompetitorEntity>> = repository.getCompetitorsForLeague("Bronze")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val competitorsSilver: StateFlow<List<LeaderboardCompetitorEntity>> = repository.getCompetitorsForLeague("Silver")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val competitorsGold: StateFlow<List<LeaderboardCompetitorEntity>> = repository.getCompetitorsForLeague("Gold")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val competitorsCrystal: StateFlow<List<LeaderboardCompetitorEntity>> = repository.getCompetitorsForLeague("Crystal")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val competitorsObsidian: StateFlow<List<LeaderboardCompetitorEntity>> = repository.getCompetitorsForLeague("Obsidian")
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Bookmarks, Notes, Spaced repetition flows
  val bookmarks: StateFlow<List<com.example.data.models.BookmarkEntity>> = repository.observeAllBookmarks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notes: StateFlow<List<com.example.data.models.LessonNoteEntity>> = repository.observeAllNotes()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val reviewQueue: StateFlow<List<com.example.data.models.ReviewQueueEntity>> = repository.observeReviewQueue()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Unified Reactive Player Progress
  val playerProgress: StateFlow<PlayerProgress?> = combine(
    userProfile,
    lessons,
    dailyQuests
  ) { user, lessonList, questList ->
    if (user != null) {
      repository.progressionService.buildPlayerProgress(user, lessonList, questList)
    } else {
      null
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Adaptive Recommendations Flow
  val recommendations: StateFlow<List<com.example.data.models.LearningRecommendation>> = combine(
    learnerMemories,
    lessons,
    challenges,
    projects
  ) { memories, lessonList, chalList, projList ->
    val unresolved = listOf<com.example.data.models.UserMistakeEntity>()
    val skills = listOf<com.example.data.models.SkillMasteryEntity>() // Ideally fetch real skills
    repository.recommendationEngine.generateRecommendations(
      unlockedLessons = lessonList.filter { it.isUnlocked },
      completedLessons = lessonList.filter { it.isCompleted },
      challenges = chalList,
      projects = projList,
      unresolvedMistakes = unresolved,
      skills = skills
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Daily Practice Flow
  private val _dailyPracticeCount = MutableStateFlow(1) // progress counter
  val dailyPracticeState: StateFlow<Pair<com.example.data.models.DailyPracticeSessionEntity, List<com.example.domain.learning.PracticeStep>>?> = combine(
    learnerMemories,
    _dailyPracticeCount
  ) { memories, count ->
    val weak = listOf<com.example.data.models.SkillMasteryEntity>() // Fetch real weak skills if needed
    val session = repository.personalizedPracticeService.generateDailySession(
      weakSkills = weak,
      mistakes = emptyList(),
      challenges = emptyList()
    )
    val steps = repository.personalizedPracticeService.parseSteps(session)
    Pair(session, steps)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    null
  )

  // Code Coach Active Modal State
  private val _showCoachSheet = MutableStateFlow(false)
  val showCoachSheet: StateFlow<Boolean> = _showCoachSheet.asStateFlow()

  private val _activeCoachContext = MutableStateFlow<LearningContext?>(null)
  val activeCoachContext: StateFlow<LearningContext?> = _activeCoachContext.asStateFlow()

  private val _activeCoachWorkspaceFiles = MutableStateFlow<Map<String, String>>(emptyMap())
  val activeCoachWorkspaceFiles: StateFlow<Map<String, String>> = _activeCoachWorkspaceFiles.asStateFlow()

  fun openCodeCoach(context: LearningContext, workspaceFiles: Map<String, String> = emptyMap()) {
    _activeCoachContext.value = context
    _activeCoachWorkspaceFiles.value = workspaceFiles
    _showCoachSheet.value = true
  }

  fun closeCodeCoach() {
    _showCoachSheet.value = false
    aiService.resetState()
  }

  fun advanceDailyPracticeStep() {
    val current = _dailyPracticeCount.value
    if (current < 4) {
      _dailyPracticeCount.value = current + 1
    }
  }

  fun claimDailyPracticeReward() {
    viewModelScope.launch {
      dailyPracticeState.value?.let { (plan, _) ->
        val levelUpResult = repository.awardBonusXpAndCoins(plan.xpReward, plan.coinReward)
        if (levelUpResult != null && levelUpResult.didLevelUp) {
          activeLevelUp.value = levelUpResult
        } else {
          activeReward.value = GamificationReward.XpReward(plan.xpReward, "Daily Practice Completed!")
        }
      }
    }
  }

  // Feedback & Modal States
  val activeLevelUp = MutableStateFlow<LevelUpResult?>(null)
  val activeReward = MutableStateFlow<GamificationReward?>(null)
  val showSettingsDialog = MutableStateFlow(false)

  fun openSettings() {
    showSettingsDialog.value = true
  }

  fun closeSettings() {
    showSettingsDialog.value = false
  }

  fun dismissLevelUp() {
    activeLevelUp.value = null
  }

  fun dismissReward() {
    activeReward.value = null
  }

  fun completeOnboarding(experience: String, language: String, dailyGoal: Int) {
    viewModelScope.launch {
      repository.completeOnboarding(experience, language, dailyGoal)
    }
  }

  fun completeLesson(
    lessonId: String,
    totalExercises: Int,
    correctCount: Int,
    mistakeCount: Int,
    hintsUsedCount: Int,
    baseXp: Int,
    baseCoins: Int,
    nextLessonId: String? = null,
    onResult: (LessonCompletionResult) -> Unit = {}
  ) {
    viewModelScope.launch {
      val result = repository.completeLesson(
        lessonId = lessonId,
        totalExercises = totalExercises,
        correctCount = correctCount,
        mistakeCount = mistakeCount,
        hintsUsedCount = hintsUsedCount,
        baseXp = baseXp,
        baseCoins = baseCoins,
        nextLessonId = nextLessonId
      )
      if (result.levelUpResult.didLevelUp) {
        activeLevelUp.value = result.levelUpResult
      } else {
        activeReward.value = GamificationReward.XpReward(result.xpGained, "Lesson Cleared!")
      }
      onResult(result)
    }
  }

  suspend fun getLessonProgress(lessonId: String): com.example.data.models.LessonProgressEntity? {
    return repository.getLessonProgress(lessonId)
  }

  fun saveLessonProgress(progress: com.example.data.models.LessonProgressEntity) {
    viewModelScope.launch {
      repository.saveLessonProgress(progress)
    }
  }

  fun recordMistake(exercise: ExerciseEntity, wrongAnswer: String) {
    viewModelScope.launch {
      repository.recordMistake(exercise, wrongAnswer)
    }
  }

  fun resolveMistake(mistakeId: String) {
    viewModelScope.launch {
      repository.resolveMistake(mistakeId)
    }
  }

  fun toggleBookmark(lessonId: String, lessonTitle: String, worldId: String) {
    viewModelScope.launch {
      repository.toggleBookmark(lessonId, lessonTitle, worldId)
    }
  }

  fun saveNoteForLesson(lessonId: String, lessonTitle: String, text: String) {
    viewModelScope.launch {
      repository.saveNoteForLesson(lessonId, lessonTitle, text)
    }
  }

  fun deleteNoteForLesson(lessonId: String) {
    viewModelScope.launch {
      repository.deleteNoteForLesson(lessonId)
    }
  }

  fun scheduleSpacedReview(conceptId: String, conceptName: String, performanceScore: Int) {
    viewModelScope.launch {
      repository.scheduleSpacedReview(conceptId, conceptName, performanceScore)
    }
  }

  fun deleteReviewItem(conceptId: String) {
    viewModelScope.launch {
      repository.deleteReviewItem(conceptId)
    }
  }

  suspend fun getExercisesForLesson(lessonId: String): List<ExerciseEntity> {
    return repository.getExercisesForLessonOnce(lessonId)
  }

  fun claimQuest(quest: DailyQuestEntity) {
    viewModelScope.launch {
      val levelUpResult = repository.claimQuest(quest)
      if (levelUpResult != null && levelUpResult.didLevelUp) {
        activeLevelUp.value = levelUpResult
      } else {
        activeReward.value = GamificationReward.CoinReward(quest.coinReward, "Quest: ${quest.title}")
      }
    }
  }

  // Coding Challenges & Code Lab
  suspend fun getChallengeById(id: String): CodingChallengeEntity? {
    return repository.getChallengeById(id)
  }

  suspend fun getChallengeByLessonId(lessonId: String): CodingChallengeEntity? {
    return repository.getChallengeByLessonId(lessonId)
  }

  suspend fun getChallengeProgress(challengeId: String): ChallengeProgressEntity? {
    return repository.getChallengeProgress(challengeId)
  }

  fun observeChallengeProgress(challengeId: String): Flow<ChallengeProgressEntity?> {
    return repository.observeChallengeProgress(challengeId)
  }

  fun saveChallengeDraft(challengeId: String, code: String, hintsUsedCount: Int = 0) {
    viewModelScope.launch {
      repository.saveChallengeDraft(challengeId, code, hintsUsedCount)
    }
  }

  fun getSubmissionsForChallenge(challengeId: String): Flow<List<SubmissionRecordEntity>> {
    return repository.getSubmissionsForChallenge(challengeId)
  }

  suspend fun executeUserCode(code: String, languageId: String, rawInput: String = ""): ExecutionResult {
    return repository.executeUserCode(code, languageId, rawInput)
  }

  suspend fun runPublicTests(challenge: CodingChallengeEntity, code: String, languageId: String? = null): TestSuiteResult {
    return repository.runPublicTests(challenge, code, languageId)
  }

  suspend fun submitChallenge(
    challenge: CodingChallengeEntity,
    code: String,
    hintsUsedCount: Int,
    languageId: String? = null
  ): SubmissionResult {
    val (result, levelUpResult) = repository.submitChallenge(challenge, code, hintsUsedCount, languageId)
    if (levelUpResult != null && levelUpResult.didLevelUp) {
      activeLevelUp.value = levelUpResult
    } else if (result.isPassed) {
      activeReward.value = GamificationReward.XpReward(result.xpAwarded, "Challenge: ${challenge.title}")
    }
    return result
  }

  fun upgradeAccount(email: String, username: String) {
    viewModelScope.launch {
      authRepository.upgradeAccount(email, username)
    }
  }

  fun deductHeart() {
    viewModelScope.launch {
      repository.deductHeart()
    }
  }

  fun restoreHearts() {
    viewModelScope.launch {
      repository.restoreHearts(5)
    }
  }

  fun createCustomProject(
    title: String,
    description: String,
    language: String,
    template: String,
    difficulty: String,
    readmeContent: String,
    onSuccess: (ProjectEntity) -> Unit
  ) {
    viewModelScope.launch {
      val project = projectRepository.createCustomProject(
        title = title,
        description = description,
        language = language,
        template = template,
        difficulty = difficulty,
        readmeContent = readmeContent
      )
      onSuccess(project)
    }
  }

  fun updateSettings(sound: Boolean, haptics: Boolean, dark: Boolean, reducedMotion: Boolean) {
    viewModelScope.launch {
      repository.updateSettings(sound, haptics, dark, reducedMotion)
    }
  }

  // Milestone 7 operations
  fun claimDailyReward(dayIndex: Int, onComplete: (LevelUpResult?) -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.claimDailyReward(dayIndex)
      if (res != null && res.didLevelUp) {
        activeLevelUp.value = res
      }
      onComplete(res)
    }
  }

  fun claimEventReward(eventId: String, onComplete: (LevelUpResult?) -> Unit = {}) {
    viewModelScope.launch {
      val res = repository.claimEventReward(eventId)
      if (res != null && res.didLevelUp) {
        activeLevelUp.value = res
      }
      onComplete(res)
    }
  }

  fun purchaseCosmetic(itemId: String, costCoins: Int, category: String, onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      val success = repository.purchaseCosmetic(itemId, costCoins, category)
      onComplete(success)
    }
  }

  fun equipCosmetic(itemId: String, category: String, onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      val success = repository.equipCosmetic(itemId, category)
      onComplete(success)
    }
  }

  fun sendXpBoostToFriend(friendId: String, onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      val success = repository.sendXpBoostToFriend(friendId)
      onComplete(success)
    }
  }

  fun restoreBrokenStreak(onComplete: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      val success = repository.restoreBrokenStreak()
      onComplete(success)
    }
  }

  fun followOrUnfollowFriend(friendId: String, follow: Boolean) {
    viewModelScope.launch {
      repository.followOrUnfollowFriend(friendId, follow)
    }
  }

  fun tickLeaderboardScores() {
    viewModelScope.launch {
      repository.tickLeaderboardScores()
    }
  }

  fun resetAllProgress() {
    viewModelScope.launch {
      repository.resetAllProgress()
    }
  }

  // DevLab Completion Methods
  fun completeBugHunt(id: String, xp: Int, coins: Int) {
    viewModelScope.launch {
      devLabRepository.completeBugHunt(id)
      val levelUpResult = repository.awardBonusXpAndCoins(xp, coins)
      if (levelUpResult != null && levelUpResult.didLevelUp) {
        activeLevelUp.value = levelUpResult
      } else {
        activeReward.value = GamificationReward.XpReward(xp, "Bug Fixed!")
      }
    }
  }

  fun completeTestFirst(id: String, xp: Int, coins: Int) {
    viewModelScope.launch {
      devLabRepository.completeTestFirst(id)
      val levelUpResult = repository.awardBonusXpAndCoins(xp, coins)
      if (levelUpResult != null && levelUpResult.didLevelUp) {
        activeLevelUp.value = levelUpResult
      } else {
        activeReward.value = GamificationReward.XpReward(xp, "TDD Challenge Complete!")
      }
    }
  }

  fun completeGitExercise(id: String, xp: Int, coins: Int, isCommit: Boolean = false, isBranch: Boolean = false, isConflict: Boolean = false) {
    viewModelScope.launch {
      devLabRepository.completeGitExercise(id, isCommit, isBranch, isConflict)
      val levelUpResult = repository.awardBonusXpAndCoins(xp, coins)
      if (levelUpResult != null && levelUpResult.didLevelUp) {
        activeLevelUp.value = levelUpResult
      } else {
        activeReward.value = GamificationReward.XpReward(xp, "Git Exercise Complete!")
      }
    }
  }

  fun completeCodeReview(id: String, isRefactor: Boolean, xp: Int, coins: Int) {
    viewModelScope.launch {
      devLabRepository.completeCodeReview(id, isRefactor)
      val levelUpResult = repository.awardBonusXpAndCoins(xp, coins)
      if (levelUpResult != null && levelUpResult.didLevelUp) {
        activeLevelUp.value = levelUpResult
      } else {
        activeReward.value = GamificationReward.XpReward(xp, if (isRefactor) "Refactor Complete!" else "Code Review Approved!")
      }
    }
  }

  fun savePortfolioItem(item: com.example.data.models.PortfolioItemEntity) {
    viewModelScope.launch {
      devLabRepository.savePortfolioItem(item)
    }
  }

  fun updatePortfolioVisibility(id: String, isPublic: Boolean) {
    viewModelScope.launch {
      devLabRepository.updatePortfolioVisibility(id, isPublic)
    }
  }

  fun toggleProjectPortfolio(projectId: String, isPortfolio: Boolean) {
    viewModelScope.launch {
      projectRepository.toggleProjectPortfolio(projectId, isPortfolio)
    }
  }

  fun commitProjectVersion(projectId: String, message: String, onComplete: () -> Unit) {
    viewModelScope.launch {
      projectRepository.commitProjectVersion(projectId, message)
      onComplete()
    }
  }

  suspend fun getProjectVersions(projectId: String) = projectRepository.getProjectVersions(projectId)

  fun restoreProjectVersion(projectId: String, versionNumber: Int, onComplete: () -> Unit) {
    viewModelScope.launch {
      projectRepository.restoreProjectVersion(projectId, versionNumber)
      onComplete()
    }
  }

  fun updatePortfolioReadme(id: String, readme: String) {
    viewModelScope.launch {
      devLabRepository.updatePortfolioReadme(id, readme)
    }
  }
}
