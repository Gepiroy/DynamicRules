package invsUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import DynamicRules.Events;
import Utils.TextUtil;
import obj.PlayerInfo;

public abstract class Inv implements Cloneable{
	public Player p;
	public PlayerInfo pi;
	public final String name;
	public boolean locked = true;
	
	public Inv(String name){
		this.name=ChatColor.translateAlternateColorCodes('&', name);
		InvEvents.invs.add(this);
		TextUtil.debug("added new Inv: &b"+name+" &f(&a"+InvEvents.invs.size()+"&f)");
	}
	
	public Inv unlocked(){
		locked=false;
		return this;
	}
	
	public void prepare(Player p){
		this.p=p;
		pi=Events.plist.get(p.getName());
		//sinf=pi.invS;
		//TextUtil.debug("IE prepared: sinf="+sinf);
	}
	
	public abstract void displItems(Inventory inv);
	
	public abstract void click(InventoryClickEvent e);
	
	public Inv clone(){
		try {
			return (Inv) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean title(String title){
		if(title==null)return false;
		return name.equals(title);
	}
	
	public void open(Player p){
		Inventory inv=Bukkit.createInventory(null, 27, name);
		Inv ie2=this.clone();
		ie2.prepare(p);
		ie2.displItems(inv);
		p.openInventory(inv);
	}
	
	public void click(Player p, InventoryClickEvent e){
		Inv ie2=this.clone();
		ie2.prepare(p);
		ie2.click(e);
	}
}
