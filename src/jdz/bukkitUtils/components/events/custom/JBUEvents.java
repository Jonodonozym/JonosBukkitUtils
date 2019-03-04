
package jdz.bukkitUtils.components.events.custom;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import jdz.bukkitUtils.JonosBukkitUtils;

public class JBUEvents {

	public static void registerAll(JonosBukkitUtils plugin) {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new AnvilRenameEvent.AnvilRenameListener(), plugin);
		pm.registerEvents(new AnvilRepairEvent.AnvilRepairListener(), plugin);
		pm.registerEvents(new AnvilEvent.CustomEnchantAnvilCrashPreventer(), plugin);
		pm.registerEvents(new PotionDrinkEvent.PotionDrinkEventListener(), plugin);
		pm.registerEvents(new PlayerDamagedByPlayer.PlayerDamagedByPlayerListener(), plugin);
		pm.registerEvents(new CropTrampleEvent.CropTrampleListener(), plugin);
		pm.registerEvents(new ConfigReloadEvent.ConfigReloadOnLaunch(), plugin);
	}
}
