# SharkOS
A simulated machine with registers and memory and an operating system to run SharkMachine assembly language programs 

**Overview**

The project involved simulating a machine (Shark Machine) with main memory (1024 elements) and registers (ACC, PSIAR, SAR, SDR, TMPR, CSIAR, IR, MIR) and certain fundamental commands. An operating system (SharkOS) was developed to run on this simulated machine architecture to provide multi-tasking capabilities. This involved a system clock, and also allowed for Yields (or interrupts). This involved creating processes and hence Process Control Blocks (PCBs), and then using these PCBs to run programs in a round-robin scheduling discipline.

The following sections discuss the implementation for memory, the Shark Machine, the PCBs and the SharkOS. This is followed by a walk-through of how the system initializes, and how it runs processes. The end of the report will discuss the Shark Machine Programming Language, and the programs written as per given specifications, and also how to run and modify these programs.

**Memory**

Memory is implemented as a class Memory.java. The class has only one data member: a multi-dimensional integer array, memory[1024][2]. Memory is a 1024x2 array with each address x having two sections – an **opCode** (memory[x][0]) and **operand** (memory[x][1]). If the address is just data (not an instruction), then the memory[x][0] = 0 for address x. For example, if the instruction ADD 150 is stored at the Memory address 200, then since opCode for ADD is 10, and the operand here is 150:

memory[200][0] = 10;

memory[200][1] = 150;

In addition to the memory array, there are several functions for the memory class:

1. int[] read (int address): Returns an array {op code, operand} at address
2. int readOpCode(int address): Returns left 16-bit of word (Op Code) at address. OpCode = 0 for data or LDI
3. public int readOperand(int address): Returns right-16 bit of word (Operand) at address
4. boolean writeInstruction(int address, int opCode, int operand): Writes opCode and Operand to memory address given, returns true after writing
5. boolean writeData(int address, int data): Writes data to memory at given address
6. void display(): Function to display contents of memory

**Shark Machine**

The Shark Machine is implemented as a class in SharkMachine.java.

The class has the following **registers** as **integer** variables:

1. **ACC** : Accumulator; The register involved in all arithmetic operations. One of the operands in each arithmetic operation must be in the Accumulator; the other must be in primary storage.
2. **PSIAR** : Primary Storage Instruction Address Register; This register points to the location in primary storage of the next machine language instruction to be executed.
3. **SAR:** Storage Address Register; This register is involved in all references to primary storage. It holds the address of the location in primary storage being read from or written to during the execution.
4. **SDR:** Storage Data Register; This register is also involved in all references to primary storage. It holds the data being written to or receives the data being read from primary storage at the location specified in the SAR
5. **TMPR:** Temporary Register; This register is used to extract the address portion (rightmost half) of the machine instruction in the SDR so that it may be placed in the SAR.
6. **CSIAR:** Control Storage Instruction Address Register; This register points to the location of the next microinstruction (in control storage) to be executed. It is used during the Fetch-Decode-Execute to identify the operation.
7. **MIR:** Micro-instruction Register; This register contains the current micro-instruction being executed. The implementation of SharkMachine in a High Level language like Java renders this useless, but it is included for the sake of completeness.

The following **register** is declared as an **integer array** :

1. **IR** : Instruction Register; This register contains the current instruction being executed. The ir[0] holds the **opCode** and the ir[1] holds the **operand**.

The SharkMachine also has a memory of the **Memory** type discussed previously. The following functions are included in the SharkMachine class:

1. int **loadProgram(File file, int location)**: Loads program from file into memory, starting at given location. Returns location for loading next program.
2. void **loadInstruction(String inst, int address)**: It loads one instruction into memory, based on the string and the address to enter it at. If operand is not int, it exits the system displaying the instruction with the problem.
3. int **getOpCode(String op)**: Returns opCode based on the String. This is where the &#39;Decode&#39; happens – at load time. If Instruction is not recognized, it exits the system and displays the unrecognized operation.

The **OpCode** is decoded as follows:

  1. **LDI: 0**
  2. **ADD: 10**
  3. **SUB: 20**
  4. **LDA: 30**
  5. **STR: 40**
  6. **BRH: 50**
  7. **CBR: 60**
  8. **END: 70**
  9. **YLD: 80**
1. void **READ()**: Data from primary storage location named in the SAR is placed in the SDR.
2. void **WRITE()**: Data in the SDR is placed in primary storage location named in the SAR.
3. void **FETCH()**: Fetches the next instruction pointed by the PSIAR, stores it in the IR. It also puts the next opCode in the CSIAR and
4. void **LDI()**: Load Immediate, OpCode 0
5. void **ADD()**: Add, OpCode 10
6. void **SUB()**: Sub, OpCode 20
7. void **LDA()**: Load Address, OpCode 30
8. void **STR()**: Store, OpCode 40
9. void **BRH()**: Absolute Branch, OpCode 50
10. void **CBR()**: Conditional Branch, OpCode 60
11. void **END()**: End (Halt Process), OpCode 70
12. void **YLD()**: Yield, OpCode 80
13. int **runInstruction()**: This function performs the Decode-Execute that comes after the Fetch. It Executes instruction, returns 0 for successful execution. Returns 1 for END, and -1 for YLD (Interrupt).

**Process**

The Process Control Blocks ( **PCBs** ) are implemented in a class in Process.java. The PCBs are fundamental to multi-tasking, and help make scheduling and context switching very easy, because they can save the state of the SharkMachine. This way they can easily restart executing when they next get time on the machine. The Process class has the following **integer** data members:

1. **pid** : Process ID
2. **startAddress** : The memory address where the PCB starts
3. **endAddress** : The last memory address of the PCB. Although not used during execution, this was built in should the need arrive for in the future.
4. **arrivalTime** : This is the time they arrived in the system. This information is useful for the Short Term Scheduler to dispatch jobs to the SharkMachine.

The Process also has all the registers identical to the SharkMachine, to save the state of any given process in the Machine in the event of a context switch. The methods in this class are only the required Getters and Setters. Manipulation of the PCBs in queues and the loader occur in the implementation of the Operating System.

**Main**

The Main.java file has the Main class that contains the implementation of the **SharkOS Operating System**. The file also has the main function to start the execution of the system. The Main class has some static variables:

1. int **numOfProcesses** initialized with 0 (used to assign Process IDs)
2. int **location** initialized with **500** (arbitrary location set for starting allocation of programs at this address, this value can be changed according to the need of the user)
3. SharkMachine **sharkMachine** (an object of the sharkMachine that will be used to run processes)
4. Queue\<Process> **processQ** implemented as a LinkedList\<Process> (This is the Process Queue that all PCBs are loaded to)
5. Queue\<Process> **ReadyToRun** implemented as a LinkedList\<Process> (This is the queue used for round-robin scheduling)
6. final int **timeQuantum** set to **4** (arbitrary constant set time quantum value to use the context switch and use for round robin scheduling)
7. int **systemTime** initialized at 0 (to keep track of the system time)

The class has the following static functions:

1. void **loadPrograms(String filename)**: Loads jobs in memory, creates PCBs. String filename is name of the file with list of programs and arrival times
2. void **StartSystem():** Starts the execution of processes after loading
3. void **runNextProcess()**: Runs the next process in ReadytoRun
4. void **runProcess(Process process):** Runs the process and implements Round Robin Scheduling. Processes are added to ready to run based on their arrival time, If the systemTime is a multiple of the timeQuantum or Yield instruction (system interrupt) is called, then it calls the context switch part of the function
5. void **setState (Process process):** Sets the state of the SharkMachine from the PCB
6. Process **saveState(Process process)**: Saves state from SharkMachine to the PCB

**SharkOS System**

The system opens the file named in the main function that contains the list of the text files that have the programs, and their arrival time into the system. The included file is named "ProgramList.txt." The file must have all programs with their arrival times in ascending order.

The main then calls the **loadPrograms** function with the filename. The loadProgams function goes through the file by line and splits it into program-file name and arrival time. It then calls the **loadProgram** function which goes through the text file with the program, and calls **loadInstruction** for each line of the program. The loadProgram returns the location at which the next program can be loaded. The control comes back to the loadPrograms which now creates a Process. This process is then added to the **processQ**. This process is looped for all file names in the "ProgramList.txt."

The main then calls **StartSystem()** which takes the head of the processQ and adds it to the ReadyToRun queue. It then calls the **runNextProcess()** function. The runNextProcess checks if the ReadyToRun queue is empty, and if it is not, it calls **runProcess()** with the head of the queue.

The runProcess function sets the state for the current PCB, and then begins execution. If the **system time** is equal to the **arrival time** for the next PCB in the processQ, it is added to the **ReadyToRun** queue. It then loops until the time quantum is reached and performs **fetch-decode-execute** for the PCB. A **flag** is used as a sentinel value to exit the loop. If the time quantum is reached, the flag is set to **-1** and it exits the loop. The loop is also exited for the **END** or the **YLD** instructions, which modify flag to **1** and **-1** respectively.

After exiting the loop, if the flag is 1, it means that the process has ended. In this case the state of the system is printed and then the runNextProcess() function is called. If the flag is -1, either the timer ran out, or there was a yield instruction, so the **context** needs to be switched. The state of the machine registers is saved in the PCB, and then the runNextProcess() is called, which takes care of loading the state of the next PCB into the machine and running the process.

If there are any syntax errors, or the system is unable to open any files that don&#39;t exist, most exceptions have been handled. The output should reflect if there was a problem, and describe what it was.

**Shark Machine Programming Language**

The Shark Machine Programming Language has the following commands:

1. **LDI:** Load Immediate

Syntax: LDI \<value>

Loads the \<value> in the Accumulator

2. **ADD:** Addition

Syntax: ADD \<address>

Adds value at \<address> to the Accumulator

3. **SUB:** Subtract

Syntax: SUB \<address>

Subtracts value at \<address> from the Accumulator

4. **LDA:** Load Address

Syntax: LDA \<address>

Loads value at \<address> to the Accumulator

5. **STR:** Store

Syntax: STR \<address>

Store value of the Accumulator at \<address>

6. **BRH:** Absolute Branch

Syntax: BRH \<offset>

Branches to current PSIAR + \<offset>

7. **CBR:** Conditional Branch

Syntax: CBR \<offset>

Branches to current PSIAR + \<offset> if value in the Accumulator is zero

8. **END:** End Program

Syntax: END

Ends the program

9. **YLD:** Yield

Syntax: YLD

Yields to the next process

The zip file includes 7 txt files.

"ProgramList.txt" is the file where you can specify which programs you want to run, and at what time. Remember to add these in ascending order of arrival times.

"AddProg.txt" is a program to sum in the values of all memory contents from 100 to 109, and store the result in 200. For demonstration, values are first stored in 100 through 109.

"DecrementProg.txt" decrements the value stored in location 201 by one until the result is zero. For demonstration, the value in 201 is set to **85** in the program.

"IncrementProg.txt" increments the value stored in location 301 by two until the value

has been increased by 20. For demonstration, the value in 301 has been set to **34**.

"Prog.txt" loads a 150, stores it, then subtracts it from a 150. The result is stored in 151.

"Program.txt" sums three numbers and stores the result.

"Program1.txt" loops subtracting 50 from 100 until the result is 0.

The output is named "SystemLog.txt." This file contains a log of the system, and a record of each instruction executed and from what process. It also displays memory after every process is finished.
