public class Memory {

    // Memory is a 1024x2 array with each address having an opCode and operand.
    // OpCode for LDI/data = 0
    int[][] memory;

    // Default Constructor initializes all memory to 0
    Memory() {
        memory = new int[1024][2];
    }

    public int[] read(int address) { // Returns an array {op code, operand} at address
        int[] mem = { 0, 0 };
        mem[0] = memory[address][0];
        mem[1] = memory[address][1];
        return mem;
    }

    public int readOpCode(int address) {// Returns left 16-bit of word (Op Code) at address. OpCode = 0 for data or LDI
        return memory[address][0];
    }

    public int readOperand(int address) {// Returns right-16 bit of word (Operand) at address
        return memory[address][1];
    }

    public boolean writeInstruction(int address, int opCode, int operand) {// Writes opCode and Operand to memory
        memory[address][0] = opCode;
        memory[address][1] = operand;
        return true;
    }

    public boolean writeData(int address, int data) {// Writes data to memory at given address
        memory[address][0] = 0;
        memory[address][1] = data;
        return true;
    }

    public void display() { // Function to display memory 
        for (int i = 0; i < 1024; i++) {
            for (int j = 0; j < 2; j++)
                System.out.print("Memory [" + i + "] [" + j + "] = " + memory[i][j] + "  ");
            System.out.println();
        }
    }
}