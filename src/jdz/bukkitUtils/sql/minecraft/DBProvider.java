
package jdz.bukkitUtils.sql.minecraft;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.misc.Config;

public class DBProvider<T> {
	private boolean preferSQL;
	private final DBProviderMethod<T> SQLMethod;
	private final DBProviderMethod<T> YMLMethod;
			
	public DBProvider(Plugin plugin, DBProviderMethod<T> SQLMethod, DBProviderMethod<T> YMLMethod) {
		preferSQL = Config.getSQLConfig(plugin).isPreferSQL();
		this.SQLMethod = SQLMethod;
		this.YMLMethod = YMLMethod;
	}
	
	public T get() {
		if (preferSQL)
			return SQLMethod.provide();
		return YMLMethod.provide();
	}
	
	public interface DBProviderMethod<T> {
		public T provide();
	}
}
