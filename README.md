# StudyForge

A modular, offline-first Android study app built around **spaced repetition** and a
**prerequisite/difficulty graph** — you answer easy questions first and harder ones unlock
only as you master what they build on. Designed for long-horizon passive study (months → years)
with daily scheduled reminders.

The app is a **generic engine**. All study material lives in downloadable **content packs**
("plugins") — versioned JSON bundles of flashcards and multiple-choice questions plus the
rules for how they unlock. Add a topic by publishing a new pack to your catalog and downloading
it in-app — no app update required.

> **Why packs are data, not code:** Google Play forbids apps from downloading/executing new
> code at runtime, and dynamic code loading is a security/maintenance hazard. A "module" here is
> content + unlock rules, which delivers the same extensibility, stays Play-compliant, and lets
> anyone author packs without touching the app. See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Status

Working end-to-end (v0.2.0). Hierarchical schema (Topic → Sub-topic → Lesson → Item),
spaced repetition, scoped study, and daily reminders. First full content topic shipped:
**Mathematics** — 12 sub-topics, 48 lessons, 526 items (flashcards + MCQs), tiered easy→hard.
Installable APK: `dist/studyforge-v0.2.0-debug.apk`.

## Build & run

1. Install **Android Studio** (Ladybug or newer) with the Android SDK.
2. Open the `StudyForge/` folder in Android Studio — it will sync Gradle and generate the
   Gradle wrapper automatically. (CLI alternative: run `gradle wrapper` once, then `./gradlew assembleDebug`.)
3. Run on a device/emulator (min Android 8.0 / API 26).

## How the pieces fit

- **Content pack** — `docs/pack-schema.md`; sample at `packs/math-foundations/pack.json`.
- **Catalog index** — `packs/catalog.json`. Host this + the packs anywhere static (GitHub raw,
  GitHub Pages, S3). Set the URL in the app (default placeholder in `AppContainer.catalogUrl`).
- **Engine** — `app/src/main/java/com/studyforge/app/`:
  - `model/` — pack & catalog JSON models (kotlinx.serialization)
  - `data/` — Room DB + repositories + catalog download/import
  - `domain/` — SM-2 spaced-repetition scheduler + prerequisite/tier gating
  - `notifications/` — WorkManager daily reminder
  - `ui/` — Compose screens (Home / Catalog / Study)

## Authoring a pack

Copy `packs/math-foundations/pack.json`, change the `id`, add items, bump `version`, add an
entry to `catalog.json` pointing at its URL. That's the whole workflow.
