package Utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import DynamicRules.main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {
	public static boolean debug=true;
	public static final String plugin="&6Dynamic&cRules";
	/**
	 * Color text by two switching colors, like a bee.
	 * @param st - input text
	 * @param c1 - first color
	 * @param c2 - second color
	 * @param iter - if %2==0, first color will c1. Else - c2.
	 * @param addict - add something after every color.
	 * @return
	 */
	public static String twoCols(String st, ChatColor c1, ChatColor c2, int iter, String addict){
		String RAID="";
		if(addict==null)addict="";
		for(int i=0;i<st.length();i++){
			char c=st.charAt(i);
			RAID+=((iter+i)%2==0 ? c1 : c2)+addict+c;
		}
		return RAID;
	}
	public String days(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" дней";
		if(m%10==1)return m+" день";
		if(m%10>=2&&m%10<=4)return m+" дня";
		return m+" ч.";
	}
	public String hours(int m){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" часов";
		if(m%10==1)return m+" час";
		if(m%10>=2&&m%10<=4)return m+" часа";
		return m+" ч.";
	}
	public String minutes(int m, boolean to){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" минут";
		if(m%10==1)
			if(to)return m+" минуту";
			else return m+" минута";
		if(m%10>=2&&m%10<=4)return m+" минуты";
		return m+" мин.";
	}
	public String secundes(int m, boolean to){
		if(m%10==0||m%10>=5||(m%100>10&&m%100<20))return m+" секунд";
		if(m%10==1)
			if(to)return m+" секунду";
			else return m+" секунда";
		if(m%10>=2&&m%10<=4)return m+" секунды";
		return m+" сек.";
	}
	/**
	* Fast amount and name of time.
	* @param sec - The time in seconds.
	* @param to - true="секунду", false="секунда".
	* @return String with time and name.
	*/
	public String times(int sec, boolean to){
		String ret="";
		if(sec>0)ret=secundes(sec%60, to);
		if(sec/60>0)ret=minutes(sec%3600/60, to)+" "+ret;
		if(sec/3600>0)ret=hours(sec%(3600*24)/3600)+" "+ret;
		if(sec/(3600*24)>0)ret=days(sec/(3600*24))+" "+ret;
		return ret;
	}
	public static void mes(CommandSender p, String pref, String mes){
		if(pref==null)p.sendMessage(str(mes));
		else p.sendMessage(str("&7["+pref+"&7] &f"+mes));
	}
	public static void mes(CommandSender p, String mes){
		p.sendMessage(str("&f"+mes));
	}
	public static void title(Player p, String title, String sub, int spawn, int hold, int rem){
		p.sendTitle(str(title), str(sub), spawn, hold, rem);
	}
	public static void globMessage(String pref, String mes, Sound sound, float vol, float speed){
		globMessage(pref, mes, sound, vol, speed, null, null, 0, 0, 0);
	}
	public static void globMessage(String pref, String mes){
		globMessage(pref, mes, null, 0, 0, null, null, 0, 0, 0);
	}
	public static void globMessage(String mes){
		globMessage(null, mes, null, 0, 0, null, null, 0, 0, 0);
	}
	public static void globMessage(String prefix, String mes, Sound sound, float vol, float speed, String title, String subtitle, int spawn, int hold, int remove){
		String chat="";
		if(prefix!=null)chat=str("&7["+prefix+"&7] &f"+mes);
		else chat=str(mes);
		String tit=null;
		if(title!=null)tit=str(title);
		String sub=null;
		if(subtitle!=null)sub=str(subtitle);
		for(Player p:Bukkit.getOnlinePlayers()){
			if(mes!=null)p.sendMessage(chat);
			if(sound!=null){
				p.playSound(p.getLocation(), sound, vol, speed);
			}
			if(title!=null||subtitle!=null) {
				p.sendTitle(tit, sub, spawn, hold, remove);
			}
		}
	}
	public static void globSound(Sound sound, float vol, float speed){
		for(Player p:Bukkit.getOnlinePlayers()){
			p.playSound(p.getLocation(), sound, vol, speed);
		}
	}
	public static void globTitle(String up, String down, int spawn, int hold, int out){
		for(Player p:Bukkit.getOnlinePlayers()){
			p.sendTitle(str(up), str(down), spawn, hold, out);
		}
	}
	
	public static String str(String text){
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	public static String toDate(long time){
		return toDate(time, "yyyy-MM-dd HH:mm:ss");
	}
	public static String toDate(long time, String type){
		Date when=new Date(time);
		DateFormat TIMESTAMP = new SimpleDateFormat(type);
		return TIMESTAMP.format(when);
	}
	public static void debug(String st){
		if(debug)Bukkit.getConsoleSender().sendMessage(str("deb from &6"+main.instance.getName()+"&f: "+st));
	}
	public static void sdebug(String st){
		Bukkit.getConsoleSender().sendMessage(str("deb from &6"+main.instance.getName()+"&f: "+st));
		for(Player p:Bukkit.getOnlinePlayers()){
			if(p.isOp())p.sendMessage(str("&8deb from &6"+plugin+"&f: "+st));
		}
	}
	/**
	 * How many same the strings?
	 * @param s1
	 * @param s2
	 * @param ignoreCase
	 * @return 0.0-1.0
	 */
	public double equal(String s1, String s2, boolean ignoreCase){
		int sovp=0;
		for(int i=s1.length();i>0;i--){
			if(!ignoreCase&&s1.charAt(i)==s2.charAt(i))sovp++;
			else if(ignoreCase&&(s1.charAt(i)+"").equalsIgnoreCase(s2.charAt(i)+""))sovp++;
		}
		return 1.0*s1.length()/sovp;
	}
	
	public static boolean isNumeric(String str){
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}
	
	static List<Double> doublesFromString(String st){
		List<Double> ret=new ArrayList<>();
		Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
		Matcher matcher=pat.matcher(st);
		while (matcher.find()) {
			ret.add(Double.parseDouble(matcher.group()));
		}
		return ret;
	}
	
	static List<Integer> intsFromString(String st){
		List<Integer> ret=new ArrayList<>();
		Pattern pat=Pattern.compile("[-]?[0-9]+");
		Matcher matcher=pat.matcher(st);
		while (matcher.find()) {
			ret.add(Integer.parseInt(matcher.group()));
		}
		return ret;
	}
	
	//Helpful when need to get value from item or other lore.
	//9 workers worked last 8 nights and maked 98 papers! ret=98.
	public static int lastIntFromString(String st){
		List<Integer> pret = intsFromString(st);
		if(pret.size()==0)return 0;
		return pret.get(pret.size()-1);
	}
	public static double lastDoubleFromString(String st){
		List<Double> pret = doublesFromString(st);
		if(pret.size()==0)return 0;
		return pret.get(pret.size()-1);
	}
	
	/**
	 * Fast-decime, just for my comfort.
	 * @param st
	 * @param i
	 * @return
	 */
	public static String decime(String st, double i){
		return new DecimalFormat(st).format(i);
	}
	//126 -> 02:06 ; 3692 -> 01:01:32 .
	public static String toTime(int i){
		if(i<3600)return decime("#00",i/60)+":"+decime("#00",i%60);
		else return decime("#00",i/3600)+":"+decime("#00",i%3600/60)+":"+decime("#00",i%60);
	}
	public static String CylDouble(double d, String cyl){
		return new DecimalFormat(cyl).format(d).replaceAll(",", ".");
	}
	public static List<String> split(String st, int maxPerLine){
		List<String> ret = new ArrayList<>();
		String[] sts = st.split(" ");
		String tmp="";
		int line=0;
		for(String s:sts){
			if(line+s.length()>maxPerLine){
				line=0;
				ret.add(new String(tmp));
				tmp="";
			}
			if(line!=0)tmp+=" ";
			line+=s.length();
			tmp+=s;
		}
		return ret;
	}
	
	public static void actionBar(Player p, String text){
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(str(text)));
	}
	
	public static List<String> smartColoredSplit(String from, int symbols){//needs already converted §.
		List<String> ret = new ArrayList<>();
		boolean colorIsNull=true;
		char lastColorCode='f';
		boolean nextCharIsCC=false;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<from.length();i++){
			char c = from.charAt(i);
			
			if(c=='§'){
				nextCharIsCC=true;
			}else if(nextCharIsCC){
				lastColorCode=c;
				colorIsNull=false;
				nextCharIsCC=false;
			}
			if(nextCharIsCC&&sb.length()==symbols-1){
				ret.add(sb.toString());
				sb.setLength(0);
				sb.append(c);
			}
			else{
				sb.append(c);
			}
			if(sb.length()==symbols){
				ret.add(sb.toString());
				sb.setLength(0);
				if(!colorIsNull){
					sb.append('§');
					sb.append(lastColorCode);
				}
			}
		}
		if(sb.length()>0)ret.add(sb.toString());
		return ret;
	}
	
	public static String loc(Location loc){
		if(loc==null)return "null";
		return loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ();
	}
}
