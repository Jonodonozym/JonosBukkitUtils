
package jdz.bukkitUtils.interactableObject;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.Metadatable;

import jdz.bukkitUtils.events.Listener;

public class InteractableObjectListener implements Listener {

	@EventHandler
	public void onUnload(PluginDisableEvent event) {
		for (InteractableObjectFactory<?> f : InteractableObjectFactory.get(event.getPlugin()))
			f.unregister();
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (!isInteractable(event.getRightClicked()))
			return;

		event.setCancelled(true);
		onInteract(event.getPlayer(), event.getRightClicked());
	}

	@EventHandler
	public void onFrameBrake(HangingBreakEvent event) {
		if (!isInteractable(event.getEntity()))
			return;

		if (!canBreak(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!isInteractable(event.getEntity()))
			return;

		if (!canBreak(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (tryInteract(event.getPlayer(), event.getClickedBlock()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (getBlockMeta(event.getBlock()) != null)
			event.setCancelled(true);
	}

	private Metadatable getBlockMeta(Block block) {
		Metadatable object = block;

		if (!isInteractable(object)) {
			object = block.getState();
			if (!isInteractable(object))
				return null;
		}

		return object;
	}

	private boolean tryInteract(Player player, Block block) {
		Metadatable object = getBlockMeta(block);
		if (object == null)
			return false;
		onInteract(player, object);
		return true;
	}

	private boolean isInteractable(Metadatable object) {
		return InteractableObjectFactory.get(object) != null;
	}

	private boolean canBreak(Metadatable object) {
		return InteractableObjectFactory.get(object).getType().getAnnotation(ObjectType.class).canBreak();
	}

	private void onInteract(Player player, Metadatable object) {
		InteractableObjectFactory.get(object).getMaker().make(object).onInteract(player);
	}

}
