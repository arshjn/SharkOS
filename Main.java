import java.util.*;
import java.io.*;

public class Main {

    static int numOfProcesses = 0;
    static int location = 500; // Start loading programs at 500
    static SharkMachine sharkMachine = new SharkMachine();
    static Queue<Process> processQ = new LinkedList<Process>();
    static Queue<Process> ReadyToRun = new LinkedList<Process>();
    final static int timeQuantum = 4; // CONSTANT SET TIME QUANTUM VALUE TO DO CONTEXT SWITCH AND USE FOR ROUND ROBIN
    static int systemTime = 0; // Time starts at 0

    public static void main(String[] args) {
        try {
            PrintStream o = new PrintStream(new File("SystemLog.txt"));
            // PrintStream console = System.out; // Saving console in case I want to set
            // later
            System.setOut(o);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Unable to set Output to file instead of console");
        }

        // Set File containing name of all programs to run (with arrival times in
        // ascending order) here:
        String filename = "ProgramList.txt";
        loadPrograms(filename);
        // sharkMachine.printState();

        StartSystem();

        System.out.println("\n\nMachine has finished running");
    }

    public static void loadPrograms(String filename) { // Loads jobs in memory, creates PCBs

        try {
            File file = new File(filename);
            Scanner programs = new Scanner(file);

            while (programs.hasNextLine()) {

                String line = programs.nextLine();
                String[] tempStr = line.split(" ");

                File program = new File(tempStr[0]);// Gets the actual process file name
                int temp = 0; // Placeholder for new location
                temp = sharkMachine.loadProgram(program, location);// Loads that program into memory, returns the next
                                                                   // location to write the next process
                int aT = Integer.parseInt(tempStr[1]);
                Process process = new Process(numOfProcesses, location, aT);// Makes a new Process
                location = temp;
                numOfProcesses++;// Increments the number of processes
                processQ.add(process);// Adds the process to the queue
                process.setEndAddress(location - 1);
                System.out.println("Added Process PID: " + process.getPid() + " at Arrival Time: " + aT);
            }
            programs.close();
        } catch (FileNotFoundException e) {

            System.out.println("An Error Occured. List of Programs not found");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void StartSystem() {
        Process process = processQ.poll();
        ReadyToRun.add(process);
        runNextProcess();
    }

    public static void runNextProcess() {
        if (!ReadyToRun.isEmpty())
            runProcess(ReadyToRun.poll());
    }

    // runProcess implements Round Robin Scheduling. Processes are added to ready to
    // run based on their arrival time, If the systemTime is a multiple
    // of the timeQuantum, then it calls the context switch part of the funtion
    public static void runProcess(Process process) {
        // int endloc = process.getEndAddress();
        System.out.println("\nCurrent Process PID: " + process.getPid());
        setState(process);
        int flag = 0;
        while (flag == 0) {
            if (!processQ.isEmpty() && (systemTime == processQ.peek().getArrivalTime())) {
                ReadyToRun.add(processQ.poll());
            }
            if (systemTime == 0 || (systemTime % timeQuantum != 0)) { // If the systemTime is 0 we do not want the
                                                                      // system to move to the next PCB
                sharkMachine.FETCH();
                flag = sharkMachine.runInstruction();
                System.out.println(" Instruction from PID: " + process.getPid() + ", Executed at SYSTEM TIME: " + systemTime);
                systemTime++;
            } else {
                flag = -1;
                System.out.println("\nTIMER RUNOUT at SYSTEM TIME: " + systemTime);
                systemTime++;
            }
        }
        if (flag == 1) {
            // Program has ended.
            System.out.println("\nEnd of Process with PID: " + process.getPid() + "\n");
            sharkMachine.printState();
            runNextProcess();

        } else if (flag == -1) {
            // YLD Interrupt or Timer runout, Context Switch
            if (ReadyToRun.isEmpty()) { // If no more jobs, continue running same Job
                saveState(process);
                System.out.println("No more jobs in ReadyToRun Queue, executing same process");
                runProcess(process);

            } else { // Switch jobs in system
                saveState(process); // Saves state of Process
                ReadyToRun.add(process); // Adds process at the end of the ReadyToRun Queue
                runNextProcess();// Runs next process
            }
        }
    }

    public static void setState(Process process) { // Sets the state of the SharkMachine form the PCB
        sharkMachine.acc = process.acc;
        sharkMachine.csiar = process.csiar;
        sharkMachine.ir = process.ir;
        sharkMachine.mir = process.mir;
        sharkMachine.psiar = process.psiar;
        sharkMachine.sar = process.sar;
        sharkMachine.sdr = process.sdr;
        sharkMachine.tmpr = process.tmpr;
    }

    public static Process saveState(Process process) { // Saves state from SharkMachine to the PCB
        process.setAcc(sharkMachine.acc);
        process.setCsiar(sharkMachine.csiar);
        process.setMir(sharkMachine.mir);
        process.setPsiar(sharkMachine.psiar);
        process.setSar(sharkMachine.sar);
        process.setSdr(sharkMachine.sdr);
        process.setTmpr(sharkMachine.tmpr);
        process.setIR(sharkMachine.ir);
        return process;
    }
}
