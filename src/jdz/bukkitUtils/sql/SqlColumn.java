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
public final class SqlColumn{
	private final String name;
	private final SqlColumnType type;
	@Getter private final String Default;
	@Getter private final boolean primary;

	public SqlColumn(String name, SqlColumnType type){
		this.name = name;
		this.type = type;
		this.Default = type.getDefaultStatement();
		this.primary = false;
	}
	
	public SqlColumn(String name, SqlColumnType type, String Default){
		this.name = name;
		this.type = type;
		this.Default = " DEFAULT "+Default;
		this.primary = false;
	}
	
	public SqlColumn(String name, SqlColumnType type, boolean primary){
		this.name = name;
		this.type = type;
		this.Default = type.getDefaultStatement();
		this.primary = primary;
	}
	
	public String name(){
		return name;
	}
	
	public SqlColumnType getType(){
		return type;
	}
}
