
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.fileIO.FileLogger;

public abstract class Enchantment extends org.bukkit.enchantments.Enchantment {
	static {
		new EnchantTableListener().registerEvents(JonosBukkitUtils.getInstance());
	}

	private static final Set<Enchantment> enchantments = new HashSet<Enchantment>();

	public Enchantment(JavaPlugin plugin, int id) {
		super(1000 + id % 7153);

		if (!enchantments.contains(this)) {
			enchantments.add(this);
			register(plugin);
		}
	}

	@SuppressWarnings("deprecation")
	private void register(JavaPlugin plugin) {
		try {
			Field f = org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			org.bukkit.enchantments.Enchantment.registerEnchantment(this);
		}
		catch (IllegalArgumentException e) {
			if (org.bukkit.enchantments.Enchantment.getById(getId()).equals(this))
				return;
			plugin.getLogger().severe("Enchantment " + getName() + "'s ID Conflicts with "
					+ org.bukkit.enchantments.Enchantment.getById(getId()).getName());
		}
		catch (Exception e) {
			new FileLogger(plugin).createErrorLog(e);
		}
	}

	@Override
	public boolean equals(Object other) {
		return (other).getClass().equals(getClass());
	}

	@Override
	public int hashCode() {
		return getId();
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
		lore = lore == null ? new ArrayList<String>() : lore;
		lore.add(0, ChatColor.GRAY + getName() + (getMaxLevel() <= 1 ? "" : " " + RomanNumber.of(level)));
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

	@SuppressWarnings("deprecation")
	public static boolean isCustom(org.bukkit.enchantments.Enchantment e) {
		return (59999 < e.getId() && e.getId() < 67153) || (999 < e.getId() && e.getId() < 8153);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getId() {
		int id = super.getId();
		if (id > 59999)
			return id - 59000;
		return id;
	}

	private static class EnchantTableListener implements Listener {
		@EventHandler
		public void onEnchant(EnchantItemEvent event) {
			int cost = event.getExpLevelCost();
			Material material = event.getItem().getType();
			for (Enchantment ench : enchantments) {
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
}
