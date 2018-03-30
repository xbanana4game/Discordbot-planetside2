package jp.banana.planetside2.entity;

import java.util.List;

/**
 * http://census.daybreakgames.com/get/ps2:v2/
 *
 */
public class Datatype {
	public String name;
	public String count;
	public boolean hidden;
	public List<String> resolve_list;
	public Datatype(String name, boolean hidden, String count, List<String> resolve_list) {
		super();
		this.name = name;
		this.hidden = hidden;
		this.count = count;
		this.resolve_list = resolve_list;
	}
	@Override
	public String toString() {
		return "Datatype [name=" + name + ", count=" + count + ", hidden="
				+ hidden + ", resolve_list=" + resolve_list + "]";
	}
	
	public static String csvHead() {
		return "name,count,hidden,resolve_list";
	}
	
	public static String csvBody(Datatype d) {
		return d.name+","+d.count+","+d.hidden+",\""+d.resolve_list+"\"";
	}
}
