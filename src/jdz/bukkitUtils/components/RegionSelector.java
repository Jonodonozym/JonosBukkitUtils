
package jdz.bukkitUtils.components;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.utils.ItemUtils;
import jdz.bukkitUtils.utils.WorldUtils;
import lombok.Data;

public abstract class RegionSelector implements Listener {
	@Data
	public static class Region {
		private final Location l1, l2;
	}

	private final Map<Player, Location> locationA = new HashMap<>();
	private final Map<Player, Location> locationB = new HashMap<>();
	private final ItemStack regionWand = getRegionWand();

	protected abstract ItemStack getRegionWand();

	public Region getSelectedRegion(Player player) {
		if (!locationA.containsKey(player) || !locationB.containsKey(player))
			return null;
		return new Region(locationA.get(player), locationB.get(player));
	}

	public void giveWand(Player player) {
		if (!hasWandInInventory(player))
			player.getInventory().addItem(regionWand);
	}

	public boolean hasWandInInventory(Player player) {
		for (ItemStack item : player.getInventory())
			if (ItemUtils.equals(regionWand, item))
				return true;
		return false;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!ItemUtils.equals(regionWand, event.getItem()))
			return;

		Block target = event.getPlayer().getTargetBlock(null, 100);
		if (target == null)
			return;

		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			locationA.put(event.getPlayer(), target.getLocation());
			sendFormattedMessage(event.getPlayer(), target.getLocation(), 1);
		}
		else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			locationB.put(event.getPlayer(), target.getLocation());
			sendFormattedMessage(event.getPlayer(), target.getLocation(), 2);
		}
	}

	protected void sendFormattedMessage(Player player, Location location, int corner) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Corner " + corner + " set to "
				+ WorldUtils.locationToLegibleString(location));
	}

	@Override
	public boolean registerEvents(Plugin plugin) {
		ItemLock.lockItem(regionWand);
		return Listener.super.registerEvents(plugin);
	}

	@Override
	public void unregisterEvents(Plugin plugin) {
		ItemLock.unlockItem(regionWand);
		Listener.super.unregisterEvents(plugin);
	}

}
