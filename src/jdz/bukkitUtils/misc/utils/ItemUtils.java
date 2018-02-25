
package jdz.bukkitUtils.misc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.bukkitUtils.misc.RomanNumber;

public class ItemUtils {
    public static boolean itemHasName(ItemStack stack, String name) {
        if (stack == null)
            return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return false;
        if (!meta.hasDisplayName())
            return false;
        return meta.getDisplayName().equalsIgnoreCase(name);
    }

    public static ItemStack setName(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

	public static void give(Player player, ItemStack item, int amount) {
		ItemStack newStack = new ItemStack(item);
		newStack.setAmount(amount);
		give(player, Arrays.asList(newStack));
	}
	
	public static void give(Player player, ItemStack item) {
		give(player, item, Math.min(1, item.getAmount()));
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
		HashMap<Integer, ItemStack> itemOverflow = inv.addItem(items.toArray(new ItemStack[1]));
		
		return new ArrayList<ItemStack>(itemOverflow.values());
	}
	
	public static Map<Enchantment, Integer> getCustomEnchants(ItemStack stack){
		Map<Enchantment, Integer> customEnchants = new HashMap<Enchantment, Integer>();

		if (stack.getEnchantments() == null)
			return customEnchants;
		
		for (Entry<Enchantment, Integer> entry: stack.getEnchantments().entrySet())
			if (jdz.bukkitUtils.misc.Enchantment.isCustom(entry.getKey()))
				customEnchants.put(entry.getKey(), entry.getValue());
		
		return customEnchants;
	}
	
	public static List<String> getCustomEnchantsLore(ItemStack stack){
		List<String> lore = new ArrayList<String>();
		for (Entry<Enchantment, Integer> entry: getCustomEnchants(stack).entrySet())
			lore.add(ChatColor.GRAY+entry.getKey().getName()+(entry.getValue()<=1?"":" "+RomanNumber.of(entry.getValue())));
		return lore;
	}
}
