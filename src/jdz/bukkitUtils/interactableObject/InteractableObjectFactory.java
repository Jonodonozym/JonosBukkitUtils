
package jdz.bukkitUtils.interactableObject;

import java.lang.reflect.ParameterizedType;
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
		if (object.hasMetadata("interactType")) {
			String interactType = (String) object.getMetadata("interactType").get(0).value();
			return factories.get(interactType);
		}
		return null;
	}

	static Set<InteractableObjectFactory<?>> get(Plugin plugin) {
		if (!pluginToFactories.containsKey(plugin))
			return new HashSet<>();
		return Collections.unmodifiableSet(pluginToFactories.get(plugin));
	}

	@Getter private final Class<T> type;
	@Getter private final String typeName;

	@SuppressWarnings("unchecked")
	public InteractableObjectFactory() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.typeName = InteractableObject.getTypeName(type);
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

	public T makeFrom(Metadatable object) throws ReflectiveOperationException {
		T instance = type.newInstance();
		instance.readMetadata(object);
		return instance;
	}
}
