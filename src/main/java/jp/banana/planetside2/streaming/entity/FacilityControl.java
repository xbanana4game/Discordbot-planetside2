package jp.banana.planetside2.streaming.entity;

public class FacilityControl {
    /*
    RECEIVED: {"payload":{"duration_held":"31611","event_name":"FacilityControl",
    "facility_id":"3400","new_faction_id":"2","old_faction_id":"1","outfit_id":"37537074285186809",
    "timestamp":"1520424457","world_id":"1","zone_id":"2"},"service":"event","type":"serviceMessage"}
    */
	public String duration_held;
	public String event_name;
	public String timestamp;
	public int world_id;
	public int old_faction_id;
	public String outfit_id;
	public int new_faction_id;
	public int facility_id;
	public int zone_id;
    
	@Override
	public String toString() {
		return "FacilityControl [duration_held=" + duration_held
				+ ", event_name=" + event_name + ", timestamp=" + timestamp
				+ ", world_id=" + world_id + ", old_faction_id="
				+ old_faction_id + ", outfit_id=" + outfit_id
				+ ", new_faction_id=" + new_faction_id + ", facility_id="
				+ facility_id + ", zone_id=" + zone_id + "]";
	}
}
