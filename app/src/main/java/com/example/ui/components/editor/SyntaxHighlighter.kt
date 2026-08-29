package com.example.ui.components.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.domain.languages.LanguageDefinition
import com.example.domain.languages.LanguageRegistry

object SyntaxTheme {
  val Background = Color(0xFF1E1E2E) // Catppuccin / VS Dark
  val LineNumber = Color(0xFF6C7086)
  val LineNumberActive = Color(0xFFCDD6F4)
  val CurrentLineHighlight = Color(0xFF313244)
  val TextDefault = Color(0xFFCDD6F4)
  val Keyword = Color(0xFFCBA6F7) // Mauve
  val Builtin = Color(0xFF89B4FA) // Blue
  val StringLiteral = Color(0xFFA6E3A1) // Green
  val NumberLiteral = Color(0xFFFAB387) // Peach
  val Comment = Color(0xFF7F849C) // Overlay
  val Operator = Color(0xFF89DCEB) // Sky
  val Punctuation = Color(0xFF9399B2)
  val FunctionName = Color(0xFFF9E2AF) // Yellow
  val MatchHighlight = Color(0xFFF38BA8).copy(alpha = 0.4f)
}

class SyntaxHighlighter(
  private val language: LanguageDefinition = LanguageRegistry.PYTHON
) {

  fun highlight(code: String, searchQuery: String = ""): AnnotatedString {
    return buildAnnotatedString {
      append(code)

      if (code.isEmpty()) return@buildAnnotatedString

      val length = code.length
      var i = 0

      while (i < length) {
        val ch = code[i]

        // 1. Comments (# for Python, // for others)
        if ((language.commentSyntax == "#" && ch == '#') ||
            (language.commentSyntax == "//" && ch == '/' && i + 1 < length && code[i + 1] == '/')) {
          val lineEnd = code.indexOf('\n', i).let { if (it == -1) length else it }
          addStyle(
            SpanStyle(color = SyntaxTheme.Comment, fontStyle = FontStyle.Italic),
            i,
            lineEnd
          )
          i = lineEnd
          continue
        }

        // 2. String literals: "...", '...', """...""", '''...'''
        if (ch == '"' || ch == '\'') {
          val quoteChar = ch
          val isTriple = (i + 2 < length && code[i + 1] == quoteChar && code[i + 2] == quoteChar)
          val delimiter = if (isTriple) "$quoteChar$quoteChar$quoteChar" else "$quoteChar"
          val startIdx = i
          val endIdx: Int

          if (isTriple) {
            val closeIdx = code.indexOf(delimiter, i + 3)
            endIdx = if (closeIdx == -1) length else closeIdx + 3
          } else {
            var j = i + 1
            while (j < length) {
              if (code[j] == '\\') {
                j += 2
                continue
              }
              if (code[j] == quoteChar || code[j] == '\n') {
                if (code[j] == quoteChar) j++
                break
              }
              j++
            }
            endIdx = minOf(j, length)
          }

          addStyle(
            SpanStyle(color = SyntaxTheme.StringLiteral),
            startIdx,
            endIdx
          )
          i = endIdx
          continue
        }

        // 3. Numeric literals
        if (ch.isDigit()) {
          val startIdx = i
          while (i < length && (code[i].isDigit() || code[i] == '.' || code[i] == 'e' || code[i] == 'E' || code[i] == 'x' || code[i] == 'b')) {
            i++
          }
          addStyle(
            SpanStyle(color = SyntaxTheme.NumberLiteral, fontWeight = FontWeight.SemiBold),
            startIdx,
            i
          )
          continue
        }

        // 4. Identifiers (Keywords, Builtins, Functions)
        if (ch.isLetter() || ch == '_') {
          val startIdx = i
          while (i < length && (code[i].isLetterOrDigit() || code[i] == '_')) {
            i++
          }
          val token = code.substring(startIdx, i)

          // Check if next non-space char is '(' -> Function call/def
          var nextNonSpace = i
          while (nextNonSpace < length && (code[nextNonSpace] == ' ' || code[nextNonSpace] == '\t')) {
            nextNonSpace++
          }
          val isFunctionCall = nextNonSpace < length && code[nextNonSpace] == '('

          when {
            language.keywords.contains(token) -> {
              addStyle(
                SpanStyle(color = SyntaxTheme.Keyword, fontWeight = FontWeight.Bold),
                startIdx,
                i
              )
            }
            language.builtins.contains(token) -> {
              addStyle(
                SpanStyle(color = SyntaxTheme.Builtin, fontWeight = FontWeight.SemiBold),
                startIdx,
                i
              )
            }
            isFunctionCall -> {
              addStyle(
                SpanStyle(color = SyntaxTheme.FunctionName),
                startIdx,
                i
              )
            }
          }
          continue
        }

        // 5. Operators
        if (ch in "=+-*/%&|^!<>~:") {
          addStyle(
            SpanStyle(color = SyntaxTheme.Operator),
            i,
            i + 1
          )
        } else if (ch in "()[]{},.") {
          addStyle(
            SpanStyle(color = SyntaxTheme.Punctuation),
            i,
            i + 1
          )
        }

        i++
      }

      // 6. Search Highlighting
      if (searchQuery.isNotEmpty()) {
        var searchIdx = code.indexOf(searchQuery, 0, ignoreCase = true)
        while (searchIdx != -1) {
          addStyle(
            SpanStyle(background = SyntaxTheme.MatchHighlight, fontWeight = FontWeight.Bold),
            searchIdx,
            searchIdx + searchQuery.length
          )
          searchIdx = code.indexOf(searchQuery, searchIdx + searchQuery.length, ignoreCase = true)
        }
      }
    }
  }
}
