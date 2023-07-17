package obj;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import DynamicRules.main;
import Utils.TextUtil;

public class Conf {
	public File file;
	public FileConfiguration conf;
	public Conf(){}
	public Conf(String path){
		file=new File(path);
		conf=YamlConfiguration.loadConfiguration(file);
	}
	
	public void changeFile(String path){
		file=new File(path);
	}
	
	public void save(){
		try {
	        conf.save(file);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void saveAsync(){
		new BukkitRunnable(){
			@Override
			public void run() {
				try {
			        conf.save(file);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		}.runTaskAsynchronously(main.instance);
	}
	public void setLoc(String st, Location loc){
		conf.set(st+".world",loc.getWorld().getName());
		conf.set(st+".x",loc.getX());
		conf.set(st+".y",loc.getY());
		conf.set(st+".z",loc.getZ());
	}
	public Location getLoc(String st){
		if(!conf.contains(st)){
			Bukkit.getConsoleSender().sendMessage("No loc "+st+" in config!");
			return null;
		}
		return new Location(Bukkit.getWorld(conf.getString(st+".world")),conf.getDouble(st+".x"),conf.getDouble(st+".y"),conf.getDouble(st+".z"));
	}
	public void setLocs(String st, List<Location> locs){
		int i=0;
		for(Location l:locs){
			setLoc(st+"."+i, l);
			i++;
		}
	}
	public List<Location> getLocs(String st){
		List<Location> ret = new ArrayList<>();
		for(String s:getKeys(st)){
			ret.add(getLoc(st+"."+s));
		}
		return ret;
	}
	
	public List<String> getKeys(String section){
		List<String> ret=new ArrayList<>();
		if(!conf.contains(section))return ret;
		else{
			for(String st:conf.getConfigurationSection(section).getKeys(false)){
				ret.add(st);
			}
			return ret;
		}
	}
	public void add(String where, int am){
		int now=0;
		if(conf.contains(where))now=conf.getInt(where);
		conf.set(where, now+am);
	}
	public List<String> getStringList(String section){
		List<String> ret=conf.getStringList(section);
		if(ret==null)ret=new ArrayList<>();
		return ret;
	}
	public boolean StringEqual(String where, String equal){
		if(!conf.contains(where))return false;
		else if(conf.getString(where)==null)return false;
		else if(conf.getString(where).equals(equal))return true;
		else return false;
	}
	public void setHashMap(String where, HashMap<String, Integer> hm){
		for(String st:hm.keySet()){
			conf.set(where+"."+st, hm.get(st));
		}
	}
	public HashMap<String,Integer> getHashMap(String where){
		HashMap<String,Integer> ret = new HashMap<>();
		for(String st:getKeys(where)){
			ret.put(st, conf.getInt(where+"."+st));
		}
		return ret;
	}
	
	public int getInt(String where){
		if(conf.contains(where))return conf.getInt(where);
		else return 0;
	}
	public int getInt(String where, int def){
		if(conf.contains(where))return conf.getInt(where);
		else return def;
	}
	
	public String getString(String where){
		if(conf.contains(where))return conf.getString(where);
		else return null;
	}
	public String getString(String where, String def){
		if(conf.contains(where))return conf.getString(where);
		else return def;
	}
	
	public UUID getUUID(String where){
		if(conf.contains(where)){
			try{
				return UUID.fromString(conf.getString(where));
			}catch(IllegalArgumentException e){
				TextUtil.debug("&6WARN: &ein the &f"+file.getName()+" &eline &f"+where+" &econtains string that is not UUID!");
				return null;
			}
		}
		else return null;
	}
	
	public double getDouble(String where){
		return getDouble(where,0);
	}
	public double getDouble(String where, double def){
		if(conf.contains(where))return conf.getDouble(where);
		return def;
	}
	
	public void set(String where, Object o){
		conf.set(where, o);
	}
	public boolean contains(String what){
		return conf.contains(what);
	}
}
