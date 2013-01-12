// Command Executor Framework - for Slash commands.  This really doesn't need to be a separate class for so few, but I wanted a proper
// Implementation.
package com.nuclearw.farion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jibble.pircbot.PircBot;

public class FarionCommandExecutor implements CommandExecutor {
	@SuppressWarnings("unused")
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
			} else {
				sender.sendMessage("You do not have permission to do that.");
			}
			return true;
		}

		return false;
	}
}
