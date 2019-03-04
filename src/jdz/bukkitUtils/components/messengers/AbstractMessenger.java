
package jdz.bukkitUtils.components.messengers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.components.events.Listener;

public interface AbstractMessenger extends OfflineMessenger, Listener {

	@Override
	public default void message(OfflinePlayer player, String message) {
		message(player, message, getHighestPriority(player) + 1);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public default void onPlayerJoin(PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			List<Message> messages = getQueuedMessages(event.getPlayer());
			if (!messages.isEmpty()) {
				for (Message s : messages)
					event.getPlayer().sendMessage(s.getMessage());
				clearQueuedMessages(event.getPlayer());
			}
		});
	}
}
