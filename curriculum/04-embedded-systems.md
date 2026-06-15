# Embedded Systems Programming — Lesson Plan

This module is the bridge between the device physics you re-learned in **02 (analog)** and **03 (digital)** and the running, real-time systems that everything downstream depends on. It is where Ohm's law, logic levels, clock edges, and binary number representation stop being abstractions and start being bytes you write into a peripheral register that physically toggles a pin. For this learner the framing is specific: you already think in C, pointers, and state machines as a working software developer, and you carry deep EE fundamentals from a BS in Electronics Engineering and years as a Radar Technician. What has gone rusty is the *hardware-level mental model* — the idea that memory is not a flat array of objects managed by a runtime, but a physical address space where some addresses are RAM, some are flash, and some are device control registers that have side effects when you merely read them. That mindset is the real growth area, so we move briskly through general C and software hygiene and slow down hard on bare-metal reasoning, peripherals, timing, and determinism.

Prerequisites: **02 (analog)** for ADC/DAC, op-amp front-ends, and signal conditioning; **03 (digital)** for combinational/sequential logic, buses, memory-mapped I/O, and number representation. This module is the compute substrate that **08 (radar)** runs on, and it hands off directly to **05 (linux)** when we cross from bare-metal MCUs into embedded Linux on application processors. The DSP-on-MCU track at the end (CMSIS-DSP) is deliberately the on-ramp to the radar signal-processing chain in 08.

## Learning outcomes

By the end of this module you will be able to:

- Reason about an embedded program as code and data laid out in a physical memory map, and explain what `volatile`, alignment, endianness, and the C memory model mean at the level of bus transactions.
- Read a microcontroller datasheet and reference manual, find a peripheral's register block, and configure it by writing raw register values — with and without a vendor HAL.
- Set up a cross-compilation toolchain end to end: compiler, linker script, startup code, and flash/debug over JTAG/SWD with GDB and OpenOCD/probe-rs.
- Bring up GPIO, configure and service interrupts through the NVIC, drive timers/counters/PWM, and use the ADC/DAC.
- Implement and debug the four core serial protocols — UART, SPI, I2C, CAN — including with DMA to offload the CPU.
- Configure clock trees and low-power modes and reason about their effect on peripheral timing and current draw.
- Decide between bare-metal and an RTOS, and on an RTOS (FreeRTOS and Zephyr) build tasks with correct scheduling, synchronization, and priority-inversion avoidance.
- Analyze real-time constraints, measure timing and jitter, and instrument firmware using a logic analyzer, oscilloscope, and SWO/RTT tracing rather than blind `printf`.
- Design for tight RAM/flash budgets and implement a bootloader with a safe firmware-update path.
- **(MODERN)** Write embedded Rust with the `embedded-hal` ecosystem, build a Zephyr application, target the ESP32 / STM32 / RP2040 families, and run real DSP kernels (FFT, FIR) on an MCU with CMSIS-DSP.

## Module breakdown

### Module 1: The bare-metal mindset and the memory map

- **Concepts:** What "embedded" means and the spectrum from 8-bit MCUs to application processors. The von Neumann vs Harvard distinction and why it matters for MCUs (separate flash/RAM buses). The physical address space: flash (code + constants), SRAM (data + stack + heap), and the peripheral region where addresses *are* registers. Memory-mapped I/O: writing to an address has a physical side effect; reading can clear a flag. Word size, alignment, and natural alignment faults. Endianness (most ARM Cortex-M is little-endian) and how it bites in protocol and serialization code. The stack vs heap distinction with no OS underneath you; why dynamic allocation is discouraged. What "there is no `main()` until you write the path to it" means.
- **Why it matters / connections:** This is the load-bearing reframe for a software developer. In application software, memory is a managed abstraction; here it is the actual silicon you studied in 03. Everything else in this module — peripherals, interrupts, DMA — is "read/write specific physical addresses at the right time." Directly extends 03's memory-mapped I/O and bus concepts.
- **Hands-on / exercises:** Get an STM32 (e.g. Nucleo-F411 or Blue Pill), an RP2040 (Pico), and an ESP32 dev board on the bench. Open the STM32 reference manual to the memory map; locate the boundaries of flash, SRAM, and the peripheral block. With a debugger attached and no real program, manually write a value into the GPIO output-data register from the GDB console and watch a pin go high on a meter. Read the same register back.
- **You've got it when:** You can sketch the target's memory map from memory, explain why toggling a pin is just a store to an address, and articulate why a normal `int` flag updated in an ISR can be missed by `main`.

### Module 2: Embedded C, pointers, and `volatile` at the hardware level

- **Concepts:** Pointers as addresses, and casting an integer literal address to a `volatile uint32_t *` to reach a register. `volatile`: what it actually guarantees (every access in source becomes a real memory access, no caching in registers, no reordering *of volatile accesses relative to each other*) and what it does NOT guarantee (atomicity, ordering w.r.t. non-volatile accesses, memory barriers). When you additionally need `__DMB`/`__DSB`/`__ISB` barriers and compiler barriers. Bit manipulation idioms: set/clear/toggle/test, read-modify-write hazards, and atomic bit-band (Cortex-M3/M4). `const`, `static`, and storage placement. Fixed-width types (`stdint.h`), `bool`, packed structs for register/protocol layout and the alignment traps they create. The `volatile` + struct overlay pattern for register blocks. The C memory model just enough to know why concurrency with ISRs and DMA needs care; `_Atomic` and where it's available. Integer promotion and undefined-behavior landmines that are merely sloppy on a desktop but fatal here. Inline assembly and intrinsics basics.
- **Why it matters / connections:** You already write C well; the delta is the hardware semantics layered on top. `volatile` misuse and missing barriers are among the top sources of "works in debug, fails in release / fails at -O2" embedded bugs. Sets up correct ISR and DMA buffer handling later.
- **Hands-on / exercises:** Write a tiny register-access header that maps a GPIO peripheral as a `volatile` struct, and toggle a pin with it — no HAL. Deliberately remove `volatile` and compile at `-O2`; inspect the disassembly to see the access get hoisted out of a loop and the LED stop blinking. Add it back and confirm. Write set/clear/toggle macros and verify with a logic analyzer that toggle is glitch-free.
- **You've got it when:** You can explain, with a disassembly example, exactly what `volatile` changed, and you know the difference between a compiler barrier and a CPU memory barrier and when each is required.

### Module 3: Microcontroller architecture — CPU, registers, buses

- **Concepts:** ARM Cortex-M as the reference architecture: the register file (R0–R12, SP, LR, PC), the program status registers (xPSR), banked stack pointers (MSP/PSP), Thumb-2 instruction set basics. Privilege levels (handler vs thread mode, privileged vs unprivileged) and why an RTOS uses them. The reset and exception model at the CPU level (vector table, stacking on exception entry). Core peripherals: SysTick, NVIC, optional MPU and FPU. The bus matrix (AHB/APB), wait states, and flash accelerator/cache. How a load/store actually traverses the bus to a peripheral. Comparison points: Xtensa/RISC-V cores in the ESP32 family, the dual Cortex-M0+ in RP2040, and the contrast with 8-bit AVR/PIC for perspective. CPU clock vs peripheral clock domains.
- **Why it matters / connections:** Connects 03's CPU/datapath material to a real chip. Understanding the bus matrix and clock domains is what later makes clock-tree and DMA configuration make sense instead of being cargo-culted.
- **Hands-on / exercises:** In the debugger, single-step a few instructions and watch the core registers change; trigger an exception (e.g. an unaligned access or a fault) and inspect the stacked frame to find the faulting PC. Locate the vector table in your linker output.
- **You've got it when:** You can describe what the CPU does between "an interrupt fires" and "your handler's first instruction runs," including what gets pushed and where.

### Module 4: The toolchain — cross-compiler, linker script, startup, flashing & debugging

- **Concepts:** Cross-compilation: host vs target, `arm-none-eabi-gcc`, the meaning of the triple, and what "freestanding" (no hosted libc/OS) means. The build pipeline: preprocess → compile → assemble → link, and the role of each. Object files and sections (`.text`, `.rodata`, `.data`, `.bss`, `.stack`, `.heap`). **The linker script**: defining MEMORY regions (FLASH/RAM) and SECTIONS, placing the vector table, and producing load address (LMA) vs virtual address (VMA). **Startup/crt0**: the reset handler that copies `.data` from flash to RAM, zeroes `.bss`, sets the stack pointer, calls libc init / C++ constructors, then `main`. Newlib vs newlib-nano, semihosting, and `_sbrk`/syscall stubs. Producing `.elf`/`.bin`/`.hex` and what each is for. Flashing and on-chip debug: **JTAG vs SWD** (pins, tradeoffs), debug probes (ST-Link, J-Link, CMSIS-DAP/Picoprobe), **OpenOCD** and **probe-rs**, and **GDB** workflows (breakpoints, watchpoints, examining memory and peripheral registers, semihosting I/O). SVD files for register-aware debugging. **(MODERN)** Rust toolchain: `cargo`, `thumbv7em-none-eabihf` targets, `cargo-embed`/`probe-rs`, and `defmt`/RTT logging. Vendor IDEs (STM32CubeIDE/CubeMX, ESP-IDF, Pico SDK + CMake) vs a hand-rolled Makefile/CMake build, and why building one by hand once is worth it.
- **Why it matters / connections:** This is the single biggest "rusty hardware mindset" payoff: understanding that the linker script and startup code — not magic — are what make `main()` reachable. It demystifies every vendor template. Feeds directly into bootloaders (Module 14) and into 05 where you'll cross-compile for embedded Linux.
- **Hands-on / exercises:** Build a "blink" for an STM32 *from scratch*: write your own minimal linker script and startup assembly/C, no HAL, link, flash with OpenOCD, and step through reset-to-`main` in GDB watching `.data`/`.bss` get initialized. Then reproduce the same blink three ways for contrast: STM32 HAL, RP2040 Pico SDK, and **(MODERN)** Rust with `cortex-m-rt`. Set a hardware watchpoint on a variable.
- **You've got it when:** You can explain every line of a startup file and linker script, and you can flash and debug over SWD with GDB without an IDE doing it for you.

### Module 5: GPIO — the simplest peripheral, done rigorously

- **Concepts:** Pin modes: input, output (push-pull vs open-drain), analog, and alternate function (muxing a pin to UART/SPI/timer). Internal pull-up/pull-down resistors and when external ones are mandatory (I2C, open-drain buses). Drive strength and slew rate. Input reading, debouncing in software vs hardware, and Schmitt-trigger inputs. The register sequence to configure a pin (enable the port clock first — a classic gotcha — then mode, then output type, then pull, then drive). Reading vs writing data registers; atomic set/reset registers (BSRR) to avoid read-modify-write races. Edge/level concepts setting up external interrupts.
- **Why it matters / connections:** GPIO is the "hello world" that teaches the universal peripheral pattern: enable clock → configure → use, all via register writes. The pull-up and open-drain material is the prerequisite for I2C. Connects to 02's logic-level and output-driver concepts.
- **Hands-on / exercises:** Bare-metal: blink an LED and read a button, configured purely by register writes, port clock and all. Add software debouncing and confirm bounce on a scope/logic analyzer first, then confirm it's gone. Drive an external transistor/relay and reason about open-drain vs push-pull for the load.
- **You've got it when:** You can configure any pin from the datasheet without a HAL and you never forget to enable the peripheral clock.

### Module 6: Interrupts and the NVIC

- **Concepts:** The interrupt concept: asynchronous events preempting `main`. The Cortex-M **NVIC**: vector table, IRQ numbers, enabling/disabling interrupts, pending and active bits. **Priority and preemption**: priority grouping, preemption priority vs subpriority, and how nesting works. Latency and the deterministic exception entry/exit (tail-chaining, late arrival). Writing an ISR: keep it short, clear the source flag, do minimal work, defer the rest. The interrupt-to-`main` data-sharing problem: shared state must be `volatile`, access must be atomic or protected by disabling interrupts (critical sections) or using bit-band/atomics. Re-entrancy and the danger of doing too much (or calling non-reentrant code / `printf`) in an ISR. `PRIMASK`/`BASEPRI` masking. The deferred-work pattern (top half / bottom half). Spurious and shared interrupts; external interrupt (EXTI) lines and edge configuration.
- **Why it matters / connections:** Interrupts are where the bare-metal concurrency model lives, and they are the foundation for every responsive embedded design and for the RTOS later. Priority inversion (Module 11) and DMA completion (Module 10) both build on this. The shared-state hazard is exactly the `volatile` material from Module 2 made concrete.
- **Hands-on / exercises:** Configure a button to fire an EXTI interrupt; in the ISR set a `volatile` flag and toggle an LED, observing latency on a scope by also toggling a pin at ISR entry. Build a timer-driven periodic interrupt. Introduce a deliberate shared-variable race between ISR and `main`, observe corruption, then fix it with a critical section and explain why.
- **You've got it when:** You can configure NVIC priorities to get correct preemption, write a minimal correct ISR, and safely share data with `main` — and measure your interrupt latency.

### Module 7: Timers, counters, and PWM

- **Concepts:** Timer/counter fundamentals: clock source, prescaler, auto-reload/period, count direction, and how these set frequency and resolution. Output compare and **PWM** generation (duty cycle, alignment, dead-time for motor drive). Input capture for measuring pulse width/frequency (e.g. an echo timer). One-shot vs continuous modes. Timer-triggered interrupts and timer-triggered ADC/DMA (peripheral linking without CPU involvement). Watchdog timers (independent + window) and why they exist. SysTick as the canonical periodic tick. Time bases, overflow handling, and building a monotonic microsecond clock. Resolution vs range tradeoffs.
- **Why it matters / connections:** Timers are the heartbeat of real-time systems and the source of all precise timing — PWM for actuation, input capture for measurement, and the periodic tick that an RTOS scheduler runs on (Module 11). Timer→ADC→DMA chaining (Module 10) is the basis for deterministic sampling, which is exactly how radar/DSP front-ends acquire data (Module 15, ties to 08).
- **Hands-on / exercises:** Generate a PWM signal and vary duty cycle to dim an LED or position a servo; verify frequency and duty on a scope. Use input capture to measure the period of an external signal. Configure a watchdog and prove it resets the MCU when you stop kicking it. Build a `micros()`/`millis()` timebase from a timer.
- **You've got it when:** You can produce a precise arbitrary-frequency, arbitrary-duty PWM from registers and measure an external signal's frequency with input capture.

### Module 8: ADC and DAC

- **Concepts:** ADC fundamentals revisited at the register level: resolution (bits), reference voltage, LSB size, sampling time vs source impedance, and the sample-and-hold. Single vs continuous conversion, scan/sequence mode across channels, and triggering (software vs timer-triggered). Conversion time, throughput, and the Nyquist/aliasing reminder (anti-alias filtering in 02). Oversampling and decimation for effective extra bits. Calibration, offset, and gain error. Internal channels (temperature sensor, Vref). **DAC** output, buffering, and using a timer + DMA to play out a waveform (DDS basics). Sigma-delta vs SAR ADC tradeoffs at a conceptual level.
- **Why it matters / connections:** This is where the analog world (02) enters the digital domain, and it is the literal front door of any signal-processing or radar application (08). Timer-triggered ADC + DMA (Modules 7, 10) is the standard pattern for capturing a clean, evenly-sampled buffer to feed an FFT (Module 15).
- **Hands-on / exercises:** Read a potentiometer with the ADC and stream values over UART. Set up timer-triggered sampling at a fixed rate into a DMA buffer; verify the sample rate on a scope. Generate a sine wave on the DAC via timer+DMA and view it on a scope; observe aliasing by sampling a too-fast input.
- **You've got it when:** You can acquire an evenly-sampled buffer at a known, timer-controlled rate without the CPU babysitting each sample, and you can state the aliasing risk for a given input.

### Module 9: Serial protocols — UART, SPI, I2C, CAN

- **Concepts:** **UART/USART**: asynchronous framing (start/stop/parity), baud rate and error tolerance, flow control (RTS/CTS), common uses (console, GPS, modems); the difference between UART (the protocol) and RS-232/RS-485 (the physical layer). **SPI**: master/slave, full-duplex, clock polarity/phase (CPOL/CPHA modes), chip select, daisy-chaining, and why it's fast but pin-hungry. **I2C**: two-wire open-drain bus (recall pull-ups from Module 5), addressing, start/stop/ACK/NACK, clock stretching, multi-master arbitration, and common failure modes (stuck bus, missing pull-ups, wrong address). **CAN** (and CAN FD): differential bus, arbitration by message priority, frames and identifiers, ack/error handling, and why it dominates automotive/industrial; the CAN transceiver and termination. Choosing among them by speed, distance, pin count, multi-drop needs, and noise immunity. Polled vs interrupt-driven vs DMA-driven transfers for each.
- **Why it matters / connections:** Almost every sensor, display, radio, and inter-MCU link uses one of these four. They exercise everything so far — GPIO alt-functions, interrupts, and DMA — and CAN's noise-immune differential signaling connects back to 02. Sets up the sensor-streaming capstone and links to 06 (networking) conceptually for higher-layer protocols.
- **Hands-on / exercises:** UART: build an interrupt-driven RX ring buffer and a simple command parser over the console. SPI: read an SPI sensor (e.g. an accelerometer or flash chip) and decode the transaction on a logic analyzer. I2C: read a temperature/humidity sensor (e.g. BME280); deliberately remove the pull-ups and observe the failure. CAN: send/receive frames between two boards (or board + USB-CAN) and watch arbitration.
- **You've got it when:** You can bring up an unfamiliar sensor from its datasheet on the correct bus, and you can decode and debug each protocol on a logic analyzer.

### Module 10: DMA — moving data without the CPU

- **Concepts:** What DMA is and why it exists: offload bulk/streaming transfers so the CPU isn't burning cycles per byte. DMA controller model: channels/streams, source/destination addresses, transfer size, increment modes, and arbitration/priority. Memory-to-memory, peripheral-to-memory, memory-to-peripheral. Triggering DMA from a peripheral event (ADC conversion complete, UART RX, timer). Circular/double-buffer (ping-pong) mode for continuous streaming. Half-transfer and transfer-complete interrupts. **The cache/coherency and `volatile` hazards**: DMA writes RAM behind the CPU's back — buffers must be `volatile`-aware, correctly aligned, and on cached cores (Cortex-M7) require cache maintenance (clean/invalidate) or non-cacheable regions. Bus contention and bandwidth.
- **Why it matters / connections:** DMA is what makes high-throughput, low-jitter acquisition possible — it's the difference between an MCU that can stream an ADC into an FFT (Module 15, radar 08) and one that drops samples. It is the practical payoff of understanding the bus matrix (Module 3) and the memory hazards (Module 2). The coherency pitfalls are a top-tier "rust-knocker."
- **Hands-on / exercises:** Convert the UART RX from Module 9 to DMA with a circular buffer and confirm zero bytes dropped at high baud. Set up ADC→DMA double-buffering from Module 8 and process one buffer while the other fills. On a Cortex-M7 target (if available), reproduce a cache-coherency bug and fix it with cache maintenance or an MPU non-cacheable region.
- **You've got it when:** You can stream a peripheral into a circular DMA buffer with half/full interrupts and process it without losing or corrupting data.

### Module 11: Bare-metal vs RTOS — FreeRTOS and Zephyr

- **Concepts:** When a superloop (with interrupts) is sufficient and when you need an RTOS. RTOS core concepts: **tasks/threads**, the scheduler, and context switching (what gets saved/restored — ties to Module 3). **Scheduling**: preemptive priority-based scheduling, time slicing, the idle task, and tickless idle for power. **Synchronization & IPC**: semaphores (binary/counting), mutexes, queues, event groups/flags, and task notifications; passing data safely between tasks and from ISRs (the "give from ISR" pattern). **Priority inversion** — the classic failure where a high-priority task is blocked by a low-priority one holding a mutex while a medium task runs — and the fixes: priority inheritance and priority ceiling. Deadlock, starvation, and stack-per-task sizing/overflow detection. Comparing **FreeRTOS** (small, ubiquitous, lib-you-link) with **(MODERN) Zephyr** (a full RTOS + driver model + device tree + build system + networking, more Linux-like). Heap models and memory pools. **(MODERN)** Rust concurrency on embedded: RTIC (interrupt-driven, compile-time-scheduled) and `async`/Embassy as alternatives to a traditional RTOS.
- **Why it matters / connections:** Concurrency is your home turf as a software dev, but the embedded constraints (no malloc-happy heap, ISR-to-task handoff, hard priority semantics, tiny stacks) are new. Priority inversion is a famous real-world failure (Mars Pathfinder) and exactly the kind of question that lands jobs. Zephyr's device tree and build model are the conceptual bridge to 05 (linux). Builds directly on interrupts (Module 6).
- **Hands-on / exercises:** Port the sensor+UART project to **FreeRTOS**: one task samples the sensor on a timer, posts to a queue; another task formats and streams over UART; a button ISR gives a semaphore to a control task. Deliberately construct a priority-inversion scenario with three tasks and a mutex, observe the high-priority task miss its deadline on a scope, then enable priority inheritance and confirm the fix. **(MODERN)** Rebuild the same app in **Zephyr** (device tree + Kconfig + threads) and, separately, in Rust with RTIC or Embassy.
- **You've got it when:** You can decompose a problem into tasks with correct priorities and synchronization, explain and demonstrate priority inversion and its fix, and size task stacks with overflow detection enabled.

### Module 12: Real-time constraints and determinism

- **Concepts:** Hard vs soft vs firm real-time and what "deterministic" actually means (bounded worst-case, not just fast-on-average). Latency vs throughput vs jitter. Worst-case execution time (WCET) thinking and the enemies of determinism: cache misses, branch prediction, variable-time instructions (division), DMA/bus contention, interrupt storms, and the heap. Sources of jitter and how to measure it. Designing for the worst case: rate-monotonic intuition, deadline analysis, and budgeting CPU. Why disabling interrupts, busy-waiting, and unbounded loops in critical paths are dangerous. The role of the MPU and watchdog in robustness.
- **Why it matters / connections:** Determinism is the whole point of real-time embedded and is *the* differentiator from application programming where average-case latency is fine. This is the analytical layer over the RTOS (Module 11) and timers/interrupts (6, 7), and it is non-negotiable for radar timing (08).
- **Hands-on / exercises:** Toggle a GPIO at the start/end of your sensor ISR and a periodic task; capture on a scope/logic analyzer and measure the distribution of latency and jitter over thousands of events. Introduce a long, badly-placed critical section and watch jitter blow up; quantify the worst case. Compute a simple CPU-utilization budget for your tasks and verify it empirically.
- **You've got it when:** You can state and *measure* the worst-case latency and jitter of a path in your firmware and argue whether a deadline is met.

### Module 13: Debugging and instrumentation

- **Concepts:** The embedded debugging toolkit beyond stepping in GDB. **Logic analyzer** (digital, multi-channel, protocol decode — invaluable for SPI/I2C/UART) vs **oscilloscope** (analog detail, timing, signal integrity, glitches). When you need each. The cost of `printf` (blocking, slow UART, changes timing) vs **SWO/ITM trace** and **SegGER RTL/RTT** (near-zero-overhead logging over the debug link) — and **(MODERN)** `defmt`+RTT in Rust for compact, deferred-format logging. GPIO-toggle profiling for timing (used in Modules 6/12). Reading fault handlers: decoding HardFault/UsageFault/BusFault/MemManage from the stacked registers and the CFSR. Assertions, `static_assert`, and runtime checks under tight budgets. Core-dump/postmortem techniques. Non-intrusive vs intrusive debugging and why breakpoints can mask or create timing bugs (Heisenbugs).
- **Why it matters / connections:** On bare metal you often can't just attach a debugger to a timing-sensitive bug without changing it; you need low-overhead and external instrumentation. This skill set is what makes Modules 9–12 actually achievable. Decoding faults turns mysterious crashes into addresses.
- **Hands-on / exercises:** Capture and decode an I2C transaction on a logic analyzer and find a NACK. Replace a `printf` debug path with RTT (or `defmt` in Rust) and measure the timing difference on a scope. Deliberately cause a HardFault (null deref, unaligned access, bad function pointer), then decode the stacked frame and CFSR in GDB to find the exact faulting instruction.
- **You've got it when:** You reach for a logic analyzer/RTT before blind `printf`, and you can turn a HardFault into a source line.

### Module 14: Memory-constrained design, bootloaders, and firmware update

- **Concepts:** Living in kilobytes: measuring flash/RAM usage from the map file, the cost of libc/`printf`/floating point/`malloc`, and `newlib-nano`. Avoiding the heap: static allocation, fixed pools, ring buffers, and stack budgeting. Flash characteristics: erase-before-write, page/sector granularity, endurance/wear, and read-while-write limits. Storing config/calibration in flash or EEPROM emulation; wear leveling basics. **Bootloaders**: why they exist, the boot flow (ROM bootloader → your bootloader → application), how the bootloader jumps to the app (relocate vector table, set SP/PC), and dual-bank / A-B partition schemes. **Firmware update (FOTA/DFU)**: receiving an image (UART/USB/USB-DFU/OTA-radio), validating it (CRC, signature/secure boot), writing it to the inactive bank, and atomic swap with rollback on failure. Versioning and anti-bricking. Vendor/standard stacks: ST built-in DFU, **MCUboot** (used by Zephyr), ESP-IDF OTA. Secure boot and the chain of trust at a high level.
- **Why it matters / connections:** Field-updatable, robust firmware is what separates a hobby blink from a product, and "how does OTA update work safely" is a common interview and job-relevant topic. Bootloaders are the direct application of the linker-script/startup/vector-table knowledge from Module 4. MCUboot ties to Zephyr (Module 11) and to secure-boot concepts relevant in 05/06.
- **Hands-on / exercises:** Read your map file and cut RAM/flash usage (swap full `printf` for a tiny formatter, remove the heap). Write a minimal bootloader that validates a CRC and jumps to an application built to a higher flash offset (adjust the app's linker script and vector-table offset). Implement a UART-based image upload into the inactive bank with rollback. **(MODERN)** Do an OTA update on ESP32 via ESP-IDF, and try MCUboot under Zephyr.
- **You've got it when:** You can lay out flash for a bootloader + A/B application, update over a link, validate the image, and recover from a bad update without bricking.

### Module 15: (MODERN) DSP on microcontrollers — the bridge to radar

- **Concepts:** Why DSP runs on MCUs now: Cortex-M4F/M7 with FPU, DSP/SIMD instructions, and **CMSIS-DSP**. Fixed-point (Q15/Q31) vs floating-point tradeoffs on MCUs and saturating arithmetic. Core kernels from CMSIS-DSP: FIR/IIR filtering, FFT (`arm_rfft_fast`), windowing, magnitude/dB, correlation, and basic statistics. The acquisition→processing pipeline you've already built: timer-triggered ADC → DMA double buffer → windowed FFT → spectrum, all in real time without dropping samples. Computational budget: samples/sec vs MIPS, and choosing block sizes to meet deadlines (ties to Module 12). When the MCU isn't enough and you reach for a DSP, FPGA, or application processor (handoff to 05/08). **(MODERN)** Rust DSP options and using CMSIS-DSP from Rust/C interop.
- **Why it matters / connections:** This is the explicit on-ramp to **08 (radar)**: a radar receiver is fundamentally timed acquisition followed by FFT/filtering/detection, which is exactly this pipeline. It also unifies the whole module — clocks, timers, ADC, DMA, interrupts, real-time budgeting, and instrumentation all converge here. Connects back to 01 (math: complex numbers, FFT) and 02 (analog front-end / sampling).
- **Hands-on / exercises:** On an STM32F4/F7, run a CMSIS-DSP FIR filter on a live ADC stream and view input vs output on the DAC/scope. Build a real-time spectrum analyzer: timer-triggered ADC → DMA → windowed `arm_rfft_fast` → magnitude → stream the bins over UART/RTT and plot on the host. Measure how large an FFT you can sustain at your sample rate within the per-block deadline.
- **You've got it when:** You can capture evenly-sampled data and produce a correct real-time spectrum on an MCU, and you can state the sample-rate/FFT-size/CPU-budget tradeoff — the same reasoning a radar signal chain demands.

## Capstone / integrative exercise

Build a **real-time, RTOS-based, field-updatable sensor acquisition and spectrum node** on an STM32F4/F7 (with a parallel ESP32 or RP2040 variant for ecosystem contrast):

1. **Acquisition:** A timer triggers the ADC at a precise, known rate; results stream via DMA into a circular double buffer (Modules 7, 8, 10).
2. **Processing:** An RTOS task consumes completed buffers and runs a windowed FFT / FIR via CMSIS-DSP, producing a spectrum and a couple of derived metrics, all within a measured per-block deadline (Modules 11, 12, 15).
3. **Communication:** Results stream out over an interrupt/DMA-driven UART (and/or are exposed over SPI/I2C as a peripheral, and reported on CAN for a "system bus" flavor). A command interface lets the host change parameters (Module 9).
4. **Robustness & timing:** A watchdog guards the system; you instrument task and ISR timing with GPIO toggles and RTT, and produce a measured jitter/latency report with a CPU-budget argument (Modules 6, 12, 13).
5. **Updatability:** The application runs above a bootloader you wrote; you perform a firmware update over UART (or OTA on the ESP32 variant) into an inactive bank with CRC/signature validation and rollback (Modules 4, 14).
6. **(MODERN) stretch:** Reimplement the core (acquisition + processing + UART) in **embedded Rust** (RTIC/Embassy + `embedded-hal` + CMSIS-DSP interop) and/or build the RTOS variant on **Zephyr** with device tree, Kconfig, and MCUboot.

Deliverable: working firmware on real hardware, plus a short written analysis of the memory budget, the worst-case timing/jitter (measured, not assumed), and the design choices. This single project exercises every module and produces exactly the acquisition→FFT pipeline that 08 (radar) builds on.

## Common pitfalls & rust-knockers

- Forgetting to **enable the peripheral clock** before configuring a peripheral — the registers read back as zero/garbage and nothing works.
- Omitting `volatile` on register/shared-with-ISR/DMA data — works at `-O0`, breaks at `-O2`; or assuming `volatile` gives atomicity or memory ordering (it does not).
- Read-modify-write races on shared registers; not using atomic set/reset (BSRR) or critical sections.
- Doing too much in an ISR, calling non-reentrant functions (`printf`, `malloc`) from an ISR, or forgetting to clear the interrupt flag (re-entering forever).
- Misconfigured NVIC priorities (Cortex-M priority bits are the *high* bits; lower number = higher priority) leading to wrong preemption.
- Stack overflow with no MMU to catch it — silent corruption; under-sizing RTOS task stacks; not enabling stack-overflow detection.
- DMA buffer not aligned / not coherent on cached cores (Cortex-M7) — stale or garbage data with no error.
- I2C with missing or wrong-value pull-ups, wrong 7-bit vs 8-bit address, or unhandled clock stretching; SPI mode (CPOL/CPHA) mismatch.
- Assuming the desktop C mindset: heavy `malloc`/`free`, recursion, large stack frames, blocking `printf`, and ignoring WCET.
- Floating point in an ISR without saving FPU context, or assuming the FPU is free.
- **Priority inversion** left unaddressed; using a binary semaphore where a mutex with priority inheritance is required.
- Endianness and packing assumptions in serialization that differ across host and target.
- Treating average-case latency as good enough in a hard-real-time path; debugging timing bugs with breakpoints that change the timing (Heisenbugs).
- Bootloader that doesn't relocate the vector table / set SP+PC correctly before jumping to the app; firmware update with no rollback (brick on power loss mid-write).

## Self-assessment checklist

- [ ] I can sketch the target's memory map and explain why an MMIO write has a physical side effect.
- [ ] I can explain what `volatile` does and does not guarantee, with a disassembly example, and when I additionally need a barrier.
- [ ] I can write a minimal linker script and startup file and explain how `.data`/`.bss` get initialized before `main`.
- [ ] I can flash and debug a board over SWD with GDB + OpenOCD/probe-rs without an IDE.
- [ ] I can configure any pin from the datasheet (clock, mode, type, pull) with no HAL.
- [ ] I can write a correct minimal ISR, set NVIC priorities for correct preemption, and safely share data with `main`.
- [ ] I can generate precise PWM and measure an external signal with input capture.
- [ ] I can acquire an evenly-sampled ADC buffer via timer trigger + DMA and reason about aliasing.
- [ ] I can bring up an unfamiliar sensor on UART/SPI/I2C from its datasheet and decode the bus on a logic analyzer.
- [ ] I can send/receive CAN frames and explain arbitration.
- [ ] I can stream a peripheral into a circular DMA buffer and handle half/full interrupts without data loss.
- [ ] I can configure a clock tree and a low-power mode and explain the effect on peripheral timing.
- [ ] I can decompose a system into RTOS tasks with correct priorities, queues, and mutexes.
- [ ] I can demonstrate priority inversion and fix it with priority inheritance.
- [ ] I can measure worst-case latency and jitter on a scope/logic analyzer and argue a deadline is met.
- [ ] I can decode a HardFault to a source line and use RTT/SWO instead of blind `printf`.
- [ ] I can read a map file and cut RAM/flash usage to fit a budget.
- [ ] I can lay out flash for a bootloader + A/B app and perform a validated, rollback-safe firmware update.
- [ ] **(MODERN)** I can build the same app in embedded Rust (RTIC/Embassy + embedded-hal) and in Zephyr (device tree + Kconfig + MCUboot).
- [ ] **(MODERN)** I can run a real-time FFT/FIR pipeline with CMSIS-DSP and state the sample-rate/FFT-size/CPU budget tradeoff.

## Canonical resources

Books:
- *Making Embedded Systems* — Elecia White (2nd ed.) — the modern, practical mindset book; ideal refresh anchor.
- *The Definitive Guide to ARM Cortex-M3 and Cortex-M4 Processors* — Joseph Yiu — the authoritative CPU/NVIC/architecture reference.
- *Embedded C* — Pont, and *Test-Driven Development for Embedded C* — James Grenning (for software hygiene on MCUs).
- *Programming Embedded Systems* — Barr & Massa (fundamentals).
- *The Art of Designing Embedded Systems* — Jack Ganssle (engineering judgment); his website/newsletter on debouncing and timing is canonical.
- *Mastering the FreeRTOS Real Time Kernel* — official free FreeRTOS book/guide.
- **(MODERN)** *The Embedded Rust Book* + *The Embedonomicon* + the `cortex-m`/`embedded-hal` docs; Ferrous Systems training material; RTIC and Embassy books.

Primary documents (read these like a technician):
- Your MCU's **Reference Manual** and **Datasheet** (e.g. STM32F4 RM0090), the ARM **Cortex-M4 Generic User Guide**, and the device **SVD** file.

Courses / hands-on:
- Shawn Hymel / DigiKey "Introduction to RTOS" video series (FreeRTOS, excellent).
- Zephyr Project official docs + Nordic Developer Academy (free, high-quality Zephyr + nRF Connect courses).
- ESP-IDF Programming Guide; Raspberry Pi Pico C/C++ SDK and "Getting Started" docs.
- Memfault's *Interrupt* blog (deeply practical: fault debugging, RTT, bootloaders, FOTA).
- ARM **CMSIS-DSP** documentation and examples.

Tools to own/use:
- A debug probe (ST-Link/J-Link/CMSIS-DAP/Picoprobe), **OpenOCD** and/or **probe-rs**, **arm-none-eabi-gcc** + GDB, **SegGER J-Link/RTT/Ozone** (free for eval), a **logic analyzer** (Saleae or a cheap 8-ch + **PulseView/sigrok**), and an **oscilloscope**. Dev boards: an **STM32 Nucleo/Discovery** (F4 or F7 for DSP), a **Raspberry Pi Pico (RP2040)**, and an **ESP32** dev kit.
