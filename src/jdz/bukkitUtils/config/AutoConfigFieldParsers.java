
package jdz.bukkitUtils.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class AutoConfigFieldParsers {
	private static final Map<Class<?>, ConfigParser<?>> parsers = new HashMap<Class<?>, ConfigParser<?>>();
	private static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<Class<?>, ConfigSerializer<?>>();

	public static <E> void addParser(Class<E> clazz, ConfigParser<E> parser) {
		parsers.put(clazz, parser);
	}
	public static <E> void addSerializer(Class<E> clazz, ConfigSerializer<E> serializer) {
		serializers.put(clazz, serializer);
	}
	
	public static <E> void addGenericSerializer(Class<E> clazz) {
		serializers.put(clazz, (config, path, value)->{
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
	public static <E> ConfigParser<E> getParser(Class<E> clazz) {
		if (!parsers.containsKey(clazz))
			throw new IllegalArgumentException(
					"No parser defined for " + clazz.getSimpleName() + ", add it with AutoConfigFieldParsers.addParser()");
		return (ConfigParser<E>) parsers.get(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> ConfigSerializer<E> getSerializer(Class<E> clazz) {
		if (!serializers.containsKey(clazz))
			throw new IllegalArgumentException(
					"No serializer defined for " + clazz.getSimpleName() + ", add it with AutoConfigFieldParsers.addSerializer()");
		return (ConfigSerializer<E>) serializers.get(clazz);
	}
	
	private static void addAll(ConfigParser<?> parser, Class<?>... classes) {
		for (Class<?> c: classes) {
			parsers.put(c, parser);
			addGenericSerializer(c);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void initDefaults() {
		addAll((c, s)->{
			return c.getInt(s);
		}, Integer.class, int.class);	
		
		addAll((c, s)->{
			return (short)c.getInt(s);
		}, Short.class, short.class);	
		
		addAll((c, s)->{
			return c.getLong(s);
		}, Long.class, double.class);
		
		addAll((c, s)->{
			return c.getDouble(s);
		}, Double.class, double.class);
		
		addAll((c, s)->{
			return (float)c.getDouble(s);
		}, Float.class, float.class);
		
		addAll((c, s)->{
			Material m;
			try {
				m = Material.getMaterial(Integer.parseInt(c.getString(s)));
			}
			catch (NumberFormatException e) {
				m = Material.getMaterial(c.getString(s));
			}
			if (m == null)
				throw new IllegalArgumentException(s + " is not a valid material or material id");
			return m;
		}, Material.class);
	}
}
