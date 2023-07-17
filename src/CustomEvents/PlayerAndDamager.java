package CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAndDamager {
	public Player p = null, damager = null;
	
	public PlayerAndDamager(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			p = (Player) e.getEntity();
		}
		if(e.getDamager() instanceof Projectile){
			Projectile pr = (Projectile) e.getDamager();
			if(pr.getShooter() instanceof Player){
				damager = (Player) pr.getShooter();
			}
		}else if(e.getDamager() instanceof TNTPrimed){
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			if(tnt.getSource() instanceof Player){
				damager = (Player) tnt.getSource();
			}
		}else if(e.getDamager() instanceof Player){
			damager = (Player) e.getDamager();
		}
	}
	
}
