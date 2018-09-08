
package jdz.bukkitUtils.events.custom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.bukkitUtils.misc.Config;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigSaveEvent extends Event implements Cancellable {
	private final Plugin plugin;
	private final FileConfiguration config;
	private final String name;

	public ConfigSaveEvent(Plugin plugin) {
		this(plugin, "config.yml");
	}

	public ConfigSaveEvent(Plugin plugin, String fileName) {
		this.plugin = plugin;
		config = Config.getConfig(plugin, fileName);
		if (!fileName.endsWith(".yml"))
			fileName = fileName + ".yml";
		name = fileName;
	}

	public static HandlerList getHandlerList() {
		return getHandlers(ConfigSaveEvent.class);
	}
}
