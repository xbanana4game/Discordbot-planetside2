package jp.banana.discordbot;
import jp.banana.planetside2.command.Planetside2BotCommand;
import jp.banana.planetside2.command.Planetside2FacilityControlCommand;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;


public class Planetside2Bot {
	Channel ouput_channel = null;
	static boolean running = false;

    public Planetside2Bot(String token) {
    	DiscordAPI api = Javacord.getApi(token, true);
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(DiscordAPI api) {
                // register listener
            	api.registerListener(new Planetside2BotCommand());
                api.registerListener(new Planetside2FacilityControlCommand());
				System.out.println("ps2bot ready");
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

}