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
        timeRemaining = 0;
        available = true;
        
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
        timeRemaining = p.getNextTask().getTimeLeft();
    }
    
    public Process update(){
        if(currentProcess != null){
            available = false;
            if(currentProcess.update()){
                available = true; 
                Process p = currentProcess;
                currentProcess = null;
                return p;
            }
            timeRemaining--;
            return null;
        }
        Process p = currentProcess;
        available = true;
        currentProcess = null;
        return p;
    }
}
