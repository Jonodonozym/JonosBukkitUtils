
package jdz.bukkitUtils.components.guiMenu.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStack;

public abstract class GuiMenuConfirmDialogue extends GuiMenu {
	private Inventory inventory;

	private ClickableStack confirmStack;
	private ClickableStack cancelStack;

	public GuiMenuConfirmDialogue(Plugin plugin, String name) {
		super(plugin);
		inventory = Bukkit.createInventory(null, 27, name);

		confirmStack = new ClickableStack(Material.GREEN_WOOL, ChatColor.GREEN + "Confim") {
			@Override
			public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
				onConfirm(player);
			}
		};

		cancelStack = new ClickableStack(Material.RED_WOOL, ChatColor.RED + "Cancel") {
			@Override
			public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
				onCancel(player);
			}
		};

		setItem(confirmStack, 11, inventory);
		setItem(cancelStack, 15, inventory);
	}

	@Override
	public void open(Player player) {
		player.openInventory(inventory);
	}

	public ClickableStack getConfirmStack() {
		return confirmStack;
	}

	public ClickableStack getCancelStack() {
		return cancelStack;
	}

	public abstract void onConfirm(Player player);

	public abstract void onCancel(Player player);

}
