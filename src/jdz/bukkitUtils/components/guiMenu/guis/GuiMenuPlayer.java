
package jdz.bukkitUtils.components.guiMenu.guis;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import lombok.AccessLevel;
import lombok.Setter;

public abstract class GuiMenuPlayer extends GuiMenu {
	private final Map<Player, Inventory> invs = new HashMap<>();
	@Setter(value = AccessLevel.PROTECTED) private boolean clearOnQuit = true;

	protected GuiMenuPlayer(Plugin plugin) {
		super(plugin);
	}

	@Override
	public void open(Player player) {
		if (!invs.containsKey(player))
			reset(player);

		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			update(invs.get(player));
			player.openInventory(invs.get(player));
		});
	}

	public void reset(Player player) {
		if (invs.containsKey(player))
			pages.get(invs.get(player)).clear();
		invs.put(player, createInventory(player));
	}

	protected abstract Inventory createInventory(Player player);
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Inventory inventory = invs.remove(event.getPlayer());
		delete(inventory);
	}
}
