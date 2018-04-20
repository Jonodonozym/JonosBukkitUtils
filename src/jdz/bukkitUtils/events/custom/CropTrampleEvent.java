
package jdz.bukkitUtils.events.custom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.bukkitUtils.events.Listener;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class CropTrampleEvent extends Event implements Cancellable {
	private final Player player;
	private final Block block;

	public static HandlerList getHandlerList() {
		return getHandlers(CropTrampleEvent.class);
	}

	static final class CropTrampleListener implements Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onTrample(PlayerInteractEvent event) {
			if (event.getAction() != Action.PHYSICAL)
				return;

			Block block = event.getClickedBlock();
			if (block != null && block.getType() == Material.SOIL) {
				CropTrampleEvent newEvent = new CropTrampleEvent(event.getPlayer(), block);
				newEvent.call();
				if (newEvent.isCancelled()) {
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
				}
			}
		}
	}
}
