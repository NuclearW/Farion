package com.nuclearw.farion;

import org.bukkit.plugin.java.JavaPlugin;

public class Farion extends JavaPlugin {
	@Override
	public void onEnable() {
		Config.load(this);
	}

	@Override
	public void onDisable() {
		
	}
}
