package com.nuclearw.farion;

import org.bukkit.entity.Player;
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
			message = Colors.removeFormattingAndColors(message);

			if(message.equalsIgnoreCase(".players")) {
				if(plugin.getServer().getOnlinePlayers().length == 0) {
					sendMessage(Config.channel, "Nobody online here.");
				} else {
					String send = "Players online (" + plugin.getServer().getOnlinePlayers().length + "/" + plugin.getServer().getMaxPlayers() + "): ";
					for(Player player : plugin.getServer().getOnlinePlayers()) {
						send += player.getName() + ", ";
					}
					send = send.substring(0, send.length() - 2);
					sendMessage(Config.channel, send);
				}
				return;
			}

			plugin.getServer().broadcastMessage("[IRC] <" + sender + "> " + message);
			plugin.getLogger().info("[IRC][" + Config.channel + "] <" + sender + "> " + message);
		} else if (channel.equalsIgnoreCase(Config.modChannel)) {
			// TODO: Mod Channel
		}
	}
}
