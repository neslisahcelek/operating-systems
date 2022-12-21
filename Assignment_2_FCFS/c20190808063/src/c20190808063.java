import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class c20190808063 {
    public static void main(String[] args) throws Exception{
        File file = new File("samplejobs.txt");
        Queue<Process> waitingList= new LinkedList<Process>(); //process list

        Scanner input = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while(input.hasNextLine()){
            String line= input.nextLine();
            line = line.replaceAll("[^\\d-]", " ");
            line=line.replaceAll("   "," ");
            line=line.replaceAll("  "," ");
            lines.add(line);
        }

        for (int i=1; i<=lines.size(); i++){ //between 1 and line number
            ArrayList<Integer> cpuBursts= new ArrayList<>();
            ArrayList<Integer> IOBursts= new ArrayList<>();

            String processName = "process" +i;
            String[] parts = lines.get(i-1).split(" ");
            String pid="";
            boolean first = true;

            for (int j=1; j<parts.length; j++){
                if (first==true){  //we are reading first number of line that is pid
                    pid=parts[0];
                    first=false;
                }
                cpuBursts.add(Integer.valueOf(parts[j]));
                j++;
                IOBursts.add(Integer.valueOf(parts[j]));
            }
            Process p = new Process(processName,pid,cpuBursts,IOBursts);
            waitingList.add(p);
        }
        System.out.println(waitingList.poll().IOBursts.get(2));
        System.out.println(waitingList.poll().IOBursts.get(2));
        System.out.println(waitingList.poll().name);
    }
    public void SCFS(Queue<Process> waitingList){
        int currentTime = 0;
        Process currentProcess = waitingList.poll();
        currentTime += currentProcess.cpuBursts.get(0);
    }
}
class Process{
    String name;
    String ID;
    List<Integer> cpuBursts;
    List<Integer> IOBursts;

    Process(String name, String PID, List<Integer> cpuBursts, List<Integer> IOBursts){
        this.name=name;
        this.cpuBursts=cpuBursts;
        this.IOBursts=IOBursts;
        this.ID=PID;

    }
}