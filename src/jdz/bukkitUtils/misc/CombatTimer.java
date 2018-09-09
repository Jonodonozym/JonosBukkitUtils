
package jdz.bukkitUtils.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.CombatEnterEvent;
import jdz.bukkitUtils.events.custom.CombatLeaveEvent;
import jdz.bukkitUtils.events.custom.CombatLogEvent;
import jdz.bukkitUtils.events.custom.PlayerDamagedByPlayer;
import jdz.bukkitUtils.misc.utils.CollectionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class CombatTimer implements Listener {
	private static final Map<Plugin, List<CombatTimer>> pluginToTimers = new HashMap<Plugin, List<CombatTimer>>();

	@Getter(value = AccessLevel.PROTECTED) private final int timerTicks;
	private final Map<Player, Player> lastAttacker = new HashMap<Player, Player>();
	private final Map<Player, Integer> timers = new HashMap<Player, Integer>();
	@Getter @Setter public boolean doMessages = false;
	private final Set<Player> messages = new HashSet<Player>();

	public CombatTimer(Plugin plugin, int timerTicks) {
		registerEvents(plugin);
		this.timerTicks = timerTicks;

		if (!pluginToTimers.containsKey(plugin))
			pluginToTimers.put(plugin, new ArrayList<CombatTimer>());
		pluginToTimers.get(plugin).add(this);

		Bukkit.getScheduler().runTaskTimerAsynchronously(JonosBukkitUtils.getInstance(), () -> {
			CollectionUtils.addToAll(timers, -10);
			for (Player player : CollectionUtils.removeNonPositive(timers)) {
				new CombatLeaveEvent(this, player).call();
				if (messages.remove(player))
					player.sendMessage(ChatColor.AQUA + "You are no longer in combat");
			}
		}, 10L, 10L);
	}

	public boolean isInCombat(Player player) {
		return getTimerTicks(player) != 0;
	}

	public int getTimerTicks(Player player) {
		return timers.containsKey(player) ? timers.get(player) : 0;
	}

	public Player getLastAttacker(Player player) {
		return lastAttacker.get(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (timers.containsKey(player))
			new CombatLogEvent(this, player, timers.get(player), lastAttacker.get(player)).call();
		timers.remove(player);
		lastAttacker.remove(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(PlayerDamagedByPlayer event) {
		if (!timers.containsKey(event.getPlayer()))
			new CombatEnterEvent(this, event.getPlayer(), event.getDamager()).call();

		if (!timers.containsKey(event.getDamager()))
			new CombatEnterEvent(this, event.getDamager(), event.getPlayer()).call();
			
		timers.put(event.getPlayer(), getTimerTicks());
		timers.put(event.getDamager(), getTimerTicks());
		
		if (doMessages) {
			messages.add(event.getPlayer());
			messages.add(event.getDamager());
		}
		
		lastAttacker.put(event.getPlayer(), event.getDamager());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Projectile))
			return;

		ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
		if (source == null || !(source instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = (Player) source;

		timers.put(player, getTimerTicks());
		timers.put(damager, getTimerTicks());
		if (doMessages) {
			messages.add(player);
			messages.add(damager);
		}
		
		lastAttacker.put(player, damager);
	}

	public void sendMessageOnEnd(Player player) {
		if (timers.containsKey(player))
			messages.add(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		timers.remove(event.getEntity());
		lastAttacker.remove(event.getEntity());

		for (Player player : lastAttacker.keySet())
			if (lastAttacker.get(player).equals(event.getEntity()))
				timers.remove(player);
	}

	static {
		new UnloadListener().registerEvents(JonosBukkitUtils.getInstance());
	}

	private static final class UnloadListener implements Listener {
		@EventHandler
		public void onUnload(PluginDisableEvent event) {
			Plugin plugin = event.getPlugin();
			if (pluginToTimers.containsKey(plugin))
				for (CombatTimer timer : pluginToTimers.remove(plugin))
					timer.unregisterEvents(plugin);
		}
	}
}
