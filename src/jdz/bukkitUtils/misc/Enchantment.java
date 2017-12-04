
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;
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
			plugin.getLogger().severe("Enchantment "+getName()+"'s ID Conflicts with "+Enchantment.getById(getId()).getName());
		}
		catch (Exception e) {
			new FileLogger(plugin).createErrorLog(e);
		}
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean equals(Object other) {
		if (!(other instanceof Enchantment))
			return false;
		return ((Enchantment)other).getId() == getId();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int hashCode() {
		return getId();
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
		lore.add(ChatColor.GRAY+getName()+(getMaxLevel()<=1?"":" "+RomanNumber.of(level)));
		im.setLore(lore);
		
		item.setItemMeta(im);
	}
	
	
	
	static {
		Bukkit.getPluginManager().registerEvents(new AnvilCrashPreventer(), JonosBukkitUtils.instance);
	}
	
	private static class AnvilCrashPreventer implements Listener{
		
		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			if (!(event.getInventory() instanceof AnvilInventory))
				return;
			
			for (org.bukkit.enchantments.Enchantment enchant: event.getCurrentItem().getEnchantments().keySet())
				if (enchantments.contains(enchant)) {
					event.getWhoClicked().sendMessage(ChatColor.RED+"Items with custom enchantments cannot be used on anvils!");
					event.setCancelled(true);
					return;
				}
		}
	}
}
