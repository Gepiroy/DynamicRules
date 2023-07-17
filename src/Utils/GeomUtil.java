package Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class GeomUtil {
	
	private GeomUtil(){}
	
	public static Vector direction(Location from, Location to){
		return to.toVector().subtract(from.toVector()).normalize();
	}
	
	public static void throwTo(Entity en, Location to, double power){
	    en.setVelocity(direction(en.getLocation(), to).multiply(power));
	}
	
	/**
	 * Shoots a ray from eyes. Ignores any blocks, so, we need to check it.
	 * @param p
	 * @param dist
	 * @param ray
	 * @param nothing
	 * @return
	 */
	public static Player shootRay(Player p, double dist, ParticleDot ray, ParticleDot nothing){
		double step=0.5;
		Vector vec = p.getEyeLocation().toVector();
		Vector v1 = p.getEyeLocation().getDirection().multiply(step);
		double l=step;
		vec.add(v1);
		int i=0;
		for (; l < dist; vec.add(v1)) {
			i++;
	        if(i>=3){
	        	ray.spawn(vec.toLocation(p.getWorld()));
	        	i=0;
	        }
	        l += step;
        	for(Entity en:p.getWorld().getNearbyEntities(vec.toLocation(p.getWorld()), 0.2, 0.2, 0.2)){
        		if(en instanceof Player){
        			Player pl = (Player) en;
        			if(pl==p)continue;
    	        	return pl;
    			}
        	}
	    }
		nothing.spawn(vec.toLocation(p.getWorld()));
		return null;
	}
    
    public static void lineBetweenTwoPoints(Location p1, Location p2, Particle part, float step, double ds){
    	for (Location l:lineBetweenTwoPoints(p1, p2, step)) {
	    	p1.getWorld().spawnParticle(part, l, 1, ds, ds, ds, 0);
	    }
	}
    public static void lineBetweenTwoPoints(Player p, Location p1, Location p2, Particle part, float step, double ds){
	    for (Location l:lineBetweenTwoPoints(p1, p2, step)) {
	    	p.spawnParticle(part, l, 1, ds, ds, ds, 0);
	    }
	}
    
    public static List<Location> lineBetweenTwoPoints(Location p1, Location p2, float step){
    	List<Location> ret = new ArrayList<>();
    	double distance = p1.distance(p2);
	    Vector v1 = p1.toVector();
	    Vector v2 = p2.toVector();
	    Vector vector = v2.clone().subtract(v1).normalize().multiply(step);
	    float length = 0;
	    for (; length < distance; v1.add(vector)) {
	    	ret.add(v1.toLocation(p1.getWorld()));
	        length += step;
	    }
	    return ret;
    }
    /**
     * Get blocks between points that is not AIR. Helpful when we need to create sg like a gun.
     */
    public static List<Block> blocksBetweenTwoPoints(Location p1, Location p2){
    	Location loc = p1.clone();
    	loc.setDirection(direction(p1, p2));
        BlockIterator blocksToAdd = new BlockIterator(loc, 0d, (int)p1.distance(p2));
        List<Block> ret = new ArrayList<>();
        while(blocksToAdd.hasNext()) {
            Block b = blocksToAdd.next();
            if(b.getType()!=Material.AIR)ret.add(b);
        }
        return ret;
	}
    /**
     * Get block in the front of block that player looking at.
     * @param dist - max distance.
     */
    public static Location LookingPreBlock(Player p, float dist){
		Location loc=p.getEyeLocation();
		Vector vec = loc.toVector();
		Vector v1 = loc.getDirection().normalize().multiply(0.1);
		float walked=0;
		for(;walked<dist;walked+=0.1f){
			vec.add(v1);
			Material mat=vec.toLocation(p.getWorld()).getBlock().getType();
			if(!mat.equals(Material.AIR)){
				vec.subtract(v1);
				return vec.toLocation(p.getWorld());
			}
		}
		return vec.toLocation(p.getWorld());
	}
    
    public static ParticleDot redDotDrawer(int r, int g, int b, float size){
    	return new ParticleDot() {
			DustOptions opt=new DustOptions(Color.fromRGB(r, g, b), size);
			@Override public void spawn(Location l) {
				l.getWorld().spawnParticle(Particle.REDSTONE, l, 0, 0, 0, 0, opt);
			}
		};
	}
    
    public static ParticleDot redDotDrawer(Player p, int r, int g, int b, float size){
    	return new ParticleDot() {
			DustOptions opt=new DustOptions(Color.fromRGB(r, g, b), size);
			@Override public void spawn(Location l) {
				pl.spawnParticle(Particle.REDSTONE, l, 0, 0, 0, 0, opt);
			}
		}.pl(p);
	}
    
    public static ParticleDot simpleDotDrawer(Particle p, float ds){
    	return new ParticleDot() {
			@Override public void spawn(Location l) {
				l.getWorld().spawnParticle(p, l, 1, ds, ds, ds, 0);
			}
		};
	}
    
    public static ParticleDot simpleDotDrawer(Player pl, Particle p, float ds){
    	return new ParticleDot() {
			@Override public void spawn(Location l) {
				pl.spawnParticle(p, l, 1, ds, ds, ds, 0);
			}
		}.pl(pl);
	}
    
    public static void drawRect(Location loc, int dx, int dz, float oneStep, ParticleDot dot){
		Vector v=loc.toVector();
		for(int i=0;i<dx;i++){
			v.add(new Vector(oneStep, 0, 0));
			dot.spawn(v.toLocation(loc.getWorld()));
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, oneStep));
			dot.spawn(v.toLocation(loc.getWorld()));
		}
		for(int i=0;i<dx;i++){
			v.add(new Vector(-oneStep, 0, 0));
			dot.spawn(v.toLocation(loc.getWorld()));
		}
		for(int i=0;i<dz;i++){
			v.add(new Vector(0, 0, -oneStep));
			dot.spawn(v.toLocation(loc.getWorld()));
		}
	}
    
    public void drawCircle(Location loc, double r, ParticleDot dot){
		Location l = loc.clone();
		l.subtract(r, 0, r/2);
		for(double t = 0; t < Math.PI*2; t+=1){
			double x = r * Math.sin(t);
			double z = r * Math.cos(t);
			l.add(x,0,z);
			dot.spawn(l);
		}
	}
}
