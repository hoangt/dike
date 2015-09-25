public class Program {
	int id;
	String name;
	int pid;
	int currentCore;
	long llc_load_misses; //holds all load misses from beginning
	long llc_store_misses; //holds all store misses from beginning
	long memory_accesses;   //stores LLC misses of last quanta
	double bandwidth;
	boolean finished;

	public Program(int id, String name, int pid, int currentCore,
			long llc_load_misses, long llc_store_misses, long memory_accesses,
			double bandwidth, boolean finished) {
		super();
		this.id = id;
		this.name = name;
		this.pid = pid;
		this.currentCore = currentCore;
		this.llc_load_misses = llc_load_misses;
		this.llc_store_misses = llc_store_misses;
		this.memory_accesses = memory_accesses;
		this.bandwidth = bandwidth;
		this.finished = finished;
	}

}
