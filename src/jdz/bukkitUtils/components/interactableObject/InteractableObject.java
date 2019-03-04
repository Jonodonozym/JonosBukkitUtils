
package jdz.bukkitUtils.components.interactableObject;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.persistence.ORM.NoSave;

public abstract class InteractableObject {
	private final List<Field> fields = new ArrayList<>();

	protected InteractableObject() {
		Class<?> c = getClass();
		while (!c.equals(InteractableObject.class)) {
			fields.addAll(getFields(c));
			c = c.getSuperclass();
		}
	}

	private List<Field> getFields(Class<?> c) {
		List<Field> fields = new ArrayList<>();
		for (Field field : getClass().getDeclaredFields())
			if (field.getAnnotation(NoSave.class) == null) {
				fields.add(field);
				field.setAccessible(true);
			}
		return fields;
	}

	protected void readMetadata(Metadatable object) {
		for (Field field : fields)
			if (object.hasMetadata(field.getName()))
				try {
					field.set(this, object.getMetadata(field.getName()).get(0).value());
				}
				catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
	}

	protected void writeMetadata(Metadatable object) {
		object.setMetadata("interactType",
				new FixedMetadataValue(JonosBukkitUtils.getInstance(), getTypeName(getClass())));

		for (Field field : fields)
			try {
				object.setMetadata(field.getName(),
						new FixedMetadataValue(JonosBukkitUtils.getInstance(), field.get(this)));
			}
			catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
	}

	public abstract void onInteract(Player player);

	public static String getTypeName(Class<? extends InteractableObject> c) {
		return c.getName();
	}
}
