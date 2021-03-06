
package jdz.bukkitUtils.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class SQLRow {
	private final LinkedHashMap<String, String> columnToValue;
	private final List<String> values = new ArrayList<>();

	SQLRow(LinkedHashMap<String, String> columnToValue) {
		this.columnToValue = columnToValue;
		for (String s : columnToValue.values())
			values.add(s);
	}

	public String get(int index) {
		return values().get(index);
	}

	public boolean has(String columnName) {
		return columnToValue.containsKey(columnName.toUpperCase());
	}

	public String get(String columnName) {
		if (!has(columnName))
			throw new IllegalArgumentException(
					columnName.toUpperCase() + " is not a valid column name! names are: " + columnToValue.keySet());
		return columnToValue.get(columnName.toUpperCase());
	}

	public boolean has(SQLColumn column) {
		return has(column.getName());
	}

	public String get(SQLColumn column) {
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
