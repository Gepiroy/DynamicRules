package invsUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Invs {
	
	private Invs(){}
	
	public static void open(Player p, Inv Inv){
		Inv.open(p);
	}
	
	public static boolean event(InventoryClickEvent e){
		for(Inv inv:InvEvents.invs){
			if(inv.title(e.getView().getTitle())){
				if(inv.locked)e.setCancelled(true);
				if(e.getClickedInventory() != null && (!inv.locked || e.getCurrentItem()!=null && e.getClickedInventory()==e.getView().getTopInventory())) {
					inv.click((Player) e.getWhoClicked(), e);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean Interaction(InventoryClickEvent e){
		if(e.getClickedInventory()==e.getView().getTopInventory()){
			if(e.getCurrentItem()!=null){
				return event(e);
			}
		}
		return false;
	}
}
