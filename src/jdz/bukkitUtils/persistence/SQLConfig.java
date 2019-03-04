
package jdz.bukkitUtils.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SQLConfig {
	final String dbURL;
	final String dbName;
	final String dbUsername;
	final String dbPassword;

	SQLDriver driver = SQLDriver.MYSQL_DRIVER;

	boolean preferSQL = false;
	int dbPort = 3306;
	long dbReconnectTime = 60000;

	public SQLConfig(String dbURL, String dbName, String dbUsername, String dbPassword, SQLDriver driver,
			boolean preferSQL) {
		this(dbURL, dbName, dbUsername, dbPassword);
		this.driver = driver;
		this.preferSQL = preferSQL;
	}

	public boolean isValid() {
		return !preferSQL || !(dbURL == null || dbName == null || dbUsername == null || dbPassword == null
				|| dbURL.equals("") || dbName.equals("") || dbUsername.equals("") || dbPassword.equals(""));
	}
}
