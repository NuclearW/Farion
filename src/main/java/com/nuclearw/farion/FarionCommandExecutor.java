// Command Executor Framework - for Slash commands.  This really doesn't need to be a separate class for so few, but I wanted a proper
// Implementation.
package com.nuclearw.farion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
		}

		return false;
	}
}
