
package jdz.bukkitUtils.guiMenu.itemStacks;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackNothing extends ClickableStack{
	
	public ClickableStackNothing() {
		super();
	}
	
	public ClickableStackNothing(ItemStack i) {
		super(i);
	}
	
	
	@Override
	public void onClick(GuiMenu menu, InventoryClickEvent event) {
		event.setCancelled(true);
	}

}
