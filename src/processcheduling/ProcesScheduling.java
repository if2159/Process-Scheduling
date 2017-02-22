
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
    private static int workingTasks = 1;
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
        while(stillWorking()|| !waitingList.isEmpty()){
            update();
            
            
            
            tick++;
        }
        
    }
    
    private static boolean stillWorking(){
        for(Process p: processList){
            if(p.getNextTask() != null){
                return true;
            }
        }
        return false;
    }
    
    
    private static void update(){
        changeCurrentTasks();
        //Empty WaitList
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
                    i--;
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
        
        changeCurrentTasks();

        
        
        //DISK
        if(disk.isAvailable() && !diskQueue.isEmpty()){
            Process p = diskQueue.pop();
            out.println("Process: " + p.PID + "got DISK at: " + tick + "for " + p.getNextTask().getTimeLeft());
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
                i--;
            }
            
        }
        //CORE
        for (Core c : coreArray) {
            Process p = c.update();
            if(p != null){
                p.setLocation(ProcessLocation.WAITING);
                c.setCurrentProcess(null);
                out.println("CORE completion for process: " + p.PID + " at time: " + tick);
                waitingList.add(p);
                workingTasks--;
            }
        }
        
        
        if(tick == 4448){
            out.println("DEBUG");
        }
        
        //out.println(tick);
        
    }
    
    private static void changeCurrentTasks(){
        for(Core c: coreArray){
            if(c.isAvailable() && !readyQueue.isEmpty()){
                Process p = readyQueue.pop();
                out.println("Process " + p.PID + " got CORE at: " + tick);
                p.setLocation(ProcessLocation.CORE);
                c.setCurrentProcess(p);
            }
        }
        
    }
//TODO Something is causing a blocked state or the processes in a core are set to waiting state. Unsure of whats happening
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
        fileIn = new Scanner(new File("input11.txt"));
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
        out.println("\n\n");
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
        if(readyQueueEmpty){
            out.println("Empty");
        }
        
        out.println("DISK QUEUE:");
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
                switch(pr.getLocation()){
                    case CORE:
                        out.println("RUNNING");
                        break;
                    case DISK:
                        out.println("BLOCKED - DISK");
                        break;
                    case DISPLAY:
                        out.println("BLOCKED - DISPLAY");
                        break;
                    case DISK_QUEUE:
                    case READY_QUEUE:
                    case WAITING:
                        out.println("READY");
                    break;
                } 
           }
        }
        processList.remove(p);
        out.println("\n\n\n\n");
        
    }
    
}
