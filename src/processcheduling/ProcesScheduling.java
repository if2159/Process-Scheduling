
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
            if(tick > 99 && tick %100 == 0){
                //outputState();
            }
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
    
    private static void emptyWaitList(){
        //Empty WaitList
        for (int i = 0; i < waitingList.size(); i++){
            Process p = waitingList.get(i);
            //out.println(p);
            if (p.getStartTime() <= tick) {
                if(p.getNextTask() != null){
                    //out.println("TICK: " + tick);
                    switch (p.getNextTask().getType()) {
                        case CORE:
                            out.println("Process " + p.PID + " added to READY Queue");
                            readyQueue.add(p);
                            break;
                        case DISK:
                            
                            out.println("Process " + p.PID + " added to DISK Queue");
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
    }
    
    
    private static void update(){
        changeCurrentTasks();
        emptyWaitList();
        changeCurrentTasks();
        updateDisk();
        updateDisplay();
        updateCore();
        
        
        
        emptyWaitList();
        changeCurrentTasks();
        //out.println(tick);
        
    }
    
    private static void updateCore(){
        changeCurrentTasks();
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
    }
    
    private static void updateDisplay(){
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
    }
    
    private static void updateDisk(){
        //DISK
        Process p = disk.update();
        if (p != null) {
            p.setLocation(ProcessLocation.WAITING);
            
            out.println("DISK completion for process: " + p.PID + " at time: " + tick);
            waitingList.add(p);
            workingTasks--;
        }
        

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
        
        if (disk.isAvailable() && !diskQueue.isEmpty()) {
            Process p = diskQueue.pop();
            
                out.println("Process: " + p.PID + "got DISK at: " + tick + "for " + p.getNextTask().getTimeLeft());
            p.setLocation(ProcessLocation.DISK);
            disk.setCurrentProcess(p);

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
                p.setLocation(ProcessLocation.WAITING);
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
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("CURRENT STATE OF THE SYSTEM AT t = " + tick + " ms:\n");
        int busyCores = 0;
        for(Core c: coreArray){
            if(!c.isAvailable()){
                busyCores++;
            }
        }
        sb.append("Current number of busy cores:" +  busyCores + "\n");
        sb.append("READY QUEUE:\n");
        boolean readyQueueEmpty = true;
        for(Process pr: readyQueue){
            readyQueueEmpty = false;
            sb.append("Process: " + pr.PID + "\n");
        }
        if(readyQueueEmpty){
            sb.append("Empty\n");
        }
        
        sb.append("DISK QUEUE:\n");
        boolean diskQueueEmpty = true;
        for(Process pr: diskQueue){
            diskQueueEmpty = false;
            sb.append("Process: " + pr.PID + "\n");
        }
        if(diskQueueEmpty){
            sb.append("Empty\n");
        }
        sb.append("PROCESS TABLE\n");
        for(Process pr: processList){
            sb.append("Process " + pr.PID + " started at " + pr.getStartTime() + " ms and is ");
            if(pr.getNextTask() == null){
                sb.append("TERMINATED\n");
            }
            else{
                switch(pr.getLocation()){
                    case CORE:
                        sb.append("RUNNING\n");
                        break;
                    case DISK:
                        sb.append("BLOCKED - DISK\n");
                        break;
                    case DISPLAY:
                        sb.append("BLOCKED - DISPLAY\n");
                        break;
                    case DISK_QUEUE:
                    case READY_QUEUE:
                    case WAITING:
                        sb.append("READY\n");
                    break;
                } 
           }
        }
        processList.remove(p);
        sb.append("\n\n\n\n");
        out.println(sb.toString());
        
    }
    
    private static void outputState(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("CURRENT STATE OF THE SYSTEM AT t = " + tick + " ms:\n");
        int busyCores = 0;
        for(Core c: coreArray){
            if(!c.isAvailable()){
                busyCores++;
            }
        }
        sb.append("Current number of busy cores:" +  busyCores + "\n");
        sb.append("READY QUEUE:\n");
        boolean readyQueueEmpty = true;
        for(Process pr: readyQueue){
            readyQueueEmpty = false;
            sb.append("Process: " + pr.PID + "\n");
        }
        if(readyQueueEmpty){
            sb.append("Empty\n");
        }
        
        sb.append("DISK QUEUE:\n");
        boolean diskQueueEmpty = true;
        for(Process pr: diskQueue){
            diskQueueEmpty = false;
            sb.append("Process: " + pr.PID + "\n");
        }
        if(diskQueueEmpty){
            sb.append("Empty\n");
        }
        sb.append("PROCESS TABLE\n");
        for(Process pr: processList){
            sb.append("Process " + pr.PID + " started at " + pr.getStartTime() + " ms and is ");
            if(pr.getNextTask() == null){
                sb.append("TERMINATED\n");
            }
            else{
                switch(pr.getLocation()){
                    case CORE:
                        sb.append("RUNNING\n");
                        break;
                    case DISK:
                        sb.append("BLOCKED - DISK\n");
                        break;
                    case DISPLAY:
                        sb.append("BLOCKED - DISPLAY\n");
                        break;
                    case DISK_QUEUE:
                    case READY_QUEUE:
                    case WAITING:
                        sb.append("READY\n");
                    break;
                }
           }
        }
        sb.append("\n\n\n\n");
        out.println(sb.toString());
    }
    
}
