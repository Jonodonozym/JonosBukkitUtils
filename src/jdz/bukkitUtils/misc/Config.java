/**
 * Config.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.misc;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileExporter;

/**
 * Class for getting the config without worrying about exporting it first
 *
 * @author Jonodonozym
 */
public final class Config {
	/**
	 * Gets the config as a FileConfiguration
	 * 
	 * @return
	 */
	public static FileConfiguration getConfig(Plugin plugin) {
		return getConfig(plugin, "config.yml");
	}

	/**
	 * Goes back in time to assassinate Hitler
	 * what do you think this method does?
	 * 
	 * @return
	 */
	public static File getConfigFile(Plugin plugin) {
		return getConfigFile(plugin, "config.yml");
	}

	/**
	 * Gets the config as a FileConfiguration
	 * 
	 * @return
	 */
	public static FileConfiguration getConfig(Plugin plugin, String fileName) {
		return YamlConfiguration.loadConfiguration(getConfigFile(plugin, fileName));
	}

	public static File getConfigFile(Plugin plugin, String fileName) {
		File file = new File(plugin.getDataFolder() + File.separator + fileName);
		plugin.getDataFolder().mkdir();
		FileExporter fe = new FileExporter(plugin);
		if (!file.exists() && fe.hasResource(fileName))
			fe.ExportResource(fileName, plugin.getDataFolder() + File.separator + fileName);
		return file;
	}

	public static File getDefaultSqlFile(Plugin targetPlugin) {
		File file = new File(JonosBukkitUtils.getInstance().getDataFolder() + File.separator + "sqlConfig.yml");
		targetPlugin.getDataFolder().mkdir();
		FileExporter fe = new FileExporter(JonosBukkitUtils.getInstance());
		if (!file.exists() && fe.hasResource("sqlConfig.yml"))
			fe.ExportResource("sqlConfig.yml", targetPlugin.getDataFolder() + File.separator + "sqlConfig.yml");
		return file;
	}
}
