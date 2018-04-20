
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

/**
 * 
 * https://bukkit.org/threads/inventory-anvil-events.142990/
 * 
 * @authors Zelnehlun, Jonodonozym
 */
public class AnvilRenameEvent extends AnvilEvent {
	@Getter private final String oldName, newName;

	private AnvilRenameEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem, int cost) {
		super(player, leftItem, rightItem, resultItem, cost);
		this.oldName = leftItem.getItemMeta().getDisplayName();
		this.newName = resultItem.getItemMeta().getDisplayName();
	}

	public static HandlerList getHandlerList() {
		return getHandlers(AnvilRenameEvent.class);
	}

	static final class AnvilRenameListener extends AnvilEventListener {

		@Override
		protected AnvilEvent onEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem,
				int cost) {
			if (resultItem.hasItemMeta() && resultItem.getItemMeta().hasDisplayName() && leftItem.hasItemMeta()
					&& leftItem.getItemMeta().hasDisplayName()
					&& !resultItem.getItemMeta().getDisplayName().equals(leftItem.getItemMeta().getDisplayName()))
				return new AnvilRenameEvent(player, leftItem, rightItem, resultItem, cost);
			return null;
		}

	}
}
