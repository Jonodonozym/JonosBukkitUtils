
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.bukkitUtils.misc.utils.ColorUtils;
import jdz.bukkitUtils.misc.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AnvilEvent extends Event implements Cancellable {
	@Getter private final Player player;
	@Getter private final ItemStack leftItem, rightItem, result;
	@Getter private final int levelCost;

	public static class CustomEnchantAnvilCrashPreventer implements Listener {

		@EventHandler(priority = EventPriority.LOWEST)
		public void crashBugFix(InventoryClickEvent e) {
			if (!(e.getInventory() instanceof AnvilInventory))
				return;

			InventoryView view = e.getView();
			int rawSlot = e.getRawSlot();

			if (rawSlot != view.convertSlot(rawSlot))
				return;

			if (!ItemUtils.getCustomEnchants(e.getCursor()).isEmpty()) {
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot repair this item on an anvil");
			}
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void crashBugFix(InventoryDragEvent e) {
			if (e.getInventory() instanceof AnvilInventory
					&& (e.getRawSlots().contains(0) || e.getRawSlots().contains(1))) {
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot repair this item on an anvil");
			}
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void crashBugFixShiftClick(InventoryClickEvent e) {
			if (!e.isShiftClick())
				return;

			if (!(e.getView().getTopInventory() instanceof AnvilInventory))
				return;

			int rawSlot = e.getRawSlot();

			if (!ItemUtils.getCustomEnchants(e.getView().getItem(rawSlot)).isEmpty()) {
				e.setCancelled(true);
				e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot repair this item on an anvil");
			}
		}
	}

	static abstract class AnvilEventListener implements Listener {

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
							&& entry.getValue() < entry.getKey().getMaxLevel())
						results.put(entry.getKey(), entry.getValue() + 1);
				} else
					results.put(entry.getKey(), entry.getValue());
			}

			for (Entry<Enchantment, Integer> entry : results.entrySet())
				event.getResult().addUnsafeEnchantment(entry.getKey(), entry.getValue());

			// adding lore
			List<String> leftLore = event.getLeftItem() == null ? new ArrayList<String>()
					: event.getLeftItem().getItemMeta().getLore();
			leftLore = leftLore == null ? new ArrayList<String>() : leftLore;

			List<String> newLore = ItemUtils.getCustomEnchantsLore(event.getResult());
			for (int i = customsLeft.size(); i < leftLore.size(); i++)
				newLore.add(leftLore.get(i));

			ItemMeta im = event.getResult().getItemMeta();
			im.setLore(newLore);

			event.getResult().setItemMeta(im);
		}

		private void fixItemName(AnvilEvent event) {
			ItemMeta im = event.getResult().getItemMeta();
			if (im == null)
				return;
			String displayName = im.getDisplayName();

			String oldName = event.getLeftItem().getItemMeta().getDisplayName();

			if (displayName != null && !oldName.replaceAll("§", "").equals(displayName))
				return;

			im.setDisplayName(ColorUtils.translate(oldName));
			event.getResult().setItemMeta(im);
		}

		protected abstract AnvilEvent onEvent(Player player, ItemStack leftItem, ItemStack rightItem,
				ItemStack resultItem, int cost);
	}
}
