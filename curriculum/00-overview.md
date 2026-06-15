# Comprehensive Curriculum — EE / Radar / Software Refresh & Modernize

A complete, ground-up sweep across all topics — built so nothing is assumed and no
fundamental is skipped. The goal is to **get back in the groove** across the whole stack,
surface gaps you didn't know you had, and modernize toward a competitive job market.

**Learner:** BS Electronics Engineering · former Radar Technician · current Software Developer ·
retiring soon, targeting a competitive new role. Framing throughout is **refresh + modernize**:
brisk on fundamentals, thorough on coverage, with modern/job-market additions flagged inline.

**~150 modules across 10 lesson plans.** Each plan is self-contained with learning outcomes,
sequential modules (concepts → why-it-matters → hands-on → self-check), a capstone, pitfalls,
a self-assessment checklist, and canonical resources.

---

## The dependency graph (study order)

```
            01 MATH SPINE (run in parallel, gate nothing on finishing it)
                 |
   +-------------+-------------------------------------------+
   v             v                                           v
02 ANALOG --> 03 DIGITAL --> 04 EMBEDDED --------------> 08 RADAR (capstone)
   |                            |                          ^   ^   ^
   |                            v                          |   |   |
   |                       05 LINUX (host + embedded)------+   |   |
   |                            |                              |   |
   |                            v                              |   |
   |                       06 NETWORKING --------------------- + |
   v                                                            |
07 WAVE PROPAGATION (physics gate before radar) ----------------+
   ^
10 ENGINEERING PRACTICE — woven through all of it (incl. job-market readiness)
09 AI/ML — parallel track; lands as the radar/RF classification back-end in 08
```

## The plans

| # | Topic | File | Modules | Est. time | Notes |
|---|---|---|---|---|---|
| 01 | Math Foundations | [01-math-foundations.md](01-math-foundations.md) | 12 | ~10–14 wk | The spine — feeds everything; run in parallel |
| 02 | Analog Electronics | [02-analog-electronics.md](02-analog-electronics.md) | 15 | ~10–14 wk | Refresh to fluency; LTspice + bench labs |
| 03 | Digital Electronics | [03-digital-electronics.md](03-digital-electronics.md) | 14 | ~6–8 wk | + modern HDL/FPGA track |
| 04 | Embedded Systems | [04-embedded-systems.md](04-embedded-systems.md) | 15 | ~14–18 wk | Where your software strength pays off |
| 05 | Linux Systems | [05-linux-systems.md](05-linux-systems.md) | 19 | ~14–18 wk | Incl. embedded Linux on an SBC |
| 06 | Networking | [06-networking.md](06-networking.md) | 17 | ~14–20 wk | Wireless module on-ramps to wave propagation |
| 07 | Wave Propagation | [07-wave-propagation.md](07-wave-propagation.md) | 13 | ~10–13 wk | Physics gate before radar; Smith chart |
| 08 | Radar Systems | [08-radar.md](08-radar.md) | 15 | ~14–18 wk | **Capstone** — SDR + ML emphasis |
| 09 | AI / Machine Learning | [09-artificial-intelligence.md](09-artificial-intelligence.md) | 14 | ~16–22 wk | Most new; the modernization play |
| 10 | Engineering Practice | [10-engineering-practice.md](10-engineering-practice.md) | 16 | ~8–10 wk | Cross-cutting + job-market readiness |

## How to run it

- **Don't serialize all of it.** The honest sum is well over a year if done linearly. Instead:
  run **one spine phase at a time** (02 → 03 → 04 → 07 → 08), with **01 math, 05 Linux, 06
  networking, 09 AI, and 10 engineering as parallel light tracks**.
- **Knock-the-rust-off pass first.** Because you asked to surface unknown gaps, do a fast pass
  through each plan's **self-assessment checklist** before studying it — that tells you which
  modules are genuine refreshers (skim) vs. genuine gaps (dwell). That single move turns a
  multi-year program into a targeted one.
- **End every phase with its capstone.** The capstones are designed to become portfolio pieces;
  topic 10 reuses them for resume/GitHub/interview prep.
- **The grand finale** is the radar capstone (08): raw IQ → range-Doppler map → CFAR detection,
  processed on embedded Linux, with an ML classification layer (09). It exercises all ten plans
  at once and is the single strongest portfolio artifact for the target market.

## Suggested sequencing (phased, with parallel tracks)

1. **Phase A — reactivate the core:** 02 Analog + 03 Digital  (parallel: 01 Math, 05 Linux)
2. **Phase B — make it compute:** 04 Embedded  (parallel: 01 Math, 06 Networking, 09 AI start)
3. **Phase C — the physics:** 07 Wave Propagation  (parallel: 09 AI continues)
4. **Phase D — the capstone:** 08 Radar + the 09 AI classification layer
5. **Throughout:** 10 Engineering Practice — and convert each capstone into a portfolio piece.
