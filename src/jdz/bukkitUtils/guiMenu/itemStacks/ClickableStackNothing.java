
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackNothing extends ClickableStack{
	
	public ClickableStackNothing(Material material, String name) {
		super(material, name);
	}
	
	public ClickableStackNothing(Material material, String name, List<String> lore, ItemStack i) {
		super(material, name, lore);
	}
	
	
	@Override
	public void onClick(GuiMenu menu, InventoryClickEvent event) {
		event.setCancelled(true);
		
		if (closeOnClick)
			event.getWhoClicked().closeInventory();
	}

}
