
package jdz.bukkitUtils.guiMenu.guis;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;

public abstract class GuiMenuPlayer extends GuiMenu {
	private final Map<Player, Inventory> invs = new HashMap<Player, Inventory>();

	protected GuiMenuPlayer(Plugin plugin) {
		super(plugin);
	}
	
	@Override
	public void open(Player player) {
		if (!invs.containsKey(player))
			invs.put(player, Bukkit.createInventory(player, 54, "Kit Shop"));

		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			reloadInv(player, invs.get(player));
			player.openInventory(invs.get(player));
		});
	}

	protected abstract void reloadInv(Player player, Inventory inv);
}
