package cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import DynamicRules.Events;
import DynamicRules.GlobalEventsManager;
import DynamicRules.RuleManager;
import DynamicRules.main;
import Utils.TextUtil;
import obj.PlayerInfo;
import obj.Rule;

public class CmdManager{
	
	public static List<ArgReactor> cmds = new ArrayList<>();
	
	private static final CmdExecutor executor = new CmdExecutor();
	
	private CmdManager(){}
	
	public static void init(){
		regCmd(new ArgReactor("dr", "Основная команда DynamicRules.") {
			@Override public void react(Player p) {
				sublist(p);
			}
		}.fillNext(new ArgReactor[]{
				new ArgReactor("gui", "gui со списком активных правил.") {
					@Override public void react(Player p) {
						RuleManager.inv.open(p);
					}
				},
				new ArgReactor("evgui", "gui со списком активных ивентов.") {
					@Override public void react(Player p) {
						GlobalEventsManager.inv.open(p);
					}
				},
				new ArgReactor("change", "вкл/выкл правило") {
					@Override public void react(Player p) {
						PlayerInfo pi = Events.plist.get(p.getName());
						Rule rule = null;
						for(Rule r:RuleManager.list){
							if(r.id.equals(pi.cmd.get(0))){
								rule=r;
								break;
							}
						}
						if(rule!=null){
							RuleManager.publicToggleRule(rule);
						}else{
							pi.mes("id указан через жопу, давай по-новой. Список всех:");
							for(Rule r:RuleManager.list){
								pi.mes("  &b"+r.id+"&f - "+r.name+" &f("+(RuleManager.rules.contains(r) ? "&aon" : "&coff")+"&f)");
							}
						}
					}
				}.op().addArg(new Arg("rule_id", ArgType.String))
			}
		));
	}
	
	public static void regCmd(ArgReactor ar){
		cmds.add(ar);
		main.instance.getCommand(ar.arg).setExecutor(executor);
	}
	
	public static ArgReactor findCmd(String name){
		for(ArgReactor ar:cmds){
			if(ar.arg==name)return ar;
		}
		TextUtil.sdebug("&cCan't find command &e"+name);
		return null;
	}
	
}
