package jp.banana.discordbot;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.ClientEndpoint;

import de.btobastian.javacord.entities.Channel;
import jp.banana.planetside2.api.Planetside2API;
import jp.banana.planetside2.entity.Faction;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.StreamingCommandBuilder;
import jp.banana.planetside2.streaming.StreamingCommandBuilder.EVENTNAME;
import jp.banana.planetside2.streaming.entity.FacilityControl;
import jp.banana.planetside2.streaming.entity.VehicleDestroy;
import jp.banana.planetside2.streaming.event.FacilityControlEvent;
import jp.banana.planetside2.streaming.event.VehicleDestroyEvent;

public class OutputFacilityControlLog implements FacilityControlEvent {
	private boolean outputCaptue;
	private boolean outputVsOnly;
	public List<Channel> channel_list = new ArrayList<Channel>();

	public OutputFacilityControlLog() {
		outputVsOnly = true;
		outputCaptue = true;
	}
	public String getCommandText() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder().addEventNames(EVENTNAME.FacilityControl);
		sc = sc.addWorlds(1);
		String command = sc.build();
		return command;
	}

    /**
     * ƒƒbƒZ[ƒWì¬
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
        String new_faction = Faction.getFactionName(facility_control.new_faction_id);

        if(facility_control.old_faction_id==facility_control.new_faction_id) {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%s‚ª%s‚ğ–h‰q\n", new_faction, facility_name);
        	} else {
        		outputMSG = String.format("%s‚Ì%s‚ª%s‚ğ–h‰q\n", new_faction, outfit_name, facility_name);
        	}
        	
        } else {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%s‚ª%s‚ğè—Ì\n", new_faction, facility_name);
        	} else {
                outputMSG = String.format("%s‚Ì%s‚ª%s‚ğè—Ì\n", new_faction, outfit_name, facility_name);
        	}
        }
        
		return outputMSG;
	}

	public void event(FacilityControl fc) {
    	boolean isDefend = false;
    	boolean isVS = false;
        
        //VSŠÖ˜A
        if((fc.old_faction_id==1) || (fc.new_faction_id==1)){
        	isVS = true;
        } else {
        	isVS = false;
        }
		
		//–h‰q
		if(fc.old_faction_id==fc.new_faction_id) {
			isDefend = true;
		} else {
			isDefend = false;
		}
		
		//o—Í”»’è
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

		//ƒƒbƒZ[ƒWo—Í
        if(is_output) {
        	String msg = getOutputMsg(fc);
        	for(Channel c:channel_list) {
        		c.sendMessage(msg);
        	}
        	return;
        } else {
        	return;
        }
	}

}
