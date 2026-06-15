# Content Pack Schema (schemaVersion 2)

A **pack = one Topic**. It contains **sub-topics**, each containing **lessons**, each containing
study **items** (flashcards + MCQs). This three-level hierarchy lets you study an entire topic,
a single sub-topic, or just what's due — and it drives the easy→hard ordering.

```
Topic (pack)         e.g. "Mathematics"
└─ Sub-topic         e.g. "Algebra"
   └─ Lesson         e.g. "Ratios & Proportions"   (a difficulty tier)
      └─ Item        flashcard or multiple-choice question
```

**Progression order** is computed from `subtopic.order`, then `lesson.order`, then
`lesson.difficulty` — new material is always introduced in that sequence (easy first), throttled
by a per-session new-item cap. Between packs, `requires` enforces topic prerequisites.

## Pack (Topic)

| Field | Type | Required | Notes |
|---|---|---|---|
| `schemaVersion` | int | yes | `2`. |
| `id` | string | yes | Globally unique, stable, kebab-case (e.g. `mathematics`). |
| `title` | string | yes | Topic display name. |
| `version` | string | yes | Semver; bump on content change (re-import preserves SRS state). |
| `description` | string | no | |
| `requires` | string[] | no | Pack ids that must be installed first. |
| `tags` | string[] | no | |
| `subtopics` | Subtopic[] | yes | |

## Sub-topic

| Field | Type | Required | Notes |
|---|---|---|---|
| `id` | string | yes | Unique within the pack. |
| `title` | string | yes | |
| `order` | int | yes | Sequence within the topic (1-based). |
| `lessons` | Lesson[] | yes | |

## Lesson

| Field | Type | Required | Notes |
|---|---|---|---|
| `id` | string | yes | Unique within the pack. |
| `title` | string | yes | |
| `order` | int | yes | Sequence within the sub-topic (1-based). |
| `difficulty` | int | yes | Tier; `1` = easiest. |
| `content` | string | no | Readable lesson (markdown + LaTeX + images) shown in a "Read" view before the questions. Omit for question-only lessons. |
| `items` | Item[] | yes | |

A lesson with `content` gets a **Read** button (renders the lesson, then a "Start questions" button
runs that lesson's items). Lessons without `content` go straight to questions.

## Item

Shared: `id` (unique within pack), `type` (`"flashcard"` | `"mcq"`).

Flashcard: `front` (req), `back` (req), `hint` (opt).
MCQ: `prompt` (req), `choices` (req, 2+), `answerIndex` (req, 0-based), `explanation` (opt).

Optional images (any item): `image` (absolute URL, shown with the front/prompt) and `backImage`
(shown with a flashcard's answer). PNG/JPG/**SVG** supported; host alongside the pack, e.g.
`packs/<topic>/images/foo.svg`, and reference by its raw URL. Images are disk-cached (offline
after first view).

**LaTeX math:** any text field (`front`, `back`, `prompt`, `choices`, `explanation`, `hint`) may
contain LaTeX — inline `$...$` or block `$$...$$` — rendered via Markwon/JLatexMath. Basic
markdown (e.g. `**bold**`) also works. In JSON, every LaTeX backslash must be escaped: write
`$$e^{j\\theta} = \\cos\\theta + j\\sin\\theta$$`. Plain-text items still render fine.

## Example

```json
{
  "schemaVersion": 2,
  "id": "mathematics",
  "title": "Mathematics",
  "version": "1.0.0",
  "description": "Refresher math spine for EE/radar/software.",
  "requires": [],
  "subtopics": [
    {
      "id": "algebra",
      "title": "Algebra",
      "order": 1,
      "lessons": [
        {
          "id": "ratios",
          "title": "Ratios & Proportions",
          "order": 1,
          "difficulty": 1,
          "items": [
            { "id": "alg-rat-001", "type": "flashcard",
              "front": "Define a ratio.", "back": "A comparison of two quantities by division, a:b or a/b." },
            { "id": "alg-rat-002", "type": "mcq",
              "prompt": "If a:b = 2:3 and a = 8, what is b?",
              "choices": ["6", "12", "10", "16"], "answerIndex": 1,
              "explanation": "a/b = 2/3 → 8/b = 2/3 → b = 12." }
          ]
        }
      ]
    }
  ]
}
```

## Catalog index (`catalog.json`) — unchanged from v1

Lists available packs with `id`, `title`, `version`, `description`, `requires`, `itemCount`,
and an absolute `url` to each pack's `pack.json`. Hosted on any static host (the app fetches it
anonymously, so the host must be public).
