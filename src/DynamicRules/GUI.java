package DynamicRules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import invsUtil.Inv;
import invsUtil.InvEvents;
import invsUtil.Invs;

public class GUI implements Listener{
	@EventHandler
	public void click(InventoryClickEvent e){
		if(e.getClickedInventory() != null) {
			for(Inv inv:InvEvents.invs){
				if(inv.title(e.getView().getTitle())){
					Invs.event(e);
					return;
				}
			}
		}
	}
}
