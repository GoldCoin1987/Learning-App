# Engineering Practice (cross-cutting) — Lesson Plan

This module is the connective tissue of the entire curriculum. Where modules 01–09 teach *domains* (math, analog, digital, embedded, Linux, networking, wave propagation, radar, AI), this module teaches *how engineers actually work* across all of them: how you frame a problem, decide between options, measure reality without fooling yourself, document and version what you build, communicate it to other humans, and ship it safely and ethically. Almost none of it is new to you — you lived it as a Radar Technician and you live it now as a software developer. The goal here is **refresh + modernize + make it legible to a hiring manager**. You already troubleshoot hypothesis-first; you already read a spec; you already know ESD is not optional. We are going to name those skills, sharpen the rusty ones (calibration discipline, uncertainty math, modern git/CI), bolt on the parts that didn't exist or didn't matter in your earlier roles (AI-assisted workflows, Agile/Kanban vocabulary, GitHub-as-portfolio), and package the whole thing into a job-search engine.

Prerequisites: none formally, but this module assumes you are working through the other nine in parallel — its exercises repeatedly tell you to instrument, document, and version *the work you are already doing elsewhere*. Treat module 10 as the operating system that the other modules run on top of. The final third (job-market readiness) assumes your capstones from modules 02–09 exist or are in progress, because those capstones *are* your portfolio.

## Learning outcomes

By the end of this module you will be able to:

- Decompose an ambiguous request into a **requirements specification** with measurable, testable, traceable requirements, and distinguish functional from non-functional requirements and constraints.
- Run a full **engineering design process** from problem framing through verification & validation, and articulate which phase you are in at any moment.
- Perform structured **tradeoff analysis** (decision matrices, Pugh charts, cost/risk weighting) and defend a decision in writing.
- Use a DMM, oscilloscope, logic analyzer, spectrum analyzer, and VNA with **correct technique, calibration, and a stated measurement uncertainty** — and know when your instrument is lying to you.
- Carry **units, significant figures, and error/uncertainty** correctly through any calculation, and propagate uncertainty through a result.
- Debug *anything* (analog circuit, embedded firmware, Linux box, RF link, ML pipeline) with a **systematic, hypothesis-driven, binary-search methodology** instead of flailing.
- Reason about **reliability, testing, and V&V**: write tests, define acceptance criteria, estimate MTBF, and distinguish verification from validation.
- Use **git** fluently for real collaborative, reproducible work — branches, rebases, bisect, hooks, CI — not just `add/commit/push`.
- Produce clear **technical communication**: design docs, README files, diagrams (block, schematic, sequence, state, timing), and a confident spoken explanation.
- Run small projects with **Agile/Kanban** vocabulary and produce credible **estimates**.
- Apply **safety and standards** discipline (ESD, electrical safety, RF exposure limits, lockout/tagout) and name the relevant standards bodies.
- Reason about **engineering ethics** using a recognized code (IEEE/NSPE) and real cases.
- Integrate **AI-assisted engineering** into your daily workflow responsibly (coding assistants, datasheet/RAG search, review, where to *not* trust it).
- Assemble a **portfolio, GitHub presence, and resume** that frame your radar + software hybrid as a feature, prep for interviews, and target specific roles.

## Module breakdown

### Module 1: Systems thinking & requirements

- **Concepts:**
  - Systems vs. components: emergent behavior, interfaces, boundaries, the "system of interest" and its operating environment.
  - Stakeholders and needs elicitation; turning a vague "I want X" into stated needs.
  - Requirements taxonomy: functional, non-functional (performance, reliability, power, size, cost, EMC), constraints, and assumptions.
  - The "good requirement" test: unambiguous, verifiable, atomic, traceable, feasible, necessary. Avoiding solution-bias in requirements ("shall display" not "shall use a 7-segment LED").
  - "Shall / should / may" language; acceptance criteria; requirements traceability matrix (req → design → test).
  - Interface control: ICDs, APIs, pinouts, protocol contracts as interfaces between subsystems.
  - Black-box vs. white-box thinking; the V-model (requirements ↔ verification mirror it).
  - Budgets as requirements decomposition: power budget, mass budget, timing budget, **link budget** (the radar/RF one you already know), error budget.
- **Why it matters / connections:** Every other module is a "system of interest" nested in a bigger one. A **radar (08)** is a system whose link budget (07 wave propagation) flows down to subsystem requirements for the **analog (02)** front-end, **digital/DSP (03)**, and **embedded (04)** controller. An **AI model (09)** has accuracy/latency/throughput requirements that flow down to **Linux (05)** deployment and **networking (06)** constraints. Requirements discipline is what stops you from building the wrong thing well.
- **Hands-on / exercises:**
  - Take one capstone you're building elsewhere (say the radar or the embedded project) and write a one-page requirements spec: 8–12 numbered requirements using "shall," each with an acceptance test.
  - Build a traceability matrix linking 3 requirements → design decision → the test that proves it.
  - Write a link budget *as a requirements flow-down*: top-level "detect target at range R" → required SNR → required Tx power / antenna gain / receiver NF.
- **You've got it when:** You can take any "build me a thing" sentence and produce a numbered, testable spec, and explain for each requirement how you'd verify it.

### Module 2: The engineering design process

- **Concepts:**
  - The canonical loop: define problem → research/constraints → ideate → analyze/model → select → prototype → test → iterate → document → deploy → maintain/retire.
  - The **V-model** explicitly: requirements → architecture → detailed design → implementation, mirrored on the way up by unit → integration → system → acceptance verification.
  - Design reviews: PDR (preliminary), CDR (critical), peer review, design gates. What gets reviewed and who attends.
  - Prototyping strategy: breadboard → dev board → custom PCB; spike → MVP → product. Throwaway vs. evolutionary prototypes.
  - Modeling before building: back-of-envelope, simulation (SPICE, EM, numerical), digital twins.
  - Margin and derating; design for the worst case across the operating envelope.
  - Design for X (DfX): manufacturability, testability, reliability, maintainability, cost.
  - Failure thinking up front: FMEA (Failure Modes and Effects Analysis) as a design tool, not an afterthought.
- **Why it matters / connections:** This is the meta-process the whole curriculum follows. **Analog (02)** uses SPICE before soldering; **digital (03)** simulates HDL before synthesis; **embedded (04)** prototypes on a dev kit; **AI (09)** trains on a held-out set before deployment. The V-model's right side *is* module 7 of this plan (V&V).
- **Hands-on / exercises:**
  - Draw the V-model for your radar capstone, filling in a real artifact at every node.
  - Run a 10-line FMEA on the embedded capstone: list 5 failure modes, severity × occurrence × detection = RPN, and a mitigation for the top one.
  - Write a one-page "design rationale" for a real choice (e.g., why an FPGA vs. MCU for the DSP front-end).
- **You've got it when:** You can name the phase you're in on any project and the artifact each phase must produce, and you instinctively ask "what's the worst case?" and "how will this fail?" during design.

### Module 3: Tradeoff analysis & decision-making

- **Concepts:**
  - Making tradeoffs explicit: there is no "best," only "best given weighted criteria."
  - **Decision matrix / weighted scoring**; the **Pugh matrix** (concept selection against a datum).
  - Cost of decision: reversible (two-way door) vs. irreversible (one-way door) decisions — decide fast on the former.
  - Constraints triangle (scope/time/cost/quality) and where engineering ones bite: power vs. performance, latency vs. throughput, accuracy vs. cost, resolution vs. SNR, flexibility vs. optimization.
  - Risk-weighted thinking: expected value, sensitivity analysis ("which assumption, if wrong, changes the answer?").
  - Avoiding bias: anchoring, sunk cost, premature optimization, gold-plating, bikeshedding.
  - Documenting decisions: the **ADR (Architecture/Any Decision Record)** — context, options, decision, consequences.
- **Why it matters / connections:** Radar design *is* tradeoff management — pulse width vs. range resolution vs. SNR (08, 07). **AI (09)** model-size vs. latency vs. accuracy. **Embedded (04)** RAM vs. features vs. cost. Hiring managers probe exactly this: "tell me about a hard technical tradeoff you made."
- **Hands-on / exercises:**
  - Build a weighted decision matrix to pick a microcontroller (or cloud GPU instance) for a real capstone; weight the criteria first, score second, and note whether the winner changes if you tweak weights.
  - Write 3 ADRs for decisions across your capstones; commit them to the repo under `/docs/adr/`.
- **You've got it when:** You never say "X is better" without "...for these weighted criteria," and you can produce a decision record on demand.

### Module 4: Specifications & documentation

- **Concepts:**
  - Document types and when each is used: requirements spec, design doc / RFC, ICD, test plan/report, README, runbook, datasheet, application note, user manual, ADR, changelog.
  - Reading a **datasheet** like an engineer: absolute max ratings vs. recommended operating conditions, typ/min/max, test conditions, characterization graphs, errata.
  - Docs-as-code: Markdown, AsciiDoc, version-controlled docs, generated docs (Doxygen, Sphinx, docstrings, `cargo doc`).
  - The README contract: what it does, how to build/run, dependencies, examples, license.
  - Writing for the reader: audience-first, inverted pyramid (conclusion first), one idea per paragraph, active voice, define acronyms once.
  - Diagrams as documentation (handed off to Module 9 communication, but specified here): which diagram for which purpose.
  - Traceability and single-source-of-truth; avoiding doc drift via generation and CI checks.
- **Why it matters / connections:** Every module produces artifacts that need a spec or README. A **datasheet** is the universal interface in **analog (02)/digital (03)/embedded (04)**. Good docs are also a *portfolio asset* — a clean README is the first thing a hiring engineer reads.
- **Hands-on / exercises:**
  - Reverse-engineer a real op-amp or MCU datasheet: extract 10 parameters and state the test conditions for each.
  - Write a production-grade README for one capstone (build, run, example, diagram, license).
  - Write a 2-page design doc / RFC for a feature, using the "context → options → decision → plan → risks" template.
- **You've got it when:** Someone who has never seen your project can build and run it from your README alone, and you can pull a guaranteed (not typical) spec out of any datasheet.

### Module 5: Measurement & instrumentation discipline

- **Concepts:**
  - **The cardinal rule:** the instrument and the act of measuring perturb the thing measured (loading, capacitance, ground loops). Know your instrument's input impedance, bandwidth, and the probe's effect.
  - **DMM:** true-RMS vs. average-responding, input impedance and circuit loading, burden voltage in current mode, resolution vs. accuracy (counts, % of reading + counts), 4-wire (Kelvin) low-resistance measurement, CAT safety ratings.
  - **Oscilloscope:** bandwidth (and the ×5 rule for rise time), sample rate vs. Nyquist, aliasing, real-time vs. equivalent-time, memory depth, **probe compensation**, 10× vs. 1× probes, probe loading, ground-lead inductance (the "ground spring"), triggering (edge/pulse/serial), differential & current probes, ground-referenced vs. isolated measurements (mains = use a differential probe, never float the scope).
  - **Logic analyzer:** thresholds, setup/hold sampling, state vs. timing mode, protocol decode (I²C/SPI/UART), correlating with the scope.
  - **Spectrum analyzer:** RBW/VBW, sweep time, reference level and input mixer overload, noise floor and DANL, averaging vs. peak hold, reading dBm/dBc, harmonic vs. spurious vs. intermod products.
  - **VNA:** S-parameters (S11/S21...), the **SOLT/SOLR calibration** that moves the reference plane to your DUT, port extension, Smith chart reading, return loss / VSWR / insertion loss, time-domain gating.
  - **Calibration & traceability:** zeroing/nulling, self-cal vs. lab cal, cal stickers and intervals, NIST-traceable standards, warm-up time, the difference between *accuracy*, *precision*, and *resolution*.
  - **Measurement uncertainty:** every measurement is a value ± an uncertainty with a coverage factor; combine instrument spec + technique + environment.
- **Why it matters / connections:** This is the most "Radar Technician" module — it's where your bench instincts live, and it's the most refresh-and-modernize. It underpins **analog (02)** (scope/DMM), **digital (04)** (logic analyzer), and **wave propagation/radar (07/08)** (spectrum analyzer + VNA). Modern roles in RF/test still hire hard on this. It also feeds Module 6's error analysis.
- **Hands-on / exercises:**
  - Probe-comp ritual: under/over/correctly compensate a 10× probe on a 1 kHz square wave and capture all three; explain the cause.
  - Show aliasing live: feed a signal above Nyquist for the current timebase and watch the false low-frequency tone appear.
  - Measure the same resistor with 2-wire vs. 4-wire and explain the difference at low ohms.
  - On a spectrum analyzer (or a SDR-based one like an RTL-SDR + software), narrow RBW and watch the noise floor drop and a spur emerge.
  - Run a full SOLT cal on a VNA (or a NanoVNA) and measure return loss of an antenna/cable; redo it without cal and compare.
  - Write a measured result correctly: `12.47 V ± 0.03 V (k=2)` with the basis stated.
- **You've got it when:** Before trusting any number, you automatically ask "what's my bandwidth, my loading, my reference plane, and my uncertainty?" — and you can defend the digits you reported.

### Module 6: Units, error analysis & significant figures

- **Concepts:**
  - SI base/derived units, prefixes, and **dimensional analysis** as a sanity check (if the units don't work, the equation is wrong).
  - The dB family decoded: dB, dBm, dBW, dBc, dBi/dBd, dB-Hz; when to add vs. multiply; 3 dB = ×2 power, 10 dB = ×10, 20 dB = ×10 voltage.
  - **Significant figures**: rules for +/−/×/÷, when to round (only at the end), false precision as a red flag.
  - Accuracy vs. precision vs. resolution (again, formalized): systematic (bias) vs. random error.
  - **Uncertainty propagation**: add in quadrature for independent errors; relative-error rules for products/quotients; partial-derivative (Taylor) method for arbitrary functions.
  - Statistics of measurement: mean, standard deviation, standard error of the mean, confidence intervals, distinguishing a real effect from noise.
  - Type A (statistical) vs. Type B (other) uncertainty (GUM framework, lightweight).
  - Fermi estimation / order-of-magnitude reasoning as a constant-companion skill.
- **Why it matters / connections:** Underpins **math (01)** and every quantitative claim in **02–09**. The dB family is the lingua franca of **wave propagation (07)** and **radar (08)** link budgets. Significant figures and uncertainty are how you avoid the classic intern mistake of reporting "73.428596 dB" from a ±1 dB instrument.
- **Hands-on / exercises:**
  - Convert a link budget end-to-end in dB and verify it matches the linear computation.
  - Propagate uncertainty through `P = V²/R` given uncertainties in V and R; report P ± δP.
  - Take 30 repeated readings, compute mean ± standard error, and state how many you'd need to halve the uncertainty.
  - A Fermi drill: estimate the data rate / storage of one capstone to one significant figure with no references.
- **You've got it when:** You never write more digits than you can defend, you compute "in dB" without a calculator for the round numbers, and you can propagate error through a formula.

### Module 7: Debugging & troubleshooting methodology

- **Concepts:**
  - The mindset: a bug is a falsified assumption; debugging is *science* — observe, hypothesize, predict, test, conclude. Resist random changes.
  - **Binary search the fault**: halve the system (signal chain, code, network path) and ask "is it broken before or after this point?" — your radar signal-tracing instinct generalized.
  - Reproduce first; minimize the reproduction (minimal reproducible example); change one variable at a time.
  - The classic ladders: read the error message; check power/ground/clock/connections first (the "is it plugged in" tier); rubber-duck explanation; differential debugging ("what changed?"); known-good substitution.
  - Instrumentation of software: logging levels, `printf`/tracing, debuggers (gdb/lldb, breakpoints, watchpoints), core dumps, `strace`/`ltrace`, profilers, `git bisect` to find the offending commit.
  - Heisenbugs, race conditions, the observer effect (adding a print changes timing); intermittent vs. deterministic faults.
  - Root-cause analysis: **5 Whys**, fishbone (Ishikawa) diagram; fix the cause not the symptom; write it up so it doesn't recur.
  - Knowing when to stop and ask / escalate; time-boxing.
- **Why it matters / connections:** This is your single most transferable skill from radar tech work and the one interviewers love. It applies identically to **analog (02)** dead circuits, **digital (03)** HDL, **embedded (04)** firmware, **Linux (05)** systems, **networking (06)** dropped packets, **radar (08)** no-detect, and **AI (09)** model-not-learning. `git bisect` (Module 8) is debugging-as-binary-search in version space.
- **Hands-on / exercises:**
  - Take a deliberately broken program/circuit and keep a written debug log: hypothesis → test → result, until solved.
  - Use `git bisect` to find an introduced bug across 15+ commits.
  - Use a logic analyzer + scope to trace a misbehaving I²C/SPI bus to the exact failing transaction.
  - Do a 5 Whys write-up on a real bug you've hit, ending at a root cause and a prevention.
- **You've got it when:** Faced with any broken system you reach for "reproduce, then binary-search the fault, one variable at a time" reflexively, and you can explain a past debug as a clean hypothesis chain.

### Module 8: Reliability, testing, verification & validation

- **Concepts:**
  - **Verification vs. validation:** "did we build it right" (meets the spec) vs. "did we build the right thing" (meets the need). The two sides of the V-model.
  - Test levels: unit, integration, system, acceptance, regression, smoke; the test pyramid.
  - Software testing: assertions, test frameworks (pytest, GoogleTest, etc.), fixtures/mocks, coverage (and its limits), property-based testing, fuzzing, test-driven development.
  - Hardware/embedded testing: bring-up checklists, HIL (hardware-in-the-loop), built-in self-test (BIST), boundary scan/JTAG, environmental testing.
  - **Reliability engineering:** MTBF/MTTR/availability, the bathtub curve (infant mortality → useful life → wear-out), burn-in, derating, redundancy (and single points of failure), FMEA (revisited from Module 2).
  - Statistical acceptance: confidence/coverage, corner cases, worst-case analysis (WCA), Monte Carlo tolerance analysis.
  - **Continuous Integration:** automated build + test on every push (GitHub Actions), gating merges on green, linting/static analysis.
  - Reproducibility's role in V&V: a test you can't rerun isn't a test.
- **Why it matters / connections:** This *is* the right arm of the V-model from Module 2. Tests are the executable form of the acceptance criteria from Module 1. **AI (09)** has its own validation flavor (train/validation/test split, held-out evaluation, overfitting = "passed verification, failed validation"). CI ties into git (Module 8 → 9). Reliability/MTBF is core to **radar/defense (08)** systems work.
- **Hands-on / exercises:**
  - Write a unit test suite for one software capstone module; reach meaningful (not vanity) coverage; add one property-based test.
  - Stand up a GitHub Actions workflow that builds and tests on every push, with a green/red badge in the README.
  - Compute availability for a two-unit system given MTBF/MTTR, with and without redundancy.
  - Write an acceptance test plan that maps 1-to-1 onto your Module 1 requirements.
- **You've got it when:** You can say which kind of test proves which requirement, you reflexively distinguish verification from validation, and every capstone has CI that runs its tests automatically.

### Module 9: Version control & reproducibility (git)

- **Concepts:**
  - Git mental model: snapshots not diffs; the three areas (working tree, index/staging, repo); commits as a DAG; HEAD, branches, tags as pointers.
  - Everyday flow done *well*: atomic commits, good messages (imperative subject ≤50 chars, body explains *why*), `.gitignore`, staging hunks (`git add -p`).
  - Branching & integration: feature branches, merge vs. **rebase** (and when each), fast-forward, resolving conflicts, `merge --no-ff` for readable history.
  - Collaboration: remotes, fork + pull request flow, code review etiquette, protected branches, conventional commits, semantic versioning (SemVer) and tags/releases.
  - Power tools: `git bisect` (→ Module 7), `git stash`, `git reflog` (your undo safety net), `git cherry-pick`, `git blame`, interactive history cleanup, hooks (pre-commit linting).
  - **Reproducibility beyond git:** pinned dependencies (lockfiles, `requirements.txt`/`Pipfile.lock`, `Cargo.lock`), virtual environments, containers (Docker) for "works on my machine" elimination, environment capture, seeded randomness for experiments, data/model versioning (DVC, model cards) for ML.
  - Monorepo vs. polyrepo; README + LICENSE + CONTRIBUTING hygiene.
- **Why it matters / connections:** Git is the substrate the entire portfolio (Modules 13–16) sits on, and the single most-assumed tool in any modern software/embedded role. Reproducibility links to V&V (Module 8) and to **Linux (05)** (containers, environments) and **AI (09)** (experiment/model versioning). Your **embedded (04)** firmware and **DSP (03)** code all live in git.
- **Hands-on / exercises:**
  - Take an existing capstone and rewrite its history into clean, atomic commits with proper messages (practice on a copy).
  - Do a full PR flow against yourself: branch → commit → push → open PR → review your own diff → merge with `--no-ff`.
  - Deliberately create and resolve a merge conflict; then redo it as a rebase.
  - Containerize one capstone so a stranger can `docker run` it and get identical results.
  - Recover a "lost" commit using `git reflog`.
- **You've got it when:** You use branches/rebase/bisect without googling, your commit history reads like a story, and anyone can reproduce your result from a clean clone (or container) with no hidden state.

### Module 10: Technical communication (writing, diagrams, presenting)

- **Concepts:**
  - Writing: audience analysis, **BLUF** (bottom line up front), the inverted pyramid, structure (problem → approach → result → implication), concision, active voice, editing ruthlessly.
  - The core engineering documents as communication (cross-link Module 4): design doc, report, email that gets a decision, status update.
  - **Diagrams — the right one for the job:**
    - Block diagram (system architecture, signal flow) — your radar block diagrams.
    - Schematic (analog/digital circuits, 02/03).
    - Sequence diagram (interactions/protocols, 06).
    - State machine diagram (embedded/control, 04).
    - **Timing diagram** (digital buses, 03/04).
    - Flowchart / data-flow (software, AI pipelines, 09).
    - Smith chart / spectrum plot / constellation (RF, 07/08).
  - Diagrams-as-code: **Mermaid**, PlantUML, Graphviz, draw.io; LaTeX/TikZ for publication; keeping diagrams in the repo.
  - Data visualization done honestly: axis labels with units, no truncated axes to exaggerate, log scale when appropriate, error bars.
  - Presenting: structuring a talk, one idea per slide, slides as visual aid not script, whiteboarding a design live (interview-critical), reading the room, handling questions, demoing without dying.
  - Writing for the job hunt is communication too: resume bullets, cover notes, LinkedIn — handled in Modules 13–16 but the *skill* is here.
- **Why it matters / connections:** Communication is the multiplier on every other skill — the engineer who can explain the tradeoff (Module 3) and whiteboard the architecture (Module 1) gets hired and promoted. Diagrams are the shared language across all of 02–09. This is also where the "retiring, re-entering the market" learner gains the most leverage: senior judgment + clear communication is exactly the differentiator over junior candidates.
- **Hands-on / exercises:**
  - Take one capstone and produce: a 1-page block diagram (Mermaid, in the repo), a 1-page written design summary (BLUF), and a 5-minute spoken explanation recorded on your phone — then watch it and re-record.
  - Draw a timing diagram for an SPI transaction and a state diagram for the embedded capstone's control loop.
  - Rewrite one dense paragraph from a datasheet or paper into plain language for a non-specialist manager.
  - Whiteboard (on paper, out loud, timed) the architecture of your radar capstone as if in an interview.
- **You've got it when:** You can explain any capstone in 60 seconds (manager), 5 minutes (peer engineer), and 30 minutes (deep dive), and pick the correct diagram type without thinking.

### Module 11: Project management basics (Agile/Kanban, estimation)

- **Concepts:**
  - Why process exists: managing scope, time, and uncertainty across people; the iron triangle (scope/time/cost) and the lie of "all three fixed."
  - **Agile** values vs. waterfall; when each fits (waterfall/V-model for safety-critical/defense hardware, Agile for software/exploratory).
  - **Scrum** vocabulary: sprint, backlog, user story ("as a ___ I want ___ so that ___"), story points, sprint planning, daily standup, review, retrospective, velocity, the roles (PO, SM, team).
  - **Kanban** (likely your best personal-project fit): visualize work, WIP limits, pull system, cycle time, cumulative flow — a simple board (To Do / Doing / Done) for solo capstone work.
  - **Estimation:** why it's hard (cone of uncertainty), relative sizing / story points / planning poker, three-point (PERT) estimation, padding for the unknown, tracking actual vs. estimate to calibrate.
  - Breaking work down: epics → stories → tasks; definition of "done"; the value of small slices.
  - Tools: GitHub Projects/Issues (ties your PM to your repo), Trello, Jira (name-recognition for interviews).
  - Risk management: a simple risk register (likelihood × impact, owner, mitigation).
- **Why it matters / connections:** You will be asked "are you familiar with Agile?" in nearly every software/embedded interview — this gets you fluent in the vocabulary even if you've worked in a waterfall defense world. Running your capstones on a **GitHub Projects** Kanban board makes your *process* visible in your portfolio. Estimation discipline ties to the cone of uncertainty in design (Module 2).
- **Hands-on / exercises:**
  - Put one capstone on a GitHub Projects Kanban board with WIP limits; write its work as user stories with a definition of done.
  - Estimate the remaining capstone work with three-point estimates; track actuals for two weeks; compute your personal "fudge factor."
  - Write a 5-row risk register for a capstone.
- **You've got it when:** You can speak Scrum/Kanban fluently in an interview, you size work in small slices with a definition of done, and you know your own estimates run optimistic by roughly a known factor.

### Module 12: Safety, standards & ethics

- **Concepts:**
  - **ESD:** the physics (CDM/HBM models, kV from walking on carpet kills a CMOS gate you can't see), wrist straps, mats, ionizers, ESD-safe handling, the relevant practice (ANSI/ESD S20.20). Cross-link your embedded/analog bench habits.
  - **Electrical safety:** the danger is *current through the heart*, not voltage per se; let-go threshold; isolation; never work mains alone; one-hand rule; discharge stored energy (capacitors, the "look before you touch" on power supplies); fusing; GFCI; lockout/tagout (LOTO).
  - **RF safety / exposure:** non-ionizing thermal hazards, power density limits, near-field vs. far-field, FCC MPE limits and ICNIRP guidelines, keepout zones near transmitters/antennas — directly relevant to your radar background and to RF roles.
  - Other bench hazards: laser safety classes, battery (Li-ion) handling, soldering fumes/lead, high-temperature.
  - **Standards bodies — know who owns what:** IEEE (e.g., 802.x networking), IEC, ISO, ANSI, NIST (metrology/cyber), ITU (spectrum), FCC (US spectrum/EMC), ETSI (EU), JEDEC (semiconductors), MIL-STD (defense — your world), RTCA/DO-178C & DO-254 (avionics), IPC (PCB/assembly), UL (product safety), 3GPP (cellular). EMC/EMI compliance (FCC Part 15, CE marking) as a real ship gate.
  - Functional safety overview: IEC 61508 / ISO 26262 (automotive) / DO-178C (aero) and the concept of safety integrity levels — name-level familiarity for systems roles.
  - **Engineering ethics:** the **IEEE Code of Ethics** and **NSPE Code** (paramount: public safety/health/welfare); whistleblowing; conflicts of interest; honest data (no fudging results — ties to Module 6 integrity); responsible disclosure (security); dual-use/defense ethics (relevant to a radar background); the famous cases (Challenger O-rings, Therac-25, Hyatt Regency walkway, VW emissions) and what each teaches.
  - AI-specific ethics (bridge to Module 13/09): bias, transparency, accountability, when *not* to automate a decision.
- **Why it matters / connections:** Safety discipline is a hiring signal and a life-or-limb matter on any bench (02/04) or RF site (07/08). Standards literacy is exactly what separates a hobbyist from an engineer in interviews, and your **MIL-STD/defense** exposure is an asset to name explicitly. Ethics is the floor under everything; data integrity directly reinforces Modules 5–6.
- **Hands-on / exercises:**
  - Write your own one-page bench-safety SOP covering ESD, electrical, and (if applicable) RF; pin it above your bench.
  - Map 3 of your capstones to the standards that would govern them if productized (e.g., FCC Part 15 for an emitter, IPC for the PCB).
  - Read the IEEE Code of Ethics; write a paragraph on how it would apply to a real dilemma in your past or hypothetical work.
  - Case study: read a short writeup of the Therac-25 and list the engineering-practice failures (requirements, testing, V&V, communication) that map to Modules 1–11.
- **You've got it when:** Safe handling is automatic, you can name the right standards body for a given domain, and you can reason through an ethics dilemma with a named code rather than vibes.

### Module 13: Modern & AI-assisted engineering workflows

- **Concepts:**
  - The modern toolchain you should be fluent in: a real editor/IDE (VS Code) + extensions, the terminal as a power tool, package managers, formatters/linters as enforced standards, pre-commit hooks, containers (Docker), reproducible environments — much of it threaded through earlier modules, gathered here as "this is how a 2026 engineer's desk actually looks."
  - **AI-assisted coding:** assistants (Claude Code, Copilot, Cursor-style tools) for scaffolding, refactoring, test generation, explaining unfamiliar code, translating between languages; *prompting an LLM like a junior engineer you must review*.
  - **AI for the rest of engineering:** datasheet/standard Q&A and RAG over docs, summarizing papers, drafting design docs and READMEs, rubber-ducking a debug, generating diagrams (Mermaid from a description), boilerplate test cases.
  - **The discipline — where AI lies:** hallucinated APIs/pin numbers/part specs, confidently wrong math and units (you *must* re-derive — ties to Module 6), subtly wrong concurrency/security code, stale knowledge vs. a datasheet of record. Always verify against primary sources; never paste secrets; treat output as a draft.
  - Calibrating trust by task: great for boilerplate/explanation/search, dangerous for novel quantitative reasoning and safety-critical code without verification.
  - Provenance, licensing, and IP hygiene of AI-generated code; company policies.
  - The meta-skill: AI raises the floor and rewards engineers with strong *judgment* — which is exactly the senior judgment this learner has. Frame AI as a force-multiplier on experience, not a replacement for it.
- **Why it matters / connections:** This is the biggest "modernize" delta from when you trained. It accelerates literally every other module (faster debugging in 7, faster docs in 4/10, faster boilerplate tests in 8) *if* you keep the verification discipline from Modules 5–6. It's also an interview talking point: "I use AI tools but I verify their output against primary sources" is exactly what a thoughtful employer wants to hear.
- **Hands-on / exercises:**
  - Use an AI assistant to generate a unit-test suite for a capstone, then audit every test — find at least one that's wrong or vacuous.
  - Ask an AI for an RF or signal-processing formula, then independently verify units and a limiting case (catch it if it's wrong).
  - Set up VS Code with formatter + linter + pre-commit hook on a capstone repo.
  - Generate a Mermaid architecture diagram from a prose description, then correct it by hand.
- **You've got it when:** AI is a daily accelerant in your workflow *and* you reflexively verify its quantitative and API claims against primary sources, and you can articulate the trust boundary in an interview.

### Module 14: Job-market readiness I — portfolio & GitHub presence

- **Concepts:**
  - **The portfolio thesis:** your curriculum capstones (02 analog, 03 digital, 04 embedded, 05 Linux, 06 networking, 07/08 RF/radar, 09 AI) *are* the portfolio. Curate 3–5 that tell a coherent "RF/DSP/embedded + software + modern AI" story.
  - What makes a portfolio project hireable: clear README (Module 4), a diagram (Module 10), tests + green CI badge (Module 8), reproducibility (Module 9/13), an honest "what I learned / what I'd do next," and ideally a short demo (GIF/video).
  - **GitHub presence:** a strong profile README, pinned repositories (your best 4–6), meaningful commit history (Module 9), consistent activity, an organized repo structure, license files. The profile is a living resume.
  - The flagship piece: pick one capstone (the radar/DSP one is your differentiator) and polish it to "showpiece" quality — writeup, diagrams, results with uncertainty (Module 6), video demo.
  - Optional amplifiers: a short technical blog post or two (a debug story, a tradeoff writeup — reuses Module 10), a Kaggle/Hugging Face artifact for the ML-for-signals angle (09).
  - Honesty and IP: never post employer-proprietary or classified/ITAR-restricted work — *especially* relevant given a defense radar background; rebuild concepts cleanly on open data/hardware instead.
- **Why it matters / connections:** For a career-changer/re-enterer, *demonstrated* work beats credentials. A hiring engineer who sees a clean, tested, documented radar-DSP repo with a video demo is sold before the interview. This module is the payoff that makes Modules 4, 8, 9, 10, 13 concrete.
- **Hands-on / exercises:**
  - Audit all your capstones; pick the 4–5 strongest; bring each up to the "hireable project" checklist above.
  - Write your GitHub profile README: who you are (radar tech → EE → software), what you build, links to pinned projects.
  - Polish your flagship project to showpiece quality, including a < 90-second demo video.
  - Write one technical blog post (a debug-story or tradeoff writeup from an earlier module).
- **You've got it when:** A stranger landing on your GitHub profile understands in 30 seconds that you're an RF/DSP/embedded + software engineer, and can click into a polished, reproducible flagship project.

### Module 15: Job-market readiness II — resume, LinkedIn & positioning

- **Concepts:**
  - **Positioning the hybrid:** "Radar Technician → BSEE → Software Developer" is not a scattered history — it's a rare **RF/signals + systems + software** combination. Lead with that synthesis. The defense/radar background plus modern software/AI is a genuinely scarce profile.
  - Resume mechanics: reverse-chronological, 1–2 pages, **accomplishment bullets** with the X-Y-Z / STAR form ("Accomplished X, measured by Y, by doing Z"), quantified results, action verbs, no fluff, ATS-friendly formatting (parseable, keyword-aligned to the job description).
  - Reframing radar-tech experience into engineering bullets: "isolated intermittent fault in radar RF chain to a failing mixer using systematic signal tracing, restoring system to spec" — that's Module 5 + 7 told as impact.
  - Tailoring per role/JD; the keyword-matching reality of ATS; a skills section that mirrors the curriculum (DSP, RF, embedded C, Linux, Python, git, ML).
  - LinkedIn: headline as positioning statement, About section as narrative, experience mirroring the resume, skills/endorsements, signaling "open to work," connecting with target communities.
  - Addressing the "retiring/career-stage" angle honestly and as strength: deep judgment, reliability, mentorship, no ramp-up on fundamentals; targeting roles/companies that value seniority over "10 years left."
  - References, security clearance (if held — a real asset for defense employers), and how to mention it.
- **Why it matters / connections:** The best portfolio (Module 14) still needs a resume/LinkedIn to get past the first filter. This module converts the entire curriculum into the language recruiters and ATS systems scan for. STAR bullets reuse the communication discipline of Module 10.
- **Hands-on / exercises:**
  - Write a 1-page resume; convert every bullet to X-Y-Z form with a quantified result; align keywords to a real target job posting.
  - Rewrite 5 radar-technician duties as engineering-impact bullets mapping to specific curriculum skills.
  - Write your LinkedIn headline and About section; set "open to work" for target titles.
  - Run your resume through a free ATS-style parser and fix what it mangles.
- **You've got it when:** Your resume passes the "6-second scan" with quantified impact, frames the radar+software hybrid as an asset, and is tailorable to a specific JD in under 30 minutes.

### Module 16: Job-market readiness III — interviews & target roles

- **Concepts:**
  - **Target role map** (so you aim, not spray) — and which curriculum modules feed each:
    - **RF / microwave / DSP engineer** ← 01, 02, 03, 07, 08 (your strongest, most differentiated lane).
    - **Embedded / firmware engineer** ← 03, 04, 05 (huge market, strong fit).
    - **Systems engineer** ← Modules 1–3 here + breadth across 02–09 (rewards exactly your seniority).
    - **ML-for-signals / DSP-ML engineer** ← 03, 07/08, 09 (the modern frontier; "radar + ML" is a hot, scarce combo).
    - **Test / validation / hardware engineer** ← Module 5 + 8 (your bench instrumentation depth shines).
    - Adjacent: FPGA/HDL engineer (03/04), defense systems (clearance + radar), SDR/comms engineer (06/07).
  - **Interview formats & prep:**
    - Behavioral: STAR stories — prepare 6–8 (a hard bug, a tradeoff, a failure, a conflict, a leadership/mentorship moment); reuse Modules 3, 7, 10.
    - Technical fundamentals: be ready to whiteboard a sampling/Nyquist explanation, a link budget, an op-amp config, a state machine, an I²C transaction, a bias/variance tradeoff — pulled straight from 01–09.
    - Coding screens: data structures/algorithms refresh, a primary language (Python and/or C/C++) sharp, practice talking while coding.
    - System/design interviews: apply Modules 1–3 (requirements → tradeoffs → architecture, narrated out loud).
    - The "explain your project" interview: your flagship portfolio piece, told at 1/5/30-minute depths (Module 10).
  - Logistics & soft skills: researching the company, asking good questions, salary negotiation basics, take-home assignment etiquette, remote-interview setup, following up.
  - Pipeline management: treat the search as a project (Module 11) — a Kanban board of applications, target list, weekly throughput, retrospective on rejections.
- **Why it matters / connections:** This is the destination the whole curriculum was driving toward: converting refreshed, modernized engineering skill into offers. It explicitly maps every prior module to a hireable role and an interview answer, so nothing you studied is "academic."
- **Hands-on / exercises:**
  - Write your target-role shortlist (2–3 primary, 2 stretch) and the 5 companies/teams for each.
  - Draft 8 STAR stories; record yourself answering 3 behavioral questions; review and redo.
  - Do 3 mock technical whiteboards (Nyquist, link budget, a state machine) out loud, timed.
  - Solve a week of medium coding problems while narrating; do one realistic system-design prompt.
  - Stand up an application-tracker Kanban board (Module 11) and run your search as a project.
- **You've got it when:** You have a focused target list, a stocked STAR-story bank, can whiteboard core fundamentals on demand, and you're running the search as a managed pipeline rather than hoping.

## Capstone / integrative exercise

**"Ship one project like a professional engineer, end to end."** Take your single strongest cross-disciplinary capstone — ideally the **radar/DSP signal-processing project (modules 07/08/09)**, because it is your market differentiator — and drive it through *every* module of this plan:

1. Write a **requirements spec** (M1) and draw the **V-model / FMEA** (M2).
2. Record at least two **decisions as ADRs** with a decision matrix (M3).
3. Produce a **datasheet-grade README + design doc** (M4).
4. Take real **measurements with stated calibration and uncertainty** (M5/M6) — e.g., characterize the front-end or the link with a NanoVNA/RTL-SDR.
5. Keep a **debug log** for at least one real fault, solved by binary-search/5-Whys (M7).
6. Add a **test suite + GitHub Actions CI** mapping tests to requirements (M8).
7. Manage it all in **git with clean history**, made reproducible via a **container** (M9/M13).
8. Communicate it: **block + timing + state diagrams** (Mermaid), a written summary, and a **< 90-second demo video** (M10).
9. Run it on a **GitHub Projects Kanban board** with three-point estimates tracked against actuals (M11).
10. Document the **safety, standards, and ethics** considerations, and note what's safe to publish vs. proprietary (M12).
11. Use **AI assistance** somewhere in the build and document where you caught it being wrong (M13).
12. Turn the finished thing into your **flagship portfolio piece**, a **resume bullet**, a **LinkedIn project**, and a **5-minute interview walkthrough** (M14/M15/M16).

The deliverable is one public, polished GitHub repository plus the resume bullet and the spoken walkthrough. That single artifact exercises the whole module and becomes the centerpiece of your job search.

## Common pitfalls & rust-knockers

- **Solving before specifying** — jumping to a design before writing testable requirements. Force yourself to write the spec first (M1).
- **"X is better"** with no weighted criteria — every tradeoff claim needs a decision matrix or it's an opinion (M3).
- **Trusting the instrument blindly** — forgetting probe loading, bandwidth limits, aliasing, or an uncalibrated reference plane. Re-internalize the measurement rituals (M5).
- **False precision** — reporting 6 digits from a ±1% instrument. Significant figures and uncertainty are not optional (M6).
- **Shotgun debugging** — changing many things at once and hoping. Reproduce, then binary-search, one variable at a time (M7).
- **Confusing verification and validation** — passing all tests while building the wrong thing (M8).
- **Git as `add/commit/push` only** — never branching, never `bisect`, fearing rebase, no reproducible environment. Learn the power tools and lockfiles/containers (M9/M13).
- **"My code is the documentation"** — no README, no diagram, no design doc; nobody (including future you) can rebuild it (M4/M10).
- **Over-trusting AI** — pasting hallucinated APIs/specs/math without verification, or pasting secrets/proprietary code into a prompt (M13).
- **Hiding the radar/defense background** instead of framing it as a scarce RF+systems asset; or, conversely, posting work you're not legally allowed to (ITAR/classified) (M14/M15).
- **Resume as a duty list, not an impact story** — bullets without quantified outcomes get filtered out (M15).
- **Spray-and-pray job search** — applying everywhere with one generic resume instead of targeting 2–3 roles and tailoring (M16).
- **Gold-plating / bikeshedding** — polishing the trivial while the core risk goes untested (M2/M3).
- **Skipping safety because "it's just bench work"** — ESD and stored-energy/mains complacency (M12).

## Self-assessment checklist

- [ ] I can turn a vague request into a numbered, testable requirements spec with acceptance criteria.
- [ ] I can name which design-process phase I'm in and the artifact it must produce, and I run an FMEA before building.
- [ ] I can build and defend a weighted decision matrix and write an ADR.
- [ ] I can read a datasheet for guaranteed (min/max) specs and write a README a stranger can build from.
- [ ] I use a DMM, scope, logic analyzer, spectrum analyzer, and VNA with correct technique, calibration, and a stated uncertainty.
- [ ] I carry units and significant figures correctly and can propagate uncertainty through a formula.
- [ ] I think and compute fluently in dB / dBm.
- [ ] I debug any system by reproduce → binary-search → one-variable-at-a-time → root cause, with a written log.
- [ ] I can use `git bisect` and 5-Whys to find a root cause.
- [ ] I can explain verification vs. validation and write tests that map to requirements.
- [ ] My projects have automated CI that runs tests on every push.
- [ ] I use git branches, rebase, bisect, and reflog confidently, and my history reads cleanly.
- [ ] Anyone can reproduce my results from a clean clone or a container.
- [ ] I can explain any capstone in 60 s / 5 min / 30 min and pick the right diagram type for the job.
- [ ] I can create block, schematic, sequence, state, and timing diagrams (including as code in Mermaid).
- [ ] I'm fluent in Scrum/Kanban vocabulary and can run a capstone on a Kanban board.
- [ ] I can give a three-point estimate and know my personal optimism bias.
- [ ] ESD/electrical/RF safety handling is automatic, and I can name the standards body for a given domain.
- [ ] I can reason through an ethics dilemma using the IEEE/NSPE code.
- [ ] I use AI tools daily as an accelerant and reflexively verify their quantitative/API claims.
- [ ] I have 4–5 curated, tested, documented, reproducible portfolio projects and a flagship showpiece with a demo video.
- [ ] My GitHub profile instantly communicates "RF/DSP/embedded + software + AI."
- [ ] My resume frames the radar+software hybrid as an asset, uses quantified X-Y-Z bullets, and is ATS-friendly.
- [ ] I have a target-role shortlist, 8 STAR stories, can whiteboard core fundamentals, and run my search as a managed pipeline.

## Canonical resources

**Engineering practice & systems thinking**
- *The Art of Systems Architecting* — Maier & Rechtin.
- *Thinking in Systems: A Primer* — Donella Meadows.
- INCOSE *Systems Engineering Handbook*.
- NASA *Systems Engineering Handbook* (SP-2016-6105, free PDF).

**Design, decisions, troubleshooting**
- *The Design of Everyday Things* — Don Norman (design thinking).
- *Debugging: The 9 Indispensable Rules* — David J. Agans (the troubleshooting bible).
- *Why Programs Fail* — Andreas Zeller (systematic debugging, delta debugging).
- *The Pragmatic Programmer* — Hunt & Thomas (practice, DRY, tracer bullets).

**Measurement & instrumentation**
- Keysight / Tektronix application notes ("Oscilloscope Fundamentals," "Spectrum Analysis Basics," "Network Analyzer Basics," "8 Hints for…") — free, authoritative.
- *The Art of Electronics* — Horowitz & Hill (Ch. on measurement, plus everything).
- NIST/ISO **GUM** (*Guide to the Expression of Uncertainty in Measurement*) and *Taylor, An Introduction to Error Analysis*.
- Tooling: a NanoVNA, an RTL-SDR (cheap spectrum analysis), Sigrok/PulseView (logic analyzer software).

**Version control & reproducibility**
- *Pro Git* — Chacon & Straub (free online — the definitive git book).
- "Learn Git Branching" (interactive, learngitbranching.js.org).
- Docker docs; *The Turing Way* (open handbook on reproducible research).

**Testing & reliability**
- *Working Effectively with Legacy Code* — Michael Feathers; pytest / GoogleTest docs.
- GitHub Actions documentation (CI).
- MIL-HDBK-217 / *Reliability Engineering* — Elsayed (MTBF, reliability math).

**Communication & PM**
- *The Elements of Style* — Strunk & White; *On Writing Well* — Zinsser.
- *Trees, Maps, and Theorems* — Jean-luc Doumont (engineer-focused communication — excellent).
- Mermaid / PlantUML / Graphviz docs (diagrams as code).
- *Scrum: The Art of Doing Twice the Work in Half the Time* — Sutherland; Atlassian Agile/Kanban guides (free); *Software Estimation: Demystifying the Black Art* — Steve McConnell.

**Safety, standards, ethics**
- ANSI/ESD S20.20 overview; IEEE/IEC standards portals; FCC OET Bulletin 65 (RF exposure / MPE); ICNIRP guidelines.
- **IEEE Code of Ethics** and **NSPE Code of Ethics** (both free online).
- Case studies: Therac-25 (Leveson & Turner), Challenger (Rogers Commission / Feynman appendix), Hyatt Regency walkway.
- *To Engineer Is Human* — Henry Petroski (the role of failure in design).

**Modern / AI-assisted workflows**
- Vendor docs for your assistant of choice (e.g., Claude Code / Anthropic docs), VS Code docs, pre-commit framework docs.
- Practice: integrate the assistant into the capstone, always verifying against primary sources.

**Job-market readiness**
- *Cracking the Coding Interview* — Gayle Laakmann McDowell (coding/behavioral).
- *Designing Data-Intensive Applications* — Kleppmann (systems-design depth, if targeting backend/systems).
- LeetCode / HackerRank (coding practice); *The Tech Resume Inside Out* — Gergely Orosz; *What Color Is Your Parachute?* — Bolles (career-change framing).
- GitHub's "Profile README" and "GitHub Pages" docs (portfolio hosting); LinkedIn's own job-search resources.
