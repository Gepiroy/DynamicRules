package MinigameToys;

public enum GameCondition {
	//PvP in overworld, PvP everywhere, hunger decreasing, etc.
	//Сразу несколько вещей могут толкать правило: игра сама по себе запрещает pvp в об. мире, но 
	//ивент должен сиё чудо разрешить. Тогда это кондиция "разрешить пвп в мире" - хоть что-то разрешит => збс.
	//Булеан не пройдёт, опасно.
	//public final String condition;
	
	//public GameCondition(String condition){
	//	this.condition=condition;
	//}
	
	PvP_overworld;
}
