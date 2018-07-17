
package jdz.bukkitUtils.sql;

import lombok.Data;

@Data
public class SQLDriver {
	public static final SQLDriver MYSQL_DRIVER = new SQLDriver("mysql", "com.mysql.jdbc.Driver");
	public static final SQLDriver POSTGRE_DRIVER = new SQLDriver("postgresql", "org.postgresql.Driver");

	private final String name;
	private final String classpath;
}
