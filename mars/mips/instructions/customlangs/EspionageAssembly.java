package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;


public class EspionageAssembly extends CustomAssembly {
    @Override
    public String getName() {
        return "Espionage Assembly";
    }

    @Override
    public String getDescription() {
        return "An espionage based assembly language similar to MIPS";
    }

    @Override
    protected void populate() {
        // eqp (same as li)
        instructionList.add(
            new BasicInstruction("eqp $t0, 100",
                "Equip register with a value (Assign value to register: set $t0 to signed 16-bit immediate)",
            BasicInstructionFormat.I_FORMAT,
            "000001 00000 fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int imm = operands[1] << 16 >> 16;
                        RegisterFile.updateRegister(operands[0], imm);
                    }
               }));
        // agg (same as addi)
        instructionList.add(
            new BasicInstruction("agg $t1, $t2, 100",
            	"Aggregate Data (Addition immediate with overflow: $t1 = $t2 + signed 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000010 sssss fffff tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = operands[2] << 16 >> 16;
                        int sum = add1 + add2;
                    // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && add2 >= 0 && sum < 0)
                            || (add1 < 0 && add2 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement,
                                "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                  }
               }));
        instructionList.add(
            new BasicInstruction("it $t1, label($t2)",
            	"Interrogate Target: Get a value from your enemy's memory at $t2 + offset and set $t1 to it.",
                BasicInstructionFormat.I_FORMAT,
                "000011 ttttt fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getWord(
                            RegisterFile.getValue(operands[2]) + operands[1])
                            );
                        } 
                        catch (AddressErrorException e) {
                           throw new ProcessingException(statement, e);
                        }
                    }
               }));
        instructionList.add(
            new BasicInstruction("it $t1, 0($t2)",
                "Interrogate Target: Get a value from your enemy's memory at $t2 + offset and set $t1 to it.",
                BasicInstructionFormat.I_FORMAT,
                "000011 ttttt fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            RegisterFile.updateRegister(operands[0],
                                    Globals.memory.getWord(
                                            RegisterFile.getValue(operands[2]) + operands[1])
                            );
                        }
                        catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("mem $t1, label($t2)",
                "Memorize: Store something important (value in $t1) in your memory.",
            	BasicInstructionFormat.I_FORMAT,
                "000100 ttttt fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            Globals.memory.setWord(
                            RegisterFile.getValue(operands[2]) + operands[1],
                                    RegisterFile.getValue(operands[0])
                            );
                        } 
                        catch (AddressErrorException e) {
                           throw new ProcessingException(statement, e);
                        }
                    }
               }));
        instructionList.add(
            new BasicInstruction("mem $t1, 0($t2)",
                "Memorize: Store something important (value in $t1) in your memory.",
                BasicInstructionFormat.I_FORMAT,
                "000100 ttttt fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            Globals.memory.setWord(
                                    RegisterFile.getValue(operands[2]) + operands[1],
                                    RegisterFile.getValue(operands[0])
                            );
                        }
                        catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("vic $t1, $t2, label",
                "Verify Intelligence Correlation: Branch to label's address if the intel in $t1 and $t2 are equal",
            	BasicInstructionFormat.I_BRANCH_FORMAT,
                "000101 fffff sssss tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                    
                        if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                            Globals.instructionSet.processBranch(operands[2]);
                        }
                    }
               }));
        instructionList.add(
            new BasicInstruction("lie $t1, $t2, label",
                "Lie to the enemy: If you lie ($t1 != $t2), branch to statement at label's address",
            	BasicInstructionFormat.I_BRANCH_FORMAT,
                "000110 fffff sssss tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                            Globals.instructionSet.processBranch(operands[2]);
                        }
                    }
               }));
        instructionList.add(
            new BasicInstruction("ra $t0",
                "Recruit Asset: recruit a new asset ($t0++)",
                BasicInstructionFormat.I_FORMAT,
                "000111 00000 fffff 0000000000000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[0]);
                        int add2 = 1;
                        int sum = add1 + add2;
                    // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && sum < 0) || (add1 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement,
                                "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
               }));
        instructionList.add(
            new BasicInstruction("pt $t0",
                "Poison Target: Poison a target ($t0--)",
                BasicInstructionFormat.I_FORMAT,
                "001111 00000 fffff 0000000000000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[0]);
                        int add2 = -1;
                        int sum = add1 + add2;
                        // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && sum < 0) || (add1 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement,
                                    "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }));
        instructionList.add(
            new BasicInstruction("fw",
                "Full Wipe: Destroy all the data (reset every register to zero)",
                BasicInstructionFormat.I_FORMAT,
                "000000 00000 00000 0000000000000000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        for (int i = 1; i < 26; i++) {
                            RegisterFile.updateRegister(i, 0);
                        }
                        RegisterFile.updateRegister(31, 0);
                    }
               }));
        instructionList.add(
            new BasicInstruction("st $t1, label",
                "Surveil Target: Surveil a target to get their address and put it in $t1",
                BasicInstructionFormat.I_FORMAT,
                "001000 00000 fffff ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        RegisterFile.updateRegister(operands[0], operands[1]);
                    }
                }));
        instructionList.add(
            new BasicInstruction("hi $t1",
                "Hide Intel: Push value in $t1 to the stack and reset $t1 to 0",
                BasicInstructionFormat.I_FORMAT,
                "001001 11101 fffff 1111111111111100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        RegisterFile.updateRegister(29, RegisterFile.getValue(29) - 4);
                        try {
                            Globals.memory.setWord(
                                RegisterFile.getValue(29),
                                RegisterFile.getValue(operands[0])
                            );
                        }
                        catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                        RegisterFile.updateRegister(operands[0], 0);
                    }
                }));
        instructionList.add(
            new BasicInstruction("ri $t1",
                "Reveal Intel: Pop value from the stack into $t1",
                BasicInstructionFormat.I_FORMAT,
                "001010 11101 fffff 0000000000000100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getWord(
                                    RegisterFile.getValue(RegisterFile.STACK_POINTER_REGISTER)
                                )
                            );
                        }
                        catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                        RegisterFile.updateRegister(29, RegisterFile.getValue(29) + 4);
                    }
                }));
        instructionList.add(
            new BasicInstruction("gmbli $t1, 0, 10",
                "Gamble Immediate: Generate a random integer from 0 (low, inclusive) to 10 (high, exclusive) and place it in $t1." +
                        "\nWARNING if you get the wrong value, you might be KIA.",
                BasicInstructionFormat.I_FORMAT,
                "001110 00000 fffff ssssssss tttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int low = operands[1] << 16 >> 16;
                        int immHigh = operands[2] << 16 >> 16;
                        if (low >= immHigh) {
                            throw new ProcessingException(statement,
                                    "Low value needs to be greater than high value", Exceptions.SYSCALL_EXCEPTION);
                        }
                        Random rand = new Random();
                        int unlucky = rand.nextInt(immHigh - low) + low;
                        int roll = rand.nextInt(immHigh - low) + low;
                        if (roll == unlucky) {
                            SystemIO.printString("\nYou gambled and got unlucky. You were KIA.");
                            throw new ProcessingException();
                        }
                        RegisterFile.updateRegister(operands[0], roll);
                    }
                }));

        instructionList.add(
            new BasicInstruction("gmbl $t1, $t0, $t2",
                "Gamble: Generate a random integer from $t0 (low, inclusive) to $t2 (high, exclusive) and place it in $t1." +
                        "\nWARNING if you get the wrong value, you might be KIA.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 101001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int low = operands[1] << 16 >> 16;
                        int high = operands[2] << 16 >> 16;
                        if (low >= high) {
                            throw new ProcessingException(statement,
                                    "Low value needs to be greater than high value", Exceptions.SYSCALL_EXCEPTION);
                        }
                        Random rand = new Random();
                        int unlucky = rand.nextInt(high - low) + low;
                        int roll = rand.nextInt(high - low) + low;
                        if (roll == unlucky) {
                            SystemIO.printString("\nYou gambled and got unlucky. You were KIA.");
                            throw new ProcessingException();
                        }
                        RegisterFile.updateRegister(operands[0], roll);
                    }
                }));
        instructionList.add(
            new BasicInstruction("mi $t1, $t2, $t3",
                "Merge Intel: Signed addition; set $t1 to ($t2 plus $t3)",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = RegisterFile.getValue(operands[2]);
                        int sum = add1 + add2;
                        // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                            throw new ProcessingException(statement,
                                "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                    }
                }));
        instructionList.add(
            new BasicInstruction("exf $t1",
                "Exfiltrate: Return from mission (subroutine). Jump to statement whose address is in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 100001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processJump(RegisterFile.getValue(operands[0]));
                    }
                }));
        instructionList.add(
            new BasicInstruction("stl $t1, $t2",
                "Steal: Take the value in $t2 and put it in $t1. $t2 gets reset to zero",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 100010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException
                    {
                        int[] operands = statement.getOperands();
                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                        RegisterFile.updateRegister(operands[1], 0);
                    }
                }));
        instructionList.add(
            new BasicInstruction("cpy $t1, $t2",
                "Copy Intel: copy value in $t2 into $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 100011",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                    }
                }));
        instructionList.add(
            new BasicInstruction("fea $t1",
                "Flip Enemy Agent: flip the sign of the value in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 100100",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int newValue = RegisterFile.getValue(operands[0]) * -1;
                        RegisterFile.updateRegister(operands[0], newValue);
                    }
                }));
        instructionList.add(
            new BasicInstruction("ei $t1",
                "Expose Intel: print the int value in $t1. If $v0 is 1, print the char value of $t1 (ascii)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 100101",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int sysArg = RegisterFile.getValue(2);
                        if (sysArg == 1) {
                            SystemIO.printString(String.valueOf((char) RegisterFile.getValue(operands[0])));
                        } else {
                            SystemIO.printString(String.valueOf(RegisterFile.getValue(operands[0])));
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("ti $t1",
                "Transmit Intel: print the string at the memory address in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 100110",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        char ch = 0;
                        try {
                            int byteAddress = RegisterFile.getValue(operands[0]);
                            ch = (char) Globals.memory.getByte(byteAddress);
                            // won't stop until NULL byte reached!
                            while (ch != 0) {
                                SystemIO.printString(String.valueOf(ch));
                                byteAddress++;
                                ch = (char) Globals.memory.getByte(byteAddress);
                            }
                        }
                        catch (AddressErrorException e) {
                            throw new ProcessingException(statement, e);
                        }
                    }
                }));
        instructionList.add(
            new BasicInstruction("ii $t1",
                "Intercept Intel: intercept a number (input) from the console and store it in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 100111",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int inputNum = 0;
                        try {
                            inputNum = SystemIO.readInteger(5);
                        }
                        catch (NumberFormatException e) {
                            throw new ProcessingException(statement,
                                "invalid integer input ii",
                                Exceptions.SYSCALL_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], inputNum);
                    }
                }));
        instructionList.add(
            new BasicInstruction("terminated",
                "Terminated: your cover was blown, and now you (the program) gets terminated",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 101000",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {

                        SystemIO.printString("\nYour cover was blown, and this resulted in your termination");
                        throw new ProcessingException();
                    }
                }));
        instructionList.add(
            new BasicInstruction("di target",
                "Drop In: Parachute into a target location (jump to statement at target address)",
                BasicInstructionFormat.J_FORMAT,
                "001011 ffffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processJump(
                                ((RegisterFile.getProgramCounter() & 0xF0000000)
                                        | (operands[0] << 2)));
                    }
                }));
        instructionList.add(
            new BasicInstruction("inf target",
                "Infiltrate: infiltrate a target with an escape plan (set $ra to Program Counter and then jump to target)",
                BasicInstructionFormat.J_FORMAT,
                "001100 ffffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processReturnAddress(31); // RegisterFile.updateRegister(31, RegisterFile.getProgramCounter());
                        Globals.instructionSet.processJump(
                                (RegisterFile.getProgramCounter() & 0xF0000000)
                                        | (operands[0] << 2));
                    }
                }));
        instructionList.add(
            new BasicInstruction("gdcf 5000",
                "Go Deep Cover For: go deep cover for 5000 years (sleep program for 5000 milliseconds)",
                BasicInstructionFormat.I_FORMAT,
                "001101 ffffffffffffffffffffffffff",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        try {
                            Thread.sleep(Math.abs(operands[0]));
                        }
                        catch (InterruptedException e) {
                            throw new ProcessingException(statement,
                                    "Go Deep Cover For (gdcf) interrupted",
                                    Exceptions.SYSCALL_EXCEPTION);
                        }
                    }
                }));

    }
}