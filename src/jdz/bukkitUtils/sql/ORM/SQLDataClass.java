
package jdz.bukkitUtils.sql.ORM;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import jdz.bukkitUtils.sql.Database;
import jdz.bukkitUtils.sql.SQLColumn;
import jdz.bukkitUtils.sql.SQLColumnType;
import jdz.bukkitUtils.sql.SQLRow;
import jdz.bukkitUtils.sql.TableName;
import jdz.bukkitUtils.sql.ORM.SQLDataSerialiser.ParserMethod;

public class SQLDataClass {
	public static <T extends SQLDataClass> List<T> selectAll(Database database, Class<T> clazz) {
		return select(database, clazz, "");
	}
	
	public static <T extends SQLDataClass> List<T> select(Database database, Class<T> clazz, String whereClause) {
		return select(database, clazz, false, whereClause);
	}

	public static <T extends SQLDataClass> List<T> selectForUpdate(Database database, Class<T> clazz,
			String whereClause) {
		return select(database, clazz, true, whereClause);
	}

	private static <T extends SQLDataClass> List<T> select(Database database, Class<T> clazz, boolean forUpdate,
			String whereClause) {
		String query = asSelectQuery(clazz, forUpdate, whereClause);
		List<SQLRow> rows = database.query(query);

		List<T> instances = new ArrayList<T>();
		for (SQLRow row : rows)
			instances.add(parse(clazz, row));
		return instances;
	}

	public static <T extends SQLDataClass> T selectFirst(Database database, Class<T> clazz) {
		return selectFirst(database, clazz, false, "");
	}

	public static <T extends SQLDataClass> T selectFirst(Database database, Class<T> clazz, String whereClause) {
		return selectFirst(database, clazz, false, whereClause);
	}

	public static <T extends SQLDataClass> T selectFirstForUpdate(Database database, Class<T> clazz,
			String whereClause) {
		return selectFirst(database, clazz, true, whereClause);
	}

	private static <T extends SQLDataClass> T selectFirst(Database database, Class<T> clazz, boolean forUpdate,
			String whereClause) {
		String query = asSelectQuery(clazz, forUpdate, whereClause);
		SQLRow row = database.queryFirst(query);
		return parse(clazz, row);
	}

	public boolean insert(Database database) {
		return database.update(asInsert());
	}

	public boolean update(Database database) {
		return database.update(asReplaceUpdate());
	}

	public boolean delete(Database database) {
		return database.update(asDeleteUpdate());
	}

	private static String asSelectQuery(Class<? extends SQLDataClass> clazz, boolean forUpdate, String whereClause) {
		String query = "SELECT ";

		String columns = getColumnStringList(clazz);
		columns = columns.substring(0, columns.length() - 2);

		query += columns;

		query += " FROM " + getTableJoinClause(clazz);
		if (whereClause != null && !whereClause.equals("")) {
			whereClause = whereClause.replaceFirst("(?i)WHERE", "").trim();
			if (whereClause.toLowerCase().startsWith("order"))
				query += " " + whereClause;
			else
				query += " WHERE " + whereClause;
		}
		if (forUpdate)
			query += " FOR UPDATE OF " + getTableName(clazz);
		return query + ";";
	}

	@SuppressWarnings("unchecked")
	private static String getColumnStringList(Class<? extends SQLDataClass> clazz) {
		String str = "";
		for (Field field : clazz.getDeclaredFields())
			if (field.getAnnotation(NoSave.class) != null)
				continue;
			else if (SQLDataClass.class.equals(field.getType().getSuperclass()))
				str += getColumnStringList((Class<? extends SQLDataClass>) field.getType());
			else
				str += getColumnName(field) + ", ";
		return str;
	}

	public static void createTable(Class<? extends SQLDataClass> clazz, Database database) {
		String tableName = getTableName(clazz);

		List<SQLColumn> columns = new ArrayList<SQLColumn>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(NoSave.class) != null)
				continue;

			String name = field.getName();
			if (field.getAnnotation(ColumnName.class) != null)
				name = field.getAnnotation(ColumnName.class).value();

			boolean isKey = field.getAnnotation(PrimaryKey.class) != null;

			SQLColumnType type = SQLColumnType.getBest(field.getType());
			columns.add(new SQLColumn(name, type, isKey));
		}

		database.addTable(tableName, columns.toArray(new SQLColumn[columns.size()]));
	}

	public static void createIndex(Class<? extends SQLDataClass> clazz, String fieldName, Database database) {
		Field field = null;
		for (Field f : clazz.getDeclaredFields())
			if (f.getName().equalsIgnoreCase(fieldName) || f.getAnnotation(ColumnName.class) != null
					&& f.getAnnotation(ColumnName.class).value().equalsIgnoreCase(fieldName)) {
				field = f;
				break;
			}
		if (field == null)
			throw new IllegalArgumentException("No field in " + clazz.getSimpleName() + " named " + fieldName);

		String table = getTableName(clazz);

		String columnName = field.getName();
		if (field.getAnnotation(ColumnName.class) != null)
			columnName = field.getAnnotation(ColumnName.class).value();
		String indexName = columnName + "_index";

		String update = "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = '" + indexName
				+ "' AND object_id = OBJECT_ID('" + table + "')) " + "BEGIN " + "CREATE INDEX " + indexName + " ON "
				+ table + " (" + columnName + ");" + "END;";

		database.update(update);
	}

	@SuppressWarnings("unchecked")
	private static String getTableJoinClause(Class<? extends SQLDataClass> clazz) {
		String str = getTableName(clazz);
		for (Field field : clazz.getDeclaredFields())
			if (SQLDataClass.class.equals(field.getType().getSuperclass()))
				str += " NATURAL JOIN " + getTableName((Class<? extends SQLDataClass>) field.getType());
		return str;
	}

	public static String getTableName(Class<? extends SQLDataClass> clazz) {
		if (clazz.getDeclaredAnnotation(TableName.class) != null)
			return clazz.getDeclaredAnnotation(TableName.class).value();
		return clazz.getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public static <T extends SQLDataClass> T parse(Class<T> clazz, SQLRow row) {
		if (row == null)
			return null;

		try {
			Object[] args = new Object[clazz.getDeclaredFields().length];
			for (int j = 0; j < clazz.getDeclaredFields().length; j++) {
				Field field = clazz.getDeclaredFields()[j];
				Class<?> paramClass = field.getType();

				if (paramClass.getSuperclass() != null && paramClass.getSuperclass().equals(SQLDataClass.class)) {
					args[j] = parse((Class<? extends SQLDataClass>) paramClass, row);
					continue;
				}

				if (!SQLDataSerialiser.hasMethods(paramClass)) {
					String cName = paramClass.getSimpleName();
					throw new IllegalArgumentException("Parser method for class " + cName
							+ " is not defined. Add it with\n SQLDataClass.addParserSerializer(" + cName
							+ ".class,(s)->{return new " + cName + "(s);}, (" + cName + ")->{ return " + cName
							+ ".toString(); });");
				}

				ParserMethod<?> method = SQLDataSerialiser.getParser(paramClass);
				String serializedObject = row.get(getColumnName(field));
				if (serializedObject == null)
					args[j] = null;
				else
					args[j] = cast(paramClass, method.parse(serializedObject));
			}
			return (T) clazz.getDeclaredConstructors()[0].newInstance(args);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("SQL row " + row
					+ " does not have column names that match the constructor parameter names of class "
					+ clazz.getSimpleName());
		}
	}

	private static String getColumnName(Field field) {
		if (field.getAnnotation(ColumnName.class) != null)
			return field.getAnnotation(ColumnName.class).value();
		return field.getName();
	}

	@SuppressWarnings("unchecked")
	private static <E> E cast(Class<E> clazz, Object o) {
		return (E) o;
	}

	private String asInsert() {
		String update = "INSERT INTO " + getTableName(getClass()) + " (";

		Map<Field, String> fieldToValues = getFieldValues((f) -> {
			return true;
		});

		for (Field field : fieldToValues.keySet())
			update += getColumnName(field) + ", ";
		update = update.substring(0, update.length() - 2);

		update += ") VALUES(";
		for (Field field : fieldToValues.keySet())
			update += fieldToValues.get(field) + ", ";
		update = update.substring(0, update.length() - 2);

		update += ");";
		return update;
	}

	private String asReplaceUpdate() {
		String update = "UPDATE " + getTableName(getClass()) + " SET ";

		Map<Field, String> fieldToValues = getFieldValues((f) -> {
			return true;
		});

		if (fieldToValues.isEmpty())
			return ";";

		for (Entry<Field, String> fieldToValue : fieldToValues.entrySet())
			update += getColumnName(fieldToValue.getKey()) + "=" + fieldToValue.getValue() + ", ";
		update = update.substring(0, update.length() - 2);

		Map<Field, String> primaryKeys = getFieldValues((f) -> {
			return f.getAnnotation(PrimaryKey.class) != null;
		});

		update += " WHERE ";
		for (Entry<Field, String> primaryKey : primaryKeys.entrySet())
			update += getColumnName(primaryKey.getKey()) + "=" + primaryKey.getValue() + " AND ";
		update = update.substring(0, update.length() - 5);

		update += ";";
		return update;
	}

	private Map<Field, String> getFieldValues(Predicate<Field> shouldFetch) {
		Map<Field, String> fields = new LinkedHashMap<Field, String>();
		for (Field field : getClass().getDeclaredFields())
			if (shouldFetch.test(field)) {
				if (!field.isAccessible())
					field.setAccessible(true);
				Class<?> clazz = field.getType();

				if (clazz.getSuperclass() != null && clazz.getSuperclass().equals(SQLDataClass.class)) {
					try {
						SQLDataClass referencedObject = (SQLDataClass) field.get(this);
						fields.putAll(referencedObject.getFieldValues(shouldFetch.and((f) -> {
							return f.getAnnotation(PrimaryKey.class) != null;
						})));
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
					continue;
				}

				if (!SQLDataSerialiser.hasMethods(clazz)) {
					String cName = clazz.getName();
					throw new IllegalArgumentException("Serialiser for class " + cName
							+ " is not defined. Add it with\n SQLDataClass.addParserSerializer(" + cName
							+ ".class,(s)->{return new " + cName + "(s);}, (" + cName + ")->{ return " + cName
							+ ".toString(); });");
				}

				Object obj;
				try {
					obj = field.get(this);
					@SuppressWarnings("unchecked") String value = SQLDataSerialiser.getSerialiser(clazz).serialise(obj);
					if (isQuoted(clazz))
						fields.put(field, "'" + value + "'");
					else
						fields.put(field, value);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		return fields;
	}

	private String asDeleteUpdate() {
		String update = "DELETE FROM " + getTableName(getClass()) + " WHERE ";

		Map<Field, String> fieldToValues = getFieldValues((f) -> {
			return f.getAnnotation(PrimaryKey.class) != null;
		});
		for (Entry<Field, String> entry : fieldToValues.entrySet())
			update += entry.getKey().getName() + "=" + entry.getValue() + " AND ";
		update = update.substring(0, update.length() - 5);
		update += ";";
		return update;
	}

	private static boolean isQuoted(Class<?> clazz) {
		return !nonQuotedClass.contains(clazz);
	}

	private static final Set<Class<?>> nonQuotedClass = new HashSet<Class<?>>(
			Arrays.asList(int.class, short.class, double.class, float.class, long.class, boolean.class, Integer.class,
					Short.class, Double.class, Float.class, Long.class, Boolean.class));

	@Override
	public String toString() {
		String s = getTableName(getClass()) + ": {";
		for (Entry<Field, String> fieldToValue : getFieldValues((f) -> {
			return f.getAnnotation(NoSave.class) == null;
		}).entrySet()) {
			if (fieldToValue.getKey().getAnnotation(PrimaryKey.class) != null)
				s += "(PK)";
			s += fieldToValue.getKey().getName() + "=" + fieldToValue.getValue();
			s += ", ";
		}
		return s.substring(0, s.length() - 2) + "}";
	}

	/**
	 * Does not work with nested types
	 * 
	 * @param c
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SQLDataClass> T fromString(Class<T> c, String s) {
		try {
			String prefix = getTableName(c) + ": \\{";

			s = s.replaceFirst(prefix, "");
			s = s.substring(0, s.length() - 1);
			s = s.replaceAll("(PK)", "");
			String[] parts = s.split(", ");

			Class<?>[] parameters = c.getConstructors()[0].getParameterTypes();
			Object[] args = new Object[parameters.length];

			int i = 0;
			for (int j = 0; j < c.getDeclaredFields().length; j++) {
				Field field = c.getDeclaredFields()[j];
				if (field.getAnnotation(NoSave.class) != null)
					continue;

				Class<?> paramClass = field.getType();

				if (!SQLDataSerialiser.hasMethods(paramClass)) {
					String cName = paramClass.getSimpleName();
					throw new IllegalArgumentException("Parser method for class " + cName
							+ " is not defined. Add it with SQLDataClass.addParserSerializer");
				}

				ParserMethod<?> method = SQLDataSerialiser.getParser(paramClass);
				String serializedObject = parts[i].substring(parts[i].indexOf('=') + 1, parts[i].length());
				i++;
				if (isQuoted(paramClass))
					serializedObject = serializedObject.substring(1, serializedObject.length() - 1);
				if (serializedObject == null)
					args[j] = null;
				else
					args[j] = cast(paramClass, method.parse(serializedObject));
			}

			return (T) c.getDeclaredConstructors()[0].newInstance(args);
		}
		catch (Exception e) {
			System.out.println(s);
			throw new RuntimeException(e);
		}
	}

	public String toLongString() {
		String s = "";
		for (Entry<Field, String> fieldToValue : getFieldValues((f) -> {
			return f.getAnnotation(NoSave.class) != null;
		}).entrySet()) {
			String value = fieldToValue.getValue();
			if (value != null && value.startsWith("'"))
				value = value.substring(1, value.length() - 1);
			s += fieldToValue.getKey().getName() + ": " + value + "\n";
		}
		return s.substring(0, s.length() - 1);
	}

	public String toShortString() {
		String s = "";
		for (Entry<Field, String> fieldToValue : getFieldValues((f) -> {
			return f.getAnnotation(NoSave.class) != null;
		}).entrySet())
			s += fieldToValue.getValue() + ", ";
		return s.substring(0, s.length() - 2);
	}
}
