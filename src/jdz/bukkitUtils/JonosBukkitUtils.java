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

import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.events.custom.JBUEvents;
import jdz.bukkitUtils.fileIO.JarUtils;
import jdz.bukkitUtils.misc.Enchantment;
import jdz.bukkitUtils.updaters.PluginUpdater;
import lombok.Getter;

/**
 * Jono's (or Java, I don't give a damn what you think) Utilities for Bukkit to
 * make your coding life
 * easier and less copy-pasty copy-pasty from old projects and the internet
 *
 * @author Jonodonozym
 */
public final class JonosBukkitUtils extends JavaPlugin {
	@Getter private static JonosBukkitUtils instance;
	private final int bukkitID = 281287;

	public final Set<org.bukkit.enchantments.Enchantment> defaultEnchantments = new HashSet<org.bukkit.enchantments.Enchantment>(
			Arrays.asList(Enchantment.values()));

	@Override
	public void onLoad() {
		PluginUpdater.updateAll();
	}

	@Override
	public void onEnable() {
		instance = this;

		new JarUtils(this).extractLibs("libs/lombok.jar");
		new JarUtils(this).extractLibs("libs/exp4j.jar");
		new JarUtils(this).extractLibs("libs/github-api.jar");

		JBUEvents.registerAll(this);
	}
}
