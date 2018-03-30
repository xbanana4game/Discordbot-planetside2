package jp.banana.discordbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class BotConfig {
	private static BotConfig singleton = new BotConfig();
	
	private BotConfig() {
		readConfig();
	}

	public static BotConfig getSingleton() {
		return singleton;
	}

	private String token;
	private String serviceID;
	private String outfitID;
	
	public String getToken() {
		return token;
	}

	public String getServiceID() {
		return serviceID;
	}

	public String getOutfitID() {
		return outfitID;
	}

	private boolean readConfig() {
	    String configFile = "discord.conf";
	    Properties prop = new Properties();

	    try {
	      prop.load(new FileInputStream(configFile));
	    } catch (IOException e) {
	      e.printStackTrace();
	      return false;
	    }

	    token = prop.getProperty("token");
	    serviceID = prop.getProperty("serviceID");
	    outfitID = prop.getProperty("outfitID");
	    System.out.println("token: " + token);
	    System.out.println("serviceID: " + serviceID);
	    System.out.println("outfitID: " + outfitID);
	    return true;
	}
}
