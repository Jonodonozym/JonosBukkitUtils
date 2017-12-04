/**
 * BukkitJUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.events.custom.AnvilRenameEvent;
import jdz.bukkitUtils.events.custom.AnvilRepairEvent;
import jdz.bukkitUtils.misc.Config;
import jdz.bukkitUtils.misc.PluginUpdater;

/**
 * Jono's (or Java, I don't give a damn what you think) Utilities for Bukkit to make your coding life
 * easier and less copy-pasty copy-pasty from old projects and the internet
 *
 * @author Jonodonozym
 */
public final class JonosBukkitUtils extends JavaPlugin{
	public static JonosBukkitUtils instance;
	private final int bukkitID = 281287;
	
	@Override
	public void onEnable() {
		instance = this;
		FileConfiguration config = Config.getConfig(this);
		if (config.getBoolean("autoUpdate"))
			new PluginUpdater(this, bukkitID, this.getFile(), PluginUpdater.UpdateType.DEFAULT, false);
		
		// registering custom events
		Bukkit.getPluginManager().registerEvents(new AnvilRenameEvent.AnvilRenameListener(), this);
		Bukkit.getPluginManager().registerEvents(new AnvilRepairEvent.AnvilRepairListener(), this);
	}
}
