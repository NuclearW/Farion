package com.nuclearw.farion;

import java.io.File;

public class Config {
	public static String nick, nickServPassword, channel, modChannel, hostname;
	public static int port;
	public static boolean ssl;

	public static void load(Farion plugin) {
		if(!new File(plugin.getDataFolder() , "config.yml").exists()) {
			plugin.saveDefaultConfig();
		}

		nick = plugin.getConfig().getString("Bot.Nick");
		nickServPassword = plugin.getConfig().getString("Bot.NickServ");

		hostname = plugin.getConfig().getString("Server.Hostname");
		port = plugin.getConfig().getInt("Server.Port");
		ssl = plugin.getConfig().getBoolean("Server.SSL");

		channel = plugin.getConfig().getString("Channel.Name");

		modChannel = plugin.getConfig().getString("ModChannel.Name");
	}
}
