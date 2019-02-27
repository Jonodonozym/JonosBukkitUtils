
package jdz.bukkitUtils.guiMenu.guis;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;

public abstract class GuiMenuPlayer extends GuiMenu {
	private final Map<Player, Inventory> invs = new HashMap<>();

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
			pages.remove(invs.get(player));
		invs.put(player, createInventory(player));
	}

	protected abstract Inventory createInventory(Player player);
}
