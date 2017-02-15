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
    LinkedList<Task> tasks = null;
    
    /**
     * This is where the process currently is. Used for update Method.
     */
    ProcessLocation location;
    
    public Process(int st){
        startTime = st;
        tasks = new LinkedList();
    }
    
    public void addTask(Task t){
        tasks.add(t);
    }
    
    public int getStartTime(){
        return startTime;
    }
    
    public Task getNextTask(){
        return tasks.getFirst();
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
                    }
                }
               break;
            case DISK:
                if (location == ProcessLocation.DISK) {
                    if (currentTask.update()) {
                        tasks.remove();
                    }
                }
               break;
            case DISPLAY:
                if(location == ProcessLocation.DISPLAY){
                    if(currentTask.update()){
                        tasks.remove();
                    }
                }
               break;
            case INPUT:
                if (location == ProcessLocation.DISPLAY) {
                    if (currentTask.update()) {
                        tasks.remove();
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
        return "From Process "+tasks.size();
    }
}
