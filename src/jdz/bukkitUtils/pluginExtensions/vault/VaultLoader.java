/**
 * Vault.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.pluginExtensions.vault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import jdz.bukkitUtils.utils.ColorUtils;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Automatically loads Vault shiz so you don't have to worry about that
 * RegisteredServiceProviderFactoryClassLoaderModernImplementation stuff.
 *
 * @author Jonodonozym
 */
public final class VaultLoader {
	@Getter private static Economy economy = fetch(Economy.class);
	@Getter private static Chat chat = fetch(Chat.class);
	@Getter private static Permission permission = fetch(Permission.class);

	private static <T> T fetch(Class<T> clazz) {
		RegisteredServiceProvider<T> provider = Bukkit.getServicesManager().getRegistration(clazz);
		if (provider == null)
			return null;
		return provider.getProvider();
	}

	public static String getGroup(Player player) {
		if (chat == null || permission == null)
			return "";

		String prefix = chat.getPlayerPrefix(player);
		String group = permission.getPrimaryGroup(player);

		if (prefix == null || prefix == "")
			prefix = chat.getGroupPrefix(player.getWorld(), group);

		return ColorUtils.translate(prefix);
	}
}
