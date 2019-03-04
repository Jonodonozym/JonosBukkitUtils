
package jdz.bukkitUtils.components.messengers;

import java.util.UUID;

import jdz.bukkitUtils.persistence.ORM.SQLDataClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Message extends SQLDataClass implements Comparable<Message> {
	@Getter private final String serverName;
	@Getter private final UUID playerUUID;
	@Getter private final String message;
	@Getter private final int priority;

	@Override
	public int compareTo(Message o) {
		return getPriority() - o.getPriority();
	}
}
