
package jdz.bukkitUtils.misc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

public class MaterialUtils {
	
	//
	// TOOLS & ARMOR
	//
	

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
	public static ResourceType getResource(Material material) {
		if (!isArmour(material) && !isTool(material))
			throw new IllegalArgumentException("getResourceTier requires either armour or tools");

		return ResourceType.valueOf(material.name().split("_")[0]);
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

	public static enum ToolType {
		PICKAXE, AXE, HOE, SPADE
	}

	public static enum ArmourType {
		HELMET, CHESTPLATE, LEGGINGS, BOOTS
	}
	
	
	public static enum ResourceType{
		LEATHER(0),
		WOOD(0),
		GOLD(0),
		STONE(1),
		CHAINMAIL(2),
		IRON(2),
		DIAMOND(3);
		
		private final int tier;
		
		private ResourceType(int tier) {
			this.tier = tier;
		}
		
		public int getTier() {
			return tier;
		}
		
		public Material getMaterial() {
			switch(this) {
				case LEATHER: return Material.LEATHER;
				case WOOD: return Material.WOOD;
				case STONE: return Material.COBBLESTONE;
				case GOLD: return Material.GOLD_INGOT;
				case IRON: return Material.IRON_INGOT;
				case CHAINMAIL: return Material.FIRE;
				case DIAMOND: return Material.DIAMOND;
				default: throw new IllegalStateException("ResourceType "+name()+" has no material component");
			}
		}
	}
	
	
	
	//
	//  CROPS
	//
	
	

	private static final Set<Material> crops = new HashSet<Material>(
			Arrays.asList(Material.CARROT, Material.POTATO, Material.NETHER_STALK, Material.CROPS));

	public static boolean isCrop(Material material) {
		return crops.contains(material);
	}
	
	public static boolean isCropMature(Block block) {
		if(block.getState() == null || !(block.getState() instanceof Crops))
			return false;
		Crops crops = (Crops) block.getState();
		return (crops.getState() == CropState.RIPE);
	}
	
	
	//
	// HARDNESS / BLOCK BREAKING
	//
	
	// https://minecraft.gamepedia.com/Breaking
	public static final Map<Material, Double> blockHardness = new HashMap<Material, Double>();
	public static final Map<Material, ToolType> blockTool = new HashMap<Material, ToolType>();
	public static final Map<Material, Integer> requiredTier = new HashMap<Material, Integer>();
	
	static {
		blockHardness.put(Material.BARRIER, Double.MAX_VALUE);
		blockHardness.put(Material.BEDROCK, Double.MAX_VALUE);
		blockHardness.put(Material.COMMAND, Double.MAX_VALUE);
		blockHardness.put(Material.ENDER_PORTAL, Double.MAX_VALUE);
		blockHardness.put(Material.ENDER_PORTAL_FRAME, Double.MAX_VALUE);
		blockHardness.put(Material.PORTAL, Double.MAX_VALUE);
		
		try { blockHardness.put(Material.STRUCTURE_BLOCK, Double.MAX_VALUE); }
		catch (Exception e) {}

		blockHardness.put(Material.LAVA, 100.0);
		blockHardness.put(Material.WATER, 100.0);
		
		blockHardness.put(Material.OBSIDIAN, 50.0);
		blockHardness.put(Material.ENDER_CHEST, 22.5);
		blockHardness.put(Material.ANVIL, 5.0);
		blockHardness.put(Material.COAL_BLOCK, 5.0);
		blockHardness.put(Material.DIAMOND_BLOCK, 5.0);
		blockHardness.put(Material.EMERALD_BLOCK, 5.0);
		blockHardness.put(Material.IRON_BLOCK, 5.0);
		blockHardness.put(Material.REDSTONE_BLOCK, 5.0);
		blockHardness.put(Material.ENCHANTMENT_TABLE, 5.0);
		blockHardness.put(Material.IRON_FENCE, 5.0);
		blockHardness.put(Material.IRON_DOOR, 5.0);
		blockHardness.put(Material.IRON_TRAPDOOR, 5.0);
		blockHardness.put(Material.MOB_SPAWNER, 5.0);
		blockHardness.put(Material.WEB, 4.0);
		blockHardness.put(Material.DISPENSER, 3.5);
		blockHardness.put(Material.DROPPER, 3.5);
		blockHardness.put(Material.FURNACE, 3.5);
		
		// TODO whenever i can be arsed
		// https://minecraft.gamepedia.com/Breaking
		
	}
	
	
	//
	// ITEM DROPS
	//
	
	
	
	private static final Set<Material> fortuneDropPercents = new HashSet<Material>(Arrays.asList(Material.COAL_ORE,
			Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.QUARTZ_ORE, Material.LAPIS_ORE));

	private static final Set<Material> fortuneMaxDrops = new HashSet<Material>();
	private static final Map<Material, Integer> fortuneMaxDropsLimit = new HashMap<Material, Integer>();
	private static final Map<Material, Integer> defaultDropMin = new HashMap<Material, Integer>();
	private static final Map<Material, Integer> defaultDropMax = new HashMap<Material, Integer>();

	static {
		fortuneMaxDrops.add(Material.REDSTONE);
		fortuneMaxDrops.add(Material.CARROT_ITEM);
		fortuneMaxDrops.add(Material.POTATO_ITEM);
		fortuneMaxDrops.add(Material.MELON);
		fortuneMaxDrops.add(Material.NETHER_WARTS);
		fortuneMaxDrops.add(Material.GLOWSTONE_DUST);
		fortuneMaxDrops.add(Material.PRISMARINE_CRYSTALS);

		fortuneMaxDropsLimit.put(Material.GLOWSTONE_DUST, 4);
		fortuneMaxDropsLimit.put(Material.PRISMARINE_CRYSTALS, 5);
		fortuneMaxDropsLimit.put(Material.MELON, 9);

		defaultDropMin.put(Material.REDSTONE, 4);
		defaultDropMin.put(Material.CARROT_ITEM, 1);
		defaultDropMin.put(Material.POTATO_ITEM, 1);
		defaultDropMin.put(Material.GLOWSTONE_DUST, 2);
		defaultDropMin.put(Material.PRISMARINE_CRYSTALS, 2);
		defaultDropMin.put(Material.MELON, 3);
		defaultDropMin.put(Material.NETHER_WARTS, 2);

		defaultDropMax.put(Material.REDSTONE, 5);
		defaultDropMax.put(Material.CARROT_ITEM, 4);
		defaultDropMin.put(Material.POTATO_ITEM, 4);
		defaultDropMax.put(Material.GLOWSTONE_DUST, 4);
		defaultDropMin.put(Material.PRISMARINE_CRYSTALS, 3);
		defaultDropMin.put(Material.MELON, 7);
		defaultDropMin.put(Material.NETHER_WARTS, 4);
	}

	@SuppressWarnings("deprecation")
	public static List<ItemStack> getDrops(ItemStack tool, Block block) {
		if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0)
			return Arrays.asList(new ItemStack(block.getType(), block.getData()));

		Collection<ItemStack> drops = block.getDrops(tool);

		List<ItemStack> newDrops = new ArrayList<ItemStack>();

		if (isCrop(block.getType()) && !isCropMature(block))
			return newDrops;

		int fortuneLevel = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

		for (ItemStack item : drops)
			newDrops.add(new ItemStack(item.getType(), getDropCount(item.getType(), fortuneLevel)));

		return newDrops;
	}

	private static final SplittableRandom random = new SplittableRandom(System.currentTimeMillis());

	private static int a(Material material, SplittableRandom random) {
		return material == Material.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
	}

	private static int getDropCount(Material mat, int fortuneLevel) {

		if (fortuneDropPercents.contains(mat)) {
			if (fortuneLevel > 0) {
				int drops = random.nextInt(fortuneLevel + 2) - 1;
				if (drops < 0) {
					drops = 0;
				}
				return a(mat, random) * (drops + 1);
			}
			return a(mat, random);
		}

		else if (fortuneMaxDrops.contains(mat)) {
			int dropDifference = defaultDropMax.get(mat) - defaultDropMin.get(mat);

			int drops = defaultDropMin.get(mat) + (random.nextInt(fortuneLevel + dropDifference));

			if (fortuneMaxDropsLimit.containsKey(mat))
				return Math.max(drops, fortuneMaxDropsLimit.get(mat));
			return drops;
		}

		else
			return 1;
	}
}
