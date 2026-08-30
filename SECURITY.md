# CodeQuest Security Specification

## 1. Threat Model & Security Posture

CodeQuest operates as an on-device Android educational platform executing user-supplied code, communicating with AI services, and persisting learner progress. The core security pillars include:

1. **Sandboxed Code Execution**: Preventing arbitrary code execution on the Android OS or JVM host.
2. **Secret Management**: Preventing API key leakage into binary builds or source control.
3. **Data Sanitization & Injection Defense**: Protecting user privacy and LLM prompt integrity.
4. **Anti-Exploitation & Gamification Integrity**: Ensuring deterministic rewards and preventing replay attacks or infinite XP farming.

---

## 2. Code Execution Sandboxing

### Threat: Untrusted Code Escape (RCE)
- **Mitigation**: CodeQuest **never** passes user code to `Runtime.getRuntime().exec()`, `ProcessBuilder`, `dalvik.system.DexClassLoader`, or Java Reflection.
- **AST Architecture**: Code is parsed directly into memory AST tokens (`StmtAssignment`, `StmtIf`, `StmtDef`, `StmtExpr`).
- **Forbidden Builtins & Reflection**:
  ```kotlin
  val FORBIDDEN_KEYWORDS = setOf(
    "__import__", "exec", "eval", "compile", "open", "file", "os", "sys",
    "subprocess", "shutil", "socket", "threading", "multiprocessing", "ctypes",
    "pickle", "globals", "locals", "vars", "getattr", "setattr", "delattr",
    "__class__", "__subclasses__", "__bases__", "__dict__"
  )
  ```
- **Resource Constraints**:
  - `maxStepBudget`: Terminates execution when instructions exceed 5,000 steps (configurable per challenge).
  - `maxOutputChars`: Caps stdout/stderr buffer to 4,000 characters to block memory inflation.
  - `timeoutMs`: Halts multi-file evaluation if duration exceeds 2,500ms.

---

## 3. Secret Management

- **Zero Hardcoded Secrets**: Secrets such as `GEMINI_API_KEY` are not hardcoded in source files.
- **Secrets Gradle Plugin**: Injected via `com.google.android.libraries.mapsplatform.secrets-gradle-plugin` into `BuildConfig`.
- **Safe Fallback**: If `BuildConfig.GEMINI_API_KEY` is missing or contains template defaults, `AIService` automatically routes to `IntelligentLocalAIProvider` with zero runtime crashes or diagnostic leaks.

---

## 4. Prompt Injection & Privacy Sanitization

- **Context Sanitization**: `ContextManager` trims all user code to a 1,800 character ceiling before sending prompts.
- **System Path Redaction**: Stack traces and error messages containing internal paths (`/data/data/`, `/var/lib/`) are regex-sanitized into `[SYSTEM_PATH]`.
- **Strict Role Framing**: Prompts enforce the "Code Coach" persona and explicitly prohibit dumping complete working solutions or following instructions that override the system prompt.
- **AIRateLimiter**: Token bucket limiter restricts mentor requests to a maximum of 6 calls per minute to prevent API quota exhaustion.

---

## 5. Gamification Integrity & Anti-Farming

- **Idempotent Rewards**:
  - Lessons and challenges track completion status. Re-submitting an already cleared challenge grants practice bonus XP (5 XP) rather than the full first-time reward.
  - Daily login rewards enforce an epoch-day check, preventing multiple claims on the same calendar day.
  - Streak freeze cards and friend XP boosts enforce a 24-hour cooldown timestamp.
