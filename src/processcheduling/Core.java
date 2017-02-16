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
        available = true;
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
        if(p != null){
            available = false;
            sliceRemaining = timeSlice;
        }
    }
    
    public Process update(){
        if(currentProcess != null && sliceRemaining > 0){
            available = false;
            if(currentProcess.update()){
                available = true;
                //System.out.println("CORE RELEASED EARLY");
                return currentProcess;
            }
            sliceRemaining--;
            return null;
        }
        available = true;
        return currentProcess;
    }
}
