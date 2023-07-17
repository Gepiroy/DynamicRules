package cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import Utils.TextUtil;

public abstract class ArgReactor {
	public String arg;
	public String lore;
	public boolean op=false;
	
	public List<ArgReactor> next = new ArrayList<>();
	public List<Arg> args = new ArrayList<>();
	
	
	public ArgReactor(String arg, String lore){
		this.arg=arg;
		this.lore=lore;
	}
	
	public abstract void react(Player p);
	
	void hreact(Player p){
		if(op&&!p.isOp())return;
	}
	
	public ArgReactor fillNext(ArgReactor[] args){
		for(ArgReactor ar:args){
			this.next.add(ar);
		}
		return this;
	}
	
	public ArgReactor addArg(Arg a){
		this.args.add(a);
		return this;
	}
	
	public void sublist(Player p){
		sublist(p, arg+" ");
	}
	
	//pres нужно с пробелом после себя ставить.
	public void sublist(Player p, String pres){
		sublist(p, "Список команд:", pres);
	}
	
	//pres нужно с пробелом после себя ставить.
	public void sublist(Player p, String pref, String pres){
		TextUtil.mes(p, TextUtil.plugin, pref);
		for(ArgReactor ar:next){
			if(ar.op&&!p.isOp())continue;
			char c = 'e';
			if(ar.op)c = 'b';//Подсветка для разрабов
			StringBuilder s = new StringBuilder("&7/&a"+pres+"&"+c+ar.arg+" ");
			for(Arg arg:ar.args){
				if(arg.isImportant)s.append("&f<&6"+arg+"&f> ");
				else s.append("&7[&f"+arg+"&7] ");
			}
			if(ar.lore!=null)s.append("&7- &f"+ar.lore);
			TextUtil.mes(p, s.toString());
		}
	}
	
	public ArgReactor op(){
		op=true;
		return this;
	}
}
