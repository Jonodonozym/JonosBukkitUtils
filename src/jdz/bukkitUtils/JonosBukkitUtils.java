/**
 * BukkitJUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils;

import org.bukkit.plugin.java.JavaPlugin;
import jdz.bukkitUtils.events.custom.JBUEvents;
import jdz.bukkitUtils.fileIO.JarUtils;
import jdz.bukkitUtils.misc.ServerTimer;
import jdz.bukkitUtils.updaters.PluginUpdater;

/**
 * Jono's (or Java, I don't give a damn what you think) Utilities for Bukkit to
 * make your coding life
 * easier and less copy-pasty copy-pasty from old projects and the internet
 *
 * @author Jonodonozym
 */
public final class JonosBukkitUtils extends JavaPlugin {
	private static JonosBukkitUtils instance;

	public static JonosBukkitUtils getInstance() {
		return instance;
	}

	// private final int bukkitID = 281287;

	@Override
	public void onLoad() {
		instance = this;

		new JarUtils(this).extractLibs("libs/lombok.jar");
		new JarUtils(this).extractLibs("libs/exp4j.jar");
		new JarUtils(this).extractLibs("libs/mockito.jar");

		PluginUpdater.updateAll();
	}

	@Override
	public void onEnable() {
		JBUEvents.registerAll(this);
		ServerTimer.start();
	}
}
