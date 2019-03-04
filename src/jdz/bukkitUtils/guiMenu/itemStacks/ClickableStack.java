
package jdz.bukkitUtils.guiMenu.itemStacks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.guiMenu.guis.GuiMenu;
import jdz.bukkitUtils.misc.utils.ItemUtils;
import lombok.Getter;

public abstract class ClickableStack {
	@Getter private boolean closeOnClick = false;
	@Getter private final ItemStack stack;

	public ClickableStack() {
		this(new ItemStack(Material.AIR));
	}

	public ClickableStack(Material material) {
		this(new ItemStack(material));
	}

	public ClickableStack(ItemStack stack) {
		this.stack = stack;
	}

	public ClickableStack(Material material, String name) {
		this(material, name, new ArrayList<String>());
	};

	public ClickableStack(Material material, String name, List<String> lore) {
		this(material);
		setName(name);
		setLore(lore);
	};

	public void closeOnClick() {
		closeOnClick = true;
	}

	public void setMaterial(Material material) {
		stack.setType(material);
	}

	public void setData(int data) {
		ItemUtils.setDamage(stack, data);
	}

	public void setName(String name) {
		ItemUtils.setName(stack, name);
	}

	public void setLore(List<String> lore) {
		ItemUtils.setLore(stack, lore);
	}

	public void update() {}

	public abstract void onClick(Player player, GuiMenu menu, InventoryClickEvent event);
}
