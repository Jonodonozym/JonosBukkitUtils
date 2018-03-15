/**
 * SqlColumn.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a column in an sql table
 *
 * @author Jaiden Baker
 */
@AllArgsConstructor
public final class SqlColumn {
	@Getter private final String name;
	@Getter private final SqlColumnType type;
	@Getter private final String Default;
	@Getter private final boolean primary;

	public SqlColumn(String name, SqlColumnType type) {
		this(name, type, type.getDefaultStatement(), false);
	}

	public SqlColumn(String name, SqlColumnType type, String Default) {
		this(name, type, " DEFAULT " + Default, false);
	}

	public SqlColumn(String name, SqlColumnType type, boolean primary) {
		this(name, type, type.getDefaultStatement(), primary);
	}
}
