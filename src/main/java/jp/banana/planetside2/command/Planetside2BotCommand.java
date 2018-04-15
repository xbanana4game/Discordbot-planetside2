package jp.banana.planetside2.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.banana.discordbot.BotConfig;
import jp.banana.discordbot.OutputFacilityControlLog;
import jp.banana.discordbot.OutputVehicleDestroyLog;
import jp.banana.planetside2.api.Planetside2API;
import jp.banana.planetside2.entity.Datatype;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.StreamingCommandBuilder;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class Planetside2BotCommand implements MessageCreateListener {
	private static Logger logger = LoggerFactory.getLogger(Planetside2BotCommand.class);
	private boolean runningVehcleDestroy;
	private boolean runningFacility;
	private Planetside2EventStreaming client;
	public static Channel debug_channel = null;
	OutputVehicleDestroyLog vehcleDestroyListener;
	OutputFacilityControlLog facilityListener;
	private List<Channel> output_channel_list = new ArrayList<Channel>();
	
	public Planetside2BotCommand() {
		runningFacility = false;
		runningVehcleDestroy = false;
		client = new Planetside2EventStreaming();
    	try {
    		client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onMessageCreate(DiscordAPI api, Message message) {
		if (message.getContent().equalsIgnoreCase("!ps2bot ping")) {
			logger.debug("!ps2bot ping");
    		message.reply("pong Planetside2BotCommand");
    		for(Channel c:output_channel_list) {
    			c.sendMessage("pong #"+c.getName());
    		}
    	}
		
		if (message.getContent().equalsIgnoreCase("!ps2bot help")) {
			message.reply("!ps2bot outfitlog/facility/clearCommand/facility.csv/weapon.csv/datatype.csv");
			message.reply("!ps2bot help/ping/debug/stat/join/disconnect");
		}
		
    	if (message.getContent().equalsIgnoreCase("!ps2bot facility.csv")) {
    		Planetside2API.getSingleton().outputCsvFacility();
    		File f = new File("facility.csv.txt");
    		message.getChannelReceiver().sendFile(f);
    	}
    	
    	if (message.getContent().equalsIgnoreCase("!ps2bot datatype.csv")) {
    		logger.debug("!ps2bot datatype.csv");
    		List<Datatype> dt = Planetside2API.getSingleton().getDatatype();
    		Planetside2API.getSingleton().outputCsvDatatype(dt);
    		File f = new File("datatype.csv.txt");
    		message.getChannelReceiver().sendFile(f);
    	}
    	
    	if (message.getContent().equalsIgnoreCase("!ps2bot weapon.csv")) {
    		Planetside2API.getSingleton().outputCsvWeaponInfo();
    		File f = new File("weapon.csv.txt");
    		message.getChannelReceiver().sendFile(f);
    	}
		
        if (message.getContent().equalsIgnoreCase("!ps2bot outfitlog")) {
        	if(runningVehcleDestroy==true) {
        		//起動済み
        		message.reply("車両破壊ログ出力中");
        	} else {
            	runningVehcleDestroy = true;
            	vehcleDestroyListener = new OutputVehicleDestroyLog();
            	client.addListener(vehcleDestroyListener);
            	client.sendCommand(vehcleDestroyListener.getCommandText());
            	String outfitName = Planetside2API.getOutfitName(BotConfig.getSingleton().getOutfitID());
            	message.reply("車両破壊ログ出力開始 OUTFIT: "+outfitName);
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot join")) {
			if (runningVehcleDestroy||runningFacility) {
				boolean isChannelExist = false;
				Channel c = message.getChannelReceiver();
				for (Channel cc : output_channel_list) {
					if (cc.getId() == c.getId()) {
						message.reply("ps2bot is already joined");
						isChannelExist = true;
					}
				}
				// 出力チャンネル追加
				if (!isChannelExist) {
					message.reply("ps2bot join");
					output_channel_list.add(c);
					if(vehcleDestroyListener!=null) {
						vehcleDestroyListener.setChannel_list(output_channel_list);
					}
					if(facilityListener!=null) {
						facilityListener.setChannel_list(output_channel_list);
					}
				}
			}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot disconnect")) {
        	if(runningVehcleDestroy||runningFacility) {
				Channel c = message.getChannelReceiver();
				for (Channel cc : output_channel_list) {
					if (cc.getId() == c.getId()) {
						message.reply("ps2bot disconnected");
						output_channel_list.remove(cc);
						if(vehcleDestroyListener!=null) {
							vehcleDestroyListener.setChannel_list(output_channel_list);
						}
						if(facilityListener!=null) {
							facilityListener.setChannel_list(output_channel_list);
						}
					}
				}
			}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot debug")) {
        	Collection<Channel> c = api.getChannels();
        	for (Iterator<Channel> i = c.iterator(); i.hasNext();) {
        		Channel cc = (Channel) i.next();
        		if(cc.getName().equals("ps2bot")) {
        			debug_channel = cc;
        			break;
        		}
        	}
        	if(debug_channel!=null) {
        		debug_channel.sendMessage("Debug Channel ON");
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot stat")) {
        	StringBuilder str = new StringBuilder();
        	if(runningVehcleDestroy) {
        		str.append("ps2bot outfitlog is running.\n");
        	} else {
        		str.append("ps2bot outfitlog is not running.\n");
        	}
        	
        	if(runningFacility) {
        		str.append("ps2bot facility is running.\n");
        	} else {
        		str.append("ps2bot facility is not running.\n");
        	}
        	
        	for(Channel c:output_channel_list) {
        		str.append("output_channel_list: "+c.getName()+"\n");
        	}
        	message.reply(str.toString());
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot clearCommand")) {
        	if(runningVehcleDestroy) {
        		String command = new StreamingCommandBuilder().clearCommand().build();
        		client.sendCommand(command);
//        		runningVehcleDestroy = false;
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot echo")) {
        	if(runningVehcleDestroy) {
//        		String command = StreamingCommandBuilder.echoCommand();
        		StreamingCommandBuilder sc = new StreamingCommandBuilder().help();
        		String command = sc.build();
        		System.out.println(command);
        		client.sendCommand(command);
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot facility")) {
        	if(runningFacility==true) {
        		//起動済み
        		message.reply("拠点占拠ログ出力中");
        	} else {
        		runningFacility = true;
            	message.reply("拠点占拠ログ出力開始");
            	facilityListener = new OutputFacilityControlLog();
            	client.addListener(facilityListener);
            	client.sendCommand(facilityListener.getCommandText());
        	}
        }
	}

}
