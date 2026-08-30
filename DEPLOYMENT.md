# CodeQuest Deployment & Release Guide

## 1. Build Configurations

The project defines standard `debug` and `release` build types in `app/build.gradle.kts`.

### Release Build Type Checklist:
- **Minification & Shrinking**: Enable `isMinifyEnabled = true` with ProGuard rules (`proguard-rules.pro`).
- **Resource Shrinking**: Enable `isShrinkResources = true` to remove unused vector assets and layouts.
- **Signing Config**: Configure production keystore securely via CI/CD environment variables.

---

## 2. Compilation & Verification

To verify that the application compiles and passes all unit and security checks:

```bash
# 1. Run unit and security tests
gradle testDebugUnitTest

# 2. Build debug APK
gradle assembleDebug

# 3. Build release bundle for Google Play
gradle bundleRelease
```

---

## 3. Environment Configuration

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Configure credentials in `.env`:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
3. In Google AI Studio / CI environments, supply credentials via the **Secrets panel**.

---

## 4. Release Checklist

| Check | Item | Status |
|---|---|---|
| ✅ | `applicationId` unique identifier configured | Complete |
| ✅ | Custom launcher adaptive icon configured | Complete |
| ✅ | `app_name` resource matches platform metadata | Complete |
| ✅ | Target SDK 34 (Android 14) / Min SDK 24 | Verified |
| ✅ | Zero hardcoded API keys in source files | Verified |
| ✅ | AST sandbox step limits & memory ceilings | Verified |
| ✅ | Offline local fallback for AI Code Coach | Verified |
| ✅ | Room Database version & pre-population | Verified |
