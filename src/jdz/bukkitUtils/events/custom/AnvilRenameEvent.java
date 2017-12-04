
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * https://bukkit.org/threads/inventory-anvil-events.142990/
 * 
 * @authors Zelnehlun, Jonodonozym
 */
public class AnvilRenameEvent extends AnvilEvent {

	private final String oldName;
	private final String newName;

	public AnvilRenameEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem, int cost) {
		super(player, leftItem, rightItem, resultItem, cost);
		this.oldName = leftItem.getItemMeta().getDisplayName();
		this.newName = resultItem.getItemMeta().getDisplayName();
	}

	public String getOldName() {
		return oldName;
	}

	public String getNewName() {
		return newName;
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
