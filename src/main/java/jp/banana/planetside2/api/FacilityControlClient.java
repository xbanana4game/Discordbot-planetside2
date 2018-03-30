package jp.banana.planetside2.api;

import javax.websocket.ClientEndpoint;

import de.btobastian.javacord.entities.Channel;
import jp.banana.planetside2.entity.Faction;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.entity.FacilityControl;

@ClientEndpoint
public class FacilityControlClient extends Planetside2EventStreaming {

	private boolean connery;
	private boolean outputCaptue;
	private boolean outputVsOnly;

	public FacilityControlClient(Channel api) {
		super(api);
		connery = true;
		outputVsOnly = true;
		outputCaptue = true;
	}

	@Override
	public String setCommand() {
		String command = "";
        if(connery==true) {
            command = "{\"service\":\"event\",\"action\":\"subscribe\",\"worlds\":[\"1\"],\"eventNames\":[\"FacilityControl\"]}";
        } else {
        	command = "{\"service\":\"event\",\"action\":\"subscribe\",\"worlds\":[\"1\",\"9\",\"10\",\"11\",\"13\",\"17\",\"18\",\"19\",\"25\",\"1000\",\"1001\"],\"eventNames\":[\"FacilityControl\"]}";
        }
		return command;
	}

	@Override
	public String getOutputMsg(String message) {
    	boolean isDefend = false;
    	boolean isVS = false;
        
        FacilityControl facility_control = parseFacilityControl(message);
        
        //VSŠÖ˜A
        if((facility_control.old_faction_id==1) || (facility_control.new_faction_id==1)){
        	isVS = true;
        } else {
        	isVS = false;
        }
		
		//–h‰q
		if(facility_control.old_faction_id==facility_control.new_faction_id) {
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
        	String msg = getOutputMsg(facility_control);
        	return msg;
        } else {
        	return null;
        }
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

}
