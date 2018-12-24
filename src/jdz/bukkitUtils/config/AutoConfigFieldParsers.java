
package jdz.bukkitUtils.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class AutoConfigFieldParsers {
	private static final Map<Class<?>, ConfigParser<?>> parsers = new HashMap<Class<?>, ConfigParser<?>>();
	private static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<Class<?>, ConfigSerializer<?>>();

	public static <E> E parse(Class<E> c, ConfigurationSection section, String path) {
		return getParser(c).parse(section, path);
	}

	public static <E> void save(Class<E> c, ConfigurationSection section, String path, Object value) {
		getSerializer(c).save(section, path, value);
	}

	public static <E> void addParser(Class<E> clazz, ConfigParser<E> parser) {
		parsers.put(clazz, parser);
	}

	public static <E> void addSerializer(Class<E> clazz, ConfigSerializer<E> serializer) {
		serializers.put(clazz, serializer);
	}

	public static <E> void addGenericSerializer(Class<E> clazz) {
		serializers.put(clazz, (config, path, value) -> {
			config.set(path, value);
		});
	}

	public static interface ConfigParser<E> {
		public E parse(ConfigurationSection section, String path);
	}

	public static interface ConfigSerializer<E> {
		public void save(ConfigurationSection section, String path, Object value);
	}

	@SuppressWarnings("unchecked")
	private static <E> ConfigParser<E> getParser(Class<E> clazz) {
		if (!parsers.containsKey(clazz))
			throw new IllegalArgumentException("No parser defined for " + clazz.getSimpleName()
					+ ", add it with AutoConfigFieldParsers.addParser()");
		return (ConfigParser<E>) parsers.get(clazz);
	}

	@SuppressWarnings("unchecked")
	private static <E> ConfigSerializer<E> getSerializer(Class<E> clazz) {
		if (!serializers.containsKey(clazz))
			throw new IllegalArgumentException("No serializer defined for " + clazz.getSimpleName()
					+ ", add it with AutoConfigFieldParsers.addSerializer()");
		return (ConfigSerializer<E>) serializers.get(clazz);
	}

	@SuppressWarnings("deprecation")
	public static void initDefaults() {
		addAll((c, s) -> {
			return c.getInt(s);
		}, Integer.class, int.class);

		addAll((c, s) -> {
			return (short) c.getInt(s);
		}, Short.class, short.class);

		addAll((c, s) -> {
			return c.getLong(s);
		}, Long.class, double.class);

		addAll((c, s) -> {
			return c.getDouble(s);
		}, Double.class, double.class);

		addAll((c, s) -> {
			return (float) c.getDouble(s);
		}, Float.class, float.class);

		addAll((c, s) -> {
			Material m;
			try {
				m = Material.getMaterial(Integer.parseInt(c.getString(s)));
			}
			catch (NumberFormatException e) {
				m = Material.getMaterial(c.getString(s));
			}
			if (m == null)
				new IllegalArgumentException(s + " is not a valid material or material id").printStackTrace();
			return m;
		}, Material.class);

		addCollection(Set.class);
		addCollection(List.class);
	}

	private static void addAll(ConfigParser<?> parser, Class<?>... classes) {
		for (Class<?> c : classes) {
			parsers.put(c, parser);
			addGenericSerializer(c);
		}
	}

	private static <E extends Collection<?>> void addCollection(Class<E> clazz) {
		addParser(clazz, getCollectionParser(clazz));
		addSerializer(clazz, getCollectionSerializer(clazz));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <E extends Collection> ConfigParser<E> getCollectionParser(Class<E> clazz) {
		Class<?> childClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

		ConfigParser<?> childParser = getParser(childClass);

		try {
			Constructor<E> constructor = clazz.getConstructor(Collection.class);

			return (c, s) -> {
				List elements = new ArrayList<>();
				ConfigurationSection newSection = c.getConfigurationSection(s);
				if (newSection != null)
					for (String childPath : newSection.getKeys(false))
						elements.add(childParser.parse(newSection, childPath));
				try {
					return constructor.newInstance(elements);
				}
				catch (ReflectiveOperationException e) {
					e.printStackTrace();
					return null;
				}
			};
		}
		catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <E extends Collection> ConfigSerializer<E> getCollectionSerializer(Class<E> clazz) {
		Class<?> childClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
		ConfigSerializer<?> childSerializer = getSerializer(childClass);
		return (c, s, o) -> {
			E collection = (E) o;
			ConfigurationSection newSection = c.getConfigurationSection(s);
			if (newSection == null)
				newSection = c.createSection(s);
			int i = 0;
			for (Object child : collection)
				childSerializer.save(newSection, "" + (i++), child);
		};
	}
}
