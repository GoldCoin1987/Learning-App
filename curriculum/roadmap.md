# Learning Roadmap — Radar/RF + Software Hybrid

**Goal:** Refresh a strong EE/radar foundation and add modern supporting skills to be
competitive in a new job market post-retirement.

**Profile:** BS Electronics Engineering · former Radar Technician · current Software Developer.
This is a **refresh + modernize** plan, *not* a beginner curriculum. The rust is mostly on
the math/RF theory; the new material is modern tooling — SDR, ML-based signal processing,
modern embedded Linux, and current AI.

**The competitive thesis:** Radar/RF domain knowledge + real software ability is a rare
combination. Most RF engineers can't build software; most software devs don't understand the
physics. Sitting in that intersection — especially with modern DSP/ML and SDR — is the edge.

---

## How the topics connect

```
            MATH SPINE (complex #s, calculus, linear algebra, Fourier/DSP, probability)
                 | underpins everything below
   +-------------+-------------------------------------------+
   v             v                                           v
ANALOG  -->  DIGITAL  -->  EMBEDDED  ----------------->  RADAR (capstone)
electronics  electronics  systems prog                   ^   ^   ^
   |                          |                           |   |   |
   |                          v                           |   |   |
   |                    LINUX (host + embedded Linux)------+   |   |
   |                          |                               |   |
   |                          v                               |   |
   |                    NETWORKING (comms, protocols)---------+   |
   v                                                              |
WAVE PROPAGATION (EM, transmission lines, antennas) --------------+
   ^
ENGINEERING practice (systems thinking, design, measurement) — woven throughout
AI — cross-cutting; increasingly the back-end of modern radar/RF signal classification
```

Analog + digital are the foundation; embedded is where they meet code; wave propagation is
the physics of the channel; radar fires everything at once. Linux/networking/AI/engineering
are cross-cutting — picked up in service of the spine.

---

## Phases (recalibrated for refresh, not beginner)

### Phase 0 — Math spine (refresh, run in parallel)
Knock the rust off rather than relearn. Priority: complex numbers/phasors, Fourier/Laplace,
linear algebra (now also for ML), probability/detection theory.
**Milestone:** move fluidly between time-domain and spectrum views of a signal.

### Phase 1 — Analog electronics (refresh)
DC fundamentals, reactive components/impedance/resonance, BJT/MOSFET, **op-amps**, active
filters, noise & real-world non-idealities. You know this — speed-run to fluency.
**Milestone:** design/analyze an active filter and a transistor amp from memory; read a datasheet cold.

### Phase 2 — Digital → Embedded (refresh + modernize; leverage software strength)
Logic/state machines/ADC-DAC, MCU architecture, **embedded C at register level** (GPIO, UART,
SPI, I2C, ADC, PWM), RTOS concepts. New/modern: current toolchains, Rust-on-embedded, modern dev boards.
**Milestone:** drive a sensor over I2C/SPI on STM32/RP2040/ESP32 via interrupt, stream over UART — no library hand-holding.

### Phase 3 — Linux + Networking (cross-cutting, software-heavy → fast)
Linux process/filesystem/systemd/boot, kernel vs userspace, drivers & /dev + /sys, cross-compilation,
then **embedded Linux** (Yocto/Buildroot, device tree). Networking: TCP/IP, sockets, Ethernet/PHY,
wireless protocols (on-ramp to wave propagation).
**Milestone:** boot a custom Linux image on an SBC, talk to it over a socket, trace packets with Wireshark.

### Phase 4 — Wave propagation (refresh the gate before radar)
Maxwell (conceptual), EM waves/polarization, **transmission lines** (matching, reflections,
standing waves, Smith chart), antennas (patterns, gain, beamwidth, arrays), propagation/path loss.
**Milestone:** explain mismatch reflections; compute a link/path-loss budget.

### Phase 5 — Radar (capstone — modernize hard)
Radar equation, range/resolution/Doppler, **FMCW** (cheap + everywhere now), DSP chain
(matched filter, range-Doppler FFT, **CFAR**), beamforming/phased arrays.
**Modern emphasis (the competitive part):** SDR (RTL-SDR -> HackRF/USRP), GNU Radio,
and **ML for radar** — target classification, micro-Doppler gesture/gait recognition, clutter rejection.
**Capstone project:** produce a range-Doppler plot from raw captures (TI mmWave or RTL-SDR),
processed on an embedded Linux target. Touches all nine topics at once and is a portfolio piece.

---

## Where AI and Engineering fit
- **AI** — run as a parallel track: linear algebra + probability -> classical ML -> neural nets
  -> a DSP/signals application. Lands naturally as the radar/RF classification back-end in Phase 5.
- **Engineering** — not a separate topic; the practice woven through every phase
  (systems thinking, tradeoffs, instrumentation: scope/logic analyzer/spectrum analyzer/VNA, debugging discipline).

---

## Cadence & job-market notes
- One spine phase at a time + Linux/networking/AI as parallel light tracks + a hardware
  project at the end of each phase so it sticks. ~12-24 months at a deliberate pace; faster given the existing foundation.
- **For competitiveness, emphasize and build portfolio around:** SDR + GNU Radio, ML-based
  signal processing, modern embedded Linux, and the radar capstone project. These are the
  differentiators that modernize the existing radar/EE credentials.

## Next moves (open)
- [ ] Expand a chosen phase into a week-by-week plan with specific resources/exercises
- [ ] Curated canonical resource list (Sedra-Smith, Horowitz & Hill, Razavi, radar texts, etc.)
- [ ] Pick hardware: starter kit + dev board + SDR/radar module matched to budget
- [ ] Choose a starting phase
