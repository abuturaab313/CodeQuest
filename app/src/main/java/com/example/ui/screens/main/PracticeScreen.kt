package com.example.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.domain.learning.PracticeStep
import com.example.domain.ai.models.AIMentorMode
import com.example.domain.ai.models.LearningContext
import com.example.ui.components.GameCard
import com.example.ui.theme.*

@Composable
fun PracticeScreen(
  onStartPractice: (String) -> Unit,
  onOpenCodingLab: (String) -> Unit = {},
  challenges: List<CodingChallengeEntity> = emptyList(),
  unresolvedMistakes: List<UserMistakeEntity> = emptyList(),
  learnerMemories: List<LearnerMemoryEntity> = emptyList(),
  dailyPracticeState: Pair<DailyPracticeSessionEntity, List<PracticeStep>>? = null,
  onOpenCodeCoach: (LearningContext) -> Unit = {},
  onClaimDailyReward: () -> Unit = {},
  onAdvanceDailyStep: () -> Unit = {},
  modifier: Modifier = Modifier,
  
  // Milestone 7 advanced capabilities
  weeklyEvents: List<EventEntity> = emptyList(),
  friends: List<FriendEntity> = emptyList(),
  unlockedCosmetics: List<UnlockedCosmeticEntity> = emptyList(),
  dailyRewardClaims: List<DailyRewardClaimEntity> = emptyList(),
  competitorsBronze: List<LeaderboardCompetitorEntity> = emptyList(),
  competitorsSilver: List<LeaderboardCompetitorEntity> = emptyList(),
  competitorsGold: List<LeaderboardCompetitorEntity> = emptyList(),
  competitorsCrystal: List<LeaderboardCompetitorEntity> = emptyList(),
  competitorsObsidian: List<LeaderboardCompetitorEntity> = emptyList(),
  userProfile: UserEntity? = null,
  onClaimEventReward: (String) -> Unit = {},
  onPurchaseCosmetic: (String, Int, String) -> Unit = { _, _, _ -> },
  onEquipCosmetic: (String, String) -> Unit = { _, _ -> },
  onSendXpBoost: (String) -> Unit = {},
  onRestoreBrokenStreak: () -> Unit = {},
  onFollowFriend: (String, Boolean) -> Unit = { _, _ -> },
  onTickLeaderboard: () -> Unit = {},
  onClaimWeeklyDailyReward: (Int) -> Unit = {}
) {
  var activeTab by remember { mutableStateOf(0) }
  
  Column(
    modifier = modifier
      .fillMaxSize()
  ) {
    // Practice Hub Top Tab Layout
    TabRow(
      selectedTabIndex = activeTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = QuestPrimary,
      modifier = Modifier.fillMaxWidth().testTag("practice_hub_tabs")
    ) {
      Tab(
        selected = activeTab == 0,
        onClick = { activeTab = 0 },
        text = { Text("Adaptive Lab", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
        icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_adaptive_lab").minimumInteractiveComponentSize()
      )
      Tab(
        selected = activeTab == 1,
        onClick = { activeTab = 1 },
        text = { Text("Weekly Events", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_weekly_events").minimumInteractiveComponentSize()
      )
      Tab(
        selected = activeTab == 2,
        onClick = { activeTab = 2 },
        text = { Text("Social Standings", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
        icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_social_standings").minimumInteractiveComponentSize()
      )
      Tab(
        selected = activeTab == 3,
        onClick = { activeTab = 3 },
        text = { Text("Code Shop", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
        icon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_code_shop").minimumInteractiveComponentSize()
      )
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      when (activeTab) {
        0 -> {
          AdaptiveLabTab(
            onStartPractice = onStartPractice,
            onOpenCodingLab = onOpenCodingLab,
            challenges = challenges,
            unresolvedMistakes = unresolvedMistakes,
            learnerMemories = learnerMemories,
            dailyPracticeState = dailyPracticeState,
            onOpenCodeCoach = onOpenCodeCoach,
            onClaimDailyReward = onClaimDailyReward,
            onAdvanceDailyStep = {
              onAdvanceDailyStep()
              onTickLeaderboard() // Tick leaderboard score bot emulation on learning events
            }
          )
        }
        1 -> {
          WeeklyEventsTab(
            weeklyEvents = weeklyEvents,
            dailyRewardClaims = dailyRewardClaims,
            onClaimEventReward = onClaimEventReward,
            onClaimDailyReward = onClaimWeeklyDailyReward,
            userProfile = userProfile,
            onExploreChallenges = { activeTab = 0 }
          )
        }
        2 -> {
          SocialStandingsTab(
            friends = friends,
            userProfile = userProfile,
            competitorsBronze = competitorsBronze,
            competitorsSilver = competitorsSilver,
            competitorsGold = competitorsGold,
            competitorsCrystal = competitorsCrystal,
            competitorsObsidian = competitorsObsidian,
            onSendXpBoost = onSendXpBoost,
            onFollowFriend = onFollowFriend
          )
        }
        3 -> {
          CodeShopTab(
            userProfile = userProfile,
            unlockedCosmetics = unlockedCosmetics,
            onPurchaseCosmetic = onPurchaseCosmetic,
            onEquipCosmetic = onEquipCosmetic,
            onRestoreBrokenStreak = onRestoreBrokenStreak
          )
        }
      }
    }
  }
}

// ==========================================
// TAB 0: ADAPTIVE LAB (Existing layout beautifully preserved)
// ==========================================
@Composable
fun AdaptiveLabTab(
  onStartPractice: (String) -> Unit,
  onOpenCodingLab: (String) -> Unit,
  challenges: List<CodingChallengeEntity>,
  unresolvedMistakes: List<UserMistakeEntity>,
  learnerMemories: List<LearnerMemoryEntity>,
  dailyPracticeState: Pair<DailyPracticeSessionEntity, List<PracticeStep>>?,
  onOpenCodeCoach: (LearningContext) -> Unit,
  onClaimDailyReward: () -> Unit,
  onAdvanceDailyStep: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf("ALL") }

  val categories = remember(challenges) {
    listOf("ALL") + challenges.map { it.category }.distinct()
  }

  val filteredChallenges = remember(challenges, selectedCategory) {
    if (selectedCategory == "ALL") challenges
    else challenges.filter { it.category == selectedCategory }
  }

  val weakSkills = remember(learnerMemories) {
    learnerMemories.filter { it.isWeakSkill() }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Practice & Adaptive Lab",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Sharpen your coding intuition with real interactive Python challenges and drills.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // AI Code Coach Quick Launcher Banner
    item {
      AICoachLauncherCard(
        onOpenCoach = { mode ->
          val context = LearningContext(
            sourceScreen = "PRACTICE",
            activeConcept = weakSkills.firstOrNull()?.conceptKey ?: "PYTHON_BASICS"
          )
          onOpenCodeCoach(context)
        }
      )
    }

    // Daily Practice Plan Card
    dailyPracticeState?.let { (plan, tasks) ->
      item {
        DailyPracticePlanCard(
          plan = plan,
          tasks = tasks,
          onStartTask = { task ->
            if (task.type == "CHALLENGE" && task.targetId != null) {
              onOpenCodingLab(task.targetId)
            } else if (task.targetId != null) {
              onStartPractice(task.targetId)
            }
            onAdvanceDailyStep()
          },
          onClaim = onClaimDailyReward
        )
      }
    }

    // Adaptive Mastery & Weak Skills Breakdown
    if (learnerMemories.isNotEmpty()) {
      item {
        AdaptiveMasterySection(
          memories = learnerMemories,
          onDrillSkill = { conceptKey ->
            onStartPractice("drill_$conceptKey")
          }
        )
      }
    }

    // Mistake Review Vault (Active if user made mistakes in previous lessons)
    if (unresolvedMistakes.isNotEmpty()) {
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          color = HeartRose.copy(alpha = 0.12f),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, HeartRose)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(HeartRose),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Replay, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "MISTAKE REVIEW VAULT",
                style = MaterialTheme.typography.labelSmall.copy(color = HeartRose, fontWeight = FontWeight.ExtraBold)
              )
              Text(
                text = "${unresolvedMistakes.size} Concept${if (unresolvedMistakes.size > 1) "s" else ""} To Review",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Re-try previous tricky questions to earn +50 XP and fix weak spots.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { onStartPractice("mistake_vault") },
              colors = ButtonDefaults.buttonColors(containerColor = HeartRose, contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("start_mistake_review_button").minimumInteractiveComponentSize()
            ) {
              Text("Review", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Interactive Coding Lab Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Terminal, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Coding Lab Challenges",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // Category Filter Chips
    if (categories.size > 1) {
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = {
                Text(
                  text = if (cat == "ALL") "All Categories" else cat.replace('_', ' '),
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = QuestPrimary.copy(alpha = 0.15f),
                selectedLabelColor = QuestPrimary
              ),
              modifier = Modifier.minimumInteractiveComponentSize()
            )
          }
        }
      }
    }

    // Coding Challenge Cards List
    items(filteredChallenges) { challenge ->
      CodingChallengeItemCard(
        challenge = challenge,
        onClick = { onOpenCodingLab(challenge.id) }
      )
    }

    // Classic Practice Modes
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Quick Drills & Minigames",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    item {
      PracticeModeCard(
        title = "Daily Quick Drill",
        description = "5 rapid-fire questions on syntax, output prediction, and operators.",
        xp = "+60 XP",
        difficulty = "EASY",
        icon = Icons.Default.AutoAwesome,
        accentColor = QuestPrimary,
        onClick = { onStartPractice("daily_drill") },
        testTag = "practice_daily_drill"
      )
    }

    item {
      PracticeModeCard(
        title = "Bug Hunting Arena",
        description = "Find and fix syntax errors, off-by-one loops, and type mismatches.",
        xp = "+100 XP",
        difficulty = "MEDIUM",
        icon = Icons.Default.BugReport,
        accentColor = HeartRose,
        onClick = {
          val debugChallenge = challenges.firstOrNull { it.category == "DEBUGGING" }
          if (debugChallenge != null) {
            onOpenCodingLab(debugChallenge.id)
          } else {
            onStartPractice("bug_hunting")
          }
        },
        testTag = "practice_bug_hunt"
      )
    }

    item {
      PracticeModeCard(
        title = "Speed Coding Challenge",
        description = "Write the solution before the 60-second timer runs out!",
        xp = "+120 XP",
        difficulty = "TIMED",
        icon = Icons.Default.Timer,
        accentColor = StreakFlame,
        onClick = {
          val mathChallenge = challenges.firstOrNull { it.category == "MATH" }
          if (mathChallenge != null) {
            onOpenCodingLab(mathChallenge.id)
          } else {
            onStartPractice("speed_code")
          }
        },
        testTag = "practice_speed"
      )
    }

    item {
      PracticeModeCard(
        title = "Algorithm Foundations",
        description = "Array searching, string reversal, and mathematical sequences.",
        xp = "+150 XP",
        difficulty = "HARD",
        icon = Icons.Default.Code,
        accentColor = QuestIndigo,
        onClick = {
          val advChallenge = challenges.firstOrNull { it.difficulty.uppercase() == "HARD" || it.difficulty.uppercase() == "ADVANCED" }
            ?: challenges.firstOrNull { it.category == "LISTS" }
          if (advChallenge != null) {
            onOpenCodingLab(advChallenge.id)
          } else {
            onStartPractice("algorithms")
          }
        },
        testTag = "practice_algorithms"
      )
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

// ==========================================
// TAB 1: WEEKLY EVENTS & REWARD CALENDAR
// ==========================================
@Composable
fun WeeklyEventsTab(
  weeklyEvents: List<EventEntity>,
  dailyRewardClaims: List<DailyRewardClaimEntity>,
  onClaimEventReward: (String) -> Unit,
  onClaimDailyReward: (Int) -> Unit,
  userProfile: UserEntity?,
  onExploreChallenges: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Weekly Events & Calendar",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Complete seasonal community events and claim daily reward bundles.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 7-day Daily Reward Calendar
    item {
      Text(
        text = "Daily Attendance Rewards",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    item {
      GameCard(borderColor = QuestGold.copy(alpha = 0.4f)) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "7-Day Code Calendar",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Claim progressive daily bonuses to keep your streak fired up!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          
          Spacer(modifier = Modifier.height(14.dp))
          
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            dailyRewardClaims.take(7).forEach { claim ->
              val isUnlocked = userProfile != null // Can claim if user is logged in
              
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 2.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    when {
                      claim.isClaimed -> QuestSuccess.copy(alpha = 0.15f)
                      else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                  )
                  .border(
                    width = 1.dp,
                    color = if (claim.isClaimed) QuestSuccess else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable(enabled = !claim.isClaimed) {
                    onClaimDailyReward(claim.dayIndex)
                  }
                  .padding(vertical = 10.dp)
              ) {
                Text(
                  text = "Day ${claim.dayIndex}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (claim.isClaimed) QuestSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Icon(
                  imageVector = if (claim.isClaimed) Icons.Default.CheckCircle else Icons.Default.Stars,
                  contentDescription = null,
                  tint = if (claim.isClaimed) QuestSuccess else XpGold,
                  modifier = Modifier.size(22.dp)
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                  text = if (claim.coinReward > 0) "${claim.coinReward}C" else "+${claim.xpReward}X",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                  color = if (claim.isClaimed) QuestSuccess else QuestPrimary
                )
              }
            }
          }
        }
      }
    }

    // Active Live Events List
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Stars, contentDescription = null, tint = QuestPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Active Coding Events",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    if (weeklyEvents.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No active weekly events right now. Check back tomorrow!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    } else {
      items(weeklyEvents) { event ->
        EventItemCard(
          event = event,
          onClaimReward = { onClaimEventReward(event.id) },
          onAction = onExploreChallenges
        )
      }
    }
    
    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun EventItemCard(
  event: EventEntity,
  onClaimReward: () -> Unit,
  onAction: () -> Unit
) {
  val progressFraction = if (event.targetProgress > 0) event.currentProgress.toFloat() / event.targetProgress else 0f
  
  GameCard(
    borderColor = if (event.isCompleted) QuestSuccess.copy(alpha = 0.5f) else QuestPrimary.copy(alpha = 0.3f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("event_card_${event.id}")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (event.isCompleted) QuestSuccess.copy(alpha = 0.12f) else QuestPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (event.isCompleted) Icons.Default.WorkspacePremium else Icons.Default.Stars,
              contentDescription = null,
              tint = if (event.isCompleted) QuestSuccess else QuestPrimary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (event.isCompleted) QuestSuccess.copy(alpha = 0.15f) else QuestPrimary.copy(alpha = 0.15f)
            ) {
              Text(
                text = if (event.isCompleted) "COMPLETED" else "LIVE EVENT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = if (event.isCompleted) QuestSuccess else QuestPrimary,
                  fontSize = 9.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = event.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
        
        Text(
          text = "Ends in 3d",
          style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
      
      Spacer(modifier = Modifier.height(10.dp))
      
      Text(
        text = event.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      // Progress details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Progress: ${event.currentProgress} / ${event.targetProgress}",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${(progressFraction * 100).toInt()}%",
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = if (event.isCompleted) QuestSuccess else QuestPrimary
        )
      }
      
      Spacer(modifier = Modifier.height(6.dp))
      
      LinearProgressIndicator(
        progress = { progressFraction.coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = if (event.isCompleted) QuestSuccess else QuestPrimary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "EVENT REWARDS:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "+${event.xpReward} XP", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.width(10.dp))
            Icon(Icons.Default.Storefront, contentDescription = null, tint = QuestGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "+${event.coinReward} Coins", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
          }
        }
        
        if (event.isClaimed) {
          Button(
            onClick = {},
            enabled = false,
            colors = ButtonDefaults.buttonColors(disabledContainerColor = QuestSuccess.copy(alpha = 0.2f), disabledContentColor = QuestSuccess),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Claimed", fontWeight = FontWeight.Bold)
          }
        } else if (event.isCompleted) {
          Button(
            onClick = onClaimReward,
            colors = ButtonDefaults.buttonColors(containerColor = QuestSuccess, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("claim_event_reward_${event.id}").minimumInteractiveComponentSize()
          ) {
            Text("Claim Rewards", fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
          ) {
            Text("Solve Challenges", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ==========================================
// TAB 2: SOCIAL STANDINGS & LEAGUE LEADERBOARDS
// ==========================================
@Composable
fun SocialStandingsTab(
  friends: List<FriendEntity>,
  userProfile: UserEntity?,
  competitorsBronze: List<LeaderboardCompetitorEntity>,
  competitorsSilver: List<LeaderboardCompetitorEntity>,
  competitorsGold: List<LeaderboardCompetitorEntity>,
  competitorsCrystal: List<LeaderboardCompetitorEntity>,
  competitorsObsidian: List<LeaderboardCompetitorEntity>,
  onSendXpBoost: (String) -> Unit,
  onFollowFriend: (String, Boolean) -> Unit
) {
  var activeSubTab by remember { mutableStateOf(0) } // 0 = Leagues, 1 = Coder Friends
  
  val userXp = userProfile?.xp ?: 0
  val currentLeague = when {
    (userProfile?.level ?: 1) <= 2 -> "Bronze"
    (userProfile?.level ?: 1) <= 4 -> "Silver"
    (userProfile?.level ?: 1) <= 6 -> "Gold"
    (userProfile?.level ?: 1) <= 8 -> "Crystal"
    else -> "Obsidian"
  }
  
  val rawCompetitors = when (currentLeague) {
    "Bronze" -> competitorsBronze
    "Silver" -> competitorsSilver
    "Gold" -> competitorsGold
    "Crystal" -> competitorsCrystal
    else -> competitorsObsidian
  }
  
  // Sort real user XP dynamically together with offline competitor bots!
  val sortedStandings = remember(rawCompetitors, userXp, userProfile) {
    val playerComp = LeaderboardCompetitorEntity(
      id = "user_player",
      leagueName = currentLeague,
      username = userProfile?.username ?: "You",
      avatarId = userProfile?.avatarId ?: "avatar_default",
      xp = userXp,
      isPlayer = true
    )
    (rawCompetitors + playerComp).sortedByDescending { it.xp }
  }
  
  var searchFriendText by remember { mutableStateOf("") }
  
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))
    
    // Sub-segment controller
    TabRow(
      selectedTabIndex = activeSubTab,
      containerColor = Color.Transparent,
      contentColor = QuestPrimary,
      modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
      Tab(
        selected = activeSubTab == 0,
        onClick = { activeSubTab = 0 },
        text = { Text("Leaderboard Leagues", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)) },
        modifier = Modifier.minimumInteractiveComponentSize()
      )
      Tab(
        selected = activeSubTab == 1,
        onClick = { activeSubTab = 1 },
        text = { Text("Friends & Follows", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)) },
        modifier = Modifier.minimumInteractiveComponentSize()
      )
    }
    
    Spacer(modifier = Modifier.height(14.dp))
    
    if (activeSubTab == 0) {
      // Leagues Leaderboards View
      LazyColumn(
        modifier = Modifier.fillMaxSize().weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          GameCard(borderColor = QuestPrimary.copy(alpha = 0.3f)) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(
                    when (currentLeague) {
                      "Bronze" -> Color(0xFFCD7F32)
                      "Silver" -> Color(0xFFC0C0C0)
                      "Gold" -> XpGold
                      "Crystal" -> QuestIndigo
                      else -> Color(0xFF4A154B)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  text = "CURRENT LEAGUE: ${currentLeague.uppercase()}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = QuestPrimary)
                )
                Text(
                  text = "Promotion Zone: Top 3 • Demotion Zone: Bottom 3",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
          
          Spacer(modifier = Modifier.height(10.dp))
          
          Text(
            text = "Live Division Standings",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        
        items(sortedStandings.take(15)) { competitor ->
          val rankIndex = sortedStandings.indexOf(competitor) + 1
          
          // Highlights
          val itemBg = when {
            competitor.isPlayer -> QuestPrimary.copy(alpha = 0.12f)
            rankIndex <= 3 -> QuestSuccess.copy(alpha = 0.05f)
            rankIndex >= sortedStandings.size - 2 -> HeartRose.copy(alpha = 0.05f)
            else -> MaterialTheme.colorScheme.surface
          }
          val borderClr = when {
            competitor.isPlayer -> QuestPrimary
            rankIndex <= 3 -> QuestSuccess.copy(alpha = 0.4f)
            rankIndex >= sortedStandings.size - 2 -> HeartRose.copy(alpha = 0.4f)
            else -> MaterialTheme.colorScheme.outlineVariant
          }
          
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = itemBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderClr)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Rank tag or medal icon
              Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
              ) {
                if (rankIndex <= 3) {
                  Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (rankIndex) {
                      1 -> XpGold
                      2 -> Color(0xFFC0C0C0)
                      else -> Color(0xFFCD7F32)
                    },
                    modifier = Modifier.size(24.dp)
                  )
                } else {
                  Text(
                    text = rankIndex.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              
              Spacer(modifier = Modifier.width(10.dp))
              
              // Competitor Avatar
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (competitor.avatarId) {
                    "avatar_ninja" -> Icons.Default.SportsEsports
                    "avatar_coder" -> Icons.Default.Code
                    "avatar_space" -> Icons.Default.RocketLaunch
                    else -> Icons.Default.AccountCircle
                  },
                  contentDescription = null,
                  tint = QuestPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              
              Spacer(modifier = Modifier.width(12.dp))
              
              // Name and badge
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = competitor.username,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (competitor.isPlayer) FontWeight.Black else FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (competitor.isPlayer) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = QuestPrimary
                  ) {
                    Text(
                      text = "YOU",
                      color = Color.White,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 8.sp),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }
              
              Text(
                text = "${competitor.xp} XP",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                color = if (competitor.isPlayer) QuestPrimary else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    } else {
      // Friends & Follows Search and List
      LazyColumn(
        modifier = Modifier.fillMaxSize().weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          // Search & Follow text box
          OutlinedTextField(
            value = searchFriendText,
            onValueChange = { searchFriendText = it },
            label = { Text("Find Coder by Name") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
              if (searchFriendText.isNotBlank()) {
                IconButton(
                  onClick = {
                    onFollowFriend("friend_custom_${searchFriendText.lowercase()}", true)
                    searchFriendText = ""
                  },
                  modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                  Icon(Icons.Default.PersonAdd, contentDescription = "Add Coder", tint = QuestPrimary)
                }
              }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("friend_search_input"),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
              if (searchFriendText.isNotBlank()) {
                onFollowFriend("friend_custom_${searchFriendText.lowercase()}", true)
                searchFriendText = ""
              }
            })
          )
          
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Coder Friends Following",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        
        if (friends.isEmpty()) {
          item {
            Box(
              modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("No friends followed yet. Search for names like Ada_Lovelace or ByteSlayer above!", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
          }
        } else {
          items(friends) { friend ->
            FriendRow(
              friend = friend,
              onSendBoost = { onSendXpBoost(friend.id) },
              onUnfollow = { onFollowFriend(friend.id, false) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun FriendRow(
  friend: FriendEntity,
  onSendBoost: () -> Unit,
  onUnfollow: () -> Unit
) {
  val cooldown = (System.currentTimeMillis() - friend.lastXpBoostEpochMs) < (24 * 60 * 60 * 1000)
  
  GameCard {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (friend.avatarId) {
            "avatar_ninja" -> Icons.Default.SportsEsports
            "avatar_coder" -> Icons.Default.Code
            "avatar_space" -> Icons.Default.RocketLaunch
            else -> Icons.Default.AccountCircle
          },
          contentDescription = null,
          tint = QuestPrimary,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = friend.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "${friend.xp} XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.width(10.dp))
          Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = StreakFlame, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "${friend.streakDays}d Streak", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      
      Spacer(modifier = Modifier.width(6.dp))
      
      // XP Boost Send Action
      IconButton(
        onClick = onSendBoost,
        enabled = !cooldown,
        modifier = Modifier.testTag("send_boost_${friend.id}").minimumInteractiveComponentSize()
      ) {
        Icon(
          imageVector = if (cooldown) Icons.Default.CheckCircle else Icons.Default.Send,
          contentDescription = "Send XP Boost",
          tint = if (cooldown) QuestSuccess else QuestPrimary
        )
      }
      
      // Unfollow
      IconButton(
        onClick = onUnfollow,
        modifier = Modifier.minimumInteractiveComponentSize()
      ) {
        Icon(Icons.Default.Delete, contentDescription = "Unfollow", tint = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

// ==========================================
// TAB 3: COSMETIC SHOP & CODECOINS
// ==========================================
data class ShopItem(
  val id: String,
  val title: String,
  val price: Int,
  val category: String, // "AVATAR", "THEME", "TITLE", "STREAK_FREEZE"
  val icon: ImageVector,
  val description: String
)

@Composable
fun CodeShopTab(
  userProfile: UserEntity?,
  unlockedCosmetics: List<UnlockedCosmeticEntity>,
  onPurchaseCosmetic: (String, Int, String) -> Unit,
  onEquipCosmetic: (String, String) -> Unit,
  onRestoreBrokenStreak: () -> Unit
) {
  val coins = userProfile?.coins ?: 0
  val gems = userProfile?.gems ?: 0
  
  val shopItems = remember {
    listOf(
      ShopItem("streak_freeze", "Streak Freeze Card", 150, "STREAK_FREEZE", Icons.Default.LocalFireDepartment, "Protects/recovers a broken streak back to active state."),
      ShopItem("avatar_ninja", "Cyber Ninja Avatar", 100, "AVATAR", Icons.Default.SportsEsports, "Equip a legendary cybernetic neon ninja profile icon."),
      ShopItem("avatar_coder", "Terminal Coder Avatar", 150, "AVATAR", Icons.Default.Code, "Equip a vintage green phosphor hacker avatar."),
      ShopItem("avatar_space", "Astro Voyager Avatar", 200, "AVATAR", Icons.Default.RocketLaunch, "Equip a glowing deep-space cosmic astronaut avatar."),
      ShopItem("theme_cyberpunk", "Neon Cyberpunk Theme", 150, "THEME", Icons.Default.AutoAwesome, "Unlocks rich electric pink and cyberpunk high contrast visuals."),
      ShopItem("theme_slate", "Retro Arcade Theme", 200, "THEME", Icons.Default.SportsEsports, "Unlocks visual aesthetics centered around pixel console colors."),
      ShopItem("title_bug_slayer", "Title: Bug Slayer", 50, "TITLE", Icons.Default.BugReport, "Wear the honorable title badge of Bug Slayer on your profile."),
      ShopItem("title_async_legend", "Title: Async Legend", 100, "TITLE", Icons.Default.Timer, "Wear the title Async Legend proudly under your username.")
    )
  }
  
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "Cosmetic Code Shop",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
      color = MaterialTheme.colorScheme.onBackground
    )
    Text(
      text = "Spend your earned CodeCoins (C) on custom styles, titles, and safeguards.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    Spacer(modifier = Modifier.height(14.dp))
    
    // Balance Hud
    GameCard(borderColor = QuestGold.copy(alpha = 0.5f)) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Storefront, contentDescription = null, tint = QuestGold, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "YOUR BALANCE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "$coins CodeCoins", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = XpGold)
        }
      }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Grid of shop items
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize().weight(1f)
    ) {
      items(shopItems) { item ->
        val unlockedItem = unlockedCosmetics.firstOrNull { it.id == item.id }
        val isUnlocked = unlockedItem != null && (unlockedItem.quantity > 0 || item.category != "STREAK_FREEZE")
        val isEquipped = unlockedItem?.isEquipped == true
        
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = when {
              isEquipped -> QuestPrimary
              isUnlocked -> QuestSuccess.copy(alpha = 0.5f)
              else -> MaterialTheme.colorScheme.outlineVariant
            }
          ),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isEquipped -> QuestPrimary.copy(alpha = 0.15f)
                    isUnlocked -> QuestSuccess.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isEquipped) QuestPrimary else if (isUnlocked) QuestSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
              )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
              text = item.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              textAlign = TextAlign.Center,
              maxLines = 1
            )
            
            Text(
              text = item.description,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              modifier = Modifier.height(26.dp)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            if (item.category == "STREAK_FREEZE") {
              val quantity = unlockedItem?.quantity ?: 0
              if (quantity > 0) {
                Text(
                  text = "Owned: $quantity Card${if (quantity > 1) "s" else ""}",
                  style = MaterialTheme.typography.labelSmall.copy(color = QuestSuccess, fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(bottom = 4.dp)
                )
                Button(
                  onClick = { onRestoreBrokenStreak() },
                  colors = ButtonDefaults.buttonColors(containerColor = StreakFlame),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().testTag("use_freeze_shop_btn").minimumInteractiveComponentSize()
                ) {
                  Text("Use Freeze", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              } else {
                Button(
                  onClick = { onPurchaseCosmetic(item.id, item.price, item.category) },
                  colors = ButtonDefaults.buttonColors(containerColor = XpGold),
                  shape = RoundedCornerShape(10.dp),
                  enabled = coins >= item.price,
                  modifier = Modifier.fillMaxWidth().testTag("buy_freeze_shop_btn").minimumInteractiveComponentSize()
                ) {
                  Text("Buy ${item.price}C", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }
            } else {
              if (isEquipped) {
                Button(
                  onClick = {},
                  enabled = false,
                  colors = ButtonDefaults.buttonColors(disabledContainerColor = QuestPrimary.copy(alpha = 0.2f), disabledContentColor = QuestPrimary),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                ) {
                  Text("Equipped", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              } else if (isUnlocked) {
                Button(
                  onClick = { onEquipCosmetic(item.id, item.category) },
                  colors = ButtonDefaults.buttonColors(containerColor = QuestSuccess),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().testTag("equip_item_${item.id}").minimumInteractiveComponentSize()
                ) {
                  Text("Equip", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              } else {
                Button(
                  onClick = { onPurchaseCosmetic(item.id, item.price, item.category) },
                  colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
                  shape = RoundedCornerShape(10.dp),
                  enabled = coins >= item.price,
                  modifier = Modifier.fillMaxWidth().testTag("buy_item_${item.id}").minimumInteractiveComponentSize()
                ) {
                  Text("Buy ${item.price}C", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }
            }
          }
        }
      }
    }
  }
}

// ==========================================
// SUB-COMPONENTS & HELPERS
// ==========================================
@Composable
private fun AICoachLauncherCard(onOpenCoach: (AIMentorMode) -> Unit) {
  GameCard(
    borderColor = QuestPrimary.copy(alpha = 0.4f),
    onClick = { onOpenCoach(AIMentorMode.HINT) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .background(QuestPrimary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "Code Coach",
          tint = QuestPrimary,
          modifier = Modifier.size(28.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "AI CODE COACH",
            style = MaterialTheme.typography.labelSmall.copy(
              color = QuestPrimary,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = QuestSuccess.copy(alpha = 0.15f)
          ) {
            Text(
              text = "Online",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = QuestSuccess),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }

        Text(
          text = "Get Hints, Debug Errors & Quiz",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "Ask your 24/7 personal tutor for step-by-step guidance.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = { onOpenCoach(AIMentorMode.HINT) },
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("btn_open_coach_practice").minimumInteractiveComponentSize()
      ) {
        Text("Ask Coach", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}

@Composable
private fun DailyPracticePlanCard(
  plan: DailyPracticeSessionEntity,
  tasks: List<PracticeStep>,
  onStartTask: (PracticeStep) -> Unit,
  onClaim: () -> Unit
) {
  GameCard(borderColor = QuestGold.copy(alpha = 0.4f)) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = QuestGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "YOUR DAILY PRACTICE",
              style = MaterialTheme.typography.labelSmall.copy(color = QuestGold, fontWeight = FontWeight.Black)
            )
          }
          Text(
            text = "${plan.completedSteps} of ${plan.totalSteps} Completed",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = QuestGold.copy(alpha = 0.15f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "+${plan.xpReward} XP",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = QuestGold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      tasks.forEach { task ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (task.isCompleted) QuestSuccess.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable(enabled = !task.isCompleted) { onStartTask(task) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (task.isCompleted) QuestSuccess else QuestPrimary,
            modifier = Modifier.size(20.dp)
          )

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = task.title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = task.description,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (task.isCompleted) {
            Text(
              text = "Done",
              style = MaterialTheme.typography.labelSmall.copy(color = QuestSuccess, fontWeight = FontWeight.Bold)
            )
          } else {
            Button(
              onClick = { onStartTask(task) },
              colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(30.dp).minimumInteractiveComponentSize()
            ) {
              Text("Start", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
            }
          }
        }
      }

      if (plan.isCompleted) {
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = onClaim,
          colors = ButtonDefaults.buttonColors(containerColor = QuestSuccess),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().height(40.dp).minimumInteractiveComponentSize()
        ) {
          Text("Claim Daily Practice Rewards (+150 XP)", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun AdaptiveMasterySection(
  memories: List<LearnerMemoryEntity>,
  onDrillSkill: (String) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "Adaptive Concept Mastery",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    memories.forEach { mem ->
      val tier = MasteryTier.fromPercentage(mem.masteryScore)
      val tierColor = when (tier) {
        MasteryTier.NEEDS_PRACTICE -> HeartRose
        MasteryTier.DEVELOPING -> QuestGold
        MasteryTier.COMPETENT -> QuestIndigo
        MasteryTier.STRONG, MasteryTier.MASTERED -> QuestSuccess
      }

      GameCard {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = mem.conceptTitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = tierColor.copy(alpha = 0.15f)
              ) {
                Text(
                  text = tier.label,
                  style = MaterialTheme.typography.labelSmall.copy(color = tierColor, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Mastery: ${mem.masteryScore}% • ${mem.successfulAttempts}/${mem.totalAttempts} correct • Level: ${mem.learnerLevel}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (mem.parseRecentMistakes().isNotEmpty()) {
              Text(
                text = "Recent slip: ${mem.parseRecentMistakes().first()}",
                style = MaterialTheme.typography.labelSmall.copy(color = HeartRose)
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = { onDrillSkill(mem.conceptKey) },
            colors = ButtonDefaults.buttonColors(containerColor = if (mem.isWeakSkill()) HeartRose else QuestPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp).minimumInteractiveComponentSize()
          ) {
            Text(if (mem.isWeakSkill()) "Reinforce" else "Practice", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
          }
        }
      }
    }
  }
}

@Composable
private fun CodingChallengeItemCard(
  challenge: CodingChallengeEntity,
  onClick: () -> Unit
) {
  val diffColor = when (challenge.difficulty.uppercase()) {
    "EASY", "BEGINNER" -> QuestGreen
    "MEDIUM", "INTERMEDIATE" -> QuestGold
    "HARD", "ADVANCED" -> QuestRed
    else -> QuestPrimary
  }

  GameCard(
    borderColor = if (challenge.isCompleted) QuestGreen.copy(alpha = 0.4f) else QuestPrimary.copy(alpha = 0.25f),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
        .testTag("challenge_card_${challenge.id}"),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (challenge.isCompleted) QuestGreen.copy(alpha = 0.15f) else QuestPrimary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          if (challenge.isCompleted) Icons.Default.CheckCircle else Icons.Default.Code,
          contentDescription = null,
          tint = if (challenge.isCompleted) QuestGreen else QuestPrimary,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = diffColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = challenge.difficulty.uppercase(),
              color = diffColor,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "+${challenge.xpReward} XP",
            style = MaterialTheme.typography.labelSmall.copy(color = QuestPrimary, fontWeight = FontWeight.Bold)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = challenge.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = challenge.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (challenge.isCompleted) QuestGreen else QuestPrimary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(36.dp).minimumInteractiveComponentSize()
      ) {
        Text(if (challenge.isCompleted) "Replay" else "Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun PracticeModeCard(
  title: String,
  description: String,
  xp: String,
  difficulty: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  GameCard(
    borderColor = accentColor.copy(alpha = 0.3f),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag(testTag),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = difficulty,
            style = MaterialTheme.typography.labelSmall.copy(
              color = accentColor,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = xp,
            style = MaterialTheme.typography.labelSmall.copy(
              color = XpGold,
              fontWeight = FontWeight.Bold
            )
          )
        }
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White),
        shape = CircleShape,
        modifier = Modifier.size(38.dp).minimumInteractiveComponentSize(),
        contentPadding = PaddingValues(0.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Start",
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
