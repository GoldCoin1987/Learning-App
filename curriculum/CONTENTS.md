# StudyForge — Contents & Roadmap

_A learn-anything, beginner-first app. Status of every published topic, plus future work and the rework backlog._

> **Direction (2026-06):** pivoting from a personal EE/radar refresher to a general beginner app. New content follows `docs/content-standard.md` — complete-beginner readings, **MCQ-only** questions, **a diagram in (almost) every lesson**. Topics 01–07 were authored to the old EE/radar-refresher persona and are queued for a beginner rework.

## Status summary

| # | Topic | Status | Sub-topics | Lessons | Items |
|---|---|---|---|---|---|
| 01 | Mathematics | DONE v2.0.0 | 12 | 48 | 464 |
| 02 | Analog Electronics | DONE v2.0.0 | 15 | 59 | 556 |
| 03 | Digital Electronics | DONE v1.0.1 | 14 | 52 | 492 |
| 04 | Embedded Systems | DONE v1.0.1 | 15 | 50 | 452 |
| 05 | Linux Systems | DONE v1.0.0 | 19 | 67 | 607 |
| 06 | Networking | DONE v1.0.0 | 17 | 57 | 545 |
| 07 | Wave Propagation | DONE v1.0.0 | 13 | 43 | 409 |
| 11 | Physics | PARTIAL v0.1.0 | 1 | 4 | 35 |

**Published so far:** 106 sub-topics · 380 lessons · 3560 items across 8 topics (1 partial).

---
# Roadmap & future work

## A. Finish in-progress topics

**Physics** — 1/15 sub-topics done. Remaining:
- Module 2: Motion in a Straight Line
- Module 3: Vectors & Motion in Two Dimensions
- Module 4: Forces & Newton's Laws
- Module 5: Energy, Work & Power
- Module 6: Momentum & Collisions
- Module 7: Rotation & Torque
- Module 8: Gravity & the Solar System
- Module 9: Fluids
- Module 10: Oscillations & Waves
- Module 11: Sound
- Module 12: Heat & Temperature
- Module 13: Electricity & Magnetism
- Module 14: Light & Optics
- Module 15: A Peek at Modern Physics

## B. Finish the original core curriculum (still EE/radar-framed — author under the new beginner standard)

**08 Radar Systems** — 15 modules:
- Module 1: Orientation, IQ, and the complex-baseband mental model
- Module 2: Radar principles, history, and taxonomy
- Module 3: The radar range equation and SNR budget
- Module 4: Range, resolution, PRF, and the ambiguity trade space
- Module 5: The radar block diagram, end to end
- Module 6: Waveforms — pulsed, CW, FMCW, LFM/chirp, pulse compression, matched filtering
- Module 7: Doppler processing — MTI, pulse-Doppler, and the range-Doppler map
- Module 8: The ambiguity function and waveform design
- Module 9: Detection theory and CFAR
- Module 10: Tracking — filtering and data association
- Module 11: Clutter and interference
- Module 12: Antennas, scanning, phased arrays, and beamforming
- Module 13: Imaging radar — SAR and ISAR overview
- Module 14: Software-defined radio and FMCW hardware — the modern lab
- Module 15: Machine learning for radar — the competitive differentiator

**09 AI & Machine Learning** — 14 modules:
- Module 1: What AI/ML Is — The Landscape and the Mental Model
- Module 2: The ML Workflow — Data → Features → Train → Evaluate → Deploy
- Module 3: Classical ML I — Regression and the Geometry of Learning
- Module 4: Classical ML II — Trees, SVMs, k-NN, and Ensembles
- Module 5: Model Evaluation — Honesty, Metrics, and the Bias–Variance Tradeoff
- Module 6: Feature Engineering — Especially on Signals
- Module 7: Neural Networks From the Ground Up — Perceptron, MLPs, Backprop
- Module 8: PyTorch and the Deep Learning Toolchain
- Module 9: Deep Learning Architectures — CNNs, RNNs/LSTMs, Transformers
- Module 10: Signal & Radar ML Applications — Your Domain, Modernized
- Module 11: The Modern LLM Era — Transformers at Scale, Fine-Tuning, Prompting, RAG, Agents
- Module 12: Unsupervised Learning — Clustering and Dimensionality Reduction
- Module 13: MLOps Basics — From Notebook to Production
- Module 14: Edge AI & TinyML — Running Models on Embedded Targets

**10 Engineering Practice** — 16 modules:
- Module 1: Systems thinking & requirements
- Module 2: The engineering design process
- Module 3: Tradeoff analysis & decision-making
- Module 4: Specifications & documentation
- Module 5: Measurement & instrumentation discipline
- Module 6: Units, error analysis & significant figures
- Module 7: Debugging & troubleshooting methodology
- Module 8: Reliability, testing, verification & validation
- Module 9: Version control & reproducibility
- Module 10: Technical communication
- Module 11: Project management basics
- Module 12: Safety, standards & ethics
- Module 13: Modern & AI-assisted engineering workflows
- Module 14: Job-market readiness I — portfolio & GitHub presence
- Module 15: Job-market readiness II — resume, LinkedIn & positioning
- Module 16: Job-market readiness III — interviews & target roles

## C. New topics to add (beginner-first; each needs a `curriculum/<NN>-<topic>.md` outline first)
- Robotics
- Computer Architecture
- Chemistry
- Materials Science
- Japanese (language)
- Astronomy & Astrophysics
- Biology
- Earth & Environmental Science
- Thermodynamics & Heat
- Control Systems
- Optics & Photonics
- Data Science & Statistics
- Cybersecurity
- Quantum Computing
- Mechanical (statics/dynamics)
- _Humanities track (later): History; additional world languages._

## D. Rework backlog — bring EXISTING content to the beginner standard
Topics 01–07 (Math, Analog, Digital, Embedded, Linux, Networking, Wave Propagation) were written to the old "EE/radar refresher" persona. Per sub-topic (~90 passes total):
- Rewrite each lesson reading **beginner-first** (Hook→Build→Recap, define all terms, no career/persona asides).
- **Ease tier-1** lessons to assume zero background.
- **Convert flashcards → MCQ** (the app is moving MCQ-only for study; flashcards retired until a builder ships).
- Add diagrams toward the **≥1-per-lesson** target.

## E. App & product
- **Flashcard builder** — deferred feature; lets users make their own flashcards later.
- **Google Play Store release** — packaging the app for public distribution.
- **Micro-transactions** — content packs may become individually purchasable. Likely needs: price/entitlement fields in the catalog, Play Billing integration, a free tier vs paid packs, and per-pack licensing/ownership checks before download.
- Reliability: keep the strict pre-publish type-check; consider automated content QA (answer-key review) as scale grows.