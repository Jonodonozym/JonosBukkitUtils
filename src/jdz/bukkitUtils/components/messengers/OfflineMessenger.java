
package jdz.bukkitUtils.components.messengers;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public interface OfflineMessenger {
	public static OfflineMessenger getMessenger(Plugin plugin) {
		return OfflineMessengerYML.get(plugin);
	}

	public static OfflineMessenger getNetworkMessenger(Plugin plugin, String serverGroup) {
		return OfflineMessengerSQL.get(plugin, serverGroup);
	}

	public void message(OfflinePlayer player, String message);

	public void message(OfflinePlayer player, String message, int priority);

	public List<Message> getQueuedMessages(OfflinePlayer player);

	public void setQueuedMessages(OfflinePlayer offlinePlayer, List<String> messages);

	public void clearQueuedMessages(OfflinePlayer offlinePlayer);

	public int getHighestPriority(OfflinePlayer player);
}
