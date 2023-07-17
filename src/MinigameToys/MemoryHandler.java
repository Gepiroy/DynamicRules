package MinigameToys;

import org.bukkit.scheduler.BukkitRunnable;

import DynamicRules.main;
import Utils.TextUtil;

public class MemoryHandler {
	private static int min = 1024;
	public static void enable(){
		new BukkitRunnable() {
			@Override
			public void run() {
				int mem=memRemainBeforeShutdown();
				if(mem<min){
					min=mem;
					TextUtil.sdebug("&emem remain before &cshutdown&e: &6"+mem+"mb");
				}
			}
		}.runTaskTimer(main.instance, 40, 600);//30 sec
	}
	public static int memRemainBeforeShutdown(){
		return (int) ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory())/1048576);
	}
}
