package jp.banana.discordbot;


import jp.banana.planetside2.api.Planetside2API;


public class DiscordBot {

	public static void main(String[] args) {
		if(!BotConfig.getSingleton().checkConfig()) {
			return;
		}
		String token = BotConfig.getSingleton().getToken();
		
		Planetside2API.getSingleton();
		@SuppressWarnings("unused")
		Planetside2Bot ping = new Planetside2Bot(token);
	}

}
