
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
    private static LinkedList<Process> readyQueue;
    private static ArrayList<Process> waitingList;
    private static int tick = 0;
    private static int workingTasks = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        readyQueue = new LinkedList();
        waitingList = new ArrayList();

        try{
            readFile();
        }
        catch(FileNotFoundException e){
            out.println("File  Not Found.");
        }
        while(workingTasks > 0 && !waitingList.isEmpty()){
            for(Process p: waitingList){
                if(p.getStartTime() <= tick){
                    switch(p.nextTask().)
                    readyQueue.add(p);
                }
            }
            
            for(Core c: coreArray){
                if(c.update()){
                    c.setCurrentProcess(readyQueue.pop());
                }
            }
            
            
            
            tick++;
        }
        
    }

    private static void readFile() throws FileNotFoundException{
        out.println("Number:");
        byte selection = (new Scanner(System.in)).nextByte();
        Scanner fileIn;
        switch(selection){
            case 0:
                fileIn = new Scanner(new File("input10.txt"));
                break;
            case 1:
                fileIn = new Scanner(new File("input11.txt"));
                break;
            default:
                throw new FileNotFoundException("File number " + selection + " not found");
        }
        
        while(fileIn.hasNext()){
            String s = fileIn.next();
            int n    = fileIn.nextInt();
            handleInput(s,n);
        }
    }
    
    private static void handleInput(String s, int n){
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
