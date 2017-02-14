package processcheduling;

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
}
