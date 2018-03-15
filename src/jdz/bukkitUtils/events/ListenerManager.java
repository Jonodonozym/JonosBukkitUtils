
package jdz.bukkitUtils.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import lombok.Getter;

public class ListenerManager implements Listener {
	@Getter private static final ListenerManager instance = new ListenerManager();
	private ListenerManager() {
		register(this, JonosBukkitUtils.getInstance());
	}
	
	private static Map<Plugin, Set<org.bukkit.event.Listener>> registered = new HashMap<Plugin, Set<org.bukkit.event.Listener>>();

	boolean register(org.bukkit.event.Listener l, Plugin plugin) {
		if (!registered.containsKey(plugin))
			registered.put(plugin, new HashSet<org.bukkit.event.Listener>());
		if (registered.get(plugin).contains(l))
			throw new IllegalStateException("Listener cannot be added multiple times");

		registered.get(plugin).add(l);
		Bukkit.getPluginManager().registerEvents(l, plugin);
		return true;
	}

	@EventHandler
	public void onUnload(PluginDisableEvent event) {
		registered.remove(event.getPlugin());
	}
}
