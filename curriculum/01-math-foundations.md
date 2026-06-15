# Math Foundations for EE/Radar/Software — Lesson Plan

This is module 01 of the curriculum — the bedrock that every later topic leans on. You already earned a BS in Electronics Engineering and worked hands-on as a Radar Technician, so the goal here is not to teach this material cold but to **knock the rust off, restore fluency, and modernize** the toolkit for today's job market (DSP, ML/AI, software-defined radio, beamforming). We deliberately start from algebra/trig "no matter how easy," because fluency at the bottom is what makes the top (Fourier, detection theory, eigen-decomposition for beamforming) feel effortless again. Treat the early modules as a fast warm-up; slow down where you feel friction.

Prerequisites: none beyond a prior engineering math background. By the end you should be able to read a radar signal-processing paper, an EM textbook chapter, or an ML linear-algebra derivation without stopping to re-derive the math. Cross-references point forward to: **02 Analog**, **03 Digital**, **07 Wave Propagation**, **08 Radar**, **09 AI**. The two highest-leverage threads for your target market are (1) **linear algebra → DSP/ML/beamforming** and (2) **probability/random processes → detection, estimation, and noise**. Spend extra time there.

## Learning outcomes

By the end of this module you will be able to:

- Manipulate algebraic, trigonometric, exponential, and logarithmic expressions fluently, including dB math.
- Move freely between rectangular, polar, and exponential forms of complex numbers and use phasors for AC/RF analysis.
- Work in Cartesian, cylindrical, and spherical coordinates and perform vector operations (dot, cross, gradient, divergence, curl).
- Differentiate and integrate single- and multivariable functions, and interpret results physically (rates, areas, flux, optimization).
- Solve the differential equations that govern RLC circuits and EM fields, by hand and via Laplace transform.
- Perform and interpret matrix operations, solve linear systems, and compute eigenvalues/eigenvectors/SVD — and explain why these underpin DSP, ML, and beamforming.
- Derive and apply Fourier series, the Fourier transform, the DTFT/DFT/FFT, the Laplace transform, and the z-transform, and explain how they relate.
- State and apply the sampling theorem and Nyquist criterion, and reason about aliasing and reconstruction.
- Apply probability, random variables, distributions, and estimators; characterize random processes and noise (especially in radar/comms).
- Explain the foundations of detection and estimation (likelihood ratio, ROC, matched filter, MLE/MAP, Cramér–Rao bound) at a level that feeds radar and ML.

## Module breakdown

### Module 1: Algebra, Functions, and dB Math (warm-up)

- **Concepts:**
  - Real number system, exponents, radicals, scientific/engineering notation, significant figures.
  - Polynomials: factoring, roots, the quadratic formula, polynomial long division, partial fractions (preview of Laplace/z inverse transforms).
  - Rational, exponential, and logarithmic functions; laws of exponents and logarithms; natural log and *e*.
  - Solving equations and inequalities; systems of linear equations (substitution/elimination — formalized later with matrices).
  - Function concepts: domain/range, composition, inverse functions, even/odd symmetry.
  - **Decibels and engineering units:** dB, dBm, dBW, dBi, dBc; power vs. voltage ratios (10·log vs. 20·log); SI prefixes; unit conversions.
  - Series and sequences: arithmetic/geometric series, the geometric series sum (foundation for z-transform and Taylor series).
- **Why it matters / connections:** Partial fractions are the workhorse for inverse Laplace (RLC transients, control) and inverse z-transform (DSP). dB math is pervasive across **02 Analog**, **07 Wave Propagation**, and **08 Radar** (link budgets, radar range equation, noise figure). Geometric series underpins the z-transform region of convergence.
- **Hands-on / exercises:**
  - Decompose `(3s+5)/((s+1)(s+2))` into partial fractions by hand.
  - Convert: 100 W to dBW and dBm; a voltage gain of 50 to dB; a noise figure of 3 dB to a linear factor.
  - Sum the geometric series `Σ a·r^n` and state the convergence condition.
- **You've got it when:** you can do dB ↔ linear conversions in your head for common values (3 dB ≈ 2×, 10 dB = 10×, 20 dB = 100× voltage) and split any proper rational function into partial fractions without notes.

### Module 2: Trigonometry and the Unit Circle

- **Concepts:**
  - Angles, radians vs. degrees, the unit circle, signs by quadrant.
  - The six trig functions, their graphs, periods, amplitudes, and phase shifts.
  - Fundamental identities: Pythagorean, sum/difference, double/half-angle, product-to-sum and sum-to-product.
  - Inverse trig functions and their ranges; `atan2` and why software uses it.
  - Sinusoids in engineering form: `A·cos(ωt + φ)`, angular frequency ω = 2πf, period, phase, time delay ↔ phase relationship.
  - Law of sines and cosines; triangle solving (geometry for antenna arrays and radar geometry).
- **Why it matters / connections:** Sinusoids are the atoms of AC, RF, and waves. Product-to-sum identities are literally how mixers/heterodyning work (**02 Analog**, **08 Radar**). Phase ↔ time-delay reasoning feeds **beamforming** (Module 7) and **wave propagation** (07). `atan2` matters for phase computation in software DSP.
- **Hands-on / exercises:**
  - Derive `cos(A)cos(B)` as a sum of cosines and relate it to a mixer producing sum and difference frequencies.
  - Express `3cos(ωt) + 4sin(ωt)` as a single `R·cos(ωt + φ)`.
  - Compute the phase shift in degrees for a 1 ns delay at 1 GHz.
- **You've got it when:** you can convert between `a·cos + b·sin` and amplitude-phase form instantly, and explain mixing using a trig identity.

### Module 3: Complex Numbers and Phasors

- **Concepts:**
  - Imaginary unit, rectangular form `a + jb` (engineering uses *j*), complex plane.
  - Polar and exponential forms; **Euler's formula** `e^(jθ) = cosθ + j·sinθ`; conversions in both directions.
  - Arithmetic: addition, multiplication, division, conjugate, magnitude, argument.
  - De Moivre's theorem; roots of unity (preview of DFT twiddle factors).
  - **Phasors:** representing sinusoids as complex amplitudes; impedance of R, L, C; reactance; phasor arithmetic for AC circuit analysis.
  - Complex exponentials as the natural basis for LTI systems and frequency-domain analysis.
- **Why it matters / connections:** Phasors are the bridge from this module into **02 Analog** (AC analysis, filters) and **07 Wave Propagation** (propagating waves `e^(j(ωt−kz))`). Roots of unity are exactly the DFT basis (Module 8). Complex baseband (I/Q) representation in modern SDR/radar rests entirely here.
- **Hands-on / exercises:**
  - Find the steady-state current phasor for a series RLC driven by `10cos(ωt)` at resonance and off-resonance.
  - Compute the 8th roots of unity and plot them; connect to an 8-point DFT.
  - Convert an I/Q sample pair to magnitude and phase.
- **You've got it when:** you instinctively reach for `e^(jθ)` instead of trig identities, and can analyze an AC circuit purely in phasor/impedance form.

### Module 4: Vectors and Coordinate Systems

- **Concepts:**
  - Vectors: components, magnitude, unit vectors, addition/scaling.
  - Dot product (projection, work, angle between) and cross product (area, torque, normal direction, right-hand rule).
  - Coordinate systems: **Cartesian, cylindrical, spherical** — and conversions among them.
  - Vector fields; line, surface, and volume elements in each coordinate system.
  - **Vector calculus operators:** gradient, divergence, curl, Laplacian; physical meaning of each.
  - Integral theorems: divergence (Gauss) theorem and Stokes' theorem (the math spine of Maxwell's equations).
- **Why it matters / connections:** This is the language of **07 Wave Propagation** and EM (Maxwell's equations are divergence/curl statements). Spherical coordinates describe antenna radiation patterns (**08 Radar**). Dot/cross products and projections also reappear in **linear algebra** (Module 6) and ML feature geometry (**09 AI**).
- **Hands-on / exercises:**
  - Convert a point and a vector field between Cartesian and spherical coordinates.
  - Compute the divergence and curl of a given field; interpret physically.
  - Use the divergence theorem to relate a flux integral to a volume integral on a simple field.
- **You've got it when:** you can write the gradient/divergence/curl in cylindrical or spherical coordinates with a quick reference, and explain what each Maxwell equation says in plain language.

### Module 5: Calculus — Single and Multivariable

- **Concepts:**
  - **Single-variable:** limits and continuity; the derivative (definition, rules: product, quotient, chain); derivatives of trig/exp/log; implicit differentiation.
  - Applications of derivatives: rates of change, optimization (max/min), L'Hôpital's rule, related rates.
  - Integration: antiderivatives, definite integrals, the Fundamental Theorem of Calculus; techniques (substitution, by parts, partial fractions, trig substitution).
  - **Series:** Taylor and Maclaurin series; convergence; small-angle and `e^x`/`sin`/`cos` expansions; linearization.
  - **Multivariable:** partial derivatives, gradient, directional derivatives, chain rule; multiple integrals; the **Jacobian** (change of variables — links to coordinate transforms in Module 4).
  - Optimization with the gradient; constrained optimization via **Lagrange multipliers**.
  - **MODERN add:** automatic differentiation vs. symbolic/numeric differentiation — why backprop in ML is just the chain rule applied mechanically.
- **Why it matters / connections:** Derivatives/integrals are the substrate of differential equations (Module 9) and transforms (Modules 8, 10). The gradient and Lagrange multipliers are the core of ML optimization (**09 AI** — gradient descent). Taylor series justify linearization in control and small-signal analysis (**02 Analog**).
- **Hands-on / exercises:**
  - Optimize a function of two variables; redo it with one equality constraint via Lagrange multipliers.
  - Expand `e^(jθ)` as a power series and recover Euler's formula.
  - Compute a double integral in polar coordinates using the Jacobian.
  - By hand, derive the gradient of a simple least-squares loss `||Ax − b||²` (sets up Modules 6 and 09).
- **You've got it when:** you see backprop, gradient descent, and the divergence theorem as different faces of calculus you already know.

### Module 6: Linear Algebra (high-leverage for DSP/ML/beamforming)

- **Concepts:**
  - Vectors and vector spaces; linear independence, basis, dimension, span; subspaces (column space, null space, row space).
  - Matrices: multiplication, transpose, inverse, rank, trace, determinant.
  - Solving linear systems: Gaussian elimination, LU decomposition, existence/uniqueness, least squares (normal equations, pseudoinverse).
  - **Eigenvalues and eigenvectors:** characteristic equation, diagonalization, similarity; symmetric/Hermitian matrices and the spectral theorem.
  - **Singular Value Decomposition (SVD):** geometry, low-rank approximation, condition number, relation to PCA.
  - Orthogonality, projections, Gram–Schmidt, QR decomposition.
  - Inner products, norms (L1, L2, L∞), positive-definite matrices, quadratic forms.
  - Complex/Hermitian matrices (essential for array signal processing).
  - **MODERN add:** matrices as the data structure of ML; tensors as a generalization; why GPUs exist (matrix multiply).
- **Why it matters / connections:** This is arguably the single most market-relevant module. **DSP** uses it for filter design and transforms; **beamforming** (radar/array processing) is eigen-decomposition of the spatial covariance matrix (MUSIC, MVDR/Capon, ESPRIT); **09 AI** is matrices end to end (PCA = eigen/SVD, linear regression = least squares, neural nets = matrix multiplies). The least-squares thread from Module 5 lands here.
- **Hands-on / exercises:**
  - Solve an overdetermined system by least squares two ways (normal equations and pseudoinverse) and compare.
  - Hand-compute eigenvalues/eigenvectors of a 2×2 and 3×3 symmetric matrix; diagonalize it.
  - Implement PCA on a small dataset using SVD (in NumPy); reconstruct with reduced rank.
  - Form a spatial covariance matrix for a simulated array and find its dominant eigenvector; relate it to direction-of-arrival (preview of beamforming in **08 Radar**).
- **You've got it when:** you can explain — and compute — why the eigenvectors of a covariance matrix point toward signal subspaces, and you reach for SVD when a matrix is ill-conditioned.

### Module 7: Differential Equations (RLC, control, and EM)

- **Concepts:**
  - First-order ODEs: separable, linear (integrating factor); RC and RL transient response; time constants.
  - Second-order linear ODEs with constant coefficients: characteristic roots; **overdamped, critically damped, underdamped** responses; natural frequency and damping ratio.
  - **RLC circuits:** the canonical second-order system; resonance, Q factor; step and impulse response.
  - Forced response, resonance, and the connection to phasors (sinusoidal steady state).
  - Systems of ODEs and state-space form (links to Modules 6 and 10).
  - Partial differential equations: the **wave equation** and **diffusion/heat equation**; separation of variables; boundary conditions (foundation for EM and guided waves).
  - Numerical methods: Euler, Runge–Kutta (how simulators actually solve these).
- **Why it matters / connections:** RLC and time-constant intuition feeds **02 Analog** directly. The wave equation is the heart of **07 Wave Propagation**. State-space ODEs connect to control and to the z-transform/discrete state-space in **03/04**. Damping ratio and natural frequency are the vocabulary of filter and control design.
- **Hands-on / exercises:**
  - Solve a series RLC step response by hand for all three damping cases; sketch each.
  - Express the same RLC system in state-space form and identify the system matrix's eigenvalues (tie to Module 6).
  - Separate variables on the 1-D wave equation and find the standing-wave modes of a string/transmission line.
- **You've got it when:** given an RLC circuit you can immediately state whether it rings, and you recognize eigenvalues of the state matrix as the system's natural frequencies/poles.

### Module 8: Frequency-Domain Transforms (Fourier, Laplace, z)

- **Concepts:**
  - **Fourier series:** periodic signals as sums of harmonics; trigonometric and complex-exponential forms; spectrum; Gibbs phenomenon; Parseval's theorem.
  - **Continuous Fourier transform (CTFT):** definition, key pairs, properties (linearity, time/frequency shift, scaling, convolution ⇄ multiplication, modulation), duality.
  - **Laplace transform:** definition, region of convergence, transform pairs, properties; using it to solve ODEs (especially RLC); **transfer functions, poles and zeros**, stability.
  - Convolution, impulse response, and LTI system analysis tying time and frequency domains together.
  - **Discrete-time transforms:** DTFT; the **DFT** and **FFT** (and why the FFT matters computationally); leakage, windowing, zero-padding.
  - **z-transform:** definition, region of convergence, relation to Laplace (s ↔ z mapping), inverse via partial fractions; digital filter transfer functions, poles/zeros in the z-plane, stability (unit circle).
  - The big picture: how CTFT, Laplace, DTFT, DFT, and z-transform relate (continuous vs. discrete, periodic vs. aperiodic).
  - **MODERN add:** spectrograms / short-time Fourier transform and a one-paragraph orientation to wavelets; FFT libraries in software (NumPy/SciPy, FFTW).
- **Why it matters / connections:** This is the core of **03 Digital/DSP** and a pillar of **08 Radar** (pulse compression, Doppler processing, range-Doppler maps are FFTs). Laplace transfer functions feed **02 Analog** filter and control design. The convolution theorem underpins matched filtering (Module 11) and CNNs (**09 AI**). Windowing/leakage is everyday practical DSP knowledge.
- **Hands-on / exercises:**
  - Compute the Fourier series of a square wave; observe Gibbs ringing as you add harmonics.
  - Solve an RLC step response with the Laplace transform; identify poles and relate them to the damping from Module 7.
  - In NumPy: FFT a sum of two sinusoids, then add a window and compare leakage; zero-pad and observe interpolation.
  - Design a simple first-order IIR digital filter; plot its poles/zeros and frequency response.
- **You've got it when:** you can move among s-plane, z-plane, and frequency response fluidly, and you can predict an FFT's output (bin spacing, leakage) before running it.

### Module 9: Sampling, Nyquist, and Quantization

- **Concepts:**
  - Ideal sampling as impulse-train multiplication; spectral replication.
  - **Nyquist–Shannon sampling theorem**; Nyquist rate; **aliasing** and folding.
  - Anti-alias filtering; reconstruction (sinc interpolation, zero-order hold, DACs).
  - **Bandpass sampling / undersampling** (deliberate aliasing for RF — directly relevant to SDR/radar receivers).
  - Quantization: levels, quantization noise, SNR vs. bits (the ~6 dB/bit rule), dither.
  - I/Q sampling and complex baseband; decimation and interpolation; multirate basics.
- **Why it matters / connections:** The hinge between continuous (analog/RF) and discrete (software) worlds — central to **03 Digital**, **04 Embedded**, and modern **08 Radar** receivers (digital IF, SDR). Bandpass sampling is a key modern technique you'll see in software-defined radio job descriptions.
- **Hands-on / exercises:**
  - Sample a sinusoid above and below Nyquist; show the aliased frequency both in math and in a NumPy plot.
  - Compute required ADC bits for a target SNR using the 6 dB/bit rule.
  - Demonstrate bandpass sampling: pick a carrier and sample rate that fold an RF signal to a usable baseband.
- **You've got it when:** given a signal bandwidth and sample rate you can predict the alias frequency instantly, and you can justify an ADC bit-depth choice from an SNR spec.

### Module 10: Probability and Statistics

- **Concepts:**
  - Probability axioms, conditional probability, independence, **Bayes' theorem**.
  - Random variables: discrete and continuous; PMF, PDF, CDF; expectation, variance, moments.
  - Key distributions: Bernoulli, binomial, Poisson, uniform, **Gaussian/normal**, exponential, Rayleigh and Rician (radar/fading), chi-squared (detection), and the Central Limit Theorem.
  - Joint distributions, covariance, correlation, conditional/marginal distributions; multivariate Gaussian (ties to Module 6 covariance matrices).
  - Statistics: sampling, estimators (bias, variance, consistency), confidence intervals, hypothesis testing, p-values.
  - **MODERN add:** the frequentist vs. Bayesian distinction; bootstrapping/resampling; why ML is applied probability (likelihoods, priors, regularization as priors).
- **Why it matters / connections:** Foundation for random processes/noise (Module 11), detection/estimation (Module 12), and **09 AI** (everything from logistic regression to Bayesian methods). Rayleigh/Rician/chi-squared distributions are specifically the math of radar clutter and detection statistics (**08 Radar**). The multivariate Gaussian links covariance matrices (Module 6) to real estimation.
- **Hands-on / exercises:**
  - Work a Bayes' theorem problem (e.g., radar detection given false-alarm and detection probabilities).
  - Simulate the CLT: average many uniform samples and watch a Gaussian emerge.
  - Fit a Gaussian to data; compute a confidence interval and run a hypothesis test.
- **You've got it when:** you can apply Bayes' theorem without hesitation and you recognize a covariance matrix as a multivariate-Gaussian parameter, not just a table of numbers.

### Module 11: Random Processes and Noise

- **Concepts:**
  - Random/stochastic processes; ensembles; stationarity (strict vs. wide-sense); ergodicity.
  - **Autocorrelation and cross-correlation;** power spectral density (PSD); the **Wiener–Khinchin theorem** (PSD ⇄ autocorrelation via Fourier).
  - **Noise:** thermal/Johnson noise (kTB), shot noise, white vs. colored noise, **AWGN**; noise figure and noise temperature (links to **02 Analog**, **08 Radar** link budgets).
  - Filtering of random processes through LTI systems (input/output PSD relationship).
  - SNR, matched-filter SNR gain (sets up Module 12).
  - **MODERN add:** generating and simulating noise in software; Monte Carlo methods for system-level performance estimation.
- **Why it matters / connections:** This is where probability meets signals. Essential for **08 Radar** (clutter, noise-limited detection, integration gain) and **06 Networking/comms** (channel models). PSD and autocorrelation are everyday DSP tools. Matched-filter intuition flows straight into detection theory.
- **Hands-on / exercises:**
  - Generate AWGN in NumPy; estimate its autocorrelation and PSD; verify Wiener–Khinchin.
  - Pass white noise through a simple filter and verify the output PSD shape.
  - Compute the noise power for a given bandwidth and temperature (kTB) and convert to dBm.
- **You've got it when:** you can explain why a matched filter maximizes SNR and you can compute a receiver's noise floor from bandwidth and temperature.

### Module 12: Detection and Estimation Basics

- **Concepts:**
  - Hypothesis testing framing: H0 vs. H1; probability of detection (Pd), probability of false alarm (Pfa), miss.
  - **Likelihood ratio test (LRT)** and the Neyman–Pearson criterion; thresholds; **ROC curves**.
  - The **matched filter** as the optimal detector in AWGN; correlation receiver; pulse compression preview.
  - **CFAR** (constant false-alarm rate) detection — core radar technique.
  - Estimation theory: **maximum likelihood (MLE)**, **maximum a posteriori (MAP)**, minimum mean-square error (MMSE); bias/variance.
  - **Cramér–Rao lower bound** (fundamental limit on estimator variance); Fisher information.
  - Connections to ML: classification as detection, MLE/MAP as the basis of model training, loss functions as negative log-likelihoods.
- **Why it matters / connections:** This is the capstone of the probabilistic thread and the direct mathematical foundation of **08 Radar** (detection, CFAR, parameter estimation of range/Doppler/angle) and a conceptual bridge to **09 AI** (classification, MLE training, decision theory). It also reuses the matched filter (Module 11) and the covariance/eigen machinery (Module 6).
- **Hands-on / exercises:**
  - Implement a matched filter for a known pulse in noise; sweep threshold and plot an ROC curve.
  - Simulate a cell-averaging CFAR detector and measure its false-alarm rate.
  - Compute the MLE for the mean of Gaussian samples; compare its variance to the Cramér–Rao bound.
- **You've got it when:** you can derive a detector from a likelihood ratio, read an ROC curve, and explain how training an ML classifier is MLE/MAP in disguise.

## Capstone / integrative exercise

**Build a tiny end-to-end radar/DSP signal chain in Python (NumPy/SciPy/Matplotlib)** that exercises the whole module:

1. **Generate** a transmit pulse (e.g., a linear-FM chirp) — uses complex exponentials (M3), trig (M2), sampling (M9).
2. **Simulate** a target return: delay + Doppler shift + attenuation, then add AWGN (M11), with returns from multiple ranges.
3. **Matched-filter / pulse-compress** the return (convolution and FFT — M8; matched filter — M12).
4. **Doppler processing:** FFT across pulses to build a range-Doppler map (M6 linear algebra, M8 FFT).
5. **Detection:** apply a CFAR detector and produce an ROC by sweeping threshold over many Monte Carlo trials (M10–M12).
6. **(Stretch) Beamforming/DOA:** simulate a small antenna array, form the spatial covariance matrix, and estimate direction of arrival via its eigenvectors (MUSIC) — M4 geometry + M6 eigen + M11 covariance.

Write up: which mathematical tool did each stage rely on, and where would a real system differ? This single project touches every module and is portfolio-worthy for radar/DSP/SDR roles.

## Common pitfalls & rust-knockers

- **Engineering *j* vs. math *i*** — and sign conventions in `e^{±jωt}`; pick one and be consistent.
- **Radians vs. degrees** in code (NumPy uses radians) — a classic silent bug.
- **10·log vs. 20·log** — power ratios vs. amplitude ratios in dB.
- **Forgetting the Jacobian** when changing coordinates/variables in integrals.
- **Conflating Nyquist rate (2·B) with Nyquist frequency (fs/2)**; assuming you must sample above the highest frequency rather than above the bandwidth (bandpass sampling).
- **FFT misreadings:** bin spacing = fs/N, spectral leakage without windowing, and assuming the FFT gives a continuous spectrum.
- **Eigenvectors are not unique** (scale/sign), and only symmetric/Hermitian matrices guarantee real eigenvalues and orthogonal eigenvectors.
- **Normal equations vs. SVD/QR** — using `inv(AᵀA)` on ill-conditioned data; prefer SVD/QR.
- **Stationarity/ergodicity assumed without justification** in random-process work.
- **Confusing correlation with independence**, and Pfa with the threshold itself in detection.
- **Mixing up convolution and correlation** (time-reversal) when implementing matched filters.
- **Off-by-conjugate errors** in complex inner products / Hermitian transposes.

## Self-assessment checklist

- [ ] I can convert among rectangular, polar, and exponential complex forms and analyze an AC circuit entirely with phasors.
- [ ] I can do common dB ↔ linear conversions mentally and compute a noise floor from kTB.
- [ ] I can write gradient, divergence, and curl in spherical/cylindrical coordinates and state Maxwell's equations in words.
- [ ] I can differentiate/integrate fluently and set up a constrained optimization with Lagrange multipliers.
- [ ] I can solve an RLC circuit by hand (all damping cases) and via the Laplace transform, and find its poles.
- [ ] I can compute eigenvalues/eigenvectors and an SVD, and explain their role in PCA and beamforming.
- [ ] I can derive a Fourier series, use the convolution theorem, and predict an FFT's bin spacing and leakage.
- [ ] I can map between s-plane and z-plane and assess stability in each.
- [ ] I can determine the alias frequency for any sample rate and justify an ADC bit depth from an SNR target.
- [ ] I can apply Bayes' theorem and identify Rayleigh/Rician/chi-squared in a radar context.
- [ ] I can compute autocorrelation/PSD, apply Wiener–Khinchin, and explain why the matched filter is optimal in AWGN.
- [ ] I can build a detector from a likelihood ratio, read an ROC, run a CFAR, and connect MLE/MAP to ML training.
- [ ] I completed the capstone signal chain and can explain the math behind each stage.

## Canonical resources

**Refresher / breadth**
- *Mathematical Methods for Physics and Engineering* — Riley, Hobson & Bence (broad single-volume reference).
- *Schaum's Outlines* (Calculus; Complex Variables; Linear Algebra; Probability & Statistics) — cheap, drill-heavy, ideal for knocking off rust.
- Khan Academy (algebra → multivariable calculus, linear algebra, statistics) — fast warm-up for early modules.
- Paul's Online Math Notes — excellent free calculus/ODE/linear-algebra references.

**Linear algebra (high priority)**
- Gilbert Strang, *Introduction to Linear Algebra* + MIT 18.06 lectures (free on MIT OCW / YouTube).
- 3Blue1Brown, *Essence of Linear Algebra* (geometric intuition).

**Differential equations / signals & transforms**
- Boyce & DiPrima, *Elementary Differential Equations*.
- Oppenheim & Willsky, *Signals and Systems* (Fourier/Laplace/sampling — the classic).
- Oppenheim & Schafer, *Discrete-Time Signal Processing* (DTFT/DFT/z-transform, the DSP bible).
- Steven W. Smith, *The Scientist and Engineer's Guide to Digital Signal Processing* (free online, very practical for FFT/filters).

**Probability, random processes, detection/estimation**
- Papoulis & Pillai, *Probability, Random Variables and Stochastic Processes*.
- Steven M. Kay, *Fundamentals of Statistical Signal Processing*, Vol. I (Estimation) and Vol. II (Detection) — the canonical detection/estimation texts and directly radar-relevant.
- Mark Richards, *Fundamentals of Radar Signal Processing* (ties the math straight into radar — great bridge to module 08).

**Modern / software tooling**
- Python scientific stack: **NumPy, SciPy, Matplotlib** (do all exercises here); Jupyter notebooks for the capstone.
- *Mathematics for Machine Learning* — Deisenroth, Faisal & Ong (free PDF; connects linear algebra/calculus/probability to ML — directly market-relevant).
- 3Blue1Brown neural-network series (chain rule → backprop intuition).
- GNU Radio / a cheap RTL-SDR dongle (optional but excellent for making sampling/Nyquist/SDR concepts tangible).
