
package jdz.bukkitUtils.components.guiMenu.itemStacks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import jdz.bukkitUtils.components.guiMenu.guis.GuiMenu;

public class ClickableStackReturnArrow extends ClickableStack {
	private final GuiMenu superMenu;
	private final String returnCommand;

	public ClickableStackReturnArrow() {
		this(null, null);
	}

	public ClickableStackReturnArrow(String returnCommand) {
		this(null, returnCommand);
	}

	public ClickableStackReturnArrow(GuiMenu superMenu) {
		this(superMenu, "");
	}

	public ClickableStackReturnArrow(GuiMenu superMenu, String returnCommand) {
		super(Material.ARROW, ChatColor.AQUA
				+ (superMenu == null && (returnCommand == null || returnCommand.equals("")) ? "Exit" : "Return"));
		this.superMenu = superMenu;
		this.returnCommand = returnCommand;
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		if (returnCommand != null && !returnCommand.equals(""))
			Bukkit.dispatchCommand(player, returnCommand);
		else if (superMenu != null)
			superMenu.open(player);
		else
			player.closeInventory();
	}

}
