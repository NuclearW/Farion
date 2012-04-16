package com.nuclearw.farion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.TrustingSSLSocketFactory;

public class Farion extends JavaPlugin implements Listener {
	private static Bot bot;
	private FarionCommandExecutor farionExecutor;
	protected static Map<String, FarionRemoteConsoleCommandSender> remoteSenders = new HashMap<String, FarionRemoteConsoleCommandSender>();

	@Override
	public void onEnable() {
		farionExecutor = new FarionCommandExecutor(this);

		//Nuke: Unused executor, I did NOT bind anything to it yet.  Don't forget to register commands.

		Config.load(this);
		
		bot = new Bot(this);

		FarionRemoteConsoleCommandSender.setBot(bot);

		for(String name : Config.remoteUsernames) {
			remoteSenders.put(name, new FarionRemoteConsoleCommandSender(name));
		}

		connect();

		getServer().getPluginManager().registerEvents(this, this);

		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading " + getDescription().getFullName());
	}

	//Chat Handler
	@EventHandler(ignoreCancelled = true)
	public void onChat(PlayerChatEvent event) {
		bot.sendMessage(Config.channel, "<" + event.getPlayer().getName() + "> " + event.getMessage());
		// TODO: Mod Channel ?
	}

	///me handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/me")) {
			String[] words = event.getMessage().split(" ");
			if(words.length < 2) return;

			String message = words[1];
			for(int i = 2; i < words.length; i++) {
				message += " " + words[i];
			}

			bot.sendMessage(Config.channel, "* " + event.getPlayer().getName() + " " + message);
			// TODO: Mod Channel ?
		}
	}

	//Connect to the IRC server
	public static void connect() {
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
	}

	//Join Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		bot.sendMessage(Config.channel, event.getPlayer().getName() + " logged in.");
	}

	//Quit Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerQuitEvent event) {
		bot.sendMessage(Config.channel, event.getPlayer().getName() + " left the server.");
	}

	//Kick Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		String strKickreason;
		
		//Check if there's an actual kick reason passed, and if there is, include it in the output
		strKickreason = event.getReason();
		if(strKickreason != null) {
			bot.sendMessage(Config.channel, event.getPlayer().getName() + " was kicked: [" + strKickreason + "]");
		} else {
			bot.sendMessage(Config.channel, event.getPlayer().getName() + " was kicked.");
		}
	}

	//Server chatter handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onServerCommand(ServerCommandEvent event) {
		if(event.getCommand().toLowerCase().startsWith("say")) {
			String[] words = event.getCommand().split(" ");
			if(words.length < 2) return;

			String message = words[1];
			for(int i = 2; i < words.length; i++) {
				message += " " + words[i];
			}

			bot.sendMessage(Config.channel, "<*Console*> " + message);
			// TODO: Mod Channel ?
		}
	}
}
