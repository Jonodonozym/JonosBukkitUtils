
package jdz.bukkitUtils.messengers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.Config;
import jdz.bukkitUtils.sql.ORM.SQLDataClass;

public class OfflineMessengerYML implements Listener, OfflineMessenger {
	private static final Map<Plugin, OfflineMessengerYML> messengers = new HashMap<>();

	public static OfflineMessengerYML get(Plugin plugin) {
		if (!messengers.containsKey(plugin))
			messengers.put(plugin, new OfflineMessengerYML(plugin));
		return messengers.get(plugin);
	}

	private final Map<UUID, List<Message>> playerToMessages = new HashMap<>();
	private final Plugin plugin;

	public OfflineMessengerYML(Plugin plugin) {
		this.plugin = plugin;
		loadMessages();
		registerEvents(JonosBukkitUtils.getInstance());
	}

	@Override
	public void message(OfflinePlayer player, String message) {
		message(player, message, getHighestPriority(player) + 1);
	}

	@Override
	public void message(OfflinePlayer player, String message, int priority) {
		if (player.isOnline()) {
			player.getPlayer().sendMessage(message);
			return;
		}

		if (!playerToMessages.containsKey(player.getUniqueId()))
			playerToMessages.put(player.getUniqueId(), new ArrayList<Message>());

		int i = 0;
		for (Message m : playerToMessages.get(player.getUniqueId()))
			if (m.getPriority() > priority)
				break;
		playerToMessages.get(player.getUniqueId()).add(i,
				new Message(plugin.getName(), player.getUniqueId(), message, priority));
	}

	@Override
	public List<Message> getQueuedMessages(OfflinePlayer player) {
		if (!playerToMessages.containsKey(player.getUniqueId()))
			playerToMessages.put(player.getUniqueId(), new ArrayList<Message>());
		return playerToMessages.get(player.getUniqueId());
	}

	@Override
	public void setQueuedMessages(OfflinePlayer offlinePlayer, List<String> messages) {
		clearQueuedMessages(offlinePlayer);
		List<Message> newMessages = new ArrayList<>();
		for (int i = 0; i < messages.size(); i++)
			newMessages.add(new Message(plugin.getName(), offlinePlayer.getUniqueId(), messages.get(i), i));
		playerToMessages.put(offlinePlayer.getUniqueId(), newMessages);
	}

	@Override
	public void clearQueuedMessages(OfflinePlayer offlinePlayer) {
		playerToMessages.remove(offlinePlayer.getUniqueId());
	}

	@EventHandler
	public void onPluginStop(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin))
			saveMessages();
	}

	private void saveMessages() {
		try {
			FileConfiguration config = new YamlConfiguration();
			for (UUID player : playerToMessages.keySet())
				if (!playerToMessages.get(player).isEmpty()) {
					List<String> stringList = new ArrayList<>();
					for (Message m : playerToMessages.get(player))
						stringList.add(m.toString());
					config.set(player.toString(), stringList);
				}
			config.save(Config.getConfigFile(plugin, "offlineMessages.yml"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadMessages() {
		FileConfiguration config = Config.getConfig(plugin, "offlineMessages.yml");
		playerToMessages.clear();
		for (String player : config.getKeys(false)) {
			List<Message> messageList = new ArrayList<>();
			for (String s : config.getStringList(player))
				messageList.add(SQLDataClass.fromString(Message.class, s));
			playerToMessages.put(UUID.fromString(player), messageList);
		}
	}

	@Override
	public int getHighestPriority(OfflinePlayer player) {
		List<Message> messages = playerToMessages.get(player.getUniqueId());
		if (messages == null || messages.isEmpty())
			return 10000;
		return messages.get(messages.size() - 1).getPriority();
	}
}
