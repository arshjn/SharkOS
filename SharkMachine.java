import java.util.*;
import java.io.*;

public class SharkMachine {

    // Declaring Registers as follows:
    // acc is Accumulator, 32 bits
    // psiar is Primary Storage Instruction Address Register, 16 bits
    // sar is Storage Address Register, 16 bits
    // sdr is Storage Data Register, 32 bits
    // tmpr is Temporary Register, 32 bits
    // csiar is Control Storage Instruction Address Register, 16 bits
    // ir is Instruction Register, 32 bits implemented as 2 element array
    // mir is Micro-instruction Register,32 bits
    int acc, psiar, sar, sdr, tmpr, csiar, mir;
    int ir[];

    // Getting an object of Memory to use as system memory
    Memory memory;

    SharkMachine() {
        acc = 0;
        psiar = 0;
        sar = 0;
        sdr = 0;
        tmpr = 0;
        csiar = 0;
        ir = new int[2];
        mir = 0;
        memory = new Memory();
    }

    // Memory Display
    public void printState() {
        System.out.println("Registers: ");
        System.out.println("ACC = " + acc);
        System.out.println("PSIAR = " + psiar);
        System.out.println("SAR = " + sar);
        System.out.println("SDR = " + sdr);
        System.out.println("TMPR = " + tmpr);
        System.out.println("CSIAR = " + csiar);
        System.out.println("IR[opCode][Operand]: " + ir[0] + "  " + ir[1]);
        System.out.println("MIR = " + mir);
        memory.display();
    }

    public int loadProgram(File file, int location) {// Loads program from File, starting at given location. Returns
                                                     // location for loading next program.
        try {
            Scanner program = new Scanner(file);

            while (program.hasNextLine()) {
                String data = program.nextLine();
                loadInstruction(data, location);
                location++;
            }
            program.close();
        } catch (FileNotFoundException e) {
            System.out.println("An Error Occured. File not opened");
            e.printStackTrace();
        }
        return location;
    }

    public void loadInstruction(String inst, int address) { // If operand is not int, it exits the system displaying
                                                            // instruction with problem.
        String[] arr = inst.split(" ");
        int opCode = getOpCode(arr[0]);
        int operand = 0;
        if (opCode != 70 && opCode != 80) { // There is no operand for the END or YLD instruction
            try {
                operand = Integer.parseInt(arr[1]);
            } catch (Exception e) {
                System.out.println("Invalid Syntax in program (2nd part of instruction must be Int): " + inst);
                System.exit(0);
            }
        }
        memory.writeInstruction(address, opCode, operand);
    }

    public int getOpCode(String op) { // If Instruction is not recognized, it exits the system and displays the
                                      // unrecognized Operation

        switch (op) {
            case "LDI":
                return 0;
            case "ADD":
                return 10;
            case "SUB":
                return 20;
            case "LDA":
                return 30;
            case "STR":
                return 40;
            case "BRH":
                return 50;
            case "CBR":
                return 60;
            case "END":
                return 70;
            case "YLD":
                return 80;
            default:
                System.out.println("Error, instruction not recognized. Given Operation: " + op);
                printState();
                System.exit(0);
                return -1;
        }
    }

    public void READ() {
        sdr = memory.readOperand(sar);
    }

    public void WRITE() {
        memory.writeData(sar, sdr);
    }

    public void FETCH() {
        sar = psiar;
        ir = memory.read(sar);
        csiar = ir[0];
        sdr = ir[1];
    }

    public void LDI() { // OpCode 0
        System.out.print("LDI");
        acc = psiar + 1;
        psiar = acc;
        acc = sdr;
        csiar = 0;
    }

    public void ADD() { // OpCode 10
        System.out.print("ADD");
        tmpr = acc;
        acc = psiar + 1;
        psiar = acc;
        acc = tmpr;
        tmpr = sdr;
        sar = tmpr;
        READ();
        tmpr = sdr;
        acc += tmpr;
        csiar = 0;
    }

    public void SUB() { // OpCode 20
        System.out.print("SUB");
        tmpr = acc;
        acc = psiar + 1;
        psiar = acc;
        acc = tmpr;
        tmpr = sdr;
        sar = tmpr;
        READ();
        tmpr = sdr;
        acc = acc - tmpr;
        csiar = 0;
    }

    public void LDA() { // OpCode 30
        System.out.print("LDA");
        acc = psiar + 1;
        psiar = acc;
        tmpr = sdr;
        sar = tmpr;
        READ();
        acc = sdr;
        csiar = 0;
    }

    public void STR() { // OpCode 40
        System.out.print("STR");
        tmpr = acc;
        acc = psiar + 1;
        psiar = acc;
        acc = tmpr;
        tmpr = sdr;
        sar = tmpr;
        sdr = acc;
        WRITE();
        csiar = 0;
    }

    public void BRH() { // OpCode 50 - The SDR contains increment value instead of memory address
        System.out.print("BRH");
        psiar += sdr;
        csiar = 0;
    }

    public void CBR() { // OpCode 60 - The SDR contains increment value instead of memory address
        System.out.print("CBR");
        if (acc == 0) {
            psiar += sdr;
            csiar = 0;
        } else {
            tmpr = acc;
            acc = psiar + 1;
            psiar = acc;
            acc = tmpr;
            csiar = 0;
        }
    }

    public void END() {
        System.out.print("END");

        acc = 0;
        psiar = 0;
        sar = 0;
        sdr = 0;
        tmpr = 0;
        csiar = 0;
        ir = new int[2];
        mir = 0;
    }

    public void YLD() {
        System.out.print("YLD");
        tmpr = acc;
        acc = psiar + 1;
        psiar = acc;
        acc = tmpr;
    }

    public int runInstruction() { // Decode-Execute that comes after the Fetch
        // Executes instruction, returns 0 for successful execution. Returns 1 for END,
        // and -1 for YLD (Interrupt)
        switch (csiar) {
            case 0:
                LDI();
                return 0;
            case 10:
                ADD();
                return 0;
            case 20:
                SUB();
                return 0;
            case 30:
                LDA();
                return 0;
            case 40:
                STR();
                return 0;
            case 50:
                BRH();
                return 0;
            case 60:
                CBR();
                return 0;
            case 70:
                END();
                return 1;
            case 80:
                YLD();
                return -1;
            default: // This shouldn't be called because this exception would have already been
                     // caught during load.However if the program overwrites memory, it may happen
                System.out.println(
                        "Error - Invalid CSIAR during Fetch-Decode-Execute (Memory Over-write possible): " + csiar);
                printState();
                System.exit(0);
                return 1; // Code execution will never get here

        }
    }

}