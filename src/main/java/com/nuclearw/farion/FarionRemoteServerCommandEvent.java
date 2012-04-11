package com.nuclearw.farion;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.RemoteServerCommandEvent;

public class FarionRemoteServerCommandEvent extends RemoteServerCommandEvent {
	private static final HandlerList handlers = new HandlerList();

	public FarionRemoteServerCommandEvent(CommandSender sender, String command) {
		super(sender, command);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
