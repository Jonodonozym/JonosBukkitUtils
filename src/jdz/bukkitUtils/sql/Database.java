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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class with static methods to interact with the sql database
 * 
 * @author Jonodonozym
 */
public abstract class Database {
	protected Connection dbConnection = null;
	private boolean isAutoReconnecting = false;

	private final List<Runnable> runOnConnect = new ArrayList<Runnable>();

	protected SQLConfig config;

	protected Database() {}

	protected Database(SQLConfig config) {
		setConfig(config);
	}

	protected void setConfig(SQLConfig config) {
		try {
			Class.forName(config.driver.getClasspath()).newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.config = config;

		if (config.isValid())
			openConnection(true);
		else
			throw new NullPointerException("Fields in the SQL config cannot be null or empty!");
	}

	public boolean isConnected() {
		try {
			if (dbConnection != null && !dbConnection.isClosed())
				return true;
		}
		catch (SQLException e) {}
		if (dbConnection != null && config.isValid())
			autoReconnect();
		return false;
	}

	/**
	 * Executes a query, returning the rows if the database responds with them
	 * 
	 * @param query
	 * @return
	 */
	public List<SQLRow> query(String query) {
		List<SQLRow> rows = new ArrayList<SQLRow>();

		if (!isConnected())
			return rows;

		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				for (int i = 1; i <= columns; i++) {
					String str = rs.getString(i);
					if ("null".equals(str))
						str = null;
					row.put(rs.getMetaData().getColumnName(i).toUpperCase(), str);
				}
				if (row.size() > 0)
					rows.add(new SQLRow(row));
			}
		}
		catch (SQLException e) {
			onError(e, query);
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					onError(e, query);
				}
			}
		}
		return rows;
	}

	public SQLRow queryFirst(String query) {
		if (query.endsWith(";"))
			query = query.substring(0, query.length() - 1);
		if (!query.contains("LIMIT 1"))
			query += " LIMIT 1;";
		List<SQLRow> rows = query(query);
		if (rows.isEmpty())
			return null;
		return rows.get(0);
	}

	public List<SQLRow> query(PreparedStatement statement) {
		return query(statement.toString());
	}

	public SQLRow queryFirst(PreparedStatement statement) {
		return queryFirst(statement.toString());
	}

	protected List<String> getColumns(String table) {
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
		}
		catch (SQLException e) {
			onError(e, query);
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					onError(e, query);
				}
			}
		}
		return columns;
	}

	/**
	 * Executes a database update
	 * 
	 * @param update
	 */
	public boolean update(String update) {
		if (!isConnected()) {
			runOnConnect(() -> {
				update(update);
			});
			return false;
		}

		try {
			return update(dbConnection.prepareStatement(update));
		}
		catch (SQLException e) {
			onError(e, update);
			return false;
		}
	}

	public boolean update(PreparedStatement statement) {
		try {
			return statement.execute();
		}
		catch (SQLException exception) {
			onError(exception, statement.toString());
			return false;
		}
	}

	private final Executor executor = Executors.newCachedThreadPool();

	/**
	 * Executes a database update asynchronously be wary of concurrency issues
	 * 
	 * @param update
	 */
	protected void updateAsync(String update) {
		try {
			if (!isConnected()) {
				runOnConnect(() -> {
					updateAsync(update);
				});
				return;
			}

			updateAsync(dbConnection.prepareStatement(update));
		}
		catch (SQLException e) {
			onError(e, update);
		}
	}

	protected void updateAsync(PreparedStatement update) {
		executor.execute(() -> {
			update(update);
		});
	}

	/**
	 * Checks to see if the database has a table
	 * 
	 * @param tableName
	 * @return
	 */
	protected boolean hasTable(String tableName) {
		if (!isConnected())
			return false;

		for (SQLRow row : query("SHOW TABLES LIKE '" + tableName + "';"))
			if (row.get(0).equals(tableName))
				return true;

		return false;
	}

	/**
	 * Creates a new table, if it doesn't already exist
	 * 
	 * @param tableName
	 * @param columns
	 */
	public void addTable(String tableName, SQLColumn... columns) {
		if (!isConnected()) {
			runOnConnect(() -> {
				addTable(tableName, columns);
			});
			return;
		}

		if (!hasTable(tableName)) {
			String update = "CREATE TABLE IF NOT EXISTS " + tableName;
			if (columns != null && columns.length > 0) {
				update = update + " (";
				for (SQLColumn c : columns)
					update += c.getName() + " " + c.getType().getSqlSyntax() + " NOT NULL"
							+ c.getType().getDefaultStatement() + ", ";
				if (columns.length != 0)
					update = update.substring(0, update.length() - 2);
			}
			update += ");";

			update(update);
		}
		else if (columns != null && columns.length > 0)
			addColumns(tableName, columns);
		if (columns != null && columns.length > 0)
			updatePrimaryKeys(tableName, columns);
	}

	protected void removeTable(String tableName) {
		String update = "DROP TABLE IF EXISTS " + tableName + ";";
		update(update);
	}

	/**
	 * Adds a single column to the table, if it doesn't exist
	 * 
	 * @param tableName
	 * @param column
	 */
	protected void addColumn(String tableName, SQLColumn column) {
		addColumns(tableName, column);
	}

	/**
	 * Adds multiple columns to the table, if they don't exist
	 * 
	 * @param tableName
	 * @param columns
	 */
	protected void addColumns(String tableName, SQLColumn... columns) {
		if (columns == null || columns.length == 0)
			return;

		if (!isConnected()) {
			runOnConnect(() -> {
				addColumns(tableName, columns);
			});
			return;
		}

		String update = "ALTER TABLE " + tableName + " ";
		List<String> existingColumns = getColumns(tableName);
		List<String> primaryKeys = getPrimaryKeys(tableName);
		for (SQLColumn c : columns) {
			if (!containsEqualsIgnoreCase(existingColumns, c.getName())) {
				update += "ADD COLUMN " + c.getName() + " " + c.getType().getSqlSyntax() + " NOT NULL" + c.getDefault()
						+ ", ";
				if (c.isPrimary())
					primaryKeys.add(c.getName());
			}
		}

		if (update.contains(",")) {
			update = update.substring(0, update.length() - 2);
			update += ";";
			update(update);
			setPrimaryKeys(tableName, primaryKeys.toArray(new String[primaryKeys.size()]));
		}
	}

	private boolean containsEqualsIgnoreCase(List<String> list, String string) {
		String lowerCase = string.toLowerCase();
		for (String s : list)
			if (s.toLowerCase().equals(lowerCase))
				return true;
		return false;
	}

	/**
	 * drops a column from a table
	 * 
	 * @param tableName
	 * @param column
	 */
	protected void removeColumn(String tableName, String column) {
		removeColumns(tableName, column);
	}

	/**
	 * drops multiple columns from a table
	 * 
	 * @param tableName
	 * @param columns
	 */
	protected void removeColumns(String tableName, String... columns) {
		if (columns == null || columns.length == 0)
			return;

		if (!isConnected()) {
			runOnConnect(() -> {
				removeColumns(tableName, columns);
			});
			return;
		}

		String update = "ALTER TABLE " + tableName + " ";
		List<String> existingColumns = getColumns(tableName);
		for (String c : columns)
			if (!existingColumns.contains(c))
				update += "DROP COLUMN " + c + ", ";

		if (update.contains(",")) {
			update = update.substring(0, update.length() - 2);
			update += ";";
			update(update);
		}
	}

	protected List<String> getPrimaryKeys(String table) {
		List<String> keys = new ArrayList<String>();
		if (!isConnected())
			return keys;

		String query = "SHOW KEYS FROM " + table + " WHERE Key_name = 'PRIMARY'";
		List<SQLRow> result = query(query);
		for (SQLRow row : result)
			keys.add(row.get("Column_name"));
		return keys;
	}

	protected void updatePrimaryKeys(String table, SQLColumn... columns) {
		List<String> keys = new ArrayList<String>();
		for (SQLColumn c : columns)
			if (c.isPrimary())
				keys.add(c.getName());
		setPrimaryKeys(table, keys.toArray(new String[keys.size()]));
	}

	protected void setPrimaryKeys(String table, String[] keys) {
		if (!isConnected()) {
			runOnConnect(() -> {
				setPrimaryKeys(table, keys);
			});
			return;
		}

		String dropUpdate = "";
		if (!getPrimaryKeys(table).isEmpty())
			dropUpdate = "drop primary key, ";

		if (keys != null && keys.length != 0) {
			String update = "ALTER TABLE " + table + " " + dropUpdate + "ADD PRIMARY KEY (";
			for (String s : keys)
				update += s + ", ";
			update = update.substring(0, update.length() - 2) + ");";
			update(update);
		}
	}



	/**
	 * Opens a new connection to a specified SQL database If it fails 3 times,
	 * writes the error to a log file in the plugin's directory
	 * 
	 * @param doLogging whether or not to log errors
	 * @return the opened connection, or null if one couldn't be created
	 */
	private void openConnection(boolean doLogging) {
		closeConnection();

		try {
			String url = "jdbc:" + config.driver.getName() + "://" + config.dbURL + ":" + config.dbPort + "/"
					+ config.dbName + "?user=" + config.dbUsername + "&password=" + config.dbPassword
					+ "&loginTimeout=1000&useSSL=false&autoReconnect=true";

			dbConnection = DriverManager.getConnection(url, config.dbUsername, config.dbPassword);

			// updateAsync("SET SESSION wait_timeout = 999999;");

			for (Runnable r : new ArrayList<Runnable>(runOnConnect)) {
				if (r == null)
					continue;
				r.run();
			}
			runOnConnect.clear();
			return;
		}
		catch (SQLException e) {
			if (doLogging)
				onError(e);
			autoReconnect();
		}
	}

	public void runOnConnect(Runnable r) {
		if (isConnected())
			r.run();
		else
			runOnConnect.add(r);
	}

	/**
	 * Closes a given connection, catching any errors
	 */
	protected boolean closeConnection() {
		if (dbConnection != null) {
			try {
				dbConnection.close();
				dbConnection = null;
				return true;
			}
			catch (SQLException e) {}
		}
		return false;
	}

	private void autoReconnect() {
		if (!isAutoReconnecting) {
			isAutoReconnecting = true;
			new Thread(() -> {
				closeConnection();
				long lastTime = System.currentTimeMillis();
				while (dbConnection == null) {
					openConnection(false);

					long threadDelay = config.dbReconnectTime - (System.currentTimeMillis() - lastTime);
					lastTime = System.currentTimeMillis();

					if (threadDelay > 0)
						try {
							Thread.sleep(threadDelay);
						}
						catch (InterruptedException e) {}
				}

				isAutoReconnecting = false;
			}).run();
		}
	}

	private void onError(Throwable t) {
		onError(t, "");
	}

	protected void onError(Throwable t, String query) {
		t.printStackTrace();
		if (query != null && !query.equals(""))
			System.out.println("Using query: " + query);
	}

	protected boolean executeTransaction(Transaction t) {
		try {
			dbConnection.setAutoCommit(false);
			boolean success = false;
			try {
				success = t.execute();
			}
			catch (Exception ex) {
				onError(ex);
			}
			if (success)
				dbConnection.commit();
			else
				dbConnection.rollback();
			dbConnection.setAutoCommit(true);
			return success;
		}
		catch (SQLException e) {
			onError(e);
			return false;
		}
	}

	public static interface Transaction {
		public boolean execute() throws SQLException;
	}
}
