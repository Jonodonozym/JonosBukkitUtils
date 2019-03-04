
package jdz.bukkitUtils.components.guiMenu.guis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStack;

public abstract class GuiMenu implements Listener {
	protected final Map<Inventory, Map<Integer, ClickableStack>> pages = new HashMap<>();

	protected GuiMenu(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler()
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		Inventory inv = p.getOpenInventory().getTopInventory();

		ItemStack stack = null;
		if (e.getCurrentItem() != null)
			stack = e.getCurrentItem();
		else if (e.getCursor() != null)
			stack = e.getCursor();

		if (stack == null)
			return;
		if (inv == null)
			return;

		if (!pages.containsKey(inv))
			return;

		e.setCancelled(true);

		if (e.getRawSlot() != e.getSlot())
			return;

		if (!pages.get(inv).containsKey(e.getSlot()))
			return;

		ClickableStack clickable = pages.get(inv).get(e.getSlot());
		if (clickable == null)
			return;

		if (clickable.isCloseOnClick())
			p.closeInventory();

		Bukkit.getScheduler().runTaskLater(JonosBukkitUtils.getInstance(), () -> {
			clickable.onClick((Player) e.getWhoClicked(), this, e);
		}, 1);
	}

	@EventHandler
	public void onMove(InventoryMoveItemEvent event) {
		if (pages.containsKey(event.getDestination()) || pages.containsKey(event.getSource()))
			event.setCancelled(true);
	}

	protected void update(Inventory inv) {
		if (!pages.containsKey(inv))
			return;

		for (Integer i : pages.get(inv).keySet())
			inv.setItem(i, pages.get(inv).get(i).getStack());
	}

	protected void clear(Inventory inv) {
		if (pages.containsKey(inv))
			pages.get(inv).clear();
		inv.clear();
	}

	protected boolean setItem(ClickableStack item, Inventory inv, int row, int column) {
		return setItem(item, row * 9 + column, inv);
	}

	protected boolean setItem(ClickableStack item, int slot, Inventory inv) {
		if (slot < 0 || slot >= inv.getSize())
			return false;

		if (!pages.containsKey(inv))
			pages.put(inv, new HashMap<Integer, ClickableStack>());

		pages.get(inv).put(slot, item);
		inv.setItem(slot, item.getStack());
		return true;
	}

	public void updateAll() {
		for (Inventory inv : pages.keySet())
			updateItems(inv);
	}

	public void updateOpen() {
		for (Inventory inv : pages.keySet())
			if (!inv.getViewers().isEmpty())
				updateItems(inv);
	}

	public void updateItems(Inventory inv) {
		if (!pages.containsKey(inv))
			return;

		for (Entry<Integer, ClickableStack> entry : pages.get(inv).entrySet()) {
			entry.getValue().update();
			inv.setItem(entry.getKey(), entry.getValue().getStack());
		}
	}

	public abstract void open(Player player);
}
