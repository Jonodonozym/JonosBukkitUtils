
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import lombok.Getter;

public abstract class ClickableStack {
	@Getter private boolean closeOnClick = false;
	private final ItemStack stack;

	public ClickableStack(ItemStack stack) {
		this.stack = stack;
	}

	public ClickableStack(Material material, String name) {
		this(material, name, new ArrayList<String>());
	};

	public ClickableStack(Material material, String name, List<String> lore) {
		stack = new ItemStack(Material.AIR);
		setInfo(material, name, lore);
	};

	public void closeOnClick() {
		closeOnClick = true;
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setInfo(Material material, String name, List<String> lore) {
		stack.setType(material);
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		stack.setItemMeta(im);
	}

	public abstract void onClick(GuiMenu menu, InventoryClickEvent event);
}
