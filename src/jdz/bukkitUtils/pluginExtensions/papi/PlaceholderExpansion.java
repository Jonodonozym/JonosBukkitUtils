
package jdz.bukkitUtils.pluginExtensions.papi;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
	private final Plugin plugin;

	@Override
	public String getAuthor() {
		List<String> authors = plugin.getDescription().getAuthors();
		if (authors != null && !authors.isEmpty())
			return authors.get(0);
		return null;
	}

	@Override
	public String getIdentifier() {
		return plugin.getName();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean register() {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
			return false;
		
		return super.register();
	}
}
