package com.nuclearw.farion;

import java.io.File;
import java.util.List;

public class Config {
	public static String nick, nickServPassword, channel, dccPassword, modChannel, hostname, password;
	public static int port;
	public static boolean ssl, retryConnect, channelParts, channelJoins, channelNickChanges;
	public static List<String> remoteUsernames;

	public static String ircMessage, gameMessage;
	public static String ircMeMessage, gameMeMessage;

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
		password = plugin.getConfig().getString("Server.Password");

		channel = plugin.getConfig().getString("Channel.Name");
		channelJoins = plugin.getConfig().getBoolean("Channel.ShowJoins");
		channelParts = plugin.getConfig().getBoolean("Channel.ShowParts");
		channelNickChanges = plugin.getConfig().getBoolean("Channel.ShowNickChanges");
		modChannel = plugin.getConfig().getString("ModChannel.Name");

		ircMessage = plugin.getConfig().getString("Message.IrcMessage");
		gameMessage = plugin.getConfig().getString("Message.GameMessage");

		ircMeMessage = plugin.getConfig().getString("Message.IrcMeMessage");
		gameMeMessage = plugin.getConfig().getString("Message.GameMeMessage");

		remoteUsernames = plugin.getConfig().getStringList("RemoteConsoleUsers");
	}
}
