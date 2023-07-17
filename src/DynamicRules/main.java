package DynamicRules;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import CustomEvents.CECore;
import MinigameToys.MGTEvents;
import MinigameToys.MGTManager;
import MinigameToys.Stages;
import Utils.TextUtil;
import Villages.VillageManager;
import cmd.CmdManager;
import obj.PlayerInfo;

public class main extends JavaPlugin{
	public static Random r = new Random();
	public static main instance;
	public static World world;
	//CmdManager cm;
	
	public void onEnable(){
		instance = this;
		world = Bukkit.getWorld("world");
		CmdManager.init();
		//RuleManager.enable();
		VillageManager.enable();
		MGTManager.enable();
		//Conf conf = new Conf(getDataFolder()+"/conf.yml");
		
		Bukkit.getPluginManager().registerEvents(new CECore(), this);
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getPluginManager().registerEvents(new VillageManager(), this);
		Bukkit.getPluginManager().registerEvents(new GUI(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			int secRate=0;
			public void run(){
				secRate++;
				RuleManager.tick();
				GlobalEventsManager.tick();
				MGTManager.tick();
				if(secRate>=20){
					secRate=0;
					for(Player p:Bukkit.getOnlinePlayers()){
						PlayerInfo pi=Events.plist.get(p.getName());
						for(String st:new ArrayList<>(pi.timers.keySet())){
							if(pi.timers.addAndRemZero(st, -1)){
								if(st.equals("resp")){
									if(pi.swaits.get("respType").equals("1")){
										p.teleport(pi.deathLoc);
										Events.resped(pi);
									}else{
										Player to = Bukkit.getPlayer(pi.swaits.get("respType"));
										if(to==null){
											pi.mes("Игрок не найден.");
											pi.team.lives++;
											Events.setDeath(pi);
										}
									}
								}
							}
						}
						if(pi.bools.contains("dead")){
							//if(p.getLocation().distance(pi.deathLoc)>1)p.teleport(pi.deathLoc);
							if(p.getEyeLocation().getBlock().getType().isSolid())pi.potion(PotionEffectType.BLINDNESS, 1, 60);
							
							if(pi.timers.containsKey("resp")){
								pi.title("&6Возрождение...", "&e"+pi.timers.get("resp"), 5, 10, 10);
							}
						}
						pi.board.setScore("&b"+MGTManager.stages.stage.display, 10);
						if(MGTManager.stages.stage==Stages.Game){
							if(pi.team==null)pi.board.setScore("&c&lКоманда не определена!!!", 3);
							else{
								pi.board.setScore("&6Команда: "+pi.team.name, 3);
								pi.board.setScore("&6Жизней: &2"+pi.team.lives, 2);
							}
						}
						pi.board.setScore("Таймер: &b"+TextUtil.toTime(MGTManager.stages.timer), 0);
						
					}
				}
				if(secRate%3==0){
					world.setTime(world.getTime()+2);
				}
			}
		}, 1, 1);
	}
	public void onDisable(){
		//Conf conf = new Conf(getDataFolder()+"/conf.yml");
		//conf.save();
		for(Player p:Bukkit.getOnlinePlayers()){
			MGTEvents.doLeave(p);
		}
		//RuleManager.disable();
		VillageManager.disable();
	}
}
