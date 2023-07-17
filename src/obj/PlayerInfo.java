package obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import DynamicRules.main;
import MinigameToys.Team;
import Utils.TextUtil;

public class PlayerInfo {
	static Random r = main.r;
	
	public Waits timers=new Waits();
	public Waits fastTimers=new Waits();
	public ArrayList<String> bools=new ArrayList<>();
	public HashMap<String,Long> realTimers = new HashMap<>();
    public Waits waits = new Waits();
    public SWaits swaits = new SWaits();
    public String pname;
    public List<String> cmd = new ArrayList<>();
    public Player p;
    public Block lastClickedBlock;
    public Team team;
    
    public Board board;
    
    public boolean successfullyLoaded=false;

	public Location deathLoc;
    
    public PlayerInfo(Player p){
    	init(p);
    }
	/*public PlayerInfo(FileConfiguration conf, String p){//never used in minigame.
		init(p);
		if(conf.contains("timers"))for(String st:conf.getConfigurationSection("timers").getKeys(false)){
			timers.put(st, conf.getInt("timers."+st));
		}
		if(conf.contains("fastTimers"))for(String st:conf.getConfigurationSection("fastTimers").getKeys(false)){
			fastTimers.put(st, conf.getInt("fastTimers."+st));
		}
		if(conf.contains("waits"))for(String st:conf.getConfigurationSection("waits").getKeys(false)){
			waits.put(st, conf.getInt("waits."+st));
		}
		if(conf.contains("bools"))for(String st:conf.getConfigurationSection("bools").getKeys(false)){
			bools.add(st);
		}
		if(conf.contains("realTimers"))for(String st:conf.getConfigurationSection("realTimers").getKeys(false)){
			realTimers.put(st,conf.getLong("realTimers."+st));
		}
	}*/
	
	void init(Player p){
		this.p=p;
		pname=p.getName();
		board = new Board(TextUtil.plugin, this);
	}
	
	public void reset(){
		timers.clear();
		waits.clear();
		bools.clear();
		realTimers.clear();
		lastClickedBlock=null;
	}
	
	public void save(){//never used in minigames.
		Conf conf = new Conf(main.instance.getDataFolder()+"/players/"+pname+".yml");
		conf.set("timers",null);
		for(String st:timers.keySet()){
			conf.set("timers."+st,timers.get(st));
		}
		conf.set("fastTimers",null);
		for(String st:fastTimers.keySet()){
			conf.set("fastTimers."+st,fastTimers.get(st));
		}
		conf.set("waits",null);
		for(String st:waits.keySet()){
			conf.set("waits."+st,waits.get(st));
		}
		conf.set("bools",bools);
		conf.set("realTimers",null);
		for(String st:realTimers.keySet()){
			conf.set("realTimers."+st,realTimers.get(st));
		}
		conf.save();
	}
    public void setbool(String bool, boolean set){
    	if(set)
    		if(!bools.contains(bool))bools.add(bool);
    	else if(bools.contains(bool))bools.remove(bool);
    }
    public void changeBool(String bool){
    	setbool(bool, !bools.contains(bool));
    }
    
    ////////////////////////////////////////////////////////////////////
    
    public void mes(String pref, String mes){
    	TextUtil.mes(p, pref, mes);
    }
    public void mes(String mes){
    	TextUtil.mes(p, mes);
    }
    public void actionBar(String text){
    	TextUtil.actionBar(p, text);
    }
    public void title(String title, String sub, int spawn, int hold, int rem){
    	TextUtil.title(p, title, sub, spawn, hold, rem);
    }
    public void sound(Sound sound, float volume, float pitch){
    	p.playSound(p.getLocation(), sound, volume, pitch);
    }
    public void sound(Sound sound, float volume, float from, float to){
    	p.playSound(p.getLocation(), sound, volume, r.nextFloat()*(to-from)+from);
    }
    public void soundOut(Sound sound, float volume, float pitch){
    	p.getWorld().playSound(p.getLocation(), sound, volume, pitch);
    }
    public void soundOut(Sound sound, float volume, float from, float to){
    	p.getWorld().playSound(p.getLocation(), sound, volume, r.nextFloat()*(to-from)+from);
    }
    public void particle(Location l, Particle part, int am, double spread, double speed){
    	p.spawnParticle(part, l, am, spread, spread, spread, speed);
    }
    public void particle(Location l, Particle part, int am, double sx, double sy, double sz, double speed){
    	p.spawnParticle(part, l, am, sx, sy, sz, speed);
    }
    public double dist(Location to){
    	return p.getLocation().distance(to);
    }
    public double dist(int dy, Location to){
    	return p.getLocation().add(0, dy, 0).distance(to);
    }
    public ItemStack hitem(){
    	return p.getInventory().getItemInMainHand();
    }
    public void potion(PotionEffectType type, int lvl, int dur){
    	p.addPotionEffect(new PotionEffect(type, dur, lvl, false, false, false));
    }
}
