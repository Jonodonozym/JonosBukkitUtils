
package jdz.bukkitUtils.sql;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.Config;

public class SqlDatabase extends Database {
	private final Plugin plugin;
	private boolean doFileLogging = false;
	private boolean doConsoleLogging = false;
	private final FileLogger logger;

	public SqlDatabase(Plugin plugin) {
		this.plugin = plugin;
		this.logger = new FileLogger(plugin);
		logger.setWriteToLog(false);
		logger.setPrintToConsole(false);
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			setConfig(getConfig(plugin));
		});
	}

	protected void setDoFileLogging(boolean doFileLogging) {
		this.doFileLogging = doFileLogging;
		logger.setWriteToLog(doFileLogging);
	}


	protected void setDoConsoleLogging(boolean doConsoleLogging) {
		this.doConsoleLogging = doConsoleLogging;
		logger.setPrintToConsole(doConsoleLogging);
	}

	public SqlConfig getConfig(Plugin plugin) {
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

	@Override
	protected void update(String update) {
		if (doFileLogging || doConsoleLogging)
			logger.log(update);
		super.update(update);
	}

	@Override
	protected List<SqlRow> query(String query) {
		if (doFileLogging || doConsoleLogging) {
			logger.log(query);
			List<SqlRow> rows = super.query(query);
			logger.log("Size: " + rows.size());
			for (SqlRow row : rows)
				logger.log(row.toString());
			return rows;
		}
		return super.query(query);
	}
}
