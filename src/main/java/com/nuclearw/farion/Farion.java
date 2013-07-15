package com.nuclearw.farion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;

import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

public class Farion extends JavaPlugin implements Listener {
	protected static Bot bot;
	private FarionCommandExecutor farionExecutor;
	private static boolean mcMMOEnabled = false;
	protected static Map<String, FarionRemoteConsoleCommandSender> remoteSenders = new HashMap<String, FarionRemoteConsoleCommandSender>();

	@Override
	public void onEnable() {
		farionExecutor = new FarionCommandExecutor(this);

		getCommand("farion").setExecutor(farionExecutor);

		Config.load(this);
		
		bot = new Bot();

		for(String name : Config.remoteUsernames) {
			remoteSenders.put(name, new FarionRemoteConsoleCommandSender(name));
		}

		bot.getListenerManager().addListener(new BotEvents(this, bot));

		connect();

		getServer().getPluginManager().registerEvents(this, this);

		if (getServer().getPluginManager().getPlugin("mcMMO") != null) {
			mcMMOEnabled = true;
			getServer().getPluginManager().registerEvents(new FarionMcMMO(bot, this), this);
			getLogger().info("mcMMO Found, Registering Events");
			bot.joinChannel(Config.modChannel);
		} else {
			getLogger().info("mcMMO NOT Found: mod Channel functions disabled.");
		}

		getServer().getScheduler().scheduleSyncRepeatingTask(this, BotMessageQueue.getInstance(), 1, 1);

		metrics();

		getLogger().info("Finished Loading " + getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		getLogger().info("Finished Unloading " + getDescription().getFullName());
	}

	//Chat Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.getRecipients().size() != getServer().getOnlinePlayers().length) return;
		if(!event.getPlayer().hasPermission("farion.relay")) return;

		if(Config.showIrcMeMessage) {
			String sendMessage = Config.ircMessage
			                     .replace("{username}", event.getPlayer().getName())
			                     .replace("{message}", ColorConverter.minecraftToIrc(event.getMessage()));

			BotMessageQueue.getInstance().queueMessage(Config.channel, sendMessage);
		}
		// TODO: Mod Channel ?
	}

	///me handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/me")) {
			if(!event.getPlayer().hasPermission("farion.relay")) return;

			String[] words = event.getMessage().split(" ");
			if(words.length < 2) return;

			String message = words[1];
			for(int i = 2; i < words.length; i++) {
				message += " " + words[i];
			}

			if(Config.showIrcMeMessage) {
				String sendMessage = Config.ircMeMessage
				                     .replace("{username}", event.getPlayer().getName())
				                     .replace("{message}", message);

				BotMessageQueue.getInstance().queueMessage(Config.channel, sendMessage);
			}
			// TODO: Mod Channel ?
		}
	}

	public static void reconnect() {
		try {
			bot.willfulDisconnect = true;
			bot.disconnect();
			while(bot.isConnected()) continue;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		connect();
		bot.willfulDisconnect = false;
	}

	//Connect to the IRC server
	public static void connect() {

		bot.setName(Config.nick);
		bot.setLogin("Farion");
		bot.setVerbose(Config.debug);

		try {
			if(Config.ssl) {
				if(Config.password == null) {
					bot.connect(Config.hostname, Config.port, new UtilSSLSocketFactory().disableDiffieHellman().trustAllCertificates());
				} else {
					bot.connect(Config.hostname, Config.port, Config.password, new UtilSSLSocketFactory().disableDiffieHellman().trustAllCertificates());
				}
			} else {
				if(Config.password == null) {
					bot.connect(Config.hostname, Config.port);
				} else {
					bot.connect(Config.hostname, Config.port, Config.password);
				}
			}

			if(Config.nickServPassword != null && !Config.nickServPassword.isEmpty()) {
				bot.identify(Config.nickServPassword);
			}

			bot.joinChannel(Config.channel);

			if(mcMMOEnabled) {
				bot.joinChannel(Config.modChannel);
			}
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
		if(!Config.showIrcJoinMessage) return;

		String message = Config.ircJoinMessage
		                 .replace("{username}", event.getPlayer().getName())
		                 .replace("{displayname}", event.getPlayer().getDisplayName());

		BotMessageQueue.getInstance().queueMessage(Config.channel, message);
	}

	//Quit Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(!Config.showIrcPartMessage) return;

		String message = Config.ircPartMessage
		                 .replace("{username}", event.getPlayer().getName())
		                 .replace("{displayname}", event.getPlayer().getDisplayName());

		BotMessageQueue.getInstance().queueMessage(Config.channel, message);
	}

	//Kick Handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		String strKickreason;
		
		//Check if there's an actual kick reason passed, and if there is, include it in the output
		strKickreason = event.getReason();
		if(strKickreason != null) {
			BotMessageQueue.getInstance().queueMessage(Config.channel, event.getPlayer().getName() + " was kicked: [" + ColorConverter.minecraftToIrc(strKickreason) + "]");
		} else {
			BotMessageQueue.getInstance().queueMessage(Config.channel, event.getPlayer().getName() + " was kicked.");
		}
	}

	//Server chatter handler
	@EventHandler(priority = EventPriority.MONITOR)
	public void onServerCommand(ServerCommandEvent event) {
		if(event.getCommand().toLowerCase().startsWith("say")) {
			String[] words = event.getCommand().split(" ");
			if(words.length < 2) return;

			String message = words[1];
			for(int i = 2; i < words.length; i++) {
				message += " " + words[i];
			}

			BotMessageQueue.getInstance().queueMessage(Config.channel, "<*Console*> " + message);
			// TODO: Mod Channel ?
		}
	}

	public boolean getmcMMOEnabled() {
		return mcMMOEnabled;
	}

	private void metrics() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) { }
	}
}
