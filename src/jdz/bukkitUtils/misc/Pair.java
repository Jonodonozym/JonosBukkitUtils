
package jdz.bukkitUtils.misc;

import lombok.Data;

@Data
public class Pair<E extends Object, F extends Object> {
	private final E key;
	private final F value;
}
