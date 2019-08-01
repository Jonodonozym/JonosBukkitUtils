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

import jdz.bukkitUtils.commands.CommandArgumentParsers;
import jdz.bukkitUtils.commands.JBU.ReloadConfigCommand;
import jdz.bukkitUtils.commands.JBU.SaveConfigCommand;
import jdz.bukkitUtils.components.ItemLock;
import jdz.bukkitUtils.components.LibraryLoader;
import jdz.bukkitUtils.components.ServerTimer;
import jdz.bukkitUtils.components.events.ListenerManager;
import jdz.bukkitUtils.components.events.custom.JBUEvents;
import jdz.bukkitUtils.components.interactableObject.InteractableObjectListener;
import jdz.bukkitUtils.configuration.ConfigReloadEvent;
import jdz.bukkitUtils.persistence.minecraft.SQLDataClassBukkitParser;
import jdz.bukkitUtils.pluginUpdaters.PluginUpdater;
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
		LibraryLoader.extractAndLoadLibraries(this, "lombok.jar", "mockito.jar");
	}

	@Override
	public void onEnable() {
		ListenerManager.getInstance().registerEvents(this);
		CommandArgumentParsers.initDefaults();
		SQLDataClassBukkitParser.initDefaults();
		new ReloadConfigCommand().register(this);
		new SaveConfigCommand().register(this);
		new InteractableObjectListener().registerEvents(this);
		new ConfigReloadEvent.ConfigReloadOnLaunch().registerEvents(this);
		ItemLock.getInstance().registerEvents(this);

		PluginUpdater.updateAll();

		new ConfigReloadEvent(this).call();
		JBUEvents.registerAll(this);
		ServerTimer.start();
	}
}
