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
        //�T���_���[�j��̂ݏo��
        if(Planetside2BotCommand.outputSundyOnly) {
        	if(vd.vehicle_id!=2) {
        		return false;
        	}
        }
        if(vd.vehicle_id>20) {
        	return false;
        }
        
        //JVSG MEMBER�̂ݏo��
        boolean jvsg = Planetside2API.getSingleton().isJVSGMember(vd.attacker_character_id);
        boolean jvsg2 = Planetside2API.getSingleton().isJVSGMember(vd.character_id);
        log.debug("jvsg: "+jvsg);
        if(jvsg==false&&jvsg2==false) {
        	log.debug("Attacker is not JVSG");
			return false;
		}
		// TK�͏o�͂��Ȃ�
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
	    		outputMSG = String.format("%s��%s���j�󂳂�܂���", attacked_name,vehicle_name);
	    	} else {
	    		outputMSG = String.format("%s��%s��%s�Ŕj�󂳂�܂���", attacked_name,vehicle_name,weapon_name);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}

	/**
     * ���b�Z�[�W�쐬
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
				outputMSG = String.format("%s��%s��j��", attacker_name,vehicle_name);
//				outputMSG = String.format("%s��%s��%s��j��", attacker_name,attacked_name,vehicle_name);
			} else {
				outputMSG = String.format("%s��%s��%s�Ŕj��", attacker_name,vehicle_name,weapon_name);
//				outputMSG = String.format("%s��%s��%s��%s�Ŕj��", attacker_name,attacked_name,vehicle_name,weapon_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputMSG;
	}	
}
