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
        //ƒTƒ“ƒ_ƒ‰[”j‰ó‚Ì‚İo—Í
        if(Planetside2BotCommand.outputSundyOnly) {
        	if(vd.vehicle_id!=2) {
        		return false;
        	}
        }
        if(vd.vehicle_id>20) {
        	return false;
        }
        
        //JVSG MEMBER‚Ì‚İo—Í
        boolean jvsg = Planetside2API.getSingleton().isJVSGMember(vd.attacker_character_id);
        boolean jvsg2 = Planetside2API.getSingleton().isJVSGMember(vd.character_id);
        log.debug("jvsg: "+jvsg);
        if(jvsg==false&&jvsg2==false) {
        	log.debug("Attacker is not JVSG");
			return false;
		}
		// TK‚Ío—Í‚µ‚È‚¢
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
	    		outputMSG = String.format("%s‚Ì%s‚ª”j‰ó‚³‚ê‚Ü‚µ‚½", attacked_name,vehicle_name);
	    	} else {
	    		outputMSG = String.format("%s‚Ì%s‚ª%s‚Å”j‰ó‚³‚ê‚Ü‚µ‚½", attacked_name,vehicle_name,weapon_name);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}

	/**
     * ƒƒbƒZ[ƒWì¬
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
				outputMSG = String.format("%s‚ª%s‚ğ”j‰ó", attacker_name,vehicle_name);
//				outputMSG = String.format("%s‚ª%s‚Ì%s‚ğ”j‰ó", attacker_name,attacked_name,vehicle_name);
			} else {
				outputMSG = String.format("%s‚ª%s‚ğ%s‚Å”j‰ó", attacker_name,vehicle_name,weapon_name);
//				outputMSG = String.format("%s‚ª%s‚Ì%s‚ğ%s‚Å”j‰ó", attacker_name,attacked_name,vehicle_name,weapon_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}	
}
