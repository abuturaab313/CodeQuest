# CodeQuest Developer & Curriculum Authoring Guide

This guide describes how to extend CodeQuest with new programming languages, worlds, chapters, interactive lessons, coding challenges, multi-file projects, and Dev Lab exercises.

---

## 1. Adding a New Programming Language (12-Step Checklist)

1. **Register Language Constant**:
   - Add language key to `LanguageRegistry` in `com.example.domain.languages.LanguageRegistry`.
2. **Define Language Configuration**:
   - Specify file extension (e.g., `.rs` for Rust, `.go` for Go), default boilerplates, syntax highlights, and icon identifiers.
3. **Implement `CodeRuntime` Interface**:
   - Implement `execute()` and `runTestSuite()` in `com.example.domain.languages`.
4. **Register in `CodeExecutionService`**:
   - Add routing in `DefaultCodeExecutionService.kt` to bind the new runtime.
5. **Add Default Starter Course**:
   - Add `CourseEntity` in `InitialData.kt` with a unique ID (e.g., `course_rust`).
6. **Define Worlds & Themed Realms**:
   - Add `WorldEntity` records associated with `course_rust`.
7. **Define Chapters & Learning Modules**:
   - Add `ChapterEntity` records under each world.
8. **Create Lessons & Interactive Exercises**:
   - Add `LessonEntity` and `ExerciseEntity` records (Multiple Choice, Drag & Drop, Parsons, Code Fill).
9. **Create Algorithmic Coding Challenges**:
   - Add `CodingChallengeEntity` records in `InitialChallengeData.kt` with public & hidden test cases.
10. **Create Capstone Multi-File Projects**:
    - Add `ProjectEntity` records in `InitialData.kt` or `ProjectCurriculum.kt` with virtual starter files and unit tests.
11. **Define Skill Masteries**:
    - Add `SkillMasteryEntity` records in `InitialData.kt` for radar mastery tracking.
12. **Configure AI Code Coach Prompts**:
    - Add language-specific nuances and keywords to `MentorPrompts.kt`.

---

## 2. Curriculum Data Schema

### Exercise Types:
- `MULTIPLE_CHOICE`: Single question with 3-4 options.
- `PARSONS`: Reorder shuffled code blocks to form valid logic.
- `FILL_BLANK`: Insert missing tokens or keywords.
- `CODE_RUN`: In-editor code exercise with target output matching.

### Test Case Format for Challenges:
```json
[
  {"input": "5\n10", "expectedOutput": "15"},
  {"input": "-3\n8", "expectedOutput": "5"}
]
```

---

## 3. Creating Real-World Projects & Multi-File Workspaces

Projects use JSON-encoded virtual files:
```json
{
  "calculator.py": "def add(a, b):\n    return a + b",
  "main.py": "import calculator\nprint(calculator.add(5, 3))",
  "README.md": "# Calculator Project\nComplete the four basic arithmetic functions."
}
```

Unit tests validate the project using either `OUTPUT_MATCH`, `SUBSTRING`, `REGEX`, or `ASSERTION_FUNCTION`.

---

## 4. Coding Standards & Conventions

- **Kotlin DSL & Jetpack Compose**: All UI is built using Compose components with Material 3 design tokens.
- **Modifiers**: Use `Modifier.testTag("unique_snake_case_tag")` on all interactive buttons, cards, and text inputs for automated testing.
- **Coroutines & Flow**: Use `viewModelScope.launch` for background writes and `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)` for observable StateFlows.
