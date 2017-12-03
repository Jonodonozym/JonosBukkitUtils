
package jdz.bukkitUtils.misc.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

public class MaterialUtils {

	private static final Set<Material> tools = new HashSet<Material>(Arrays.asList(Material.DIAMOND_PICKAXE,
			Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.GOLD_PICKAXE, Material.WOOD_PICKAXE,
			Material.DIAMOND_SPADE, Material.IRON_SPADE, Material.STONE_SPADE, Material.GOLD_SPADE, Material.WOOD_SPADE,
			Material.DIAMOND_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.GOLD_AXE, Material.WOOD_AXE,
			Material.DIAMOND_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.GOLD_HOE, Material.WOOD_HOE));

	private static final Set<Material> armour = new HashSet<Material>(Arrays.asList(Material.LEATHER_BOOTS,
			Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS, Material.GOLD_BOOTS,
			Material.GOLD_CHESTPLATE, Material.GOLD_HELMET, Material.GOLD_LEGGINGS, Material.CHAINMAIL_BOOTS,
			Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.IRON_BOOTS,
			Material.IRON_CHESTPLATE, Material.IRON_HELMET, Material.IRON_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET, Material.DIAMOND_LEGGINGS));

	public static boolean isTool(Material material) {
		return tools.contains(material);
	}

	public static boolean isArmour(Material material) {
		return armour.contains(material);
	}

	/**
	 * 
	 * @requires isArmour(material) || isTool(material)
	 * @param material
	 * @return
	 */
	public static Material getResourceTier(Material material) {
		if (!isArmour(material) && !isTool(material))
			throw new IllegalArgumentException("getResourceTier requires either armour or tools");

		return Material.valueOf(material.name().split("_")[0]);
	}
	
	/**
	 * @requires isTool(material)
	 * @param material
	 * @return
	 */
	public static ToolType getToolType(Material material) {
		if (!isTool(material))
			throw new IllegalArgumentException("getToolType requires tools to be passed in");
		
		return ToolType.valueOf(material.name().split("_")[1]);
	}
	
	/**
	 * @requires isArmour(material)
	 * @param material
	 * @return
	 */
	public static ArmourType getArmourType(Material material) {
		if (!isTool(material))
			throw new IllegalArgumentException("getArmourType requires armour to be passed in");
		
		return ArmourType.valueOf(material.name().split("_")[1]);
	}
	
	public static enum ToolType{
		PICKAXE,
		AXE,
		HOE,
		SHOVEL
	}
	
	public static enum ArmourType{
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS
	}
}
