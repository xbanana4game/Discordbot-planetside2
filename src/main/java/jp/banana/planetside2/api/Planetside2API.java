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
import jp.banana.planetside2.entity.Datatype;
import jp.banana.planetside2.entity.Facility;
import jp.banana.planetside2.entity.Outfit;
import jp.banana.planetside2.entity.Vehicle;
import jp.banana.planetside2.entity.Weapon;


public class Planetside2API {
	private static Logger log = LoggerFactory.getLogger(Planetside2API.class);
	private static Planetside2API singleton = new Planetside2API();
	public Outfit outfit = new Outfit();
	public List<Vehicle> vehicle;
	public List<Facility> facility;
	
	private Planetside2API() {
		super();
		init();
	}
	
	public void init() {
		log.debug("Planetside2API INIT");
		getVehicleInfo();
		getOutfitMember();
		getWeaponInfo();
		getFacilityData();
	}
	
	public static Planetside2API getSingleton() {
		return singleton;
	}
	
	public static String getAPIString(String page_url) {
		URL url = null;
		String line = null;
		//FIX Check [A Valid Service ID]
		log.info("GET: "+page_url);
		try {
			url = new URL(page_url);
			URLConnection conn;
			conn = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			
			if ((line = in.readLine()) == null) {
				return null;
			}
			log.debug("getAPIString() data:"+line);
		} catch (MalformedURLException e1) {
			log.error(e1.getMessage());
//			e1.printStackTrace();
		} catch (IOException e) {
			log.error(e.getMessage());
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
		
		if(id.equals("")||id==null){
			return "NULL";
		}
		
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "map_region", "facility_id="+id).build();
		String data = getAPIString(command);
		if(data==null) {
			return "";
		}
		Facility fc = parseFacility(data);
		if(fc==null) {
			return "";
		}
		if(fc.facility_type.contains("Outpost")) {
			return fc.facility_name;
		}
		return fc.facility_name+" "+fc.facility_type;
	}
	
	public void getFacilityData() {
		facility = new ArrayList<Facility>();
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "map_region").setLimit(5000).build();
		log.info("getFacilityData: "+command);
		String data = getAPIString(command);
		facility = parseFacilityList(data);
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
			log.error(ex.getMessage());
//			ex.printStackTrace();
		}
	}
	
	public static Facility parseFacility(String data) {
		Facility facility = new Facility();
		JSONObject json = new JSONObject(data);
		try {
			int returned = json.getInt("returned");
			if(returned==0) {
				log.error("Facility not Found."+data);
				return null;
			}
		} catch(Exception e) {
			log.debug(e.getMessage()+data);
			return null;
		}
		
		try {
			facility.facility_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("facility_id");
			facility.facility_name = json.getJSONArray("map_region_list").getJSONObject(0).getString("facility_name");
			facility.facility_type = json.getJSONArray("map_region_list").getJSONObject(0).getString("facility_type");
			facility.facility_type_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("facility_type_id");
			facility.location_x = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_x");
			facility.location_y = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_y");
			facility.location_z = json.getJSONArray("map_region_list").getJSONObject(0).getDouble("location_z");
			facility.map_region_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("map_region_id");
			facility.zone_id = json.getJSONArray("map_region_list").getJSONObject(0).getInt("zone_id");
		} catch(Exception e) {
			log.error(e.getMessage());
			log.info(data);
		}
		return facility;
	}
	public static List<Facility> parseFacilityList(String data) {
		List<Facility> fc = new ArrayList<Facility>();
		JSONObject json = new JSONObject(data);
		try {
			int returned = json.getInt("returned");
			log.debug("parseFacilityList returned:"+returned);
			if(returned==0) {
				return null;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
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
				log.trace(fc.toString());
			} catch (Exception e) {
				log.debug(e.getMessage()+json.getJSONArray("map_region_list").getJSONObject(i).toString());
			}
		}
		return fc;
	}
	
    
	public static String getCharacterName(String id) throws Exception {
		CharacterInfo chara = new CharacterInfo();
		
		if(id.equals("")||id==null){
			return "NULL";
		}
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "character", "character_id="+id).build();
		String data = getAPIString(command);
		chara = parseCharacter(data);
		if(chara==null) {
			return "";
		}
		return chara.name_first;
	}
	
	public static CharacterInfo getCharacterInfo(String id) {
		CharacterInfo chara = new CharacterInfo();
		
		if(id.equals("")||id==null){
			return chara;
		}
		
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "vehicle").setLimit(5000).build();
		String data = getAPIString(command);
		if(data==null) {
			return null;
		}
		chara = parseCharacter(data);
		return chara;
	}
	
	public static CharacterInfo parseCharacter(String data) {
		CharacterInfo chara = new CharacterInfo();
		
		try{
			JSONObject json = new JSONObject(data);
			chara.name_first = json.getJSONArray("character_list").getJSONObject(0).getJSONObject("name").getString("first");
			int returned = json.getInt("returned");
			log.debug("returned:"+returned);
			if(returned==0) {
				log.info("Character Not found. returned=0");
				chara = null;
			}
		} catch (Exception e) {
			log.error(e.getMessage()+data);
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
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "vehicle").setLimit(5000).setLang("en").build();
		log.info("getVehicleInfo: "+command);
		String data = getAPIString(command);
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
				log.debug(e.getMessage());
				log.debug(json.getJSONArray("vehicle_list").getJSONObject(i).toString());
//				System.err.println(e.getMessage());
//				System.err.println(json.getJSONArray("vehicle_list").getJSONObject(i).toString());
//				System.err.println(v);
//				vehicle.add(v);
			}
		}
		return vehicle;
	}
	
	public void getOutfitMember() {
		String outfitID = BotConfig.getSingleton().getOutfitID();
		String command = new ApiCommandBuilder(NAMESPACE.PS2V2, "outfit", "outfit_id="+outfitID+"&c:resolve=member").build();
		log.info("getOutfitMember: "+command);
		String data = getAPIString(command);
		if(data==null) {
			return;
		}
		outfit.member_list = parseOutfitMember(data);
	}
	

	public boolean isOutfitMember(String character_id) {
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
	public static String getOutfitName(String id) {
		if(id.equals("")||id==null||id.equals("0")){
			return "UNKNOW";
		}
		
		Outfit outfit = new Outfit();
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "outfit", "outfit_id="+id).build();
		String data = getAPIString(command);
		outfit = parseOutfit(data);
		return outfit.name;
	}
	public static Outfit parseOutfit(String data) {
		Outfit outfit = new Outfit();
		
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		log.debug("parseOutfit returned:"+returned);
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
		String command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "item").setLang("en").setLimit(5000).setStart("0").build();
		log.info("getWeaponInfo: "+command);
		String data = getAPIString(command);
		parseWeapon(data);
		
		command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "item").setLang("en").setLimit(5000).setStart("5000").build();
		log.info("getWeaponInfo: "+command);
		data = getAPIString(command);
		parseWeapon(data);
		
		command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "item").setLang("en").setLimit(5000).setStart("10000").build();
		log.info("getWeaponInfo: "+command);
		data = getAPIString(command);
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
				wp.is_vehicle_weapon = json.getJSONArray("item_list").getJSONObject(i).getInt("is_vehicle_weapon");
				if(wp.item_type_id==26||wp.is_vehicle_weapon==1) {
					wp.item_category_id = json.getJSONArray("item_list").getJSONObject(i).getInt("item_category_id");
					wp.name_en = json.getJSONArray("item_list").getJSONObject(i).getJSONObject("name").getString("en");
					wp.description_en = json.getJSONArray("item_list").getJSONObject(i).getJSONObject("description").getString("en");
					wp.image_set_id = json.getJSONArray("item_list").getJSONObject(i).getInt("image_set_id");
					wp.image_id = json.getJSONArray("item_list").getJSONObject(i).getInt("image_id");
					wp.image_path = json.getJSONArray("item_list").getJSONObject(i).getString("image_path");
					wp.is_default_attachment = json.getJSONArray("item_list").getJSONObject(i).getInt("is_default_attachment");
				
					weapon_list.add(wp);
				}
				log.trace(wp.toString());
			} catch(Exception e) {
				log.debug(e.getMessage()+json.getJSONArray("item_list").getJSONObject(i).toString());
//				weapon_list.add(wp);
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
	
	public List<Datatype> getDatatype() {
		String url = new ApiCommandBuilder(NAMESPACE.PS2).build();
		String data = getAPIString(url);
		List<Datatype> dt = parseDatatype(data);
		return dt;
	}
	
	public void outputCsvDatatype(List<Datatype> datatype_list) {
		// ファイル出力
		try {
			FileWriter fw = new FileWriter("datatype.csv.txt");
			fw.write(Datatype.csvHead()+"\n");
			for (Datatype d : datatype_list) {
				String l = Datatype.csvBody(d);
				fw.write(l+"\n");
			}
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public List<Datatype> parseDatatype(String data) {
		List<Datatype> datatype_list = new ArrayList<Datatype>();
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		log.info("parseDatatype returned:"+returned);
		if(returned==0) {
			return null;
		}
		for(int i=0;i<json.getJSONArray("datatype_list").length();i++) {
			try{
				String name = json.getJSONArray("datatype_list").getJSONObject(i).getString("name");
				boolean hidden = json.getJSONArray("datatype_list").getJSONObject(i).getBoolean("hidden");
				Object count_ = json.getJSONArray("datatype_list").getJSONObject(i).get("count");

				String count = null;
				if(count_ instanceof String) {
					count = json.getJSONArray("datatype_list").getJSONObject(i).getString("count");
				} else if(count_ instanceof Integer){
					count = String.valueOf(json.getJSONArray("datatype_list").getJSONObject(i).getInt("count"));
				}
				
				List<String> resolve_list = new ArrayList<String>();
				int resolve_list_len = json.getJSONArray("datatype_list").getJSONObject(i).getJSONArray("resolve_list").length();
				for(int j=0;j<resolve_list_len;j++) {
					resolve_list.add(json.getJSONArray("datatype_list").getJSONObject(i).getJSONArray("resolve_list").getString(j));
				}
				
				Datatype dt = new Datatype(name, hidden, count, resolve_list );
				datatype_list.add(dt);
				log.debug(i+":"+json.getJSONArray("datatype_list").getJSONObject(i).toString());
				log.debug(dt.toString());
			}
			catch(Exception e) {
				log.debug(i+":"+json.getJSONArray("datatype_list").getJSONObject(i).toString());
				log.debug(e.getMessage());
			}

		}
		return datatype_list;
	}
	
	public static String getZoneName(int zone_id) {
		switch(zone_id) {
		case 2:
			return "Indar";
		case 4:
			return "Hossin";
		case 6:
			return "Amerish";
		case 8:
			return "Esamir";
		default:
			break;
		}
		return String.valueOf(zone_id);
	}
}
