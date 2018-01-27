
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackLinkedMenu extends ClickableStack{
	private final GuiMenu targetGuiMenu;
	
	public ClickableStackLinkedMenu(Material material, String name, GuiMenu targetGuiMenu) {
		super(material, name);
		this.targetGuiMenu = targetGuiMenu;
	}

	public ClickableStackLinkedMenu(Material material, String name, List<String> lore, GuiMenu targetGuiMenu) {
		super(material, name, lore);
		this.targetGuiMenu = targetGuiMenu;
	}

	@Override
	public void onClick(GuiMenu menu, InventoryClickEvent event) {		
		targetGuiMenu.open((Player)event.getWhoClicked());
	}

}
