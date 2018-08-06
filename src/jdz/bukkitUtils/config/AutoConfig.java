
package jdz.bukkitUtils.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.bukkitUtils.misc.Config;

public abstract class AutoConfig implements Listener {
	private final Plugin plugin;
	private final String fileName;
	private final String section;

	private final List<Field> fields = new ArrayList<Field>();

	protected AutoConfig(Plugin plugin) {
		this(plugin, "", "config.yml");
	}

	protected AutoConfig(Plugin plugin, String configSection) {
		this(plugin, configSection, "config.yml");
	}

	protected AutoConfig(Plugin plugin, String configSection, String file) {
		this.plugin = plugin;
		this.section = configSection;
		this.fileName = file;

		for (Field field : getClass().getDeclaredFields())
			if (field.getAnnotation(NotConfig.class) != null) {
				field.setAccessible(true);
				fields.add(field);
			}
	}

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(plugin))
			return;

		if (!event.getName().equals(fileName))
			return;

		FileConfiguration config = event.getConfig();
		ConfigurationSection configSection = config.getConfigurationSection(section);
		reloadConfig(configSection);
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

	private void reloadConfig(ConfigurationSection section) {
		try {
			for (Field field : fields) {
				Object val = AutoConfigFieldParsers.getParser(field.getType()).parse(section, field.getName());
				field.set(this, val);
			}
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private void writeConfig(ConfigurationSection section) {
		try {
			for (Field field : fields) {
				Object val = field.get(this);
				AutoConfigFieldParsers.getSerializer(field.getType()).save(section, field.getName(), val);
			}
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}
