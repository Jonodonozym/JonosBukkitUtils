
package jdz.bukkitUtils.components.guiMenu.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.components.guiMenu.itemStacks.ClickableStack;

public class GuiMenuList extends GuiMenu {
	private List<GuiMenuListPage> pages = new ArrayList<>();

	private List<ClickableStack> items = new ArrayList<>();

	private final Plugin plugin;
	private final String name;
	private final GuiMenu superMenu;

	public GuiMenuList(Plugin plugin, String name) {
		this(plugin, name, Arrays.asList(), null);
	}

	public GuiMenuList(Plugin plugin, String name, List<ClickableStack> items) {
		this(plugin, name, items, null);
	}

	public GuiMenuList(Plugin plugin, String name, List<ClickableStack> items, GuiMenu superMenu) {
		super(plugin);

		this.plugin = plugin;
		this.name = name;
		this.superMenu = superMenu;

		setItems(new ArrayList<>(items));
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

	protected List<GuiMenuListPage> getPages() {
		return Collections.unmodifiableList(pages);
	}

	@Override
	public void open(Player player) {
		pages.get(0).open(player);
	}
}
