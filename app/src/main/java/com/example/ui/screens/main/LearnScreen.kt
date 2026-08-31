package com.example.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity
import com.example.domain.languages.LanguageRegistry
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonStyle
import com.example.ui.components.GameCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
  worlds: List<WorldEntity>,
  lessons: List<LessonEntity>,
  onSelectLesson: (LessonEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  var lockedNoticeMessage by remember { mutableStateOf<String?>(null) }
  var showConceptComparisonDialog by remember { mutableStateOf(false) }

  // Interactive Level Details Sheet
  var selectedLessonForIntro by remember { mutableStateOf<LessonEntity?>(null) }
  var lockedLessonForDetails by remember { mutableStateOf<LessonEntity?>(null) }
  
  // Active course language selection
  var selectedLanguageId by remember { mutableStateOf("python") }
  
  // Filtered worlds for the active course
  val courseWorlds = remember(worlds, selectedLanguageId) {
    val filtered = worlds.filter { it.courseId == selectedLanguageId }.sortedBy { it.worldNumber }
    if (filtered.isNotEmpty()) filtered else worlds.sortedBy { it.worldNumber }
  }

  // Track selected world id
  var selectedWorldId by remember(courseWorlds) {
    mutableStateOf(courseWorlds.firstOrNull()?.id ?: "py_w1")
  }
  
  // Set default selection to first unlocked, uncompleted world if available
  LaunchedEffect(courseWorlds, lessons) {
    val firstUncompletedWorld = courseWorlds
      .firstOrNull { world ->
        world.isUnlocked && lessons.filter { it.worldId == world.id }.any { !it.isCompleted }
      }
    if (firstUncompletedWorld != null) {
      selectedWorldId = firstUncompletedWorld.id
    } else if (courseWorlds.isNotEmpty()) {
      selectedWorldId = courseWorlds.first().id
    }
  }

  // Auto-scroll logic to recommended level node on selection of a world
  val mapListState = rememberLazyListState()
  val worldLessons = remember(selectedWorldId, lessons) {
    lessons.filter { it.worldId == selectedWorldId }.sortedBy { it.lessonNumber }
  }

  val recommendedIndex = remember(worldLessons) {
    val firstUnlockedUncompleted = worldLessons.indexOfFirst { it.isUnlocked && !it.isCompleted }
    if (firstUnlockedUncompleted != -1) {
      firstUnlockedUncompleted
    } else {
      0
    }
  }

  LaunchedEffect(selectedWorldId, recommendedIndex) {
    if (worldLessons.isNotEmpty() && recommendedIndex > 0) {
      coroutineScope.launch {
        delay(150)
        mapListState.animateScrollToItem((recommendedIndex + 2).coerceIn(0, worldLessons.size + 3))
      }
    }
  }

  LaunchedEffect(lockedNoticeMessage) {
    if (lockedNoticeMessage != null) {
      delay(3000)
      lockedNoticeMessage = null
    }
  }

  val selectedWorld = remember(selectedWorldId, worlds) {
    worlds.find { it.id == selectedWorldId }
  }
  val activeWorldTheme = remember(selectedWorld) {
    if (selectedWorld != null) {
      getWorldTheme(selectedWorld.id, selectedWorld.worldNumber, selectedWorld.title, selectedWorld.subtitle)
    } else {
      getWorldTheme("py_w1", 1, "Python Plains", "Foundations")
    }
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    LazyColumn(
      state = mapListState,
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 40.dp)
    ) {
      // Top Title Bar with Compare Languages Action
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Quest World Map",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "Progress through chapters, challenge bosses, and master code!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            FilledTonalButton(
              onClick = { showConceptComparisonDialog = true },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = QuestPrimary.copy(alpha = 0.15f),
                contentColor = QuestPrimary
              ),
              modifier = Modifier.padding(start = 8.dp)
            ) {
              Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Compare", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Language Switcher Selector Chips
      item {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
          Text(
            text = "SELECT LANGUAGE TRACK",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp
            ),
            color = QuestPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            val languages = LanguageRegistry.SUPPORTED_LANGUAGES
            items(languages) { lang ->
              val isSelected = selectedLanguageId == lang.id
              FilterChip(
                selected = isSelected,
                onClick = { selectedLanguageId = lang.id },
                label = {
                  Text(
                    text = "${lang.icon} ${lang.name}",
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = QuestPrimary,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
              )
            }
          }
        }
      }

      // Dynamic Swipeable World Selector Carousel
      item {
        Text(
          text = "CHOOSE YOUR WORLD",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp
          ),
          color = QuestPrimary,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(courseWorlds) { world ->
            val isSelected = world.id == selectedWorldId
            val lessonsInWorld = lessons.filter { it.worldId == world.id }
            val completedInWorld = lessonsInWorld.count { it.isCompleted }
            val totalInWorld = lessonsInWorld.size.coerceAtLeast(1)
            val progressPercent = completedInWorld.toFloat() / totalInWorld
            val theme = getWorldTheme(world.id, world.worldNumber, world.title, world.subtitle)

            WorldCarouselCard(
              world = world,
              isSelected = isSelected,
              theme = theme,
              progressPercent = progressPercent,
              completedCount = completedInWorld,
              totalCount = totalInWorld,
              onClick = {
                if (world.isUnlocked) {
                  selectedWorldId = world.id
                } else {
                  lockedNoticeMessage = "Locked: Requires ${world.requiredXp} XP to unlock ${theme.title}!"
                }
              }
            )
          }
        }
      }

      // World Overview Title Banner for current selected world
      item {
        if (selectedWorld != null) {
          val completedInWorld = worldLessons.count { it.isCompleted }
          val totalInWorld = worldLessons.size.coerceAtLeast(1)
          val totalStarsInWorld = completedInWorld * 3 // Estimate max stars
          val earnedStars = worldLessons.filter { it.isCompleted }.sumOf { 3 } // 3 stars per completed

          WorldHeaderBannerCard(
            world = selectedWorld,
            theme = activeWorldTheme,
            completedLessons = completedInWorld,
            totalLessons = totalInWorld,
            earnedStars = earnedStars,
            totalStars = totalStarsInWorld.coerceAtLeast(totalInWorld * 3),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
          )
        }
      }

      // CHAPTER & LEVEL NODES WITH S-CURVE SERPENTINE PATH
      if (worldLessons.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No levels available in this world yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      } else {
        // Group lessons by Chapter and render
        var currentChapterId = ""
        worldLessons.forEachIndexed { index, lesson ->
          if (lesson.chapterId != currentChapterId) {
            currentChapterId = lesson.chapterId
            item {
              ChapterDivider(
                chapterNumber = index / 3 + 1,
                title = when (index / 3) {
                  0 -> "FOUNDATIONS & BASICS"
                  1 -> "CORE APPLICATION & LOGIC"
                  2 -> "INTERMEDIATE EXERCISES"
                  else -> "CLIMAX & MASTERY"
                },
                accentColor = activeWorldTheme.accentColor
              )
            }
          }

          // Alternating Serpent S-Curve nodes with curve paths drawn dynamically
          item {
            val isCurrent = lesson.isUnlocked && !lesson.isCompleted
            val isRecommended = index == recommendedIndex

            val currentBias = getBiasForIndex(index)
            val nextBias = if (index < worldLessons.lastIndex) getBiasForIndex(index + 1) else currentBias
            val hasNextNode = index < worldLessons.lastIndex
            
            val outlineColor = MaterialTheme.colorScheme.outline
            val activeColor = activeWorldTheme.accentColor

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .drawBehind {
                  if (hasNextNode) {
                    val f1 = 0.5f + currentBias * 0.38f
                    val f2 = 0.5f + nextBias * 0.38f
                    val x1 = f1 * size.width
                    val y1 = size.height * 0.45f
                    val x2 = f2 * size.width
                    val y2 = size.height * 1.45f

                    val path = Path().apply {
                      moveTo(x1, y1)
                      cubicTo(
                        x1, y1 + size.height * 0.55f,
                        x2, y2 - size.height * 0.55f,
                        x2, y2
                      )
                    }
                    drawPath(
                      path = path,
                      color = if (lesson.isCompleted) QuestSuccess.copy(alpha = 0.85f) else outlineColor.copy(alpha = 0.3f),
                      style = Stroke(
                        width = 5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = if (lesson.isCompleted) null else PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
                      )
                    )
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              LevelMapSerpentNode(
                lesson = lesson,
                isCurrent = isCurrent,
                isRecommended = isRecommended,
                horizontalBias = currentBias,
                worldTheme = activeWorldTheme,
                onClick = {
                  if (lesson.isUnlocked) {
                    selectedLessonForIntro = lesson
                  } else {
                    lockedLessonForDetails = lesson
                  }
                }
              )
            }
          }
        }
      }
    }

    // Interactive Level Intro Bottom Sheet
    if (selectedLessonForIntro != null) {
      val introLesson = selectedLessonForIntro!!
      ModalBottomSheet(
        onDismissRequest = { selectedLessonForIntro = null },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
      ) {
        LevelIntroBottomSheetContent(
          lesson = introLesson,
          worldTheme = activeWorldTheme,
          onStartLesson = {
            val toStart = introLesson
            selectedLessonForIntro = null
            onSelectLesson(toStart)
          },
          onClose = { selectedLessonForIntro = null }
        )
      }
    }

    // Locked Level Bottom Sheet
    if (lockedLessonForDetails != null) {
      val lockedLesson = lockedLessonForDetails!!
      ModalBottomSheet(
        onDismissRequest = { lockedLessonForDetails = null },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
      ) {
        LevelLockedBottomSheetContent(
          lesson = lockedLesson,
          onClose = { lockedLessonForDetails = null }
        )
      }
    }

    // Floating locked quick toast notification
    AnimatedVisibility(
      visible = lockedNoticeMessage != null,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    ) {
      if (lockedNoticeMessage != null) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.inverseSurface,
          tonalElevation = 6.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.inverseOnSurface,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = lockedNoticeMessage ?: "",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.inverseOnSurface
            )
          }
        }
      }
    }

    if (showConceptComparisonDialog) {
      com.example.ui.components.ConceptComparisonDialog(
        onDismiss = { showConceptComparisonDialog = false }
      )
    }
  }
}

@Composable
private fun LevelIntroBottomSheetContent(
  lesson: LessonEntity,
  worldTheme: WorldTheme,
  onStartLesson: () -> Unit,
  onClose: () -> Unit
) {
  val isBoss = lesson.lessonType == LessonType.BOSS
  val isChallenge = lesson.lessonType == LessonType.CHALLENGE
  val isProject = lesson.lessonType == LessonType.PROJECT

  val accentColor = when {
    isBoss -> HeartRose
    isChallenge -> QuestIndigo
    isProject -> Color(0xFFFF9800)
    else -> worldTheme.accentColor
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 16.dp)
      .navigationBarsPadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top icon badge
    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.18f))
        .border(2.dp, accentColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = when {
          lesson.isCompleted -> Icons.Default.CheckCircle
          isBoss -> Icons.Default.MilitaryTech
          isChallenge -> Icons.Default.Psychology
          isProject -> Icons.Default.Terminal
          else -> Icons.Default.PlayArrow
        },
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(36.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Type Badge
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = accentColor.copy(alpha = 0.15f),
      border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
      Text(
        text = when {
          isBoss -> "⚔️ BOSS BATTLE CLIMAX"
          isChallenge -> "⚡ CODING CHALLENGE"
          isProject -> "🛠️ MINI PROJECT"
          else -> "⭐ LEVEL ${lesson.lessonNumber}"
        },
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        ),
        color = accentColor,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = lesson.title,
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = if (lesson.conceptSummary.isNotBlank()) lesson.conceptSummary else "Learn core coding patterns and practical implementations.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(18.dp))

    // Reward preview cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // XP Reward
      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, XpGold.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.Stars, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text("+${lesson.xpReward} XP", fontWeight = FontWeight.Bold, color = XpGold, fontSize = 13.sp)
            Text("Reward", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      // Coins Reward
      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, XpGold.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text("+${lesson.coinReward} Coins", fontWeight = FontWeight.Bold, color = XpGold, fontSize = 13.sp)
            Text("Bonus", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      // Star rating potential
      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, XpGold.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = XpGold, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(if (lesson.isCompleted) "3/3 Stars" else "Up to 3 ★", fontWeight = FontWeight.Bold, color = XpGold, fontSize = 13.sp)
            Text(if (lesson.isCompleted) "Mastered" else "Potential", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Main Big Play Button
    GameButton(
      text = if (lesson.isCompleted) "Replay Level" else "Start Level →",
      onClick = onStartLesson,
      style = GameButtonStyle.PRIMARY,
      icon = if (lesson.isCompleted) Icons.Default.Replay else Icons.Default.PlayArrow,
      modifier = Modifier.fillMaxWidth(),
      testTag = "start_level_sheet_btn"
    )

    Spacer(modifier = Modifier.height(12.dp))
  }
}

@Composable
private fun LevelLockedBottomSheetContent(
  lesson: LessonEntity,
  onClose: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 20.dp)
      .navigationBarsPadding(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(32.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Level ${lesson.lessonNumber} is Locked",
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Complete the preceding levels on your Quest Map to unlock this level!",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = onClose,
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
      Text("Got It", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun WorldCarouselCard(
  world: WorldEntity,
  isSelected: Boolean,
  theme: WorldTheme,
  progressPercent: Float,
  completedCount: Int,
  totalCount: Int,
  onClick: () -> Unit
) {
  val borderAlpha = if (isSelected) 1f else 0.25f
  val scale by animateFloatAsState(targetValue = if (isSelected) 1.02f else 0.98f, label = "carousel_scale")

  Card(
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier
      .width(190.dp)
      .height(140.dp)
      .border(
        width = if (isSelected) 3.dp else 1.dp,
        color = if (isSelected) theme.accentColor else Color.Gray.copy(alpha = borderAlpha),
        shape = RoundedCornerShape(20.dp)
      )
      .clickable { onClick() }
      .testTag("world_tab_${world.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            if (world.isUnlocked) {
              listOf(theme.colors[0].copy(alpha = 0.12f), theme.colors[1].copy(alpha = 0.24f))
            } else {
              listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
            }
          )
        )
        .padding(14.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "WORLD ${world.worldNumber}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp,
              color = if (world.isUnlocked) theme.accentColor else Color.Gray
            )
          )

          if (!world.isUnlocked) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Locked",
              tint = Color.Gray,
              modifier = Modifier.size(16.dp)
            )
          } else if (completedCount == totalCount && totalCount > 0) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Completed",
              tint = QuestSuccess,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column {
          Text(
            text = theme.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (world.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray,
            maxLines = 1
          )
          Text(
            text = if (world.isUnlocked) "$completedCount/$totalCount Levels" else "Locked",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Progress line
        LinearProgressIndicator(
          progress = { progressPercent },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = theme.accentColor,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeCap = StrokeCap.Round
        )
      }
    }
  }
}

@Composable
private fun WorldHeaderBannerCard(
  world: WorldEntity,
  theme: WorldTheme,
  completedLessons: Int,
  totalLessons: Int,
  earnedStars: Int,
  totalStars: Int,
  modifier: Modifier = Modifier
) {
  GameCard(
    modifier = modifier.fillMaxWidth(),
    borderColor = theme.accentColor.copy(alpha = 0.5f)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(theme.colors[0], theme.colors[1])
          )
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "WORLD ${world.worldNumber}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White.copy(alpha = 0.85f),
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = theme.title,
            style = MaterialTheme.typography.headlineSmall.copy(
              color = Color.White,
              fontWeight = FontWeight.Black
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = theme.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.9f)
          )
          
          Spacer(modifier = Modifier.height(10.dp))
          
          // Star status pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black.copy(alpha = 0.25f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = XpGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$earnedStars / $totalStars Stars",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "$completedLessons/$totalLessons Cleared",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White.copy(alpha = 0.8f)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun LevelMapSerpentNode(
  lesson: LessonEntity,
  isCurrent: Boolean,
  isRecommended: Boolean,
  horizontalBias: Float,
  worldTheme: WorldTheme,
  onClick: () -> Unit
) {
  val isBoss = lesson.lessonType == LessonType.BOSS
  val isChallenge = lesson.lessonType == LessonType.CHALLENGE
  val isProject = lesson.lessonType == LessonType.PROJECT

  // Determine state
  val state = when {
    !lesson.isUnlocked -> "LOCKED"
    isCurrent && isRecommended -> "CURRENT"
    !lesson.isCompleted -> "AVAILABLE"
    lesson.starsEarned == 3 -> "PERFECT"
    lesson.starsEarned == 2 -> "MASTERED"
    else -> "COMPLETED"
  }

  val nodeColor = when {
    state == "PERFECT" -> XpGold
    state == "MASTERED" -> QuestSuccess
    state == "COMPLETED" -> QuestSuccess
    isBoss -> HeartRose
    isChallenge -> QuestIndigo
    isProject -> Color(0xFFFF9800)
    state == "CURRENT" || state == "AVAILABLE" -> worldTheme.accentColor
    else -> MaterialTheme.colorScheme.outline
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
  val borderPulse by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_anim"
  )

  // Size logic
  val outerSize = when (state) {
    "LOCKED" -> 50.dp
    "CURRENT" -> 80.dp
    "BOSS", "PERFECT", "MASTERED" -> 70.dp
    else -> 60.dp
  }
  
  val innerSize = when (state) {
    "LOCKED" -> 36.dp
    "CURRENT" -> 60.dp
    "BOSS", "PERFECT", "MASTERED" -> 50.dp
    else -> 44.dp
  }
  
  val isSquareNode = isBoss || isProject

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp),
    contentAlignment = when {
      horizontalBias < -0.1f -> Alignment.CenterStart
      horizontalBias > 0.1f -> Alignment.CenterEnd
      else -> Alignment.Center
    }
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.wrapContentSize()
    ) {
      if (state == "CURRENT") {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(nodeColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("node_rec_tag")
        ) {
          Text(
            text = "PLAY",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
      }

      // Outer interactive circular / shaped level button
      Box(
        modifier = Modifier
          .size(outerSize)
          .clip(if (isSquareNode) RoundedCornerShape(20.dp) else CircleShape)
          .background(
            if (state == "LOCKED") MaterialTheme.colorScheme.surfaceVariant else nodeColor.copy(alpha = 0.15f)
          )
          .border(
            width = if (state == "CURRENT") borderPulse.dp else if (state == "MASTERED") 4.dp else 2.dp,
            color = if (state == "CURRENT") nodeColor else if (state == "MASTERED") XpGold else nodeColor.copy(alpha = if (state == "LOCKED") 0.4f else 1f),
            shape = if (isSquareNode) RoundedCornerShape(20.dp) else CircleShape
          )
          .clickable { onClick() }
          .testTag("lesson_node_${lesson.id}"),
        contentAlignment = Alignment.Center
      ) {
        // Inner icon container
        Box(
          modifier = Modifier
            .size(innerSize)
            .clip(if (isSquareNode) RoundedCornerShape(14.dp) else CircleShape)
            .background(
              if (state == "LOCKED") Color.Gray.copy(alpha = 0.25f) else nodeColor
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when {
              state == "PERFECT" -> Icons.Default.Stars
              state == "MASTERED" || state == "COMPLETED" -> Icons.Default.Check
              state == "LOCKED" -> Icons.Default.Lock
              isBoss -> Icons.Default.MilitaryTech
              isChallenge -> Icons.Default.Psychology
              isProject -> Icons.Default.Terminal
              else -> Icons.Default.PlayArrow
            },
            contentDescription = null,
            tint = if (state == "LOCKED") Color.Gray else Color.White,
            modifier = Modifier.size(if (state == "CURRENT") 28.dp else 22.dp)
          )
        }
      }

      // 3 Stars rating underneath completed nodes
      if (state == "PERFECT") {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 1..3) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = XpGold,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      } else if (state == "MASTERED") {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 1..2) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = XpGold,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Node label
      if (state != "LOCKED") {
        Text(
          text = when {
            isBoss -> "⚔️ BOSS BATTLE"
            isChallenge -> "CHALLENGE"
            isProject -> "PROJECT"
            else -> "Level ${lesson.lessonNumber}"
          },
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Black,
            color = nodeColor
          )
        )
        
        Text(
          text = lesson.title,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun ChapterDivider(
  chapterNumber: Int,
  title: String,
  accentColor: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(accentColor)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "CHAPTER $chapterNumber: $title",
      style = MaterialTheme.typography.labelLarge.copy(
        color = accentColor,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Black
      )
    )
    Spacer(modifier = Modifier.width(12.dp))
    Box(
      modifier = Modifier
        .weight(1f)
        .height(2.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    )
  }
}

// Helpers for alternating Serpent biases
private fun getBiasForIndex(index: Int): Float {
  return when (index % 4) {
    0 -> -0.45f // Left
    1 -> 0.0f   // Center
    2 -> 0.45f  // Right
    3 -> 0.0f   // Center
    else -> 0.0f
  }
}

data class WorldTheme(
  val title: String,
  val subtitle: String,
  val colors: List<Color>,
  val accentColor: Color
)

fun getWorldTheme(worldId: String, worldNum: Int, dbTitle: String, dbSubtitle: String): WorldTheme {
  return when (worldId) {
    "py_w1" -> WorldTheme("Python Plains", "Foundations, syntax, printing, and memory", listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)), Color(0xFF4CAF50))
    "py_w2" -> WorldTheme("Loop Forest", "Conquer conditions, loops, and branching logic", listOf(Color(0xFF00796B), Color(0xFF004D40)), Color(0xFF009688))
    "py_w3" -> WorldTheme("Data Structure City", "Lists, dictionaries, sets, and tuples", listOf(Color(0xFF0277BD), Color(0xFF01579B)), Color(0xFF03A9F4))
    "py_w4" -> WorldTheme("Function Fortress", "Modular reusable logic machines and scope", listOf(Color(0xFF4527A0), Color(0xFF311B92)), Color(0xFF673AB7))
    "py_w5" -> WorldTheme("OOP Kingdom", "Object-Oriented classes, inheritance, blueprints", listOf(Color(0xFFEF6C00), Color(0xFFE65100)), Color(0xFFFF9800))
    "py_w6" -> WorldTheme("Exception Caverns", "Error handling, try/except, and file I/O", listOf(Color(0xFFC62828), Color(0xFFB71C1C)), Color(0xFFF44336))
    "py_w7" -> WorldTheme("Algorithm Mountains", "Searching, sorting, recursion, and Big-O", listOf(Color(0xFFF57F17), Color(0xFFF9A825)), Color(0xFFFFEB3B))
    "py_w8" -> WorldTheme("Advanced Python Lab", "Decorators, generators, lambdas, and type hints", listOf(Color(0xFF880E4F), Color(0xFFAD1457)), Color(0xFFE91E63))
    "py_w9" -> WorldTheme("API City", "HTTP requests, REST endpoints, JSON parsing", listOf(Color(0xFF37474F), Color(0xFF212121)), Color(0xFF607D8B))
    "py_w10" -> WorldTheme("Capstone Island", "Full-stack projects and production software", listOf(Color(0xFF1A237E), Color(0xFF0D47A1)), Color(0xFF3F51B5))
    else -> WorldTheme(dbTitle, dbSubtitle, listOf(QuestPrimary, QuestPrimaryDark), QuestPrimary)
  }
}
