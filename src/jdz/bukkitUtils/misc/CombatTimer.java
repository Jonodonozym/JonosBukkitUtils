
package jdz.bukkitUtils.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
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
	private final Map<Player, LivingEntity> lastMobAttacker = new HashMap<Player, LivingEntity>();
	private final Map<Player, Integer> timers = new HashMap<Player, Integer>();

	@Getter @Setter public boolean doMessages = false;
	private final Set<Player> messages = new HashSet<Player>();
	@Getter @Setter public boolean triggeredByMobs = false;

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

	public LivingEntity getLastMobAttackerr(Player player) {
		return lastMobAttacker.get(player);
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
		add(event.getPlayer(), event.getDamager());
		add(event.getDamager(), event.getPlayer());

		lastAttacker.put(event.getPlayer(), event.getDamager());
	}

	private void add(Player player, Player opponent) {
		if (!timers.containsKey(player))
			new CombatEnterEvent(this, player, opponent).call();
		timers.put(player, getTimerTicks());
		if (doMessages)
			messages.add(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileHit(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Projectile))
			return;

		ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
		if (source == null || !(source instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = (Player) source;

		add(player, damager);
		add(damager, player);

		lastAttacker.put(player, damager);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMobHit(EntityDamageByEntityEvent event) {
		if (!triggeredByMobs)
			return;

		Player player;
		LivingEntity entity;

		if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity) {
			player = (Player) event.getEntity();
			entity = (LivingEntity) event.getDamager();
		}
		if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			player = (Player) event.getDamager();
			entity = (LivingEntity) event.getEntity();
		}
		else
			return;

		add(player, null);
		lastMobAttacker.put(player, entity);
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
