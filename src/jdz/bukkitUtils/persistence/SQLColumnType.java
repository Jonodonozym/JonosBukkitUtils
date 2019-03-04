/**
 * SQLColumnType.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.persistence;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * Enumerator for SQL column types to make your life slightly easier
 * probably not worth the lines of code, but oh well, made it anyway.
 *
 * @author Jonodonozym
 */
public enum SQLColumnType {
	INT_1_BYTE("tinyint", byte.class, Byte.class), INT_2_BYTE("smallint", short.class, Short.class), INT_3_BYTE(
			"mediumint"), INT_4_BYTE("int"), INT("int", int.class, Integer.class), LONG("bigint", long.class,
					Long.class), DOUBLE("double", double.class,
							Double.class), BOOLEAN("boolean", boolean.class, Boolean.class),

	STRING_16("varchar(16)"), STRING_32("varchar(32)"), STRING_64("varchar(64)"), STRING_128(
			"varchar(128)"), STRING_256("varchar(256)"), STRING_512("varchar(512)"), STRING_1024(
					"varchar(1024)"), STRING("text", String.class), STRING_LARGE("mediumtext");

	@Getter private final String sqlSyntax;
	@Getter private final List<Class<?>> javaClasses;

	private SQLColumnType(String sqlSyntax, Class<?>... classes) {
		this.sqlSyntax = sqlSyntax;
		javaClasses = Arrays.asList(classes);
	}

	String getDefaultStatement() {
		if (this == STRING || this == STRING_LARGE)
			return "";
		if (name().startsWith("STRING"))
			return " DEFAULT ''";
		return " DEFAULT 0";
	}

	public boolean isString() {
		return name().startsWith("STRING");
	}

	public String format(String s) {
		if (name().startsWith("STRING"))
			return "'" + s + "'";
		return s;
	}

	public static SQLColumnType getBest(Class<?> clazz) {
		for (SQLColumnType type : values())
			for (Class<?> javaClazz : type.getJavaClasses())
				if (javaClazz.equals(clazz))
					return type;
		return STRING;
	}
}
