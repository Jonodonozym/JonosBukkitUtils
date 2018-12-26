
package jdz.bukkitUtils.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import jdz.bukkitUtils.events.Listener;

public abstract class PlayerDataManager<E> implements Listener {
	private final Map<Player, E> data = new HashMap<>();

	public E get(Player player) {
		return data.get(player);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!storageContains(event.getPlayer()))
			data.put(event.getPlayer(), loadDefault(event.getPlayer()));
		else
			data.put(event.getPlayer(), loadFromStorage(event.getPlayer()));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		saveToStorage(event.getPlayer(), get(event.getPlayer()));
	}

	protected abstract void saveToStorage(Player player, E data);

	protected abstract E loadDefault(Player player);

	protected abstract boolean storageContains(Player player);

	protected abstract E loadFromStorage(Player player);


}
