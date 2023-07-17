package MinigameToys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import DynamicRules.Events;
import DynamicRules.RuleManager;
import DynamicRules.main;
import Utils.TextUtil;
import obj.PlayerInfo;

public class Stages {
	private static final World world = Bukkit.getWorld("world");
	
	public static List<MinigameStage> list = new ArrayList<>();
	
	public static final MinigameStage Waiting = new MinigameStage(600, "&bОжидание игроков") {
		@Override public void onSet() {
			for(Player p:Bukkit.getOnlinePlayers()){
				onJoin(p);
			}
			
			RuleManager.resetRules();
		}
		@Override public void onJoin(Player p) {
			p.setHealth(20);
			p.setFoodLevel(20);
			if(p.getGameMode()==GameMode.CREATIVE)return;
			for(PotionEffect pef:p.getActivePotionEffects()){
				p.removePotionEffect(pef.getType());
			}
			p.getInventory().clear();
			p.setGameMode(GameMode.SURVIVAL);
			p.getInventory().setItem(0, TeamManager.item.clone());
			p.getInventory().setItem(8, Ready.item.clone());
			//Телепорт не нужен конкретно в этой миниигре.
		}
		@Override public boolean skip() {
			return false;
		}
	};
	public static final MinigameStage Prepare = new MinigameStage(30, "&bПодготовка") {
		@Override public void onSet() {
			for(Player p:Bukkit.getOnlinePlayers()){
				p.teleport(p.getWorld().getSpawnLocation());
				p.getInventory().clear();
				TextUtil.title(p, "&bПодготовка!", "Через &e30 секунд &fможно будет начать развитие.", 20, 50, 30);
			}
			TeamManager.raspr();
			for(Team t:TeamManager.teams){
				t.lives=t.getMembers().size()*3;
			}
			world.setTime(0);
			for(Entity en:world.getEntities()){
				if(en instanceof Monster)en.remove();
			}
		}
		@Override public void onJoin(Player p) {
			
		}
		@Override public boolean skip() {
			return false;
		}
	}.kick();
	public static final MinigameStage Game = new MinigameStage(3600*3, "&aИгра") {
		@Override public void onSet() {
			for(PlayerInfo pi:Events.plist.values()){
				pi.title("&bИгра началась!", "&aУдачи!", 20, 20, 20);
			}
			new BukkitRunnable() {
				@Override public void run() {
					TextUtil.globMessage(TextUtil.plugin, "Каждую полночь правила будут меняться. Будьте готовы адаптироваться под новые условия!");
				}
			}.runTaskLater(main.instance, 200);
		}
		@Override public void onJoin(Player p) {
			
		}
		@Override public boolean skip() {
			return false;
		}
	}.kick();
	public static final MinigameStage End = new MinigameStage(10, "&bКонец игры") {
		@Override public void onSet() {
			TextUtil.globMessage("&dКонец игры", "Разраб - ленивая жопа, которая забыла сделать концовку нормальной!");
			RuleManager.resetRules();
		}
		@Override public void onJoin(Player p) {
			
		}
		@Override public boolean skip() {
			return false;
		}
	}.kick();
}
