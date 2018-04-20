
package jdz.bukkitUtils.misc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;

public class VelocityTracker implements Listener {
	public static Vector getVelocity(Player player) {
		return instance.moveData.get(player).velocity;
	}

	public static void init() {}

	private static final VelocityTracker instance = new VelocityTracker();

	private static class MoveData {
		private final Player player;
		private Location location;
		private Vector velocity;

		private MoveData(Player player) {
			this.player = player;
			location = player.getLocation();
			velocity = new Vector();
		}

		public void update(double tickDiff) {
			if (player.getWorld().equals(location.getWorld())) {
				Vector displacement = player.getLocation().toVector().subtract(location.toVector());
				velocity = displacement.multiply(20.0 / tickDiff);
			}
			else
				velocity = new Vector();
			location = player.getLocation();
		}
	}

	private final Map<Player, MoveData> moveData = new HashMap<Player, MoveData>();

	private VelocityTracker() {
		registerEvents(JonosBukkitUtils.getInstance());
		Bukkit.getScheduler().runTaskTimer(JonosBukkitUtils.getInstance(), () -> {
			for (MoveData data : moveData.values())
				data.update(1);
		}, 0, 1);
		for (Player player : Bukkit.getOnlinePlayers())
			moveData.put(player, new MoveData(player));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		moveData.put(event.getPlayer(), new MoveData(event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		moveData.remove(event.getPlayer());
	}

}
