package obj;

import java.util.HashMap;

public class SWaits extends HashMap<String, String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SWaits(){
		
	}
	
	public SWaits(HashMap<String, String> hm){
		for(String st:hm.keySet()){
			super.put(st, hm.get(st));
		}
	}
	
	public void set(String key, String val){
		if(containsKey(key))super.remove(key);
		if(key!=null)super.put(key, val);
	}
	
	@Override
	public String get(Object key){
		if(containsKey(key))return super.get(key);
		else return null;
	}
	
	@Override
	public String remove(Object key){
		if(containsKey(key))super.remove(key);
		return null;
	}
	
	@Override
	public String toString(){
		return super.toString();
	}
}
