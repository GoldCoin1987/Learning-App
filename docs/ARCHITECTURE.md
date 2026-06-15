# StudyForge — Architecture

## Design goals

1. **Offline-first.** Once a pack is downloaded, everything (study, scheduling, reminders) works
   with no network.
2. **Modular / extensible without app updates.** New topics arrive as downloadable content packs.
3. **Knowledge builds on knowledge.** Progression is enforced by a prerequisite graph (between
   packs) and difficulty tiers (within a pack).
4. **Passive over years.** Spaced repetition + daily reminders keep a light, sustainable cadence.
5. **Play-compliant.** No runtime code download; packs are pure data.

## Layered structure

```
ui/ (Compose)            Home · Catalog · Study  — thin, state from repositories
   |
domain/                  Sm2 (spaced repetition) · Gating (prereqs + tier unlock)
   |
data/                    PackRepository (download/import) · StudyRepository (session/grade)
   |  Room                packs, items (+ embedded SRS state)
model/                   Pack / Item / CatalogIndex  (kotlinx.serialization)
notifications/           WorkManager daily reminder (self-rescheduling)
```

Manual dependency injection via `AppContainer` (held by the `StudyForgeApp` Application) — no
Hilt, to keep the scaffold lean. Swap in Hilt later if the graph grows.

## The "plugin" model

A **pack** = one curriculum module. It declares:
- `requires`: other pack ids that must be installed first (the **between-pack** prerequisite edges).
- per-item `difficulty` tiers (1 = easiest): the **within-pack** progression.
- per-item `requires`: optional fine-grained intra-pack ordering.

The **catalog** (`catalog.json`) is a static index the app fetches from a configurable URL. It
lists available packs and where to download each. Hosting = any static file host; no backend.

## Progression rules (domain/Gating + StudyRepository.buildSession)

- A pack is **available to install** only when all packs in its `requires` are installed.
- Within an installed pack, the engine introduces new items in ascending `difficulty`. A tier is
  withheld until every item in lower tiers has been reviewed at least once (`reps > 0`) — i.e.
  you can't be served "hard" until you've actually attempted "easy".
- A daily session = **due reviews first** (SM-2 scheduling), then a capped number of **new items**
  from unlocked tiers.

## Spaced repetition (domain/Sm2)

Classic **SM-2**: per item we store easiness factor (EF, start 2.5), repetition count, interval
(days), and due date (epoch-day). Grades 0–5; <3 lapses the item (interval→1, reps→0), ≥3 grows
the interval by EF. MCQ correctness and flashcard self-rating both map to a grade.

> Upgrade path: **FSRS** (free spaced repetition scheduler) is more accurate than SM-2 and is the
> natural v2 — the `Sm2` object is isolated so it can be swapped without touching storage.

## State preservation on re-import

`ItemDao.insertAll` uses `OnConflictStrategy.IGNORE` keyed on (packId, itemId), so re-downloading
a newer pack version **adds new cards without wiping your existing SRS progress**. (A future
migration step can reconcile edited cards.)

## Why these libraries

- **Room** — robust offline relational store with reactive `Flow` queries.
- **WorkManager** — reliable deferred/periodic work that survives Doze and reboot, the right tool
  for daily reminders (vs. fragile PWA notifications or raw AlarmManager exact-alarm permissions).
- **kotlinx.serialization** — pack/catalog JSON ↔ models with no reflection.
- **OkHttp** — minimal HTTP client for catalog + pack download.
- **Jetpack Compose + Navigation** — modern declarative UI; strong portfolio signal.
