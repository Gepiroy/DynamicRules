package Villages;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.StructureType;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import DynamicRules.main;
import Utils.TextUtil;
import net.minecraft.server.v1_16_R3.StructureBoundingBox;
import net.minecraft.server.v1_16_R3.StructureGenerator;
import net.minecraft.server.v1_16_R3.StructureStart;
import net.minecraft.server.v1_16_R3.WorldGenVillage;
import obj.Conf;

public class VillageManager implements Listener{
	public static HashMap<Location, VillageInfo> villages = new HashMap<>();
	
	@EventHandler
	public void interact(PlayerInteractEvent e){
		if(e.getClickedBlock()!=null){
			Block b = e.getClickedBlock();
			if(b.getType().toString().contains("DOOR")&&!e.getPlayer().isSneaking())return;
			Location loc = b.getWorld().locateNearestStructure(b.getLocation(), StructureType.VILLAGE, 3, false);
			if(loc!=null){
				VillageInfo vill = villages.get(loc);
				if(vill==null){
					CraftChunk c = (CraftChunk) loc.getChunk();
					Map<StructureGenerator<?>, StructureStart<?>> h = c.getHandle().h();
					StructureStart<?> structure = h.get(WorldGenVillage.VILLAGE);
					//TextUtil.debug("structure="+structure);
					StructureBoundingBox box = structure.c();
					Location from = new Location(loc.getWorld(), box.a, box.b, box.c);
					Location to = new Location(loc.getWorld(), box.d, box.e, box.f);
					
					//TextUtil.debug("Village at "+TextUtil.loc(loc)+" (from "+TextUtil.loc(from)+" to "+TextUtil.loc(to)+") registered!");
					vill = new VillageInfo(from, to);
					villages.put(loc, vill);
				}
				if(!vill.captured&&vill.isIn(b.getLocation())){
					Player p = e.getPlayer();
					boolean hasVillagers = false;
					for(LivingEntity en:p.getWorld().getLivingEntities()){
						if((en instanceof Villager)&&vill.isIn(en.getLocation())){
							e.setCancelled(true);
							TextUtil.actionBar(p, "Пока в деревне есть жители, вы не можете её трогать.");
							hasVillagers = true;
							break;
						}
					}
					if(!hasVillagers){
						vill.captured=true;
						for(Player pl:Bukkit.getOnlinePlayers()){
							if(vill.isIn(pl.getLocation())){
								pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 2);
								pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2, 0);
								TextUtil.title(pl, "&6Деревня &c&lЗАХВАЧЕНА!", "&7Можно начать разграбление.", 30, 40, 30);
							}
						}
					}
				}
			}
		}
	}
	
	public static void enable(){
		Conf conf = new Conf(main.instance.getDataFolder()+"/villages.yml");
		for(String st:conf.getKeys("villages")){
			villages.put(conf.getLoc("villages."+st+".loc"), new VillageInfo(conf, "villages."+st));
		}
	}
	
	public static void disable(){
		Conf conf = new Conf(main.instance.getDataFolder()+"/villages.yml");
		conf.set("villages", null);
		int i=0;
		for(Location loc:villages.keySet()){
			VillageInfo vinf = villages.get(loc);
			conf.setLoc("villages."+i+".loc", loc);
			vinf.save(conf, "villages."+i);
			i++;
		}
		conf.save();
	}
}
