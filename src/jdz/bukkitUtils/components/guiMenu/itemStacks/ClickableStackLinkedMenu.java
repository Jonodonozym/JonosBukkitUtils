
package jdz.bukkitUtils.components.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.components.guiMenu.guis.GuiMenu;

public class ClickableStackLinkedMenu extends ClickableStack {
	private final GuiMenu targetGuiMenu;

	public ClickableStackLinkedMenu(ItemStack item, GuiMenu targetGuiMenu) {
		super(item);
		this.targetGuiMenu = targetGuiMenu;
	}

	public ClickableStackLinkedMenu(Material material, String name, GuiMenu targetGuiMenu) {
		super(material, name);
		this.targetGuiMenu = targetGuiMenu;
	}

	public ClickableStackLinkedMenu(Material material, String name, List<String> lore, GuiMenu targetGuiMenu) {
		super(material, name, lore);
		this.targetGuiMenu = targetGuiMenu;
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		targetGuiMenu.open((Player) event.getWhoClicked());
	}

}
