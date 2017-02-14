
package processcheduling;

/**
 *
 * @author Ian Fennen
 */
public class Task {
    private TaskType type;
    private int timeLeft;
    
    public Task(TaskType tt, int initialTime){
        type = tt;
        timeLeft = initialTime;
    }
    
    public TaskType getType(){
        return type;
    }
    
    /**
     * Will subtract one tick from the time left and will
     * return true if the Task is complete.
     * @return True if the Task is complete; False if the Task is not complete.
     */
    public boolean update(){
        timeLeft--;
        return (timeLeft == 0);
    }
}
