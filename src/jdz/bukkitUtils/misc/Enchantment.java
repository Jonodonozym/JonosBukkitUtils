
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;

public abstract class Enchantment extends org.bukkit.enchantments.Enchantment{
	
	private static final Set<Enchantment> enchantments = new HashSet<Enchantment>();
	
	public Enchantment(JavaPlugin plugin, int id) {
		super(id);
		
		if (!enchantments.contains(this)){
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
			Enchantment.registerEnchantment(this);
		}
		catch (IllegalArgumentException e) {
			if (Enchantment.getById(getId()).equals(this))
				return;
			plugin.getLogger().severe("Enchantment "+getName()+"'s ID Conflicts with "+Enchantment.getById(getId()).getName());
		}
		catch (Exception e) {
			new FileLogger(plugin).createErrorLog(e);
		}
	}
	
	public abstract boolean keepOnRepair();
	public abstract boolean isUpgradeable();
	
	@Override
	public boolean equals(Object other) {
		return (other).getClass().equals(getClass());
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	/**
	 * Adds the enchantment to the item
	 * Also adds the enchantment to the lore, like a normal enchantment
	 * @param item
	 * @param level
	 */
	public void addTo(ItemStack item, int level) {
		item.addUnsafeEnchantment(this, level);
		
		ItemMeta im = item.getItemMeta();
		List<String> lore = im.getLore();
		lore = lore==null?new ArrayList<String>():lore;
		lore.add(0, ChatColor.GRAY+getName()+(getMaxLevel()<=1?"":" "+RomanNumber.of(level)));
		im.setLore(lore);
		
		item.setItemMeta(im);
	}

	private static final Set<org.bukkit.enchantments.Enchantment> defaultEnchantments = new HashSet<org.bukkit.enchantments.Enchantment>(Arrays.asList(Enchantment.values()));
			
	public static Set<org.bukkit.enchantments.Enchantment> getDefaults(){
		return defaultEnchantments;
	}
	
	public static Set<Enchantment> getCustomEnchantments() {
		return Collections.unmodifiableSet(enchantments);
	}
}
