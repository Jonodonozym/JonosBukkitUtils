
package jdz.bukkitUtils.guiMenu.itemStacks;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackCommands extends ClickableStack{
	private final String[] commands;
	private final boolean isConsole;
	
	public ClickableStackCommands(String... commands) {
		this(false, commands);
	}
	
	public ClickableStackCommands(ItemStack i, String... commands) {
		this(i, false, commands);
	}
	
	public ClickableStackCommands(ItemStack i, boolean isConsole, String... commands) {
		super(i);
		this.isConsole = isConsole;
		this.commands = commands;
	}
	
	public ClickableStackCommands(boolean isConsole, String... commands) {
		super();
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
