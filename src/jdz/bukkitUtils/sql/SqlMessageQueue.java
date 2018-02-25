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
import org.bukkit.plugin.Plugin;

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
public final class SqlMessageQueue implements Listener {
	private String MessageQueueTable = null;
	private final SqlApi sqlApi;

	public SqlMessageQueue(Plugin plugin, SqlApi sqlApi) {
		this.sqlApi = sqlApi;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		if (MessageQueueTable == null)
			setTable(plugin.getName() + "_MessageQueue");
	}

	public void setTable(String table) {
		MessageQueueTable = table;
		sqlApi.runOnConnect(() -> {
			ensureCorrectTables();
		});
	}

	private void ensureCorrectTables() {
		if (!checkPreconditions())
			return;

		String update = "CREATE TABLE IF NOT EXISTS " + MessageQueueTable
				+ " (player varchar(63), message varchar(1023), priority int);";
		sqlApi.executeUpdate(update);
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

		if (!checkPreconditions())
			return;

		String update = "INSERT INTO " + MessageQueueTable + " (player, message, priority) VALUES('"
				+ offlinePlayer.getName() + "','" + message + "'," + priority + ");";
		System.out.println(update + " :: " + message);
		sqlApi.executeUpdateAsync(update);
	}

	public void setQueuedMessages(OfflinePlayer offlinePlayer, List<String> messages) {
		if (!checkPreconditions())
			return;
		String update = "INSERT INTO " + MessageQueueTable + " (player, message, priority) VALUES('"
				+ offlinePlayer.getName() + "','{m}',{p});";
		clearQueuedMessages(offlinePlayer);
		int i = 0;
		for (String s : messages) {
			if (s == "")
				continue;
			sqlApi.executeUpdateAsync(update.replace("{m}", s).replace("{p}", "" + i++));
		}
	}

	public void clearQueuedMessages(OfflinePlayer offlinePlayer) {
		if (!checkPreconditions())
			return;

		String update = "DELETE FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName() + "';";
		sqlApi.executeUpdateAsync(update);
	}

	private List<String> getQueuedMessages(OfflinePlayer offlinePlayer) {
		if (!checkPreconditions())
			return new ArrayList<String>();

		String query = "SELECT message FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName() + "' "
				+ "ORDER BY priority asc;";
		List<String[]> list = sqlApi.getRows(query);
		List<String> returnList = new ArrayList<String>();
		for (String[] str : list)
			returnList.add(str[0]);
		return returnList;
	}

	private int getHighestPirority(OfflinePlayer offlinePlayer) {
		if (!checkPreconditions())
			return 1000;
		String query = "SELECT MAX(priority) FROM " + MessageQueueTable + " WHERE player = '" + offlinePlayer.getName()
				+ "';";
		try {
			return Integer.parseInt(sqlApi.getRows(query).get(0)[0]);
		}
		catch (Exception e) {
			return 1000;
		}
	}

	private boolean checkPreconditions() {
		if (MessageQueueTable == null)
			throw new RuntimeException(
					"Must call MessageQueue.init() in this plugin's code somewhere, preferably before calling any MessageQueue methods");

		if (!sqlApi.isConnected())
			return false;
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.instance, () -> {
			List<String> messages = getQueuedMessages(event.getPlayer());
			if (!messages.isEmpty()) {
				for (String s : messages)
					event.getPlayer().sendMessage(s);
				clearQueuedMessages(event.getPlayer());
			}
		});
	}
}
