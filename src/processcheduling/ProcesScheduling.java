
package processcheduling;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.System.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Ian Fennen
 */
public class ProcesScheduling {

    private static Core []coreArray;
    private static Disk disk;
    private static LinkedList<Process> readyQueue;
    private static LinkedList<Process> diskQueue;
    private static ArrayList<Process>  displayList;
    private static ArrayList<Process> waitingList;
    private static int tick = 0;
    private static int workingTasks = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        readyQueue = new LinkedList();
        diskQueue  = new LinkedList();
        waitingList = new ArrayList();
        displayList = new ArrayList();
        try{
            readFile();
        }
        catch(FileNotFoundException e){
            out.println("File  Not Found.");
        }
        out.println("*************START*************");
        while(workingTasks > 0 || !waitingList.isEmpty()){
            update();
            
            
            
            tick++;
        }
        
    }
    
    
    private static void update(){
        for (int i = 0; i < waitingList.size(); i++) {
            Process p = waitingList.get(i);
            //out.println(p);
            if (p.getStartTime() <= tick) {
                switch (p.getNextTask().getType()) {
                    case CORE:
                        out.println("CORE");
                        readyQueue.add(p);
                        break;
                    case DISK:
                        out.println("DISK");
                        diskQueue.add(p);
                        break;
                    case DISPLAY:
                    case INPUT:
                        out.println("INPUT");
                        displayList.add(p);
                        break;

                }
                waitingList.remove(p);
                workingTasks++;
            }
        }
        disk.update();
        //TODO currently is off by one for ticks. Uses entire slice rather than just needed part.
        for (Core c : coreArray) {
            if (c.update()) {
                if(c.getCurrentProcess() != null){
                    waitingList.add(c.getCurrentProcess());
                }
                if(!readyQueue.isEmpty()){
                    c.setCurrentProcess(readyQueue.pop());
                }
                out.println("time up");
            }
        }
        out.println(tick);
        
    }

    private static void readFile() throws FileNotFoundException{
        out.println("Number:");
        //byte selection = (new Scanner(System.in)).nextByte();
        Scanner fileIn;
        /*switch(selection){
            case 0:
                fileIn = new Scanner(new File("input10.txt"));
                break;
            case 1:
                fileIn = new Scanner(new File("input11.txt"));
                break;
            default:
                throw new FileNotFoundException("File number " + selection + " not found");
        }*/
        fileIn = new Scanner(new File("input10.txt"));
        disk = new Disk();
        while(fileIn.hasNext()){
            String s = fileIn.next();
            int n    = fileIn.nextInt();
            handleInput(s,n);
        }
    }
    
    private static void handleInput(String s, int n){
        out.println("new INPUT: " + s + " " + n);
        switch(s){
            case "NCORES":
                coreArray = new Core[n];
                break;
            case "SLICE":
                for(int i = 0; i < coreArray.length; i++){
                    coreArray[i] = new Core(n);
                }
                break;
            case "NEW":
                out.println("NEW" + n);
                Process p = new Process(n);
                waitingList.add(p);
                break;
            case "CORE":
                waitingList.get(waitingList.size()-1).addTask(new Task(TaskType.CORE,n));
                break;
            case "DISK":
                waitingList.get(waitingList.size()-1).addTask(new Task(TaskType.DISK, n));
                break;
            case "DISPLAY":
                waitingList.get(waitingList.size()-1).addTask(new Task(TaskType.DISPLAY, n));
                break;
            case "INPUT":
                waitingList.get(waitingList.size()-1).addTask(new Task(TaskType.DISPLAY, n));
                break;
        
        
        
        }
    }
    
}
