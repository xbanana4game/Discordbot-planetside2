package jp.banana.planetside2.command;

import java.util.ArrayList;

import jp.banana.planetside2.api.FacilityControlClient;
import jp.banana.planetside2.streaming.Planetside2EventStreaming;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class Planetside2FacilityControlCommand implements MessageCreateListener {

	private boolean running;
	private Channel ouput_channel;
	private Planetside2EventStreaming client;

	public void onMessageCreate(DiscordAPI api, Message message) {
		if (message.getContent().equalsIgnoreCase("!ps2bot ping")) {
    		message.reply("pong Planetside2FacilityControlCommand");
    	}
		
		if (message.getContent().equalsIgnoreCase("!ps2bot help")) {
			message.reply("!ps2bot help/ping/facility");
		}
		
		if (message.getContent().equalsIgnoreCase("!ps2bot facility")) {
        	if(running==true) {
        		//起動済み
        	} else {
            	running = true;
            	//planetside2 api スレッド開始
            	String channel_name = message.getChannelReceiver().getName();
            	ouput_channel = message.getChannelReceiver();
            	message.reply("拠点占拠ログ出力 #"+channel_name);
            	client = new FacilityControlClient(ouput_channel);
            	try {
					client.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
	}

}
