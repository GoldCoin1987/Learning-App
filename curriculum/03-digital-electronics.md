# Digital Electronics — Lesson Plan

This is module 03 of a ten-part refresh-and-modernize curriculum. You already hold a BS in Electronics Engineering and spent years as a radar technician, so the *physics* of voltage levels, noise margins, and signal integrity are in your bones — what this module does is re-seat the formal abstractions (Boolean algebra, FSMs, timing closure) and bolt on the modern toolchain (HDL on cheap FPGAs) that has become table stakes in today's hardware/embedded job market. We move at a brisk refresher pace but cover *everything*, foundational material included, because you asked to get back in the groove no matter how easy a topic looks.

Prerequisites: module 01 (math foundations — modular arithmetic, sets, basic combinatorics, sampling/Nyquist intuition) and module 02 (analog — op-amps, RC time constants, transmission-line and impedance basics, noise). Digital is, after all, analog that we've agreed to interpret in two states. This module feeds module 04 (embedded) very heavily — FSMs become firmware, ADC/DAC become peripherals — and feeds module 08 (radar) directly, where FPGA-based signal processing is now the dominant architecture. Flagged connections appear throughout as **[→04]**, **[→08]**, **[→06]**, etc.

## Learning outcomes

By the end of this module you will be able to:

- Fluently convert and compute across binary, octal, hex, BCD, and signed representations, and reason about overflow, fixed-point, and floating-point formats.
- Minimize arbitrary combinational logic by Boolean algebra, Karnaugh maps, and (conceptually) Quine–McCluskey, and recognize when a synthesizer does it for you.
- Design and analyze combinational building blocks (mux, decoder, encoder, comparator, adder, ALU) and sequential building blocks (latches, flip-flops, registers, counters, shift registers).
- Specify and implement finite state machines (Moore and Mealy) and map them onto both gates and HDL.
- Reason quantitatively about timing: propagation delay, setup/hold, clock skew, max frequency, and metastability — and design synchronizers.
- Explain and select among memory technologies (SRAM, DRAM, NOR/NAND flash, ROM/EEPROM) and logic families/levels (TTL, CMOS, LVCMOS, LVDS) with correct level-shifting and noise-margin reasoning.
- Use tri-state buffers and shared buses correctly, including bus contention avoidance.
- Analyze the analog↔digital bridge: sampling, quantization, SNR/ENOB, aliasing, and the major ADC and DAC architectures, and choose one for a given application. **[→04][→08]**
- Explain clock generation, crystal oscillators, PLLs, jitter, and clock distribution.
- **[MODERN]** Write, simulate, and synthesize Verilog (and read VHDL) for combinational and sequential designs, and deploy them to a low-cost FPGA dev board.

## Module breakdown

### Module 1: Number systems & binary arithmetic

- **Concepts:** Positional notation and radix; binary, octal, hexadecimal, and conversion among all of them (and to/from decimal). Why hex is the human-readable face of binary. Bits, nibbles, bytes, words, endianness (big vs little). Binary addition, subtraction, multiplication, division by hand. Signed representations: sign-magnitude, one's complement, two's complement (and *why* two's complement won — single zero, addition "just works"). Range and overflow detection (carry-out vs signed overflow / the V flag). Sign extension. Binary-coded decimal (BCD) and where it survives (displays, RTCs). Gray code and its use in encoders and clock-domain crossing. Fixed-point representation (Q-format) and scaling. Floating-point: IEEE 754 single/double — sign/exponent/mantissa, bias, denormals, NaN/Inf, rounding modes — at a working level. Saturating vs wrapping arithmetic.
- **Why it matters / connections:** Every layer above this assumes you compute in these systems reflexively. Fixed-point and saturating arithmetic are the daily bread of DSP and radar signal processing **[→08]**; Q-format shows up the moment you write FIR filters on an FPGA or DSP. Endianness bites in embedded and networking **[→04][→06]**. Gray code reappears in Module 6 (CDC).
- **Hands-on / exercises:** Convert a page of values among bases by hand, then check with `python3 -c`. Implement two's-complement add/subtract on paper and identify overflow. Hand-encode a few floats into IEEE 754 and verify with a converter. Write a fixed-point Q1.15 multiply and reason about the result's format.
- **You've got it when:** You can negate a two's-complement number, predict overflow from the carry/sign bits, and explain why 0.1 isn't exact in float — without looking anything up.

### Module 2: Boolean algebra & logic minimization foundations

- **Concepts:** Boolean variables, the operators AND/OR/NOT and derived XOR/NAND/NOR/XNOR. Truth tables. Axioms and theorems: identity, null, idempotence, complement, commutativity, associativity, distributivity, absorption, consensus. De Morgan's theorems (the workhorses). Duality principle. Canonical forms: sum-of-products (SOP, minterms) and product-of-sums (POS, maxterms); minterm/maxterm numbering. Functional completeness — why NAND alone (or NOR alone) can build anything. Positive vs negative logic conventions. Boolean simplification by algebra. Don't-care conditions.
- **Why it matters / connections:** This is the grammar of all digital design. De Morgan's lets you read any gate network and re-express it; functional completeness is why real chips are seas of NANDs. Minimization directly reduces gate count, power, and delay — and it's exactly what synthesis tools do under the hood in Module 8.
- **Hands-on / exercises:** Prove consensus and absorption from the axioms. Convert a truth table to both canonical SOP and POS. Rebuild XOR, MUX, and a half-adder using only NAND gates. Simplify a messy expression algebraically, then confirm equivalence with a truth table.
- **You've got it when:** You can apply De Morgan's in your head, write the canonical SOP from a truth table, and explain why a 2-input NAND is universal.

### Module 3: Logic gates, families, and levels (TTL/CMOS)

- **Concepts:** The basic gates and their symbols (ANSI/IEEE and the older shapes). Gate-level implementation: how CMOS builds gates from complementary pull-up (PMOS) and pull-down (NMOS) networks; why CMOS NAND/NOR are natural and AND/OR cost an extra inverter. Static power vs dynamic (switching) power, P = CV²f. Logic families and their history: TTL (74xx), LS, the CMOS 4000 series, and the modern HC/HCT/AC/ACT/LVC/LVT lines. Voltage levels and thresholds: V_OH, V_OL, V_IH, V_IL, and **noise margins** (your radar-tech instincts apply directly here). Supply rails: 5 V → 3.3 V → 2.5 V → 1.8 V and below. Fan-out and drive strength. Input protection, pull-up/pull-down resistors, floating-input hazards. Open-drain/open-collector outputs and wired-AND. Propagation delay and rise/fall times as a first look. Level shifting between domains and why it's mandatory. Power, ground, and decoupling (bypass) capacitors.
- **Why it matters / connections:** Mixed-voltage systems are everywhere in embedded **[→04]**; getting V_IH/V_IL wrong is a top cause of "it works on the bench, fails in the field." Open-drain is the electrical basis for I²C **[→04]**. Noise margins, decoupling, and rise times bridge straight back to your analog and signal-integrity knowledge from Module 02.
- **Hands-on / exercises:** In a logic sim, build gates from MOSFET-level models if available. Read datasheets for a 74HC00 and a 74LVC00 and tabulate their level thresholds; compute the noise margin when a 3.3 V part drives a 5 V input (and decide if it works). Wire an open-drain bus with a pull-up and observe wired-AND behavior.
- **You've got it when:** Given two parts' datasheets you can immediately say whether they interoperate directly or need a level shifter, and explain why every IC gets a 100 nF cap.

### Module 4: Combinational logic building blocks

- **Concepts:** Designing from spec → truth table → minimized logic → schematic. Multiplexers (2:1 up to n:1) and demultiplexers; muxes as universal logic (implementing any function). Decoders (e.g., 3:8) and encoders, priority encoders. Comparators (magnitude and equality). Parity generators/checkers. Code converters (binary↔BCD, binary↔Gray). Arithmetic: half-adder, full-adder, ripple-carry adder and its delay problem, carry-lookahead and carry-select adders, subtractors via two's complement, multipliers (array/Wallace at a conceptual level). The **ALU**: combining add/sub/AND/OR/XOR/shift under a function-select input, plus the flags (Zero, Carry, Negative, Overflow). Combinational hazards: static-1, static-0, and dynamic hazards, and glitch suppression with redundant terms.
- **Why it matters / connections:** This is the datapath. An ALU is the heart of every CPU you'll touch in Module 04, and the adders/multipliers here become the multiply-accumulate (MAC) units that dominate FPGA DSP and radar pulse compression **[→08]**. The mux is the single most-used structure in HDL (every `if`/`case` synthesizes to one).
- **Hands-on / exercises:** Design a 4-bit ripple-carry adder, then a carry-lookahead version, and compare delay. Build a 1-bit ALU slice and stack four to make a 4-bit ALU with flags — first in a logic simulator (Logisim Evolution or Digital), then **[MODERN]** in Verilog and simulate it. Implement a 7-segment decoder.
- **You've got it when:** You can sketch an n-bit ALU's structure from memory, explain why ripple-carry is slow, and identify a static-1 hazard on a K-map.

### Module 5: Karnaugh maps & minimization

- **Concepts:** The K-map as a geometric view of adjacency. 2-, 3-, 4-variable maps; Gray-code ordering of axes (and why adjacency = single-variable change). Grouping rules: groups of 1/2/4/8/16, wrap-around adjacency, largest-group-first. Prime implicants, essential prime implicants, and selecting a minimal cover. SOP vs POS minimization (grouping 1s vs 0s). Exploiting **don't-cares** for further reduction. 5- and 6-variable maps (paired maps) and where the method runs out. The Quine–McCluskey tabular method as the algorithmic generalization, and a conceptual nod to Espresso (what synthesizers actually use). Hazard removal by adding redundant (consensus) product terms.
- **Why it matters / connections:** K-maps build the intuition for *why* logic simplifies, even though tools now do the grinding. Recognizing essential prime implicants and don't-cares is exactly the reasoning that lets you read and trust synthesis reports in Module 8. Hazard-aware covering matters for asynchronous outputs.
- **Hands-on / exercises:** Minimize a dozen functions of increasing size by K-map, including don't-cares; verify each against the truth table. Do one by Quine–McCluskey and confirm you get the same prime implicants. Take a function with a known glitch and add the redundant term to kill it; observe in a timing simulator.
- **You've got it when:** You can fill and group a 4-variable K-map quickly, justify each grouping as a prime implicant, and use don't-cares without breaking correctness.

### Module 6: Sequential logic — latches, flip-flops, registers, counters

- **Concepts:** The leap from combinational to *stateful* logic: feedback creates memory. The SR latch (NAND and NOR forms) and its forbidden state. Gated/transparent D latch — level-sensitive. The edge-triggered D flip-flop (master–slave) — the fundamental storage element of synchronous design. JK and T flip-flops and their roles. Asynchronous vs synchronous set/reset/preset/clear. Building registers (parallel-load), shift registers (SISO/SIPO/PISO/PIPO), and using them for serialization, delay lines, and LFSRs (pseudorandom sequences, CRC) **[→06][→08]**. Counters: asynchronous (ripple) vs synchronous; up/down, modulo-N, BCD, ring and Johnson counters. Clock enables and the right way to "gate" a clock (don't — use enables). Synchronous design discipline: one clock, everything registered.
- **Why it matters / connections:** Flip-flops + combinational logic = every digital system that does anything over time. Shift registers and LFSRs are core to serial protocols, scramblers, and radar pseudo-noise codes **[→08]**. The synchronous-design discipline you adopt here is what makes FPGA timing closure tractable in Module 8.
- **Hands-on / exercises:** Build an SR latch and watch the forbidden state in a sim. Build a D flip-flop from latches and verify edge behavior. Design a synchronous mod-10 counter and a 4-bit LFSR; predict its sequence, then simulate. **[MODERN]** Re-implement the counter and shift register in Verilog with a proper synchronous `always @(posedge clk)` block.
- **You've got it when:** You can explain the difference between a latch and a flip-flop without hesitation, design a synchronous counter of any modulus, and articulate why gating clocks is a sin.

### Module 7: Finite state machines

- **Concepts:** FSMs as the formal model of sequential behavior. State, inputs, outputs, transitions. **Moore** (outputs depend on state only) vs **Mealy** (outputs depend on state + inputs) — and the trade-offs (Moore = glitch-free registered outputs, Mealy = fewer states, faster response). State diagrams and state/transition tables. State encoding: binary, Gray, and **one-hot** (and why one-hot is preferred on FPGAs). State minimization. The canonical three-block HDL structure: next-state logic, state register, output logic. Handling unused/illegal states and ensuring a safe reset and recovery. Datapath + control: FSMD (FSM with datapath) as the bridge to "real" designs. Hierarchical and communicating FSMs.
- **Why it matters / connections:** FSMs are the single most important design pattern you carry into firmware **[→04]** — every protocol handler, menu, debouncer, and control loop is an FSM, whether coded in C or HDL. On the hardware side they sequence every datapath. Radar timing controllers (PRI/PRF sequencing, mode control) are FSMs **[→08]**.
- **Hands-on / exercises:** Design a sequence detector (e.g., detect "1011" with overlap) as both Moore and Mealy; draw both diagrams, compare state counts. Implement a traffic-light or vending-machine controller. **[MODERN]** Code the sequence detector in Verilog using the three-block style, simulate with a testbench, then deploy to an FPGA and drive it with on-board buttons/LEDs. Write the *same* FSM as a C `switch` statement to feel the firmware parallel **[→04]**.
- **You've got it when:** You can take a word problem to a state diagram to a state table to working HDL, and choose Moore vs Mealy with a reason.

### Module 8: Timing, setup/hold, skew & metastability

- **Concepts:** Propagation delay (t_pd), contamination delay (t_cd). Flip-flop timing parameters: setup time (t_su), hold time (t_h), clock-to-Q (t_cq). The fundamental timing inequalities: setup constraint sets **max clock frequency** (t_cq + t_pd,max + t_su ≤ T_clk − skew); hold constraint (t_cq + t_pd,min ≥ t_h + skew) must hold at *any* frequency. Critical path analysis. Clock skew (positive and negative) and clock jitter, and their effect on both constraints. Pipelining to break long combinational paths and raise throughput. **Metastability**: what it is physically, MTBF, and why it's unavoidable when sampling asynchronous inputs. Synchronizers: the two-flop synchronizer, and proper **clock-domain crossing (CDC)** — single-bit synchronizers, multi-bit via Gray code or handshake, and async FIFOs. Reset strategies: asynchronous-assert/synchronous-deassert.
- **Why it matters / connections:** This is where most "intermittent, can't-reproduce" hardware bugs actually live — and your field experience with marginal radar systems makes the intuition click. Setup/hold and CDC are the difference between a design that simulates fine and one that fails silently in hardware. Every multi-clock FPGA radar design lives or dies on correct CDC **[→08]**, and the same metastability reasoning governs reading async sensor lines in embedded **[→04]**.
- **Hands-on / exercises:** Given t_cq/t_su/t_h/t_pd numbers, compute f_max and check the hold constraint; then add skew and recompute. Pipeline a slow combinational block and show the f_max improvement. In simulation, feed an asynchronous signal into a flop and observe a (modeled) metastable event; fix it with a two-flop synchronizer. **[MODERN]** Read a real FPGA static-timing-analysis (STA) report and find the critical path and slack.
- **You've got it when:** You can derive f_max from a datapath, explain why hold violations don't go away by slowing the clock, and instinctively reach for a two-flop synchronizer at any clock-domain boundary.

### Module 9: Memory technologies

- **Concepts:** The memory hierarchy and the volatile/non-volatile split. **SRAM**: 6-transistor cell, fast, no refresh, low density, used for caches and FPGA block RAM. **DRAM**: 1T1C cell, charge storage, **refresh** requirement, destructive read, row/column addressing, why it's dense and cheap; SDRAM/DDR generations at a conceptual level (banks, bursts, CAS latency). **ROM/PROM/EPROM/EEPROM**. **Flash**: NOR (random read, code execution / XIP) vs NAND (block erase, mass storage), floating-gate cells, SLC/MLC/TLC/QLC trade-offs, wear-out, erase-before-write, wear leveling, bad-block management. Memory organization: address/data/control buses, word width, depth × width, chip-select decoding, building larger memories from smaller chips. Read/write cycles and access time. Dual-port and FIFO memories. Content-addressable memory (CAM) at a glance.
- **Why it matters / connections:** Memory choice shapes every embedded system **[→04]** — where code runs (NOR/XIP vs RAM), how data persists (EEPROM/flash), and how fast you can stream samples (FPGA block RAM and DDR for radar data buffering **[→08]**). FIFOs reconnect to the CDC material in Module 8.
- **Hands-on / exercises:** Build an 8-bit-wide memory from two 4-bit chips, and a deeper memory with chip-select decoding (using a decoder from Module 4). **[MODERN]** Instantiate FPGA block RAM in Verilog (single- and dual-port) and build a small synchronous FIFO. Read a DDR or NAND-flash datasheet and identify refresh/erase parameters.
- **You've got it when:** You can explain why DRAM needs refresh and SRAM doesn't, why you can't byte-write NAND flash, and select a memory type for "store calibration constants" vs "buffer 1 GB/s of ADC samples."

### Module 10: Tri-state logic & buses

- **Concepts:** The three output states: high, low, and high-impedance (Hi-Z). Tri-state buffers and their enable. Shared/bidirectional buses and how multiple drivers coexist — exactly one driver enabled at a time. **Bus contention** (two drivers fighting) and its consequences; bus turnaround time. Pull-ups/pull-downs to define an idle bus. Open-drain buses as the alternative (I²C) **[→04]**. Address/data/control bus structure of a classic microprocessor system, multiplexed buses, and bus arbitration concepts. Why modern high-speed links abandoned shared parallel buses for point-to-point serial (SPI, then SerDes/PCIe/Ethernet PHYs) **[→06]**. Internal on-chip buses (and a forward pointer to AXI/Wishbone on FPGAs).
- **Why it matters / connections:** Tri-state is how a CPU shares a data bus with memory and peripherals **[→04]**, and Hi-Z reasoning is essential for not letting the magic smoke out (contention can destroy parts). The shift from parallel buses to serial links explains today's interconnect landscape **[→06]**.
- **Hands-on / exercises:** In a sim, put three tri-state buffers on one wire, enable two at once, and watch contention (X state). Build a simple read/write bus between a "CPU" and two memories using a decoder for chip-select and tri-state for data return. **[MODERN]** Model a tri-state bidirectional pin in Verilog (`inout`, with `assign data = oe ? out : 1'bz;`).
- **You've got it when:** You can explain why only one bus driver may be active, what Hi-Z means electrically, and why SPI/serial supplanted the parallel bus.

### Module 11: The analog↔digital bridge — ADC & DAC

- **Concepts:** **Sampling**: the sampling theorem (Nyquist), aliasing and the anti-alias filter, sample-and-hold. **Quantization**: resolution (bits), LSB size, quantization noise, ideal SNR = 6.02N + 1.76 dB, dynamic range. Real-converter metrics: ENOB, SINAD, SFDR, THD, INL/DNL, missing codes, offset/gain error. Coding (straight binary, offset binary, two's complement). **ADC architectures**: flash (fast, low-res), successive-approximation (SAR — the embedded workhorse), pipelined (high speed + resolution, used in radar/SDR front-ends), integrating/dual-slope (slow, precise, meters), and **sigma-delta (ΔΣ)** — oversampling + noise shaping + decimation for high-resolution audio/instrumentation. **DAC architectures**: binary-weighted, R-2R ladder, string, and the reconstruction filter; sigma-delta DACs. Oversampling and decimation/interpolation. Practical issues: aperture jitter, reference noise, grounding and layout (analog vs digital ground), and clock purity's effect on SNR.
- **Why it matters / connections:** **This is the headline connection of the whole module.** Every radar receiver digitizes IF/baseband with a high-speed ADC and every transmitter/waveform generator uses a DAC — modern direct-RF sampling pushes converters to GS/s **[→08]**. In embedded, the ADC/DAC are the most-used peripherals for sensing and control **[→04]**. The 6.02N + 1.76 dB relation and ENOB directly govern radar dynamic range and detection sensitivity, and clock jitter here ties back to Module 12. Your analog background from Module 02 (anti-alias filtering, references, grounding) is exactly what's needed to use these well.
- **Hands-on / exercises:** Compute LSB size, ideal SNR, and dynamic range for a 12-bit, 100 MS/s converter; then estimate effective SNR given a stated ENOB and aperture jitter. Sketch which ADC architecture fits: a 24-bit weigh scale, a 1 MS/s general-purpose MCU input, a 3 GS/s radar receiver. **[MODERN]** Drive a SAR ADC from an MCU and capture samples; in Python, FFT the data and read SFDR/SNR. **[→08]** Optionally interface a high-speed ADC to an FPGA over LVDS/JESD204 (conceptual) and capture a buffer to block RAM.
- **You've got it when:** You can pick an ADC architecture from requirements, explain aliasing and why the anti-alias filter is mandatory, and connect ENOB to real dynamic range.

### Module 12: Clocking, oscillators & PLLs

- **Concepts:** Why a clean clock is the heartbeat of synchronous systems. Crystal oscillators (Pierce), ceramic resonators, MEMS oscillators, RC oscillators — accuracy, stability (ppm), startup, temperature drift (TCXO/OCXO for precision). Clock distribution: trees, buffers, and managing skew. **PLLs**: phase detector, charge pump, loop filter, VCO, feedback divider — frequency synthesis (multiply/divide), the loop bandwidth trade-off, lock time. **Jitter** (period, cycle-to-cycle, long-term) and **phase noise**, and how they degrade ADC SNR (back to Module 11) and radar coherence/Doppler accuracy **[→08]**. DLLs vs PLLs. Spread-spectrum clocking for EMI. On-FPGA clock resources: dedicated clock pins, global clock buffers, and PLL/MMCM/clock-wizard blocks.
- **Why it matters / connections:** Clock quality sets the ceiling on converter performance (Module 11) and on radar phase coherence and Doppler precision **[→08]**. Every FPGA and MCU derives its working clocks from a PLL **[→04]**; knowing how to configure one (and what jitter it adds) is a routine task. Phase noise reasoning is a direct continuation of your radar receiver experience.
- **Hands-on / exercises:** Read an MCU/FPGA clock-tree diagram and trace how a 25 MHz crystal becomes a 150 MHz core clock through a PLL. Compute the divider settings for a target frequency. **[MODERN]** Use the vendor clocking wizard (Vivado MMCM / Quartus PLL) to synthesize a derived clock on your FPGA board and verify it. Estimate ADC SNR degradation from a given clock jitter spec.
- **You've got it when:** You can configure a PLL to hit a target frequency, explain how jitter limits ADC performance, and articulate why radar cares intensely about phase noise.

### Module 13: [MODERN] HDL — Verilog & VHDL

- **Concepts:** What an HDL *is* and isn't — describing hardware (parallel, structural) vs writing software (sequential). Verilog focus (with VHDL reading literacy, since defense/aerospace and radar shops often use VHDL **[→08]**). Modules, ports, nets (`wire`) vs variables (`reg`/`logic`), buses and vectors. **Combinational** modeling: continuous `assign`, `always @(*)`, complete sensitivity lists, blocking (`=`) assignments, `if`/`case`. **Sequential** modeling: `always @(posedge clk)`, **non-blocking (`<=`) assignments** and *why the blocking/non-blocking discipline matters* (the #1 beginner trap). Synchronous reset patterns. Parameters and generics for reusable, sized modules. The three-block FSM template (from Module 7) in HDL. **Testbenches** and simulation: stimulus, `$display`/`$monitor`, waveform viewing, self-checking tests, and assertions. The crucial distinction between **synthesizable** RTL and simulation-only constructs (delays, `initial` for hardware, etc.). Inferring vs instantiating (latches inferred by accident — and how to avoid them). A first look at SystemVerilog improvements (`logic`, `always_ff`/`always_comb`).
- **Why it matters / connections:** HDL fluency is the single biggest modernization gap for someone with strong fundamentals but a gap in years — it is now the baseline expectation for digital/FPGA/ASIC roles, and increasingly for embedded roles touching FPGAs **[→04]**. VHDL literacy specifically opens radar/defense doors **[→08]**. The simulate-before-synthesize habit mirrors test-driven software practice you already know.
- **Hands-on / exercises:** Install a free simulator (Icarus Verilog + GTKWave, or Verilator). Re-implement, with self-checking testbenches: the Module 4 ALU, the Module 6 counter/shift register, and the Module 7 FSM. Deliberately write an unintended latch and catch it in synthesis warnings. Read a provided VHDL module and translate it to Verilog. Practice the blocking/non-blocking rules until they're automatic.
- **You've got it when:** You can write clean synthesizable RTL for combinational and sequential logic with correct `=`/`<=` usage, write a self-checking testbench, and read VHDL well enough to port it.

### Module 14: [MODERN] FPGAs — architecture & flow

- **Concepts:** What an FPGA *is*: a fabric of configurable logic blocks (LUTs + flip-flops), block RAM, DSP slices (hardened multipliers/MACs), clock management (PLL/MMCM), and configurable I/O — programmed by a bitstream. LUT-based logic vs gate-based. Hard vs soft IP; soft-core CPUs (RISC-V, Nios, MicroBlaze). The **implementation flow**: design entry (HDL) → simulation → synthesis → placement & routing → static timing analysis → bitstream → configuration. Timing **constraints** (.xdc/.sdc): clock definitions, I/O delays, and reading **slack**/closing timing (ties straight back to Module 8). Resource utilization reports. Pin/constraint files mapping HDL ports to physical pins (buttons, LEDs, PMOD, clocks). FPGA vs MCU vs ASIC vs GPU — when each wins (parallelism, latency, throughput, NRE cost, volume). Vendor ecosystems: AMD/Xilinx (Vivado), Intel/Altera (Quartus), Lattice (open-source toolchain via Yosys/nextpnr). **Why FPGAs dominate modern radar/SDR signal processing**: deterministic parallel MAC throughput for FIR filtering, pulse compression, FFTs, and beamforming at line rate **[→08]**.
- **Why it matters / connections:** FPGAs are the centerpiece of modern radar and SDR signal chains **[→08]** and a fast-growing slice of embedded **[→04]**. This module turns your refreshed fundamentals into a deployable, demonstrable, resume-grade skill — a working FPGA project is exactly the artifact that makes a returning engineer competitive **[→10]**.
- **Hands-on / exercises:** Get a low-cost board — recommended: a Lattice board with the **fully open-source toolchain** (iCEBreaker / TinyFPGA / ULX3S via Yosys + nextpnr + IceStorm), or an AMD Artix-7 board (Arty / Basys 3) with free Vivado. Blink an LED from HDL (the FPGA "hello world"), then deploy the FSM and ALU from earlier modules driven by buttons/switches/LEDs. Read the utilization and timing reports; intentionally over-clock until timing fails and observe the slack. **[→08] Capstone-adjacent:** implement a small FIR low-pass filter (using DSP slices) and observe it filtering a stepped input — the literal building block of radar signal processing.
- **You've got it when:** You can take an HDL design through the full vendor flow to a configured board, read and act on a timing/utilization report, and explain when an FPGA beats an MCU or GPU.

## Capstone / integrative exercise

**Build a small FPGA-based digital signal-acquisition and processing chain — a "mini radar/SDR front end" in microcosm.** This integrates nearly every module:

1. **Acquire:** Sample an analog signal with an external SAR or pipelined ADC (or the board's audio/line input), respecting Nyquist and an anti-alias filter (Modules 11, 2/analog). Bring samples into the FPGA over a serial/LVDS interface.
2. **Buffer:** Store samples in **block RAM** through a small **FIFO** that crosses from the ADC clock domain to the processing clock domain using a proper **synchronizer/Gray-coded async FIFO** (Modules 9, 8, 10).
3. **Process:** Run the samples through a **FIR low-pass (or matched) filter** built from **MAC units / DSP slices**, using **fixed-point Q-format** arithmetic (Modules 1, 4, 14). Optionally compute a small FFT or magnitude.
4. **Control:** Sequence the whole pipeline (idle → acquire → process → output) with a **Moore FSM**, with safe reset and illegal-state recovery (Module 7).
5. **Clock:** Derive the processing clock from the input crystal via a **PLL/MMCM**, and reason about how clock jitter would limit your ADC's effective SNR (Modules 12, 11).
6. **Output / report:** Drive results to LEDs / a UART to a PC, or a simple display. Capture samples to Python and **FFT them to measure SNR/SFDR/ENOB** of your chain (Module 11).
7. **Deliver like an engineer (forward to [→10]):** version-control the HDL, write self-checking testbenches for each block, document the constraints and the timing-closure result, and write a short README. This is the portfolio artifact.

Stretch goals: parameterize the filter taps; add a second clock domain to stress CDC; implement the same FIR in C on a soft RISC-V core and compare throughput (FPGA parallelism vs CPU) — a direct lead-in to **[→04]** and **[→08]**.

## Common pitfalls & rust-knockers

- **Two's-complement overflow vs carry.** Carry-out and signed overflow are *different* flags; mixing them up corrupts arithmetic. Re-derive the V flag.
- **Confusing latches and flip-flops** — and accidentally *inferring* a latch in HDL by leaving an incomplete `if`/`case` or missing `else`. Synthesis warns; read the warnings.
- **Blocking vs non-blocking (`=` vs `<=`).** Use non-blocking in clocked (`always @(posedge clk)`) blocks and blocking in combinational blocks. Getting this wrong produces designs that simulate fine and synthesize wrong (or vice versa). This is *the* HDL rust-knocker.
- **Gating the clock.** Don't AND a clock with an enable. Use a synchronous clock-enable on the flip-flop instead. Gated clocks wreck timing and skew.
- **Ignoring clock-domain crossing / metastability.** Any signal crossing clock domains needs a synchronizer; multi-bit needs Gray code or a handshake/async FIFO. Skipping this causes rare, unreproducible failures — the worst kind.
- **Setup/hold confusion.** Slowing the clock fixes *setup* violations, never *hold* violations. Know which constraint you're fighting.
- **Forgetting the anti-alias filter** before an ADC, or sampling below Nyquist and being surprised by aliases. Also: assuming you get the full 6.02N+1.76 dB SNR when ENOB and jitter say otherwise.
- **Bus contention.** Enabling two tri-state drivers at once — at best an X in sim, at worst dead silicon. Always provide a defined idle (pull-up/down) and exactly-one-driver logic.
- **Mixed-voltage interfacing without level shifting.** Check V_IH/V_IL/V_OH/V_OL and noise margins every time; 3.3 V↔5 V is not automatically safe.
- **No decoupling capacitors / poor grounding.** Digital switching noise into analog references destroys ADC performance — your analog instincts apply.
- **Treating HDL like software** (sequential, procedural). It describes parallel hardware. The `always` block is not a function call.
- **Trusting simulation alone.** A design that simulates perfectly can still fail timing in hardware. Always check the STA report and close timing.

## Self-assessment checklist

- [ ] I can convert fluently among binary/hex/decimal and do two's-complement arithmetic with correct overflow detection.
- [ ] I can encode/decode IEEE 754 floats and work in fixed-point Q-format.
- [ ] I can apply De Morgan's and simplify Boolean expressions, and write canonical SOP/POS from a truth table.
- [ ] I can minimize logic with K-maps (including don't-cares) and explain prime/essential prime implicants.
- [ ] I can design muxes, decoders, adders, and a multi-bit ALU with correct flags.
- [ ] I can distinguish latches from flip-flops and design synchronous counters and shift registers.
- [ ] I can take a spec to a Moore/Mealy FSM, choose an encoding, and implement it in HDL.
- [ ] I can compute f_max from timing parameters and explain why hold violations are frequency-independent.
- [ ] I instinctively add a two-flop synchronizer (or async FIFO) at every clock-domain crossing.
- [ ] I can explain SRAM vs DRAM vs NOR vs NAND flash and select memory for a given need.
- [ ] I can use tri-state buffers/buses correctly and avoid contention.
- [ ] I can compute LSB/SNR/ENOB and choose an ADC/DAC architecture from requirements.
- [ ] I can explain sampling, aliasing, quantization, and the role of anti-alias and reconstruction filters.
- [ ] I can configure a PLL to a target frequency and explain how jitter limits ADC and radar performance.
- [ ] **[MODERN]** I can write clean synthesizable Verilog (correct `=`/`<=`) with self-checking testbenches.
- [ ] **[MODERN]** I can read VHDL and translate it to Verilog.
- [ ] **[MODERN]** I can take a design through the full FPGA flow to a board and read/act on timing and utilization reports.
- [ ] I completed the capstone acquisition/FIR pipeline and measured its SNR/SFDR.

## Canonical resources

**Foundational textbooks**
- Harris & Harris, *Digital Design and Computer Architecture* (RISC-V or ARM edition) — the modern standard; teaches digital logic *and* HDL *and* a CPU together. Top pick for this refresh.
- Wakerly, *Digital Design: Principles and Practices* — exhaustive, classic reference for the fundamentals.
- Mano & Ciletti, *Digital Design* — the canonical undergrad text; great for K-maps, FSMs, and combinational/sequential coverage.
- Brown & Vranesic, *Fundamentals of Digital Logic with Verilog/VHDL Design* — strong on the HDL bridge.

**HDL & FPGA (modern track)**
- Pong P. Chu, *FPGA Prototyping by Verilog Examples* (and the VHDL companion) — project-driven, board-ready.
- *Designing Video Game Hardware in Verilog* / nandland.com (Russell Merrick, *Getting Started with FPGAs*) — gentle, practical FPGA on-ramp.
- HDLBits (online) — graded Verilog exercises; the fastest way to rebuild RTL muscle memory.
- Cummings' SNUG papers on non-blocking assignments and CDC — the definitive practitioner notes on the two biggest traps.
- ZipCPU blog (Gisselquist) — excellent practical FPGA/Verilog and formal-verification articles.

**Converters / signal chain**
- Walt Kester (ADI), *Data Conversion Handbook* (free from Analog Devices) — the bible on ADC/DAC, sampling, and converter metrics. **[→08]**
- Analog Devices and TI application notes on ADC/clock jitter and SNR.

**Tools**
- Logic simulation: **Logisim Evolution** or **Digital** (hneemann) — for gate-level intuition.
- HDL simulation: **Icarus Verilog + GTKWave**, or **Verilator**; **EDA Playground** (browser, no install).
- FPGA flows: **AMD Vivado** (Artix-7: Arty/Basys 3) or **Intel Quartus**; **open-source** Yosys + nextpnr + IceStorm for Lattice iCE40 (iCEBreaker, TinyFPGA), or apicula for Gowin.
- Courses: MIT 6.004 (computation structures), Coursera/Nand2Tetris (*The Elements of Computing Systems* — build a computer from NAND up), and the Harris & Harris companion lectures.
