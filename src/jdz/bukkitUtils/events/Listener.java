
package jdz.bukkitUtils.events;

import org.bukkit.plugin.Plugin;

public interface Listener extends org.bukkit.event.Listener{
	public default boolean registerEvents(Plugin plugin) {
		return ListenerManager.getInstance().register(this, plugin);
	}
	
	public default void unregisterEvents(Plugin plugin) {
		ListenerManager.getInstance().unregister(this, plugin);
	}
}
