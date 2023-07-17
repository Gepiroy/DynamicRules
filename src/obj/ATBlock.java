package obj;

public class ATBlock {
	public final String title, sub;
	public final int spawn, hold, rem, length;
	public final ATSound[] sounds;
	
	public ATBlock(String title, String sub, int spawn, int hold, int rem, int length, ATSound[] sounds){
		this.title=title;
		this.sub=sub;
		this.spawn=spawn;
		this.hold=hold;
		this.rem=rem;
		this.length=length;
		this.sounds=sounds;
	}
}
