
package jdz.bukkitUtils.guiMenu.guis;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStackLinkedMenu;

class GuiMenuListPage extends GuiMenu {
	private final Inventory inventory;
	private final int pageNumber;

	public GuiMenuListPage(JavaPlugin plugin, String name, int pageNumber) {
		super(plugin);
		this.pageNumber = pageNumber;
		inventory = Bukkit.createInventory(null, 54, name + " | Page " + pageNumber);
	}

	@Override
	public void open(Player player) {
		player.openInventory(inventory);
	}

	void setup(List<ClickableStack> items, GuiMenu previousPage, GuiMenu nextPage, GuiMenu superMenu) {
		int startIndex = pageNumber * 45;
		int endIndex = items.size() < 54 ? items.size() : Math.min((pageNumber + 1) * 45, items.size());

		for (int index = startIndex; index < endIndex; index++)
			setItem(items.get(index), index - startIndex, inventory);

		if (previousPage != null)
			setItem(new ClickableStackLinkedMenu(Material.ARROW, ChatColor.GREEN + "Previous Page", previousPage), 53,
					inventory);
		if (nextPage != null)
			setItem(new ClickableStackLinkedMenu(Material.ARROW, ChatColor.GREEN + "Next Page", nextPage), 45,
					inventory);

		if (superMenu != null)
			setItem(new ClickableStackLinkedMenu(Material.ENDER_PEARL, ChatColor.GREEN + "Back", superMenu), 49,
					inventory);
	}
}
