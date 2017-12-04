
package jdz.bukkitUtils.events.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
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
import jdz.bukkitUtils.misc.RomanNumber;
import jdz.bukkitUtils.misc.utils.ColorUtils;
import jdz.bukkitUtils.misc.utils.ItemUtils;

public abstract class AnvilEvent extends Event implements Cancellable {
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

	static abstract class AnvilEventListener implements Listener {

		@EventHandler
		public void onInventoryClick(InventoryClickEvent e) {
			if (e.isCancelled())
				return;

			Player player = (Player) e.getWhoClicked();
			Inventory inv = e.getInventory();

			if (!(inv instanceof AnvilInventory))
				return;

			AnvilInventory anvil = (AnvilInventory) inv;
			InventoryView view = e.getView();
			int rawSlot = e.getRawSlot();

			if (rawSlot != view.convertSlot(rawSlot))
				return;
			if (rawSlot != 2)
				return;

			// all three items in the anvil inventory
			ItemStack[] items = anvil.getContents();
			ItemStack item1 = items[0];
			ItemStack item2 = items[1];

			ItemStack item3 = e.getCurrentItem();

			if (item3 == null)
				return;
			ItemMeta meta = item3.getItemMeta();

			if (meta == null)
				return;
			if (meta instanceof Repairable) {
				Repairable repairable = (Repairable) meta;
				int repairCost = repairable.getRepairCost();
				if (player.getLevel() >= repairCost) {
					AnvilEvent anvilEvent = onEvent(player, item1, item2, item3, repairCost);
					if (anvilEvent == null)
						return;
					if (!anvilEvent.isCalled())
						anvilEvent.call();
					if (anvilEvent.isCancelled())
						e.setCancelled(true);
					else {
						fixCustomEnchants(anvilEvent);
						fixItemName(anvilEvent);
					}
				}
			}
		}

		private void fixCustomEnchants(AnvilEvent event) {
			Map<Enchantment, Integer> customsLeft = event.getLeftItem() == null ? new HashMap<Enchantment, Integer>()
					: ItemUtils.getCustomEnchants(event.getLeftItem());
			Map<Enchantment, Integer> customsRight = event.getRightItem() == null ? new HashMap<Enchantment, Integer>()
					: ItemUtils.getCustomEnchants(event.getRightItem());

			Map<Enchantment, Integer> results = new HashMap<Enchantment, Integer>(customsLeft);
			for (Entry<Enchantment, Integer> entry : customsRight.entrySet()) {
				if (results.containsKey(entry.getKey())) {
					if (results.get(entry.getKey()) < entry.getValue())
						results.put(entry.getKey(), entry.getValue());
					else if (results.get(entry.getKey()) == entry.getValue()
							&& entry.getValue() < entry.getKey().getMaxLevel()
							&& (!(entry.getKey() instanceof jdz.bukkitUtils.misc.Enchantment)
									|| ((jdz.bukkitUtils.misc.Enchantment) entry.getKey()).isUpgradeable()))
						results.put(entry.getKey(), entry.getValue() + 1);
				} else
					results.put(entry.getKey(), entry.getValue());
			}
			
			// removing non-repairable enchantments
			List<Enchantment> toRemove = new ArrayList<Enchantment>();
			for (Entry<Enchantment, Integer> entry : results.entrySet())
				if (entry.getKey() instanceof jdz.bukkitUtils.misc.Enchantment && !((jdz.bukkitUtils.misc.Enchantment)entry.getKey()).keepOnRepair()) 
					toRemove.add(entry.getKey());
			for (Enchantment e: toRemove)
				results.remove(e);
			
			for (Entry<Enchantment, Integer> entry : results.entrySet())
				event.getResult().addUnsafeEnchantment(entry.getKey(), entry.getValue());
			
			// adding lore
			List<String> leftLore = event.getLeftItem() == null? new ArrayList<String>(): event.getLeftItem().getItemMeta().getLore();
			leftLore = leftLore==null?new ArrayList<String>():leftLore;
			
			for (Enchantment e: customsLeft.keySet())
				leftLore.remove(ChatColor.GRAY+e.getName()+(e.getMaxLevel()<=1?"":" "+RomanNumber.of(customsLeft.get(e))));
			
			List<String> newLore = ItemUtils.getCustomEnchantsLore(event.getResult());
			ItemMeta im = event.getResult().getItemMeta();
			if (im.getLore() != null) newLore.addAll(im.getLore());
			im.setLore(newLore);
			event.getResult().setItemMeta(im);
		}
		
		private void fixItemName(AnvilEvent event) {
			ItemMeta im = event.getResult().getItemMeta();
			if (im == null) return;
			String displayName = im.getDisplayName();
			if (displayName == null) return;
			im.setDisplayName(ColorUtils.translate(displayName));
			event.getResult().setItemMeta(im);
		}

		protected abstract AnvilEvent onEvent(Player player, ItemStack leftItem, ItemStack rightItem,
				ItemStack resultItem, int cost);
	}
}
