package jp.banana.planetside2.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jp.banana.planetside2.api.JvsgOutputLogClient;
import jp.banana.planetside2.api.Planetside2API;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import jp.banana.planetside2.streaming.StreamingCommandBuilder;
import jp.banana.planetside2.streaming.entity.VehicleDestroy;
import jp.banana.planetside2.streaming.event.VehicleDestroyEvent;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class Planetside2BotCommand implements MessageCreateListener {

	private boolean running;
	private Channel ouput_channel;
	private Planetside2EventStreaming client;
	private Channel debug_channel = null;
	private List<Channel> output_channel_list = new ArrayList<Channel>();
	public static boolean outputSundyOnly;

	public void onMessageCreate(DiscordAPI api, Message message) {
		if (message.getContent().equalsIgnoreCase("!ps2bot ping")) {
    		message.reply("pong Planetside2BotCommand");
    		for(Channel c:output_channel_list) {
    			c.sendMessage("pong #"+c.getName());
    		}
    	}
		
		if (message.getContent().equalsIgnoreCase("!ps2bot help")) {
			message.reply("!ps2bot jvsglog/facility.csv/weapon.csv");
			message.reply("!ps2bot help/ping/debug/stat/join/disconnect");
		}
		
    	if (message.getContent().equalsIgnoreCase("!ps2bot facility.csv")) {
    		Planetside2API.getSingleton().outputCsvFacility();
    		File f = new File("facility.csv.txt");
    		message.getChannelReceiver().sendFile(f);
    	}
    	
    	if (message.getContent().equalsIgnoreCase("!ps2bot weapon.csv")) {
    		Planetside2API.getSingleton().outputCsvWeaponInfo();
    		File f = new File("weapon.csv.txt");
    		message.getChannelReceiver().sendFile(f);
    	}
		
        if (message.getContent().equalsIgnoreCase("!ps2bot jvsglog")) {
        	if(running==true) {
        		//起動済み
        		if(outputSundyOnly) {
        			outputSundyOnly = false;
        		} else {
        			outputSundyOnly = true;
        		}
            	if(outputSundyOnly) {
                	message.reply("JVSG Sunderer破壊ログ出力");
            	} else {
                	message.reply("車両破壊ログ出力");
            	}
        	} else {
            	running = true;
            	String channel_name = message.getChannelReceiver().getName();
            	ouput_channel = message.getChannelReceiver();
            	client = new JvsgOutputLogClient(ouput_channel);
            	client.addListener(new VehicleDestroyEvent() {

					public void event(VehicleDestroy vd) {
//						System.out.println(vd);
					}
            		
				});
            	
            	if(outputSundyOnly) {
                	message.reply("JVSG Sunderer破壊ログ出力 #"+channel_name);
            	} else {
                	message.reply("車両破壊ログ出力");
            	}
            	
            	try {
            		client.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot join")) {
			if (running == true) {
				boolean isChannelExist = false;
				Channel c = message.getChannelReceiver();
				output_channel_list = client.getChannel_list();
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
					client.setChannel_list(output_channel_list);
				}
			}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot disconnect")) {
        	if(running==true) {
				Channel c = message.getChannelReceiver();
				output_channel_list = client.getChannel_list();
				for (Channel cc : output_channel_list) {
					if (cc.getId() == c.getId()) {
						message.reply("ps2bot disconnected");
						output_channel_list.remove(cc);
						client.setChannel_list(output_channel_list);
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
        	debug_channel.sendMessage("Debug Channel");
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot stat")) {
        	StringBuilder str = new StringBuilder();
        	if(running) {
        		str.append("ps2bot jvsglog is running.\n");
        		str.append("setOutputSundyOnly: "+outputSundyOnly+"\n");
        	} else {
        		str.append("ps2bot jvsglog is not running.\n");
        	}
        	for(Channel c:output_channel_list) {
        		str.append("output_channel_list: "+c.getName()+"\n");
        	}
        	message.reply(str.toString());
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot clear")) {
        	if(running) {
        		String command = new StreamingCommandBuilder().clearCommand().build();
        		client.sendCommand(command);
        	}
        }
        
        if (message.getContent().equalsIgnoreCase("!ps2bot echo")) {
        	if(running) {
//        		String command = StreamingCommandBuilder.echoCommand();
        		StreamingCommandBuilder sc = new StreamingCommandBuilder().help();
        		String command = sc.build();
        		System.out.println(command);
        		client.sendCommand(command);
        	}
        }
	}

}
