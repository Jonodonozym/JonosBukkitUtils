
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackCommands extends ClickableStack{
	private final List<String> commands;
	private final boolean isConsole;
	
	public ClickableStackCommands(Material material, String name, boolean isConsole, List<String> commands) {
		super(material, name);
		this.isConsole = isConsole;
		this.commands = commands;
	}
	public ClickableStackCommands(Material material, String name, List<String> lore, boolean isConsole, List<String> commands) {
		super(material, name, lore);
		this.isConsole = isConsole;
		this.commands = commands;
	}
	
	@Override
	public void onClick(GuiMenu menu, InventoryClickEvent event) {
		if (isConsole)
			for (String command: commands)
				Bukkit.getServer().dispatchCommand(event.getWhoClicked(), command);
		
		else
			for (String command: commands)
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), insertPlayerName(command, event.getWhoClicked().getName()));
		
		if (closeOnClick)
			event.getWhoClicked().closeInventory();
	}
	
	private final String insertPlayerName(String string, String name) {
		String newString = string.toLowerCase().replaceAll("\\{player\\}", name);
		newString = newString.replaceAll("\\{playername\\}", name);
		newString = newString.replaceAll("%player%", name);
		newString = newString.replaceAll("%playername%", name);
		newString = newString.replaceAll("[player]", name);
		newString = newString.replaceAll("[playername]", name);
		return newString;
	}

}
