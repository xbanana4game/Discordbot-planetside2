package jp.banana.planetside2.streaming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class StreamingCommandBuilder {
	HashMap<String, String> query = new HashMap<String, String>();
	List<String> world = new ArrayList<String>();
	List<String> eventName = new ArrayList<String>();
	List<String> characters = new ArrayList<String>();
	
	public StreamingCommandBuilder() {
		query.put("service", "event");
		query.put("action", "subscribe");
	}
	/**
	 * 
	 * @param service "event"
	 * @param action "subscribe"
	 */
	public StreamingCommandBuilder(String service, String action) {
		query.put("service", service);
		query.put("action", action);
	}
	public static String echoCommand() {
		return "{\"action\":\"echo\",\"payload\":{\"test\":\"test\"},\"service\":\"event\"}";
	}
	
	public StreamingCommandBuilder clearCommand() {
		query.put("service", "event");
		query.put("action", "clearSubscribe");
		query.put("all", "true");
		return this;
	}
	
	public StreamingCommandBuilder addWorlds(int worldID) {
		world.add(String.valueOf(worldID));
		return this;
	}
	
	public enum EVENTNAME {
		FacilityControl("FacilityControl"),
		VehicleDestroy("VehicleDestroy"),
		Death("Death");
		String eventName;
		EVENTNAME(String event) {
			eventName = event;
		}
		public String getName() {
			return eventName;
		}
	};
	public StreamingCommandBuilder addEventNames(EVENTNAME event) {
		eventName.add(event.getName());
		return this;
	}
	
	public StreamingCommandBuilder addCharacters(String character) {
		characters.add(character);
		return this;
	}
	
	public StreamingCommandBuilder help() {
		query.put("service", "event");
		query.put("action", "help");
		return this;
	}
	
	public String build() {
		JSONObject json = new JSONObject();
		for(Iterator<String> it = query.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			String value = query.get(key);
			json.put(key, value);
		}
		if(world.size()!=0) {
			json.put("worlds", world);
		}
		
		if(eventName.size()!=0){
			json.put("eventNames", eventName);
		}
		
		if(characters.size()!=0){
			json.put("characters", characters);
		}
		String command = json.toString();
		return command;
	}
}
