
package jdz.bukkitUtils.utils;

import org.bukkit.Bukkit;

public class ReflectionUtils {
	public static Class<?> getClass(String cp) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + cp);
	}

	public static String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
}
