package processcheduling;

import java.util.LinkedList;

/**
 *
 * @author Ian Fennen
 */
public class Core {
    
    public final int timeSlice;
    private int sliceRemaining;
    private boolean available;
    private Process currentProcess;

    public Core(int ts){
        timeSlice = ts;
    }
    public int getTimeSlice() {
        return timeSlice;
    }

    public int getSliceRemaining() {
        return sliceRemaining;
    }

    public boolean isAvailable() {
        return available;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }
    
    public void setCurrentProcess(Process p){
        currentProcess = p;
    }
    
    public boolean update(){
        if(currentProcess != null && sliceRemaining > 0){
            available = false;
            if(currentProcess.update()){
                currentProcess = null;
            }
            sliceRemaining--;
            return false;
        }
        available = true;
        return true;
    }
}
