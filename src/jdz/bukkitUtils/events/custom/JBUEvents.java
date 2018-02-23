
package jdz.bukkitUtils.events.custom;

import org.bukkit.Bukkit;

import jdz.bukkitUtils.JonosBukkitUtils;

public class JBUEvents {

	public static void registerAll(JonosBukkitUtils plugin) {
		// registering custom events
		Bukkit.getPluginManager().registerEvents(new AnvilRenameEvent.AnvilRenameListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new AnvilRepairEvent.AnvilRepairListener(), plugin);
		Bukkit.getPluginManager().registerEvents(new AnvilEvent.CustomEnchantAnvilCrashPreventer(), plugin);
		Bukkit.getPluginManager().registerEvents(new PotionDrinkEvent.PotionDrinkEventListener(), plugin);
	}
}
