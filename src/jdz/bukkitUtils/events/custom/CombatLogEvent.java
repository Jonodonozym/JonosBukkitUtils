
package jdz.bukkitUtils.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.events.Event;
import jdz.bukkitUtils.misc.CombatTimer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CombatLogEvent extends Event {
	private final CombatTimer timer;
	private final Player player;
	private final int remainingTicks;
	private final Player attacker;

	public static HandlerList getHandlerList() {
		return getHandlers(CombatLogEvent.class);
	}
}
