
package jdz.bukkitUtils.sql;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SqlConfig {
	final String dbURL;
	final String dbPort;
	final String dbName;
	final String dbUsername;
	final String dbPassword;
	long dbReconnectTime = 60000;

	public boolean isValid() {
		return !(dbURL == null || dbPort == null || dbName == null || dbUsername == null || dbPassword == null
				|| dbURL.equals("") || dbPort.equals("") || dbName.equals("") || dbUsername.equals("")
				|| dbPassword.equals(""));
	}
}
