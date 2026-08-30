package com.example.ui.screens.devlab

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.*

enum class DevLabCategory(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  CODE("Code", Icons.Default.Terminal),
  DEBUG("Debug", Icons.Default.BugReport),
  TEST("Test", Icons.Default.TaskAlt),
  GIT("Git", Icons.Default.CallSplit),
  PROJECTS("Projects", Icons.Default.FolderSpecial),
  PORTFOLIO("Portfolio", Icons.Default.Work)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperLabScreen(
  bugHunts: List<BugHuntEntity>,
  testFirstChallenges: List<TestFirstChallengeEntity>,
  gitExercises: List<GitExerciseEntity>,
  codeReviews: List<CodeReviewEntity>,
  projects: List<ProjectEntity>,
  portfolioItems: List<PortfolioItemEntity>,
  stats: DeveloperStatsEntity?,
  onOpenBugHunt: (BugHuntEntity) -> Unit,
  onOpenTestFirst: (TestFirstChallengeEntity) -> Unit,
  onOpenGitLab: (GitExerciseEntity) -> Unit,
  onOpenCodeReview: (CodeReviewEntity) -> Unit,
  onOpenProject: (ProjectEntity) -> Unit,
  onOpenPortfolio: () -> Unit,
  onOpenReadmeBuilder: (ProjectEntity?) -> Unit,
  onCreateNewProject: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf(DevLabCategory.CODE) }
  var languageFilter by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Developer Lab", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
              text = "Real-World Engineering • Bug Hunts • Git • TDD",
              style = MaterialTheme.typography.labelSmall,
              color = QuestCyan
            )
          }
        },
        actions = {
          IconButton(
            onClick = onOpenPortfolio,
            modifier = Modifier.testTag("devlab_btn_open_portfolio")
          ) {
            Badge(containerColor = QuestGold) {
              Icon(Icons.Default.Work, contentDescription = "Portfolio", tint = Color.Black)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Category Navigation Bar (CODE, DEBUG, TEST, GIT, PROJECTS, PORTFOLIO)
      ScrollableTabRow(
        selectedTabIndex = selectedCategory.ordinal,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = QuestPrimary
      ) {
        DevLabCategory.entries.forEach { category ->
          Tab(
            selected = selectedCategory == category,
            onClick = { selectedCategory = category },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(category.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(category.title, fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal)
              }
            },
            modifier = Modifier.testTag("devlab_tab_${category.name.lowercase()}")
          )
        }
      }

      // Stats Summary Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        tonalElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatItem(label = "Bugs Fixed", value = "${stats?.bugsFixedCount ?: 0}", icon = Icons.Default.BugReport, tint = QuestRed)
            StatItem(label = "Tests Passed", value = "${stats?.testsPassedCount ?: 0}", icon = Icons.Default.CheckCircle, tint = QuestGreen)
            StatItem(label = "Git Commits", value = "${stats?.commitsCreatedCount ?: 0}", icon = Icons.Default.CallSplit, tint = QuestCyan)
          }

          IconButton(
            onClick = { onOpenReadmeBuilder(null) },
            modifier = Modifier.testTag("devlab_btn_new_readme")
          ) {
            Icon(Icons.Default.Description, contentDescription = "README Builder", tint = QuestPrimary)
          }
        }
      }

      // Main Category Content
      Box(modifier = Modifier.weight(1f)) {
        when (selectedCategory) {
          DevLabCategory.CODE -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text("Clean Code & Code Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              items(codeReviews) { review ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCodeReview(review) }
                    .testTag("devlab_card_review_${review.id}"),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(if (review.isRefactorChallenge) QuestCyan.copy(alpha = 0.2f) else QuestPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = if (review.isRefactorChallenge) Icons.Default.AutoFixHigh else Icons.Default.RateReview,
                          contentDescription = null,
                          tint = if (review.isRefactorChallenge) QuestCyan else QuestPrimary
                        )
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(review.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                          text = if (review.isRefactorChallenge) "Refactoring • Test Preservation" else "Code Review • Tradeoff Analysis",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.Gray
                        )
                      }
                    }

                    if (review.isCompleted) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = QuestGreen)
                    } else {
                      AssistChip(
                        onClick = { onOpenCodeReview(review) },
                        label = { Text("+${review.xpReward} XP", fontSize = 11.sp) }
                      )
                    }
                  }
                }
              }
            }
          }

          DevLabCategory.DEBUG -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text("Bug Hunt Missions (7-Step Guided Debugger)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              items(bugHunts) { hunt ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBugHunt(hunt) }
                    .testTag("devlab_card_bughunt_${hunt.id}"),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(QuestRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = QuestRed)
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(hunt.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                          text = "${hunt.language.uppercase()} • ${hunt.bugType.name.replace("_", " ")} • ${hunt.difficulty}",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.Gray
                        )
                      }
                    }

                    if (hunt.isCompleted) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = QuestGreen)
                    } else {
                      AssistChip(
                        onClick = { onOpenBugHunt(hunt) },
                        label = { Text("+${hunt.xpReward} XP", fontSize = 11.sp) }
                      )
                    }
                  }
                }
              }
            }
          }

          DevLabCategory.TEST -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text("Test-Driven Development (TDD First)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              items(testFirstChallenges) { challenge ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenTestFirst(challenge) }
                    .testTag("devlab_card_testfirst_${challenge.id}"),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(QuestGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = QuestGreen)
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(challenge.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                          text = "${challenge.language.uppercase()} • Suite Coverage ${challenge.estimatedCoveragePercent}%",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.Gray
                        )
                      }
                    }

                    if (challenge.isCompleted) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = QuestGreen)
                    } else {
                      AssistChip(
                        onClick = { onOpenTestFirst(challenge) },
                        label = { Text("+${challenge.xpReward} XP", fontSize = 11.sp) }
                      )
                    }
                  }
                }
              }
            }
          }

          DevLabCategory.GIT -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text("Git Lab Simulator & VCS Curriculum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              items(gitExercises) { ex ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGitLab(ex) }
                    .testTag("devlab_card_git_${ex.id}"),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(QuestCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.CallSplit, contentDescription = null, tint = QuestCyan)
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(ex.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                          text = "Lesson ${ex.lessonNumber} • ${ex.concept}",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.Gray
                        )
                      }
                    }

                    if (ex.isCompleted) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = QuestGreen)
                    } else {
                      AssistChip(
                        onClick = { onOpenGitLab(ex) },
                        label = { Text("+${ex.xpReward} XP", fontSize = 11.sp) }
                      )
                    }
                  }
                }
              }
            }
          }

          DevLabCategory.PROJECTS -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text("My Custom Projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              item {
                Button(
                  onClick = onCreateNewProject,
                  modifier = Modifier.fillMaxWidth().testTag("devlab_btn_create_new_project"),
                  colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null)
                  Spacer(Modifier.width(8.dp))
                  Text("Create New Project", fontWeight = FontWeight.Bold)
                }
              }
              
              val customProjects = projects.filter { it.isCustom }
              if (customProjects.isEmpty()) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  ) {
                    Text("No custom projects yet. Create one to get started!", modifier = Modifier.padding(16.dp), color = Color.Gray)
                  }
                }
              } else {
                items(customProjects) { p ->
                  ProjectCard(p, onOpenProject)
                }
              }

              item {
                Spacer(Modifier.height(16.dp))
                Text("CodeQuest Curriculum Projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              
              val cqProjects = projects.filter { !it.isCustom }
              items(cqProjects) { p ->
                ProjectCard(p, onOpenProject)
              }
            }
          }

          DevLabCategory.PORTFOLIO -> {
            PortfolioScreen(
              portfolioItems = portfolioItems,
              onToggleVisibility = { _, _ -> },
              onOpenReadmeBuilder = { onOpenReadmeBuilder(null) },
              onNavigateBack = { selectedCategory = DevLabCategory.CODE }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProjectCard(p: ProjectEntity, onOpenProject: (ProjectEntity) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpenProject(p) }
      .testTag("devlab_card_project_${p.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(QuestPrimary.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(if (p.isCustom) Icons.Default.Code else Icons.Default.FolderSpecial, contentDescription = null, tint = QuestPrimary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(p.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
          Text(
            text = "${p.language.uppercase()} • ${p.difficulty} • ${if (p.isCustom) p.projectType else "Curriculum"}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
          )
        }
      }

      Button(
        onClick = { onOpenProject(p) },
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary)
      ) {
        Text(if (p.isCustom) "Open IDE" else "Open Workspace", fontSize = 12.sp)
      }
    }
  }
}

@Composable
private fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(4.dp))
    Column {
      Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
      Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Color.Gray)
    }
  }
}
