package MinigameToys;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;

import DynamicRules.Events;
import invsUtil.Invs;
import obj.PlayerInfo;

public class MGTEvents implements Listener{//Already activated in MGTManager.
	
	@EventHandler
	public void login(PlayerLoginEvent e){
		if(MGTManager.stages.stage!=Stages.Waiting&&!Events.plist.containsKey(e.getPlayer().getName())){
			e.disallow(Result.KICK_OTHER, "Игра уже началась.");
			return;
		}
		if(!doLogin(e.getPlayer()))e.disallow(Result.KICK_BANNED, "Проблемы при подготовке информации.");
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void join(PlayerJoinEvent e){
		doJoin(e.getPlayer());
	}
	
	public static boolean doLogin(Player p){
		try{
			PlayerInfo pi = Events.plist.get(p.getName());
			if(pi==null)pi = new PlayerInfo(p);
			pi.p=p;
			Events.plist.putIfAbsent(p.getName(), pi);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void doJoin(Player p){
		PlayerInfo pi = Events.plist.get(p.getName());
		p.setScoreboard(pi.board.getScoreboard());
		MGTManager.stages.stage.onJoin(p);
	}
	
	@EventHandler
	public void leave(PlayerQuitEvent e){
		Player p=e.getPlayer();
		doLeave(p);
	}
	public static void doLeave(Player p){
		//PlayerInfo pi = plist.get(p.getName());
		//pi.save();
		if(MGTManager.stages.stage==Stages.Waiting)Events.plist.remove(p.getName());
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent e){
		if(MGTManager.stages.stage==Stages.Waiting)e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)//executing first.
	public void interact(PlayerInteractEvent e){
		ItemStack hitem = e.getPlayer().getInventory().getItemInMainHand();
		PlayerInfo pi = Events.plist.get(e.getPlayer().getName());
		if(hitem.isSimilar(Ready.item)){
			e.setCancelled(true);
			Ready.change(pi);
		}
		if(hitem.isSimilar(TeamManager.item)){
			e.setCancelled(true);
			Invs.open(pi.p, TeamManager.inv);
		}
		
		if(MGTManager.stages.stage==Stages.Waiting||MGTManager.stages.stage==Stages.Prepare){
			e.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.LOWEST)//прежде всего
	public void hunger(FoodLevelChangeEvent e){
		if(MGTManager.stages.stage!=Stages.Game)e.setFoodLevel(20);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)//прежде всего
	public void hurt(EntityDamageEvent e){
		if(MGTManager.stages.stage!=Stages.Game)e.setCancelled(true);
	}
}
