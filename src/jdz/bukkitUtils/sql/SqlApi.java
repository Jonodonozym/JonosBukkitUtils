/**
 * SqlApi.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.TimedTask;

/**
 * Utility class with static methods to interact with the sql database
 * 
 * @author Jonodonozym
 */
public final class SqlApi {
	private final String driver = "com.mysql.jdbc.Driver";
	private TimedTask autoReconnectTask = null;
	private Connection dbConnection = null;
	private List<Runnable> connectHooks = new ArrayList<Runnable>();

	private final JavaPlugin plugin;
	private SqlConfig config;
	private final FileLogger fileLogger;

	public SqlApi(JavaPlugin plugin) {
		this(plugin, new SqlConfig(plugin));
	}

	public SqlApi(JavaPlugin plugin, SqlConfig config) {
		this.config = config;
		this.plugin = plugin;
		fileLogger = new FileLogger(plugin);
		openConnection(true);
	}

	public void runOnConnect(Runnable r) {
		if (isConnected())
			r.run();
		else
			connectHooks.add(r);
	}

	public void reloadConfig() {
		SqlConfig newConfig = new SqlConfig(plugin);
		if (newConfig.equals(config))
			return;
		config = newConfig;
		openConnection(true);
	}

	/**
	 * Opens a new connection to a specified SQL database If it fails 3 times,
	 * writes the error to a log file in the plugin's directory
	 * 
	 * @param logger
	 *            the logger to record success / fail messages to
	 * @return the opened connection, or null if one couldn't be created
	 */
	public Connection openConnection(boolean doLogging) {
		if (dbConnection != null)
			close(dbConnection);
		try {
			try {
				Class.forName(driver).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				if (Bukkit.getLogger() != null)
					fileLogger.createErrorLog(e);
			}

			String url = "jdbc:mysql://" + config.dbURL + ":" + config.dbPort + "/" + config.dbName + "?user="
					+ config.dbUsername + "&password=" + config.dbPassword + "&loginTimeout=1000&useSSL=false";

			dbConnection = DriverManager.getConnection(url, config.dbUsername, config.dbPassword);

			if (doLogging)
				Bukkit.getLogger().info(
						"Successfully connected to the " + config.dbName + " database at the host " + config.dbURL);

			Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
				for (Runnable r : connectHooks)
					r.run();
			});

			return dbConnection;
		}

		catch (SQLException e) {
			if (doLogging) {
				Bukkit.getLogger()
						.info("Failed to connect to the database. Refer to the error log file in the plugin's directory"
								+ " and contact the database host / plugin developer to help resolve the issue.");
				fileLogger.createErrorLog(e);
			}
			autoReconnect();
		}
		return null;
	}

	/**
	 * Closes a given connection, catching any errors
	 * 
	 * @param connection
	 */
	public boolean close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
			}
		}
		return false;
	}

	public boolean isConnected() {
		try {
			if (dbConnection != null && !dbConnection.isClosed())
				return true;
		} catch (SQLException e) {
		}
		autoReconnect();
		return false;
	}

	private boolean autoReconnect() {
		if (autoReconnectTask == null) {
			plugin.getLogger().info("SQL database '" + config.dbName + "' recently went offline, will auto-reconnect.");
			autoReconnectTask = new TimedTask(plugin, config.dbReconnectTime, () -> {
				Connection con = openConnection(false);
				if (con != null) {
					Bukkit.getLogger().info("Successfully re-connected to the database");
					dbConnection = con;
					if (autoReconnectTask != null)
						autoReconnectTask.stop();
					autoReconnectTask = null;
				}
			});
			autoReconnectTask.start();
		}
		return true;
	}

	/**
	 * Executes a query, returning the rows if the database responds with them
	 * 
	 * @param connection
	 * @param query
	 * @return
	 */
	public List<String[]> getRows(String query) {
		List<String[]> rows = new ArrayList<String[]>();

		if (!isConnected())
			return rows;

		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				String[] row = new String[columns];
				for (int i = 1; i <= columns; i++)
					row[i - 1] = rs.getString(i);
				if (row.length > 0)
					rows.add(row);
			}
		} catch (SQLException e) {
			fileLogger.createErrorLog(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					fileLogger.createErrorLog(e);
				}
			}
		}
		return rows;
	}

	public List<String> getColumns(String table) {
		List<String> columns = new ArrayList<String>();
		if (!isConnected())
			return columns;

		String query = "SHOW columns FROM " + table + ";";
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
				columns.add(rs.getString("Field"));
		} catch (SQLException e) {
			fileLogger.createErrorLog(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					fileLogger.createErrorLog(e);
				}
			}
		}
		return columns;
	}

	/**
	 * Executes a database update
	 * 
	 * @param connection
	 * @param update
	 */
	public void executeUpdate(String update) {
		executeUpdate(update, true, fileLogger);
	}

	/**
	 * Executes a database update
	 * 
	 * @param connection
	 * @param update
	 */
	public void executeUpdate(String update, boolean runAfterReconnect, FileLogger logger) {
		if (!isConnected() && runAfterReconnect) {
			runOnConnect(() -> {
				executeUpdate(update, false, logger);
			});
			return;
		}

		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			logger.createErrorLog(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.createErrorLog(e);
				}
			}
		}
	}

	/**
	 * Executes a database update asynchronously be wary of concurrency issues
	 * 
	 * @param connection
	 * @param update
	 */
	public void executeUpdateAsync(String update) {
		new BukkitRunnable() {
			@Override
			public void run() {
				executeUpdate(update, true, new FileLogger(plugin));
			}
		}.runTaskAsynchronously(JonosBukkitUtils.instance);
	}

	/**
	 * Checks to see if the database has a table
	 * 
	 * @param connection
	 * @param tableName
	 * @return
	 */
	public boolean hasTable(String tableName) {
		if (!isConnected())
			return false;

		String query = "SHOW TABLES LIKE '" + tableName + "';";
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
				return true;
		} catch (SQLException e) {
			fileLogger.createErrorLog(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					fileLogger.createErrorLog(e);
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new table, if it doesn't already exist
	 * 
	 * @param tableName
	 * @param columns
	 */
	public void addTable(String tableName, SqlColumn... columns) {
		String update = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
		for (SqlColumn c : columns)
			update += c.name() + " " + c.type().getSqlSyntax() + ", ";
		if (columns.length != 0)
			update = update.substring(0, update.length() - 2);
		update += ");";

		executeUpdate(update);
	}

	public void removeTable(String tableName) {
		String update = "DROP TABLE IF EXISTS '" + tableName + "';";
		executeUpdate(update);
	}

	/**
	 * Adds a single column to the table, if it doesn't exist
	 * 
	 * @param tableName
	 * @param column
	 */
	public void addColumn(String tableName, SqlColumn column) {
		addColumns(tableName, column);
	}

	/**
	 * Adds multiple columns to the table, if they don't exist
	 * 
	 * @param tableName
	 * @param columns
	 */
	public void addColumns(String tableName, SqlColumn... columns) {
		String update = "ALTER TABLE " + tableName + " ";
		List<String> existingColumns = getColumns(tableName);
		for (SqlColumn c : columns)
			if (!existingColumns.contains(c.name()))
				update += "ADD COLUMN '" + c.name() + "' " + c.type().getSqlSyntax() + " NOT NULL, ";

		if (update.contains(",")) {
			update = update.substring(0, update.length() - 2);
			update += ";";
			executeUpdate(update);
		}
	}

	/**
	 * drops a column from a table
	 * 
	 * @param tableName
	 * @param columnName
	 */
	public void removeColumn(String tableName, String columnName) {
		removeColumns(tableName, columnName);
	}

	/**
	 * drops multiple columns from a table
	 * 
	 * @param tableName
	 * @param columnNames
	 */
	public void removeColumns(String tableName, String... columnNames) {
		String update = "ALTER TABLE " + tableName + " ";
		List<String> existingColumns = getColumns(tableName);
		for (String c : columnNames)
			if (!existingColumns.contains(c))
				update += "DROP COLUMN " + c + ", ";

		if (update.contains(",")) {
			update = update.substring(0, update.length() - 2);
			update += ";";
			executeUpdate(update);
		}
	}
}
