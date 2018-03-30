package jp.banana.planetside2.entity;

/*
 * http://census.daybreakgames.com/get/ps2:v2/character/?character_id=5428010618020694593
 */
public class CharacterInfo {
	public String character_id;
	public String name_first;

	@Override
	public String toString() {
		return "CharacterInfo [character_id=" + character_id + ", name_first="
				+ name_first + "]";
	}
}
