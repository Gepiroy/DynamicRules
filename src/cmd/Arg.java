package cmd;

public class Arg {
	public String display = "Ник";
	public ArgType type = ArgType.Player;
	public boolean isImportant = false;
	
	public Arg(String display, ArgType type){
		this.display=display;
		this.type=type;
	}
	
	public Arg important(){
		isImportant=true;
		return this;
	}
	
	@Override
	public String toString(){
		return display;
	}
}
