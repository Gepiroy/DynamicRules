package cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import DynamicRules.Events;
import Utils.TextUtil;
import obj.PlayerInfo;

public class CmdExecutor implements CommandExecutor{ //Системный. Просто выполняет поручения от CmdManager-а.
	private static final String onlyOp="Эта команда предназначена для разрабов. Просто играй! Быть разрабом - &cочень тяжёлый труд&f!";
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Не, разрабу не лень добавлять проверку на игрока.");
			return true;
		}
		Player p=(Player) sender;
		PlayerInfo pi = Events.plist.get(p.getName());
		pi.cmd.clear();
		StringBuilder pres = new StringBuilder(label+" ");
		TextUtil.debug("cmd="+cmd+"; label="+label+"; args="+args);
		for(ArgReactor c:CmdManager.cmds){
			if(c.arg.equalsIgnoreCase(label)){
				ArgReactor ar = c;
				if(ar.op&&!p.isOp()){
					TextUtil.mes(p, TextUtil.plugin, onlyOp);
					return true;
				}
				int num=0;
				for(String st:args){
					boolean found=false;
					if(ar.next.size()>0){
						found=false;
						for(ArgReactor ar2:ar.next){
							if(ar2.arg.equalsIgnoreCase(st)){
								if(ar2.op&&!p.isOp()){
									TextUtil.mes(p, TextUtil.plugin, onlyOp);
									return true;
								}
								ar=ar2;
								pres.append(ar.arg+" ");
								found=true;
							}
						}
						if(!found){
							ar.sublist(p, "Команда &cне найдена&f. &aПопробуйте&f:", pres.toString());
							break;
						}
					}
					if(!found){
						if(ar.args.size()>num){
							pi.cmd.add(st);
							num++;
						}else if(ar.args.size()==0){
							ar.sublist(p, "Команда &cне найдена&f. &aПопробуйте&f:", pres.toString());
							return true;
						}
					}
				}
				TextUtil.debug("&areacted!");
				ar.react(p);
			}
		}
		return true;
	}
}
