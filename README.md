# MARS-LE
MARS (fork of a fork) MIPS Assembler and Runtime Simulator

 MARS is a lightweight interactive development environment (IDE) for programming in MIPS assembly language, intended for educational-level use with Patterson and Hennessy's Computer Organization and Design.
 MARS was developed by Pete Sanderson and Ken Vollmar and can be found at https://dpetersanderson.github.io/.

 The MARS-LE fork of MARS is a research project undertaken by John Edelman, Jun Law, and Dominic Dabish in summer 2025, aiming to modernize MARS and specifically to enable students to specify their own custom assembly languages for use with the MARS simulator.

## Espionage Assembly:
Espionage Assembly is a custom MIPS-like assembly language implemented
using MARS-LE.
## Instructions in Espionage Assembly:
- `eqp $t0, 100` – **Equip:** Equip register with a value (Assign value to register: set $t0 to signed 16-bit immediate)
- `agg $t1, $t2, 100` – **Aggregate Data:** Addition immediate with overflow (`$t1 = $t2 + 100`)
- `it $t1, label($t2)` – **Interrogate Target:** Get a value from your enemy's memory at `$t2` + offset and set `$t1` to it.
- `mem $t1, label($t2)` – **Memorize:** Store something important (value in `$t1`) in your memory.
- `vic $t1, $t2, label` – **Verify Intelligence Correlation:** Branch to label's address if the intel in `$t1` and `$t2` are equal
- `lie $t1, $t2, label` – **Lie to the Enemy:** If you lie (`$t1 != $t2`), branch to statement at label's address
- `ra $t0` – **Recruit Asset:** Recruit a new asset ($t0++)
- `pt, $t0` – **Poison Target:** Poison a target (`$t0--`)
- `fw` – **Full Wipe:** Destroy all the data (reset every register to zero)
- `st, $t1, label` – **Surveil Target:** Surveil a target to get their address and put it in `$t1`
- `ti $t1` – **Transmit Intel:** print the string at the memory address in `$t1`
- `di label` – **Drop In:** Parachute into a target location (jump to statement at target address)
- `ei $t1` – **Expose Intel:** Print the int value in `$t1`. If `$v0` is 1, print the char value of `$t1` (ascii)
- `gdcf 5000` – **Go Deep Cover For:** Sleep for the specified number of milliseconds
- `inf label` – **Infiltrate:** Infiltrate a target with an escape plan (set `$ra` to Program Counter and then jump to target)
- `cpy $t1, $t2` – **Copy Intel:** Copy the contents from one register to another
- `hi $t1` – **Hide Intel:** Push value in `$t1` to the stack and reset `$t1` to 0
- `ri #t1` – **Reveal Intel:** Pop value from the stack into `$t1`
- `gmbli $t1, 0, 10` – **Gamble Immediate:** Gamble Immediate: Generate a random integer from 0 (low, inclusive) to 10 (high, exclusive) and place it in `$t1`. **WARNING** if you get the wrong value, you might be KIA.
- `gmbl $t1, $t0, $t2` – **Gamble:** Generate a random integer from `$t0` (low, inclusive) to `$t2` (high, exclusive) and place it in `$t1`. **WARNING** if you get the wrong value, you might be KIA.
- `mi $t1, $t2, $t3` – **Merge Intel:** Signed addition; set `$t1` to `$t2 + $t3`
- `exf $t1` – **Exfiltrate:** Return from mission (subroutine). Jump to statement whose address is in `$t1`
- `stl $t1, $t2` – **Steal:** Take the value in `$t2` and put it in `$t1`. `$t2` gets reset to zero.
- `fea $t1` – **Flip Enemy Agent:** flip the sign of the value in `$t1`
- `ii $t1` – **Intercept Intel:** Intercept a number (input) from the console and store it in `$t1`
- `terminated` – **Terminated:** Your cover was blown, and now you (the program) gets terminated