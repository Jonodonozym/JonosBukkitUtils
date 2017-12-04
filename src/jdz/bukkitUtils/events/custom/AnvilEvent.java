
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;

abstract class AnvilEvent extends Event implements Cancellable {
	private final Player player;
	private final ItemStack leftItem, rightItem, resultItem;
	private final int cost;
	
	public AnvilEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem, int cost) {
		this.player = player;
		this.leftItem = leftItem;
		this.rightItem = rightItem;
		this.resultItem = resultItem;
		this.cost = cost;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ItemStack getLeftItem() {
		return leftItem;
	}
	
	public ItemStack getRightItem() {
		return rightItem;
	}
	
	public ItemStack getResult() {
		return resultItem;
	}
	
	public int getLevelCost() {
		return cost;
	}
	
	static abstract class AnvilEventListener implements Listener{

		@EventHandler
		public void onInventoryClick(InventoryClickEvent e){
			if (e.isCancelled())
				return;

			Player player = (Player) e.getWhoClicked();
			Inventory inv = e.getInventory();

			if (!(inv instanceof AnvilInventory))
				return;
			
			AnvilInventory anvil = (AnvilInventory)inv;
			InventoryView view = e.getView();
			int rawSlot = e.getRawSlot();
		 
			if(rawSlot != view.convertSlot(rawSlot)) return;
			if(rawSlot != 2) return;
			
			// all three items in the anvil inventory
			ItemStack[] items = anvil.getContents();
			ItemStack item1 = items[0];
			ItemStack item2 = items[1];

			ItemStack item3 = e.getCurrentItem();
			
			if (item3 == null) return;
			ItemMeta meta = item3.getItemMeta();
		 
			if (meta == null) return;
			if(meta instanceof Repairable) {
				Repairable repairable = (Repairable)meta;
				int repairCost = repairable.getRepairCost();
				if(player.getLevel() >= repairCost) {
					AnvilEvent anvilEvent = onEvent(player, item1, item2, item3, repairCost);
					if (anvilEvent == null) return;
					if (!anvilEvent.isCalled())
						anvilEvent.call();
					if (anvilEvent != null && anvilEvent.isCancelled())
						e.setCancelled(true);
				}
			}
		}
		
		protected abstract AnvilEvent onEvent(Player player, ItemStack leftItem, ItemStack rightItem, ItemStack resultItem, int cost);
	}
}
