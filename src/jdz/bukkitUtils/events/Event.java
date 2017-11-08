
package jdz.bukkitUtils.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public abstract class Event extends org.bukkit.event.Event{
	@SuppressWarnings("rawtypes")
	private static final Map<Class, HandlerList> handlers = new HashMap<Class, HandlerList>();
	
	@Override
	public HandlerList getHandlers() {
		if (!handlers.containsKey(getClass()))
			handlers.put(getClass(), new HandlerList());
		return handlers.get(getClass());
	}
	
	public void call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
	}

}
