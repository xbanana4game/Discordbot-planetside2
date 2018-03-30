package jp.banana.planetside2.entity;

/*
 * http://census.daybreakgames.com/get/ps2:v2/vehicle?c:limit=1000&c:lang=en
 */
public class Vehicle {
	public int vehicle_id;
	public String name_en;
	public String description_en;
	public int type_id;
	public String type_name;
	public int cost;
	public int cost_resource_id;
	public int image_set_id;
	public int image_id;
	//"https://census.daybreakgames.com"+image_path
	public String image_path;

	@Override
	public String toString() {
		return "Vehicle [vehicle_id=" + vehicle_id + ", name_en=" + name_en
				+ ", description_en=" + description_en + ", type_id=" + type_id
				+ ", type_name=" + type_name + ", cost=" + cost
				+ ", cost_resource_id=" + cost_resource_id + ", image_set_id="
				+ image_set_id + ", image_id=" + image_id + ", image_path="
				+ image_path + "]";
	}
	
	
}
