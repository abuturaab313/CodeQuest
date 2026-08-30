# CodeQuest Architecture Specification

## 1. Architectural Philosophy

CodeQuest is designed following **Clean Architecture** principles and the standard Android **MVVM (Model-View-ViewModel)** architectural pattern. It decouples UI presentation, business domain logic, and data persistence layers to maximize maintainability, testability, and responsiveness.

```
┌────────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                   │
│  - MainTabs (Home, Learn, Practice, Dev Lab, Profile)  │
│  - Screens (LessonScreen, CodeLabScreen, DevLabScreen) │
│  - Modals (CodeCoachSheet, LevelUpDialog, Settings)   │
└──────────────────────────▲─────────────────────────────┘
                           │ StateFlow / Observables
┌──────────────────────────┴─────────────────────────────┐
│                 ViewModel Layer (State)                │
│  - MainViewModel (Unified reactive screen state flows) │
└──────────────────────────▲─────────────────────────────┘
                           │ Coroutines / Suspend Functions
┌──────────────────────────┴─────────────────────────────┐
│                   Domain Layer (Core)                  │
│  - Execution Engine (Safe AST Sandbox, ProjectRunner)  │
│  - AI Service & Providers (Gemini / Local Coach)      │
│  - Game Services (XP, Hearts, Streaks, Quests, Mastery)│
│  - Language Strategy (Python, JS, Java, C, C++)        │
└──────────────────────────▲─────────────────────────────┘
                           │ Flow / DAO Interfaces
┌──────────────────────────┴─────────────────────────────┐
│                  Data Layer (Storage)                  │
│  - CodeQuestDatabase (Room Database v9, 36 Entities)  │
│  - Repositories (CodeQuest, Project, DevLab, Auth)     │
│  - Converters, DAOs, Pre-populated Seed Curricula     │
└────────────────────────────────────────────────────────┘
```

---

## 2. Core Domain Subsystems

### 2.1 Safe In-Memory AST Sandbox (`SafePythonSandboxEngine`)
- **Isolation**: Instead of executing untrusted user code via native JVM runtimes or `Runtime.getRuntime().exec()`, the engine tokenizes and parses code into an Abstract Syntax Tree (`ASTNode`).
- **Deterministic Step Budget**: Every statement increment checks against `options.maxStepBudget` (default: 5,000 operations). Exceeding this budget safely throws a `TimeoutException` without blocking background threads.
- **Output Ceilings**: Output is captured in an in-memory buffer with strict character ceilings (`maxOutputChars: 4000`), terminating infinite print generators with `OutputLimitException`.
- **Keyword Blacklist**: Statically parses tokens against dangerous keywords (`__import__`, `exec`, `eval`, `globals`, `locals`, `ctypes`, `subprocess`, `os`, `sys`, `open`).

### 2.2 Multi-Language Strategy (`CodeRuntime`)
- Standardized `CodeRuntime` interface exposes:
  - `execute(code: String, rawInput: String, options: ExecutionOptions): ExecutionResult`
  - `runTestSuite(code: String, testSuite: List<TestCase>, options: ExecutionOptions): TestSuiteResult`
- Language implementations:
  - `PythonRuntime`: Backed by AST Interpreter.
  - `JavaScriptRuntime`, `JavaRuntime`, `CRuntime`, `CppRuntime`: Backed by lightweight sandbox runtimes with syntax checking and test assertion runners.

### 2.3 AI Code Coach Pipeline
- **Hybrid Provider Routing**:
  1. `GeminiAIProvider`: Connects to `gemini-2.5-flash` with 800 token maximums, low temperature (0.4), and Socratic instructional prompts.
  2. `IntelligentLocalAIProvider`: Offline heuristic analyzer offering immediate rule-based syntax analysis, structural hints, and common mistake diagnoses.
- **Rate Limiting & Caching**:
  - Sliding window rate limiter (max 6 requests/minute) prevents spamming.
  - In-memory `ConcurrentHashMap` response cache avoids duplicate requests for identical code errors and hint levels.

---

## 3. Data Persistence Layer (Room)

- **Database**: `CodeQuestDatabase` with Room version 9.
- **Key Entities**:
  - `UserEntity`: Profile, XP, level, coins, streak stats, settings, hearts.
  - `LessonEntity` / `ExerciseEntity`: Curriculum graph with multi-choice, drag-and-drop, parsons, and coding problems.
  - `ProjectEntity` / `ProjectFileEntity` / `ProjectProgressEntity`: Multi-file IDE workspace supporting virtual file trees.
  - `BugHuntEntity` / `TestFirstChallengeEntity` / `GitExerciseEntity` / `CodeReviewEntity`: Milestone 11 developer challenges.
  - `LearnerMemoryEntity` / `ReviewQueueEntity`: Adaptive learning mastery scores and spaced repetition schedules.
- **Thread Safety**: All DAO writes execute asynchronously on `Dispatchers.IO`.

---

## 4. UI Layer & Compose State Management

- **Unidirectional Data Flow**: State flows down via immutable `StateFlow` streams from `MainViewModel`; UI events call ViewModel suspend functions.
- **Adaptive Layouts**: Container-based layouts with `Modifier.widthIn(max = 600.dp)` on tablets and foldables.
- **Material 3 Theming**: Consistent color tokens (`QuestPrimary`, `QuestSecondary`, `QuestSurface`, `QuestAccent`) with dark mode and high-contrast support.
