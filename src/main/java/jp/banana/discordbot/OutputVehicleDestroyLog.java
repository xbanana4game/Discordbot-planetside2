package jp.banana.discordbot;
import java.util.ArrayList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.banana.planetside2.api.Planetside2API;
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
        
        //OUTFIT MEMBER‚Ì‚İo—Í
        boolean outfit = Planetside2API.getSingleton().isOutfitMember(vd.attacker_character_id);
        boolean outfit2 = Planetside2API.getSingleton().isOutfitMember(vd.character_id);
        log.debug("outfit: "+outfit);
        if(outfit==false&&outfit2==false) {
        	log.debug("Attacker is not Outfit");
			return false;
		}
		// TK‚Ío—Í‚µ‚È‚¢
		if (outfit == true && outfit2 == true) {
			log.debug("Attacked is Outfit");
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
        
        boolean outfit2 = Planetside2API.getSingleton().isOutfitMember(vd.character_id);
        String outputMSG = "";
        if(outfit2==false) {
        	outputMSG = getOutputMsg(vd);
        } else {
        	outputMSG = getOutputMsgDestroyed(vd);
        }
        
        log.info(outputMSG);
    	for(Channel c:channel_list) {
    		c.sendMessage(outputMSG);
    	}
	}	
}
