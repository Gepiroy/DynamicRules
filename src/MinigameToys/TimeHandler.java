package MinigameToys;

import java.util.Date;

import Utils.TextUtil;

public class TimeHandler {
	private final long from;
	public int time=0;
	
	public TimeHandler(){
		from=new Date().getTime();
	}
	
	public int time(){
		time=(int) (new Date().getTime()-from);
		return time;
	}
	
	public int time(String pre, int trigger){
		time=(int) (new Date().getTime()-from);
		if(time>=trigger)TextUtil.sdebug(pre+time);
		return time;
	}
}
