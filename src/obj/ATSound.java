package obj;

import org.bukkit.Sound;

public class ATSound {
	public final Sound s;
	public final float speed;
	public final int moment;
	
	public ATSound(Sound s, float speed, int moment){
		this.s=s;
		this.speed=speed;
		this.moment=moment;
	}
}
