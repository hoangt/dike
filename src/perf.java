import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class perf extends Thread {
	// class that runs perf with threads
	private Thread t;
	private String threadName;
	private String value;
	private int pid;
	private int freq;
	private volatile boolean isRunning = true;

	perf(String name, int pid, int freq) {
		threadName = name;
		this.pid=pid;
		this.freq=freq;
		//System.out.println("Creating " + threadName);
	}

	public String runCommand(String command) throws IOException,
			InterruptedException {

		StringBuffer output = new StringBuffer();

		// Process p;
		// p = Runtime.getRuntime().exec(command);

		Process p = Runtime.getRuntime().exec(
				new String[] { "/bin/bash", "-c", command });
		// p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String line = "";
		int counter = 0;
		while ((counter < 10)) {
			if ((line = reader.readLine()) != null) {
				// System.out.println("RunCommand: " + line);
				output.append(line + "\n");
			}
			counter++;
			// System.out.println("RunCommand: " + line);

		}

		return output.toString();

		// cmd =
		// "perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "
		// + progs[i].pid + " sleep 1 2>&1 &";
		// outpTable[i] = inspector.runCommand(cmd);

	}

	public void run() {
		//System.out.println("MyThread running");
		//System.out.println("Thread: " + threadName);

		while (isRunning) {
			//String cmd = "taskset -c 39 perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "
					//+ pid + " sleep "+((double) freq / 1000)+" 2>&1 ";
			String cmd = "perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "
					+ pid + " sleep "+((double) freq / 1000)+" 2>&1 ";
			try {
				value=runCommand(cmd);
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//value = testCounter + " Thread: " + threadName;
			// Let the thread sleep for a while.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void kill() {
		isRunning = false;
	}

	
	public void setschedFreq(int newschedFreq) {
		this.freq=newschedFreq;
	}
	

	public void setPID(int newPID) {
		this.pid=newPID;
	}
	
	
	public String getValue() {
		return value;
	}

}
