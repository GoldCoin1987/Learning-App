#!/usr/bin/env python3
"""Validate per-subtopic fragments and assemble a topic pack.

Usage:
  python assemble.py --id mathematics --title "Mathematics" --desc "..." [--requires a,b] [--version 1.0.0]

Reads fragments from  packs/_build/<id>/*.json  and writes  packs/<id>/pack.json.
"""
import argparse
import json
import os
import sys

HERE = os.path.dirname(__file__)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--id", required=True)
    ap.add_argument("--title", required=True)
    ap.add_argument("--desc", default="")
    ap.add_argument("--requires", default="")
    ap.add_argument("--version", default="1.0.0")
    ap.add_argument("--tags", default="")
    args = ap.parse_args()

    build_dir = os.path.join(HERE, args.id)
    out_dir = os.path.join(HERE, "..", args.id)
    out = os.path.join(out_dir, "pack.json")

    meta = {
        "schemaVersion": 2,
        "id": args.id,
        "title": args.title,
        "version": args.version,
        "description": args.desc,
        "requires": [r for r in args.requires.split(",") if r],
        "tags": [t for t in args.tags.split(",") if t],
    }

    errors, item_ids, lesson_ids, subtopics = [], {}, {}, []
    files = sorted(f for f in os.listdir(build_dir) if f.endswith(".json"))
    for fname in files:
        with open(os.path.join(build_dir, fname), encoding="utf-8") as fh:
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
                    choices, ai = item.get("choices"), item.get("answerIndex")
                    if not item.get("prompt"):
                        errors.append(f"{iid}: mcq missing prompt")
                    if not isinstance(choices, list) or len(choices) < 2:
                        errors.append(f"{iid}: mcq needs >=2 choices")
                    elif not isinstance(ai, int) or ai < 0 or ai >= len(choices):
                        errors.append(f"{iid}: mcq answerIndex out of range")
                else:
                    errors.append(f"{iid}: unknown type '{t}'")
                # Referenced diagrams hosted in this pack must exist on disk.
                marker = f"/packs/{args.id}/images/"
                for key in ("image", "backImage"):
                    url = item.get(key)
                    if url and marker in url:
                        rel = url.split(marker, 1)[1]
                        if not os.path.exists(os.path.join(out_dir, "images", rel)):
                            errors.append(f"{iid}: {key} -> missing images/{rel}")
        subtopics.append(st)

    subtopics.sort(key=lambda s: s.get("order", 0))
    n_lessons = sum(len(s["lessons"]) for s in subtopics)
    n_items = len(item_ids)
    n_flash = sum(1 for s in subtopics for l in s["lessons"] for i in l["items"] if i.get("type") == "flashcard")
    print(f"subtopics: {len(subtopics)}  lessons: {n_lessons}  items: {n_items}  "
          f"(flashcards: {n_flash}, mcq: {n_items - n_flash})")

    if errors:
        print(f"\n{len(errors)} VALIDATION ERROR(S):")
        for e in errors[:50]:
            print("  -", e)
        sys.exit(1)

    pack = dict(meta)
    pack["subtopics"] = subtopics
    os.makedirs(out_dir, exist_ok=True)
    with open(out, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(pack, fh, ensure_ascii=False, indent=2)
    with open(out, encoding="utf-8") as fh:
        json.load(fh)
    print(f"\nOK -> {os.path.normpath(out)} ({round(os.path.getsize(out)/1024,1)} KB), "
          f"validated, no duplicate ids.")


if __name__ == "__main__":
    main()
