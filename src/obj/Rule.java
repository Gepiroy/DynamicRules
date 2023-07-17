package obj;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import DynamicRules.RuleManager;
import Utils.ItemUtil;

public abstract class Rule implements Listener{
	
	public final Material mat;
	public final String name;
	public final String id;
	public final List<String> lore;
	
	
	public Rule(String id, String name, Material mat, Object lore){
		RuleManager.list.add(this);
		this.id=id;
		this.name=name;
		this.mat=mat;
		this.lore=ItemUtil.lore(lore);
	}
	
	public abstract void onActivate();
	
	public abstract void tick(int rate);
	
	public ItemStack displ(){
		return ItemUtil.create(mat, name, lore);
	}
}
