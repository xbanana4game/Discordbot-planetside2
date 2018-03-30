package jp.banana.planetside2.entity;

public class Faction {
	public static String vs_long = "Vanu Sovereignty";
	public static String tr_long = "Terran Republic";
	public static String nc_long = "New Conglomerate";
	public static String vs = "VS";
	public static String tr = "TR";
	public static String nc = "NC";
	public int faction_id;
	
	public static String getFactionName(int id) {
		String faction = "";
		switch(id) {
		case 1:
			faction = vs;
			break;
		case 2:
			faction = nc;
			break;
		case 3:
			faction = tr;
			break;
		default:
				break;
		}
		return faction;
	}
	
	public static String getFactionNameLong(int id) {
		String faction = "";
		switch(id) {
		case 1:
			faction = vs_long;
			break;
		case 2:
			faction = nc_long;
			break;
		case 3:
			faction = tr_long;
			break;
		default:
				break;
		}
		return faction;
	}
}
