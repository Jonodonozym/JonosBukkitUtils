
package jdz.bukkitUtils.nms;

import java.util.Set;

import org.bukkit.Material;

import jdz.bukkitUtils.misc.utils.MaterialUtils;
import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.ItemAxe;
import net.minecraft.server.v1_11_R1.ItemPickaxe;
import net.minecraft.server.v1_11_R1.ItemSpade;

public class NMSAdaptor_11 extends NMSAdaptor{
	
	private final Set<Block> pickaxeSet;
	private final Set<Block> spadeSet;
	private final Set<Block> axeSet;
	
	@SuppressWarnings("unchecked")
	public NMSAdaptor_11() {
		pickaxeSet = getSet(ItemPickaxe.class, "e");
		spadeSet = getSet(ItemSpade.class, "e");
		axeSet = getSet(ItemAxe.class, "e");
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isBestTool(Material tool, org.bukkit.block.Block block) {
		switch (MaterialUtils.getToolType(tool)) {
			case AXE:
				return axeSet.contains(Block.getById(block.getTypeId()));
			case HOE:
				return false;
			case PICKAXE:
				return pickaxeSet.contains(Block.getById(block.getTypeId()));
			case SPADE:
				return spadeSet.contains(Block.getById(block.getTypeId()));
			default:
				return false;
		}
	}
}
