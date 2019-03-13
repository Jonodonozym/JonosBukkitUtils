
package jdz.bukkitUtils.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.utils.ItemUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemLock implements Listener {
	@Getter private static final ItemLock instance = new ItemLock();

	private static List<ItemStack> lockedItems = new ArrayList<>();
	private static List<Predicate<ItemStack>> lockConditions = new ArrayList<>();

	public static void addCondition(Predicate<ItemStack> predicate) {
		lockConditions.add(predicate);
	}

	public static void lockItem(ItemStack item) {
		if (!isLocked(item))
			lockedItems.add(item);
	}

	public static void unlockItem(ItemStack item) {
		lockedItems.removeIf((lockedItem) -> {
			return ItemUtils.equals(lockedItem, item);
		});
	}

	public static boolean isLocked(ItemStack item) {
		if (item == null)
			return false;

		for (Predicate<ItemStack> condition : lockConditions)
			if (condition.test(item))
				return true;

		for (ItemStack lockedItem : lockedItems)
			if (ItemUtils.equals(item, lockedItem))
				return true;
		return false;
	}

	@EventHandler
	public void onSoulboundDrop(PlayerDropItemEvent e) {
		if (isLocked(e.getItemDrop().getItemStack())) {
			Player p = e.getPlayer();
			p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1F, (float) (Math.random() * 0.25 + 0.25f));
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onUse(PlayerItemDamageEvent e) {
		if (isLocked(e.getItem()))
			e.setCancelled(true);
	}

	private Set<InventoryType> allowedTypes = new HashSet<InventoryType>(Arrays.asList(InventoryType.PLAYER,
			InventoryType.WORKBENCH, InventoryType.CRAFTING, InventoryType.ANVIL, InventoryType.ENCHANTING));

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void StopClicking(InventoryClickEvent event) {
		if (allowedTypes.contains(event.getInventory().getType()))
			return;

		ItemStack stack = event.getCurrentItem();
		if (event.getHotbarButton() != -1)
			stack = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
		if (stack == null || stack.getType() == Material.AIR)
			stack = event.getCursor();
		if (stack == null || stack.getType() == Material.AIR)
			return;

		if (!isLocked(stack))
			return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void StopClicking(InventoryMoveItemEvent event) {
		if (allowedTypes.contains(event.getDestination().getType()))
			return;

		if (isLocked(event.getItem()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClose(InventoryCloseEvent event) {
		if (isLocked(event.getView().getCursor())) {
			event.getPlayer().getInventory().addItem(event.getView().getCursor());
			event.getView().setCursor(null);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.getDrops().removeIf((item) -> {
			return isLocked(item);
		});
	}
}
