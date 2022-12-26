/*
@aouthor: neslisah celek
@id: 20190808063
 */
import java.io.File;
import java.util.*;

public class c20190808063 {
    public static void main(String[] args) throws Exception{
        File file = new File("samplejobs2.txt");
        List<String> lines = format(file);

        Queue<Process> waitingList= new LinkedList<>(); //process queue
        addProcess(waitingList, lines);

        List<Process> processList = new ArrayList<>(waitingList); //for using below calculateAverages function

        idleProcess idle = new idleProcess(0);
        FCFS(waitingList,lines, idle);

        calculateAverages(processList);
        System.out.println("IDLE process executed: " + idle.countIdle + " times.");
        System.out.println("HALT");
    }
    public static void FCFS(Queue<Process> waitingList, List<String> lines, idleProcess idle){
        int currentTime = 0;
        int round = 0;
        int control = 1; //for using in while loop

        while (control > 0){
            for (int i=0; i<lines.size(); i++){
                if (waitingList.size()==0)  //if all processes terminated
                    control--;

                Process currentProcess = waitingList.peek();
                if (currentProcess==null){ //avoid exception
                    break;
                }
                System.out.print("current time: " + currentTime);
                System.out.println(" round: " + round);

                System.out.println("current: " + currentProcess.name + "return: " + currentProcess.returnTime);

                if (round==0){ //first round, every process run simply, no need to control
                    currentTime += currentProcess.cpuBursts.get(0);
                    currentProcess.setReturnTime(currentTime+(currentProcess.IOBursts.get(0)));
                    currentProcess.cpuBursts.remove(0);  //first tuple is completed
                    currentProcess.IOBursts.remove(0);  //first tuple is completed
                    waitingList.poll();                   //remove the process from the queue
                    waitingList.add(currentProcess);      //now, the process is waiting for second round
                }
                else {
                    if (currentProcess.returnTime > currentTime) { //if process is not ready (waiting for io)
                        System.out.println(currentProcess.name + " waiting for io");
                        System.out.println("i: " +i);
                        if (i==lines.size()-1){
                            assert waitingList.peek() != null;  //avoid exception
                            //calculate early return time of all processes
                            int returnMin = waitingList.peek().returnTime;
                            System.out.println(waitingList.peek().name + " " +returnMin);
                            for (Process p:waitingList){
                                if (returnMin>p.returnTime){
                                    returnMin=p.returnTime;
                                    System.out.println(p.name + " " + p.returnTime);
                                }
                            }
                            System.out.println("idle is running for " + (returnMin - currentTime));
                            if ((returnMin - currentTime) != 0) //if current time equals returnMin, idle will not execute
                                idle.countIdle++;    //idle is executing until current time equals returnMin
                            currentTime = returnMin;
                            waitingList.poll(); //remove the process from the queue
                            waitingList.add(currentProcess); //now, the process is waiting for next round
                            round--;    //round is not increasing
                        }
                    }
                    else if (currentProcess.IOBursts.get(0)==-1){ //if process is about to terminate
                        System.out.println( currentProcess.name + " terminate");
                        currentTime += currentProcess.cpuBursts.get(0); //it is running for last time
                        currentProcess.turnaroundTime = currentTime; //turnaroundTime is equal to finish running
                        currentProcess.setReturnTime(0);  //it will not return
                        waitingList.poll(); //remove the process from the queue
                    }
                    else { //execute normally
                        System.out.println(currentProcess.name + " running");
                        currentTime += currentProcess.cpuBursts.get(0); //it is running
                        currentProcess.setReturnTime(currentTime+currentProcess.IOBursts.get(0)); //it will return after io operations
                        currentProcess.cpuBursts.remove(0); //tuple is completed
                        currentProcess.IOBursts.remove(0);
                        waitingList.poll(); //remove the process from the queue
                        waitingList.add(currentProcess); //now, the process is waiting for next round
                    }
                }
            }
            round++;
        }
    }
    public static void calculateAverages(List<Process> processList){
        double waitingSum=0; //sum of waiting times of all process
        double turnAroundSum=0; //sum of turnaround times of all process
        for (Process p: processList){
            waitingSum += p.countWaitingTime();
            turnAroundSum += p.turnaroundTime;
            System.out.println(p.name + " waitin: " + p.countWaitingTime() + " turn: " + p.turnaroundTime);
        }
        System.out.println(waitingSum);
        //averages of waiting times and turnaround times
        double avgWaiting = waitingSum / processList.size();
        double avgTurnaround = turnAroundSum / processList.size();

        System.out.println("Average turnaround time: " + avgTurnaround +
                            "\nAverage waiting time: " + avgWaiting);
    }
    public static void addProcess(Queue<Process> waitingList, List<String> lines){ //create Process instances from file lines
        for (int i=1; i<=lines.size(); i++){ //between 1 and line number(number of processes)
            ArrayList<Integer> cpuBursts= new ArrayList<>();
            ArrayList<Integer> IOBursts= new ArrayList<>();
            String processName = "process" +i;
            String[] parts = lines.get(i-1).split(" ");

            int pid=0;
            boolean first = true;

            for (int j=1; j<parts.length; j++){
                if (first){  //reading first number of line that is pid
                    pid=Integer.parseInt(parts[0]);
                    first=false;
                }
                cpuBursts.add(Integer.valueOf(parts[j])); //next part is cpu burst
                j++;
                IOBursts.add(Integer.valueOf(parts[j])); //next part is io burst
            }
            Process p = new Process(processName,pid,cpuBursts,IOBursts);
            waitingList.add(p);
        }
    }
    public static List<String> format(File file) throws Exception{ //take inputs from file, convert it to string with spaces
        Scanner input = new Scanner(file);
        List<String> lines = new ArrayList<>();

        while(input.hasNextLine()){
            String line= input.nextLine();
            line = line.replaceAll("[^\\d-]", " ");
            line=line.replaceAll(" {3}"," ");
            line=line.replaceAll(" {2}"," ");
            lines.add(line);
        }
        return lines;
    }
}
class Process{
    String name;
    int ID;
    List<Integer> cpuBursts;
    List<Integer> IOBursts;
    int returnTime; //return time after executing a tuple
    int waitingTime;
    int turnaroundTime;
    int burstSum=0; //sum of cpu bursts

    Process(String name, int PID, List<Integer> cpuBursts, List<Integer> IOBursts){
        this.name=name;
        this.cpuBursts=cpuBursts;
        this.IOBursts=IOBursts;
        this.ID=PID;
        for (int burst : cpuBursts){
            burstSum+=burst;
        }
    }
    Process(int PID){ //idle process
        ID=PID;
    }
    public int countWaitingTime(){ //turnaround time - cpu bursts
        waitingTime=turnaroundTime-burstSum;
        return waitingTime;
    }
    public void setReturnTime(int returnTime) {

        this.returnTime = returnTime;
    }
}
class idleProcess extends Process{
    int countIdle;
    idleProcess(int PID) {
        super(PID);
        countIdle=0;
    }
}