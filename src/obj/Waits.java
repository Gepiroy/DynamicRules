package obj;

import java.util.HashMap;

public class Waits extends HashMap<String, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void add(String key, int am){
		merge(key, am, Integer::sum);
	}
	
	public boolean addAndRemZero(String key, int am){
		merge(key, am, Integer::sum);
		if(get(key)<=0){
			super.remove(key);
			return true;
		}
		return false;
	}
	
	public void set(String key, int am, boolean zero){
		super.put(key, am);
		if(zero&&get(key)<=0)super.remove(key);
	}
	
	@Override
	public Integer get(Object key){
		if(containsKey(key))return super.get(key);
		else return 0;
	}
}
