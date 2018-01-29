
package jdz.bukkitUtils.misc.loot;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;

public abstract class Loot {
	public abstract Collection<ItemStack> getItems();

	public abstract String getName();

}
