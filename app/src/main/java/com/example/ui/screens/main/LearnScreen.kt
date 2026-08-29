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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LessonEntity
import com.example.data.models.LessonType
import com.example.data.models.WorldEntity
import com.example.ui.components.GameCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LearnScreen(
  worlds: List<WorldEntity>,
  lessons: List<LessonEntity>,
  onSelectLesson: (LessonEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  var lockedNoticeMessage by remember { mutableStateOf<String?>(null) }
  
  // Track selected world id
  var selectedWorldId by remember { mutableStateOf("py_w1") }
  
  // Set default selection to first unlocked, uncompleted world if available
  LaunchedEffect(worlds, lessons) {
    val firstUncompletedWorld = worlds.sortedBy { it.worldNumber }
      .firstOrNull { world ->
        world.isUnlocked && lessons.filter { it.worldId == world.id }.any { !it.isCompleted }
      }
    if (firstUncompletedWorld != null) {
      selectedWorldId = firstUncompletedWorld.id
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
      // default to first lesson if none, or last completed
      0
    }
  }

  LaunchedEffect(selectedWorldId, recommendedIndex) {
    if (worldLessons.isNotEmpty() && recommendedIndex > 0) {
      // scroll to the recommended node (offset by header items: title card + selector card + spacing = index + 3)
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

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    LazyColumn(
      state = mapListState,
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 32.dp)
    ) {
      // Top Title Bar
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
          Text(
            text = "Learning Quest Map",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Embark on your offline coding quest. Complete levels, conquer boss battles, and build production projects!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
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
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(worlds.sortedBy { it.worldNumber }) { world ->
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
                  lockedNoticeMessage = "Locked: Requires more XP to unlock ${theme.title}!"
                }
              }
            )
          }
        }
      }

      // World Overview Title Banner for current selected world
      item {
        val selectedWorld = worlds.find { it.id == selectedWorldId }
        if (selectedWorld != null) {
          val theme = getWorldTheme(selectedWorld.id, selectedWorld.worldNumber, selectedWorld.title, selectedWorld.subtitle)
          WorldHeaderBannerCard(
            world = selectedWorld,
            theme = theme,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
          )
        }
      }

      // CHAPTER & LEVEL NODES
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
            // Add a chapter header divider node
            item {
              ChapterDivider(
                chapterNumber = index / 4 + 1,
                title = if (selectedWorldId == "py_w1" && index < 3) "FOUNDATIONS" else "ADVANCED TOPICS"
              )
            }
          }

          // Alternating Serpent S-Curve nodes with curve paths drawn dynamically
          item {
            val isCurrent = lesson.isUnlocked && !lesson.isCompleted
            val isRecommended = index == recommendedIndex

            // Alternating serpentine offset math
            val currentBias = getBiasForIndex(index)
            val nextBias = if (index < worldLessons.lastIndex) getBiasForIndex(index + 1) else currentBias
            val hasNextNode = index < worldLessons.lastIndex
            
            // Extract Composable color lookup before entering drawBehind scope
            val outlineColor = MaterialTheme.colorScheme.outline

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .drawBehind {
                  if (hasNextNode) {
                    val f1 = 0.5f + currentBias * 0.38f
                    val f2 = 0.5f + nextBias * 0.38f
                    val x1 = f1 * size.width
                    val y1 = size.height / 2
                    val x2 = f2 * size.width
                    val y2 = size.height * 1.5f

                    val path = Path().apply {
                      moveTo(x1, y1)
                      cubicTo(
                        x1, y1 + size.height * 0.6f,
                        x2, y2 - size.height * 0.6f,
                        x2, y2
                      )
                    }
                    drawPath(
                      path = path,
                      color = if (lesson.isCompleted) QuestSuccess.copy(alpha = 0.7f) else outlineColor.copy(alpha = 0.3f),
                      style = Stroke(
                        width = 4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
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
                onClick = {
                  if (lesson.isUnlocked) {
                    onSelectLesson(lesson)
                  } else {
                    lockedNoticeMessage = "Level ${lesson.lessonNumber} is locked! Complete preceding levels first."
                  }
                }
              )
            }
          }
        }
      }
    }

    // Locked Notification Popup
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
      .width(185.dp)
      .height(135.dp)
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
              listOf(theme.colors[0].copy(alpha = 0.08f), theme.colors[1].copy(alpha = 0.18f))
            } else {
              listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
            }
          )
        )
        .padding(12.dp)
    ) {
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "WORLD ${world.worldNumber}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = if (world.isUnlocked) theme.accentColor else Color.Gray
          )
          
          Icon(
            imageVector = if (!world.isUnlocked) Icons.Default.Lock else if (progressPercent >= 1.0f) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (world.isUnlocked) theme.accentColor else Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }

        Column {
          Text(
            text = theme.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
          )
          Text(
            text = if (world.isUnlocked) "${(progressPercent * 100).toInt()}% Done ($completedCount/$totalCount)" else "Locked",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Mini Progress bar
        LinearProgressIndicator(
          progress = progressPercent.coerceIn(0f, 1f),
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(CircleShape),
          color = if (world.isUnlocked) theme.accentColor else Color.Gray,
          trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
      }
    }
  }
}

@Composable
private fun WorldHeaderBannerCard(
  world: WorldEntity,
  theme: WorldTheme,
  modifier: Modifier = Modifier
) {
  GameCard(
    borderColor = theme.accentColor.copy(alpha = 0.4f),
    modifier = modifier.fillMaxWidth()
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
            text = "CURRENT EXPEDITION",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White.copy(alpha = 0.85f),
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "World ${world.worldNumber}: ${theme.title}",
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
        }

        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
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
  onClick: () -> Unit
) {
  val isBoss = lesson.lessonType == LessonType.BOSS
  val isChallenge = lesson.lessonType == LessonType.CHALLENGE
  val isProject = lesson.lessonType == LessonType.PROJECT

  val nodeColor = when {
    lesson.isCompleted -> QuestSuccess
    isBoss -> HeartRose
    isChallenge -> QuestIndigo
    isProject -> Color(0xFFFF9800)
    lesson.isUnlocked -> QuestPrimary
    else -> MaterialTheme.colorScheme.outline
  }

  // Animation values for current recommended steps (glowing pulses)
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
  val borderPulse by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_anim"
  )

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
      if (isRecommended) {
        // Glowing recommendation badge floating over node
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(XpGold)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("node_rec_tag")
        ) {
          Text(
            text = "⭐ YOUR NEXT STEP",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Black
            )
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
      }

      // Outer interactive circular level button
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(
            if (lesson.isUnlocked) nodeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
          )
          .border(
            width = if (isRecommended) borderPulse.dp else if (isCurrent) 3.dp else 1.5.dp,
            color = if (isRecommended) XpGold else nodeColor,
            shape = CircleShape
          )
          .clickable { onClick() }
          .testTag("lesson_node_${lesson.id}"),
        contentAlignment = Alignment.Center
      ) {
        // Inner circle icon container
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (lesson.isUnlocked) nodeColor else Color.Gray.copy(alpha = 0.2f)
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when {
              lesson.isCompleted -> Icons.Default.Check
              !lesson.isUnlocked -> Icons.Default.Lock
              isBoss -> Icons.Default.MilitaryTech
              isChallenge -> Icons.Default.Psychology
              isProject -> Icons.Default.Terminal
              else -> Icons.Default.PlayArrow
            },
            contentDescription = null,
            tint = if (lesson.isUnlocked) Color.White else Color.Gray,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Node label
      Text(
        text = when {
          isBoss -> "BOSS BATTLE"
          isChallenge -> "CHALLENGE"
          isProject -> "PROJECT"
          else -> "Level ${lesson.lessonNumber}"
        },
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Black,
          color = if (lesson.isUnlocked) nodeColor else Color.Gray
        )
      )
      
      Text(
        text = lesson.title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = if (lesson.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun ChapterDivider(
  chapterNumber: Int,
  title: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(QuestPrimary)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "CHAPTER $chapterNumber: $title",
      style = MaterialTheme.typography.labelLarge.copy(
        color = QuestPrimary,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Black
      )
    )
    Spacer(modifier = Modifier.width(12.dp))
    Box(
      modifier = Modifier
        .weight(1f)
        .height(1.5.dp)
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
    "py_w1" -> WorldTheme("Python Valley", "Begin your epic programming adventure", listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)), Color(0xFF4CAF50))
    "py_w2" -> WorldTheme("Logic Forest", "Conquer conditions and branch logic", listOf(Color(0xFF00796B), Color(0xFF004D40)), Color(0xFF009688))
    "py_w3" -> WorldTheme("Loop City", "Automate code repetition with loops", listOf(Color(0xFF0277BD), Color(0xFF01579B)), Color(0xFF03A9F4))
    "py_w4" -> WorldTheme("Function Factory", "Craft modular reusable logic machines", listOf(Color(0xFF4527A0), Color(0xFF311B92)), Color(0xFF673AB7))
    "py_w5" -> WorldTheme("Object Kingdom", "Master Object-Oriented Blueprint designs", listOf(Color(0xFFEF6C00), Color(0xFFE65100)), Color(0xFFFF9800))
    "py_w6" -> WorldTheme("File Fortress", "Read, write, and persist persistent data", listOf(Color(0xFFC62828), Color(0xFFB71C1C)), Color(0xFFF44336))
    "py_w7" -> WorldTheme("Algorithm Desert", "Unlock searching, sorting, and efficiency", listOf(Color(0xFFF57F17), Color(0xFFF9A825)), Color(0xFFFFEB3B))
    "py_w8" -> WorldTheme("Advanced Labs", "Dive into advanced libraries and science", listOf(Color(0xFF880E4F), Color(0xFFAD1457)), Color(0xFFE91E63))
    "py_w9" -> WorldTheme("Developer District", "Build databases, systems, and terminal UIs", listOf(Color(0xFF37474F), Color(0xFF212121)), Color(0xFF607D8B))
    "py_w10" -> WorldTheme("Capstone Island", "Synthesize your full power into apps", listOf(Color(0xFF1A237E), Color(0xFF0D47A1)), Color(0xFF3F51B5))
    else -> WorldTheme(dbTitle, dbSubtitle, listOf(QuestPrimary, QuestPrimaryDark), QuestPrimary)
  }
}
