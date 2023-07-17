package obj;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import DynamicRules.GlobalEventsManager;
import Utils.ItemUtil;
import Utils.TextUtil;

public abstract class GlobalEvent implements Listener{
	
	public final Material mat;
	public final String name;
	public final String id;
	public final List<String> lore;
	public final int time; //in seconds
	
	public int timer;
	
	public GlobalEvent(String id, String name, Material mat, Object lore, int time){
		GlobalEventsManager.list.add(this);
		this.id=id;
		this.name=name;
		this.mat=mat;
		this.lore=ItemUtil.lore(lore);
		this.time=time;
	}
	
	public void activate(){
		timer=time;
		onActivate();
		TextUtil.globMessage(TextUtil.plugin, "&bИвент: &7\""+name+"&7\"&f!");
		for(String st:lore){
			TextUtil.globMessage("  "+st);
		}
	}
	
	public abstract void onActivate();
	
	public void tick(int rate){
		onTick(rate);
		if(rate%20==0)if(timer--<=0)end();//Через && будет декреминировать каждый тик.
	}
	
	public abstract void onTick(int rate);
	
	public void end(){
		TextUtil.globMessage(TextUtil.plugin, "&bИвент &7\""+name+"&7\" &0окончен.");
		onEnd();
		GlobalEventsManager.remEvent(this);
	}
	
	public abstract void onEnd();
	
	public ItemStack displ(){
		return ItemUtil.create(mat, name, lore);
	}
}