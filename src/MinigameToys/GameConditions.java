package MinigameToys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameConditions {
	private static HashMap<Object, List<GameCondition>> conditions = new HashMap<>();
	
	public static boolean isCondited(GameCondition cond){
		for(List<GameCondition> cos:conditions.values())for(GameCondition c:cos){
			if(c==cond)return true;
		}
		return false;
	}
	
	public static void cond(Object from, GameCondition c){
		List<GameCondition> list = conditions.get(from);
		if(list==null)list=new ArrayList<>();
		list.add(c);
		conditions.put(from, list);
	}
	
	public static void decond(Object from, GameCondition c){
		List<GameCondition> list = conditions.get(from);
		list.remove(c);
		if(list.size()==0)conditions.remove(from);
	}
	
	public static void decond(Object from){
		conditions.remove(from);
	}
}
