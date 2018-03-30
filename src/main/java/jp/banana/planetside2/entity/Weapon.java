package jp.banana.planetside2.entity;

/*
 * http://census.daybreakgames.com/get/ps2:v2/item?c:limit=1000&c:lang=en
 */
public class Weapon {
	public int item_id;
	public int item_type_id;
	public int item_category_id;
	public int is_vehicle_weapon;
	public String name_en;
	public String description_en;
	public int faction_id;
	public int max_stack_size;
	public int image_set_id;
	public int image_id;
	public String image_path;
	public int is_default_attachment;
	@Override
	public String toString() {
		return "Weapon [item_id=" + item_id + ", item_type_id=" + item_type_id
				+ ", item_category_id=" + item_category_id
				+ ", is_vehicle_weapon=" + is_vehicle_weapon + ", name_en="
				+ name_en + ", description_en=" + description_en
				+ ", faction_id=" + faction_id + ", max_stack_size="
				+ max_stack_size + ", image_set_id=" + image_set_id
				+ ", image_id=" + image_id + ", image_path=" + image_path
				+ ", is_default_attachment=" + is_default_attachment + "]";
	}
	
}
