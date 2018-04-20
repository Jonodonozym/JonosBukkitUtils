
package jdz.bukkitUtils.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Deprecated
public abstract class PotionEffectType extends org.bukkit.potion.PotionEffectType {
	private static Field byIdField;

	static {
		try {
			byIdField = org.bukkit.potion.PotionEffectType.class.getDeclaredField("byId");

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(byIdField, byIdField.getModifiers() & ~Modifier.FINAL);

			byIdField.setAccessible(true);

			Field acceptingNewField = org.bukkit.potion.PotionEffectType.class.getDeclaredField("acceptingNew");
			acceptingNewField.setAccessible(true);
			acceptingNewField.set(null, true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int SID = org.bukkit.potion.PotionEffectType.values().length;

	public PotionEffectType() {
		super(SID++);

		try {
			org.bukkit.potion.PotionEffectType[] currentTypes = (org.bukkit.potion.PotionEffectType[]) byIdField
					.get(null);
			org.bukkit.potion.PotionEffectType[] newTypes = new org.bukkit.potion.PotionEffectType[currentTypes.length
					+ 1];
			for (int i = 0; i < currentTypes.length; i++)
				newTypes[i] = currentTypes[i];
			byIdField.set(null, newTypes);

			registerPotionEffectType(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
