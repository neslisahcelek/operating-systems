
import java.io.File;
import java.util.*;

public class c20190808063 {
    public static void main(String[] args) throws Exception{
        File file = new File("samplejobs.txt");
        Queue<Process> waitingList= new LinkedList<Process>(); //process list
        List<String> lines = format(file);

        addProcess(waitingList, lines);
        Process idle = new Process("process0",0);
        FCFS(waitingList,lines);

        System.out.println("HALT");
    }
    public static void FCFS(Queue<Process> waitingList, List<String> lines){
        int countIdle=0; //# of times idle process was executed
        int currentTime = 0;
        int round = 0;

        for (int j=1; j<10; j++){
            for (int i=0; i<lines.size(); i++){
                Process currentProcess = waitingList.peek();
                if (currentProcess==null){
                    break;
                }
                System.out.print("current time: " + currentTime);
                System.out.println(" round: " + round);

                System.out.println("current: " + currentProcess.name + "return: " + currentProcess.returnTime);

                if (round==0){
                    currentTime += currentProcess.cpuBursts.get(0);
                    currentProcess.setReturnTime(currentTime+(currentProcess.IOBursts.get(0)));
                    currentProcess.cpuBursts.remove(0);
                    currentProcess.IOBursts.remove(0);
                    waitingList.poll();
                    waitingList.add(currentProcess);
                }
                else {
                    if (currentProcess.returnTime > currentTime) { //if process is not ready (waiting for io)
                        System.out.println(currentProcess.name + " waiting for io");
                        //Queue<Process> waitingNow = new LinkedList<>();
                        //waitingNow.add(currentProcess);
                        System.out.println("i: " +i);
                        if (i==lines.size()-1){
                            assert waitingList.peek() != null;
                            int returnMin = waitingList.peek().returnTime;
                            System.out.println(waitingList.peek().name + " " +returnMin);
                            for (Process p:waitingList){
                                if (returnMin>p.returnTime){
                                    returnMin=p.returnTime;
                                    System.out.println(p.name + " " + p.returnTime);
                                }
                            }
                            System.out.println("idle is running for " + (returnMin - currentTime));
                            countIdle++;
                            currentTime = returnMin;
                            waitingList.poll();
                            waitingList.add(currentProcess);
                            round--; //round aynı kaldığı için 1.nin ki sıkıntı, çalıştıktan sonra listeyi boşaltabiliriz
                        }
                    }
                    else if (currentProcess.IOBursts.get(0)==-1){ //terminate
                        currentTime += currentProcess.cpuBursts.get(0);
                        currentProcess.setReturnTime(0);
                        waitingList.poll();
                        System.out.println( currentProcess.name + " terminate");
                    }
                    else {
                        System.out.println(currentProcess.name + " running");
                        currentTime += currentProcess.cpuBursts.get(0);
                        currentProcess.setReturnTime(currentTime+currentProcess.IOBursts.get(0));
                        currentProcess.cpuBursts.remove(0);
                        currentProcess.IOBursts.remove(0);
                        waitingList.poll();
                        waitingList.add(currentProcess);
                    }
                }
            }
            round++;
        }
        //System.out.print(currentProcess.name + " ");
        //System.out.println(waitingList.peek().name);
    }
    public static void addProcess(Queue<Process> waitingList, List<String> lines){
        for (int i=1; i<=lines.size(); i++){ //between 1 and line number
            ArrayList<Integer> cpuBursts= new ArrayList<>();
            ArrayList<Integer> IOBursts= new ArrayList<>();

            String processName = "process" +i;
            String[] parts = lines.get(i-1).split(" ");
            int pid=0;
            boolean first = true;

            for (int j=1; j<parts.length; j++){
                if (first==true){  //we are reading first number of line that is pid
                    pid=Integer.valueOf(parts[0]);
                    first=false;
                }
                cpuBursts.add(Integer.valueOf(parts[j]));
                j++;
                IOBursts.add(Integer.valueOf(parts[j]));
            }
            Process p = new Process(processName,pid,cpuBursts,IOBursts);
            waitingList.add(p);
        }
    }
    public static List<String> format(File file) throws Exception{
        Scanner input = new Scanner(file);
        List<String> lines = new ArrayList<>();

        while(input.hasNextLine()){
            String line= input.nextLine();
            line = line.replaceAll("[^\\d-]", " ");
            line=line.replaceAll("   "," ");
            line=line.replaceAll("  "," ");
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
    int returnTime;
    int nextBurst=0;

    Process(String name, int PID, List<Integer> cpuBursts, List<Integer> IOBursts){
        this.name=name;
        this.cpuBursts=cpuBursts;
        this.IOBursts=IOBursts;
        this.ID=PID;
    }
    Process(String name, int PID){
        name=name;
        ID=PID;
    }

    public void setReturnTime(int returnTime) {
        this.returnTime = returnTime;
    }
}