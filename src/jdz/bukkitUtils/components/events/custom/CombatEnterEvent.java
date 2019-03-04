
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
public class CombatEnterEvent extends Event {
	private final CombatTimer timer;
	private final Player player;
	private final Player opponent;

	public static HandlerList getHandlerList() {
		return getHandlers(CombatEnterEvent.class);
	}
}
