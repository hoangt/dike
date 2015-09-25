import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

class Statistics {

	double getMean(double[] data) {
		int size = data.length;
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / size;
	}

	double getVariance(double[] data) {

		int size = data.length;
		double mean = getMean(data);
		double temp = 0;
		for (double a : data)
			temp += (mean - a) * (mean - a);
		return temp / size;
	}

	double getStdDev(double[] data) {
		return Math.sqrt(getVariance(data));
	}

	public double median(double[] data) {
		Arrays.sort(data);

		if (data.length % 2 == 0) {
			return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
		} else {
			return data[data.length / 2];
		}
	}
}

public class supervisor extends inspector {
	int[] coresObservation;
	int[] progsObservation;
	boolean decision = false;
	int[] candidates;

	int[] candidatesfromLastMigration = new int[swapSize];

	static double[][] memAccPredictionTable;
	static double[][] memAccActualTable;
	static int[][] migrationHistory;

	ArrayList<Integer> swapSizeList;
	ArrayList<Integer> schedFreqList;
	int swapSizePointer;
	int schedFreqPointer;

	int benchmarkType = 0; // 1=UC, Unbalanced, more CPUint, 2=UM, Unbalanced,
							// more MemInt - 3=Balanced,

	public supervisor() {
		coresObservation = new int[dimension]; // 0:
		// Unknown,
		// SlowCore:1 , FastCore:2

		for (int i = 0; i < dimension; i++) {
			if ((i / 10) % 2 == 0)
				coresObservation[i] = 2;
			else
				coresObservation[i] = 1;
		}

		progsObservation = new int[dimension]; // 0:
		// Unknown, CpuIntensive:1 , MemoryIntensive:2

		memAccPredictionTable = new double[Time][dimension];
		memAccActualTable = new double[Time][dimension];
		migrationHistory = new int[Time][dimension];

		for (int i = 0; i < Time; i++) {
			for (int j = 0; j < dimension; j++) {
				memAccPredictionTable[i][j] = 0;
				memAccActualTable[i][j] = 1;
				migrationHistory[i][j] = -1;
			}
		}

		swapSizeList = new ArrayList<Integer>();
		schedFreqList = new ArrayList<Integer>();

		// for (int i = 2; i < sampleSize / 2; i = i + 2) {
		// swapSizeList.add(i);
		// }
		swapSizeList.add(2);
		swapSizeList.add(4);
		swapSizeList.add(6);
		swapSizeList.add(8);
		swapSizeList.add(10);
		swapSizeList.add(12);
		swapSizeList.add(14);
		swapSizeList.add(16);

		schedFreqList.add(1000);
		schedFreqList.add(500);
		schedFreqList.add(200);
		schedFreqList.add(100);

		// starting from <16,500>
		for (int i = 0; i < swapSizeList.size(); i++) {
			if (swapSize == swapSizeList.get(i)) {
				swapSizePointer = i;
				break;
			}
		}

		for (int i = 0; i < schedFreqList.size(); i++) {
			if (schedFreq == schedFreqList.get(i)) {
				schedFreqPointer = i;
				break;
			}
		}

		// swapSizePointer = swapSizeList.size() - 1;
		// schedFreqPointer = schedFreqList.size() - 3;

	}

	double coresSpeedAvgCalculator() {

		double sum = 0.0;
		for (int i = 0; i < dimension; i++) {
			sum += cores[progs[i].currentCore].speed;
		}

		double outp = sum / sampleSize;
		return outp;
	}

	void smartObserver(int time) {

		for (int i = 0; i < dimension; i++) {
			if (!progs[i].finished) {
				if (progs[i].name.startsWith("j")
						|| progs[i].name.startsWith("t")
						|| progs[i].name.startsWith("st")
						|| progs[i].name.startsWith("n")) {
					progsObservation[i] = 2;
				} else {
					progsObservation[i] = 1;
				}
			}
		}

		// for (int i = 0; i < dimension; i++) {
		// if (debugMode)
		// System.out.print(progs[i].name + " : " + progs[i].bandwidth
		// + "  |  ");
		// if (!progs[i].finished) {
		// if (progs[i].bandwidth > 5.00) {
		// progsObservation[i] = 2;
		// } else {
		// progsObservation[i] = 1;
		// }
		// }
		// }

		// Comparing to average
		// TODO Margin around average

		// double avgSpeed = coresSpeedAvgCalculator(); for (int i = 0; i <
		// sampleSize; i++) { if (cores[progs[i].currentCore].speed < avgSpeed)
		// coresObservation[progs[i].currentCore] = 1; else
		// coresObservation[progs[i].currentCore] = 2; }
		//

	}

	boolean checkPos(int progID) {
		if (progsObservation[progID] == coresObservation[progs[progID].currentCore]) {
			return true;
		}

		return false;
	}

	int checkAssymmetric(long[][] table) { // 0: all same type , 1 : 1M/3C , 2:
											// 3M/1C , 3: 2M/2C
		int cpuInt = 0;
		int memInt = 0;
		for (int i = dimension - sampleSize; i < dimension; i++) {
			if (progsObservation[(int) table[i][0]] == 1) {
				cpuInt++;
			} else if (progsObservation[(int) table[i][0]] == 2) {
				memInt++;
			}
		}

		int retVal = -1;

		if (cpuInt == 0 || memInt == 0) {
			retVal = 0;
		} else if (cpuInt != memInt) {
			if (cpuInt > memInt) {
				retVal = 1;
			} else {
				retVal = 2;
			}
		} else {
			retVal = 3;
		}

		// System.out.println("Assymetry is " + retVal);

		if (retVal == 0) {
			System.exit(0);
		}

		benchmarkType = retVal;

		return retVal;

	}

	public int[] selector(int time) {

		long[][] table = new long[dimension][2]; // firstCol= Prog.ID |

		for (int i = 0; i < dimension; i++) { // using snapshot selection rather
												// than accumulated sum over
												// time from beginning
			// table[i][1] = progs[i].llc_load_misses +
			// progs[i].llc_store_misses;
			table[i][1] = progs[i].memory_accesses;
			// (long) (cores[progs[i].currentCore].speed * cycle); OLD
			// calculation
			table[i][0] = progs[i].id;
		}

		Arrays.sort(table, new Comparator<long[]>() {
			public int compare(long[] a, long[] b) {
				return Long.compare(a[1], b[1]);
			}
		});

		if ((time >= 20) && (time % 20 == 1)) {
			candidatesfromLastMigration = new int[swapSize];
		}

		if (debugMode) { // TODO
			for (int i = 0; i < dimension; i++)
				if (table[i][1] != 0)
					System.out.print(table[i][0] + "  ");
			System.out.println();

			for (int i = 0; i < dimension; i++)
				if (table[i][1] != 0)
					System.out.print(table[i][1] + "  ");
			System.out.println();
		}

		// initialize all candidates to -1, means invalid
		for (int k = 0; k < swapSize; k++) {
			candidates[k] = -1;
		}

		int assym = checkAssymmetric(table);
		int headIndex = dimension - sampleSize;
		if (assym == 0 || assym == 1) { // all are same type
			for (int k = 0; k < (swapSize / 2); k++) {
				candidates[k] = (int) table[headIndex + k][0];
			}
		} else if (assym == 2) {
			int counter = 0;
			while ((counter < (swapSize / 2) && (headIndex < dimension
					- (sampleSize / 2)))) {
				// candidates[counter] = (int) table[headIndex][0];
				if ((checkPos(((int) table[headIndex][0])))
						&& (progsObservation[(int) table[headIndex][0]] == 1)) {
					// System.out.println(" Program " + (int)
					// table[headIndex][0]
					// + " does not need to move. Right cozy place");
				} else {
					candidates[counter] = (int) table[headIndex][0];
					counter++;
					// System.out.println("I'm fucking here");
				}

				headIndex++;

			}

		} else if (assym == 3) {

			int counter = 0;
			while ((counter < (swapSize / 2) && (headIndex < dimension
					- (sampleSize / 2)))) {
				// candidates[counter] = (int) table[headIndex][0];
				if (checkPos(((int) table[headIndex][0]))) {
					// System.out.println(" Program " + (int)
					// table[headIndex][0]
					// + " does not need to move. Right cozy place");
				} else {
					candidates[counter] = (int) table[headIndex][0];
					counter++;
					// System.out.println("I'm fucking here");
				}

				headIndex++;

			}
		}

		if (debugMode) { // TODO

			System.out.print("SLOW: Current Candidates: ");
			for (int z = 0; z < swapSize / 2; z++)
				System.out.print(candidates[z] + " | ");

			System.out.print("/n SLOW: From last qunta Candidates: ");
			for (int z = 0; z < swapSize / 2; z++)
				System.out.print(candidatesfromLastMigration[z] + " | ");
			System.out.println();

		}

		int tailIndex = dimension - 1;
		if (assym == 0 || assym == 2) {
			for (int k = 0; k < (swapSize / 2); k++) {
				candidates[swapSize - 1 - k] = (int) table[tailIndex - k][0];
			}

		} else if (assym == 1) {
			int counter = 0;
			while ((counter < (swapSize / 2) && (tailIndex >= dimension
					- (sampleSize / 2)))) {

				if ((checkPos(((int) table[tailIndex][0])))
						&& (progsObservation[(int) table[tailIndex][0]] == 2)) {
					// System.out.println(" Program " + (int)
					// table[tailIndex][0]
					// + " does not need to move. Right cozy place");
				} else {
					candidates[swapSize - 1 - counter] = (int) table[tailIndex][0];
					counter++;
				}

				tailIndex--;

			}
		} else if (assym == 2) {
			int counter = 0;
			while ((counter < (swapSize / 2) && (tailIndex >= dimension
					- (sampleSize / 2)))) {
				if (progsObservation[(int) table[tailIndex][0]] == 2) {
					candidates[swapSize - 1 - counter] = (int) table[tailIndex][0];
					counter++;
				}

				tailIndex--;
			}

		} else if (assym == 3 || assym == 1) {
			int counter = 0;
			while ((counter < (swapSize / 2) && (tailIndex >= dimension
					- (sampleSize / 2)))) { // TODO I removed /2 from here
				// candidates[swapSize - 1 - counter] = (int)
				// table[tailIndex][0];
				if (checkPos(((int) table[tailIndex][0]))) {
					// System.out.println(" Program " + (int)
					// table[tailIndex][0]
					// + " does not need to move. Right cozy place");
				} else {
					candidates[swapSize - 1 - counter] = (int) table[tailIndex][0];
					counter++;
				}

				tailIndex--;

			}

			/*
			 * int counter = 0; int candid1Core = progs[candidates[swapSize - 1
			 * - counter]].currentCore; while (counter < (swapSize / 2)) { if
			 * (progsObservation[candidates[swapSize - 1 - counter]] == 2) {
			 * while (coresObservation[candid1Core] == 2) { tailIndex--;
			 * candidates[swapSize - 1 - counter] = (int) table[tailIndex][0];
			 * candid1Core = progs[candidates[swapSize - 1 -
			 * counter]].currentCore; if (tailIndex == dimension - sampleSize) {
			 * 
			 * System.err.println(" Asshole Tail"); System.exit(0);
			 * 
			 * } }// end of while counter++; } else { candidates[swapSize - 1 -
			 * counter] = (int) table[tailIndex][0]; tailIndex--; counter++;
			 * 
			 * // System.out.println("Fastest program is NOT a mem int"); } }
			 */
		}
		if (debugMode) { // TODO
			System.out.print("FAST: Current Candidates: ");
			for (int z = swapSize - 1; z >= swapSize / 2; z--)
				System.out.print(candidates[z] + " | ");

			System.out.print("/n FAST: From last qunta Candidates: ");
			for (int z = swapSize - 1; z >= swapSize / 2; z--)
				System.out.print(candidatesfromLastMigration[z] + " | ");
			System.out.println();
		}

		// TODO
		/*
		 * // check about do not migrating same program consecutively for (int k
		 * = 0; k < swapSize; k++) { if (candidates[k] ==
		 * candidatesfromLastMigration[k]) { if (k < swapSize / 2) { if
		 * (candidates[k + 1] == (int) table[headIndex + 1][0]) { headIndex++; }
		 * candidates[k] = (int) table[headIndex + 1][0]; headIndex++; } else {
		 * if (candidates[k - 1] == (int) table[tailIndex - 1][0]) {
		 * tailIndex--; } candidates[k] = (int) table[tailIndex - 1][0];
		 * tailIndex--; } } }
		 */

		/*
		 * if (tailIndex <= headIndex) { for (int k = 0; k < swapSize; k++)
		 * candidates[k] = -1;
		 * 
		 * System.out.println(" pointers crossed each other "); return
		 * candidates; // no migrations }
		 */

		for (int k = 0; k < swapSize; k++) {
			if (candidates[k] != -1) {
				// System.out.println(progs[candidates[k]].name + " : Candid" +
				// k + " " + +candidates[k] + " on core " +
				// progs[candidates[k]].currentCore);
				if (progs[candidates[k]].name.equals("*")) {
					candidates[k] = -1;
				}
			}

			candidatesfromLastMigration[k] = candidates[k];
		}

		if (duplicates(candidates)) {
			System.err.println("vaveyllllaaaa");
			System.exit(0);
		}

		return candidates;
	}

	boolean duplicates(int[] zipcodelist) {
		Set<Integer> lump = new HashSet<Integer>();
		for (int i : zipcodelist) {
			if (i != -1) {
				if (lump.contains(i))
					return true;
				lump.add(i);
			}
		}
		return false;
	}

	boolean predictor(int slowProg, int fastProg, int index) {

		int coreWithSlowCandid = progs[slowProg].currentCore;
		int coreWithFastCandid = progs[fastProg].currentCore;

		double predictedSlowCandidWithMig = cores[coreWithFastCandid].speed
				* (cycle) / 1000;
		double predictedSlowCandidWithoutMig = progs[slowProg].memory_accesses;

		/*
		 * // TODO : OLD calculation double predictedSlowCandidWithMig =
		 * cores[coreWithFastCandid].speed (cycle) +
		 * (progs[slowProg].llc_load_misses + progs[slowProg].llc_store_misses);
		 * 
		 * double predictedSlowCandidWithoutMig =
		 * cores[coreWithSlowCandid].speed (cycle) +
		 * (progs[slowProg].llc_load_misses + progs[slowProg].llc_store_misses);
		 */
		// Let's assume that overhead is not important to us at all
		double slowMigrationOverhead = 0; // (MIGRATION_PENALTY *
											// (cores[coreWithFastCandid /
											// 8][coreWithFastCandid %
											// 8].speed));

		double slowcost = (-slowMigrationOverhead)
				+ (predictedSlowCandidWithMig - predictedSlowCandidWithoutMig);

		double predictedFastCandidWithMig = cores[coreWithSlowCandid].speed
				* (cycle) / 1000;
		double predictedFastCandidWithoutMig = progs[fastProg].memory_accesses;

		double fastcost = (-slowMigrationOverhead)
				+ (predictedFastCandidWithMig - predictedFastCandidWithoutMig);

		double cost = slowcost + fastcost;

		if (debugMode) {
			System.out.println("Prog Slow " + slowProg + " "
					+ progs[slowProg].name + "  W "
					+ predictedSlowCandidWithMig); // +" || "+
													// cores[coreWithFastCandid].speed
													// +" | "+(cycle));
			System.out.println("Prog Slow " + slowProg + " "
					+ progs[slowProg].name + " WO "
					+ predictedSlowCandidWithoutMig);
			System.out.println("Prog Fast " + fastProg + " "
					+ progs[fastProg].name + "  W "
					+ predictedFastCandidWithMig); // +" || "+
													// cores[coreWithFastCandid].speed
													// +" | "+(cycle));
			System.out.println("Prog Fast " + fastProg + " "
					+ progs[fastProg].name + " WO "
					+ predictedFastCandidWithoutMig);
			// System.out.println("Cost of migration is " + cost);

		}
		// System.err.println("migration cost of " + slowProg + " and " +
		// fastProg + " is:: " + cost);
		double predictedVal;

		memAccPredictionTable[index][slowProg] = predictedSlowCandidWithMig;
		memAccPredictionTable[index][fastProg] = predictedFastCandidWithMig;

		if (cost > 0) {

			predictedVal = (cores[coreWithFastCandid].speed * (cycle))
					+ (progs[slowProg].llc_load_misses + progs[slowProg].llc_store_misses);

			return true;
		} else {
			predictedVal = (cores[coreWithSlowCandid].speed * (cycle))
					+ (progs[slowProg].llc_load_misses + progs[slowProg].llc_store_misses);

			return false;
		}

	}

	boolean decider(int slowProg, int fastProg, boolean prediction, int index) {

		if (!prediction)
			return false;

		if (progsObservation[slowProg] == progsObservation[fastProg]) {
			// System.err.println(" We are going to switch threads of same type");
			return true;
			// System.exit(0);
		}

		// Rule 0 : do migration if it's between mem and cpu int
		if ((progsObservation[slowProg] == 1)
				&& (progsObservation[fastProg] == 2))
			return true;

		// Rule 1: never put a cpu int into fast core, unless both candidates
		// are cpu int
		if (progsObservation[slowProg] == 1)
			if (coresObservation[progs[fastProg].currentCore] == 2)
				if (progsObservation[fastProg] != 1)
					return false;

		// TODO add more rules
		// Rule 2:

		return true;

	}

	private void migrator(int candid0, int candid1) throws IOException {
		String cmd1 = "taskset -pc " + progs[candid1].currentCore + " "
				+ progs[candid0].pid;

		String cmd2 = "taskset -pc " + progs[candid0].currentCore + " "
				+ progs[candid1].pid;

		Process p1 = Runtime.getRuntime().exec(
				new String[] { "/bin/bash", "-c", cmd1 });

		Process p2 = Runtime.getRuntime().exec(
				new String[] { "/bin/bash", "-c", cmd2 });

		int swapTmp = progs[candid1].currentCore;
		progs[candid1].currentCore = progs[candid0].currentCore;
		progs[candid0].currentCore = swapTmp;

		Program swapTmp2 = cores[progs[candid1].currentCore].prog;
		cores[progs[candid1].currentCore].prog = cores[progs[candid0].currentCore].prog;
		cores[progs[candid0].currentCore].prog = swapTmp2;

		// System.out.println("Total Number of swaps: " + totalSwaps);
		totalSwaps++;
		// printLayout();
	}

	public void schedule(int index) throws IOException {
		// updating logger tables
		for (int j = 0; j < dimension; j++) {
			memAccActualTable[index][j] = progs[j].memory_accesses;
		}

		candidates = new int[swapSize];
		smartObserver(index);

		candidates = selector(index);

		for (int k = 0; k < (swapSize / 2); k++) {
			int candidA = candidates[k];
			int candidB = candidates[swapSize - k - 1];

			if (candidA != -1 && candidB != -1) { // it means one of the
													// candidates are invalid
				boolean prediction = predictor(candidA, candidB, index);

				if (index < 10) {
					prediction = true;
				}

				decision = decider(candidA, candidB, prediction, index);
				if (decision) {
					if (schedulerActivated) {

						migrationHistory[index][candidA] = progs[candidB].currentCore;
						migrationHistory[index][candidB] = progs[candidA].currentCore;

						migrator(candidA, candidB);

						migrationLogger.add(index + "," + totalSwaps + ","
								+ progs[candidA].name + ","
								+ progs[candidA].currentCore + ","
								+ progs[candidB].name + ","
								+ progs[candidB].currentCore + ",\n");

						// System.out.println("QUANTA " + index +
						// " Migration between prog " + progs[candidA].name +
						// " and " + progs[candidB].name);
					}

				} else {
					// System.out.println("QUANTA " + index +
					// " NO Migration happened between " + progs[candidA].name +
					// " and " + progs[candidB].name);
				}
			}
		}
	}

	public void optimize(int time, double[] fairnessHistory) {
		// System.out.println(time+" : fairness::  "+getFairness());

		// TODO primary goal, fairness
		double avgFairness = new Statistics().getMean(fairnessHistory);

		if (avgFairness < 0.01) { // system is fair , TODO switch it to a user
									// input threshold
			return;
		}

		if (adaptationOrientation == 0) { //fairness is primary goal
			if (benchmarkType == 3) { // B
				schedFreqPointer = Math.min(++schedFreqPointer,
						schedFreqList.size() - 1); // decreasing schedFreq
			} else if (benchmarkType == 1) { // UC
				swapSizePointer = Math.min(++swapSizePointer,
						swapSizeList.size() - 1); // increasing swapSize

				schedFreqPointer = Math.min(++schedFreqPointer,
						schedFreqList.size() - 2); // decreasing schedFreq to
													// 200

			} else if (benchmarkType == 2) { // UM
				swapSizePointer = Math.min(++swapSizePointer,
						swapSizeList.size() - 1); // increasing swapSize

				schedFreqPointer = Math.min(++schedFreqPointer,
						schedFreqList.size() - 3); // decreasing schedFreq to
													// 500

			}
		} else if (adaptationOrientation == 1) { //performance is primary goal
			if (benchmarkType == 3) { // B
				
				schedFreqPointer = Math.max(--schedFreqPointer,
						0); // increasing schedFreq
				
			} else if (benchmarkType == 1) { // UC
				swapSizePointer = Math.min(++swapSizePointer,
						swapSizeList.size() - 1); // increasing swapSize

				schedFreqPointer = Math.max(--schedFreqPointer,
						0); // increasing schedFreq to 1000

			} else if (benchmarkType == 2) { // UM
				
				schedFreqPointer = Math.max(--schedFreqPointer,
						0); // increasing schedFreq to 1000

			}
		}
		// if (avgFairness > 0.6) { // gotta go full speed
		//
		// schedFreqPointer = schedFreqList.size() - 1; // =100, as fast as
		//
		// swapSize = swapSizeList.size() - 1; // =16, maximum number of swaps
		//
		// } else if (avgFairness < 0.20) { // we're fair now, so remove
		// Overhead,
		// // increase schedFreq schedFreq =
		// // 500;
		// schedFreqPointer = Math.max(--schedFreqPointer, 0);
		//
		// if (schedFreqPointer == 0) { // let's change swapSize to lessen
		// // overhead
		// swapSizePointer = Math.max(--swapSizePointer, 0);
		// }
		// } else {
		// // fairness in danger, increase swapSize
		// swapSizePointer = Math.min(++swapSizePointer,
		// swapSizeList.size() - 1);
		//
		// schedFreqPointer = Math.max(--schedFreqPointer, 1);
		// }

		// System.out.println(swapSizePointer+
		// " &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7 "+schedFreqPointer);

		schedFreq = schedFreqList.get(schedFreqPointer);
		swapSize = swapSizeList.get(swapSizePointer);

		System.out.print(benchmarkType + "  AvgFairness is " + avgFairness);
		System.out.print("     new schedFreq is " + schedFreq);
		System.out.println("     new swapSize is " + swapSize);

	}

	// schedFreq = 200;

	public double getFairness() {
		double fairness = -1;

		Statistics stat = new Statistics();
		HashSet<String> hash = new HashSet<>();

		for (int i = 0; i < dimension; i++) {
			String app = progs[i].name.substring(0, progs[i].name.length() - 1);
			if (!app.equals("")) {
				hash.add(app);
			}
		}

		int index = 0; // for copies of each application , max value 8
		int counter = 0; // for each benchmark , max value 4

		double[] data = new double[sampleSize / hash.size()];
		double[] stdevs = new double[hash.size()];

		for (String value : hash) {

			for (int i = 0; i < dimension; i++) {
				if (progs[i].name.startsWith(value)) {
					data[index++] = progs[i].bandwidth;
				}
			}
			index = 0;
			stdevs[counter++] = stat.getStdDev(data) / stat.getMean(data);
			// System.out.println(value + " ::: " + stdevs[counter - 1]);
		}

		fairness = stat.getMean(stdevs);
		// System.out.println(" Fairness " + fairness);

		if (fairness == -1) {
			System.err.println("Sth terrible has happened, call Batman");
			System.exit(0);
		}

		return fairness;
	}

	public static void reportAccuracy() throws IOException {
		double[][] accuracyTable = new double[Time][dimension];

		FileWriter predictionWriter = new FileWriter("Prediction.csv");
		for (int i = 0; i < Time; i++) {
			predictionWriter.append(i + ",");
			for (int j = 0; j < dimension; j++) {
				predictionWriter.append(memAccPredictionTable[i][j] + ",");
			}
			predictionWriter.append('\n');
		}

		predictionWriter.close();

		for (int i = 1; i < Time; i++) {
			for (int j = 0; j < dimension; j++) {
				if (migrationHistory[i][j] != -1) {
					accuracyTable[i][j] = (memAccActualTable[i][j] - memAccPredictionTable[i][j])
							/ memAccActualTable[i][j];
				} else {
					accuracyTable[i][j] = (memAccActualTable[i][j] - memAccActualTable[i - 1][j])
							/ memAccActualTable[i][j];
					// accuracyTable[i][j] = -1;
				}
			}
		}

		FileWriter accuracyWriter = new FileWriter("Accuracy.csv");
		for (int i = 0; i < Time; i++) {
			accuracyWriter.append(i + ",");
			for (int j = 0; j < dimension; j++) {
				accuracyWriter.append(accuracyTable[i][j] + ",");
			}
			accuracyWriter.append('\n');
		}

		accuracyWriter.close();
	}

}
