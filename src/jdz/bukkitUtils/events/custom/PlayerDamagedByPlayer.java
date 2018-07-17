
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import jdz.bukkitUtils.events.Event;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class PlayerDamagedByPlayer extends Event implements Cancellable {
	private final EntityDamageByEntityEvent parent;
	private final Player player;
	private final Player damager;

	@Override
	public void setCancelled(boolean cancel) {
		parent.setCancelled(cancel);
	}

	@Override
	public boolean isCancelled() {
		return parent.isCancelled();
	}

	public static HandlerList getHandlerList() {
		return getHandlers(PlayerDamagedByPlayer.class);
	}

	static final class PlayerDamagedByPlayerListener implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onDamage(EntityDamageByEntityEvent event) {
			if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player))
				return;

			new PlayerDamagedByPlayer(event, (Player) event.getEntity(), (Player) event.getDamager()).call();
		}
	}
}
