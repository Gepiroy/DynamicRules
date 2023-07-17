package Villages;

import org.bukkit.Location;

import obj.Conf;

public class VillageInfo {
	public final Location from, to;
	public boolean captured=false;
	
	public VillageInfo(Location from, Location to){
		this.from=from;
		this.to=to;
	}
	
	public VillageInfo(Conf conf, String st){
		this.from=conf.getLoc(st+".from");
		this.to=conf.getLoc(st+".to");
		captured=conf.conf.getBoolean(st+".captured");
	}
	
	public void save(Conf conf, String st){
		conf.setLoc(st+".from", from);
		conf.setLoc(st+".to", to);
		conf.set(st+".captured", captured);
	}
	
	public boolean isIn(Location loc){
		return (loc.getX()>from.getX()&&loc.getX()<to.getX()&&
				loc.getY()>from.getY()&&loc.getY()<to.getY()&&
				loc.getZ()>from.getZ()&&loc.getZ()<to.getZ());
	}
}
