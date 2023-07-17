package MinigameToys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import DynamicRules.Events;
import Utils.ItemUtil;
import invsUtil.Inv;
import invsUtil.Invs;
import obj.PlayerInfo;

public class TeamManager {
	
	/*
	 * Каждый раз этот класс нужно перенастраивать: менять названия команд и т.п.
	 * В последнем методе нужно подключать свою систему команд для перешифровки из селекторов в прям прям команды.
	 */
	
	public static List<Team> teams = new ArrayList<>(); //Сюда автоматом пихаются все новые тимы.
	
	static final Team t1 = new Team();
	static final Team t2 = new Team();
	static final Team t3 = new Team();
	static final Team t4 = new Team();
	
	//public static HashTeam selectors = new HashTeam();
	
	//TODO init with teams preset.
	
	public static final ItemStack item = ItemUtil.create(Material.COMPASS, 1, "&5Выбор команды", "&9o&f/-_-\\&c0", null, 0);
	
	static ItemStack gen(Material mat, String name, PlayerInfo pi, int team){
		List<String> lore = new ArrayList<>();
		for(PlayerInfo pli:Events.plist.values()){
			if(pli.team!=null&&pli.team.id==team-1){
				lore.add((pli.p==pi.p ? "&f- &a" : "&7- &f")+pli.pname);
			}
		}
		Enchantment ench = null;
		if(pi.team!=null&&pi.team.id==team-1)ench=Enchantment.ARROW_DAMAGE;
		return ItemUtil.create(mat, 1, name, lore, ench, 1);
	}
	
	static final int[][] presets = {
			{0,0,1,0,0,0,2,0,0},//2
			{0,0,1,0,2,0,3,0,0},//3
			{0,1,0,2,0,3,0,4,0},//4
			{1,0,2,0,3,0,4,0,5}};//5
	
	public static final Inv inv = new Inv("&dВыбор команды") {
		@Override public void displItems(Inventory inv) {
			if(teams.size()<6){
				for(Team t:teams){
					for(int i=0;i<9;i++){
						if(presets[teams.size()-2][i]==t.id+1){
							inv.setItem(i, gen(t.mat, t.name, pi, t.id+1));
							break;
						}
					}
				}
			}else{
				int i=0;
				for(Team t:teams){
					i++;
					inv.setItem(i, gen(t.mat, t.name, pi, t.id+1));
				}
			}
			
			inv.setItem(22, gen(Material.TNT, "&dРандом", pi, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			int teamNum=0;
			if(e.getSlot()<18){
				if(teams.size()<6){
					teamNum = presets[teams.size()-2][e.getSlot()];
				}else teamNum = e.getSlot()+1;
			}
			set(pi, teamNum);//Аргумент - id_тимы+1. 0 = обнулить тиму.
		}
	};
	
	static void set(PlayerInfo pi, int team){
		Team old = pi.team;
		pi.team= team==0 ? null : teams.get(team-1);
		if(old!=pi.team)for(Player pl:Bukkit.getOnlinePlayers()){
			InventoryView v = pl.getOpenInventory();
			if(v!=null&&v.getTitle().equals(inv.name))Invs.open(pl, inv);
		}
	}
	
	static void raspr(){
		//Определяем тимы, что ДОЛЖНЫ быть
		int[] ints = new int[teams.size()];
		for(PlayerInfo pi:Events.plist.values()){
			if(pi.team!=null)ints[pi.team.id]++;
		}
		List<Team> avaliable = new ArrayList<>();
		for(int i=0;i<ints.length;i++){
			if(ints[i]>0)avaliable.add(teams.get(i));
		}
		if(avaliable.size()==0)avaliable.add(teams.get(0));
		if(avaliable.size()<2)avaliable.add(teams.get(avaliable.get(0).id==0? 1 : 0));
		
		//Определяем порог справедливости
		int fair = (Events.plist.size()+avaliable.size()-1)/avaliable.size();
		
		//Выгоняем всех несправедливых
		List<PlayerInfo> rpls = new ArrayList<>(Events.plist.values());
		Collections.shuffle(rpls);//Обеспечиваем рандомность.
		for(PlayerInfo pi:rpls){
			if(pi.team!=null&&ints[pi.team.id]>fair){
				ints[pi.team.id]--;
				pi.team=null;
			}
		}
		
		//Распределяем их
		for(PlayerInfo pi:rpls){
			if(pi.team==null){
				for(Team t:avaliable) if(ints[t.id]<fair){
					pi.team=t;
					ints[t.id]++;
					break;
				}
			}
		}
		//??? Profit!
		for(PlayerInfo pi:rpls){
			updateScoreboard(pi);
			rasprScoreboard(pi);
		}
	}
	
	public static void updateScoreboard(PlayerInfo pi){
		Scoreboard s = pi.board.getScoreboard();
		org.bukkit.scoreboard.Team t = s.getTeam("we");
		if(t==null){
			t=s.registerNewTeam("we");
			t.setCanSeeFriendlyInvisibles(true);
			t.setColor(ChatColor.GREEN);
			//t.setPrefix(ChatColor.GREEN.toString());
			//t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		}
		t = s.getTeam("enemies");
		if(t==null){
			t=s.registerNewTeam("enemies");
			t.setColor(ChatColor.RED);
			//t.setPrefix(ChatColor.RED.toString());
			//t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		}
	}
	
	public static void rasprScoreboard(PlayerInfo pi){
		Scoreboard s = pi.board.getScoreboard();
		org.bukkit.scoreboard.Team we = s.getTeam("we");
		org.bukkit.scoreboard.Team enemies = s.getTeam("enemies");
		for(PlayerInfo pli:Events.plist.values()){
			if(pli.team==pi.team)we.addEntry(pli.pname);
			else enemies.addEntry(pli.pname);
		}
	}
	
	public List<Team> getAliveTeams(){
		List<Team> ret = new ArrayList<>();
		for(Team t:teams)if(!t.isEliminated())ret.add(t);
		return ret;
	}
}
