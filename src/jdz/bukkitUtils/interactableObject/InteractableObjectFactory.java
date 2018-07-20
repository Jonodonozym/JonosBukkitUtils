
package jdz.bukkitUtils.interactableObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

public class InteractableObjectFactory<T extends InteractableObject> {
	private static final Map<String, InteractableObjectFactory<?>> factories = new HashMap<>();
	private static final Map<Plugin, Set<InteractableObjectFactory<?>>> pluginToFactories = new HashMap<>();

	static InteractableObjectFactory<?> get(Metadatable object) {
		String interactType = (String) object.getMetadata("interactType").get(0).value();
		return factories.get(interactType);
	}

	static Set<InteractableObjectFactory<?>> get(Plugin plugin) {
		if (!pluginToFactories.containsKey(plugin))
			return new HashSet<>();
		return Collections.unmodifiableSet(pluginToFactories.get(plugin));
	}

	@Getter private final Class<T> type;
	@Getter private final String typeName;
	@Getter private final ObjectMaker<T> maker;

	public InteractableObjectFactory(Class<T> type, ObjectMaker<T> maker) {
		this.type = type;
		this.typeName = InteractableObject.getTypeName(type);
		this.maker = maker;
	}

	public void register(Plugin plugin) {
		if (factories.containsKey(typeName))
			throw new IllegalStateException("There is already a factory for object type " + typeName
					+ ". Use @ObjectType on the object to define a new name");
		factories.put(typeName, this);
		if (!pluginToFactories.containsKey(plugin))
			pluginToFactories.put(plugin, new HashSet<>());
		pluginToFactories.get(plugin).add(this);
	}

	public void unregister() {
		factories.remove(typeName);
		for (Plugin plugin : pluginToFactories.keySet())
			if (pluginToFactories.get(plugin).contains(this))
				pluginToFactories.get(plugin).remove(this);
	}

	public static interface ObjectMaker<T extends InteractableObject> {
		public T make(Metadatable object);
	}
}
