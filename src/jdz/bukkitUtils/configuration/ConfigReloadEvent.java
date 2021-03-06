
package jdz.bukkitUtils.configuration;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.components.events.Cancellable;
import jdz.bukkitUtils.components.events.Event;
import jdz.bukkitUtils.components.events.Listener;
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

	public static class ConfigReloadOnLaunch implements Listener {
		@EventHandler
		public void onLoad(PluginEnableEvent event) {
			if (event.getPlugin() == JonosBukkitUtils.getInstance())
				return;
			Bukkit.getScheduler().runTask(JonosBukkitUtils.getInstance(), () -> {
				new ConfigReloadEvent(event.getPlugin()).call();
			});
		}
	}
}
