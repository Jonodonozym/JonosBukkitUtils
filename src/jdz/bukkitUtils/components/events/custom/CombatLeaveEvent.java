
package jdz.bukkitUtils.components.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.components.CombatTimer;
import jdz.bukkitUtils.components.events.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CombatLeaveEvent extends Event {
	private final CombatTimer timer;
	private final Player player;

	public static HandlerList getHandlerList() {
		return getHandlers(CombatLeaveEvent.class);
	}
}