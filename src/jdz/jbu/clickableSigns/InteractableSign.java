
package jdz.jbu.clickableSigns;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public abstract class InteractableSign {
	protected final Block block;
	protected final Sign sign;
	
	public InteractableSign(Block block, Sign sign) {
		this.block = block;
		this.sign = sign;
	}
	
	/**
	 * Is called when a player interacts with the sign
	 * @param player
	 */
	public abstract void onInteract(Player player) throws InvalidSignException;
	
	/**
	 * Is called when the sign is placed
	 * 
	 * WARNING: this sign instance is not kept in memory, so
	 * DO NOT ASSIGN CLASS FIELDS IN THIS METHOD
	 * if you do, then you'll have to do it again in the onInteract
	 * @param player
	 */
	public abstract void onCreate(Player player) throws InvalidSignException;
}
