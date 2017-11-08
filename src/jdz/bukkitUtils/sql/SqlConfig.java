
package jdz.bukkitUtils.sql;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.Config;

public class SqlConfig {
	String dbURL = "";
	String dbPort = "";
	String dbName = "";
	String dbUsername = "";
	String dbPassword = "";
	int dbReconnectTime = 1200;
	
	public SqlConfig(JavaPlugin plugin) {
		reload(Config.getConfigFile(plugin, "sqlConfig.yml"));
	}
	
	public SqlConfig(File configFile){
		reload(configFile);
	}
	
	public boolean reload(File configFile) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		
		dbURL = config.getString("database.URL");
		dbPort = config.getString("database.port");
		dbName = config.getString("database.name");
		dbUsername = config.getString("database.username");
		dbPassword = config.getString("database.password");
		dbReconnectTime = config.getInt("database.autoReconnectSeconds")*20;
		dbReconnectTime = dbReconnectTime<=600?600:dbReconnectTime;
		
		if (dbURL.equals("") || dbName.equals("") || dbUsername.equals("") || dbPassword.equals("")) {
			Bukkit.getLogger().info(
					"Some of the database lines in config.yml are empty or missing, please fill in the config.yml and reload the plugin.");

			if (!config.contains("database.URL"))
				config.addDefault("database.URL", "");
			
			if (!config.contains("database.name"))
				config.addDefault("database.name", "");
			
			if (!config.contains("database.username"))
				config.addDefault("database.username", "");
			
			if (!config.contains("database.password"))
				config.addDefault("database.password", "");
			
			if (!config.contains("database.autoReconnectSeconds"))
				config.addDefault("database.autoReconnectSeconds", 60);
			
			try {
				config.save(configFile);
			} catch (IOException e) {
				new FileLogger(JonosBukkitUtils.instance).createErrorLog(e, "An error occurred in the JonosBukkitUtils");
			}
			
			return false;
		} 
		return true;
	}
}
