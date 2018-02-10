
package jdz.bukkitUtils.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public abstract class Event extends org.bukkit.event.Event{
	private static final Map<String, HandlerList> handlers = new HashMap<String, HandlerList>();
	
	@Override
	public HandlerList getHandlers() {
		return getHandlers(this.getClass());
	}
	
	protected static HandlerList getHandlers(Class<? extends Event> c) {
		String className = c.getName();
		if (!handlers.containsKey(className))
			handlers.put(className, new HandlerList());
		return handlers.get(className);
	}
	
	private boolean isCalled = false;
	
	public void call() {
		if (isCalled)
			throw new IllegalStateException("Events can only be called once");
		
		Bukkit.getServer().getPluginManager().callEvent(this);
		if (this instanceof Cancellable)
			Cancellable.set.remove(this);	
		
		isCalled = true;
	}
	
	public boolean isCalled() {
		return isCalled;
	}
}
