
package jdz.bukkitUtils.events;

import java.util.HashSet;
import java.util.Set;

public interface Cancellable extends org.bukkit.event.Cancellable{
	static Set<Cancellable> set = new HashSet<Cancellable>();
	
	public default boolean isCancelled() {
		return set.contains(this);
	}
	
	public default void setCancelled(boolean isCancelled) {
		if (isCancelled)
			set.add(this);
		else
			set.remove(this);
	}
}
