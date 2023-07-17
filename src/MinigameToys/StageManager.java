package MinigameToys;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import DynamicRules.Events;
import Utils.GeomUtil;
import Utils.TextUtil;
import obj.PlayerInfo;

public class StageManager extends MGTClass{
	public StageManager(){
		super("&6Stage&7Manager");
	}
	
	@Override public void init(){
		Stages.Waiting.set();
	};
	
	public int timer = Stages.Waiting.timer;
	public MinigameStage stage = Stages.Waiting;
	
	private int minToStart=2;
	
	@Override
	public void tick(int rate){
		if(rate%20!=0)return;
		if(stage!=Stages.Waiting||Bukkit.getOnlinePlayers().size()>=minToStart)timer--;
		
		if(stage==Stages.Waiting)for(Player p:Bukkit.getOnlinePlayers())
			if(p.getLocation().distance(p.getWorld().getSpawnLocation())>20)
				GeomUtil.throwTo(p, p.getWorld().getSpawnLocation(), 0.5);
		
		if(timer<=5&&timer>0){
			TextUtil.globSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 2, 1);
		}else if(timer==0){
			TextUtil.globSound(Sound.BLOCK_DISPENSER_DISPENSE, 2, 1);
			timerZero();
		}else if(stage.skip())timerZero();
	}
	
	public void timerZero(){
		int index=0;
		for(MinigameStage s:Stages.list){
			index++;
			if(s==stage)break;
		}
		if(index==Stages.list.size())index=0;
		Stages.list.get(index).set();
		
		for(Player p:Bukkit.getOnlinePlayers()){
			PlayerInfo pi = Events.plist.get(p.getName());
			pi.board.resetScores();
		}
	}
}
