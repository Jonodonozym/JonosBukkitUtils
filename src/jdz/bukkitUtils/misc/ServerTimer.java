
package jdz.bukkitUtils.misc;

import org.bukkit.Bukkit;

import jdz.bukkitUtils.JonosBukkitUtils;
import lombok.Getter;

public class ServerTimer {
	@Getter private static int serverTicks = 0;
	private static boolean started = false;

	public static void start() {
		if (!started) {
			started = true;

			Bukkit.getScheduler().runTaskTimer(JonosBukkitUtils.getInstance(), () -> {
				serverTicks++;
			}, 1, 1);
		}
	}

}
