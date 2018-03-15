
package jdz.bukkitUtils.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SqlRow {
	private final LinkedHashMap<String, String> columnToValue;
	private final List<String> values = new ArrayList<String>();
	
	SqlRow(LinkedHashMap<String, String> columnToValue) {
		this.columnToValue = columnToValue;
		for (String s: columnToValue.values())
			values.add(s);
	}
	
	public String get(int index) {
		return values().get(index);
	}
	
	public String get(String columnName) {
		String upperName = columnName.toUpperCase();
		if (!columnToValue.containsKey(upperName))
			throw new IllegalArgumentException(upperName + " is not a valid column name! names are: "+columnToValue.keySet());
		return columnToValue.get(upperName);				
	}
	
	public String get(SqlColumn column) {
		return get(column.getName());
	}
	
	public int length() {
		return values.size();
	}
	
	public List<String> values(){
		return values;
	}
}
