
package jdz.bukkitUtils.misc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.PlayerDamagedByPlayer;
import jdz.bukkitUtils.misc.utils.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

public class CombatTimer implements Listener{
	@Getter private static final CombatTimer timer = new CombatTimer();
	private CombatTimer() {
		registerEvents(JonosBukkitUtils.getInstance());
		Bukkit.getScheduler().runTaskAsynchronously(JonosBukkitUtils.getInstance(), ()->{
			CollectionUtils.addToAll(timers, -1);
			CollectionUtils.removeNonPositive(timers);
		});
	}
	
	@Getter @Setter private int timerTicks = 100;
	private final Map<Player, Player> lastAttacker = new HashMap<Player, Player>();
	private final Map<Player, Integer> timers = new HashMap<Player, Integer>();
	
	public int getTimerTicks(Player player) {
		return timers.containsKey(player)?timers.get(player):0;
	}
	
	public Player getLastAttacker(Player player) {
		return lastAttacker.get(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogout(PlayerQuitEvent event) {
		timers.remove(event.getPlayer());
		lastAttacker.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(PlayerDamagedByPlayer event) {
		timers.put(event.getPlayer(), timerTicks);
		lastAttacker.put(event.getPlayer(), event.getDamager());
	}

}
