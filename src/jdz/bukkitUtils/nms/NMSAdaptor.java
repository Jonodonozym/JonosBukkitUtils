
package jdz.bukkitUtils.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class NMSAdaptor {

	public static String getVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf(".") + 1);
	}

	public static NMSAdaptor getAdaptor() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Set getSet(Class<?> c, String fieldName) {
		try {
			Field f = c.getDeclaredField(fieldName);
			f.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			return (Set) f.get(null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public abstract boolean isBestTool(Material toolType, Block block);
}
