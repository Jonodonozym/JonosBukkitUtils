
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackNothing extends ClickableStack {

	public ClickableStackNothing(ItemStack stack) {
		super(stack);
	}

	public ClickableStackNothing(Material material, String name) {
		super(material, name);
	}

	public ClickableStackNothing(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}


	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {}

}
