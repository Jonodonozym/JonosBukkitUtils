/**
 * BukkitJUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.events.custom.JBUEvents;
import jdz.bukkitUtils.fileIO.JarUtils;
import jdz.bukkitUtils.misc.Config;
import jdz.bukkitUtils.misc.Enchantment;
import jdz.bukkitUtils.misc.PluginUpdater;

/**
 * Jono's (or Java, I don't give a damn what you think) Utilities for Bukkit to make your coding life
 * easier and less copy-pasty copy-pasty from old projects and the internet
 *
 * @author Jonodonozym
 */
public final class JonosBukkitUtils extends JavaPlugin{
	public static JonosBukkitUtils instance;
	private final int bukkitID = 281287;

	public final Set<org.bukkit.enchantments.Enchantment> defaultEnchantments = new HashSet<org.bukkit.enchantments.Enchantment>(Arrays.asList(Enchantment.values()));
	
	@Override
	public void onEnable() {
		instance = this;
		
		new JarUtils(this).extractLibs("libs/lombok.jar");
		
		FileConfiguration config = Config.getConfig(this);
		if (config.getBoolean("autoUpdate"))
			new PluginUpdater(this, bukkitID, this.getFile(), PluginUpdater.UpdateType.DEFAULT, false);
		
		JBUEvents.registerAll(this);
	}
}
