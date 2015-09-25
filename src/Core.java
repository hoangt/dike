
public class Core {
	int id;
	double speed;
	Program prog;
	boolean active; //means that this core is running
	
	public Core(int id, double speed, Program prog, boolean active) {
		super();
		this.id = id;
		this.speed = speed;
		this.prog = prog;
		this.active=active;
	}
	
}
