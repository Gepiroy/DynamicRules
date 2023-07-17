package MinigameToys;

public abstract class MGTClass {
	public final String name;
	
	public double ping=0;
	
	public MGTClass(String name){
		this.name=name;
		MGTManager.classes.add(this);
	}
	
	public abstract void init();
	
	public abstract void tick(int rate);
	
	
}
