package DynamicRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import CustomEvents.PlayerAndDamager;
import MinigameToys.GameCondition;
import MinigameToys.GameConditions;
import MinigameToys.MGTManager;
import MinigameToys.Stages;
import MinigameToys.Team;
import Utils.TextUtil;
import invsUtil.Inv;
import obj.GlobalEvent;
import obj.PlayerInfo;

public class GlobalEventsManager {
	
	static final Random r = main.r;
	
	public static List<GlobalEvent> list = new ArrayList<>();
	
	public static List<GlobalEvent> active = new ArrayList<>();
	
	public static void addEvent(GlobalEvent e){
		active.add(e);
		e.activate();
		Bukkit.getPluginManager().registerEvents(e, main.instance);
	}
	
	public static void remEvent(GlobalEvent e){
		active.remove(e);
		HandlerList.unregisterAll(e);
	}
	
	public static final GlobalEvent stealLife = new GlobalEvent("stealLife", "Жизнекрад", Material.GOLDEN_SWORD, new String[]{"Убийство игрока другой команды даёт вашей команде жизнь.","PvP включено везде до первой смерти."}, 600) {
		
		@Override public void onActivate() {
			GameConditions.cond(this, GameCondition.PvP_overworld);
		}
		
		@Override public void onTick(int rate) {
			
		}
		
		@Override public void onEnd() {
			GameConditions.decond(this);
		}
		
		@EventHandler
		public void death(PlayerDeathEvent e){
			PlayerInfo pi = Events.plist.get(e.getEntity().getName());
			EntityDamageEvent ede = pi.p.getLastDamageCause();
			if(ede instanceof EntityDamageByEntityEvent){
				PlayerAndDamager pad = new PlayerAndDamager((EntityDamageByEntityEvent) ede);
				if(pad.p!=null&&pad.damager!=null){
					Team t = Events.plist.get(pad.damager.getName()).team;
					TextUtil.globMessage(name, "Команда "+t.name+" &fолучает дополнительную жизнь!");
					t.lives++;
					end();
				}
			}
		}
	};
	
	public static final GlobalEvent fastFood = new GlobalEvent("fastFood", "Фастфуд", Material.GOLDEN_SWORD, new String[]{"Кто первый съест определённую еду, получит доп жизнь команде."}, 600) {
		
		List<Material> needToEat;
		HashMap<Material, String> foodDictionary = new HashMap<>();
		
		
		private List<Material> getFoods() {
		    List<Material> list = new ArrayList<>();
		    for (Material mat : Material.values()) {
		        if (mat.isEdible()) {
		            list.add(mat);
		        }
		    }
		    return list;
		}
		
		@Override public void onActivate() {
			foodDictionary.put(Material.BEETROOT, "Свекла");
			foodDictionary.put(Material.BEETROOT_SOUP, "Свекольный суп");
			foodDictionary.put(Material.CARROT, "Морковь");
			foodDictionary.put(Material.RABBIT_STEW, "Рагу из кролика");
			foodDictionary.put(Material.ROTTEN_FLESH, "Гнилая плоть");
			foodDictionary.put(Material.MELON_SLICE, "Кусок арбуза");
			foodDictionary.put(Material.PUMPKIN_PIE, "Тыквенный пирог");
			
			List<Material> foods = getFoods();
			TextUtil.globMessage(name, "Еда, которую нужно съесть:");
			needToEat = new ArrayList<>();
			for(int i=0;i<2;i++){
				Material mat = foods.get(r.nextInt(foods.size()));
				needToEat.add(mat);
				TextUtil.globMessage(" - &e"+trans(mat));
				foods.remove(mat);
				for(PlayerInfo pi:Events.plist.values()){
					pi.setbool("fastFood"+i, false);
				}
			}
		}
		
		String trans(Material mat){
			if(foodDictionary.containsKey(mat))return foodDictionary.get(mat);
			return mat.name();
		}
		
		@Override public void onTick(int rate) {
			
		}
		
		@Override public void onEnd() {
		}
		
		@EventHandler
		public void eat(FoodLevelChangeEvent e){
			if(e.getItem()!=null){
				PlayerInfo pi = Events.plist.get(e.getEntity().getName());
				for(int i=0;i<needToEat.size();i++){
					if(e.getItem().getType()==needToEat.get(i)&&!pi.team.bools.contains("fastFood"+i)){
						pi.team.setBool("fastFood"+i);
						for(int j=0;j<needToEat.size();j++){
							if(!pi.team.bools.contains("fastFood"+j)){
								for(PlayerInfo pli:pi.team.getMembers()){
									pli.mes(name, "&6"+pi.pname+" &fсъел &6"+trans(e.getItem().getType())+"&f. Вашей команде осталось съесть:");
									for(j=0;j<needToEat.size();j++){
										if(!pi.team.bools.contains("fastFood"+j)){
											pli.mes(" - &e"+trans(needToEat.get(j)));
										}
									}
								}
								return;
							}
						}
						TextUtil.globMessage(name, "&6"+pi.pname+" &fсъедает последний инградиент, и его команда получает &bдоп. жизнь&f!", Sound.ENTITY_PLAYER_BURP, 1, 2);
						pi.team.lives++;
						end();
					}
				}
			}
		}
	};
	
	public static final GlobalEvent suicide = new GlobalEvent("suicide", "Кто первый умрёт", Material.SKELETON_SKULL, new String[]{"Кто первый умрёт - получить доп. жизнь команде, не теряя свою.","&cНО&f: в чате 5 минут не выводятся сообщения о смерти."}, 600) {
		
		PlayerInfo winner;
		List<PlayerInfo> afterWinner = new ArrayList<>();
		
		@Override public void onActivate() {
			winner=null;
		}
		
		@Override public void onTick(int rate) {
			
		}
		
		@Override public void onEnd() {
			if(winner==null){
				TextUtil.globMessage(name, "Никто не умер за эти 5 минут! О, чудо!", Sound.BLOCK_BELL_RESONATE, 2, 2);
			}else{
				TextUtil.globMessage(name, "&6"+winner.pname+" &fумер первым, принеся команде "+winner.team.name+" &fдоп. жизнь!", Sound.BLOCK_BELL_RESONATE, 2, 0);
				new BukkitRunnable() {
					@Override public void run() {
						if(afterWinner.size()==0){
							this.cancel();
							return;
						}
						TextUtil.globMessage(name, "&7"+afterWinner.get(0).pname+" &fумер позже, унеся у команды "+afterWinner.get(0).team.name+" &f жизнь.", Sound.BLOCK_BELL_USE, 2, 0);
						afterWinner.remove(0);
					}
				}.runTaskTimer(main.instance, 20, 40);
			}
		}
		
		@EventHandler
		public void die(PlayerDeathEvent e){
			PlayerInfo pi = Events.plist.get(e.getEntity().getName());
			e.setDeathMessage(null);
			pi.team.mes(name, "&6"+pi.pname+"&f умер.");
			if(winner==null){
				winner=pi;
				pi.team.mes(name, "И он становится &aпервым&f, принося команде &bжизнь&f! &7(Тупо выдано 2 жизни, 1 из которых потратится на возрождение)");
				pi.team.lives+=2;
			}else{
				pi.team.mes(name, "И он становится &cНЕ&f первым. &7(Первым был &c"+winner.pname+"&7 из команды "+winner.team.name+"&7)");
			}
		}
	};
	
	private static int rate=0;
	private static int lastTime=-1;
	public static void tick(){
		if(MGTManager.stages.stage!=Stages.Game)return;
		for(GlobalEvent e:active){
			e.tick(rate);
		}
		rate++;
		int time = (int) Bukkit.getWorld("world").getTime()+8000;//0 = 8:00; 1000 = 9:00...
		if(time>=24000)time-=24000;
		if(lastTime>0&&time<lastTime){
			changeDay();
		}
		lastTime=time;
	}
	
	public static void resetLastTime(){
		lastTime=-1;
	}
	
	public static void changeDay(){
		List<GlobalEvent> canAdd = new ArrayList<>(list);
		for(GlobalEvent e:new ArrayList<>(active)){
			canAdd.remove(e);
		}
		GlobalEvent c = canAdd.get(r.nextInt(canAdd.size()));
		addEvent(c);
	}
	
	public static final Inv inv = new Inv("&6Активные ивенты") {
		
		@Override
		public void displItems(Inventory inv) {
			int i=0;
			for(GlobalEvent e:active){
				inv.setItem(i, e.displ());
				i++;
			}
		}
		
		@Override
		public void click(InventoryClickEvent e) {
			//do nothing...
		}
	};
	
	/*public static void enable(){
		Conf conf = new Conf(main.instance.getDataFolder()+"/activeRules.yml");
		if(conf.contains("rules")){
			List<String> sts = conf.getStringList("rules");
			for(GlobalEvent e:list){
				if(sts.contains(e.id))addEvent(e);
			}
		}
	}
	
	public static void disable(){
		Conf conf = new Conf(main.instance.getDataFolder()+"/activeRules.yml");
		List<String> toSet = new ArrayList<>();
		for(GlobalEvent e:active){
			toSet.add(e.id);
		}
		conf.set("rules", toSet);
		conf.save();
	}*/
}
