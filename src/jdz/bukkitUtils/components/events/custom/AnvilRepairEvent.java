
package jdz.bukkitUtils.components.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.utils.ItemUtils;

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
			if (ItemUtils.getDamage(resultItem) > ItemUtils.getDamage(leftItem))
				return new AnvilRepairEvent(player, leftItem, rightItem, resultItem, cost);
			return null;
		}

	}

}
