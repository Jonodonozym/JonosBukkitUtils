
package jdz.bukkitUtils.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SQLDriver {
	MYSQL("mysql", "com.mysql.jdbc.Driver"), POSTGRESQL("postgresql", "org.postgresql.Driver");

	private final String name;
	private final String classpath;
}
