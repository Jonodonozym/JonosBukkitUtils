
package jdz.bukkitUtils.misc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
	
	public static void give(Player player, ItemStack item) {
		give(player, Arrays.asList(item));
	}

	public static void give(Player player, ItemStack item, int amount) {
		ItemStack newStack = new ItemStack(item);
		newStack.setAmount(amount);
		give(player, newStack);
	}
	
	public static void give(Player player, Collection<ItemStack> items) {
		List<ItemStack> overflow = addAllItems(player.getInventory(), items);
		
		if (!overflow.isEmpty()) {
			for (ItemStack item: overflow)
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.sendMessage(ChatColor.GRAY+"Inventory full, dropping item"+(overflow.size()>1 || overflow.get(0).getAmount() > 1?"s":"")+" on the ground");
		}
	}
	
	public static List<ItemStack> addAllItems(Inventory inv, Collection<ItemStack> items){
		List<ItemStack> overflow = new ArrayList<ItemStack>();
		
		for (ItemStack item: items) {
			HashMap<Integer, ItemStack> itemOverflow = inv.addItem(item);
			overflow.addAll(itemOverflow.values());
		}
		
		return overflow;
	}
}
