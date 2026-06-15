# Radar Systems — Lesson Plan

**Role in the arc.** This is the capstone (topic 08). Radar is the one engineering system that *requires* essentially everything before it: the linear algebra, Fourier analysis, probability, and complex-baseband math from **01**; the amplifiers, filters, mixers, and noise figure of **02 (analog/RF)**; the ADCs, sampling theory, quantization, and FPGA/fixed-point pipelines of **03 (digital)**; the real-time compute and DMA of **04 (embedded)**; the processing host, drivers, and toolchain of **05 (linux)**; data movement, timing, and streaming transport of **06 (networking)**; and the propagation, antennas, polarization, and link budget of **07 (wave propagation)**. It then hands off to **09 (AI/ML)** for classification (micro-Doppler, gait/gesture, learned clutter suppression). Treat every earlier module as a callable subroutine here.

**What this module is for you specifically.** You already lived the operational layer as a radar technician and you have the EE fundamentals, so this is *refresh + modernize*. The plan deliberately spends the first third reactivating the classical radar mental model (range equation, pulse-Doppler, CFAR, tracking) and then pivots hard into the layer that did not exist — or was not accessible — when you were turning wrenches: software-defined radio (RTL-SDR → HackRF → USRP), GNU Radio, cheap FMCW mmWave modules (TI IWR/AWR), all signal processing reimplemented in Python/NumPy/SciPy so you *own* the math, and machine learning on radar returns. That modern stack is your competitive differentiator in the job market: plenty of people can recite the radar range equation; far fewer can take raw IQ off a $30 dongle, build a range-Doppler map in NumPy, run CFAR, and feed a micro-Doppler spectrogram to a classifier — and explain every block. The plan is sequential; do not skip "easy" modules, because the rust-knockers there are exactly what an interviewer probes.

## Learning outcomes

By the end of this module you will be able to:

1. Derive and apply the radar range equation, and reason quantitatively about detection range, SNR, and the trade space (power, aperture, frequency, integration, losses).
2. Explain and compute range, range resolution, unambiguous range, velocity, velocity resolution, and the PRF/ambiguity trade-offs for pulsed, CW, FMCW, and LFM/chirp waveforms.
3. Draw the full radar block diagram from antenna to detection and describe the function, impairments, and modern (SDR) equivalent of every block.
4. Implement matched filtering / pulse compression, Doppler processing, and a range-Doppler map from raw complex IQ in Python/NumPy/SciPy.
5. Apply detection theory (Neyman-Pearson, ROC) and implement CA-/OS-CFAR detectors with a defensible probability of false alarm.
6. Model clutter and interference and apply MTI / pulse-Doppler / Doppler-domain suppression, and articulate where learned (ML) suppression helps.
7. Implement a tracker: nearest-neighbor and probabilistic data association feeding an alpha-beta and then a Kalman/IMM filter.
8. Explain phased arrays and digital beamforming (array factor, steering vectors, grating lobes, taper), monopulse angle estimation, and the SAR/ISAR imaging idea.
9. Operate an SDR end-to-end (RTL-SDR → HackRF → USRP) and a TI mmWave FMCW EVM, capture IQ, and process it offline and in near-real-time, ideally on embedded Linux.
10. Build and defend an ML pipeline for radar (micro-Doppler spectrograms → gait/gesture classification; learned clutter suppression) including honest evaluation.

## Module breakdown

### Module 0: Orientation, IQ, and the complex-baseband mental model
- **Concepts:** What radar measures (range, radial velocity, angle, RCS, sometimes polarization/micro-motion) and why it is fundamentally an *echo-timing-plus-phase* problem. Reactivate complex baseband: the analytic signal, I and Q as the real/imaginary parts of the complex envelope, why we down-convert to baseband, and why phase carries Doppler. Sampling and Nyquist for *complex* (IQ) signals vs real signals (complex sampling needs fs ≥ B, not 2B). Units discipline: dB, dBm, dBW, dBsm (RCS), dBi/dBic, noise in dBm/Hz, kTB. The decibel mental arithmetic an interviewer expects on a whiteboard (3 dB = ×2, 10 dB = ×10, 30 dB = ×1000).
- **Why it matters / connections:** Everything downstream is complex-baseband DSP (01) sampled by an ADC (03). If IQ is fuzzy, range-Doppler and beamforming will be fuzzy. dB fluency is the single fastest "is this person actually an RF person" filter in interviews.
- **Hands-on / exercises:** In NumPy: synthesize a complex tone, plot magnitude/phase; build a quadrature down-converter (multiply by e^{-j2πf t}, low-pass, decimate) and recover IQ from a real passband signal; verify Nyquist by deliberately under-sampling and watching aliasing. Write a tiny helper module `radarutils.py` (dB/linear conversions, FFT-with-fftshift wrapper, windowing) you will reuse in every later module.
- **You've got it when:** You can state, without notes, why a real ADC at fs can represent complex bandwidth fs and explain to a non-radar colleague what I and Q physically are and where Doppler lives.

### Module 1: Radar principles, history, and taxonomy
- **Concepts:** The basic echo principle and Christian Hülsmeyer → Watson-Watt/Chain Home → WWII magnetron → modern phased arrays and automotive/mmWave/SDR radar timeline. Monostatic vs bistatic vs multistatic. Primary vs secondary (transponder/IFF) radar. Search vs track vs imaging roles. Active vs passive radar. The major application domains: air/sea surveillance, weather (dual-pol), automotive (FMCW), through-wall/vital-signs, ground-penetrating, SAR/ISAR, and the new world of consumer gesture radar (Google Soli). Why frequency choice drives everything (size, resolution, atmospheric loss).
- **Why it matters / connections:** Frames the rest. Your operational background lives here — connect the equipment you maintained to its place in this taxonomy. Bistatic/passive radar is a natural SDR project later.
- **Hands-on / exercises:** Write a one-page "radar systems I have touched" map placing your prior equipment into the taxonomy (band, role, waveform, antenna type) — this becomes interview narrative. Sketch a monostatic timeline diagram (pulse out, echo in, range from delay).
- **You've got it when:** You can classify any radar you read about by geometry, role, waveform, and band in under a minute, and connect frequency choice to physical size and resolution.

### Module 2: The radar range equation and SNR budget
- **Concepts:** Derive the monostatic range equation step by step: transmit power density at range R, intercept by target RCS σ, re-radiation back, capture by antenna aperture, into Pr = (Pt G² λ² σ) / ((4π)³ R⁴ L). The R⁴ law and what it means operationally. Noise: thermal noise N = kTB, noise figure F, system noise temperature; SNR = Pr / N. Detection-range form solving for R_max. Integration gain (coherent vs non-coherent), the radar equation in terms of energy not power (the integral form), duty cycle and average vs peak power. Loss budget (atmospheric, scan, beam-shape, straddle, processing, plumbing). RCS basics: definition, fluctuation models (Swerling 0–IV), why RCS is aspect- and frequency-dependent.
- **Why it matters / connections:** This is *the* equation. Noise figure and kTB tie directly to 02; aperture/gain/λ tie to 07; integration gain ties to coherent processing in Module 6. Interviewers love "if I double the antenna diameter, what happens to range?" (answer through G ∝ A and the R⁴ law).
- **Hands-on / exercises:** Build a parameterized range-equation calculator in Python that outputs R_max and plots SNR vs range and SNR vs RCS, with a full itemized loss budget. Add Swerling-aware single-pulse and N-pulse-integrated detection range. Sanity-check it against a worked example from Skolnik.
- **You've got it when:** You can rearrange the range equation to solve for any variable from memory, explain why range scales as the fourth root of power, and produce a defensible link budget with named loss terms.

### Module 3: Range, resolution, PRF, and the ambiguity trade space
- **Concepts:** Range from round-trip time, R = cτ/2. Unambiguous range R_u = c/(2·PRF) and range ambiguity (second-time-around echoes). Range resolution ΔR = c/(2B) — *bandwidth*, not pulse length, sets resolution once you allow pulse compression. Duty cycle, range/eclipsing blind zones. Doppler frequency fd = 2vr/λ and the velocity-measurement view. Unambiguous velocity from PRF (v_u = ±λ·PRF/4) and velocity resolution from coherent processing interval (CPI) duration. The fundamental PRF tension: low PRF → unambiguous range but ambiguous Doppler; high PRF → unambiguous Doppler but ambiguous range; medium PRF + PRF staggering/diversity to resolve both. Introduce the range-Doppler grid as the natural output.
- **Why it matters / connections:** This trade space is the heart of waveform design and a guaranteed interview topic. Connects PRF choice to the application (weather vs airborne intercept). Sets up Module 6 (Doppler) and Module 7 (ambiguity function) rigorously.
- **Hands-on / exercises:** Extend the calculator: given PRF, B, fc, CPI, print R_u, ΔR, v_u, Δv and flag where a given target is ambiguous. Simulate two targets at ranges that fold (second-time-around) and show the apparent vs true range. Implement PRF staggering and show ambiguity resolution by Chinese-remainder reasoning.
- **You've got it when:** Given any (PRF, B, fc, CPI) you can immediately produce all four limits and explain the PRF dilemma and at least two ways to break it.

### Module 4: The radar block diagram, end to end (classical and SDR equivalent)
- **Concepts:** Walk every block: antenna → circulator/duplexer (T/R switching, protect the receiver) → LNA → mixer + LO (superhet down-conversion, image rejection) → IF amplifier/filter → second down-conversion to baseband → IQ demodulator → ADC → digital receiver / DDC → signal processor → detector → tracker → display/data-out. Transmitter side: waveform generator/DDS, up-converter, high-power amplifier (magnetron/klystron/TWT vs solid-state GaN), pre-driver. Timing and coherence: STALO/COHO, the master oscillator, why phase coherence pulse-to-pulse is mandatory for Doppler. Dynamic range, sensitivity time control (STC), AGC, gain distribution and the cascade noise figure (Friis). The *modern* mapping: in an SDR the mixer/LO/IF/ADC collapse into one chip (RTL-SDR's RTL2832U+R820T, or USRP's AD936x RFIC), and the signal processor becomes your Linux host/FPGA — so "the receiver" is now mostly software.
- **Why it matters / connections:** This is where your technician knowledge is gold — refresh it, then explicitly overlay the SDR equivalent so you can speak both languages. Friis cascade NF ties to 02; ADC dynamic range and DDC tie to 03; the processing host ties to 04/05.
- **Hands-on / exercises:** Draw the classical block diagram from memory, then annotate each block with its SDR/RFIC equivalent. Compute a cascaded noise figure and gain budget for a simple receiver chain in Python (Friis). Open the RTL-SDR / USRP block diagram and label every stage against your drawing.
- **You've got it when:** You can point at any block, state its job, its dominant impairment, and its modern SDR realization, and compute where the system noise figure is actually set.

### Module 5: Waveforms — pulsed, CW, FMCW, LFM/chirp, pulse compression, matched filtering
- **Concepts:** Simple pulse: resolution vs energy conflict (short pulse = good resolution, low energy). The fix — *pulse compression*: transmit a long coded/chirped pulse, compress on receive. Linear FM (chirp/LFM): instantaneous frequency sweep, time-bandwidth product, compression ratio = TB. Matched filter: definition (correlate received signal with conjugate time-reversed replica), why it maximizes output SNR, equivalence to correlation and to frequency-domain multiply-by-conjugate. Range sidelobes and windowing/weighting (Taylor, Hamming) trade-off (sidelobe level vs resolution/SNR loss). Phase-coded waveforms (Barker, polyphase/Frank). CW radar: pure Doppler, no range. FMCW: the dominant modern short-range architecture — sawtooth/triangle sweep, the *beat frequency* gives range, range and Doppler coupling, the fast-chirp (FMCW radar-on-chip) scheme where range comes from per-chirp FFT and Doppler from chirp-to-chirp FFT. Stretch processing / dechirp-on-receive.
- **Why it matters / connections:** Matched filtering is the single most important DSP operation in radar and recurs in comms and sonar. FMCW is what automotive and TI mmWave modules use — directly relevant to the modern job market. Connects to FFT/correlation (01) and to the TI EVM work in Module 13.
- **Hands-on / exercises:** In NumPy/SciPy: generate an LFM chirp, build its matched filter, compress a noisy echo and measure the compression gain and sidelobe level; apply Taylor weighting and quantify the resolution/sidelobe trade. Implement a Barker-13 coded pulse and compress it. Simulate an FMCW sweep with one moving target and recover range from the beat-frequency FFT; show range-Doppler coupling on a triangle sweep.
- **You've got it when:** You can implement a matched filter two ways (time-domain correlation and frequency-domain conjugate-multiply), explain why TB sets compression gain, and explain why FMCW beat frequency encodes range.

### Module 6: Doppler processing — MTI, pulse-Doppler, and the range-Doppler map
- **Concepts:** Doppler shift recap and why coherence enables it. MTI (moving target indication): pulse-canceller (single/double delay-line canceller), the high-pass frequency response, blind speeds (multiples of v_u) and PRF staggering to fill them, MTI improvement factor. Pulse-Doppler: organize returns into a range × pulse (slow-time) data matrix; FFT across slow-time per range bin → the Doppler dimension; the result is the range-Doppler map (RDM). Coherent processing interval (CPI), Doppler resolution = 1/CPI, Doppler windowing for sidelobes. Coherent vs non-coherent integration gain. DC/zero-Doppler clutter notch. Spectral leakage and why windowing matters in slow-time too.
- **Why it matters / connections:** The RDM is the central data product and the input to CFAR (Module 8), tracking (Module 9), and ML (Module 14). This module is the bridge from "samples" to "decisions." FFTs from 01; the data-matrix layout matters for the FPGA/embedded implementation (03/04).
- **Hands-on / exercises:** Build the full slow-time/fast-time pipeline in NumPy: synthesize multiple pulses with targets at different ranges and velocities plus clutter at zero Doppler; range-compress (Module 5), assemble the data matrix, FFT across pulses, produce and plot a range-Doppler map. Implement a single- and double-delay-line MTI canceller and plot the frequency response and blind speeds. Compare coherent vs non-coherent integration SNR empirically.
- **You've got it when:** You can produce a clean range-Doppler map from synthetic IQ, point to where clutter, a slow target, and a fast target appear, and explain how CPI length and PRF set the Doppler axis.

### Module 7: The ambiguity function and waveform design
- **Concepts:** Definition of the ambiguity function χ(τ, fd) as the matched-filter response to a delayed/Doppler-shifted copy. The "thumbtack" ideal vs real shapes. Reading the AF: the zero-Doppler cut is range resolution, the zero-delay cut is Doppler resolution, the ridge of the LFM chirp (range-Doppler coupling). Volume-invariance ("you can't win everywhere") and how that constrains waveform design. Comparing AFs of simple pulse, LFM, coded, and CW. Brief: cognitive/adaptive waveforms and why this is an active research/SDR area.
- **Why it matters / connections:** This is the unifying theory that explains the resolution/ambiguity trade-offs you saw empirically in Modules 3 and 5. It is a "senior radar engineer" topic that distinguishes a refreshed technician from a casual hobbyist.
- **Hands-on / exercises:** Compute and plot (as a 2-D image and as cuts) the ambiguity function for a simple pulse and an LFM chirp in NumPy; visually confirm the LFM range-Doppler ridge and relate it to the coupling you saw in Module 5. Quantify resolution from the cuts.
- **You've got it when:** Given a waveform you can predict the shape of its ambiguity function and read range/Doppler resolution and coupling off it.

### Module 8: Detection theory and CFAR
- **Concepts:** Detection as hypothesis testing: H0 (noise only) vs H1 (target+noise). Neyman-Pearson criterion, likelihood ratio, threshold setting. Probability of detection Pd, probability of false alarm Pfa, the ROC curve, and how SNR and integration move you along it. Why a fixed threshold fails in real (non-stationary) clutter → constant false alarm rate (CFAR). Cell-averaging CFAR (CA-CFAR): guard cells, training/reference cells, the threshold multiplier α as a function of N and desired Pfa. Failure modes: clutter edges (CA fails), multiple closely spaced targets (target masking). Variants: GO-CFAR, SO-CFAR, OS-CFAR (ordered statistics) and when each wins. 2-D CFAR on the range-Doppler map. Detection in Swerling fluctuation, binary integration (M-of-N).
- **Why it matters / connections:** CFAR is the standard detector and an expected interview term. Builds directly on probability (01) and operates on the RDM (Module 6). It is the detection stage of your capstone.
- **Hands-on / exercises:** Implement CA-CFAR and OS-CFAR in NumPy (1-D first, then 2-D on a range-Doppler map). Derive the α-vs-Pfa relationship for CA-CFAR and verify empirically by Monte Carlo that you hit the target Pfa. Construct a clutter-edge scenario and show CA-CFAR's false-alarm spike, then show OS-CFAR handling it. Plot an ROC curve for a Swerling-I target by simulation.
- **You've got it when:** You can implement 2-D CFAR on a range-Doppler map, set the threshold for a stated Pfa and defend the number, and explain when to choose OS- over CA-CFAR.

### Module 9: Tracking — filtering and data association
- **Concepts:** From detections (plots) to tracks. The estimation problem: noisy position measurements, unknown velocity, process noise. Alpha-beta (and alpha-beta-gamma) filters as the intuition. The Kalman filter: state-space model, predict/update, process and measurement noise covariances (Q, R), the Kalman gain, why it is the optimal linear estimator. Nonlinear variants: EKF and UKF (radar measurements — range/azimuth/Doppler — are nonlinear in Cartesian state). Maneuvering targets: Interacting Multiple Model (IMM). Track lifecycle: initiation, confirmation (M-of-N), maintenance, deletion. Data association: gating, global nearest neighbor (GNN), probabilistic data association (PDA/JPDA) for clutter, brief mention of MHT. Track-while-scan vs dedicated track.
- **Why it matters / connections:** Kalman filtering is one of the most transferable skills in the whole curriculum — it appears in robotics, navigation, sensor fusion, and finance, which broadens your job market well beyond radar. Uses linear algebra and probability from 01.
- **Hands-on / exercises:** Implement an alpha-beta tracker, then a linear Kalman filter for a constant-velocity target with noisy range/angle measurements; plot true vs measured vs filtered tracks and the error covariance shrinking. Add an EKF for polar measurements. Add simple GNN gating with a couple of crossing targets and false alarms; observe track swaps and discuss JPDA as the fix. Optionally run the tracker on detections coming out of your Module 8 CFAR.
- **You've got it when:** You can derive the Kalman predict/update equations, explain Q vs R tuning, and run a tracker end-to-end on CFAR detections with realistic clutter.

### Module 10: Clutter and interference
- **Concepts:** Clutter taxonomy: surface (land/sea), volume (rain/chaff), discretes; clutter RCS via reflectivity σ0 and resolution-cell area/volume. Clutter statistics beyond Gaussian (Weibull, K-distribution, log-normal) and why CFAR choice depends on it. Clutter spectrum and spread (internal motion, platform motion → clutter Doppler spread, the airborne main-lobe/sidelobe clutter problem). Mitigation: MTI/pulse-Doppler notching (Module 6), space-time adaptive processing (STAP) at a conceptual level (why joint angle-Doppler filtering beats either alone on a moving platform). Interference: RFI, mutual interference between radars (a real automotive problem), jamming (noise, deception) and ECCM at a survey level, eclipsing/range-folded clutter. The modern angle: *learned* clutter suppression and interference mitigation with ML (Module 14) and why it is attractive when statistics are intractable.
- **Why it matters / connections:** Clutter is why radar is hard in the real world and why CFAR/MTI exist. The statistics tie back to Module 8 detector choice; STAP previews phased arrays (Module 11); learned suppression is a strong ML portfolio angle.
- **Hands-on / exercises:** Simulate Gaussian vs K-distributed clutter and show how a CA-CFAR tuned for Gaussian under-performs on heavy-tailed clutter. Add a clutter ridge to a range-Doppler map and suppress it with an MTI notch; quantify SINR improvement. Read one STAP tutorial and write a half-page explaining the angle-Doppler clutter ridge in your own words.
- **You've got it when:** You can describe what clutter looks like on an RDM, choose a CFAR/suppression strategy from the clutter statistics, and explain in plain language why STAP exists.

### Module 11: Antennas, scanning, phased arrays, and beamforming
- **Concepts:** Recap antenna gain/aperture/beamwidth/sidelobes (from 07). Mechanical scanning vs electronic scanning. The phased array: array of elements, element spacing (d ≤ λ/2 to avoid grating lobes), the *array factor*, the *steering vector*, beam steering by progressive phase shift. Analog vs digital beamforming; the move to all-digital arrays where each element has its own receiver/ADC (this is where SDR and radar converge). Amplitude tapering (Taylor/Chebyshev) for sidelobe control and the gain/sidelobe/beamwidth trade. Grating lobes, scan loss, beam broadening off boresight. Multiple simultaneous beams, adaptive nulling (put a null on a jammer), and the link to adaptive arrays/STAP. MIMO radar (orthogonal waveforms, virtual array) as used in automotive mmWave to synthesize a large aperture from few elements. Monopulse angle estimation: sum and difference beams, the monopulse ratio, why it gives sub-beamwidth angle accuracy in one pulse.
- **Why it matters / connections:** Phased arrays + digital beamforming are *the* modern radar frontier and where your RF + DSP + SDR skills combine into a high-value niche. Array factor is just a spatial DFT — explicitly connect it to the FFT (01). MIMO virtual arrays are exactly how the TI mmWave modules get angular resolution (Module 13).
- **Hands-on / exercises:** In NumPy: compute and plot the array factor of an N-element uniform linear array; steer the beam by applying a phase taper and watch the main lobe move; reduce d below λ/2 and above to make grating lobes appear/disappear; apply Taylor amplitude taper and quantify sidelobe reduction vs beam broadening. Implement digital beamforming on synthetic multi-element IQ (delay-and-sum). Implement monopulse sum/difference and estimate a target angle. Implement the MIMO virtual-array idea with 2 TX / 4 RX and show the synthesized 8-element aperture.
- **You've got it when:** You can derive the steering vector, predict grating-lobe onset from element spacing, do digital beamforming on IQ, and explain how a MIMO virtual array gets angular resolution from few physical elements.

### Module 12: Imaging radar — SAR and ISAR overview
- **Concepts:** Why a moving platform synthesizes a large aperture → fine cross-range resolution (SAR). Stripmap vs spotlight modes; the key result that azimuth resolution ≈ half the real antenna length (the counterintuitive "smaller antenna, finer resolution" fact). Range-Doppler/range migration concept, motion compensation, the role of platform geometry. ISAR: target motion (rotation) instead of platform motion provides the aperture (ship/aircraft imaging). Interferometric SAR (height), polarimetric SAR (classification), GMTI. This is a *survey* module — understand the principle and vocabulary, not the full processing chain.
- **Why it matters / connections:** SAR is a huge employer of radar/DSP/ML talent (defense, remote sensing, climate). The synthetic-aperture idea is the time-domain twin of the phased array (Module 11). SAR imagery is a major ML application area, bridging to 09.
- **Hands-on / exercises:** Read a SAR primer and write a one-page explanation of why a shorter real antenna yields finer azimuth resolution. Optional stretch: run a small open SAR processing example (e.g., a range-Doppler-algorithm tutorial on a public raw dataset) to see a focused image emerge. Download and view a Sentinel-1 SAR scene to connect theory to real data.
- **You've got it when:** You can explain the synthetic-aperture principle, the antenna-length/azimuth-resolution result, and the SAR/ISAR distinction to a non-specialist.

### Module 13: Software-defined radio and FMCW hardware — the modern lab
- **Concepts:** The SDR ladder and what each rung teaches: **RTL-SDR** (~$30, RX-only, ~2.4 MS/s, 8-bit — perfect for passive radar, ADS-B, learning IQ capture); **HackRF One** (half-duplex TX/RX, ~20 MHz, 8-bit — enables CW/ranging experiments); **USRP** (full-duplex, wide bandwidth, 12–16-bit, UHD driver, MIMO-capable — research-grade). Sample rate, bit depth, and dynamic-range implications (tie to 03). **GNU Radio**: flowgraph model, IQ file source/sink, the GRC GUI, and dropping into Python blocks; using it for capture and basic processing, then doing the heavy DSP in your own NumPy code for full control/understanding. IQ file formats and metadata (SigMF). Calibration, DC offset and IQ imbalance correction, LO leakage. **FMCW radar-on-chip**: the **TI mmWave** family (IWR1443/1642/6843 / AWR automotive), the EVM + DCA1000 capture card, mmWave Studio, the chirp configuration parameters (slope, idle, ADC samples, chirps per frame) and how they map directly onto Modules 3/5/6. Why mmWave (76–81 GHz) gives fine range/velocity in a tiny module. Brief: Google Soli / Infineon BGT60 60 GHz gesture radar.
- **Why it matters / connections:** This is the heart of the modern/competitive layer and the source of *real* IQ for the capstone. It operationalizes every prior module on hardware you can actually own. Drivers/USB/host processing tie to 04/05; streaming IQ over the network ties to 06.
- **Hands-on / exercises:** Install GNU Radio; capture FM broadcast / ADS-B with an RTL-SDR to confirm the toolchain and visualize a real spectrum. Record IQ to a file and re-process it in your own NumPy code (waterfall, channelize). With HackRF (if available) do a simple CW Doppler experiment (transmit a tone, detect Doppler from a moving target like a fan or a thrown ball) — the classic "speed-gun" demo. Configure a TI mmWave EVM (or work from a captured TI dataset if hardware is unavailable): set up a chirp profile, capture raw ADC via DCA1000, and load the raw data into Python. Document the full toolchain in your repo README so it is reproducible.
- **You've got it when:** You can capture real IQ from at least an RTL-SDR, process it in your own code, explain the dynamic-range difference across the SDR ladder, and map a TI mmWave chirp configuration onto the range/velocity equations from Module 3.

### Module 14: Machine learning for radar — the competitive differentiator
- **Concepts:** Why radar + ML now: cheap mmWave/SDR sensors produce rich data; classical detectors struggle with classification, intent, and intractable clutter statistics. Feature representations: the **micro-Doppler signature** (time-frequency spectrogram via STFT/spectrogram of slow-time) capturing limb/rotor/wheel micro-motions; range-Doppler maps and range-angle maps as image-like tensors; point clouds (from mmWave). Classic tasks: **human gait / activity recognition**, **gesture recognition** (Soli-style), **drone vs bird classification** (rotor micro-Doppler), **fall detection / vital signs**, hand/finger gesture. Pipelines: (a) hand-crafted features + SVM/random forest; (b) CNNs on spectrograms/RDMs; (c) sequence models (RNN/temporal CNN/transformers) on RDM sequences; (d) PointNet-style nets on radar point clouds. **Learned clutter/interference suppression** and denoising autoencoders. Hard parts unique to radar: tiny/imbalanced datasets, domain shift across sensors and geometries, the need for honest evaluation (subject-independent splits — never let the same person leak into train and test), and explainability. Connect explicitly to topic 09 for the ML mechanics (you build the *features* here; 09 deepens the models).
- **Why it matters / connections:** This is the single biggest market differentiator for someone with your background — RF/radar domain knowledge *plus* ML is rare and well-paid (automotive perception, defense, smart-home/health sensing). It directly fuses topic 09 with everything in 01–13.
- **Hands-on / exercises:** Generate micro-Doppler spectrograms from synthetic (and, ideally, real SDR/mmWave) data for a few motion classes; train a small CNN to classify them; report accuracy with a *subject-independent* split and a confusion matrix, and discuss failure modes. Use a public radar dataset (see resources) to train a gait or gesture classifier. Build a baseline (hand-crafted features + SVM) and beat it with a CNN to show you understand the trade-off. Optional: train a small autoencoder for clutter suppression and compare SINR vs MTI. Be ready to articulate evaluation pitfalls in an interview.
- **You've got it when:** You can turn radar IQ into a spectrogram/RDM tensor, train and *honestly* evaluate a classifier (subject-independent), beat a classical baseline, and explain to an interviewer why naive splits inflate accuracy.

## Capstone / integrative exercise

**Project: "Raw IQ to detections on embedded Linux — a software-defined radar processor."**

Build a single, well-documented repository that takes raw complex IQ and produces a CFAR detection list and (stretch) tracks and a micro-Doppler classification, running the processing chain on an embedded Linux target (e.g., Raspberry Pi or similar) to prove it works off the workstation.

Required pipeline (each stage reuses the module where you built it):
1. **Ingest** raw IQ — from a TI mmWave EVM capture (preferred), an FMCW/CW HackRF experiment, or a high-fidelity synthetic generator you write (must include thermal noise, clutter, and multiple Swerling targets at known range/velocity for ground-truth validation). Support a SigMF-style metadata sidecar.
2. **Pulse compression / range processing** — matched filter or FMCW range FFT (Module 5), with windowing.
3. **Doppler processing** — assemble the slow-time/fast-time matrix and FFT to produce a **range-Doppler map** (Module 6), with an MTI/clutter notch option (Module 10).
4. **Detection** — **2-D CA- or OS-CFAR** on the RDM at a stated Pfa, output a detection list with range/velocity/SNR (Module 8).
5. **Estimation** (stretch) — angle via beamforming/monopulse if multi-channel data is available (Module 11); a **Kalman tracker** over frames with gating/association (Module 9).
6. **Classification** (stretch) — micro-Doppler spectrogram → CNN to label motion/target type (Module 14).
7. **Deploy on embedded Linux** (Module/topic 04/05): cross-build or run the Python pipeline on the SBC; measure throughput and latency per stage; identify the bottleneck and propose (or implement) one optimization (vectorization, fixed-point, or an FPGA/GPU offload sketch). Optionally stream IQ to the SBC over the network (topic 06).

Deliverables: the repo with a clear README and reproducible setup; a notebook/report walking from raw IQ to RDM to CFAR detections with annotated figures; validation against synthetic ground truth (measured vs true range/velocity, achieved Pfa via Monte Carlo); a short "system performance" section with the link budget (Module 2) and the measured embedded latency. This artifact *is* your interview portfolio — it demonstrates the full stack from RF physics to ML on real hardware.

**You've got it when:** a stranger can clone the repo, run it on synthetic data, see a labeled range-Doppler map with correct CFAR detections, and read a report that connects every block back to first principles — and you can do the live "fan/ball Doppler" demo on request.

## Common pitfalls & rust-knockers

- **IQ/Doppler sign confusion.** Getting the sign of the LO mix or the FFT convention wrong flips approaching/receding. Always validate with a known-velocity synthetic target before trusting real data.
- **Confusing pulse length with range resolution.** Resolution is set by *bandwidth* (c/2B); pulse compression decouples energy (pulse length) from resolution. A classic interview trap.
- **fftshift / axis-labeling errors.** Half of all "wrong" range-Doppler maps are just unshifted FFTs or mislabeled Hz↔m/s↔m axes. Build axis helpers once (Module 0) and reuse.
- **Forgetting the R⁴ law / power-vs-energy.** Doubling power barely helps range; integration and aperture often help more. Know the energy (integral) form of the range equation.
- **CFAR tuned for the wrong statistics.** CA-CFAR assumes homogeneous Gaussian backgrounds; it false-alarms at clutter edges and masks closely spaced targets. Pick OS-CFAR when appropriate and *verify Pfa by Monte Carlo*, never just trust the formula.
- **Blind speeds / ambiguities ignored.** A target can vanish in an MTI notch or fold in range/Doppler. Always reason about R_u, v_u, and blind speeds for your PRF.
- **Windowing amnesia.** Forgetting slow-time and fast-time windows produces sidelobes that masquerade as targets; over-windowing throws away SNR and resolution. Know the trade.
- **Kalman tuning by vibes.** Wrong Q/R makes the filter diverge or lag. Understand what each covariance physically represents; validate with NEES/innovation consistency, not eyeballing.
- **SDR dynamic-range naivety.** An 8-bit RTL-SDR will not see a small target next to a strong one; strong signals cause LO leakage/DC spikes and IQ imbalance. Calibrate, and know when you need a USRP.
- **ML evaluation leakage.** Same subject/scene in train and test inflates accuracy and is a red flag to any serious interviewer. Use subject-/scene-independent splits and report them explicitly.
- **Treating GNU Radio as a black box.** Capturing in GNU Radio but never reimplementing the DSP yourself leaves gaps that interviews expose. Re-derive in NumPy at least once.
- **Linear-vs-dB arithmetic slips.** Mixing power and amplitude ratios, or adding/multiplying dB incorrectly. Drill the mental arithmetic until it is automatic.

## Self-assessment checklist

- [ ] I can derive the radar range equation and solve it for any variable from memory.
- [ ] I can explain the R⁴ law and the energy (integral) form, and build an itemized link budget.
- [ ] Given (PRF, B, fc, CPI) I can state R, ΔR, R_u, fd, v_u, and Δv and explain the PRF dilemma.
- [ ] I can draw the full radar block diagram and give each block's SDR/RFIC equivalent and dominant impairment.
- [ ] I can implement a matched filter two ways and explain why TB sets pulse-compression gain.
- [ ] I can explain why FMCW beat frequency encodes range and process a fast-chirp FMCW frame.
- [ ] I can build a range-Doppler map from raw IQ and locate clutter, slow, and fast targets on it.
- [ ] I can sketch and read an ambiguity function and predict range/Doppler resolution and coupling.
- [ ] I can implement 2-D CA- and OS-CFAR, set a threshold for a stated Pfa, and verify it by Monte Carlo.
- [ ] I can derive the Kalman predict/update equations and run a tracker on CFAR detections with clutter.
- [ ] I can describe clutter statistics, choose a suppression strategy, and explain STAP in plain language.
- [ ] I can derive a ULA steering vector, do digital beamforming on IQ, and explain grating lobes and MIMO virtual arrays.
- [ ] I can explain the synthetic-aperture principle and the SAR azimuth-resolution result.
- [ ] I can capture real IQ from an RTL-SDR (and ideally HackRF/USRP/TI mmWave) and process it in my own code.
- [ ] I can map a TI mmWave chirp configuration onto the range/velocity equations.
- [ ] I can turn radar data into a micro-Doppler spectrogram and train/honestly evaluate a classifier that beats a classical baseline.
- [ ] I have a portfolio repo running raw-IQ → RDM → CFAR (→ tracking/ML) on embedded Linux with a written report.
- [ ] I can do dB mental arithmetic instantly and never confuse power and amplitude ratios.

## Canonical resources

**Foundational textbooks**
- Merrill I. Skolnik, *Introduction to Radar Systems* (3rd ed.) — the classic; range equation, systems view, your operational refresher.
- Skolnik (ed.), *Radar Handbook* (3rd ed.) — encyclopedic reference.
- Mark A. Richards, *Fundamentals of Radar Signal Processing* (2nd ed.) — the DSP bible: matched filtering, Doppler, CFAR, the modern processing focus you need.
- Richards, Scheer, Holm (eds.), *Principles of Modern Radar: Basic Principles* (and the *Advanced* and *Applications* volumes, "POMR") — comprehensive and modern.
- Bassem R. Mahafza, *Radar Systems Analysis and Design Using MATLAB* — worked examples and code you can port to NumPy.
- Nadav Levanon & Eli Mozeson, *Radar Signals* — definitive on waveforms and the ambiguity function.

**Specialized**
- Ramon Nitzberg / or Bar-Shalom, Willett, Tian, *Tracking and Data Fusion* (and Bar-Shalom's *Estimation with Applications to Tracking and Navigation*) — Kalman/IMM/JPDA.
- Robert J. Mailloux, *Phased Array Antenna Handbook* — arrays and beamforming.
- Ian G. Cumming & Frank H. Wong, *Digital Processing of SAR Data* — SAR processing.
- Victor C. Chen, *The Micro-Doppler Effect in Radar* — the reference for the ML-feature physics in Module 14.
- Guerci, *Space-Time Adaptive Processing for Radar* — STAP.

**Courses / online**
- MIT Lincoln Laboratory "Introduction to Radar Systems" lecture series (freely available) — superb systems-level refresher.
- Mark Richards' Georgia Tech radar short courses / lecture notes.
- TI mmWave training (TI Resource Explorer, mmWave Academy, mmWave Studio docs) — directly for Module 13.

**Tools / software**
- **GNU Radio** (+ gr-osmosdr) — SDR flowgraphs and capture.
- **Python stack**: NumPy, SciPy (signal), Matplotlib; scikit-learn and PyTorch/TensorFlow for Module 14.
- **SigMF** — IQ recording metadata standard.
- **SDR hardware**: RTL-SDR (rtl-sdr/librtlsdr), HackRF One, Ettus USRP (UHD).
- **TI mmWave**: IWR/AWR EVM + DCA1000 capture card, mmWave Studio.
- **pysdr.org** (Marc Lichtman, *"A PySDR Guide to SDR and DSP using Python"*) — the best free modern IQ/SDR/Python primer; near-mandatory for the SDR modules.

**Datasets for ML (Module 14)**
- The University of Glasgow radar gait/activity datasets; "Dop-NET" and similar micro-Doppler gesture sets; DARPA/public drone micro-Doppler sets; TI mmWave people-counting/gesture sample datasets. (Search current availability; the field publishes new open sets regularly.)
