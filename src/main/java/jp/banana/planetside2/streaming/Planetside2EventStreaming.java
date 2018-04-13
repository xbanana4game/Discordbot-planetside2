package jp.banana.planetside2.streaming;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import jp.banana.discordbot.BotConfig;
import jp.banana.planetside2.streaming.entity.FacilityControl;
import jp.banana.planetside2.streaming.entity.VehicleDestroy;
import jp.banana.planetside2.streaming.event.EventListener;
import jp.banana.planetside2.streaming.event.FacilityControlEvent;
import jp.banana.planetside2.streaming.event.HeartbeatEvent;
import jp.banana.planetside2.streaming.event.VehicleDestroyEvent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class Planetside2EventStreaming extends Thread {
	public Session session;
	public List<EventListener> listeners = new ArrayList<EventListener>();
	private static Logger logger = LoggerFactory.getLogger(Planetside2EventStreaming.class);
	public final static String STREAMING_URL = "wss://push.planetside2.com/streaming?environment=ps2";
	
	public void addListener(EventListener listener) {
		synchronized (listeners) {
			this.listeners.add(listener);
		}
	}
	
	public void sendCommand(String command) {
		if(session.isOpen()) {
			try {
				logger.debug("[Send]:"+command);
				session.getBasicRemote().sendText(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@OnOpen
    public void onOpen(Session session) {
		logger.debug("[Connected]");
    }
    
    @OnMessage
    public void onMessage(String message) {
//        System.err.println("[Receive]:" + message);
        logger.debug("[Receive]:" + message);
        
		JSONObject json = new JSONObject(message);
		
		String event_name = "";
		if(json.has("payload")) {
			event_name = json.getJSONObject("payload").getString("event_name");
			logger.debug("event_name: "+event_name);
		}
		
//		String eventNames = "";
//		if(json.has("subscription")) {
//			eventNames = json.getJSONObject("subscription").getJSONArray("eventNames").get(0).toString();
//		}
//		logger.debug("eventNames: "+eventNames);
		
		String type = "";
		if(json.has("type")){
			type = json.getString("type");
			logger.debug("type: "+type);
		}
		
		synchronized (listeners) {
			if(event_name.equals("VehicleDestroy")) {
				VehicleDestroy vd = parseVehicleDestroy(message);
				logger.debug(vd.toString());
				for(EventListener l:listeners) {
					if(l instanceof VehicleDestroyEvent) {
						((VehicleDestroyEvent) l).event(vd);
					}
				}
			} else if(event_name.equals("FacilityControl")) {
				FacilityControl fc = parseFacilityControl(message);
				logger.debug(fc.toString());
				for(EventListener l:listeners) {
					if(l instanceof FacilityControlEvent) {
						((FacilityControlEvent) l).event(fc);
					}
				}
			} else if(type.equals("heartbeat")){
				boolean online = parseHeartBeat("EventServerEndpoint_Connery_1", message);
				if(!online) {
					logger.error("[Receive]:heartbeat EventServerEndpoint_Connery_1:"+online);
				} else {
					logger.debug("[Receive]:heartbeat EventServerEndpoint_Connery_1:"+online);
				}
				for(EventListener l:listeners) {
					if(l instanceof HeartbeatEvent) {
						((HeartbeatEvent) l).event(online);
					}
				}
			} else {
				logger.info("[Receive]:" + message);
			}
		}
		
		logger.debug("[END]\n");
    }
    
	public void run() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {
			String serviceID = BotConfig.getSingleton().getServiceID();
			URI uri = URI.create(STREAMING_URL+"&service-id=s:"+serviceID);
			session = container.connectToServer(this,uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        while (session.isOpen()) {
            try {
				Thread.sleep(100 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            logger.debug("open");
        }
        logger.debug("end");
	}
	
	public VehicleDestroy parseVehicleDestroy(String data) {
		VehicleDestroy vd = new VehicleDestroy();
		
		JSONObject json = new JSONObject(data);
		vd.event_name = json.getJSONObject("payload").getString("event_name");
//		System.out.println("event_name is "+vd.event_name);
		if(vd.event_name.equals("VehicleDestroy")){
			vd.attacker_character_id = json.getJSONObject("payload").getString("attacker_character_id");
			vd.attacker_loadout_id = json.getJSONObject("payload").getInt("attacker_loadout_id");
			vd.attacker_vehicle_id = json.getJSONObject("payload").getInt("attacker_vehicle_id");
			vd.attacker_weapon_id = json.getJSONObject("payload").getInt("attacker_weapon_id");
			vd.character_id = json.getJSONObject("payload").getString("character_id");
			vd.event_name = json.getJSONObject("payload").getString("event_name");
			vd.facility_id = json.getJSONObject("payload").getInt("facility_id");
			vd.faction_id = json.getJSONObject("payload").getInt("faction_id");
			vd.timestamp = json.getJSONObject("payload").getString("timestamp");
			vd.vehicle_id = json.getJSONObject("payload").getInt("vehicle_id");
			vd.world_id = json.getJSONObject("payload").getInt("world_id");
			vd.zone_id = json.getJSONObject("payload").getInt("zone_id");

			vd.service = json.getString("service");
			vd.type = json.getString("type");
		} else {
			return null;
		}
	
		return vd;
	}
	
    public FacilityControl parseFacilityControl(String data) {
        JSONObject json = new JSONObject(data);
        FacilityControl fc = new FacilityControl();
        
        String event_name = json.getJSONObject("payload").getString("event_name");
        if(event_name.equals("FacilityControl")) {
        	fc.duration_held = json.getJSONObject("payload").getString("duration_held");
        	fc.event_name = json.getJSONObject("payload").getString("event_name");
            fc.facility_id = json.getJSONObject("payload").getInt("facility_id");
            fc.new_faction_id = json.getJSONObject("payload").getInt("new_faction_id");
            fc.old_faction_id = json.getJSONObject("payload").getInt("old_faction_id");
            fc.outfit_id = json.getJSONObject("payload").getString("outfit_id");
            fc.timestamp = json.getJSONObject("payload").getString("timestamp");
            fc.world_id = json.getJSONObject("payload").getInt("world_id");
            fc.zone_id = json.getJSONObject("payload").getInt("zone_id");
        }

		return fc;
	}
    
    public boolean parseHeartBeat(String endpoint, String data) {
    	JSONObject json = new JSONObject(data);
    	boolean online = Boolean.valueOf(json.getJSONObject("online").getString(endpoint));
    	return online;
    }

	@OnError
    public void onError(Throwable th) {   
    }

    @OnClose
    public void onClose(Session session) {
    	logger.debug(session.getId() + " was closed.");
    }
    
    public void closeSession() {
    	try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}