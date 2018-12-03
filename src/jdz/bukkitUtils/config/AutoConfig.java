
package jdz.bukkitUtils.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.bukkitUtils.events.custom.ConfigSaveEvent;
import jdz.bukkitUtils.misc.Config;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AutoConfig implements Listener {
	@Getter private final Plugin plugin;
	@Getter private final String fileName;
	@Getter private final String section;

	@Getter(value = AccessLevel.PACKAGE) private final List<Field> fields = new ArrayList<Field>();

	protected AutoConfig(Plugin plugin) {
		this(plugin, "", "config.yml");
	}

	protected AutoConfig(Plugin plugin, String configSection) {
		this(plugin, configSection, "config.yml");
	}

	protected AutoConfig(Plugin plugin, String configSection, String file) {
		this.plugin = plugin;
		this.section = configSection;
		if (!file.endsWith(".yml"))
			file = file + ".yml";
		this.fileName = file;

		for (Field field : getClass().getDeclaredFields())
			if (field.getAnnotation(NotConfig.class) != null) {
				field.setAccessible(true);
				fields.add(field);
			}
	}
	
	public void register() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(plugin))
			return;

		if (!event.getName().equals(fileName))
			return;

		reloadConfig();
		saveChanges();
	}

	@EventHandler
	public void onConfigSave(ConfigSaveEvent event) {
		if (!event.getPlugin().equals(plugin))
			return;

		if (!event.getName().equals(fileName))
			return;

		saveChanges();
	}

	public void saveChanges() {
		FileConfiguration config = Config.getConfig(plugin, fileName);
		ConfigurationSection configSection = config.getConfigurationSection(section);
		if (configSection == null)
			configSection = config.createSection(section);
		writeConfig(configSection);
		try {
			config.save(Config.getConfigFile(plugin, fileName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadConfig() {
		FileConfiguration config = Config.getConfig(plugin, fileName);
		ConfigurationSection configSection = config.getConfigurationSection(section);
		reloadConfig(configSection);
	}

	public void reloadConfig(ConfigurationSection section) {
		try {
			for (Field field : fields) {
				if (section.contains(field.getName())) {
					Object val = AutoConfigFieldParsers.parse(field.getType(), section, field.getName());
					field.set(this, val);
				}
			}
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	protected void writeConfig(ConfigurationSection section) {
		try {
			for (Field field : fields) {
				Object val = field.get(this);
				AutoConfigFieldParsers.save(field.getType(), section, field.getName(), val);
			}
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}
