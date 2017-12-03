
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;

public abstract class Enchantment extends org.bukkit.enchantments.Enchantment{
	
	public Enchantment(int id) {
		super(id);
	}
	
	public void register() {
		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Enchantment.registerEnchantment(this);
			} catch (IllegalArgumentException e) {
				// if this is thrown it means the id is already taken.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
