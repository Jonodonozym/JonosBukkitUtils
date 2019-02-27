
package jdz.bukkitUtils.events.custom;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.Config;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigReloadEvent extends Event implements Cancellable {
	private final Plugin plugin;
	private final FileConfiguration config;
	private final String name;

	public ConfigReloadEvent(Plugin plugin) {
		this(plugin, "config.yml");
	}

	public ConfigReloadEvent(Plugin plugin, String fileName) {
		this.plugin = plugin;
		config = Config.getConfig(plugin, fileName);
		if (!fileName.endsWith(".yml"))
			fileName = fileName + ".yml";
		name = fileName;
	}

	public File getConfigFile() {
		return Config.getConfigFile(plugin, name);
	}

	public static HandlerList getHandlerList() {
		return getHandlers(ConfigReloadEvent.class);
	}

	static class ConfigReloadOnLaunch implements Listener {
		@EventHandler
		public void onLoad(PluginEnableEvent event) {
			Bukkit.getScheduler().runTask(event.getPlugin(), () -> {
				new ConfigReloadEvent(event.getPlugin()).call();
			});
		}
	}
}
