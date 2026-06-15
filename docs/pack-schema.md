# Content Pack Schema (schemaVersion 1)

A **pack** is one curriculum module: metadata, prerequisite links, and a list of study items
(flashcards + multiple-choice questions). Plain JSON, UTF-8.

## Pack object

| Field | Type | Required | Notes |
|---|---|---|---|
| `schemaVersion` | int | yes | Currently `1`. |
| `id` | string | yes | Globally unique, stable, kebab-case (e.g. `math-foundations`). Used as the install key. |
| `title` | string | yes | Display name. |
| `version` | string | yes | Semver. Bump when content changes; re-import preserves SRS state. |
| `curriculumIndex` | string | no | e.g. `"01"` — sort/display order within the curriculum. |
| `description` | string | no | Short blurb. |
| `requires` | string[] | no | Pack `id`s that must be installed first (prerequisite edges). |
| `tags` | string[] | no | Free-form. |
| `items` | Item[] | yes | The study items. |

## Item object

Shared fields:

| Field | Type | Required | Notes |
|---|---|---|---|
| `id` | string | yes | Unique **within the pack**. |
| `type` | string | yes | `"flashcard"` or `"mcq"`. |
| `difficulty` | int | yes | Tier; `1` = easiest. Lower tiers are introduced/unlocked first. |
| `topic` | string | no | Sub-topic label for grouping. |
| `requires` | string[] | no | Item `id`s (within this pack) to learn first. |

Flashcard (`type: "flashcard"`):

| Field | Type | Required |
|---|---|---|
| `front` | string | yes |
| `back` | string | yes |
| `hint` | string | no |

Multiple choice (`type: "mcq"`):

| Field | Type | Required | Notes |
|---|---|---|---|
| `prompt` | string | yes | |
| `choices` | string[] | yes | 2+ options. |
| `answerIndex` | int | yes | 0-based index of the correct choice. |
| `explanation` | string | no | Shown after answering. |

## Catalog index (`catalog.json`)

```json
{
  "schemaVersion": 1,
  "name": "StudyForge Curriculum Catalog",
  "updated": "2026-06-15",
  "packs": [
    {
      "id": "math-foundations",
      "title": "Math Foundations",
      "version": "1.0.0",
      "curriculumIndex": "01",
      "description": "Complex numbers, phasors, transforms — the spine.",
      "requires": [],
      "itemCount": 6,
      "url": "https://raw.githubusercontent.com/YOU/studyforge-packs/main/math-foundations/pack.json",
      "sha256": null
    }
  ]
}
```

`url` is an absolute link to that pack's `pack.json`. `sha256` is optional integrity (reserved
for a future verification step). Host the catalog and packs on any static host.
