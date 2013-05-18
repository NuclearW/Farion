package com.nuclearw.farion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.pircbotx.PircBotX;

import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;

public class FarionMcMMO implements Listener {
	private PircBotX bot;
	private Farion plugin;

	public FarionMcMMO(PircBotX bot, Farion plugin) {
		this.bot = bot;
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onAdminChat(McMMOAdminChatEvent event) {
		if(Config.showIrcMeMessage && !event.getPlugin().equals(plugin)) {
			String sendMessage = Config.ircMessage
			                     .replace("{username}", event.getSender())
			                     .replace("{message}", ColorConverter.minecraftToIrc(event.getMessage()));

			BotMessageQueue.getInstance().queueMessage(Config.modChannel, sendMessage);
		}
	}
}
