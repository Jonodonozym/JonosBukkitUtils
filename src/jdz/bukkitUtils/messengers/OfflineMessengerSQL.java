/**
 * SqlMessageQueue.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.messengers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.sql.SqlDatabase;
import jdz.bukkitUtils.sql.ORM.SQLDataClass;

/**
 * Allows you to queue messages for players who may or may not be online
 * Requires an sql database, hence requires an SqlApi instance
 *
 * if they're online, sends them the message. Otherwise, sends them the message
 * the next time they log-on
 *
 * @author Jonodonozym
 */
public final class OfflineMessengerSQL extends SqlDatabase implements AbstractMessenger {
	private static final Map<String, OfflineMessengerSQL> messengers = new HashMap<>();

	public static OfflineMessengerSQL get(Plugin plugin, String serverGrounp) {
		if (!messengers.containsKey(serverGrounp))
			messengers.put(serverGrounp, new OfflineMessengerSQL(serverGrounp));
		return messengers.get(serverGrounp);
	}

	private final String serverName;

	private PreparedStatement clearQueueStatement;
	private PreparedStatement highestPriority;

	public OfflineMessengerSQL(String serverName) {
		super(JonosBukkitUtils.getInstance());
		this.serverName = serverName;

		Bukkit.getServer().getPluginManager().registerEvents(this, JonosBukkitUtils.getInstance());

		runOnConnect(() -> {
			try {
				clearQueueStatement = dbConnection.prepareStatement("DELETE FROM "
						+ SQLDataClass.getTableName(Message.class) + " WHERE player = ? AND serverName = ?;");
				clearQueueStatement.setString(1, serverName);
				highestPriority = dbConnection.prepareStatement(
						"SELECT MAX(priority) FROM " + SQLDataClass.getTableName(Message.class) + " WHERE player = ?;");
			}
			catch (SQLException e) {
				onError(e, "");
			}
		});
	}

	@Override
	public void message(OfflinePlayer offlinePlayer, String message, int priority) {
		new Message(serverName, offlinePlayer.getUniqueId(), message, priority).insert(this);
	}

	@Override
	public void setQueuedMessages(OfflinePlayer offlinePlayer, List<String> messages) {
		executeTransaction(() -> {
			clearQueuedMessages(offlinePlayer);
			for (int i = 0; i < messages.size(); i++)
				if (!new Message(serverName, offlinePlayer.getUniqueId(), messages.get(i), i).insert(this))
					return false;
			return true;
		});
	}

	@Override
	public void clearQueuedMessages(OfflinePlayer offlinePlayer) {
		try {
			clearQueueStatement.setString(0, offlinePlayer.getName());
			updateAsync(clearQueueStatement);
		}
		catch (SQLException e) {
			onError(e, clearQueueStatement.toString());
		}
	}

	@Override
	public List<Message> getQueuedMessages(OfflinePlayer offlinePlayer) {
		if (!isConnected())
			return new ArrayList<>();

		return SQLDataClass.select(this, Message.class, "WHERE player = " + offlinePlayer.getName()
				+ " AND serverName = " + serverName + " ORDER BY priority asc;");
	}

	@Override
	public int getHighestPriority(OfflinePlayer player) {
		if (!isConnected())
			return 1000;

		try {
			highestPriority.setString(0, player.getName());
			return Integer.parseInt(query(highestPriority).get(0).get(0));
		}
		catch (Exception e) {
			return 1000;
		}
	}
}
