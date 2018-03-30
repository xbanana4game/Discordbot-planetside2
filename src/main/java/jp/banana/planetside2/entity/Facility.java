package jp.banana.planetside2.entity;

/*
 * http://census.daybreakgames.com/get/ps2/map_region?facility_id=5100
 */
public class Facility {
	public int map_region_id;
	public int zone_id;
	public int facility_id;
	public String facility_name;
	public int facility_type_id;
	public String facility_type;
	public double location_x;
	public double location_y;
	public double location_z;
	
	@Override
	public String toString() {
		return "Facility [map_region_id=" + map_region_id + ", zone_id="
				+ zone_id + ", facility_id=" + facility_id + ", facility_name="
				+ facility_name + ", facility_type_id=" + facility_type_id
				+ ", facility_type=" + facility_type + ", location_x="
				+ location_x + ", location_y=" + location_y + ", location_z="
				+ location_z + "]";
	}

}
