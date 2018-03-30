package jp.banana.planetside2.api;
import javax.websocket.ClientEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.banana.planetside2.command.Planetside2BotCommand;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.entity.VehicleDestroy;
import de.btobastian.javacord.entities.Channel;

@ClientEndpoint
public class JvsgOutputLogClient extends Planetside2EventStreaming {
	private static Logger log = LoggerFactory.getLogger(JvsgOutputLogClient.class);
	
	public JvsgOutputLogClient(Channel api) {
		super(api);
	}

	@Override
	public String setCommand() {
		return "{\"service\":\"event\",\"action\":\"subscribe\",\"worlds\":[\"1\"],\"characters\":[\"all\"],\"eventNames\":[\"VehicleDestroy\"]}";
	}

	@Override
	public String getOutputMsg(String message) {
        VehicleDestroy vd = parseVehicleDestroy(message);
        if(vd==null) {
        	System.err.println("parseVehicleDestroy error");
        	return null;
        }
        
        if(isOutput(vd)==false) {
        	return null;
        }
        
        boolean jvsg2 = Planetside2API.getSingleton().isJVSGMember(vd.character_id);
        String outputMSG = "";
        if(jvsg2==false) {
        	outputMSG = getOutputMsg(vd);
        } else {
        	outputMSG = getOutputMsgDestroyed(vd);
        }
		return outputMSG;
	}
	
	private boolean isOutput(VehicleDestroy vd) {
        //サンダラー破壊のみ出力
        if(Planetside2BotCommand.outputSundyOnly) {
        	if(vd.vehicle_id!=2) {
        		return false;
        	}
        }
        if(vd.vehicle_id>20) {
        	return false;
        }
        
        //JVSG MEMBERのみ出力
        boolean jvsg = Planetside2API.getSingleton().isJVSGMember(vd.attacker_character_id);
        boolean jvsg2 = Planetside2API.getSingleton().isJVSGMember(vd.character_id);
        log.debug("jvsg: "+jvsg);
        if(jvsg==false&&jvsg2==false) {
        	log.debug("Attacker is not JVSG");
			return false;
		}
		// TKは出力しない
		if (jvsg == true && jvsg2 == true) {
			log.debug("Attacked is JVSG");
			return false;
		}

		return true;
	}

    private String getOutputMsgDestroyed(VehicleDestroy vd) {
    	String outputMSG = "";
    	try {
	    	String attacked_name = Planetside2API.getCharacterName(vd.character_id);
//	    	String attacker_name = Planetside2API.getCharacterName(vd.attacker_character_id);
	    	String vehicle_name = Planetside2API.getSingleton().getVehicleName(vd.vehicle_id);
	    	String weapon_name = Planetside2API.getSingleton().getWeaponName(vd.attacker_weapon_id);
	    	if(weapon_name==null) {
	    		outputMSG = String.format("%sの%sが破壊されました", attacked_name,vehicle_name);
	    	} else {
	    		outputMSG = String.format("%sの%sが%sで破壊されました", attacked_name,vehicle_name,weapon_name);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}

	/**
     * メッセージ作成
     * @param facility_control
     * @return
     */
	private String getOutputMsg(VehicleDestroy vd) {
		String outputMSG = "";
		try {
			String attacker_name = Planetside2API.getCharacterName(vd.attacker_character_id);
//			String attacked_name = Planetside2API.getCharacterName(vd.character_id);
			String vehicle_name = Planetside2API.getSingleton().getVehicleName(vd.vehicle_id);
			String weapon_name = Planetside2API.getSingleton().getWeaponName(vd.attacker_weapon_id);
			if(weapon_name==null) {
				outputMSG = String.format("%sが%sを破壊", attacker_name,vehicle_name);
//				outputMSG = String.format("%sが%sの%sを破壊", attacker_name,attacked_name,vehicle_name);
			} else {
				outputMSG = String.format("%sが%sを%sで破壊", attacker_name,vehicle_name,weapon_name);
//				outputMSG = String.format("%sが%sの%sを%sで破壊", attacker_name,attacked_name,vehicle_name,weapon_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}	
}
