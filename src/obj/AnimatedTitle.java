package obj;

import java.util.ArrayList;
import java.util.List;

public class AnimatedTitle {
	public List<ATBlock> blocks = new ArrayList<>();
	
	public AnimatedTitle add(ATBlock b){
		blocks.add(b);
		return this;
	}
}
