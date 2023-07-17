package MinigameToys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import obj.PlayerInfo;
import Utils.ItemUtil;
import Utils.TextUtil;

public class Ready {
	private Ready(){}
	
	private static List<PlayerInfo> ready = new ArrayList<>();
	
	public static final ItemStack item = ItemUtil.create(Material.TOTEM_OF_UNDYING, 1, "&eГотов!", "&fСообщить о готовности начать игру", null, 0);
	
	private static final String tkey = "noReadyChange";
	private static final int set = 10;
	
	public static void change(PlayerInfo pi){
		if(MGTManager.stages.timer<=set)return;
		if(pi.timers.containsKey(tkey)){
			TextUtil.mes(pi.p, "Подождите ещё &e"+pi.timers.get(tkey)+" сек.");
			return;
		}
		if(ready.contains(pi)){
			ready.remove(pi);
			TextUtil.globMessage(TextUtil.plugin, pi.pname+" &8не готов... "+remain());
		}else{
			ready.add(pi);
			TextUtil.globMessage(TextUtil.plugin, pi.pname+" &aготов! "+remain());
			if(ready.size()==Bukkit.getOnlinePlayers().size()&&MGTManager.stages.timer>set&&ready.size()>1){
				for(PlayerInfo pli:new ArrayList<>(ready)){
					if(pli.p==null||!pli.p.isOnline()){
						ready.remove(pli);
						continue;
					}
				}
				if(ready.size()!=Bukkit.getOnlinePlayers().size())return;
				TextUtil.globMessage(TextUtil.plugin, "&aВсе игроки готовы&f! Таймер сброшен на &a"+set+" секунд&f!");
				MGTManager.stages.timer=set;
			}
		}
		pi.timers.put(tkey, 3);
	}
	
	private static String remain(){
		return "&8("+ready.size()+"/"+Bukkit.getOnlinePlayers().size()+")";
	}
	
	public static void gameStart(){
		ready.clear();
	}
}
