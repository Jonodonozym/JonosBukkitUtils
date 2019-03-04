
package jdz.bukkitUtils.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;

public abstract class Enchantment extends org.bukkit.enchantments.Enchantment {
	static {
		new EnchantTableListener().registerEvents(JonosBukkitUtils.getInstance());
	}

	private static final Set<Enchantment> enchantments = new HashSet<>();

	public Enchantment(Plugin plugin, int id) {
		super(new NamespacedKey(plugin, id + ""));

		if (!enchantments.contains(this)) {
			enchantments.add(this);
			registerEnchantment(this);
		}
	}

	@Override
	public boolean equals(Object other) {
		return other.getClass().equals(getClass());
	}

	/**
	 * Adds the enchantment to the item Also adds the enchantment to the lore, like
	 * a normal enchantment
	 *
	 * @param item
	 * @param level
	 */
	public void addTo(ItemStack item, int level) {
		item.addUnsafeEnchantment(this, level);

		ItemMeta im = item.getItemMeta();
		List<String> lore = im.getLore();
		lore = lore == null ? new ArrayList<>() : lore;
		lore.add(0, ChatColor.GRAY + getKey().getKey() + (getMaxLevel() <= 1 ? "" : " " + RomanNumber.of(level)));
		im.setLore(lore);

		item.setItemMeta(im);
	}

	public int getLevel(ItemStack stack) {
		Map<org.bukkit.enchantments.Enchantment, Integer> enchants = stack.getEnchantments();
		for (org.bukkit.enchantments.Enchantment enchant : enchants.keySet())
			if (enchant.getClass().getName().equals(getClass().getName()))
				return enchants.get(enchant);
		return 0;
	}

	public static Set<Enchantment> getCustomEnchantments() {
		return Collections.unmodifiableSet(enchantments);
	}

	public static boolean isCustom(org.bukkit.enchantments.Enchantment e) {
		return e.getKey().getNamespace() != NamespacedKey.BUKKIT
				&& e.getKey().getNamespace() != NamespacedKey.MINECRAFT;
	}

	private static class EnchantTableListener implements Listener {
		@EventHandler
		public void onEnchant(EnchantItemEvent event) {
			int cost = event.getExpLevelCost();
			Material material = event.getItem().getType();
			for (Enchantment ench : enchantments)
				if (ench instanceof EnchantTableAddition) {
					EnchantTableAddition enchAdd = (EnchantTableAddition) ench;
					if (enchAdd.useOnTable() && enchAdd.getEnchantChance(cost, material) > Math.random()) {
						int level = enchAdd.getEnchantLevel(cost, material);
						ench.addTo(event.getItem(), level);
					}
				}
		}
	}
}
