package processcheduling;

import java.util.LinkedList;

/**
 *
 * @author Ian Fennen
 */
public class Disk {
    
    private int timeRemaining;
    private boolean available;
    private Process currentProcess;

    public Disk(){
    }

    public int getTimeRemaining() {
        return timeRemaining;
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
        if(currentProcess != null && timeRemaining > 0){
            available = false;
            if(currentProcess.update()){
                currentProcess = null;
            }
            timeRemaining--;
            return false;
        }
        available = true;
        return true;
    }
}
