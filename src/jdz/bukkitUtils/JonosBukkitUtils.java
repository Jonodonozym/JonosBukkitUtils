/**
 * BukkitJUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.commands.JBU.ReloadConfigCommand;
import jdz.bukkitUtils.commands.JBU.SaveConfigCommand;
import jdz.bukkitUtils.events.custom.JBUEvents;
import jdz.bukkitUtils.fileIO.JarUtils;
import jdz.bukkitUtils.interactableObject.InteractableObjectListener;
import jdz.bukkitUtils.misc.ServerTimer;
import jdz.bukkitUtils.sql.minecraft.SQLDataClassBukkitParser;
import jdz.bukkitUtils.updaters.PluginUpdater;
import lombok.Getter;

/**
 * Jono's Utilities for Bukkit to make your coding life easier
 * and less copy-pasty copy-pasty from old projects and the internet
 *
 * @author Jonodonozym
 */
public final class JonosBukkitUtils extends JavaPlugin {
	@Getter private static Plugin instance;

	@Override
	public void onLoad() {
		instance = this;

		new JarUtils(this).extractLibs("libs/lombok.jar", "libs/exp4j.jar", "libs/mockito.jar");
	}

	@Override
	public void onEnable() {

		ArgumentParsers.initDefaults();
		SQLDataClassBukkitParser.initDefaults();
		new ReloadConfigCommand().register(this);
		new SaveConfigCommand().register(this);
		new InteractableObjectListener().registerEvents(this);

		PluginUpdater.updateAll();

		JBUEvents.registerAll(this);
		ServerTimer.start();
	}
}
