
package jdz.bukkitUtils.guiMenu.guis;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.guiMenu.itemStacks.ClickableStack;

public abstract class GuiMenu implements Listener{
	private final Map<String, Map<Integer,ClickableStack>> pages = new HashMap<String, Map<Integer,ClickableStack>>();
	
	public GuiMenu(JavaPlugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
		Inventory inv = p.getOpenInventory().getTopInventory();

        ItemStack stack = null;
        if (e.getCurrentItem() != null)
            stack = e.getCurrentItem();
        else if (e.getCursor() != null)
            stack = e.getCursor();
        
        if (stack == null) return;
        if (inv == null) return;
        
        if (!pages.containsKey(inv.getName())) return;
        if (!pages.get(inv.getName()).containsKey(e.getSlot())) return;
        
        ClickableStack clickable = pages.get(inv.getName()).get(e.getSlot());
        
        clickable.onClick(this, e);
        e.setCancelled(true);
	}
	
	protected void clear(Inventory inv) {
		if (pages.containsKey(inv.getName()))
			pages.get(inv.getName()).clear();
		inv.clear();
	}
	
	protected boolean setItem(ClickableStack item, int slot, Inventory inv) {
		if (slot < 0 || slot >= inv.getSize()) return false;

		if (!pages.containsKey(inv.getName()))
			pages.put(inv.getName(), new HashMap<Integer, ClickableStack>());
		
		pages.get(inv.getName()).put(slot, item);
		inv.setItem(slot, item.getStack());
		return true;
	}
	
	public abstract void open(Player player);	
}
