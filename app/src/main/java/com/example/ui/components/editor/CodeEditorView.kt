package com.example.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.languages.LanguageDefinition
import com.example.domain.languages.LanguageRegistry
import com.example.ui.theme.QuestPrimary
import com.example.ui.theme.QuestSurface
import com.example.ui.theme.QuestSurfaceDark

@Composable
fun CodeEditorView(
  code: String,
  onCodeChange: (String) -> Unit,
  language: LanguageDefinition = LanguageRegistry.PYTHON,
  starterCode: String = "",
  isFullscreen: Boolean = false,
  onToggleFullscreen: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var textFieldValue by remember(code) {
    mutableStateOf(
      TextFieldValue(
        text = code,
        selection = TextRange(code.length)
      )
    )
  }

  // Undo / Redo History
  val undoStack = remember { mutableStateListOf<String>() }
  val redoStack = remember { mutableStateListOf<String>() }
  var isUndoRedoAction by remember { mutableStateOf(false) }

  // Search & Replace State
  var isSearchOpen by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var replaceQuery by remember { mutableStateOf("") }

  // Editor Settings State
  var fontSizeSp by remember { mutableFloatStateOf(14f) }
  var showResetDialog by remember { mutableStateOf(false) }

  val clipboardManager = LocalClipboardManager.current
  val syntaxHighlighter = remember(language) { SyntaxHighlighter(language) }

  // Update undo stack when code changes normally
  fun updateCodeWithHistory(newText: String, newSelection: TextRange? = null) {
    if (!isUndoRedoAction && newText != textFieldValue.text) {
      if (undoStack.size > 50) undoStack.removeAt(0)
      undoStack.add(textFieldValue.text)
      redoStack.clear()
    }
    isUndoRedoAction = false
    textFieldValue = TextFieldValue(
      text = newText,
      selection = newSelection ?: TextRange(newText.length)
    )
    onCodeChange(newText)
  }

  val lineCount = remember(textFieldValue.text) {
    maxOf(1, textFieldValue.text.lines().size)
  }

  val currentLineIndex = remember(textFieldValue.text, textFieldValue.selection) {
    val cursor = textFieldValue.selection.start
    var line = 0
    var count = 0
    for (l in textFieldValue.text.lines()) {
      count += l.length + 1
      if (cursor < count) break
      line++
    }
    line
  }

  // Visual Transformation for Syntax Highlighting
  val syntaxVisualTransformation = remember(syntaxHighlighter, searchQuery) {
    VisualTransformation { text ->
      val highlighted = syntaxHighlighter.highlight(text.text, searchQuery)
      TransformedText(highlighted, OffsetMapping.Identity)
    }
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = SyntaxTheme.Background,
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF313244))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Editor Header Toolbar
      EditorHeaderToolbar(
        language = language,
        fontSizeSp = fontSizeSp,
        onCycleFontSize = {
          fontSizeSp = when (fontSizeSp) {
            12f -> 14f
            14f -> 16f
            16f -> 18f
            else -> 12f
          }
        },
        isSearchOpen = isSearchOpen,
        onToggleSearch = { isSearchOpen = !isSearchOpen },
        canUndo = undoStack.isNotEmpty(),
        canRedo = redoStack.isNotEmpty(),
        onUndo = {
          if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(textFieldValue.text)
            isUndoRedoAction = true
            updateCodeWithHistory(prev, TextRange(minOf(textFieldValue.selection.start, prev.length)))
          }
        },
        onRedo = {
          if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(textFieldValue.text)
            isUndoRedoAction = true
            updateCodeWithHistory(next, TextRange(minOf(textFieldValue.selection.start, next.length)))
          }
        },
        onCopy = {
          clipboardManager.setText(AnnotatedString(textFieldValue.text))
        },
        onPaste = {
          val clipText = clipboardManager.getText()?.text ?: ""
          if (clipText.isNotEmpty()) {
            val start = textFieldValue.selection.start
            val end = textFieldValue.selection.end
            val newText = textFieldValue.text.replaceRange(start, end, clipText)
            updateCodeWithHistory(newText, TextRange(start + clipText.length))
          }
        },
        onSelectAll = {
          textFieldValue = textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
        },
        onReset = { showResetDialog = true },
        isFullscreen = isFullscreen,
        onToggleFullscreen = onToggleFullscreen
      )

      // 2. Search & Replace Expandable Bar
      if (isSearchOpen) {
        SearchReplaceBar(
          searchQuery = searchQuery,
          onSearchChange = { searchQuery = it },
          replaceQuery = replaceQuery,
          onReplaceChange = { replaceQuery = it },
          onReplaceAll = {
            if (searchQuery.isNotEmpty()) {
              val newText = textFieldValue.text.replace(searchQuery, replaceQuery, ignoreCase = true)
              updateCodeWithHistory(newText)
            }
          },
          onClose = {
            isSearchOpen = false
            searchQuery = ""
            replaceQuery = ""
          }
        )
      }

      // 3. Editor Body (Line Numbers + Monospace BasicTextField)
      val vScrollState = rememberScrollState()
      val hScrollState = rememberScrollState()

      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(SyntaxTheme.Background)
      ) {
        // Line Numbers Gutter
        Column(
          modifier = Modifier
            .widthIn(min = 36.dp)
            .background(Color(0xFF181825))
            .verticalScroll(vScrollState)
            .padding(vertical = 12.dp, horizontal = 6.dp),
          horizontalAlignment = Alignment.End
        ) {
          for (lineIndex in 0 until lineCount) {
            val isActive = lineIndex == currentLineIndex
            Text(
              text = "${lineIndex + 1}",
              style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.5f).sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) SyntaxTheme.LineNumberActive else SyntaxTheme.LineNumber
              )
            )
          }
        }

        // Code Text Area
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .verticalScroll(vScrollState)
            .horizontalScroll(hScrollState)
            .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
          BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
              val oldText = textFieldValue.text
              val newText = newValue.text

              // Handle auto-indent on newline (e.g. after ':')
              if (newText.length == oldText.length + 1 &&
                  newValue.selection.start > 0 &&
                  newText[newValue.selection.start - 1] == '\n') {
                val lineIndex = newText.substring(0, newValue.selection.start - 1).lines().size - 1
                val prevLine = oldText.lines().getOrNull(lineIndex) ?: ""
                val prevIndent = prevLine.takeWhile { it == ' ' || it == '\t' }
                val extraIndent = if (prevLine.trimEnd().endsWith(":")) "    " else ""
                val fullIndent = prevIndent + extraIndent

                if (fullIndent.isNotEmpty()) {
                  val insertIdx = newValue.selection.start
                  val augmented = newText.substring(0, insertIdx) + fullIndent + newText.substring(insertIdx)
                  val newPos = insertIdx + fullIndent.length
                  updateCodeWithHistory(augmented, TextRange(newPos))
                  return@BasicTextField
                }
              }

              updateCodeWithHistory(newText, newValue.selection)
            },
            textStyle = TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = fontSizeSp.sp,
              lineHeight = (fontSizeSp * 1.5f).sp,
              color = SyntaxTheme.TextDefault
            ),
            cursorBrush = SolidColor(QuestPrimary),
            visualTransformation = syntaxVisualTransformation,
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
            modifier = Modifier
              .fillMaxSize()
              .testTag("code_editor_text_field")
          )
        }
      }

      // 4. Quick Accessory Symbol Bar
      AccessorySymbolsBar(
        symbols = language.commonSymbols,
        onInsertSymbol = { sym ->
          val start = textFieldValue.selection.start
          val end = textFieldValue.selection.end
          val insertText = when (sym) {
            "( )" -> "()"
            "[ ]" -> "[]"
            "{ }" -> "{}"
            "\" \"" -> "\"\""
            "' '" -> "''"
            else -> sym
          }
          val newText = textFieldValue.text.replaceRange(start, end, insertText)
          val cursorOffset = if (sym.contains(" ")) insertText.length / 2 else insertText.length
          updateCodeWithHistory(newText, TextRange(start + cursorOffset))
        }
      )
    }
  }

  // Reset Starter Code Confirmation Dialog
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("Reset Starter Code?", fontWeight = FontWeight.Bold) },
      text = { Text("This will discard all your current modifications and restore the challenge's original template.") },
      confirmButton = {
        Button(
          onClick = {
            updateCodeWithHistory(starterCode)
            showResetDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Reset")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun EditorHeaderToolbar(
  language: LanguageDefinition,
  fontSizeSp: Float,
  onCycleFontSize: () -> Unit,
  isSearchOpen: Boolean,
  onToggleSearch: () -> Unit,
  canUndo: Boolean,
  canRedo: Boolean,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  onCopy: () -> Unit,
  onPaste: () -> Unit,
  onSelectAll: () -> Unit,
  onReset: () -> Unit,
  isFullscreen: Boolean,
  onToggleFullscreen: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF181825))
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Language Tag
    Surface(
      color = QuestPrimary.copy(alpha = 0.15f),
      shape = RoundedCornerShape(6.dp)
    ) {
      Text(
        text = language.name,
        color = QuestPrimary,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    // Undo / Redo
    IconButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.size(32.dp)) {
      Icon(
        Icons.AutoMirrored.Filled.Undo,
        contentDescription = "Undo",
        tint = if (canUndo) SyntaxTheme.TextDefault else SyntaxTheme.LineNumber,
        modifier = Modifier.size(16.dp)
      )
    }
    IconButton(onClick = onRedo, enabled = canRedo, modifier = Modifier.size(32.dp)) {
      Icon(
        Icons.AutoMirrored.Filled.Redo,
        contentDescription = "Redo",
        tint = if (canRedo) SyntaxTheme.TextDefault else SyntaxTheme.LineNumber,
        modifier = Modifier.size(16.dp)
      )
    }

    // Font Size
    IconButton(onClick = onCycleFontSize, modifier = Modifier.size(32.dp)) {
      Text(
        text = "${fontSizeSp.toInt()}",
        color = SyntaxTheme.TextDefault,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
      )
    }

    // Search
    IconButton(onClick = onToggleSearch, modifier = Modifier.size(32.dp)) {
      Icon(
        Icons.Default.Search,
        contentDescription = "Search",
        tint = if (isSearchOpen) QuestPrimary else SyntaxTheme.TextDefault,
        modifier = Modifier.size(16.dp)
      )
    }

    // Copy / Paste
    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
      Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SyntaxTheme.TextDefault, modifier = Modifier.size(16.dp))
    }
    IconButton(onClick = onPaste, modifier = Modifier.size(32.dp)) {
      Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = SyntaxTheme.TextDefault, modifier = Modifier.size(16.dp))
    }

    // Reset Starter Code
    IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
      Icon(Icons.Default.Refresh, contentDescription = "Reset Code", tint = SyntaxTheme.TextDefault, modifier = Modifier.size(16.dp))
    }

    // Fullscreen Toggle
    IconButton(onClick = onToggleFullscreen, modifier = Modifier.size(32.dp)) {
      Icon(
        if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
        contentDescription = "Toggle Fullscreen",
        tint = SyntaxTheme.TextDefault,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
private fun SearchReplaceBar(
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  replaceQuery: String,
  onReplaceChange: (String) -> Unit,
  onReplaceAll: () -> Unit,
  onClose: () -> Unit
) {
  Surface(
    color = Color(0xFF11111B),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Find...", fontSize = 12.sp) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 12.sp, color = SyntaxTheme.TextDefault),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = QuestPrimary,
          unfocusedBorderColor = Color(0xFF313244),
          focusedContainerColor = Color(0xFF181825),
          unfocusedContainerColor = Color(0xFF181825)
        ),
        modifier = Modifier.weight(1f).height(42.dp)
      )

      OutlinedTextField(
        value = replaceQuery,
        onValueChange = onReplaceChange,
        placeholder = { Text("Replace...", fontSize = 12.sp) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 12.sp, color = SyntaxTheme.TextDefault),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = QuestPrimary,
          unfocusedBorderColor = Color(0xFF313244),
          focusedContainerColor = Color(0xFF181825),
          unfocusedContainerColor = Color(0xFF181825)
        ),
        modifier = Modifier.weight(1f).height(42.dp)
      )

      Button(
        onClick = onReplaceAll,
        colors = ButtonDefaults.buttonColors(containerColor = QuestPrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(40.dp)
      ) {
        Text("Replace", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = SyntaxTheme.TextDefault, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun AccessorySymbolsBar(
  symbols: List<String>,
  onInsertSymbol: (String) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF181825))
      .padding(horizontal = 6.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    items(symbols) { sym ->
      Surface(
        onClick = { onInsertSymbol(sym) },
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF313244),
        modifier = Modifier.height(30.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text(
            text = sym,
            style = TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = SyntaxTheme.TextDefault
            )
          )
        }
      }
    }
  }
}
