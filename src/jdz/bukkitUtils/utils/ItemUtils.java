
package jdz.bukkitUtils.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.bukkitUtils.components.RomanNumber;

public class ItemUtils {
	public static boolean hasName(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
	}

	public static boolean hasName(ItemStack item, String name) {
		if (hasName(item))
			return item.getItemMeta().getDisplayName().equalsIgnoreCase(name);
		return false;
	}

	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static String getName(ItemStack item) {
		if (!hasName(item))
			return StringUtils.capitalizeWords(item.getType().toString());
		return item.getItemMeta().getDisplayName();
	}

	public static boolean isDamageable(ItemStack item) {
		return true;
	}

	public static ItemStack setDamage(ItemStack item, int damage) {
		item.setDurability((short) damage);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack setData(ItemStack item, int data) {
		item.getData().setData((byte) data);
		return item;
	}

	public static int getDamage(ItemStack item) {
		return item.getDurability();
	}

	public static boolean hasLore(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasLore()
				&& !item.getItemMeta().getLore().isEmpty();
	}

	public static boolean hasLore(ItemStack item, List<String> lore) {
		if (!hasLore(item))
			return lore.isEmpty() || lore == null;
		List<String> itemLore = item.getItemMeta().getLore();
		for (int i = 0; i < lore.size(); i++)
			if (!lore.get(i).equals(itemLore.get(i)))
				return false;
		return true;
	}

	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static List<String> getLore(ItemStack item) {
		if (!hasLore(item))
			return new ArrayList<>();
		return item.getItemMeta().getLore();
	}

	public static void give(Player player, ItemStack item) {
		give(player, Arrays.asList(item));
	}

	public static void give(Player player, ItemStack item, int amount) {
		List<ItemStack> stacks = new ArrayList<>();

		ItemStack newStack = item.clone();
		newStack.setAmount(item.getType().getMaxStackSize());
		int fullStacks = amount / item.getType().getMaxStackSize();
		int remainder = amount % item.getType().getMaxStackSize();

		for (int i = 0; i < fullStacks; i++)
			stacks.add(newStack);

		newStack.setAmount(remainder);
		stacks.add(newStack);
		give(player, stacks);
	}

	public static void give(Player player, Collection<ItemStack> items) {
		List<ItemStack> overflow = addAllItems(player.getInventory(), items);

		if (!overflow.isEmpty()) {
			for (ItemStack item : overflow)
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.sendMessage(ChatColor.GRAY + "Inventory full, dropping item"
					+ (overflow.size() > 1 || overflow.get(0).getAmount() > 1 ? "s" : "") + " on the ground");
		}
	}

	public static List<ItemStack> addAllItems(Inventory inv, Collection<ItemStack> items) {
		HashMap<Integer, ItemStack> itemOverflow = inv.addItem(items.toArray(new ItemStack[1]));
		return new ArrayList<>(itemOverflow.values());
	}

	public static boolean has(Inventory inv, Material m, int amount) {
		return count(inv, m) >= amount;
	}

	public static boolean has(Inventory inv, ItemStack item, int amount) {
		return count(inv, item) >= amount;
	}

	public static int count(Inventory inv, Material m) {
		return count(inv, new ItemStack(m));
	}

	public static int count(Inventory inv, ItemStack item) {
		int count = 0;
		for (ItemStack inventoryItem : inv.getContents())
			if (equals(inventoryItem, item))
				count += inventoryItem.getAmount();
		return count;
	}

	public static void remove(Inventory inv, Material m, int quantity) {
		remove(inv, new ItemStack(m), quantity);
	}

	public static void remove(Inventory inv, ItemStack item, int quantity) {
		ItemStack[] contents = inv.getContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack inventoryItem = contents[i];
			if (!equals(inventoryItem, item))
				continue;

			if (quantity >= inventoryItem.getAmount()) {
				quantity -= inventoryItem.getAmount();
				inv.setItem(i, new ItemStack(Material.AIR));
				if (quantity == 0)
					return;
			}
			else {
				ItemStack newItem = new ItemStack(inventoryItem);
				newItem.setAmount(inventoryItem.getAmount() - quantity);
				inv.setItem(i, newItem);
				return;
			}
		}
	}

	public static boolean hasEnchants(ItemStack item) {
		return item != null && !item.getEnchantments().isEmpty();
	}

	public static Map<Enchantment, Integer> getCustomEnchants(ItemStack item) {
		Map<Enchantment, Integer> customEnchants = new HashMap<>();

		if (item.getEnchantments() == null)
			return customEnchants;

		for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
			if (jdz.bukkitUtils.components.Enchantment.isCustom(entry.getKey()))
				customEnchants.put(entry.getKey(), entry.getValue());

		return customEnchants;
	}

	public static List<String> getCustomEnchantsLore(ItemStack item) {
		List<String> lore = new ArrayList<>();
		for (Entry<Enchantment, Integer> entry : getCustomEnchants(item).entrySet())
			lore.add(ChatColor.GRAY + StringUtils.capitalizeWord(entry.getKey().getName())
					+ (entry.getValue() <= 1 ? "" : " " + RomanNumber.of(entry.getValue())));
		return lore;
	}

	public static boolean equals(ItemStack a, ItemStack b) {
		if (a == null || b == null)
			return false;

		if (a.getType() != b.getType())
			return false;

		if (!a.hasItemMeta() && !b.hasItemMeta())
			return true;

		if (!a.hasItemMeta() || b.hasItemMeta())
			return false;

		ItemMeta metaA = a.getItemMeta(), metaB = b.getItemMeta();

		return metaA.getDisplayName().equals(metaB.getDisplayName()) && metaA.getEnchants().equals(metaB.getEnchants())
				&& metaA.getLore().equals(metaB.getLore());
	}
}
