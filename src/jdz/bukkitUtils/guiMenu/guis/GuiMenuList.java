
package jdz.bukkitUtils.guiMenu.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStackNothing;

public class GuiMenuList extends GuiMenu {
	private GuiMenuListPage firstInv;
	private final JavaPlugin plugin;
	private final String name;
	private final GuiMenu superMenu;
	
	public GuiMenuList(JavaPlugin plugin, String name, List<? extends ItemStack> items) {
		this(plugin, name, items, null);
	}

	public GuiMenuList(JavaPlugin plugin, String name, List<? extends ItemStack> items, GuiMenu superMenu) {
		super(plugin);
		
		this.plugin = plugin;
		this.name = name;
		this.superMenu = superMenu;
		
		setItems(items);
	}
	
	public void setItems(List<? extends ItemStack> items) {
		List<ClickableStack> clickables = new ArrayList<ClickableStack>();
		for (ItemStack i : items)
			if (i instanceof ClickableStack)
				clickables.add((ClickableStack) i);
			else
				clickables.add(new ClickableStackNothing(i));

		List<GuiMenuListPage> inventories = new ArrayList<GuiMenuListPage>();

		int numPages = items.size() <= 54 ? 1 : (items.size() + 44) / 45;
		for (int i = 0; i < numPages; i++)
			inventories.add(new GuiMenuListPage(plugin, name, i));

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
