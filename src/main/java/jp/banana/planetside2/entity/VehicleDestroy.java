package jp.banana.planetside2.entity;

import org.json.JSONObject;

/*
 * 
 */
public class VehicleDestroy {
/*
 {"payload":{"attacker_character_id":"5428013610397688977","attacker_loadout_id":"12",
 "attacker_vehicle_id":"10","attacker_weapon_id":"5209","character_id":"5428123302631199761",
 "event_name":"VehicleDestroy","facility_id":"0","faction_id":"1","timestamp":"1521340406",
 "vehicle_id":"12","world_id":"17","zone_id":"4"},"service":"event","type":"serviceMessage"}
 */
	public String attacker_character_id;
	public int attacker_loadout_id;
	public int attacker_vehicle_id;
	public int attacker_weapon_id;
	public String character_id;
	public String event_name;
	public int facility_id;
	public int faction_id;
	public String timestamp;
	public int vehicle_id;
	public int world_id;
	public int zone_id;
	public String service;
	public String type;
	
	public static VehicleDestroy parseVehicleDestroy(String data) {
		VehicleDestroy vd = new VehicleDestroy();
		
		JSONObject json = new JSONObject(data);
		vd.event_name = json.getJSONObject("payload").getString("event_name");
//		System.out.println("event_name is "+vd.event_name);
		if(vd.event_name.equals("VehicleDestroy")){
			vd.attacker_character_id = json.getJSONObject("payload").getString("attacker_character_id");
			vd.attacker_loadout_id = json.getJSONObject("payload").getInt("attacker_loadout_id");
			vd.attacker_vehicle_id = json.getJSONObject("payload").getInt("attacker_vehicle_id");
			vd.attacker_weapon_id = json.getJSONObject("payload").getInt("attacker_weapon_id");
			vd.character_id = json.getJSONObject("payload").getString("character_id");
			vd.event_name = json.getJSONObject("payload").getString("event_name");
			vd.facility_id = json.getJSONObject("payload").getInt("facility_id");
			vd.faction_id = json.getJSONObject("payload").getInt("faction_id");
			vd.timestamp = json.getJSONObject("payload").getString("timestamp");
			vd.vehicle_id = json.getJSONObject("payload").getInt("vehicle_id");
			vd.world_id = json.getJSONObject("payload").getInt("world_id");
			vd.zone_id = json.getJSONObject("payload").getInt("zone_id");

			vd.service = json.getString("service");
			vd.type = json.getString("type");
		} else {
			return null;
		}
	
		return vd;
	}

	@Override
	public String toString() {
		return "VehicleDestroy [attacker_character_id=" + attacker_character_id
				+ ", attacker_loadout_id=" + attacker_loadout_id
				+ ", attacker_vehicle_id=" + attacker_vehicle_id
				+ ", attacker_weapon_id=" + attacker_weapon_id
				+ ", character_id=" + character_id + ", event_name="
				+ event_name + ", facility_id=" + facility_id + ", faction_id="
				+ faction_id + ", timestamp=" + timestamp + ", vehicle_id="
				+ vehicle_id + ", world_id=" + world_id + ", zone_id="
				+ zone_id + ", service=" + service + ", type=" + type + "]";
	}
	
	
}
