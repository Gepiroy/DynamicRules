package MinigameToys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;

import DynamicRules.Events;
import obj.PlayerInfo;

public class Team {
	public final int id;
	private static int maxid=0;
	public static final int maxPlayers = 2;
	public final String name;
	public final Material mat = Material.GUNPOWDER;
	public List<String> bools = new ArrayList<>();
	
	//Выше - системные штуки.
	
	public int lives;
	
	public Team(){
		id=maxid++;
		name=ChatColor.YELLOW+""+(id+1);
		TeamManager.teams.add(this);
	}
	
	public List<PlayerInfo> getMembers(){
		List<PlayerInfo> ret = new ArrayList<>();
		for(PlayerInfo pi:Events.plist.values()){
			if(pi.team==this)ret.add(pi);
		}
		return ret;
	}
	
	public void toggleBool(String st){
		if(bools.contains(st))bools.remove(st);
		else bools.add(st);
	}
	public void setBool(String st){
		if(!bools.contains(st))bools.add(st);
	}
	
	public void mes(String prefix, String mes){
		for(PlayerInfo pli:getMembers()){
			pli.mes(prefix, mes);
		}
	}
	
	public void mes(String mes){
		for(PlayerInfo pli:getMembers()){
			pli.mes(mes);
		}
	}
	
	public boolean isEliminated(){
		if(lives>0)return false;
		for(PlayerInfo pi:getMembers())if(pi.p.getGameMode()!=GameMode.SPECTATOR){
			return false;
		}
		return true;
	}
}
