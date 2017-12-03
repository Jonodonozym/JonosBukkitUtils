
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.fileIO.FileLogger;

public abstract class Enchantment extends org.bukkit.enchantments.Enchantment{
	
	private static final Set<Enchantment> enchantments = new HashSet<Enchantment>();
	
	public Enchantment(JavaPlugin plugin, int id) {
		super(id);
		
		if (enchantments.contains(this))
			throw new IllegalArgumentException("An enchantment with an id of "+id+" has already been registered!");
		
		enchantments.add(this);
		register(plugin);
	}
	
	private void register(JavaPlugin plugin) {
		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Enchantment.registerEnchantment(this);
			} catch (IllegalArgumentException e) {
				new FileLogger(plugin).createErrorLog(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
