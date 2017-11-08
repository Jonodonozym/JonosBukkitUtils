
package jdz.jbu.clickableSigns;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
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
	
	private boolean registered = false;
	public void register() {
		if (!registered) {
			plugin.getServer().getPluginManager().registerEvents(this,  plugin);
			registered = true;
		}
	}
	
	public boolean isRegistered() {
		return registered;
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (!action.equals(Action.RIGHT_CLICK_BLOCK))
			return;

		if (event.getClickedBlock() == null || event.getClickedBlock().getState() == null || !(event.getClickedBlock().getState() instanceof Sign))
			return;

		InteractableSign sign;
		try {
			sign = factory.construct(event.getClickedBlock(), (Sign) event.getClickedBlock().getState());

			if (sign == null)
				return;

			sign.onInteract(player);
		} catch (InvalidSignException e) {
			player.sendMessage(ChatColor.RED + "An error occurred trying to click this sign.");
			new FileLogger(plugin).createErrorLog(e);
		}
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		Player player = event.getPlayer();
		
		try {
			InteractableSign sign = factory.construct(event.getBlock(), event.getLines());

			System.out.println(sign);
			if (sign == null)
				return;

			SignCreatePermission perms = sign.getClass().getAnnotation(SignCreatePermission.class);

			if (perms != null && !player.hasPermission(perms.value())) {
				event.getBlock().breakNaturally();
				player.sendMessage("You don't have the permissions to place that sign!");
				return;
			}

			sign.onCreate(player);
			player.sendMessage(ChatColor.GREEN+sign.getType()+" sign created!");
		} catch (InvalidSignException e) {
			event.getBlock().breakNaturally();
			player.sendMessage(ChatColor.RED + e.getMessage());
		}
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (event.getBlock().getState() == null || !(event.getBlock().getState() instanceof Sign))
			return;

		try {
			InteractableSign sign = factory.construct(event.getBlock(), (Sign) event.getBlock().getState());

			if (sign == null)
				return;

			SignCreatePermission perms = sign.getClass().getAnnotation(SignCreatePermission.class);

			if (perms != null && !player.hasPermission(perms.value())) {
				event.setCancelled(true);
				player.sendMessage("You don't have the permissions to break that sign!");
				return;
			}
		} catch (InvalidSignException e) { }
	}
}
