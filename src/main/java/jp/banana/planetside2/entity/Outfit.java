package jp.banana.planetside2.entity;

import java.util.ArrayList;
import java.util.List;

/*
 * http://census.daybreakgames.com/get/ps2/outfit?outfit_id=37549802960998499
 */
public class Outfit {
	public String outfit_id;
	public String name;
	public String name_lower;
	public String alias;
	public String alias_lower;
	public String time_created;
	public String time_created_date;
	public String leader_character_id;
	public int member_count;
	public List<CharacterInfo> member_list = new ArrayList<CharacterInfo>();
	
	@Override
	public String toString() {
		return "Outfit [outfit_id=" + outfit_id + ", name=" + name
				+ ", name_lower=" + name_lower + ", alias=" + alias
				+ ", alias_lower=" + alias_lower + ", time_created="
				+ time_created + ", time_created_date=" + time_created_date
				+ ", leader_character_id=" + leader_character_id
				+ ", member_count=" + member_count + "]";
	}
}
