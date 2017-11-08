/**
 * BukkitJUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.bstats.Metrics;
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
		boolean update = config.getBoolean("autoupdate");
		if (update)
			new PluginUpdater(this, bukkitID, this.getFile(), PluginUpdater.UpdateType.DEFAULT, false);
		
		new Metrics(this);
	}
}
