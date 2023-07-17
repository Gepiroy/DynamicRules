package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GepUtil {
	//Раньше это была утилита, в которой лежало ВСЁ, ЧТО Я КОГДА-ЛИБО СОЗДАВАЛ...
	//Сейчас тут ток возня с листами.
	
	public static String chancesByCoef(String[] sts, int[] coefs){
		int coef=0;
		for(int d:coefs){
			coef+=d;
		}
		int r = new Random().nextInt(coef);
		int ch = 0;
		for(int i=0;i<sts.length;i++){
			if(r>=ch&&r<=ch+coefs[i]){
				return sts[i];
			}
			ch+=coefs[i];
		}
		return ""+r;
	}
	public static String chancesByCoef(HashMap<String, Integer> sts){
		int coef=0;
		for(int d:sts.values()){
			coef+=d;
		}
		int r = new Random().nextInt(coef);
		int ch = 0;
		for(String st:sts.keySet()){
			if(r>=ch&&r<ch+sts.get(st)){
				return st;
			}
			ch+=sts.get(st);
		}
		return ""+r;
	}
	public static boolean chance(int ch){
		return new Random().nextInt(100)+1<=ch;
	}
	public static boolean chance(double ch){
		return new Random().nextDouble()<=ch;
	}
	public static String chances(String[] sts, double[] chs){
		double r = new Random().nextInt(100)+new Random().nextDouble();
		double ch = 0.000;
		for(int i=0;i<sts.length;i++){
			if(r>ch&&r<=ch+chs[i]){
				return sts[i];
			}
			ch+=chs[i];
		}
		return "";
	}
	public static ArrayList<String> stringToArrayList(String st){
		ArrayList<String> ret = new ArrayList<>();
		if(st==null||st.length()==0||st.equals("null"))return ret;
		String toadd = "";
		for(int i=0;i<st.length();i++){
			String c = st.charAt(i)+"";
			if(!c.equals(";")){
				toadd=toadd+c;
			}
			else{
				ret.add(toadd);
				toadd="";
			}
		}
		return ret;
	}
	public static String ArrayListToString(List<String> ara){
		String ret = "";
		for(String st:ara){
			ret = ret+st+";";
		}
		return ret;
	}
	public static String maxFromHM(HashMap<String,Integer> hm){
		String ret="";
		int max=Integer.MIN_VALUE;
		for(String st:hm.keySet()){
			if(hm.get(st)>max){
				max=hm.get(st);
				ret=st;
			}
		}
		return ret;
	}
	
	public static HashMap<String, Integer> hmFromString(String hmst){
		HashMap<String,Integer> another = new HashMap<>();
		if(hmst==null||hmst.length()<=4||hmst.contains("null"))return another;
		TextUtil.debug("hmfromstring: "+hmst);
		hmst = hmst.replace("{", "").replace("}", "");
		String[] hms = hmst.split(", ");
		
		for(String st:hms){
			String[] sts = st.split("=");
			another.put(sts[0], Integer.parseInt(sts[1]));
		}
		return another;
	}
	
}