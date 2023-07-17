package CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
 
public class PreDeathEvent extends Event implements Cancellable {
	
	private EntityDamageEvent e;
	private boolean isCancelled;
	private static final HandlerList handlers = new HandlerList();
	
	public PreDeathEvent(EntityDamageEvent e){
		this.e=e;
	}
	
	public LivingEntity getEntity(){
		return (LivingEntity) e.getEntity();
	}
	
	public EntityDamageEvent getLastDamage(){
		return e;
	}
	
	@Override
	public boolean isCancelled() {
	    return this.isCancelled;
	}
	
	@Override
	public void setCancelled(boolean arg) {
		this.isCancelled=arg;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}