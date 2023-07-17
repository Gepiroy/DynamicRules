package MinigameToys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import DynamicRules.main;
import Utils.TextUtil;
import cmd.ArgReactor;
import cmd.CmdManager;

public class MGTManager {
	/*
	 * Depends on invsUtil, TextUtil, ItemUtil and cmd.
	 */
	private MGTManager(){}
	
	protected static List<MGTClass> classes = new ArrayList<>();
	
	public static final StageManager stages = new StageManager();
	
	
	public static void enable(){
		for(Player p:Bukkit.getOnlinePlayers()){
			MGTEvents.doLogin(p);
			MGTEvents.doJoin(p);
		}
		MemoryHandler.enable();
		for(MGTClass c:classes){
			c.init();
		}
		Bukkit.getPluginManager().registerEvents(new MGTEvents(), main.instance);
		
	}
	
	private static int rate=0;
	public static void tick(){//20/sec
		TimeHandler tTotal = new TimeHandler();
		for(MGTClass c:classes){
			TimeHandler t = new TimeHandler();
			c.tick(rate);
			int ticks=t.time(c.name+"&f's ticktime was &6", 50);
			c.ping+=0.01*(ticks-c.ping);
		}
		rate++;
		if(tTotal.time()>=50){
			TextUtil.sdebug("&etTotal &fwas &c"+tTotal.time+"&fms.");
		}
	}
	
	void regCmds(){//Добавляем к первой команде (основной команде игры) наши штучки.
		CmdManager.cmds.get(0).fillNext(new ArgReactor[]{new ArgReactor("ping", "Средний пинг модулей MGT за 100 тиков") {
			@Override
			public void react(Player p) {
				for(MGTClass c:classes){
					TextUtil.mes(p, TextUtil.plugin, c.name+"&f's ping is &6"+TextUtil.CylDouble(c.ping, "#0.000")+"&fms.");
				}
			}
		}.op()});
	}
	
	public static void gameStart(){
		Ready.gameStart();
	}
	
	public static void gameEnd(){
		
	}
}
