
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;

public class ClickableStackCommands extends ClickableStack {
	private final List<String> commands;
	private final boolean isConsole;

	public ClickableStackCommands(ItemStack stack, boolean isConsole, List<String> commands) {
		super(stack);
		this.isConsole = isConsole;
		this.commands = commands;
	}

	public ClickableStackCommands(Material material, String name, boolean isConsole, List<String> commands) {
		super(material, name);
		this.isConsole = isConsole;
		this.commands = commands;
	}

	public ClickableStackCommands(Material material, String name, List<String> lore, boolean isConsole,
			List<String> commands) {
		super(material, name, lore);
		this.isConsole = isConsole;
		this.commands = commands;
	}

	@Override
	public void onClick(Player player, GuiMenu menu, InventoryClickEvent event) {
		if (isConsole)
			for (String command : commands)
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
						insertPlayerName(command, player.getName()));

		else
			for (String command : commands)
				Bukkit.getServer().dispatchCommand(player, insertPlayerName(command, player.getName()));
	}

	private static final String insertPlayerName(String string, String name) {
		String newString = string.toLowerCase().replaceAll("\\{player\\}", name);
		newString = newString.replaceAll("\\{playername\\}", name);
		newString = newString.replaceAll("%player%", name);
		newString = newString.replaceAll("%playername%", name);
		return newString;
	}

	public static void main(String[] args) {
		System.out.println(insertPlayerName("cc open menu", "aXed"));
	}

}
