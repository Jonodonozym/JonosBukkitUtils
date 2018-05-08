
package jdz.bukkitUtils.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class SqlRow {
	private final LinkedHashMap<String, String> columnToValue;
	private final List<String> values = new ArrayList<String>();

	SqlRow(LinkedHashMap<String, String> columnToValue) {
		this.columnToValue = columnToValue;
		for (String s : columnToValue.values())
			values.add(s);
	}

	public String get(int index) {
		return values().get(index);
	}

	public String get(String columnName) {
		String upperName = columnName.toUpperCase();
		if (!columnToValue.containsKey(upperName))
			throw new IllegalArgumentException(
					upperName + " is not a valid column name! names are: " + columnToValue.keySet());
		return columnToValue.get(upperName);
	}

	public String get(SqlColumn column) {
		return get(column.getName());
	}

	public int length() {
		return values.size();
	}

	public List<String> values() {
		return values;
	}

	@Override
	public String toString() {
		if (columnToValue.isEmpty())
			return "{}";

		String s = "";
		for (Entry<String, String> entry : columnToValue.entrySet())
			s += entry.getKey() + ": " + entry.getValue() + ",   ";
		if (!columnToValue.isEmpty())
			s = s.substring(0, s.length() - 4);
		return "{" + s + "}";
	}
}
