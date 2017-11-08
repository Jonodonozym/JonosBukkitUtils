
package jdz.jbu.guiMenu.itemStacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.jbu.guiMenu.guis.GuiMenu;

public class ClickableStackLinkedMenu extends ClickableStack{
	private final GuiMenu targetGuiMenu;
	
	protected ClickableStackLinkedMenu(GuiMenu targetGuiMenu) {
		super();
		this.targetGuiMenu = null;
	}

	public ClickableStackLinkedMenu(GuiMenu targetGuiMenu, ItemStack i) {
		super(i);
		this.targetGuiMenu = null;
	}

	@Override
	public void onClick(GuiMenu menu, InventoryClickEvent event) {
		targetGuiMenu.open((Player)event.getWhoClicked());
	}

}
