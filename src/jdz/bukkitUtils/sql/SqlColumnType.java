/**
 * SqlColumnType.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.sql;

/**
 * Enumerator for SQL column types to make your life slightly easier
 * probably not worth the lines of code, but oh well, made it anyway.
 *
 * @author Jonodonozym
 */
public enum SqlColumnType {
	INT_1_BYTE, INT_2_BYTE, INT_3_BYTE, INT_4_BYTE, LONG, INT, DOUBLE, BOOLEAN, STRING_32, STRING_64, STRING_128, STRING_256, STRING_512, STRING_1024, STRING, STRING_LARGE;

	@SuppressWarnings("incomplete-switch")
	String getSqlSyntax() {
		switch (this) {
		case INT_1_BYTE:
			return "tinyint";
		case INT_2_BYTE:
			return "smallint";
		case INT_3_BYTE:
			return "mediumint";
		case INT_4_BYTE:
		case INT:
			return "int";
		case LONG:
			return "bigint";
		case DOUBLE:
			return "double";
		case BOOLEAN:
			return "boolean";

		case STRING:
			return "text";
		case STRING_LARGE:
			return "mediumtext";
		}

		return "varchar(" + name().substring(name().indexOf("_")).replace("_", "") + ")";
	}

	String getDefaultStatement() {
		if (this == STRING || this == STRING_LARGE)
			return "";
		if (this.name().startsWith("STRING"))
			return " DEFAULT ''";
		return " DEFAULT 0";
	}

	public boolean isString() {
		return name().startsWith("STRING");
	}

	public Class<?> getJavaClass() {
		switch (this) {
		case DOUBLE:
			return Double.class;
		case INT:
			return Integer.class;
		case INT_1_BYTE:
			return Byte.class;
		case INT_2_BYTE:
			return Short.class;
		case INT_3_BYTE:
			return Integer.class;
		case INT_4_BYTE:
			return Integer.class;
		case LONG:
			return Long.class;
		case BOOLEAN:
			return Boolean.class;
		default:
			return String.class;
		}
	}
}
