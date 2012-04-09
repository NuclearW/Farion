package com.nuclearw.farion;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

public class Bot extends PircBot {
	private static Farion plugin;

	public Bot(Farion instance) {
		plugin = instance;

		this.setName(Config.nick);
	}

	@Override
	protected void onConnect() {
		identify(Config.nickServPassword);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (channel.equalsIgnoreCase(Config.channel)) {
			plugin.getServer().broadcastMessage("[IRC] <" + sender + ">: " + Colors.removeFormattingAndColors(message));
		} else if (channel.equalsIgnoreCase(Config.modChannel)) {
			// TODO: Mod Channel
		}
	}
}
