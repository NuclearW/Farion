// Command Executor Framework - for Slash commands.  This really doesn't need to be a separate class for so few, but I wanted a proper
// Implementation.
package com.nuclearw.farion;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;
import org.pircbotx.User;

public class FarionCommandExecutor implements CommandExecutor {
	private Farion plugin;

	public FarionCommandExecutor(Farion plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length != 1) return false;

		if(args[0].equalsIgnoreCase("remote")) {
			if(sender.hasPermission("farion.remoteusernames")) {
				String send = "Nicks with access (* are active): ";
				for(FarionRemoteConsoleCommandSender remote : Farion.remoteSenders.values()) {
					send += remote.getName();
					if(remote.doesRecieve()) send += "*";
					send += ", ";
				}
				send = send.substring(0, send.length() - 2);
				sender.sendMessage(send);
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		} else if(args[0].equalsIgnoreCase("reconnect")) {
			if(sender.hasPermission("farion.reconnect")) {
				Farion.reconnect();
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		} else if(args[0].equalsIgnoreCase("clear")) {
			if(sender.hasPermission("farion.clear")) {
				Farion.bot.clearQueue();
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		} else if(args[0].equalsIgnoreCase("reload")) {
			if(sender.hasPermission("farion.reload")) {
				Config.reload(plugin);
				sender.sendMessage("Reloaded config");
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		} else if(args[0].equalsIgnoreCase("list")) {
			if(sender.hasPermission("farion.list")) {
				String message = ChatColor.GRAY + "Connected IRC users: ";
				Bot bot = Farion.bot;

				Channel channel = bot.getChannel(Config.channel);
				Set<User> users = bot.getUsers(channel);
				// Should be impossible to be empty, and we never show the name of ourselves in this list
				if(users.isEmpty() || users.size() == 1) {
					message += "Nobody home!";
				} else {
					for(User user : users) {
						// Never list ourselves
						if(user.getNick().equalsIgnoreCase(Config.nick)) {
							continue;
						}

						String prefix = "";
						if(user.getChannelsOwnerIn().contains(channel)) {
							prefix = ChatColor.DARK_PURPLE + "~";
						} else if(user.getChannelsSuperOpIn().contains(channel)) {
							prefix = ChatColor.DARK_RED + "&";
						} else if(user.getChannelsOpIn().contains(channel)) {
							prefix = ChatColor.GREEN + "@";
						} else if(user.getChannelsHalfOpIn().contains(channel)) {
							prefix = ChatColor.AQUA + "%";
						} else if(user.getChannelsVoiceIn().contains(channel)) {
							prefix = ChatColor.GOLD + "+";
						}

						message += prefix + ChatColor.RESET + user.getNick() + ", ";
					}

					message = message.substring(0, message.length() - 2);
					sender.sendMessage(message);
				}
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		}

		return false;
	}
}
