
package jdz.bukkitUtils.components;

import org.bukkit.Material;

public interface EnchantTableAddition {
	public boolean useOnTable();

	public double getEnchantChance(int levelCost, Material m);

	public int getEnchantLevel(int levelCost, Material m);
}
