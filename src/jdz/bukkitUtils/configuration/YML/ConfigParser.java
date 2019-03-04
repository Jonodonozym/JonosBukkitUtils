
package jdz.bukkitUtils.configuration.YML;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public interface ConfigParser<E> {
	public E parse(ConfigurationSection section, String path);

	public static final Map<Class<?>, ConfigParser<?>> parsers = new HashMap<>();

	public static <E> void add(Class<?> clazz, ConfigParser<?> parser) {
		ConfigParser.parsers.put(clazz, parser);
	}

	@SuppressWarnings("unchecked")
	public static <E> E parse(Type t, Class<E> c, ConfigurationSection section, String path) {
		if (t instanceof ParameterizedType)
			return getCollectionParser((ParameterizedType) t, c).parse(section, path);
		return getParser((Class<E>) t).parse(section, path);
	}

	@SuppressWarnings({ "unchecked" })
	public static <E> ConfigParser<E> getCollectionParser(ParameterizedType t, Class<E> c) {
		final Class<?> nestedClass = getNestedClass(t);
		final ConfigParser<?> nestedParser = getParser(nestedClass);
		return (config, path) -> {
			ConfigurationSection section = config.getConfigurationSection(path);
			String implementedClassName = section.getString("type");

			List<Object> objects = new ArrayList<>();
			for (String key : section.getKeys(false)) {
				if (key.equals("type"))
					continue;
				objects.add(nestedParser.parse(section, key));
			}

			try {
				Class<?> implementedClass = Class.forName(implementedClassName);
				E instance = (E) implementedClass.getConstructor(Collection.class).newInstance(objects);
				return instance;
			}
			catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}

			return null;
		};
	}

	@SuppressWarnings("unchecked")
	public static <E> ConfigParser<E> getParser(Class<E> clazz) {
		if (!parsers.containsKey(clazz))
			throw new IllegalArgumentException("No parser defined for " + clazz.getSimpleName()
					+ ", add it with AutoConfigFieldParsers.addParser()");
		return (ConfigParser<E>) parsers.get(clazz);
	}

	public static Class<?> getNestedClass(ParameterizedType t) {
		return (Class<?>) t.getActualTypeArguments()[0];
	}
}
