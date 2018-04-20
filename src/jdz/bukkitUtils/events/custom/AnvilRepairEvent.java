
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class AnvilRepairEvent extends AnvilEvent {

	private AnvilRepairEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem, int cost) {
		super(player, leftItem, rightItem, resultItem, cost);
	}

	public static HandlerList getHandlerList() {
		return getHandlers(AnvilRepairEvent.class);
	}

	static final class AnvilRepairListener extends AnvilEventListener {

		@Override
		protected AnvilEvent onEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem,
				int cost) {
			if (resultItem.getDurability() > leftItem.getDurability())
				return new AnvilRepairEvent(player, leftItem, rightItem, resultItem, cost);
			return null;
		}

	}

}
