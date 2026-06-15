#!/usr/bin/env python3
"""Validate the per-subtopic fragments and assemble packs/mathematics/pack.json."""
import json
import os
import sys

BUILD_DIR = os.path.join(os.path.dirname(__file__), "mathematics")
OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "mathematics")
OUT = os.path.join(OUT_DIR, "pack.json")

PACK_META = {
    "schemaVersion": 2,
    "id": "mathematics",
    "title": "Mathematics",
    "version": "1.0.0",
    "description": "Math spine refresher for EE/radar/software: algebra & dB math through "
                   "detection & estimation. Tiered easy to hard.",
    "requires": [],
    "tags": ["math", "foundations"],
}

errors = []
item_ids = {}
lesson_ids = {}
subtopics = []

files = sorted(f for f in os.listdir(BUILD_DIR) if f.endswith(".json"))
for fname in files:
    path = os.path.join(BUILD_DIR, fname)
    with open(path, encoding="utf-8") as fh:
        raw = fh.read()
    try:
        st = json.loads(raw)
    except json.JSONDecodeError as e:
        errors.append(f"{fname}: INVALID JSON - {e}")
        continue
    for key in ("id", "title", "order", "lessons"):
        if key not in st:
            errors.append(f"{fname}: missing subtopic key '{key}'")
    if not isinstance(st.get("lessons"), list) or not st["lessons"]:
        errors.append(f"{fname}: 'lessons' must be a non-empty list")
        continue
    for lesson in st["lessons"]:
        for key in ("id", "title", "order", "difficulty", "items"):
            if key not in lesson:
                errors.append(f"{fname}/{lesson.get('id','?')}: missing lesson key '{key}'")
        # Namespace lesson ids by subtopic so generic names ("foundations") stay unique.
        lid = f"{st['id']}-{lesson.get('id')}"
        lesson["id"] = lid
        if lid in lesson_ids:
            errors.append(f"duplicate lesson id '{lid}' in {fname} and {lesson_ids[lid]}")
        lesson_ids[lid] = fname
        for item in lesson.get("items", []):
            iid = item.get("id")
            if not iid:
                errors.append(f"{fname}: item missing id")
                continue
            if iid in item_ids:
                errors.append(f"DUPLICATE item id '{iid}' in {fname} and {item_ids[iid]}")
            item_ids[iid] = fname
            t = item.get("type")
            if t == "flashcard":
                if not item.get("front") or not item.get("back"):
                    errors.append(f"{iid}: flashcard missing front/back")
            elif t == "mcq":
                choices = item.get("choices")
                ai = item.get("answerIndex")
                if not item.get("prompt"):
                    errors.append(f"{iid}: mcq missing prompt")
                if not isinstance(choices, list) or len(choices) < 2:
                    errors.append(f"{iid}: mcq needs >=2 choices")
                elif not isinstance(ai, int) or ai < 0 or ai >= len(choices):
                    errors.append(f"{iid}: mcq answerIndex out of range")
            else:
                errors.append(f"{iid}: unknown type '{t}'")
    subtopics.append(st)

subtopics.sort(key=lambda s: s.get("order", 0))

n_lessons = sum(len(s["lessons"]) for s in subtopics)
n_items = len(item_ids)
n_flash = sum(1 for s in subtopics for l in s["lessons"] for i in l["items"] if i.get("type") == "flashcard")
n_mcq = n_items - n_flash

print(f"subtopics: {len(subtopics)}  lessons: {n_lessons}  items: {n_items}  "
      f"(flashcards: {n_flash}, mcq: {n_mcq})")

if errors:
    print(f"\n{len(errors)} VALIDATION ERROR(S):")
    for e in errors[:50]:
        print("  -", e)
    sys.exit(1)

pack = dict(PACK_META)
pack["subtopics"] = subtopics
os.makedirs(OUT_DIR, exist_ok=True)
with open(OUT, "w", encoding="utf-8", newline="\n") as fh:
    json.dump(pack, fh, ensure_ascii=False, indent=2)
# Re-read to confirm the written file parses.
with open(OUT, encoding="utf-8") as fh:
    json.load(fh)
size_kb = round(os.path.getsize(OUT) / 1024, 1)
print(f"\nOK -> {os.path.normpath(OUT)} ({size_kb} KB), validated, no duplicate ids.")
