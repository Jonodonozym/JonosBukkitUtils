
package jdz.bukkitUtils.components.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

public class ListenerManager implements Listener {
	@Getter private static final ListenerManager instance = new ListenerManager();
	private final Map<Plugin, Set<org.bukkit.event.Listener>> registered = new HashMap<>();

	boolean isRegistered(org.bukkit.event.Listener l, Plugin plugin) {
		return registered.containsKey(plugin) && registered.get(plugin).contains(l);
	}

	boolean register(org.bukkit.event.Listener l, Plugin plugin) {
		if (!registered.containsKey(plugin))
			registered.put(plugin, new HashSet<org.bukkit.event.Listener>());
		if (registered.get(plugin).contains(l))
			throw new IllegalStateException("Listener cannot be added multiple times");

		registered.get(plugin).add(l);
		Bukkit.getPluginManager().registerEvents(l, plugin);
		return true;
	}

	void unregister(org.bukkit.event.Listener l, Plugin plugin) {
		if (registered.containsKey(plugin))
			registered.get(plugin).remove(l);
		HandlerList.unregisterAll(l);
	}

	@EventHandler
	public void onUnload(PluginDisableEvent event) {
		registered.remove(event.getPlugin());
	}
}
