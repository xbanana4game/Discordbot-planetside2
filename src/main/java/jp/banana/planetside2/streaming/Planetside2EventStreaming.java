package jp.banana.planetside2.streaming;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import jp.banana.planetside2.streaming.event.VehicleDestroyEvent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.btobastian.javacord.entities.Channel;


abstract public class Planetside2EventStreaming extends Thread {
	public Channel api;
	public String command;
	public Session session;
	public List<EventListener> listeners = new ArrayList<EventListener>();
	public List<Channel> channel_list = new ArrayList<Channel>();
	private static Logger logger = LoggerFactory.getLogger(Planetside2EventStreaming.class);
	
	
	public List<Channel> getChannel_list() {
		return channel_list;
	}

	public void setChannel_list(List<Channel> channel_list) {
		this.channel_list = channel_list;
	}

	public final static String STREAMING_URL = "wss://push.planetside2.com/streaming?environment=ps2";
    public Channel getApi() {
		return api;
	}
    
    abstract public String setCommand();

	public void setApi(Channel api) {
		this.api = api;
	}
	
	public void setApi(List<Channel> api) {
		this.channel_list = api;
	}
	
	public void addListener(EventListener listener) {
		synchronized (listeners) {
			this.listeners.add(listener);
		}
	}

	public Planetside2EventStreaming(Channel api) {
        super();
    	this.api = api;
    	this.command = setCommand();
    }

	@OnOpen
    public void onOpen(Session session) {
		logger.debug("[セッション確立]");
    }
    
    @OnMessage
    public void onMessage(String message) {
//        System.err.println("[Receive]:" + message);
        logger.debug("[Receive]:" + message);
        String msg = getOutputMsg(message);
        
		JSONObject json = new JSONObject(message);
		String event_name = json.getJSONObject("payload").getString("event_name");
		logger.debug("event_name: "+event_name);
		synchronized (listeners) {
			if(event_name.equals("VehicleDestroy")) {
				for(EventListener l:listeners) {
					if(l instanceof VehicleDestroyEvent) {
						VehicleDestroy vd = parseVehicleDestroy(message);
						((VehicleDestroyEvent) l).event(vd);
					}
				}
			} else if(event_name.equals("FacilityControl")) {
				for(EventListener l:listeners) {
					if(l instanceof FacilityControlEvent) {
						((FacilityControlEvent) l).event(message);
					}
				}
			} else {
				
			}
		}
        
        if(msg!=null && api!=null) {
        	 api.sendMessage(msg);
        }
        if(msg!=null && channel_list!=null) {
        	for(Channel c:channel_list) {
        		c.sendMessage(msg);
        	}
        }
    }
    
	public void run() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {
			String serviceID = BotConfig.getSingleton().getServiceID();
			URI uri = URI.create(STREAMING_URL+"&service-id=s:"+serviceID);
			session = container.connectToServer(this,uri);
			session.getBasicRemote().sendText(command);
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
	
    /**
     * メッセージ作成
     * @param facility_control
     * @return
     */
	abstract public String getOutputMsg(String message);

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