
package jdz.bukkitUtils.sql;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.Config;

public class SqlDatabase extends Database {
	private final JavaPlugin plugin;

	public SqlDatabase(JavaPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			setConfig(getConfig(plugin));
		});
	}

	public SqlConfig getConfig(JavaPlugin plugin) {
		File configFile = Config.getConfigFile(plugin, "sqlConfig.yml");
		if (!configFile.exists())
			configFile = Config.getDefaultSqlFile(plugin);

		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		String dbURL = config.getString("database.URL");
		String dbPort = config.getString("database.port");
		String dbName = config.getString("database.name");
		String dbUsername = config.getString("database.username");
		String dbPassword = config.getString("database.password");
		long dbReconnectTime = config.getInt("database.autoReconnectSeconds") * 20;
		dbReconnectTime = dbReconnectTime <= 600 ? 600 : dbReconnectTime;

		return new SqlConfig(dbURL, dbPort, dbName, dbUsername, dbPassword, dbReconnectTime);
	}

	@Override
	protected void onError(Throwable t, String query) {
		new FileLogger(plugin).createErrorLog((Exception) t, query);
	}
}
