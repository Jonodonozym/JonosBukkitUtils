
package jdz.bukkitUtils.sql;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.Config;
import lombok.Getter;

public class SqlDatabase extends Database {
	@Getter private final Plugin plugin;
	private boolean doFileLogging = false;
	private boolean doConsoleLogging = false;
	private final FileLogger logger;

	public SqlDatabase(Plugin plugin) {
		this.plugin = plugin;
		logger = new FileLogger(plugin);
		logger.setWriteToLog(false);
		logger.setPrintToConsole(false);
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			setConfig(Config.getSQLConfig(plugin));
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

	@Override
	protected void onError(Throwable t, String query) {
		new FileLogger(plugin).createErrorLog((Exception) t, query);
	}

	@Override
	public boolean update(String update) {
		if (doFileLogging || doConsoleLogging)
			logger.log(update);
		return super.update(update);
	}

	@Override
	public List<SQLRow> query(String query) {
		if (doFileLogging || doConsoleLogging) {
			logger.log(query);
			List<SQLRow> rows = super.query(query);
			logger.log("Size: " + rows.size());
			for (SQLRow row : rows)
				logger.log(row.toString());
			return rows;
		}
		return super.query(query);
	}
}
