
package jdz.bukkitUtils.misc.loot;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.inventory.ItemStack;

public abstract class Loot {
	public abstract Collection<ItemStack> getItems();
	public abstract String getName();

	public static Loot make(String name, ItemStack... items) {
		return make(name, Arrays.asList(items));
	}

	public static Loot make(String name, Collection<ItemStack> items) {
		return new Loot() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public Collection<ItemStack> getItems() {
				return items;
			}
		};
	}
}
