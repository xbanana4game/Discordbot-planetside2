package jp.banana.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class BotConfig {
	private static BotConfig singleton = new BotConfig();
	private final String CONFIG_FILE = "discord.conf";
	private String token;
	private String serviceID;
	private String outfitID;
	
	private BotConfig() {
		checkConfig();
		readConfig();
	}
	
	public boolean checkConfig() {
		File f = new File(CONFIG_FILE);
		if(f.exists()) {
			return true;
		} else {
			System.err.println("configfile not exist. "+CONFIG_FILE);
		}
		return false;
	}

	public static BotConfig getSingleton() {
		return singleton;
	}


	
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
	    Properties prop = new Properties();

	    try {
	      prop.load(new FileInputStream(CONFIG_FILE));
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
