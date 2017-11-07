
package jdz.jbu.clickableSigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.jbu.fileIO.FileLogger;

public class SignEventListener implements Listener {
	private final InteractableSignFactory factory;
	private final JavaPlugin plugin;

	@SafeVarargs
	public SignEventListener(JavaPlugin plugin, Class<? extends InteractableSign>... classes) {
		this.factory = new InteractableSignFactory(classes);
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return;

		Material clickedBlock = event.getClickedBlock().getType();

		if (clickedBlock != Material.WALL_SIGN && clickedBlock != Material.SIGN_POST)
			return;

		InteractableSign sign;
		try {
			sign = factory.construct(event.getClickedBlock());

			if (sign == null)
				return;

			sign.onInteract(player);
		} catch (InvalidSignException e) {
			player.sendMessage(ChatColor.RED + "An error occurred trying to click this sign");
			new FileLogger(plugin).createErrorLog(e);
		}
	}

	@EventHandler
	public void onSignPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		Material placedBlock = event.getBlock().getType();

		if (placedBlock != Material.WALL_SIGN && placedBlock != Material.SIGN_POST)
			return;

		try {
			InteractableSign sign = factory.construct(event.getBlock());

			if (sign == null)
				return;

			SignCreatePermission perms = sign.getClass().getAnnotation(SignCreatePermission.class);

			if (perms != null && !player.hasPermission(perms.value())) {
				event.setCancelled(true);
				player.sendMessage("You don't have the permissions to place that sign!");
				return;
			}

			sign.onCreate(player);
		} catch (InvalidSignException e) {
			player.sendMessage(ChatColor.RED + "An error occurred trying to click this sign");
			new FileLogger(plugin).createErrorLog(e);
		}
	}
}
