package processcheduling;

import java.util.LinkedList;

/**
 *
 * @author Ian Fennen
 */
public class Process {
    /**
     * The time that this {@code Process} will start.
     */
    private final int startTime;
    /**
     * The list of {@code Task}s left for the {@code Process} to complete.
     */
    private LinkedList<Task> tasks = null;
    
    public final int PID;
    
    
    /**
     * This is where the process currently is. Used for update Method.
     */
    private ProcessLocation location;
    
    public Process(int st, int pid){
        startTime = st;
        PID = pid;
        tasks = new LinkedList();
    }
    
    public void addTask(Task t){
        //if(!(t.getType() == TaskType.DISK && t.getTimeLeft() == 0)){
            tasks.add(t);
        //}
    }
    
    public int getStartTime(){
        return startTime;
    }
    
    public Task getNextTask(){
        if(!tasks.isEmpty()){
            return tasks.getFirst();
        }
        return null;
    }
    
    public void setLocation(ProcessLocation pl){
        location = pl;
    }
    
    public ProcessLocation getLocation(){
        return location;
    }
    
    /**
     * Will update the current {@code Task}.
     * @return True if {@code Process} is complete; False if the {@code Task}s are not complete.
     */
    public boolean update(){
        if(tasks.isEmpty()){
            return true;
        }
        Task currentTask = tasks.getFirst();
        switch(currentTask.getType()){
            case CORE:
                if(location == ProcessLocation.CORE){
                    if(currentTask.update()){
                        
                        tasks.remove();
                        return true;
                    }
                    else{
                        //System.out.println("CORE UPDATE: " + currentTask.getTimeLeft());
                    }
                }
               break;
            case DISK:
                if (location == ProcessLocation.DISK) {
                    if (currentTask.update()) {
                        tasks.remove();
                        return true;
                    }
                }
               break;
            case DISPLAY:
                if(location == ProcessLocation.DISPLAY){
                    if(currentTask.update()){
                        tasks.remove();
                        return true;
                    }
                }
               break;
            case INPUT:
                if (location == ProcessLocation.DISPLAY) {
                    if (currentTask.update()) {
                        tasks.remove();
                        return true;
                    }
                }
               break;
            default:
                //Waiting in a queue. Do nothing.
               break;
        }
        return false;
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("From Process PID = " + PID +"\n\tTasks:"+tasks.size());
        for(int i = 0; i < tasks.size(); i++){
            Task t = tasks.get(i);
            s.append("\t");
            s.append(t);
        }
        
        return s.toString();
    }
}
