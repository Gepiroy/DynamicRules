package CustomEvents;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CECore implements Listener{
	//Подключать через тупо рег ивентов. Не забывай, блэн!
	
	@EventHandler
	public void dmg(EntityDamageEvent e){
		if(e.getEntity() instanceof LivingEntity){
			LivingEntity en = (LivingEntity) e.getEntity();
			if(en.getHealth()-e.getFinalDamage()<=0){
				PreDeathEvent pde = new PreDeathEvent(e);
				Bukkit.getPluginManager().callEvent(pde);
				e.setCancelled(pde.isCancelled());
			}
		}
	}
}
