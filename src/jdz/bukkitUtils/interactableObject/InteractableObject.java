
package jdz.bukkitUtils.interactableObject;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import jdz.bukkitUtils.JonosBukkitUtils;

public abstract class InteractableObject {

	protected InteractableObject(Metadatable object, Object... constructorArgs) {
		object.setMetadata("interactType", new FixedMetadataValue(JonosBukkitUtils.getInstance(), getTypeName(getClass())));
	}

	public abstract void onInteract(Player player);

	public static String getTypeName(Class<? extends InteractableObject> c) {
		ObjectType annotation = c.getAnnotation(ObjectType.class);
		if (annotation == null)
			throw new ClassFormatError(c.getSimpleName() + " is missing the ClickableFrameType annotation");
		return annotation.value();
	}
}
