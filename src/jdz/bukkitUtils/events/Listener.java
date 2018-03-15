
package jdz.bukkitUtils.events;

import org.bukkit.plugin.Plugin;

public interface Listener extends org.bukkit.event.Listener{
	public default boolean registerEvents(Plugin plugin) {
		return ListenerManager.getInstance().register(this, plugin);
	}
}
