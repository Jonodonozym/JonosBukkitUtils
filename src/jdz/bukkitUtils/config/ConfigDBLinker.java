package jdz.bukkitUtils.config;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.sql.SQLColumn;
import jdz.bukkitUtils.sql.SQLColumnType;
import jdz.bukkitUtils.sql.SQLRow;
import jdz.bukkitUtils.sql.SqlDatabase;
import jdz.bukkitUtils.sql.ORM.SQLDataSerialiser;

public class ConfigDBLinker extends SqlDatabase {
	private static final SQLColumn[] columns = { new SQLColumn("Key", SQLColumnType.STRING_32),
			new SQLColumn("Value", SQLColumnType.STRING) };

	private final String tableName;

	public ConfigDBLinker(Plugin plugin) {
		super(plugin);

		tableName = plugin.getName() + "_config";

		runOnConnect(() -> {
			addTable(tableName, columns);
		});
	}

	public void reloadConfig(AutoConfig config) {
		List<SQLRow> rows = query("SELECT Key, Value from " + tableName + ";");
		Map<String, String> keyToValue = new HashMap<String, String>();
		for (SQLRow row : rows)
			keyToValue.put(row.get(0), row.get(1));

		try {
			for (Field field : config.getFields()) {
				String key = config.getSection()+field.getName();
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
						.prepareStatement("REPLACE INTO " + tableName + " (Key, Value) VALUES(?,?);");

				try {
					for (Field field : config.getFields()) {
						String key = config.getSection()+field.getName();
						String val = SQLDataSerialiser.getSerialiser(field.getClass()).serialise(field.get(config));
						s.setString(0, key);
						s.setString(1, val);
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
