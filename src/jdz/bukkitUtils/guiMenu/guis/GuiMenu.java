
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
	private final Map<Inventory, Map<Integer,ClickableStack>> pages = new HashMap<Inventory, Map<Integer,ClickableStack>>();
	
	protected GuiMenu(JavaPlugin plugin) {
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
        
        if (!pages.containsKey(inv)) return;
        if (!pages.get(inv).containsKey(e.getSlot())) return;
        
        ClickableStack clickable = pages.get(inv).get(e.getSlot());

        if (clickable.isCloseOnClick())
        	p.closeInventory();

        e.setCancelled(true);
        clickable.onClick(this, e);
	}
	
	protected void update(Inventory inv) {
		if (!pages.containsKey(inv))
			return;
		
		for (Integer i: pages.get(inv).keySet())
			inv.setItem(i, pages.get(inv).get(i).getStack());
	}
	
	protected void clear(Inventory inv) {
		if (pages.containsKey(inv))
			pages.get(inv).clear();
		inv.clear();
	}
	
	protected boolean setItem(ClickableStack item, int slot, Inventory inv) {
		if (slot < 0 || slot >= inv.getSize()) return false;

		if (!pages.containsKey(inv))
			pages.put(inv, new HashMap<Integer, ClickableStack>());
		
		pages.get(inv).put(slot, item);
		inv.setItem(slot, item.getStack());
		return true;
	}
	
	public abstract void open(Player player);	
}
