
package jdz.bukkitUtils.sql;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {
	private static Map<Class<? extends Database>,Database> instances = new HashMap<Class<? extends Database>, Database>();
	
	public static Database getInstance(Class<? extends Database> c) {
		return instances.get(c);
	}

	protected final SqlApi api;
	
	public Database(JavaPlugin plugin) {
		if (instances.get(this.getClass()) == null)
			instances.put(this.getClass(), this);
		else
			throw new IllegalAccessError("Multiple database instances of the same class are not allowed");
		api = new SqlApi(plugin);
	}
}
