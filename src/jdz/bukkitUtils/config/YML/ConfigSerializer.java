
package jdz.bukkitUtils.config.YML;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigSerializer<E> {
	public void save(ConfigurationSection section, String path, Object value);

	public static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<Class<?>, ConfigSerializer<?>>();
	
	public static <E> void add(Class<E> clazz, ConfigSerializer<E> serializer) {
		ConfigSerializer.serializers.put(clazz, serializer);
	}

	public static <E> void addGeneric(Class<E> clazz) {
		ConfigSerializer.serializers.put(clazz, (config, path, value) -> {
			config.set(path, value);
		});
	}

	public static <E> void save(Type t, Class<E> c, ConfigurationSection section, String path, Object value) {
		if (t instanceof ParameterizedType)
			getCollectionSerializer((ParameterizedType) t, c).save(section, path, value);
		else
			getSerializer((Class<?>) t).save(section, path, value);
	}

	@SuppressWarnings({ "rawtypes" })
	public static <E> ConfigSerializer<E> getCollectionSerializer(ParameterizedType t, Class<E> c) {
		final Class<?> nestedClass = getNestedClass(t);
		final ConfigSerializer<?> nestedSerialiser = getSerializer(nestedClass);
		return (config, path, value) -> {
			config.set(path + ".type", value.getClass().getName());
			int i = 0;
			for (Object element : (Collection) value)
				nestedSerialiser.save(config, path + "." + (i++), element);
		};
	}

	@SuppressWarnings("unchecked")
	public static <E> ConfigSerializer<E> getSerializer(Class<E> clazz) {
		if (!serializers.containsKey(clazz))
			throw new IllegalArgumentException("No serializer defined for " + clazz.getSimpleName()
					+ ", add it with AutoConfigFieldParsers.addSerializer()");
		return (ConfigSerializer<E>) serializers.get(clazz);
	}

	public static Class<?> getNestedClass(ParameterizedType t) {
		return (Class<?>) t.getActualTypeArguments()[0];
	}
}
