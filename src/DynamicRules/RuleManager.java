package DynamicRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import MinigameToys.MGTManager;
import MinigameToys.Stages;
import Utils.TextUtil;
import invsUtil.Inv;
import obj.PlayerInfo;
import obj.Rule;

public class RuleManager {
	
	static final Random r = main.r;
	
	public static List<Rule> list = new ArrayList<>();
	
	public static List<Rule> rules = new ArrayList<>();
	
	public static void addRule(Rule rule){
		rules.add(rule);
		rule.onActivate();
		Bukkit.getPluginManager().registerEvents(rule, main.instance);
	}
	
	public static void remRule(Rule rule){
		rules.remove(rule);
		HandlerList.unregisterAll(rule);
	}
	
	public static void resetRules(){
		for(Rule r:rules)remRule(r);
	}
	
	static final Rule rf = new Rule("fish", "&6Режим рыбы", Material.TROPICAL_FISH, new String[]{
			"&fВы плаваете значительно &bбыстрее",
			"&сНО",
			"&eВы должны постоянно получать воду,",
			"&eиначе будете &7сохнуть&e.",
			"&fДля пополнения воды можно &bкупаться&f,",
			"&fлибо пить &dзелья&f / &9пузырьки воды&f."
	}) {
		
		@Override public void onActivate() {
			for(PlayerInfo pi:Events.plist.values()){
				pi.waits.remove("Fish");
			}
		}
		@Override public void tick(int rate) {
			if(rate%20==0){
				for(PlayerInfo pi:Events.plist.values()){
					Material mat = pi.p.getLocation().getBlock().getType();
					if(mat.equals(Material.WATER))resetFish(pi);
					else if(pi.p.getGameMode()==GameMode.SURVIVAL)pi.waits.add("Fish", 1);
					int f = pi.waits.get("Fish");
					if(f==60){
						pi.mes(name, "&7Ухх... Тяжело без воды...");
						pi.potion(PotionEffectType.BLINDNESS, 0, 3);
					}
					if(f>60){
						pi.waits.add("FishHurt", (f-30)/30);
						if(pi.waits.get("FishHurt")>=60){
							pi.p.damage(1);
							pi.mes(name, "Я сохну. Нужна вода...");
							pi.waits.add("FishHurt", -60);
						}
					}
					if(rate%100==0)pi.potion(PotionEffectType.DOLPHINS_GRACE, 0, 120);
				}
			}
		}
		@EventHandler
		public void drink(PlayerItemConsumeEvent e){
			if(e.getItem().getType()==Material.POTION){
				resetFish(Events.plist.get(e.getPlayer().getName()));
			}
		}
		
		@EventHandler
		public void die(PlayerDeathEvent e){
			Events.plist.get(e.getEntity().getName()).waits.remove("Fish");
		}
		
		private void resetFish(PlayerInfo pi){
			if(pi.waits.get("Fish")>5){
				pi.mes(name, "&bОх, как хорошо после воды!");
			}
			pi.waits.remove("Fish");
		}
	};
	static final Rule rht = new Rule("tools", "&6Хрупкие инструметы", Material.GOLDEN_PICKAXE, new String[]{
			"&eВы не можете использовать инструмент,",
			"&eкогда у него менее &650% &eпрочности."
	}) {
		
		@Override public void onActivate() {}
		
		
		@EventHandler
		public void interact(PlayerInteractEvent e){
			PlayerInfo pi = Events.plist.get(e.getPlayer().getName());
			ItemStack hitem = e.getItem();
			if(hitem!=null&&hitem.hasItemMeta()&&hitem.getItemMeta() instanceof Damageable){
				Damageable dameta = (Damageable) hitem.getItemMeta();
				if(hitem.getType().getMaxDurability()>1&&dameta.getDamage()>=hitem.getType().getMaxDurability()/2){
					e.setCancelled(true);
					pi.mes(TextUtil.plugin, "Предмет слишком хрупкий, нельзя таким пользоваться сейчас.");
				}
			}
		}
		
		@EventHandler
		public void hurt(EntityDamageByEntityEvent e){
			if(e.getDamager() instanceof Player){
				PlayerInfo pi = Events.plist.get(e.getDamager().getName());
				ItemStack hitem = pi.hitem();
				if(hitem!=null&&hitem.hasItemMeta()&&hitem.getItemMeta() instanceof Damageable){
					Damageable dameta = (Damageable) hitem.getItemMeta();
					if(dameta.getDamage()>=hitem.getType().getMaxDurability()/2){
						e.setCancelled(true);
						pi.mes(TextUtil.plugin, "Предмет слишком хрупкий, нельзя таким бить сейчас.");
					}
				}
			}
		}
		@Override public void tick(int rate) {}
	};
	static final Rule pacifism = new Rule("pacifism", "&6Пацифизм", Material.FEATHER, new String[]{"75% мобов не нападают первыми.","&cНО","&fБлижний бой отключен."}) {
		
		@Override public void onActivate() {}
		@Override public void tick(int rate) {}
		
		@EventHandler
		public void hurt(EntityDamageByEntityEvent e){
			if(e.getDamager() instanceof Player){
				e.setCancelled(true);
				TextUtil.actionBar((Player) e.getDamager(), "Ближний бой отключен.");
			}
		}
		
		@EventHandler
		public void spawn(EntitySpawnEvent e){
			if(e.getEntity() instanceof Creature && r.nextFloat()<=0.75){
				Creature en = (Creature) e.getEntity();
				en.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(0);
			}
		}

	};
	static final Rule noRegen = new Rule("noregen", "&6Нет регена", Material.RED_DYE, "&fСытость теперь равна здоровью, не снижается и не регенит его сама по себе.") {
		
		@Override public void onActivate() {}
		@Override public void tick(int rate) {}
		
		@EventHandler
		public void regen(EntityRegainHealthEvent e){
			if(e.getEntity() instanceof Player){
				if(e.getRegainReason()==RegainReason.SATIATED)e.setCancelled(true);
				new BukkitRunnable() {
					@Override public void run() {
						Player p =(Player) e.getEntity();
						p.setFoodLevel((int) (p.getHealth()));
					}
				}.runTaskLater(main.instance, 1);
			}
		}
		
		@EventHandler (ignoreCancelled=true, priority=EventPriority.HIGH)
		public void hurt(EntityDamageEvent e){
			if(e.getEntity() instanceof Player){
				new BukkitRunnable() {
					@Override public void run() {
						Player p =(Player) e.getEntity();
						p.setFoodLevel((int) (p.getHealth()));
					}
				}.runTaskLater(main.instance, 1);
			}
		}
		
		@EventHandler
		public void food(FoodLevelChangeEvent e){
			Player p = (Player) e.getEntity();
			if(e.getFoodLevel()!=p.getHealth()){
				if(e.getItem()!=null){
					int level = e.getFoodLevel();
					if(level>p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())level=(int) p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
					p.setHealth(level);
				}
				else{
					e.setFoodLevel((int) Math.round(p.getHealth()));
				}
			}
		}
		
	};
	static final Rule effect = new Rule("effect", "&6Эффект", Material.GLASS_BOTTLE, "&fВыдаётся рандомный эффект.") {
		PotionEffectType type;
		@Override public void onActivate() {
			List<PotionEffectType> pefs = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			pefs.remove(PotionEffectType.BAD_OMEN);
			pefs.remove(PotionEffectType.HARM);
			pefs.remove(PotionEffectType.HEAL);
			pefs.remove(PotionEffectType.HERO_OF_THE_VILLAGE);
			pefs.remove(PotionEffectType.LEVITATION);
			pefs.remove(PotionEffectType.POISON);
			pefs.remove(PotionEffectType.REGENERATION);
			pefs.remove(PotionEffectType.WITHER);
			pefs.remove(PotionEffectType.SATURATION);
			type=pefs.get(main.r.nextInt(pefs.size()));
			TextUtil.globMessage("&6Рандомным эффектом &fстановится &b"+type);
		}
		@Override public void tick(int rate) {
			if(rate%100!=0)return;
			for(PlayerInfo pi:Events.plist.values()){
				pi.potion(type, 0, 110);
			}
		}
	};
	
	static final Rule foodeffect = new Rule("foodeffect", "&6Эффект еды", Material.COOKIE, "&fПри поедании разных типов еды вы получаете разные эффекты.") {
		List<PotionEffectType> pefs;
		HashMap<Material, PotionEffectType> types = new HashMap<>();
		@Override public void onActivate() {
			pefs = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			pefs.remove(PotionEffectType.BAD_OMEN);
			//pefs.remove(PotionEffectType.HARM);
			//pefs.remove(PotionEffectType.HEAL);
			pefs.remove(PotionEffectType.HERO_OF_THE_VILLAGE);
			//pefs.remove(PotionEffectType.LEVITATION);
			//pefs.remove(PotionEffectType.POISON);
			//pefs.remove(PotionEffectType.REGENERATION);
			//pefs.remove(PotionEffectType.WITHER);
			//pefs.remove(PotionEffectType.SATURATION);
		}
		@EventHandler
		public void eat(FoodLevelChangeEvent e){
			if(e.getItem()!=null){
				Material mat = e.getItem().getType();
				Player p = (Player) e.getEntity();
				if(!types.containsKey(mat)){
					types.put(mat, pefs.get(r.nextInt(pefs.size())));
				}
				if(types.get(mat).isInstant())p.addPotionEffect(new PotionEffect(types.get(mat), 1, 0, false, true, true));
				else p.addPotionEffect(new PotionEffect(types.get(mat), 400, 0, false, true, true));
			}
		}
		@Override public void tick(int rate) {}
	};
	
	static final Rule wildness = new Rule("wildness", "&6Одичание", Material.CHEST, "&fНельзя открывать никакие блоки с инвентарём.") {
		
		List<Material> banMats = new ArrayList<>();
		
		@Override public void onActivate() {
			banMats.clear();
			banMats.add(Material.CRAFTING_TABLE);
			banMats.add(Material.ENCHANTING_TABLE);
			banMats.add(Material.STONECUTTER);
			banMats.add(Material.CARTOGRAPHY_TABLE);
			banMats.add(Material.FLETCHING_TABLE);
		}
		@Override public void tick(int rate) {}
		
		@EventHandler
		public void interact(PlayerInteractEvent e){
			if(e.getAction()==Action.RIGHT_CLICK_BLOCK&&(e.getClickedBlock().getState() instanceof InventoryHolder||banMats.contains(e.getClickedBlock().getType()))){
				e.setCancelled(true);
				TextUtil.actionBar(e.getPlayer(), "Такие блоки сейчас нельзя использовать.");
			}
		}
		
		@EventHandler
		public void place(BlockPlaceEvent e){
			if(e.getBlock().getState() instanceof InventoryHolder||banMats.contains(e.getBlock().getType())){
				e.setCancelled(true);
				TextUtil.actionBar(e.getPlayer(), "Такие блоки сейчас нельзя использовать.");
			}
		}
	};
	
	static final Rule mine = new Rule("mine", "&6Дробление руд", Material.IRON_PICKAXE, "&fВместо руды из железа и золота выпадают самородки.") {
		
		@Override public void onActivate() {
			
		}
		@Override public void tick(int rate) {}
		
		@EventHandler
		public void bb(BlockBreakEvent e){
			Block b = e.getBlock();
			if(b.getType()==Material.GOLD_ORE){
				e.setDropItems(false);
				for(int i=0;i<main.r.nextInt(3)+8;i++){
					b.getWorld().dropItemNaturally(b.getLocation().add(main.r.nextDouble(), main.r.nextDouble()/2, main.r.nextDouble()),new ItemStack(Material.GOLD_NUGGET));
				}
			}else if(b.getType()==Material.IRON_ORE){
				e.setDropItems(false);
				for(int i=0;i<main.r.nextInt(3)+8;i++){
					b.getWorld().dropItemNaturally(b.getLocation().add(main.r.nextDouble(), main.r.nextDouble()/2, main.r.nextDouble()),new ItemStack(Material.IRON_NUGGET));
				}
			}
		}
	};
	
	static final Rule ghasts = new Rule("ghasts", "&6Гасты", Material.GHAST_TEAR, "&fВ любом мире появляются гасты.") {
		@Override public void onActivate() {}
		@Override public void tick(int rate) {
			if(rate%20!=0)return;
			for(PlayerInfo pi:Events.plist.values()){
				if(main.r.nextBoolean()){
					pi.waits.add("spawnGhast", 1);
					if(pi.waits.get("spawnGhast")>=30){
						pi.waits.remove("spawnGhast");
						pi.p.getWorld().spawnEntity(pi.p.getLocation().add(main.r.nextInt(31)-15, 45, main.r.nextInt(31)-15), EntityType.GHAST);
					}
				}
			}
		}
	};
	
	static final Rule rainman = new Rule("rainman", "&6Мужицкий дождь", Material.WATER_BUCKET, "&fС неба падают рандомные мобы.") {
		List<EntityType> types = new ArrayList<>();
		List<UUID> saveFromFall = new ArrayList<>();
		@Override public void onActivate() {
			types.clear();
			saveFromFall.clear();
			types.add(EntityType.ARMOR_STAND);
			types.add(EntityType.BEE);
			types.add(EntityType.BLAZE);
			types.add(EntityType.CAT);
			types.add(EntityType.CAVE_SPIDER);
			types.add(EntityType.CHICKEN);
			types.add(EntityType.COD);
			types.add(EntityType.COW);
			types.add(EntityType.CREEPER);
			types.add(EntityType.DOLPHIN);
			types.add(EntityType.DONKEY);
			types.add(EntityType.EGG);
			types.add(EntityType.ENDERMAN);
			types.add(EntityType.ENDERMITE);
			types.add(EntityType.EVOKER);
			types.add(EntityType.FOX);
			types.add(EntityType.GHAST);
			types.add(EntityType.GIANT);
			types.add(EntityType.GUARDIAN);
			types.add(EntityType.HOGLIN);
			types.add(EntityType.HORSE);
			types.add(EntityType.HUSK);
			types.add(EntityType.ILLUSIONER);
			types.add(EntityType.IRON_GOLEM);
			types.add(EntityType.LLAMA);
			types.add(EntityType.MAGMA_CUBE);
			types.add(EntityType.MULE);
			types.add(EntityType.MUSHROOM_COW);
			types.add(EntityType.OCELOT);
			types.add(EntityType.PANDA);
			types.add(EntityType.PARROT);
			types.add(EntityType.PHANTOM);
			types.add(EntityType.PIG);
			types.add(EntityType.PIGLIN);
			types.add(EntityType.PIGLIN_BRUTE);
			types.add(EntityType.PILLAGER);
			types.add(EntityType.POLAR_BEAR);
			types.add(EntityType.PUFFERFISH);
			types.add(EntityType.RABBIT);
			types.add(EntityType.RAVAGER);
			types.add(EntityType.SALMON);
			types.add(EntityType.SHEEP);
			types.add(EntityType.SHULKER);
			types.add(EntityType.SILVERFISH);
			types.add(EntityType.SKELETON);
			types.add(EntityType.SLIME);
			types.add(EntityType.SKELETON_HORSE);
			types.add(EntityType.SNOWMAN);
			types.add(EntityType.SPIDER);
			types.add(EntityType.SQUID);
			types.add(EntityType.THROWN_EXP_BOTTLE);
			types.add(EntityType.TROPICAL_FISH);
			types.add(EntityType.TURTLE);
			types.add(EntityType.VEX);
			types.add(EntityType.VILLAGER);
			types.add(EntityType.VINDICATOR);
			types.add(EntityType.WITCH);
			types.add(EntityType.WITHER_SKELETON);
			types.add(EntityType.WOLF);
			types.add(EntityType.ZOGLIN);
			types.add(EntityType.ZOMBIE);
			types.add(EntityType.ZOMBIE_HORSE);
			types.add(EntityType.ZOMBIE_VILLAGER);
			types.add(EntityType.ZOMBIFIED_PIGLIN);
		}
		@Override public void tick(int rate) {
			if(rate%20!=0)return;
			for(PlayerInfo pi:Events.plist.values()){
				if(main.r.nextBoolean()){
					pi.waits.add("spawnRM", 1);
					if(pi.waits.get("spawnRM")>=30){
						pi.waits.remove("spawnRM");
						Entity en = pi.p.getWorld().spawnEntity(pi.p.getLocation().add(main.r.nextInt(31)-15, 45, main.r.nextInt(31)-15), types.get(main.r.nextInt(types.size())));
						saveFromFall.add(en.getUniqueId());
					}
				}
			}
		}
		
		@EventHandler
		public void dam(EntityDamageEvent e){
			if(e.getCause()==DamageCause.FALL&&saveFromFall.contains(e.getEntity().getUniqueId())){
				saveFromFall.remove(e.getEntity().getUniqueId());
				e.setCancelled(true);
			}
		}
	};
	
	static final Rule photo = new Rule("photo", "&6УЛЬТРАфиолет", Material.FLINT_AND_STEEL, new String[]{
			"&fНахождение не под крышей днём",
			"&fподжигает вас подобно зомби."
	}) {
		@Override public void onActivate() {}
		@Override public void tick(int rate) {
			if(rate%20!=0)return;
			int t = (int) Bukkit.getWorld("world").getTime();
			if(t>0&&t<12000)
			for(PlayerInfo pi:Events.plist.values()){
				Location l=pi.p.getEyeLocation().add(0, 1, 0);
				if(!l.getWorld().getName().equals("world"))continue;
				boolean under = false;
				for(int i=0;i<100;i++){
					if(l.getBlock().getType()!=Material.AIR){
						under=true;
						break;
					}
					l.add(0, 1, 0);
				}
				if(!under&&main.r.nextFloat()<=0.1f){
					pi.p.setFireTicks(21);
				}
			}
		}
	};
	
	static final Rule agro = new Rule("agro", "&6Агрессия", Material.BONE, new String[]{
			"&fМонстры видят гораздо дальше обычного",
			"&fи ускоряются, пока не подбегут ближе к цели."
	}) {
		@Override public void onActivate() {}
		
		PotionEffect pef = new PotionEffect(PotionEffectType.SPEED, 21, 0, false, false);
		
		@Override public void tick(int rate) {
			if(rate%20!=0)return;
			for(LivingEntity en:Bukkit.getWorld("world").getLivingEntities()){
				if(en instanceof Monster){
					Monster m = (Monster) en;
					if(m.getTarget()!=null&&m.getLocation().distance(m.getTarget().getLocation())>10){
						m.addPotionEffect(pef);
					}
				}
			}
		}
		
		@EventHandler
		public void spawn(EntitySpawnEvent e){
			if(e.getEntity() instanceof Monster){
				Monster m = (Monster) e.getEntity();
				m.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
			}
		}
	};
	
	
	
	
	private static int rate=0;
	private static int lastTime=-1;
	public static void tick(){
		if(MGTManager.stages.stage!=Stages.Game)return;
		for(Rule r:rules){
			r.tick(rate);
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
	
	private static final float addChance = 0.2f;
	private static final float remChance = 0.4f;
	
	public static void changeDay(){
		List<Rule> canAdd = new ArrayList<>(list);
		for(Rule r:new ArrayList<>(rules)){
			canAdd.remove(r);
			if(main.r.nextFloat()<=remChance){
				publicToggleRule(r);
			}
		}
		for(Rule r:canAdd){
			if(main.r.nextFloat()<=addChance){
				publicToggleRule(r);
			}
		}
	}
	
	public static void publicToggleRule(Rule r){
		if(rules.contains(r)){
			remRule(r);
			TextUtil.globMessage(TextUtil.plugin, "&8Устранено &fправило &7\""+r.name+"&7\"&f.");
		}else{
			addRule(r);
			TextUtil.globMessage(TextUtil.plugin, "&bУстановлено &fправило &7\""+r.name+"&7\"&f.");
			for(String st:r.lore){
				TextUtil.globMessage("  "+st);
			}
		}
	}
	
	public static final Inv inv = new Inv("&6Активные правила") {
		
		@Override
		public void displItems(Inventory inv) {
			int i=0;
			for(Rule r:rules){
				inv.setItem(i, r.displ());
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
			for(Rule r:list){
				if(sts.contains(r.id))addRule(r);
			}
		}
	}
	
	public static void disable(){
		Conf conf = new Conf(main.instance.getDataFolder()+"/activeRules.yml");
		List<String> toSet = new ArrayList<>();
		for(Rule r:rules){
			toSet.add(r.id);
		}
		conf.set("rules", toSet);
		conf.save();
	}*/
}
