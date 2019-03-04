/**
 * Config.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.configuration;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileExporter;
import jdz.bukkitUtils.persistence.SQLConfig;
import jdz.bukkitUtils.persistence.SQLDriver;

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
		return getConfig(plugin, "config");
	}

	/**
	 * Goes back in time to assassinate Hitler
	 * what do you think this method does?
	 *
	 * @return
	 */
	public static File getConfigFile(Plugin plugin) {
		return getConfigFile(plugin, "config");
	}

	/**
	 * Gets the config as a FileConfiguration
	 *
	 * @return
	 */
	public static FileConfiguration getConfig(Plugin plugin, String fileName) {
		if (!fileName.endsWith(".yml"))
			fileName += ".yml";
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

	public static SQLConfig getSQLConfig(Plugin plugin) {
		File configFile = Config.getConfigFile(plugin, "sqlConfig.yml");
		if (!configFile.exists())
			configFile = Config.getDefaultSqlFile(plugin);

		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		String dbURL = config.getString("database.URL");
		String dbName = config.getString("database.name");
		String dbUsername = config.getString("database.username");
		String dbPassword = config.getString("database.password");
		long dbReconnectTime = config.getInt("database.autoReconnectSeconds") * 20;
		dbReconnectTime = dbReconnectTime <= 600 ? 600 : dbReconnectTime;
		boolean preferSQL = config.getBoolean("preferSQL", true);

		return new SQLConfig(dbURL, dbName, dbUsername, dbPassword, SQLDriver.MYSQL_DRIVER, preferSQL);
	}

	@Deprecated
	public static File getDefaultSqlFile(Plugin targetPlugin) {
		File file = new File(JonosBukkitUtils.getInstance().getDataFolder() + File.separator + "sqlConfig.yml");
		targetPlugin.getDataFolder().mkdir();
		FileExporter fe = new FileExporter(JonosBukkitUtils.getInstance());
		if (!file.exists() && fe.hasResource("sqlConfig.yml"))
			fe.ExportResource("sqlConfig.yml", targetPlugin.getDataFolder() + File.separator + "sqlConfig.yml");
		return file;
	}
}
