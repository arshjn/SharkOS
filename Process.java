
public class Process {

    private int pid;// Process ID
    private int startAddress;// Address for first line of Process
    private int endAddress;
    private int arrivalTime;
    int acc, psiar, sar, sdr, tmpr, csiar, mir;
    int ir[];

    public Process(int PID, int loc, int aT) { // Constructor for PCB. Takes PID, startAddress, arrival time for PCB
        pid = PID;
        startAddress = loc;
        arrivalTime = aT;
        acc = 0;
        psiar = loc;
        sar = 0;
        sdr = 0;
        tmpr = 0;
        csiar = 0;
        ir = new int[2];
        mir = 0;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getStartAddress() {
        return this.startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getEndAddress() {
        return this.endAddress;
    }

    public void setEndAddress(int endAddress) {
        this.endAddress = endAddress;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public void setPsiar(int psiar) {
        this.psiar = psiar;
    }

    public void setSar(int sar) {
        this.sar = sar;
    }

    public void setSdr(int sdr) {
        this.sdr = sdr;
    }

    public void setTmpr(int tmpr) {
        this.tmpr = tmpr;
    }

    public void setCsiar(int csiar) {
        this.csiar = csiar;
    }

    public void setMir(int mir) {
        this.mir = mir;
    }

    public void setIR(int ar[]) {
        this.ir[0] = ar[0];
        this.ir[1] = ar[1];
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

}
