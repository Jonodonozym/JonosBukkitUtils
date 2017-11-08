
package jdz.jbu.guiMenu.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.jbu.guiMenu.itemStacks.ClickableStack;
import jdz.jbu.guiMenu.itemStacks.ClickableStackNothing;

public class ListGui extends GuiMenu {
	private final ListGuiPage firstInv;
	
	public ListGui(JavaPlugin plugin, String name, List<ItemStack> items) {
		this(plugin, name, items, null);
	}

	public ListGui(JavaPlugin plugin, String name, List<ItemStack> items, GuiMenu superMenu) {
		super(plugin);

		List<ClickableStack> clickables = new ArrayList<ClickableStack>();
		for (ItemStack i : items)
			if (i instanceof ClickableStack)
				clickables.add((ClickableStack) i);
			else
				clickables.add(new ClickableStackNothing(i));

		List<ListGuiPage> inventories = new ArrayList<ListGuiPage>();

		int numPages = items.size() <= 54 ? 1 : (items.size() + 44) / 45;
		for (int i = 0; i < numPages; i++)
			inventories.add(new ListGuiPage(plugin, name, i));

		for (int i = 0; i < numPages; i++) {
			GuiMenu nextPage = null;
			GuiMenu previousPage = null;
			if (i > 0)
				previousPage = inventories.get(i - 1);
			if (i < numPages - 1)
				nextPage = inventories.get(i + 1);
			inventories.get(i).setup(clickables, previousPage, nextPage, superMenu);
		}
		
		firstInv = inventories.get(0);
	}

	@Override
	public void open(Player player) {
		firstInv.open(player);
	}
}
