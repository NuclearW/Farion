package com.nuclearw.farion.runnable;

import org.bukkit.entity.Player;

public class RemovePlayerTask implements Runnable {
	private final Player player;
	private final String reason;
	private final boolean ban;

	public RemovePlayerTask(Player player, String reason, boolean ban) {
		this.player = player;
		this.reason = reason;
		this.ban = ban;
	}

	@Override
	public void run() {
		if(ban) {
			player.setBanned(true);
		}

		player.kickPlayer(reason);
	}
}
