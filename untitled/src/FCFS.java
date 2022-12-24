import java.util.ArrayList;
import java.util.List;

public class FCFS {
    // Represents a process with a name, an arrival time, and a list of bursts
    static class Process {
        String name;
        int arrivalTime;
        List<Integer> bursts;
        int waitingTime;
        int turnaroundTime;
        int completionTime;

        public Process(String name, int arrivalTime, List<Integer> bursts) {
            this.name = name;
            this.arrivalTime = arrivalTime;
            this.bursts = bursts;
        }
    }

    // Represents an idle process with a name and a burst time
    static class IdleProcess extends Process {
        public IdleProcess(int burstTime) {
            super("Idle", 0, List.of(burstTime));
        }
    }

    // Returns the average waiting time for a list of processes using the FCFS algorithm
    public static double getAverageWaitingTime(List<Process> processes) {
        // Sort the processes by arrival time
        //processes.sort((p1, p2) -> p1.arrivalTime - p2.arrivalTime);

        // Initialize the waiting time and turnaround time
        int waitingTime = 0;
        int turnaroundTime = 0;

        // Calculate the waiting time, turnaround time, and completion time for each process
        int currentTime = 0;
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            // Wait for the process to arrive if it has not yet arrived
            if (currentTime < p.arrivalTime) {
                // Insert an idle process to fill the gap
                Process idle = new IdleProcess(p.arrivalTime - currentTime);
                waitingTime += idle.bursts.get(0);
                currentTime = p.arrivalTime;
            }
            // Calculate the waiting time for the process
            p.waitingTime = currentTime - p.arrivalTime;
            // Calculate the turnaround time and completion time for the process
            p.turnaroundTime = p.waitingTime;
            p.completionTime = currentTime;
            // Execute the CPU and I/O bursts for the process
            for (int burst : p.bursts) {
                p.turnaroundTime += burst;
                p.completionTime += burst;
                currentTime += burst;
            }
            // Add the waiting time and turnaround time to the total
            waitingTime += p.waitingTime;
            turnaroundTime += p.turnaroundTime;
        }

        // Return the average waiting time
        return (double) waitingTime / processes.size();
    }

    public static void main(String[] args) {
        // Create a list of processes
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, List.of(3, 4, 2, 3, 2)));
        processes.add(new Process("P2", 0, List.of(2, 1, 4, 3, 2)));
        processes.add(new Process("P1", 0, List.of(3, 4, 2, 3, 2)));
        processes.add(new Process("P2", 0, List.of(2, 1, 4, 3, 2)));
    }
}
