package jp.banana.planetside2.api;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.banana.discordbot.BotConfig;
import jp.banana.planetside2.command.ApiCommandBuilder;
import jp.banana.planetside2.command.ApiCommandBuilder.NAMESPACE;
import jp.banana.planetside2.entity.CharacterInfo;
import jp.banana.planetside2.entity.Facility;
import jp.banana.planetside2.entity.Outfit;
import jp.banana.planetside2.entity.Vehicle;
import jp.banana.planetside2.entity.Weapon;
import jp.banana.planetside2.streaming.entity.FacilityControl;

public class Planetside2API {
	private static Planetside2API singleton = new Planetside2API();
	public Outfit outfit = new Outfit();
	public List<Vehicle> vehicle;
	public List<Facility> facility;
	private static Logger log;
	
	private Planetside2API() {
		super();
		log = LoggerFactory.getLogger(Planetside2API.class);
		log.debug("Planetside2API INIT");
		getVehicleInfo();
		getJVSGMember();
		getWeaponInfo();
		getFacilityData();
	}
	public static Planetside2API getSingleton() {
		return singleton;
	}
	
	public static String getAPIString(String page_url) {
		URL url = null;
		String line = null;
		try {
			url = new URL(page_url);
			URLConnection conn;
			conn = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			
			if ((line = in.readLine()) == null) {
				return null;
//				System.out.println(line);
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	/**
	 * 拠点の名前取得
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String getFacilityName(String id) throws Exception {
		Facility fc = new Facility();
		
		if(id.equals("")||id==null){
			return "NULL";
		}
		
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "map_region", "facility_id="+id);
		URL url = new URL(command.build());
		URLConnection conn = url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
//			System.out.println(line);
			fc = parseFacility(line);
			if(fc==null) {
				return "";
			}
//			System.out.println(fc);
		}
		
		if(fc.facility_type.contains("Outpost")) {
			return fc.facility_name;
		}
		return fc.facility_name+" "+fc.facility_type;
	}
	
	public void getFacilityData() {
		facility = new ArrayList<Facility>();
		
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "map_region").setLimit(5000);
		URL url = null;
		try {
			url = new URL(command.build());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = in.readLine()) != null) {
//				System.out.println(line);
				facility = parseFacilityList(line);
//				System.out.println(facility.size());
//				for(Facility fc : facility) {
//					System.out.println(fc);
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void outputCsvFacility() {
		// ファイル出力
		try {
			FileWriter fw = new FileWriter("facility.csv.txt");
			fw.write("map_region_id,zone_id,facility_id,facility_name,facility_type_id,facility_type,location_x,location_y,location_z\n");
			for (Facility fc : facility) {
				fw.write(fc.map_region_id+",");
				fw.write(fc.zone_id+",");
				fw.write(fc.facility_id+",");
				fw.write("\""+fc.facility_name+"\",");
				fw.write(fc.facility_type_id+",");
				fw.write(fc.facility_type+",");
				fw.write(fc.location_x+",");
				fw.write(fc.location_y+",");
				fw.write(fc.location_z+",");
				fw.write("\n");
			}
			fw.close();
		} catch (IOException ex) {
			log.debug(ex.getMessage());
//			ex.printStackTrace();
		}
	}
	
	public static Facility parseFacility(String data) {
		Facility facility = new Facility();
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		if(returned==0) {
			return null;
		}
		facility.facility_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("facility_id");
		facility.facility_name = json.getJSONArray("map_region_list").getJSONObject(0).getString("facility_name");
		facility.facility_type = json.getJSONArray("map_region_list").getJSONObject(0).getString("facility_type");
		facility.facility_type_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("facility_type_id");
		facility.location_x = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_x");
		facility.location_y = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_y");
		facility.location_z = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_z");
		facility.map_region_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("map_region_id");
		facility.zone_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("zone_id");
		return facility;
	}
	public static List<Facility> parseFacilityList(String data) {
		List<Facility> fc = new ArrayList<Facility>();
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		if(returned==0) {
			return null;
		}
		for(int i=0;i<json.getJSONArray("map_region_list").length();i++) {			
			try {
				Facility facility = new Facility();
				facility.facility_id = json.getJSONArray("map_region_list").getJSONObject(i).getInt("facility_id");
				facility.facility_name = json.getJSONArray("map_region_list").getJSONObject(i).getString("facility_name");
				facility.facility_type = json.getJSONArray("map_region_list").getJSONObject(i).getString("facility_type");
				facility.facility_type_id = json.getJSONArray("map_region_list").getJSONObject(i).getInt("facility_type_id");
				facility.location_x = json.getJSONArray("map_region_list").getJSONObject(i).getDouble("location_x");
				facility.location_y = json.getJSONArray("map_region_list").getJSONObject(i).getDouble("location_y");
				facility.location_z = json.getJSONArray("map_region_list").getJSONObject(i).getDouble("location_z");
				facility.map_region_id = json.getJSONArray("map_region_list").getJSONObject(i).getInt("map_region_id");
				facility.zone_id = json.getJSONArray("map_region_list").getJSONObject(i).getInt("zone_id");
				fc.add(facility);
			} catch (Exception e) {
				log.info(e.getMessage());
				log.info(json.getJSONArray("map_region_list").getJSONObject(i).toString());
//				System.err.println(e);
			}
		}
		return fc;
	}
	
    
	public static String getCharacterName(String id) throws Exception {
		CharacterInfo chara = new CharacterInfo();
		
		if(id.equals("")||id==null){
			return "NULL";
		}
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "character", "character_id="+id);
		URL url = new URL(command.build());
		URLConnection conn = url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
//			System.out.println(line);
			chara = parseCharacter(line);
			if(chara==null) {
				return "";
			}
//			System.out.println(chara);
		}
		return chara.name_first;
	}
	
	public static CharacterInfo getCharacterInfo(String id) {
		CharacterInfo chara = new CharacterInfo();
		
		if(id.equals("")||id==null){
			return chara;
		}
		
		String data = getAPIString("http://census.daybreakgames.com/get/ps2:v2/vehicle?c:limit=1000");
		if(data==null) {
			return null;
		}
		chara = parseCharacter(data);
		return chara;
	}
	
	public static CharacterInfo parseCharacter(String data) {
		CharacterInfo chara = new CharacterInfo();
		
		JSONObject json = new JSONObject(data);
		chara.name_first = json.getJSONArray("character_list").getJSONObject(0).getJSONObject("name").getString("first");
		int returned = json.getInt("returned");
		if(returned==0) {
			System.out.println("Character Not found. returned=0");
			chara = null;
		}
		return chara;
	}
	
	
	public String getVehicleName(int id) {
		String name = "";
		for(Vehicle l:vehicle) {
//			System.out.println(l);
			if(l.vehicle_id==id) {
				name = l.name_en;
			}
		}
		return name;
	}
	
	public void getVehicleInfo() {
		log.debug("getVehicleInfo");
		//http://census.daybreakgames.com/get/ps2:v2/vehicle?c:limit=1000
		String data = getAPIString("http://census.daybreakgames.com/get/ps2:v2/vehicle?c:limit=1000&c:lang=en");
		if(data==null) {
			return;
		}
		vehicle = parseVehicleList(data);
	}
	
	public static List<Vehicle> parseVehicleList(String data) {
		List<Vehicle> vehicle = new ArrayList<Vehicle>();

		JSONObject json = new JSONObject(data);

		for (int i = 0; i < json.getJSONArray("vehicle_list").length(); i++) {
			Vehicle v = new Vehicle();
			try {
				
				v.vehicle_id = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("vehicle_id");
				v.name_en = json.getJSONArray("vehicle_list").getJSONObject(i).getJSONObject("name").getString("en");
				v.description_en = json.getJSONArray("vehicle_list").getJSONObject(i).getJSONObject("description").getString("en");
				v.type_id = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("type_id");
				v.type_name = json.getJSONArray("vehicle_list").getJSONObject(i).getString("type_name");
				v.cost = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("cost");
				v.cost_resource_id = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("cost_resource_id");
				v.image_set_id = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("image_set_id");
				v.image_id = json.getJSONArray("vehicle_list").getJSONObject(i).getInt("image_id");
				v.image_path = json.getJSONArray("vehicle_list").getJSONObject(i).getString("image_path");
				
				vehicle.add(v);
				//System.out.println(v);
			} catch (Exception e) {
//				e.printStackTrace();
				log.info(e.getMessage());
				log.info(json.getJSONArray("vehicle_list").getJSONObject(i).toString());
//				System.err.println(e.getMessage());
//				System.err.println(json.getJSONArray("vehicle_list").getJSONObject(i).toString());
//				System.err.println(v);
//				vehicle.add(v);
			}
		}
		return vehicle;
	}
	
	public void getJVSGMember() {
		String outfitID = BotConfig.getSingleton().getOutfitID();
		//JVSG
		ApiCommandBuilder command = new ApiCommandBuilder(NAMESPACE.PS2V2, "outfit", "outfit_id="+outfitID+"&c:resolve=member");
		String data = getAPIString(command.build());
		if(data==null) {
			return;
		}
		outfit.member_list = parseOutfitMember(data);
	}
	

	public boolean isJVSGMember(String character_id) {
		for(CharacterInfo c:outfit.member_list) {
			if(c.character_id.equals(character_id)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * アウトフィトの名前取得
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String getOutfitName(String id) throws Exception {
		if(id.equals("")||id==null||id.equals("0")){
			return "UNKNOW";
		}
		
		Outfit outfit = new Outfit();
		URL url = new URL("http://census.daybreakgames.com/get/ps2/outfit?outfit_id="+id);
		URLConnection conn = url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
//			System.out.println(line);
			outfit = parseOutfit(line);
//			System.out.println(outfit);
		}
		return outfit.name;
	}
	public static Outfit parseOutfit(String data) {
		Outfit outfit = new Outfit();
		
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		if(returned==0){
			return null;
		}
		outfit.alias = json.getJSONArray("outfit_list").getJSONObject(0).getString("alias");
		outfit.alias_lower = json.getJSONArray("outfit_list").getJSONObject(0).getString("alias_lower");
		outfit.leader_character_id = json.getJSONArray("outfit_list").getJSONObject(0).getString("leader_character_id");
		outfit.member_count = json.getJSONArray("outfit_list").getJSONObject(0).getInt("member_count");
		outfit.name = json.getJSONArray("outfit_list").getJSONObject(0).getString("name");
		outfit.name_lower = json.getJSONArray("outfit_list").getJSONObject(0).getString("name_lower");
		outfit.outfit_id = json.getJSONArray("outfit_list").getJSONObject(0).getString("outfit_id");
		outfit.time_created = json.getJSONArray("outfit_list").getJSONObject(0).getString("time_created");
		outfit.time_created_date = json.getJSONArray("outfit_list").getJSONObject(0).getString("time_created_date");
				
		return outfit;
	}
	
	public List<CharacterInfo> parseOutfitMember(String data) {
		List<CharacterInfo> member = new ArrayList<CharacterInfo>();
		JSONObject json = new JSONObject(data);
		for (int i = 0; i < json.getJSONArray("outfit_list").getJSONObject(0)
				.getJSONArray("members").length(); i++) {
			try {
				CharacterInfo c = new CharacterInfo();
				c.character_id = json.getJSONArray("outfit_list").getJSONObject(0).getJSONArray("members").getJSONObject(i).getString("character_id");
//				c.name_first = Planetside2API.getCharacterName(c.character_id);
				member.add(c);
//				System.out.println(c);
			} catch (Exception e) {
				System.err.println(json.getJSONArray("outfit_list").getJSONObject(0).getJSONArray("members").getJSONObject(i).toString());
			}
		}
		return member;
	}
	
	public static String getOutfitMember(String outfit_id) {
		return null;
	}
	
	public void getWeaponInfo() {
		String data = getAPIString("http://census.daybreakgames.com/get/ps2:v2/item?c:limit=5000&c:lang=en&c:start=0");
		parseWeapon(data);
		data = getAPIString("http://census.daybreakgames.com/get/ps2:v2/item?c:limit=5000&c:lang=en&c:start=5000");
		parseWeapon(data);
		data = getAPIString("http://census.daybreakgames.com/get/ps2:v2/item?c:limit=5000&c:lang=en&c:start=10000");
		parseWeapon(data);
	}
	
	public List<Weapon> weapon_list = new ArrayList<Weapon>();
	public void parseWeapon(String data) {
		JSONObject json = new JSONObject(data);
		for(int i=0;i<json.getJSONArray("item_list").length();i++) {
			Weapon wp = new Weapon();
			try {
				wp.item_id = json.getJSONArray("item_list").getJSONObject(i).getInt("item_id");
				wp.item_type_id = json.getJSONArray("item_list").getJSONObject(i).getInt("item_type_id");
				wp.item_category_id = json.getJSONArray("item_list").getJSONObject(i).getInt("item_category_id");
				wp.is_vehicle_weapon = json.getJSONArray("item_list").getJSONObject(i).getInt("is_vehicle_weapon");
				wp.name_en = json.getJSONArray("item_list").getJSONObject(i).getJSONObject("name").getString("en");
				wp.description_en = json.getJSONArray("item_list").getJSONObject(i).getJSONObject("description").getString("en");
				wp.image_set_id = json.getJSONArray("item_list").getJSONObject(i).getInt("image_set_id");
				wp.image_id = json.getJSONArray("item_list").getJSONObject(i).getInt("image_id");
				wp.image_path = json.getJSONArray("item_list").getJSONObject(i).getString("image_path");
				wp.is_default_attachment = json.getJSONArray("item_list").getJSONObject(i).getInt("is_default_attachment");
				if(wp.item_type_id==26||wp.is_vehicle_weapon==1) {
					weapon_list.add(wp);
				}
//				System.out.println(wp);
			} catch(Exception e) {
				log.info(e.getMessage());
				log.info(json.getJSONArray("item_list").getJSONObject(i).toString());
//				System.err.println(e.getMessage());
//				weapon_list.add(wp);
//				System.err.println(json.getJSONArray("item_list").getJSONObject(i).toString());
			}
		}
		
	}
	
	public String getWeaponName(int weapon_id) {
		String name = null;
		for(Weapon w:weapon_list) {
			if(w.item_id==weapon_id){
				name = w.name_en;
				return name;
			}
		}
		return name;
	}
	
	public void outputCsvWeaponInfo() {
		// ファイル出力
		try {
			FileWriter fw = new FileWriter("weapon.csv.txt");
			fw.write("item_id,item_type_id,is_vehicle_weapon,name,description,image_set_id,image_id,image_path,is_default_attachment\n");
			for (Weapon wp : weapon_list) {
				fw.write(wp.item_id+",");
				fw.write(wp.item_type_id+",");
				fw.write(wp.is_vehicle_weapon+",");
				fw.write(wp.name_en+",");
				fw.write("\""+wp.description_en+"\",");
				fw.write(wp.image_set_id+",");
				fw.write(wp.image_id+",");
				fw.write(wp.image_path+",");
				fw.write(wp.is_default_attachment);
				fw.write("\n");
			}
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
