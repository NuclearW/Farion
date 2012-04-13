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
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}
