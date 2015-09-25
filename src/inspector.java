import java.io.*;
import java.text.*;
import java.util.*;

public class inspector {

	public void inputLayout(String inputLayout) throws IOException {
		File fin = new File("cores.layout");
		BufferedReader br = new BufferedReader(new FileReader(fin));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#") || line.equals(""))
				continue;
			String[] parts = line.split(":");

			String layoutName = parts[0];
			String layoutStr = parts[1];

			if (!inputLayout.equals(layoutName)) {
				// System.out.println(inputLayout + " :: "+layoutName);
				continue;
			}

			parts = layoutStr.split(",");
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].substring(0, 1).equals("*")) {
					progs[i] = new Program(i, "*", 0, i, 0, 0, 0, 0.0, true);
					cores[i] = new Core(i, 0.0, progs[i], false);
				} else {
					progs[i] = new Program(i, parts[i], 0, i, 0, 0, 0, 0.0,
							false);
					cores[i] = new Core(i, 0.0, progs[i], true);
				}
				System.out.println(i);
			}
		}

		br.close();
	}

	public void initialize(String layoutName) throws IOException {

		progs = new Program[dimension];
		cores = new Core[dimension];

		inputLayout(layoutName);

		migrationLogger
				.add("time,swapNumber,ProgA,DestCoreA,ProgB,DestCoreB\n");
		//
		// for (int i = 0; i < dimension; i++) {
		// if ((i % 10 < 4) && (i < 20)) {
		// if (i < 9) {
		// progs[i] = new Program(i, "gups" + i, 0, i, 0, 0, 0.0,
		// false);
		// cores[i] = new Core(i, 0.0, progs[i], true);
		// } else if (i < 19) {
		// progs[i] = new Program(i, "gups" + (i - 6), 0, i, 0, 0,
		// 0.0, false);
		// cores[i] = new Core(i, 0.0, progs[i], true);
		// }
		// } else {
		// progs[i] = new Program(i, "*", 0, i, 0, 0, 0.0, true);
		// cores[i] = new Core(i, 0.0, progs[i], false);
		// }
		//
		// }

		// test
		// progs[0].currentCore = 0;
		// progs[1].currentCore = 10;
		// progs[2].currentCore = 11;
		// progs[3].currentCore = 1;
		// progs[4].currentCore = 2;
		// progs[5].currentCore = 12;

		// progs[0].currentCore = 0;
		// progs[1].currentCore = 1;
		// progs[2].currentCore = 2;
		// progs[3].currentCore = 10;
		// progs[4].currentCore = 11;
		// progs[5].currentCore = 12;
		// progs[6].currentCore = 21;
		// progs[7].currentCore = 22;
		// progs[8].currentCore = 23;
		// progs[9].currentCore = 30;
		// progs[10].currentCore = 31;
		// progs[11].currentCore = 32;
		//
		// cores[0] = new Core(0, 0.0, progs[0]);
		// cores[1] = new Core(1, 0.0, progs[1]);
		// cores[2] = new Core(2, 0.0, progs[2]);
		//
		// cores[10] = new Core(10, 0.0, progs[3]);
		// cores[11] = new Core(11, 0.0, progs[4]);
		// cores[12] = new Core(12, 0.0, progs[5]);
		//
		// cores[20] = new Core(20, 0.0, progs[6]);
		// cores[21] = new Core(21, 0.0, progs[7]);
		// cores[22] = new Core(22, 0.0, progs[8]);
		//
		// cores[30] = new Core(30, 0.0, progs[9]);
		// cores[31] = new Core(31, 0.0, progs[10]);
		// cores[32] = new Core(32, 0.0, progs[11]);
		//
		// for (int i = 3; i < dimension; i++) {
		// if (i < 6) {
		// cores[i] = new Core(i, 0.0, progs[i + 7]);
		// } else if (i < 9) {
		// cores[i] = new Core(i, 0.0, progs[i + 14]);
		// } else if (i < 12) {
		// cores[i] = new Core(i, 0.0, progs[i + 21]);
		// } else {
		// cores[i] = new Core(i, 0.0, progs[i]);
		// }
		// }

	}

	long valueExtractor(String line, String parameter) {
		long outp = -1;

		String[] parts = line.split("\\s+");
		if (parts[2].equals(parameter)) {
			outp = Long.parseLong(parts[1].replaceAll(",", ""));
			// System.out.println(parameter + " is " + outp);
		}

		return outp;
	}

	// public void executeCommand(int index, int pid, Process proc, int time)
	// throws IOException {
	//
	// String command =
	// "perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "
	// + pid + " sleep 0.8 2>&1 &";
	//
	// System.out.println("Attention Attention:::  PID is " + pid);
	// // perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p $pid0
	// // sleep 1 >$backingfile0 2>&1 &
	//
	// OutputStream stdin = proc.getOutputStream();
	// InputStream stderr = proc.getErrorStream();
	// InputStream stdout = proc.getInputStream();
	//
	// String line;
	// BufferedReader reader = new BufferedReader(
	// new InputStreamReader(stdout));
	// BufferedWriter writer = new BufferedWriter(
	// new OutputStreamWriter(stdin));
	//
	// String input = command;
	// if (input.trim().equals("exit")) {
	// // Putting 'exit' amongst the echo --EOF--s below doesn't work.
	// writer.write("exit\n");
	// } else {
	// writer.write("((" + input + ") && echo --EOF--) || echo --EOF--\n");
	// }
	// writer.flush();
	//
	// long cycle = -1, tempLoadMisses = 0, tempStoreMisses = 0;
	// // System.out.println("22222Attention Attention:::  PID is "+pid);
	//
	// line = reader.readLine();
	//
	// while (line != null && !line.trim().equals("--EOF--")) {
	// // System.out.println("Stdout: "+line);
	// System.out.println("22222Attention Attention:::  PID is " + pid);
	//
	// if (line.contains("cycles")) {
	// cycle = valueExtractor(line, "cycles");
	// System.out.println("Cycle of " + pid + " is " + cycle);
	//
	// }
	//
	// if (line.contains("LLC-load-misses")) {
	// tempLoadMisses = valueExtractor(line, "LLC-load-misses");
	// if (tempLoadMisses != -1)
	// progs[index].llc_load_misses += tempLoadMisses;
	// }
	//
	// if (line.contains("LLC-store-misses")) {
	// tempStoreMisses = valueExtractor(line, "LLC-store-misses");
	// if (tempStoreMisses != -2)
	// progs[index].llc_store_misses += tempStoreMisses;
	// }
	//
	// line = reader.readLine();
	// }
	//
	// if (cycle == -1) {
	// // System.err.println("Sth bad happened");
	// }
	//
	// if (cycle != -1) {
	// // multiplied by 1000 for more comfortable precision
	// cores[progs[index].currentCore].speed = ((double) (tempLoadMisses +
	// tempStoreMisses) / cycle) * 1000;
	// // System.out.println("Core" + index + " Speed is "
	// // + cores[progs[index].currentCore].speed);
	//
	// progs[index].bandwidth = ((double) (progs[index].llc_load_misses +
	// progs[index].llc_store_misses) / (cycle * time)) * 1000;
	// // System.out.println("Bandwidth" + progs[index].name + " is "
	// // + progs[index].bandwidth);
	// }
	// if (line == null) {
	// System.err.println("Null Line in perf");
	// // break;
	// }
	// }

	public boolean checkValid(String input) {
		String regex = "[0-9]+";
		return input.matches(regex);
	}

	public boolean processPerf(int index, String input, int time) {
		// System.out.println(input);
		long tempcycle = -1, tempLoadMisses = -1, tempStoreMisses = -1;
		int extractedPID = -1;
		// System.out.println("Attention Attention:::  PID is "+pid);

		if (input.contains("Problems")) { // means that this program has
											// finished running
			// System.out.println("Input " + input);
			// System.exit(0);
			return false;
		}

		String[] parts = input.split("\\s+");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equals("process")) {
				String extractedPIDStr = parts[i + 2].replaceAll("'", "");
				extractedPID = Integer.parseInt(extractedPIDStr.substring(0,
						extractedPIDStr.length() - 1));
			}
			if (parts[i].equals("cycles")) {
				if (checkValid(parts[i - 1].replaceAll(",", "")))
					tempcycle = Long
							.parseLong(parts[i - 1].replaceAll(",", ""));
			}

			if (parts[i].equals("LLC-load-misses")) {
				if (checkValid(parts[i - 1].replaceAll(",", "")))
					tempLoadMisses = Long.parseLong(parts[i - 1].replaceAll(
							",", ""));
			}
			if (parts[i].equals("LLC-store-misses")) {
				if (checkValid(parts[i - 1].replaceAll(",", "")))
					tempStoreMisses = Long.parseLong(parts[i - 1].replaceAll(
							",", ""));
			}

		}

		// System.out.println(extractedPID + "|" + cycle + "|" + tempLoadMisses
		// + "|" + tempStoreMisses);

		if (progs[index].pid != extractedPID)
			System.out.println("Sth terrible has happened! Call Superman");

		// TODO
		// Storing into tables
		progs[index].llc_load_misses += tempLoadMisses;
		progs[index].llc_store_misses += tempStoreMisses;
		progs[index].memory_accesses = tempLoadMisses + tempLoadMisses;

		if (tempcycle != -1) {
			cycle = tempcycle;
			// multiplied by 1000 for more comfortable precision -- OLD
			// calculation
			// cores[progs[index].currentCore].speed = ((double) (tempLoadMisses
			// + tempStoreMisses) / tempcycle) * 1000;
			// progs[index].bandwidth = ((double) (progs[index].llc_load_misses
			// + progs[index].llc_store_misses) / (tempcycle * time)) * 1000;

			cores[progs[index].currentCore].speed = (cores[progs[index].currentCore].speed
					* (time - 1) + ((double) (tempLoadMisses + tempStoreMisses) / tempcycle) * 1000)
					/ (time);
			// ((double) (progs[index].llc_load_misses +
			// progs[index].llc_store_misses) / (tempcycle * time)) * 1000;
			progs[index].bandwidth = ((double) (tempLoadMisses + tempStoreMisses) / tempcycle) * 1000;
		}

		return true;

	}

	public void coresSpeedCSVWriter(int time, FileWriter BW_writer)
			throws IOException {

		BW_writer.append(time + ",");
		for (int i = 0; i < dimension; i++) {
			BW_writer.append(cores[i].speed + ",");
		}
		BW_writer.append('\n');

	}

	public void bandwidthCSVWriter(int time, FileWriter writer, supervisor sp)
			throws IOException {

		writer.append(time + ",");
		for (int i = 0; i < dimension; i++) {
			writer.append(progs[i].bandwidth + ",");
		}
		writer.append(sp.getFairness() + "\n");
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

	}

	private boolean checkAllFinished() {
		boolean allFinished = true;

		for (int i = 0; i < dimension; i++) {
			if (!progs[i].finished) {
				allFinished = false;
				break;
			}
		}
		return allFinished;
	}

	public void printLayout() {
		System.out.println(" --------------- Progs Layout -------------------");
		for (int i = 0; i < dimension; i++) { //
			System.out.print(cores[i].prog.id + ":" + cores[i].prog.name
					+ "   ");
		}
		System.out.println();
	}

	// Global Variables
	public static Core[] cores;
	public static Program[] progs;
	// static int dimension = 40;
	static int dimension = 40;
	static int sampleSize = 0;
	static int Time = 6000;
	static long cycle = 0; // approximate value for number of cycles per second
	static int swapSize = 0; // number of migrations in each quanta
	static int schedFreq = 0;

	static boolean debugMode = false;
	static boolean schedulerActivated = true;
	
	
	static int adaptationOrientation = 0; //0: Fairness 1:Performance 
	//represents adaptation primary goal

	static ArrayList<String> migrationLogger = new ArrayList<String>();

	static int totalSwaps = 0;

	public static void main(String[] args) throws IOException,
			InterruptedException {

		double[] fairnessHistory = new double[20];
		int fairnessHistoryIndex = 0;

		int argsIndex = 0;
		swapSize = Integer.parseInt(args[argsIndex++]);
		schedFreq = Integer.parseInt(args[argsIndex++]);
		int tmpSchedAct = Integer.parseInt(args[argsIndex++]);
		schedulerActivated = (tmpSchedAct == 0) ? false : true;
		String layout = args[argsIndex++];

		inspector inspector = new inspector();
		supervisor sp = new supervisor();
		FileWriter coresspeedwriter = new FileWriter("CoresSpeed.csv");
		FileWriter progsbwwriter = new FileWriter("ProgsBW.csv");

		inspector.initialize(layout);

		// TODO Remove TEST
		inspector.printLayout();

		progsbwwriter.append(",");
		for (int i = 0; i < dimension; i++) {
			progsbwwriter.append(progs[i].name + ",");
		}
		progsbwwriter.append("fairness\n");

		int n = Integer.parseInt(args[argsIndex++]);
		sampleSize = n;
		for (int i = 0; i < dimension; i++) {
			if (!progs[i].finished) {
				progs[i].pid = Integer.parseInt(args[argsIndex]);
				argsIndex++;
				System.out.println(i + ":" + progs[i].name + ":" + progs[i].pid
						+ "  $  " + argsIndex);
			}
		}

		System.out.println("\n" + swapSize + " :: " + schedFreq + " :: "
				+ schedulerActivated);

		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date dateobj = new Date();
		System.out.println(df.format(dateobj));

		// String[] outpTable = new String[sampleSize];
		// String cmd = "";

		perf[] perfArray = new perf[dimension];
		for (int i = 0; i < dimension; i++) {
			perfArray[i] = new perf("thread" + i, progs[i].pid, schedFreq);
			if (!progs[i].finished) {
				perfArray[i].start();
			}
		}

		for (int t = 1; t < Time; t++) {
			System.out.print(" Time is " + t + " | ");
			dateobj = new Date();
			System.out.println(df.format(dateobj));

			Thread.sleep(schedFreq);
			boolean running = false;
			for (int i = 0; i < dimension; i++) {
				// System.out.println(perfArray[i].getValue());
				if (!progs[i].finished) {
					if (perfArray[i].getValue() != null) {
						running = inspector.processPerf(i,
								perfArray[i].getValue(), t);
						if (!running) {
							System.out.println("Program " + i
									+ " has finished!");
							perfArray[i].kill();
							progs[i].finished = true;
						}
					}
				}
				// all progs are finished
				if (inspector.checkAllFinished())
					break;
			}

			if (inspector.checkAllFinished())
				break;

			sp.schedule(t);

			System.out.print(" Time is " + t + " | ");

			fairnessHistory[t % 20] = sp.getFairness();

			if (true) {
				if ((t >= 20) && (t % 20 == 0)) {
					// if (true){
					sp.optimize(t, fairnessHistory);

					// now we have to update monitor threads
					for (int i = 0; i < dimension; i++) {
						perfArray[i].setschedFreq(schedFreq);
					}

				}
			}
			inspector.coresSpeedCSVWriter(t, coresspeedwriter);
			inspector.bandwidthCSVWriter(t, progsbwwriter, sp);

			if (t > 20000) {
				break;
			}

		}// end of main loop

		for (int i = 0; i < dimension; i++) {
			perfArray[i].kill();
		}

		// report
		// FileWriter coresspeedwriter = new FileWriter("CoresSpeed.csv");
		supervisor.reportAccuracy();
		inspector.reportMigrationLog();

		System.out.println("Total Number of swaps is: " + totalSwaps);

		inspector.printLayout();

		System.out
				.println(" --------------- Cores Observation -------------------");
		for (int i = 0; i < dimension; i++) {
			System.out.print(sp.coresObservation[i] + " ");
		}
		System.out.println();

		System.out
				.println(" --------------- Progs Observation -------------------");
		for (int i = 0; i < dimension; i++) {
			System.out.print(sp.progsObservation[i] + " ");
		}
		System.out.println();

		System.out.println(" --------------- Progs Name -------------------");
		for (int i = 0; i < dimension; i++) { //
			// System.out.print(progs[i].bandwidth + " ");
			System.out.print(progs[i].name + " ");
		}
		System.out.println();

		System.out
				.println(" --------------- Progs Bandwidth -------------------");
		for (int i = 0; i < dimension; i++) { //
			// System.out.print(progs[i].bandwidth + " ");
			System.out.format("%.4f  ", progs[i].bandwidth);
		}
		System.out.println();

		// Clean up coresspeedwriter.flush(); progsbwwriter.flush();

		coresspeedwriter.close();
		progsbwwriter.close();

	}

	private void reportMigrationLog() throws IOException {

		FileWriter migrationLoggerWriter = new FileWriter("MigrationLog.csv");
		for (String mig : migrationLogger) {
			migrationLoggerWriter.append(mig);
		}
		migrationLoggerWriter.close();

	}
}
