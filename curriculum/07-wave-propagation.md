# Wave Propagation & Electromagnetics — Lesson Plan

This module is the physics gate between abstract math/circuits and the radar capstone (08). Everything a radar does — radiate, propagate, reflect, return, detect — is governed by the material here. For you specifically, this is *reactivation*, not first contact: you already turned wrenches on radar hardware and have a BSEE, so the goal is to rebuild the through-line from Maxwell's equations to a working link budget so the theory feels *load-bearing* again, not like trivia. We move fast through derivations you've seen and spend real time on the connective tissue (Smith chart, arrays, propagation budgets) that interview panels and modern phased-array/5G work actually probe.

Prerequisites you should have fresh from earlier modules: **vector calculus** (grad/div/curl, line and surface integrals, divergence and Stokes' theorems), **differential equations** (2nd-order linear, separation of variables), and **complex numbers / phasors** from **01**; and **analog/RF circuit concepts** (impedance, resonance, S-parameters intuition) from **02**. If any of those feel rusty, knock the rust off *before* Module 2 — the wave equation is unforgiving if div/curl are fuzzy. This module feeds **08 (Radar)** directly: the radar range equation is just a Friis link budget with a target cross-section in the middle, and phased-array beam steering is the array factor from Module 9 made electronic.

## Learning outcomes

By the end you can:

1. State Maxwell's equations in differential and integral form, explain each physically, and derive the wave equation from them.
2. Analyze uniform plane waves in free space and in lossy/dielectric/conducting media: phase velocity, wavelength, intrinsic impedance, attenuation, skin depth, polarization.
3. Compute Poynting vector and time-average power flow; reason about energy and radiation pressure qualitatively.
4. Solve reflection/refraction at boundaries (normal and oblique), apply Snell's law, Fresnel coefficients, Brewster and critical angles, total internal reflection.
5. Analyze transmission lines with the telegrapher's equations: characteristic impedance, propagation constant, reflection coefficient, VSWR, input impedance, standing-wave patterns.
6. Use the **Smith chart** fluently for impedance transformation and matching (stub and lumped-element), without a calculator for the standard moves.
7. Describe waveguide modes (TE/TM), cutoff, dominant mode, and why hollow guides beat coax at microwave/radar frequencies.
8. Characterize antennas: radiation mechanism, near/far field boundary, gain, directivity, efficiency, beamwidth, polarization, common types, and effective aperture.
9. Build array factors for linear/planar arrays, steer a beam electronically, and connect this to phased-array radar, 5G beamforming, and MIMO.
10. Construct a full RF link budget: free-space path loss, Friis, antenna gains, system noise temperature, SNR, fade margins; account for reflection, diffraction, scattering, multipath, atmospheric/ionospheric effects, and ducting.
11. Speak credibly about modern EM simulation (openEMS, HFSS-class solvers) and where method-of-moments vs FDTD vs FEM apply.

## Module breakdown

### Module 1: EM fundamentals & vector fields

- **Concepts:** Scalar vs vector fields; field lines and flux. Coulomb's law, electric field **E**, electric flux density **D**, permittivity ε (ε₀, εᵣ, complex ε for lossy media). Gauss's law intuition. Electric potential V, **E** = −∇V, gradient. Conductors, dielectrics, polarization **P**, bound vs free charge. Capacitance and stored electric energy. Steady currents, current density **J**, conductivity σ, Ohm's law in point form **J** = σ**E**. Magnetostatics: Biot–Savart, magnetic field **H**, magnetic flux density **B**, permeability μ (μ₀, μᵣ), magnetization **M**. Ampère's law, magnetic vector potential **A**, inductance, stored magnetic energy. The four field vectors and constitutive relations **D** = ε**E**, **B** = μ**H**, **J** = σ**E**. Vector operators in Cartesian/cylindrical/spherical; divergence and Stokes' theorems as the bridge between integral and differential laws.
- **Why it matters / connections:** These are the building blocks Maxwell assembles. The constitutive relations are where *materials* enter — radomes, dielectric lenses, lossy ground, and absorber all live here. Skin effect and conductivity preview waveguide/transmission-line loss. ∇/∇·/∇× fluency is non-negotiable for the next module.
- **Hands-on / exercises:** Re-derive capacitance of parallel plates and coax, and inductance of a solenoid and coax, from field integrals. Compute **E** from a point and line charge via Gauss's law. Verify the divergence theorem on a simple cube field. Identify which constitutive parameter dominates for copper, FR-4, seawater, and dry air at 10 GHz.
- **You've got it when:** You can switch between integral and differential forms of Gauss/Ampère on sight and explain what each constitutive relation physically encodes.

### Module 2: Maxwell's equations & the wave equation

- **Concepts:** The four equations, differential and integral, conceptual and working:
  - Gauss (E): ∇·**D** = ρ
  - Gauss (M): ∇·**B** = 0 (no monopoles)
  - Faraday: ∇×**E** = −∂**B**/∂t
  - Ampère–Maxwell: ∇×**H** = **J** + ∂**D**/∂t (the displacement-current term — the keystone)
  The continuity equation and charge conservation as a consequence. Time-harmonic (phasor) form with jω replacing ∂/∂t. Deriving the **wave equation** by taking curl of Faraday and substituting Ampère: ∇²**E** = με ∂²**E**/∂t² in source-free regions. Speed of light c = 1/√(μ₀ε₀); phase velocity in media v = 1/√(με). The wavenumber k = ω/v, dispersion relation. Boundary conditions on **E**, **H**, **D**, **B** at interfaces (tangential/normal continuity, surface charge/current).
- **Why it matters / connections:** This is *the* gate. The displacement current is why radiation exists at all — a radar antenna works because a time-varying field detaches and propagates. The wave equation underpins every later module. Boundary conditions are exactly what makes waveguides and reflection problems solvable.
- **Hands-on / exercises:** Derive the wave equation for **E** and for **H** from Maxwell, source-free. Convert each equation to phasor form. Show displacement current makes Ampère's law consistent for a charging capacitor. Verify a given plane-wave expression satisfies the wave equation and find k.
- **You've got it when:** You can derive the wave equation from memory and explain in one sentence what each Maxwell equation forbids or requires.

### Module 3: Plane waves, polarization, and power

- **Concepts:** Uniform plane wave solution **E** = E₀ e^{j(ωt−kz)}; transverse nature (**E** ⊥ **H** ⊥ direction of propagation). Intrinsic impedance η = √(μ/ε) (η₀ ≈ 377 Ω in free space). Relationship **H** = (1/η) **k̂** × **E**. Phase velocity, wavelength, group velocity, dispersion (preview). **Polarization:** linear, circular (LHCP/RHCP), elliptical; how phase/amplitude of orthogonal components set it; axial ratio. **Poynting vector** **S** = **E** × **H**, instantaneous and time-average ⟨**S**⟩ = ½ Re(**E** × **H***); power density (W/m²); relation to |E|²/η. Radiation pressure (qualitative).
- **Why it matters / connections:** Power density is the currency of every link and radar budget. Polarization directly governs radar target returns (polarimetric radar), rain clutter rejection, GPS (RHCP), and antenna co/cross-pol. Intrinsic impedance sets up the reflection coefficient at boundaries (next module) and antenna impedance matching.
- **Hands-on / exercises:** Given **E**, compute **H**, η, and ⟨**S**⟩. Decompose an elliptically polarized wave and find its axial ratio and tilt. Compute power density at 1 km from a 100 W isotropic source and verify with 1/(4πr²). Sketch the polarization ellipse for given component phases.
- **You've got it when:** You can read a wave's polarization from its component phasors and compute its power density and impedance without notes.

### Module 4: Propagation in media — lossy, dielectric, conducting

- **Concepts:** Complex permittivity ε = ε′ − jε″; loss tangent tan δ = ε″/ε′ (and σ/ωε). Complex propagation constant γ = α + jβ: attenuation constant α (Np/m) and phase constant β (rad/m). Good dielectric vs good conductor limits. **Skin depth** δ = 1/α = √(2/ωμσ); why high-frequency currents ride the surface. Complex intrinsic impedance and the resulting **E**/**H** phase lag in conductors. Frequency dependence of loss; dispersion. Plasma/ionosphere as a frequency-dependent medium (plasma frequency, preview for Module 11).
- **Why it matters / connections:** Atmospheric and rain attenuation, ground loss, and conductor loss in lines/waveguides all reduce to α here. Skin depth sizes plating thickness on waveguides and explains conductor Q. The ionosphere-as-plasma idea is the root of HF propagation and radar ducting later.
- **Hands-on / exercises:** Compute α, β, skin depth, and η for copper and for seawater at 1 MHz, 100 MHz, 10 GHz. Classify a material as good conductor vs good dielectric from tan δ. Find the dB/m attenuation and the distance for a wave to drop 20 dB in a lossy dielectric.
- **You've got it when:** Given σ, ε, μ, f you can immediately decide which regime applies and compute skin depth and attenuation.

### Module 5: Reflection, refraction & boundaries

- **Concepts:** Normal incidence on a planar boundary: reflection coefficient Γ = (η₂−η₁)/(η₂+η₁), transmission coefficient τ; power conservation. Standing waves from the superposition of incident and reflected (preview of VSWR). Oblique incidence: **Snell's law** (n₁ sin θ₁ = n₂ sin θ₂); TE (perpendicular) and TM (parallel) polarizations. **Fresnel coefficients.** **Brewster angle** (zero TM reflection). **Critical angle** and **total internal reflection.** Reflection from conductors (Γ → −1). Multiple-layer / quarter-wave transformer matching (η = √(η₁η₂)) — the optical analog of impedance matching.
- **Why it matters / connections:** Ground-bounce multipath, radome and radar-absorbing-material design, and dielectric lens/Luneburg antennas all derive from boundary behavior. The quarter-wave transformer reappears in Module 8 as a matching tool. Brewster/critical angles explain polarization-dependent ground reflection in propagation modeling.
- **Hands-on / exercises:** Compute Γ and power reflection at an air–glass boundary. Find Brewster and critical angles for given indices. Design a quarter-wave matching layer between two media. Show that a perfect conductor gives total reflection with a 180° phase flip.
- **You've got it when:** You can derive Γ for normal incidence and explain the physical origin of Brewster and critical angles.

### Module 6: Transmission lines & the telegrapher's equations

- **Concepts:** Distributed RLGC model; when "lumped vs distributed" matters (line length vs λ). **Telegrapher's equations** and their wave-equation solution. **Characteristic impedance** Z₀ = √((R+jωL)/(G+jωC)) → √(L/C) lossless. Propagation constant γ = α + jβ. Traveling vs standing waves. **Reflection coefficient** Γ_L = (Z_L−Z₀)/(Z_L+Z₀); Γ(z) along the line. **Input impedance** Z_in = Z₀ (Z_L + jZ₀ tan βl)/(Z₀ + jZ_L tan βl); special cases: λ/4 transformer, λ/2 repeater, shorted/open stubs as reactances. **VSWR** = (1+|Γ|)/(1−|Γ|); return loss, mismatch loss. Standing-wave voltage maxima/minima and their spacing (λ/2). Common lines: coax, microstrip, stripline, CPW, twin-lead; loss and dispersion in each.
- **Why it matters / connections:** Every connection between an RF source, feed, and antenna is a transmission line. VSWR/return loss are the *first* numbers any RF or radar tech reads off a meter. The stub-as-reactance idea is the basis of matching networks (next module). Microstrip is what you'll actually lay out on a board.
- **Hands-on / exercises:** Given Z_L and Z₀, compute Γ_L, VSWR, return loss, and Z_in at λ/8, λ/4, λ/2. Design a λ/4 transformer to match 100 Ω to 50 Ω. Find the input reactance of a shorted and open stub of given length. Locate voltage min/max positions on a mismatched line.
- **You've got it when:** You can move impedance along a line and compute VSWR/return loss fluently, and you reach for a stub when you need a specific reactance.

### Module 7: Impedance matching & the Smith chart *(budget extra time)*

- **Concepts:** Why match: maximum power transfer, minimum reflection, protecting sources. The **Smith chart** as the complex-Γ plane with constant-R and constant-X circles overlaid; normalized impedance z = Z/Z₀. Reading impedance ↔ reflection coefficient ↔ VSWR ↔ return loss off one chart. Rotation toward/away from generator (clockwise = toward generator, one full turn = λ/2). Admittance chart / Y-Smith and the impedance↔admittance flip (180° rotation). **Matching techniques:** lumped L-networks (series/shunt L and C), single-stub and double-stub tuning, quarter-wave transformer, tapered lines. Bandwidth of a match (Q, Bode–Fano limit qualitatively). Reading VSWR circles. *(MODERN)* Doing all of this in a CAD tool (Keysight ADS, QUCS, scikit-rf in Python) and on a **VNA**.
- **Why it matters / connections:** This is the single most interview-probed RF skill and the most hands-on. Antenna feed matching, amplifier input/output matching, and waveguide tuning all live here. A radar tech who can match on a Smith chart from memory signals deep competence. scikit-rf ties it to your software background.
- **Hands-on / exercises:** *Do many.* (1) Plot a load on the Smith chart and read VSWR/return loss. (2) Match a complex load to 50 Ω with a lumped L-network — both topologies. (3) Single-stub match: find stub position and length. (4) Verify the same problem numerically in **scikit-rf** and compare. (5) If a VNA is available, measure an actual antenna/cable and read S11 on the chart. (6) Rotate a load by a given line length and read the new impedance.
- **You've got it when:** You can match an arbitrary complex load to 50 Ω on a blank Smith chart with no calculator, and you can explain every move physically.

### Module 8: Waveguides

- **Concepts:** Why hollow waveguides at microwave/radar frequencies (low loss, high power handling). Rectangular waveguide modes: **TE** and **TM**, mode indices (m,n); no TEM in hollow single-conductor guides. **Cutoff frequency** f_c and cutoff wavelength; the guide as a high-pass filter. **Dominant mode** TE₁₀ and why it's used. Guide wavelength λ_g vs free-space λ; phase velocity > c and group velocity < c (no causality violation). Wave impedance of the guide. Attenuation and the effect of wall conductivity. Circular waveguides and modes (brief). Cavity resonators and Q (brief). Waveguide components: bends, tees, irises, tuning screws, transitions to coax. *(Light MODERN)* substrate-integrated waveguide (SIW).
- **Why it matters / connections:** Radar transmit chains, especially high-power and at X-band and up, are full of waveguide. As a radar tech you handled this hardware — this connects the field theory to the plumbing you knew. Cutoff and dominant-mode logic explain why a given guide is sized for a given band.
- **Hands-on / exercises:** For WR-90 (X-band), compute TE₁₀ cutoff, guide wavelength, and usable single-mode band. Find which modes propagate at a given frequency in a given guide. Compute attenuation per meter for copper walls. Identify standard WR sizes for L/S/C/X/Ku bands.
- **You've got it when:** You can compute cutoff and guide wavelength and explain why TE₁₀ dominates and why v_p > c is fine.

### Module 9: Antennas — radiation, parameters & types

- **Concepts:** Radiation mechanism (accelerating charge / time-varying current detaching fields). The infinitesimal (Hertzian) dipole as the canonical radiator; the half-wave dipole. Field regions: **reactive near field, radiating near field (Fresnel), far field (Fraunhofer)**; the 2D²/λ far-field boundary. **Radiation pattern**, main lobe, side lobes, nulls, front-to-back ratio. **Beamwidth** (HPBW/3 dB, FNBW). **Directivity** D, **gain** G = e·D, radiation efficiency e. **Effective aperture** A_e = (λ²/4π)G and the gain–aperture relationship. Radiation resistance, input impedance, bandwidth. **Polarization** of antennas, co-pol/cross-pol, polarization loss factor, axial ratio. Reciprocity (TX pattern = RX pattern). EIRP. Common types and their trade-offs: dipole, monopole, loop, patch/microstrip, horn, parabolic reflector (gain ≈ (πD/λ)²·e), helix, Yagi-Uda, log-periodic, slot, spiral. Feeds and baluns.
- **Why it matters / connections:** The antenna is where the radar meets the world — its gain and beamwidth set angular resolution and detection range. Effective aperture is the receive side of the radar equation. Far-field distance dictates how you test and calibrate. This module is the immediate setup for arrays and for the radar capstone.
- **Hands-on / exercises:** Compute far-field distance, gain, and HPBW for a 1 m parabolic dish at 10 GHz. Convert between directivity, gain, and effective aperture. Compute EIRP for a given TX power, line loss, and antenna gain. Find polarization loss between an LP and a CP antenna. Pick the right antenna type for three given scenarios and justify.
- **You've got it when:** You can relate gain ↔ beamwidth ↔ aperture ↔ frequency in your head and choose an antenna type for a stated mission.

### Module 10: Antenna arrays & beamforming *(MODERN-heavy)*

- **Concepts:** Pattern multiplication: total pattern = element pattern × **array factor**. Linear array AF for N elements with spacing d and progressive phase shift α. Beam steering by phase: θ₀ from α = −kd sin θ₀. Grating lobes and the d ≤ λ/2 (broadside) / spacing rule. Sidelobe control via amplitude tapering (uniform, binomial, Dolph–Chebyshev, Taylor). Broadside vs end-fire arrays. Planar/2D arrays and 2D beam steering. Array gain ≈ N × element gain (ideal). Mutual coupling (qualitative). **Beamforming:** analog (phase shifters), digital, and hybrid. **(MODERN)** Active electronically scanned arrays (**AESA**) and phased-array radar; **massive MIMO** and **beamforming in 5G/mmWave**; adaptive/null-steering beamforming; the link from array factor to **MIMO** spatial multiplexing and diversity. Time-delay vs phase steering and beam squint over bandwidth.
- **Why it matters / connections:** This is the bridge to modern radar (08): mechanically-scanned dishes are giving way to AESA, and the math is exactly the array factor here. The same beamforming math powers 5G base stations and Wi-Fi 6/7 — high-value, current, interview-relevant. Phase-steering connects directly to the phased-array sections of the radar module.
- **Hands-on / exercises:** Derive and plot the array factor for an 8-element uniform linear array; steer the beam to 30°. Find the spacing that triggers a grating lobe. Apply a Chebyshev taper and observe sidelobe reduction. Compute HPBW vs N. *(MODERN)* Build and animate a steered ULA pattern in Python (numpy/matplotlib); connect element phases to a "beamforming" picture. Sketch how the same array does TX and RX beamforming for MIMO.
- **You've got it when:** You can write the array factor, steer it with a phase taper, predict grating lobes, and explain how this is literally how an AESA radar and a 5G beamformer point energy.

### Module 11: Radio propagation

- **Concepts:** **Free-space path loss** FSPL = (4πd/λ)² and in dB; the **Friis transmission equation** P_r = P_t G_t G_r (λ/4πd)². Propagation mechanisms beyond free space: **reflection** (two-ray ground-bounce model, the d⁴ regime), **diffraction** (knife-edge, Fresnel zones — the first Fresnel zone and clearance), **scattering** (rough surfaces, rain, Rayleigh vs Mie). **Multipath**, fading (flat vs frequency-selective, fast vs slow), delay spread, Rician/Rayleigh statistics. Refraction in the troposphere; the 4/3-Earth-radius model and the radio horizon. **Atmospheric absorption** (the 22 GHz water and 60 GHz oxygen lines; rain attenuation rising with frequency). **Ionospheric** effects: layers, plasma frequency, MUF, HF skywave, Faraday rotation, scintillation, group delay (GPS). **Ducting** / anomalous propagation and its impact on radar (extended or "lost" coverage). Propagation by band (HF/VHF/UHF/microwave/mmWave).
- **Why it matters / connections:** This is *how the radar pulse actually gets there and back* under real atmosphere — directly drives radar detection range, the radar horizon, and anomalous-propagation surprises you'd have seen operationally. Friis is the algebraic core of the radar range equation. Fresnel-zone clearance and multipath are the daily reality of any link planner.
- **Hands-on / exercises:** Compute FSPL at 100 MHz / 2.4 GHz / 28 GHz over 10 km. Apply Friis for a given link and find received power. Two-ray model: find the crossover distance and the d⁴ falloff. First Fresnel-zone radius at link midpoint and required clearance. Estimate radio horizon with the 4/3 model. Look up rain/atmospheric attenuation at 10 and 60 GHz and discuss band choice. Explain a ducting scenario's effect on radar coverage.
- **You've got it when:** You can compute FSPL/Friis cold and explain when reflection, diffraction, multipath, atmospheric, or ionospheric effects dominate at a given band and geometry.

### Module 12: Link budgets, noise & interference

- **Concepts:** Assembling a **link budget**: P_t, line/connector losses, antenna gains, EIRP, FSPL + atmospheric/rain/margins, received power, into SNR. **Noise:** thermal/Johnson noise (kTB), noise figure F and noise temperature T_e, cascaded noise figure (**Friis noise formula**), system noise temperature, antenna noise temperature (sky, ground). Sensitivity / minimum detectable signal. **SNR**, C/N, Eb/N0 (link to Shannon capacity from 06). Fade margin and availability. **Interference:** co-channel and adjacent-channel, intermodulation, EMI/EMC basics, jamming and the radar context (J/S), spread-spectrum processing gain (qualitative). dB algebra fluency (dBm, dBW, dBi, dB) end to end. *(MODERN)* connecting link budgets to 5G/mmWave coverage planning and to phased-array EIRP gains.
- **Why it matters / connections:** The link budget is the capstone arithmetic of the whole module and the direct ancestor of the **radar range equation** in 08 (just insert target RCS and a two-way path). Noise figure and system temperature set radar sensitivity. J/S and interference are core radar-survivability topics. This is also exactly the spreadsheet an RF systems engineer is paid to build.
- **Hands-on / exercises:** Build a complete one-way link budget end to end (e.g., a 28 GHz point-to-point or a satellite downlink) and solve for SNR and fade margin. Compute cascaded noise figure for a 3-stage receiver and show why the first LNA dominates. Convert a chain of gains/losses through dBm bookkeeping. Compute minimum detectable signal for a given bandwidth and noise figure. *(Preview)* sketch how this budget morphs into the radar range equation.
- **You've got it when:** You can build a link budget from scratch in consistent dB units, find SNR and margin, and identify the dominant loss and noise terms.

### Module 13: EM simulation tools *(MODERN)*

- **Concepts:** Why simulate: closed-form solutions run out fast for real geometries. The three workhorse numerical methods and where each fits: **FDTD** (time domain, broadband, openEMS, Meep — great for antennas/transients), **FEM** (frequency domain, complex/inhomogeneous geometry, HFSS/COMSOL-class), **MoM** (method of moments, open radiating structures, NEC for wire antennas, FEKO-class). Meshing and convergence; ports and excitation; absorbing/PML boundaries; near-to-far-field transformation. Reading results: S-parameters, VSWR, gain/pattern, current distributions. **Open-source ladder:** NEC2/nec2c and xnec2c for wire antennas, openEMS for general FDTD, scikit-rf for network/Smith-chart post-processing, QUCS for circuits/matching. Commercial awareness: HFSS, CST, FEKO, ADS. *(Tie-in)* Using your software skills to script sweeps and post-process — a differentiator.
- **Why it matters / connections:** Modern antenna/array and radar front-end design is simulation-first; naming the right solver for a problem and reading its outputs is a concrete interview signal. openEMS/NEC let you actually *build* the antennas and arrays from Modules 9–10 and validate the hand calculations. Leverages your developer background directly.
- **Hands-on / exercises:** Model a half-wave dipole in xnec2c; read input impedance, pattern, and gain; compare to theory. Simulate a microstrip patch in openEMS and extract S11/VSWR; compare to your Smith-chart match. Script a frequency or geometry sweep and plot with scikit-rf. Reproduce one array-factor result from Module 10 in a full-wave tool and explain the discrepancies (mutual coupling, finite ground).
- **You've got it when:** You can choose FDTD vs FEM vs MoM for a stated problem, set up a basic antenna sim, and reconcile simulated vs analytic results.

## Capstone / integrative exercise

**Design and budget an X-band (10 GHz) point-to-point/radar-style RF link, end to end, defending every number.**

1. **Antenna:** Choose and size a parabolic dish (or design a small patch array). Compute gain, HPBW, effective aperture, far-field distance. *(Stretch: make it an N-element phased array and steer the beam ±30°, plotting the array factor and checking for grating lobes.)*
2. **Feed & match:** Specify a 50 Ω feed line (coax or waveguide — justify WR-90 vs coax). Match the antenna input impedance to 50 Ω **on a Smith chart by hand**, then verify in scikit-rf. Report VSWR and return loss.
3. **Propagation:** Compute FSPL over the path; add atmospheric/rain attenuation at 10 GHz; check first-Fresnel-zone clearance and the radio horizon (4/3 model); discuss multipath and a plausible ducting scenario.
4. **Link/range budget:** Build the full budget — EIRP, path loss, receive gain, system noise temperature, cascaded noise figure, SNR, fade margin. State minimum detectable signal.
5. **Radar bridge:** Convert your one-way budget into the **two-way radar range equation** by inserting a target RCS, and solve for detection range. *(This is the literal handoff to Module 08.)*
6. **Validate:** Simulate the antenna in openEMS or xnec2c and reconcile gain/pattern/impedance against your hand calculations; explain every discrepancy.

Deliverable: a short written report + the Smith chart + the simulation output + the budget spreadsheet, with each assumption traced back to the module that justifies it.

## Common pitfalls & rust-knockers

- **dB bookkeeping errors:** mixing dBm/dBW/dBi/dB, dropping a factor of 2 for power vs voltage (10log vs 20log), forgetting two-way loss in radar. Re-internalize early.
- **Forgetting the displacement current** when reasoning about why radiation exists — it's the whole point of Ampère–Maxwell.
- **Far-field confusion:** computing gain/pattern as if you're in the far field while physically in the near field; forgetting the 2D²/λ rule when testing antennas.
- **Smith chart direction errors:** rotating the wrong way (toward generator = clockwise), or forgetting that one full revolution is λ/2, not λ.
- **Impedance vs admittance** mix-ups on the Smith chart during stub matching — keep track of which chart you're on (or which 180° rotation you've applied).
- **VSWR/Γ sign and reference:** Γ_L uses the *load* and Z₀; sign errors flip the standing-wave pattern.
- **v_p > c panic** in waveguides — phase velocity exceeding c is fine; information travels at group velocity.
- **Grating lobes** from element spacing > λ/2 when steering — easy to forget and it wrecks an array pattern.
- **Polarization mismatch** silently eating link margin (LP↔CP costs 3 dB; cross-pol can cost much more).
- **Treating short transmission lines as lumped** (or long ones as a node) — always compare length to λ first.
- **Skin depth neglect** when estimating conductor loss and required plating thickness.
- **Friis misuse:** wrong units, forgetting it's λ² not λ, or applying free-space Friis where the two-ray d⁴ regime actually rules.
- **Conflating directivity and gain** (gain includes efficiency).

## Self-assessment checklist

- [ ] I can state all four Maxwell equations (differential + integral) and explain each physically.
- [ ] I can derive the wave equation from Maxwell's equations.
- [ ] I can analyze a plane wave: η, v_p, λ, polarization, and time-average power density.
- [ ] I can compute α, β, skin depth, and the regime (conductor/dielectric) for given material parameters.
- [ ] I can solve normal- and oblique-incidence reflection/refraction, including Brewster and critical angles.
- [ ] I can compute Γ, VSWR, return loss, and Z_in along a transmission line.
- [ ] I can match an arbitrary complex load to 50 Ω on a blank Smith chart with no calculator.
- [ ] I can verify a match in scikit-rf and (if available) read S11 on a VNA.
- [ ] I can compute waveguide cutoff and guide wavelength and explain dominant-mode operation.
- [ ] I can relate antenna gain, directivity, beamwidth, and effective aperture, and pick an antenna type for a mission.
- [ ] I can write and steer an array factor, predict grating lobes, and explain AESA/5G beamforming and MIMO.
- [ ] I can compute FSPL and apply Friis cold.
- [ ] I can identify when reflection, diffraction, multipath, atmospheric, or ionospheric effects dominate.
- [ ] I can build a complete link budget with noise figure/system temperature and find SNR and fade margin.
- [ ] I can convert a one-way link budget into the two-way radar range equation.
- [ ] I can choose FDTD vs FEM vs MoM and run a basic antenna simulation, reconciling it with theory.

## Canonical resources

**Core textbooks**
- Sadiku, *Elements of Electromagnetics* — accessible refresher, strong on vector calc and the gradient/divergence/curl mechanics (good Module 1–5 reactivation).
- Ulaby & Ravaioli, *Fundamentals of Applied Electromagnetics* — clean, application-oriented; excellent on transmission lines and the Smith chart.
- Hayt & Buck, *Engineering Electromagnetics* — classic, problem-rich.
- Pozar, *Microwave Engineering* — *the* reference for transmission lines, Smith chart, matching, waveguides, and microwave networks. Spend real time here for Modules 6–8.
- Balanis, *Antenna Theory: Analysis and Design* — the standard antenna and array reference (Modules 9–10).
- Stutzman & Thiele, *Antenna Theory and Design* — strong alternative, very readable on arrays.
- Rappaport, *Wireless Communications: Principles and Practice* — propagation models, fading, link budgets (Modules 11–12).

**Courses / lectures**
- MIT OCW 6.013 *Electromagnetics and Applications* (free) — full EM refresh.
- MIT OCW 8.02 *Electricity and Magnetism* — for the deepest-rust Module 1–2 reactivation.
- Pozar/Balanis-aligned graduate microwave and antenna lecture series on YouTube (many full courses).

**Tools (MODERN)**
- **scikit-rf** (Python) — network analysis, Smith charts, VNA data; ties RF to your software skills.
- **openEMS** — open-source FDTD; antennas and microstrip.
- **NEC2 / xnec2c** — wire-antenna MoM, fast hand-on antenna intuition.
- **QUCS / Qucs-S** — open circuit/matching simulator.
- **Keysight ADS, Ansys HFSS, CST Studio, Altair FEKO** — commercial standards; know what each is for even if you only use trials.
- A **VNA** (even a NanoVNA) — invaluable for making the Smith chart and VSWR tangible.

**References / handbooks**
- Smith chart blank PDFs (print several — you'll use them).
- ITU-R P-series recommendations for atmospheric/rain/ionospheric propagation models.
- *Antenna Engineering Handbook* (Volakis) for breadth on antenna types.
