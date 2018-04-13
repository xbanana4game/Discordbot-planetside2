package jp.banana.discordbot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.btobastian.javacord.entities.Channel;
import jp.banana.planetside2.api.Planetside2API;
import jp.banana.planetside2.entity.Faction;
import jp.banana.planetside2.streaming.StreamingCommandBuilder;
import jp.banana.planetside2.streaming.StreamingCommandBuilder.EVENTNAME;
import jp.banana.planetside2.streaming.entity.FacilityControl;
import jp.banana.planetside2.streaming.event.FacilityControlEvent;

public class OutputFacilityControlLog implements FacilityControlEvent {
	private static Logger log = LoggerFactory.getLogger(OutputFacilityControlLog.class);
	private boolean outputCaptue;
	private boolean outputVsOnly;
	public List<Channel> channel_list = new ArrayList<Channel>();

	public OutputFacilityControlLog() {
		outputVsOnly = true;
		outputCaptue = true;
	}
	public OutputFacilityControlLog(boolean VsOnly, boolean DefendOnly) {
		outputVsOnly = VsOnly;
		outputCaptue = DefendOnly;
	}
	public String getCommandText() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder().addEventNames(EVENTNAME.FacilityControl);
		sc = sc.addWorlds(1);
		String command = sc.build();
		return command;
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
	        	log.info("OutfitName not Found.");
	        	return "";
	        }
        	
        	facility_name = Planetside2API.getFacilityName(String.valueOf(facility_control.facility_id));
	        if(facility_name==null || facility_name.equals("")) {
	        	log.info("FacilityName not Found.");
	        	return "";
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
			return "";
		}
        log.debug("outfit_name: "+outfit_name+", "+"facility_name: "+facility_name);
        String new_faction = Faction.getFactionName(facility_control.new_faction_id);

        if(facility_control.old_faction_id==facility_control.new_faction_id) {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%sが%sを防衛", new_faction, facility_name);
        	} else {
        		outputMSG = String.format("%sの%sが%sを防衛", new_faction, outfit_name, facility_name);
        	}
        	
        } else {
        	if(outfit_name.equals("UNKNOW")) {
        		outputMSG = String.format("%sが%sを占領", new_faction, facility_name);
        	} else {
                outputMSG = String.format("%sの%sが%sを占領", new_faction, outfit_name, facility_name);
        	}
        }
        
		return outputMSG;
	}

	public void event(FacilityControl fc) {
    	boolean isDefend = false;
    	boolean isVS = false;
    	boolean isZone = false;
        
    	//大陸
    	if(fc.zone_id<10) {
    		//2:Indar 4:Hossin 6:Amerish 8:Esamir
    		log.debug(Planetside2API.getZoneName(fc.zone_id));
    		isZone = true;
    	} else {
    		log.debug("Unknown Zone");
    		isZone = false;
    		return;
    	}
    	
        //VS関連
        if((fc.old_faction_id==1) || (fc.new_faction_id==1)){
        	log.debug("Faction VS");
        	isVS = true;
        } else {
        	log.debug("Faction Not VS");
        	isVS = false;
        }
		
		//防衛
		if(fc.old_faction_id==fc.new_faction_id) {
			log.debug("Defend");
			isDefend = true;
		} else {
			log.debug("Capture");
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
		if(!isZone) {
			is_output = false;
		}

		//メッセージ出力
        if(is_output) {
        	String msg = getOutputMsg(fc);
        	log.info(msg);
        	for(Channel c:channel_list) {
        		c.sendMessage(msg);
        	}
        	return;
        } else {
        	return;
        }
	}
	
	public void setChannel_list(List<Channel> channel_list) {
		this.channel_list = channel_list;
	}

}
