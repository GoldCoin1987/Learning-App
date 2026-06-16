# StudyForge Content Standard (v2 — "Learn Anything", beginner-first, visual-rich)

StudyForge is a **learn-anything app for complete beginners**. Every pack — physics, chemistry,
Japanese, radar, history — follows this standard. (Topics 01–06 and Wave Propagation were authored
to an older "EE/radar refresher" persona and are scheduled for a beginner rework; new content must
use THIS standard.)

## 1. Audience: a complete beginner

- Assume **no prior background** in the topic. The reader is a curious adult or capable teen.
- **Define every term on first use**; expand every acronym once (e.g. "voltage (electrical 'pressure')").
- **Motivate before mechanics:** open each reading with 1–2 plain sentences on *what this is and why
  anyone would care*, ideally a familiar real-world hook.
- **One idea at a time.** Short sentences. Everyday analogies before formal definitions.
- **No persona / no insider asides.** Never write "your radar-tech instincts" or assume a career.
  Write for anyone, anywhere.
- Math is welcome but **always explained in words** alongside the symbols. LaTeX (`$...$` / `$$...$$`)
  for anything beyond trivial; never leave a symbol undefined.
- Tier-1 (difficulty 1) lessons must be approachable cold; difficulty ramps gently 1→N.

## 2. Lesson reading (`content`) shape (~150–350 words, markdown)

1. **Hook** — what it is + why it matters, in plain language.
2. **Build** — teach the core idea with an analogy, then the precise version; introduce notation gently.
3. **Recap** — 1–2 sentences of the takeaway, leading into the questions.

## 3. VISUALS — diagrams are first-class (bumped up)

People learn best through visuals, so **aim for at least one diagram per lesson** (not per sub-topic).
- Each diagram is a simple, **clearly labeled** self-contained SVG: `viewBox`, white background, black
  strokes, basic shapes + short text labels. No reliance on external fonts/colors to convey meaning.
- Beginner-friendly: label axes, units, and parts; show one concept per diagram.
- Attach to an item via the `image` field (PNG/JPG/SVG URL). **Never** a lesson-level `diagram` field
  and **never** a standalone `type:"image"` item.
- Good diagram types: labeled apparatus/setups, before/after, graphs with labeled axes, cycles/flows,
  comparison side-by-sides, anatomy/parts call-outs, number lines/scales.

## 4. Questions (items) — MCQ-ONLY for all new content

- **All new questions are multiple choice.** Do NOT author flashcards in new content (a dedicated
  flashcard builder is planned for later; the app/schema still supports the `flashcard` type, and
  existing packs contain them until reworked).
- Each lesson has **≥ 8 MCQ items**.
- MCQs: exactly **4 string choices**, one correct (`answerIndex`, 0-based int), and an `explanation`
  that *teaches* (why right, and why the tempting wrong one is wrong) — not just "correct!".
- Write good distractors: plausible, reflecting common beginner misconceptions, not throwaways.

## 5. Strict schema (enforced by `packs/_build/assemble.py` — do not deviate)

- Subtopic: `{id, title, order(int), lessons[]}`
- Lesson: `{id, title, order(int), difficulty(INTEGER 1/2/3..., never a word), content, items[]}`
- Flashcard item: `{id, type:"flashcard", front, back, hint?}`
- MCQ item: `{id, type:"mcq", prompt, choices:[4 strings], answerIndex(int 0-3), explanation}`
- ids globally unique within the pack, prefixed with the sub-topic id.
- Valid JSON only (escape `\\` in LaTeX and `\\n` for newlines). The assembler validates types,
  duplicate ids, and that every referenced image file exists; a strict app-parser check runs before publish.

## 6. Publish workflow (per sub-topic, incremental)

Generate fragment → normalize stray image items → `assemble.py` (bump version `0.N.0`; `1.0.0` when the
topic is complete and drop any "(partial)" title) → strict type-check → update `packs/catalog.json`
(itemCount/version) → push. New topics need a `curriculum/<NN>-<topic>.md` outline first (module list +
key concepts per module) to serve as the generation source.
