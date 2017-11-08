
package jdz.bukkitUtils.guiMenu.guis;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;
import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStackLinkedMenu;

class ListGuiPage extends GuiMenu{
	private final Inventory inventory;
	private final int pageNumber;
	
	public ListGuiPage(JavaPlugin plugin, String name, int pageNumber) {
		super(plugin);
		this.pageNumber = pageNumber;
		inventory = Bukkit.createInventory(null, 54, name+" | Page "+pageNumber);
	}

	@Override
	public void open(Player player) {
		player.openInventory(inventory);
	}

	void setup(List<ClickableStack> items, GuiMenu previousPage, GuiMenu nextPage, GuiMenu superMenu) {
		int startIndex = pageNumber * 45;
		int endIndex = items.size() < 54? items.size():Math.min((pageNumber + 1) * 45, items.size());
		
		for (int index = startIndex; index < endIndex; index++)
			setItem(items.get(index), index-startIndex, inventory);
		
		if (previousPage != null)
			setItem(new ChangePageArrow(ArrowType.NEXT, previousPage), 53, inventory);
		if (nextPage != null)
			setItem(new ChangePageArrow(ArrowType.NEXT, nextPage), 45, inventory);
		
		if (superMenu != null)
			setItem(new ReturnPearl(superMenu), 49, inventory);
	}

	private static class ChangePageArrow extends ClickableStackLinkedMenu{
		public ChangePageArrow(ArrowType type, GuiMenu menu){
			super(menu, new ItemStack(Material.ARROW));
			ItemMeta im = getItemMeta();
			im.setDisplayName(type.toString());
			setItemMeta(im);
			setType(Material.ARROW);
		}
	}
	
	private static enum ArrowType{
		PREVIOUS,
		NEXT;
		
		@Override
		public String toString(){
			switch(this){
			case NEXT: return ChatColor.GREEN+"Next Page";
			case PREVIOUS: return ChatColor.GREEN+"Previous Page";
			}
			return "";
		}
	}

	private static class ReturnPearl extends ClickableStackLinkedMenu{
		public ReturnPearl(GuiMenu menu){
			super(menu, new ItemStack(Material.ENDER_PEARL));
			ItemMeta im = getItemMeta();
			im.setDisplayName(ChatColor.GREEN+"Back");
			setItemMeta(im);
		}
	}
}
