
package jdz.bukkitUtils.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

public class ConfigDBManager {
	private static final Map<Plugin, ConfigDBLinker> pluginToDB = new HashMap<Plugin, ConfigDBLinker>();
	
	public static ConfigDBLinker get(Plugin plugin) {
		if (!pluginToDB.containsKey(plugin))
			pluginToDB.put(plugin, new ConfigDBLinker(plugin));
		return pluginToDB.get(plugin);
	}
}
