/**
 * SqlMessageQueue.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.sql;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;

/**
 * Allows you to queue messages for players who may or may not be online
 * Requires an sql database, hence requires an SqlApi instance
 * 
 * if they're online, sends them the message. Otherwise, sends them the message
 * the next time they log-on
 *
 * @author Jonodonozym
 */
public final class SqlMessageQueue extends SqlDatabase implements Listener {
	private final String MessageQueueTable;
	private final SqlColumn[] columns = new SqlColumn[] { new SqlColumn("player", SqlColumnType.STRING_32),
			new SqlColumn("message", SqlColumnType.STRING), new SqlColumn("priotiry", SqlColumnType.INT) };

	public SqlMessageQueue(JavaPlugin plugin, SqlDatabase sqlApi) {
		super(plugin);
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		MessageQueueTable = plugin.getName() + "_MessageQueue";
		addTable(MessageQueueTable, columns);
	}

	public void addQueuedMessage(OfflinePlayer offlinePlayer, String message) {
		addQueuedMessage(offlinePlayer, message, getHighestPirority(offlinePlayer) + 1);
	}

	public void addQueuedMessage(OfflinePlayer offlinePlayer, String message, int priority) {
		if (message == "")
			return;

		if (offlinePlayer.isOnline()) {
			offlinePlayer.getPlayer().sendMessage(message);
			return;
		}

		String update = "INSERT INTO " + MessageQueueTable + " (player, message, priority) VALUES('"
				+ offlinePlayer.getName() + "','" + message + "'," + priority + ");";
		updateAsync(update);
	}

	public void setQueuedMessages(OfflinePlayer offlinePlayer, List<String> messages) {
		String update = "INSERT INTO " + MessageQueueTable + " (player, message, priority) VALUES('"
				+ offlinePlayer.getName() + "','{m}',{p});";
		clearQueuedMessages(offlinePlayer);
		int i = 0;
		for (String s : messages) {
			if (s == "")
				continue;
			updateAsync(update.replace("{m}", s).replace("{p}", "" + i++));
		}
	}

	public void clearQueuedMessages(OfflinePlayer offlinePlayer) {
		if (!isConnected())
			return;

		String update = "DELETE FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName() + "';";
		updateAsync(update);
	}

	private List<String> getQueuedMessages(OfflinePlayer offlinePlayer) {
		if (!isConnected())
			return new ArrayList<String>();

		String query = "SELECT message FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName() + "' "
				+ "ORDER BY priority asc;";
		List<SqlRow> list = query(query);
		List<String> returnList = new ArrayList<String>();
		for (SqlRow row : list)
			returnList.add(row.get(0));
		return returnList;
	}

	private int getHighestPirority(OfflinePlayer offlinePlayer) {
		if (!isConnected())
			return 1000;

		String query = "SELECT MAX(priority) FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName()
				+ "';";
		try {
			return Integer.parseInt(query(query).get(0).get(0));
		}
		catch (Exception e) {
			return 1000;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			List<String> messages = getQueuedMessages(event.getPlayer());
			if (!messages.isEmpty()) {
				for (String s : messages)
					event.getPlayer().sendMessage(s);
				clearQueuedMessages(event.getPlayer());
			}
		});
	}
}
