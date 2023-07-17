package obj;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class HashTeam extends HashMap<Player, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void add(Player key, int am){
		add(key, am, false);
	}
	
	public void add(Player key, int am, boolean zero){
		int setam = 0;
		if(containsKey(key)){
			setam=super.get(key);
			setam+=am;
			super.replace(key, setam);
		}
		else{
			super.put(key, am);
			setam=am;
		}
		if(zero&&setam<=0)super.remove(key);
	}
	
	public void set(Player key, int am){
		if(containsKey(key))super.remove(key);
		if(am>0)super.put(key, am);
	}
	
	@Override
	public Integer get(Object key){
		if(containsKey(key))return super.get(key);
		else return 0;
	}
}
