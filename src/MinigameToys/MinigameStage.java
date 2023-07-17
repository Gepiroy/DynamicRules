package MinigameToys;

import org.bukkit.entity.Player;

import Utils.TextUtil;

public abstract class MinigameStage {
	
	public final int timer;
	public final String display;
	public boolean kick=false;
	
	public MinigameStage(int timer, String display){
		this.timer=timer;
		this.display = TextUtil.str(display);
		Stages.list.add(this);
	}
	
	public abstract void onSet();//calls when stage changed to this
	
	public abstract void onJoin(Player p);
	
	public abstract boolean skip();
	
	public MinigameStage kick(){
		kick=true;
		return this;
	}
	
	public void set(){
		MGTManager.stages.timer=timer;
		MGTManager.stages.stage=this;
		onSet();
	}
}
