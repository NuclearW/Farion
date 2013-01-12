package com.nuclearw.farion;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class Bot extends PircBot {
	private static Farion plugin;

	public Bot(Farion instance) {
		plugin = instance;

		this.setName(Config.nick);
	}

	@Override
	protected void onConnect() {
		plugin.getLogger().info("Connected to IRC");
		plugin.getLogger().info(Config.hostname + ", port " + Config.port);
		identify(Config.nickServPassword);
	}

	@Override
	protected void onDisconnect() {
		plugin.getLogger().info("Disconnected from IRC.");

		//Set a delayed task to attempt a rejoin
		if(Config.retryConnect = true) {
			plugin.getLogger().info("Retrying connect in 10 seconds...");

			//Schedule the actual task
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Farion.reconnect();
				}
			}, 200L);

		//If retryConnect is false, don't even bother.
		} else {
			plugin.getLogger().info("RetryConnect is off: No additional connection attempts."); 
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(channel.equalsIgnoreCase(Config.channel)) {
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

			if(message.toLowerCase().startsWith(".kick")) {
				if(!isVoiceOrOp(sender, channel)) {
					sendMessage(Config.channel, "nope.avi");
					return;
				}

				String words[] = message.split(" ");

				if(words.length < 2) {
					sendMessage(Config.channel, "You're doing it wrong " + sender);
					return;
				}

				String target = words[1];

				Player player = plugin.getServer().getPlayer(target);
				if(player == null) {
					sendMessage(Config.channel, "Cannot find player by the name of " + target);
					return;
				}

				String kickMessage = "";

				if(words.length > 2) {
					for(int i = 2; i < words.length; i++) {
						kickMessage += words[i] + " ";
					}
					kickMessage = kickMessage.substring(0, kickMessage.length()-1);
				} else {
					kickMessage = "Kicked by admin.";
				}

				player.kickPlayer(kickMessage);

				return;
			}

			if(message.toLowerCase().startsWith(".ban")) {
				if(!isVoiceOrOp(sender, channel)) {
					sendMessage(Config.channel, "nope.avi");
					return;
				}

				String words[] = message.split(" ");

				if(words.length < 2) {
					sendMessage(Config.channel, "You're doing it wrong " + sender);
					return;
				}

				String target = words[1];

				Player player = plugin.getServer().getPlayer(target);
				if(player == null) {
					sendMessage(Config.channel, "Cannot find player by the name of " + target);
					return;
				}

				String kickMessage = "";

				if(words.length > 2) {
					for(int i = 2; i < words.length; i++) {
						kickMessage += words[i] + " ";
					}
					kickMessage = kickMessage.substring(0, kickMessage.length()-1);
				} else {
					kickMessage = "Banned by admin.";
				}

				player.setBanned(true);
				player.kickPlayer(kickMessage);

				return;
			}

			if(message.toLowerCase().startsWith(".clear")) {
				if(!isVoiceOrOp(sender, channel)) {
					sendMessage(Config.channel, "nope.avi");
					return;
				}

				clearQueue();
				return;
			}

			plugin.getServer().broadcastMessage("[IRC] <" + sender + "> " + ColorConverter.ircToMinecraft(message));
//			plugin.getLogger().info("[IRC][" + Config.channel + "] <" + sender + "> " + message);
		} else if(channel.equalsIgnoreCase(Config.modChannel)) {
			// TODO: Mod Channel
		}
	}

	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
		if(Farion.remoteSenders.containsKey(sender)) {
			FarionRemoteConsoleCommandSender remote = Farion.remoteSenders.get(sender);
			if(message.equalsIgnoreCase(".on")) {
				sendMessage(sender, "Console output is now enabled");
				remote.setRecieve(true);
				return;
			} else if(message.equalsIgnoreCase(".off")) {
				sendMessage(sender, "Console output is now disabled");
				remote.setRecieve(false);
				return;
			}

			if(remote.doesRecieve()) {
				FarionRemoteServerCommandEvent event = new FarionRemoteServerCommandEvent(remote, message);
				Bukkit.getServer().getPluginManager().callEvent(event);
				Bukkit.getServer().dispatchCommand(remote, event.getCommand());
			} else {
				sendMessage(sender, "Cannot send console commands while console output is disabled!");
			}
		}
	}

	@Override
	protected void onAction(String sender, String login, String hostname, String target, String action) {
		if(target.equalsIgnoreCase(Config.channel)) {
			plugin.getServer().broadcastMessage("[IRC] * " + sender + " " + action);
//			plugin.getLogger().info("[IRC] * " + sender + " " + action);
		} else if(target.equalsIgnoreCase(Config.modChannel)) {
			// TODO: Mod channel
		}
	}

	@Override
	protected void onIncomingChatRequest(DccChat chat) {
		try {
			// TODO: Some Authentication Method here

			chat.accept();
			chat.sendLine("Enter password.");
			String response = chat.readLine();

			if(response.equals(Config.dccPassword)) {
				chat.sendLine("Password Accepted");
				plugin.getLogger().info("DCC Console session started.");

				//Do some init function to start emulating console data

				chat.close(); //Remove this when actually ready to use
			} else {
				plugin.getLogger().info("DCC Console session attempt, incorrect password.");
				chat.sendLine("Incorrect.  Closing.");
				chat.close();
			}
		}
		catch (IOException e) {}
	}

	private boolean isVoiceOrOp(String check, String channel) {
		User[] users = getUsers(channel);
		for(User user : users) {
			if(user.isOp() || user.hasVoice()) {
				if(user.getNick().equalsIgnoreCase(check)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void clearQueue() {
		try {
			// Reflection time!
			Field queueField = PircBot.class.getDeclaredField("_outQueue");
			queueField.setAccessible(true);
			Object queueObject = queueField.get(Farion.bot);
			Method clearMethod = queueObject.getClass().getMethod("clear");
			clearMethod.setAccessible(true);
			clearMethod.invoke(queueObject);
			clearMethod.setAccessible(false);
			queueField.setAccessible(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
