
package jdz.bukkitUtils.guiMenu.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;

public abstract class GuiMenuConfirmDialogue extends GuiMenu {
	private Inventory inventory;

	private ClickableStack confirmStack;
	private ClickableStack cancelStack;

	@SuppressWarnings("deprecation")
	public GuiMenuConfirmDialogue(JavaPlugin plugin, String name) {
		super(plugin);
		inventory = Bukkit.createInventory(null, 45);

		confirmStack = new ClickableStack(new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData())) {
			@Override
			public void onClick(GuiMenu menu, InventoryClickEvent event) {
				onConfirm();
			}
		};
		ItemMeta confirmItemMeta = confirmStack.getItemMeta();
		confirmItemMeta.setDisplayName(ChatColor.GREEN + "Confirm");
		confirmStack.setItemMeta(confirmItemMeta);

		cancelStack = new ClickableStack(new ItemStack(Material.WOOL, 1, DyeColor.RED.getWoolData())) {
			@Override
			public void onClick(GuiMenu menu, InventoryClickEvent event) {
				onCancel();
			}
		};
		ItemMeta cancelItemMeta = cancelStack.getItemMeta();
		cancelItemMeta.setDisplayName(ChatColor.RED + "Cancel");
		cancelStack.setItemMeta(cancelItemMeta);

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

	public abstract void onConfirm();

	public abstract void onCancel();

}
