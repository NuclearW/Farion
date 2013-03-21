package com.nuclearw.farion;

import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_5_R2.command.ServerCommandSender;

public class FarionRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
	private String name = "Farion";
	private boolean recieve = false;
	private static Bot bot;

	public FarionRemoteConsoleCommandSender(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void sendMessage(String message) {
		try {
			if(recieve) bot.sendMessage(name, ColorConverter.minecraftToIrc(message));
		} catch(NullPointerException ex) {
			System.out.println("[Farion] Attempted to send a message in response to a command but bot was null!");
		}
	}

	public void sendMessage(String[] messages) {
		for(String message : messages) {
			sendMessage(message);
		}
	}

	public boolean isOp() {
		return true;
	}

	public void setOp(boolean arg0) {
		throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
	}

	public static void setBot(Bot bot) {
		FarionRemoteConsoleCommandSender.bot = bot;
	}

	public boolean doesRecieve() {
		return recieve;
	}

	public void setRecieve(boolean set) {
		recieve = set;
	}
}
