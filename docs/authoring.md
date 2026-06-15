# Authoring & Build Guide

How to build the app and add content packs — including from a fresh clone on another machine.
This repo is intended to be self-contained: app source, build pipeline, content source
(`curriculum/`), existing packs, and the catalog are all tracked.

## What's in the repo vs. what you install per machine

**In the repo (cloned with it):**
- App source (`app/`), Gradle config + wrapper (`gradlew`, `gradle/`).
- Pack schema (`docs/pack-schema.md`), this guide.
- Content **source material**: the curriculum lesson plans in `curriculum/` (the text packs are generated from).
- Content **build pipeline**: `packs/_build/assemble.py` and the per-sub-topic fragments in `packs/_build/<topic>/`.
- Built packs (`packs/<topic>/pack.json`) and the `packs/catalog.json` index.

**Installed per machine (NOT in the repo):**
- **Android Studio** + Android SDK (and its bundled JDK/JBR). Required to build the APK.
- **Python 3** — required to run the content assembler.
- `local.properties` (SDK path) — gitignored; Android Studio creates it automatically on first
  open, or create it manually:
  ```
  sdk.dir=C:/Users/<you>/AppData/Local/Android/Sdk
  ```

## Build the APK

- **Easiest:** open the repo folder in Android Studio; it syncs Gradle and creates the wrapper.
  Run on a device/emulator (min Android 8.0 / API 26).
- **CLI:** set `JAVA_HOME` to a JDK 17–21 (Android Studio's bundled JBR works), then:
  ```
  ./gradlew assembleDebug          # output: app/build/outputs/apk/debug/app-debug.apk
  ```
- You only need a new APK when the **app code or the pack schema** changes. Adding content packs
  does **not** require a rebuild.

## Add a content pack (no app rebuild needed)

A pack = one Topic → Sub-topics → Lessons → Items (see `docs/pack-schema.md`). Workflow:

1. **Generate per-sub-topic fragments** into `packs/_build/<topic-id>/NN-<subtopic>.json`. Each
   file is one Subtopic JSON object: `{id, title, order, lessons:[{id,title,order,difficulty,items:[...]}]}`.
   Source the content from the matching `curriculum/NN-*.md` lesson plan (one curriculum module →
   one sub-topic). Items are `flashcard` ({front,back,hint?}) or `mcq` ({prompt,choices,answerIndex,explanation}).
   (This is the step Claude automates: one generation agent per curriculum module.)
2. **Assemble + validate** (checks structure, duplicate ids, encoding; writes UTF-8 no BOM):
   ```
   python packs/_build/assemble.py --id <topic-id> --title "<Title>" \
       --desc "<one-line description>" [--requires <comma,separated,pack,ids>] [--tags a,b]
   ```
   Output: `packs/<topic-id>/pack.json`.
3. **Add a catalog entry** in `packs/catalog.json` (`id`, `title`, `version`, `description`,
   `requires`, `itemCount`, and the absolute `url` to the pack's `pack.json`).
4. **Commit and push.** The repo must stay **public** so the app can fetch packs anonymously
   from `raw.githubusercontent.com`. The installed app sees the new pack next time it opens
   "Download topics".

## How the app finds packs

`AppContainer.catalogUrl` (in `app/src/main/java/com/studyforge/app/StudyForgeApp.kt`) points at
`packs/catalog.json` on this repo's `raw.githubusercontent.com` URL. Change it there (and rebuild)
to host the catalog elsewhere.

## Topic order (dependency-aware `requires`)

01 mathematics → 02 analog-electronics → 03 digital → 04 embedded → 05 linux → 06 networking →
07 wave-propagation → 08 radar → 09 ai → 10 engineering. Set each pack's `requires` to the pack
ids it builds on (e.g. analog-electronics requires mathematics).
