package jdz.bukkitUtils.config.SQL;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.config.AutoConfig;
import jdz.bukkitUtils.sql.SQLColumn;
import static jdz.bukkitUtils.sql.SQLColumnType.STRING;
import static jdz.bukkitUtils.sql.SQLColumnType.STRING_32;
import jdz.bukkitUtils.sql.SQLRow;
import jdz.bukkitUtils.sql.SqlDatabase;
import jdz.bukkitUtils.sql.ORM.SQLDataSerialiser;

public class ConfigDBLinker extends SqlDatabase {
	private static final SQLColumn[] columns = { new SQLColumn("Section", STRING_32), new SQLColumn("Key", STRING_32),
			new SQLColumn("Value", STRING) };

	private final String tableName;

	public ConfigDBLinker(Plugin plugin) {
		super(plugin);

		tableName = plugin.getName() + "_config";

		runOnConnect(() -> {
			addTable(tableName, columns);
			addIndex(tableName, "sectionIndex", columns[0]);
		});
	}

	public void reloadConfig(AutoConfig config) {
		List<SQLRow> rows = query(
				"SELECT Key, Value from " + tableName + " WHERE section='" + config.getSection() + "';");
		Map<String, String> keyToValue = new HashMap<>();
		for (SQLRow row : rows)
			keyToValue.put(row.get(0), row.get(1));

		try {
			for (Field field : config.getFields()) {
				String key = config.getSection() + field.getName();
				if (keyToValue.containsKey(key)) {
					String stringValue = keyToValue.get(key);
					Object val = SQLDataSerialiser.getParser(field.getType()).parse(stringValue);
					field.set(config, val);
				}
			}
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void applyChanges(AutoConfig config) {
		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			try {
				PreparedStatement s = dbConnection
						.prepareStatement("REPLACE INTO " + tableName + " (Section, Key, Value) VALUES(?,?,?);");

				s.setString(0, config.getSection());
				try {
					for (Field field : config.getFields()) {
						String key = config.getSection() + field.getName();
						String val = SQLDataSerialiser.getSerialiser(field.getClass()).serialise(field.get(config));
						s.setString(1, key);
						s.setString(2, val);
						updateAsync(s);
					}
				}
				catch (ReflectiveOperationException | SQLException e) {
					e.printStackTrace();
				}
			}
			catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
	}
}
