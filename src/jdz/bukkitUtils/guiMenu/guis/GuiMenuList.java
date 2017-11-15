
package jdz.bukkitUtils.guiMenu.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;

public class GuiMenuList extends GuiMenu {
	private List<GuiMenuListPage> pages = new ArrayList<GuiMenuListPage>();
	
	private List<ClickableStack> items = new ArrayList<ClickableStack>();
	
	private final JavaPlugin plugin;
	private final String name;
	private final GuiMenu superMenu;
	
	public GuiMenuList(JavaPlugin plugin, String name, List<ClickableStack> items) {
		this(plugin, name, items, null);
	}

	public GuiMenuList(JavaPlugin plugin, String name, List<ClickableStack> items, GuiMenu superMenu) {
		super(plugin);
		
		this.plugin = plugin;
		this.name = name;
		this.superMenu = superMenu;
		
		setItems(new ArrayList<ClickableStack>(items));
	}
	
	public void setItems(List<ClickableStack> items) {
		this.items = items;
		
		pages.clear();
		int numPages = items.size() <= 54 ? 1 : (items.size() + 44) / 45;
		for (int i = 0; i < numPages; i++)
			pages.add(new GuiMenuListPage(plugin, name, i));

		for (int i = 0; i < numPages; i++) {
			GuiMenu nextPage = null;
			GuiMenu previousPage = null;
			if (i > 0)
				previousPage = pages.get(i - 1);
			if (i < numPages - 1)
				nextPage = pages.get(i + 1);
			pages.get(i).setup(items, previousPage, nextPage, superMenu);
		}
	}
	
	public void add(ClickableStack stack) {
		items.add(stack);
		setItems(items);
	}
	
	public void remove(ClickableStack stack) {
		items.remove(stack);
		setItems(items);
	}

	@Override
	public void open(Player player) {
		pages.get(0).open(player);
	}
}
