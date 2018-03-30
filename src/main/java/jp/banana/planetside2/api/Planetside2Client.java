package jp.banana.planetside2.api;
import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import jp.banana.planetside2.entity.FacilityControl;
import jp.banana.planetside2.entity.Faction;

import de.btobastian.javacord.entities.Channel;

/**
 * Websocket Endpoint implementation class WebSocketClientMain
 */

@ClientEndpoint
public class Planetside2Client extends Thread {
	Channel api;
	Session session;
	

    boolean connery = true;
    public boolean outputVsOnly = true;
    public boolean outputCaptue = true;
    
    

    public Planetside2Client(Channel api) {
        super();
    	this.api = api;
    }

    @OnOpen
    public void onOpen(Session session) {
        /* セッション確立時の処理 */
        System.err.println("[セッション確立]");
    }


    
    @OnMessage
    public void onMessage(String message) {
    	boolean isDefend = false;
    	boolean isVS = false;
    	
        /* メッセージ受信時の処理 */
        System.err.println("[受信]:" + message);
        
        FacilityControl facility_control = Planetside2API.parseFacilityControl(message);
        System.out.println(facility_control);
        //DEBUG
        //api.sendMessage(facility_control.toString());
        
//        if(facility_control.outfit_id.equals("0")) {
//        	return;
//        }
        
        //VS関連
        if((facility_control.old_faction_id==1) || (facility_control.new_faction_id==1)){
        	isVS = true;
        } else {
        	isVS = false;
        }
		
		//防衛
		if(facility_control.old_faction_id==facility_control.new_faction_id) {
			isDefend = true;
		} else {
			isDefend = false;
		}
		
		//出力判定
		boolean is_output = true;
		if(outputCaptue==true) {
			if(isDefend==true) {
				is_output = false;
			}
		}
		if(outputVsOnly==true && isVS==false) {
			if(isVS==false) {
				is_output = false;
			}
		}

		//メッセージ出力
        if(is_output) {
            if(api!=null) {        	
//            	api.sendMessage(facility_control.toString());
            	String msg = getOutputMsg(facility_control);
            	if(msg.equals("")) {
            		return;
            	}
            	api.sendMessage(msg);
            }
        } else {
        	return;
        }
        
    }

    /**
     * メッセージ作成
     * @param facility_control
     * @return
     */
	private String getOutputMsg(FacilityControl facility_control) {
		String outputMSG = "";
		String outfit_name = "";
		String facility_name = "";
		
        try {
        	outfit_name = Planetside2API.getOutfitName(facility_control.outfit_id);
	        if(outfit_name==null || outfit_name.equals("")) {
	        	return "";
	        }
        	
        	facility_name = Planetside2API.getFacilityName(String.valueOf(facility_control.facility_id));
	        if(facility_name==null || facility_name.equals("")) {
	        	return "";
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
			return "";
		}
        System.out.println("outfit_name: "+outfit_name+", "+"facility_name: "+facility_name);
        
        
        
        //String old_faction = Faction.getFactionName(facility_control.old_faction_id);
        String new_faction = Faction.getFactionName(facility_control.new_faction_id);

        if(facility_control.old_faction_id==facility_control.new_faction_id) {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%sが%sを防衛\n", new_faction, facility_name);
        	} else {
        		outputMSG = String.format("%sの%sが%sを防衛\n", new_faction, outfit_name, facility_name);
        	}
        	
        } else {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%sが%sを占領\n", new_faction, facility_name);
        	} else {
                outputMSG = String.format("%sの%sが%sを占領\n", new_faction, outfit_name, facility_name);
        	}
        }
        
		return outputMSG;
	}

	@OnError
    public void onError(Throwable th) {
        /* エラー発生時の処理 */
    }

    @OnClose
    public void onClose(Session session) {
        /* セッション解放時の処理 */
    	System.out.println(session.getId() + " was closed.");
    }

    public void run() {

        // 初期化のため WebSocket コンテナのオブジェクトを取得する
        WebSocketContainer container = ContainerProvider
                .getWebSocketContainer();
        // サーバー・エンドポイントの URI
        URI uri = URI
                .create("wss://push.planetside2.com/streaming?environment=ps2&service-id=s:discordbot");
        // サーバー・エンドポイントとのセッションを確立する
        try {
			session = container.connectToServer(new Planetside2Client(api),
			        uri);
		} catch (DeploymentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String command = "";
        //Connery
        if(connery==true) {
            command = "{\"service\":\"event\",\"action\":\"subscribe\",\"worlds\":[\"1\"],\"eventNames\":[\"FacilityControl\"]}";
        } else {
        	command = "{\"service\":\"event\",\"action\":\"subscribe\",\"worlds\":[\"1\",\"9\",\"10\",\"11\",\"13\",\"17\",\"18\",\"19\",\"25\",\"1000\",\"1001\"],\"eventNames\":[\"FacilityControl\"]}";
        }
        
        try {
			session.getBasicRemote().sendText(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        while (session.isOpen()) {
            try {
				Thread.sleep(100 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.err.println("open");
        }
        System.err.println("end");
    }
    
    public void closeSession() {
    	try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}