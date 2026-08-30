# CodeQuest ⚔️💻
### Gamified Interactive Coding Education Platform for Android

**CodeQuest** is a comprehensive, production-grade Android educational application built with Kotlin, Jetpack Compose, Material Design 3, and Room. It transforms learning to code into an engaging adventure game featuring progressive worlds, interactive lessons, sandboxed multi-language code execution, real-world project portfolios, an AI Code Coach, spaced repetition, and rich developer simulation labs.

---

## 🌟 Key Capabilities & Highlights

### 1. 🗺️ Story & Game Progression
- **5 Themed Worlds**: From "Variable Valley" and "Condition Cavern" to "Loop Lagoon", "Function Forest", and "Data Structure Dungeon".
- **RPG Game Loop**: Earn XP, level up, maintain daily activity streaks, earn CodeCoins, and unlock cosmetic developer titles, badges, and avatars.
- **Heart & Energy Economy**: Regenerative heart system (1 heart per 30 minutes) with shop refills and streak protection freezes.

### 2. ⚡ In-Memory Sandboxed Code Execution
- **Zero Insecure Reflection / Process Execution**: Custom high-performance AST engine evaluates Python code deterministically in memory.
- **Multi-Language Architecture**: Seamless runtime strategy supporting Python, JavaScript, Java, C, and C++ with standardized test runners.
- **Deterministic Resource Budgets**: Strictly enforced instruction step ceilings (preventing infinite loops) and output buffer truncation (preventing memory exhaustion).

### 3. 🧪 Developer Lab & Portfolio Suite
- **Bug Hunt**: Real-world bug fixing scenarios with pre-existing syntax, logic, and runtime errors.
- **TDD (Test-First) Challenges**: Spec-driven development where learners write code to satisfy pre-written unit test assertions.
- **Git Simulation Lab**: Interactive terminal for mastering branching, staging, committing, and 3-way merge conflict resolution.
- **Code Reviews & Refactoring**: Clean Code principles, detecting smells (magic numbers, deep nesting, DRY violations).
- **Interactive Markdown README Builder**: Live real-time Markdown preview editor with exportable developer portfolios.

### 4. 🤖 AI Code Coach & Adaptive Learning
- **Context-Aware Mentorship**: Guided Level 1-5 hints, conceptual breakdowns, code reviews, and debug suggestions powered by Gemini 2.5 Flash.
- **Intelligent Offline Fallback**: High-speed, local rule-based heuristic coach when offline or unauthenticated.
- **Prompt Injection Defense**: Context sanitization, system path redaction, and strict output token budgeting.
- **Spaced Repetition & Weak Spot Remediation**: Automated Leitner/SuperMemo-inspired review queue for concepts with lower accuracy.

---

## 🏗️ Architecture Overview

```
app/
 ├── data/
 │    ├── local/          # Room DB, DAOs, Converters, Pre-populated Seed Curricula
 │    ├── models/         # Entities, Data Classes, UI Models, Enums
 │    └── repository/     # Auth, CodeQuest, Project, DevLab Repositories
 ├── domain/
 │    ├── ai/             # Gemini Provider, Local Coach, ContextManager, Prompts, RateLimiter
 │    ├── execution/      # SafePythonSandboxEngine, ProjectRunner, CodeRuntimes
 │    ├── languages/      # Python, JS, Java, C, C++ Runtimes & Definitions
 │    └── services/       # XP, Hearts, Streaks, Quests, Scoring, Adaptive Learning
 └── ui/
      ├── components/     # Game HUD, Code Coach Sheet, LevelUp, Error Boundary, Cards
      ├── navigation/     # Jetpack Navigation Graph, Tabs, Modal Sheets
      ├── screens/        # Home, Learn, Practice, DevLab, Projects, CodeLab, Profile
      └── theme/          # M3 Color Scheme, Typography, Elevation, Shapes
```

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
- Android Studio Ladybug / Iguana or later
- JDK 17 or higher
- Android SDK 34 (UpsideDownCake) / Minimum SDK 24 (Android 7.0)

### Building the Project
```bash
# Clean and build debug APK
gradle assembleDebug

# Run unit and security tests
gradle testDebugUnitTest
```

---

## 🔒 Security & Privacy

CodeQuest adheres to the principle of least privilege:
- **No dangerous native permissions**: No storage, camera, contacts, or location access required for core learning.
- **Secret Isolation**: Injected via `BuildConfig` using `secrets-gradle-plugin` through `.env` configurations.
- **AST Sandbox Isolation**: User code cannot access filesystem, network sockets, reflection, or OS processes.

---

## 📄 License
This project is licensed under the Apache 2.0 License.
