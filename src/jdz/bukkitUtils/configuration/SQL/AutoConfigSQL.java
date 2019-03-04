
package jdz.bukkitUtils.configuration.SQL;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.configuration.AutoConfig;

public abstract class AutoConfigSQL extends AutoConfig {
	protected AutoConfigSQL(Plugin plugin) {
		super(plugin);
	}

	protected AutoConfigSQL(Plugin plugin, String configSection) {
		super(plugin, configSection);
	}

	protected AutoConfigSQL(Plugin plugin, String configSection, String file) {
		super(plugin, configSection, file);
	}

	@Override
	public void saveChanges() {
		super.saveChanges();
		ConfigDBManager.get(getPlugin()).applyChanges(this);
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		ConfigDBManager.get(getPlugin()).reloadConfig(this);
	}
}
