
package jdz.bukkitUtils.misc;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

public class BlockIteratorProtected extends org.bukkit.util.BlockIterator {

	private Block last = null;

	public BlockIteratorProtected(LivingEntity entity) {
		super(entity);
	}

	public BlockIteratorProtected(LivingEntity entity, int distance) {
		super(entity, distance);
	}

	@Override
	public boolean hasNext() {
		if (last == null || (last.getY() > 1 && last.getY() < 255))
			return super.hasNext();
		return false;
	}

	@Override
	public Block next() {
		last = super.next();
		return last;
	}
}
