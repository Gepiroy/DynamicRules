package Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ParticleDot {
	
	public Player pl;
	
	public ParticleDot pl(Player pl){
		this.pl=pl;
		return this;
	}
	
	public abstract void spawn(Location l);
}
