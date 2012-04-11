package com.nuclearw.farion;

import java.io.File;

public class Config {
	public static String nick, nickServPassword, channel, dccPassword, modChannel, hostname;
	public static int port;
	public static boolean ssl, retryConnect;

	public static void load(Farion plugin) {
		if(!new File(plugin.getDataFolder() , "config.yml").exists()) {
			plugin.saveDefaultConfig();
		}

		nick = plugin.getConfig().getString("Bot.Nick");
		nickServPassword = plugin.getConfig().getString("Bot.NickServ");
		retryConnect = plugin.getConfig().getBoolean("Bot.RetryConnect");
		dccPassword = plugin.getConfig().getString("Bot.DCCPassword");

		hostname = plugin.getConfig().getString("Server.Hostname");
		port = plugin.getConfig().getInt("Server.Port");
		ssl = plugin.getConfig().getBoolean("Server.SSL");

		channel = plugin.getConfig().getString("Channel.Name");

		modChannel = plugin.getConfig().getString("ModChannel.Name");
	}
}
