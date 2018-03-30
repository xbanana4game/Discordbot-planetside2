package jp.banana.planetside2.entity;

/*
 * http://census.daybreakgames.com/get/ps2/world?c:limit=1000&c:lang=en
 */
public class World {
	public int world_id;
	public static String getWorldName(int id) {
		String name = "";
		
		switch(id){
		case 1:
			name = "Connery";
			break;
		}
		
		return name;
	}
}
