package jp.banana.discordbot;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.ClientEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.banana.planetside2.api.Planetside2API;
import jp.banana.planetside2.command.Planetside2BotCommand;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.StreamingCommandBuilder;
import jp.banana.planetside2.streaming.StreamingCommandBuilder.EVENTNAME;
import jp.banana.planetside2.streaming.entity.VehicleDestroy;
import jp.banana.planetside2.streaming.event.VehicleDestroyEvent;
import de.btobastian.javacord.entities.Channel;

public class OutputVehicleDestroyLog implements VehicleDestroyEvent {
	private static Logger log = LoggerFactory.getLogger(OutputVehicleDestroyLog.class);
	public List<Channel> channel_list = new ArrayList<Channel>();
	
	public OutputVehicleDestroyLog() {
	}
	
	public List<Channel> getChannel_list() {
		return channel_list;
	}

	public void setChannel_list(List<Channel> channel_list) {
		this.channel_list = channel_list;
	}
	
	public void addChannel(Channel c) {
		this.channel_list.add(c);
	}

	public String getCommandText() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder();
		sc = sc.addEventNames(EVENTNAME.VehicleDestroy);
		sc = sc.addCharacters("all");
		sc = sc.addWorlds(1);
		String command = sc.build();
		return command;
	}
	
	private boolean isOutput(VehicleDestroy vd) {
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

	public void event(VehicleDestroy vd) {
        if(isOutput(vd)==false) {
        	return;
        }
        
        boolean jvsg2 = Planetside2API.getSingleton().isJVSGMember(vd.character_id);
        String outputMSG = "";
        if(jvsg2==false) {
        	outputMSG = getOutputMsg(vd);
        } else {
        	outputMSG = getOutputMsgDestroyed(vd);
        }
        
    	for(Channel c:channel_list) {
    		c.sendMessage(outputMSG);
    	}
	}	
}
