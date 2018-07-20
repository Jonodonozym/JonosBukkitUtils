
package jdz.bukkitUtils.interactableObject;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import jdz.bukkitUtils.JonosBukkitUtils;

public abstract class InteractableObject {
	protected InteractableObject(Metadatable object) {
		object.setMetadata("interactType",
				new FixedMetadataValue(JonosBukkitUtils.getInstance(), getTypeName(getClass())));
	}

	protected void readMetadata(Metadatable object) {
		for (String fieldName : getFields()) {
			if (object.hasMetadata(fieldName))
				try {
					Field f = getClass().getDeclaredField(fieldName);
					f.setAccessible(true);
					f.set(this, object.getMetadata(fieldName).get(0).value());
				}
				catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
		}
	}

	protected void writeMetadata(Metadatable object) {
		for (String fieldName : getFields()) {
			try {
				Field f = getClass().getDeclaredField(fieldName);
				f.setAccessible(true);
				object.setMetadata(fieldName, new FixedMetadataValue(JonosBukkitUtils.getInstance(), f.get(this)));
			}
			catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void onInteract(Player player);

	public abstract String[] getFields();

	public static String getTypeName(Class<? extends InteractableObject> c) {
		ObjectType annotation = c.getAnnotation(ObjectType.class);
		if (annotation == null)
			return c.getSimpleName();
		return annotation.value();
	}
}
