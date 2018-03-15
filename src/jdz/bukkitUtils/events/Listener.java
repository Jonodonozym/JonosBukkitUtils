
package jdz.bukkitUtils.events;

import org.bukkit.plugin.Plugin;

public interface Listener extends org.bukkit.event.Listener{
	public default void registerEvents(Plugin plugin) {
		ListenerManager.getInstance().register(this, plugin);
	}
}
