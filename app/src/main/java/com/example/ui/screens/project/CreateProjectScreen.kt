package com.example.ui.screens.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
  onNavigateBack: () -> Unit,
  onCreateProject: (title: String, desc: String, lang: String, template: String, diff: String, readme: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("python") }
  var template by remember { mutableStateOf("BLANK") }
  var difficulty by remember { mutableStateOf("BEGINNER") }
  var readmeContent by remember { mutableStateOf("# Project Title\n\nDescription goes here.") }
  
  val templates = listOf("BLANK", "CLI", "PACKAGE", "TESTING", "AUTOMATION", "DATA", "GAME")
  val difficulties = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Create New Project", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Project Name") },
        modifier = Modifier.fillMaxWidth()
      )
      
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
      )
      
      Text("Template", style = MaterialTheme.typography.titleSmall)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        templates.chunked(3).forEach { row ->
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            row.forEach { t ->
              FilterChip(
                selected = template == t,
                onClick = { template = t },
                label = { Text(t) }
              )
            }
          }
        }
      }
      
      Text("Difficulty", style = MaterialTheme.typography.titleSmall)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        difficulties.forEach { d ->
          FilterChip(
            selected = difficulty == d,
            onClick = { difficulty = d },
            label = { Text(d) }
          )
        }
      }
      
      OutlinedTextField(
        value = readmeContent,
        onValueChange = { readmeContent = it },
        label = { Text("Optional README") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 5
      )
      
      Button(
        onClick = {
          onCreateProject(title.ifBlank { "Untitled Project" }, description, language, template, difficulty, readmeContent)
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = title.isNotBlank()
      ) {
        Text("Create Project")
      }
    }
  }
}
