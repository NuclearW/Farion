package com.nuclearw.farion;

import java.io.IOException;

import javax.net.ssl.SSLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.TrustingSSLSocketFactory;

public class Farion extends JavaPlugin implements Listener {
	private static Bot bot;

	@Override
	public void onEnable() {
		Config.load(this);

		bot = new Bot(this);
		try {
			if(Config.ssl) {
				bot.connect(Config.hostname, Config.port, new TrustingSSLSocketFactory());
			} else {
				bot.connect(Config.hostname, Config.port, null);
			}

			bot.joinChannel(Config.channel);

			// TODO: Mod Channel
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (SSLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}

		getServer().getPluginManager().registerEvents(this, this);

		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading "+getDescription().getFullName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(PlayerChatEvent event) {
		bot.sendMessage(Config.channel, "<" + event.getPlayer().getName() + "> " + event.getMessage());
		// TODO: Mod Channel
	}
}
