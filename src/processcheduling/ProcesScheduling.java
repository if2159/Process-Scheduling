
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
    private static ArrayList<Process> processList;
    private static Disk disk;
    private static LinkedList<Process> readyQueue;
    private static LinkedList<Process> diskQueue;
    private static ArrayList<Process>  displayList;
    private static ArrayList<Process> waitingList;
    private static int tick = 0;
    private static int workingTasks = 0;
    private static int currentPID = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        readyQueue = new LinkedList();
        diskQueue  = new LinkedList();
        waitingList = new ArrayList();
        displayList = new ArrayList();
        processList = new ArrayList();
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
        for (int i = 0; i < waitingList.size(); i++){
            Process p = waitingList.get(i);
            //out.println(p);
            if (p.getStartTime() <= tick) {
                if(p.getNextTask() != null){
                    //out.println("TICK: " + tick);
                    switch (p.getNextTask().getType()) {
                        case CORE:
                            //out.println("CORE PID:" + p.PID);
                            readyQueue.add(p);
                            break;
                        case DISK:
                            //out.println("DISK PID:" + p.PID);
                            diskQueue.add(p);
                            break;
                        case DISPLAY:
                        case INPUT:
                            //out.println("INPUT PID:" + p.PID);
                            out.println("BEGIN DISPLAY for PID: " + p.PID + "at TIME: " + tick);
                            p.setLocation(ProcessLocation.DISPLAY);
                            displayList.add(p);
                            break;

                    }
                    waitingList.remove(p);
                    workingTasks++;
                }
                else{
                    waitingList.remove(p);
                    terminateProcess(p);
                    workingTasks--;
                }
            }
        }
        //DISK
        if(disk.isAvailable() && !diskQueue.isEmpty()){
            Process p = diskQueue.pop();
            p.setLocation(ProcessLocation.DISK);
            disk.setCurrentProcess(p);
        }
        else{
            Process p = disk.update();
            if(p != null){
                p.setLocation(ProcessLocation.WAITING);
                out.println("DISK completion for process: " + p.PID + " at time: " + tick);
                waitingList.add(p);
                workingTasks--;
            }
        }
        
        //DISPLAY
        for(int i = 0; i < displayList.size(); i++){
            Process p = displayList.get(i);
            if(p.update()){
                out.println("DISPLAY completion for process: " + p.PID + " at time: " + tick);
                displayList.remove(p);
                waitingList.add(p);
                workingTasks--;
            }
        }
        //CORE
        for (Core c : coreArray) {
            Process p = c.update();
            if(p != null){
                p.setLocation(ProcessLocation.WAITING);
                out.println("CORE completion for process: " + p.PID + " at time: " + tick);
                waitingList.add(p);
                workingTasks--;
            }
        }
        
        changeCurrentTasks();
        
        //out.println(tick);
        
    }
    
    private static void changeCurrentTasks(){
        for(Core c: coreArray){
            if(c.isAvailable() && !readyQueue.isEmpty()){
                Process p = readyQueue.pop();
                p.setLocation(ProcessLocation.CORE);
                c.setCurrentProcess(p);
            }
        }
        
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
                Process p = new Process(n,currentPID++);
                processList.add(p);
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

    private static void terminateProcess(Process p) {
        out.println("CURRENT STATE OF THE SYSTEM AT t = " + tick + " ms:");
        int busyCores = 0;
        for(Core c: coreArray){
            if(!c.isAvailable()){
                busyCores++;
            }
        }
        out.println("Current number of busy cores:" +  busyCores );
        out.println("READY QUEUE:");
        boolean readyQueueEmpty = true;
        for(Process pr: readyQueue){
            readyQueueEmpty = false;
            out.println("Process: " + pr.PID);
        }
        out.println("DISK QUEUE:");
        if(readyQueueEmpty){
            out.println("Empty");
        }
        boolean diskQueueEmpty = true;
        for(Process pr: diskQueue){
            diskQueueEmpty = false;
            out.println("Process: " + pr.PID);
        }
        if(diskQueueEmpty){
            out.println("Empty");
        }
        out.println("PROCESS TABLE");
        for(Process pr: processList){
            out.print("Process " + pr.PID + " started at " + pr.getStartTime() + " ms and is ");
            if(pr.getNextTask() == null){
                out.println("TERMINATED");
            }
            else{
                out.println("RUNNING");
            }
        }
        out.println("\n\n\n\n");
        
    }
    
}
