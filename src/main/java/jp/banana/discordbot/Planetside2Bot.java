package jp.banana.discordbot;
import java.util.Collection;
import java.util.Iterator;
import jp.banana.planetside2.api.Planetside2Client;
import jp.banana.planetside2.command.Planetside2BotCommand;
import jp.banana.planetside2.command.Planetside2FacilityControlCommand;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;


public class Planetside2Bot {
	Channel ouput_channel = null;
	Channel debug_channel = null;
	static boolean running = false;
	Planetside2Client client = null;

    public Planetside2Bot(String token) {
    	DiscordAPI api = Javacord.getApi(token, true);
        // connect
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(DiscordAPI api) {
                // register listener
            	api.registerListener(new Planetside2BotCommand());
                api.registerListener(new Planetside2FacilityControlCommand());

				Collection<Channel> c = api.getChannels();
				for (Iterator<Channel> i = c.iterator(); i.hasNext();) {
					Channel cc = (Channel) i.next();
					if (cc.getName().equals("ps2bot")) {
						debug_channel = cc;
						break;
					}
				}
				debug_channel.sendMessage("ps2bot ready");
				System.out.println("ps2bot ready");
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

}