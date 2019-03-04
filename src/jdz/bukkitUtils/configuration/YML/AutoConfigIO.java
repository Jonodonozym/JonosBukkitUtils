
package jdz.bukkitUtils.configuration.YML;

import java.lang.reflect.Type;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class AutoConfigIO {
	public static <E> E parse(Type t, Class<E> c, ConfigurationSection section, String path) {
		return ConfigParser.parse(t, c, section, path);
	}

	public static <E> void save(Type t, Class<E> c, ConfigurationSection section, String path, Object value) {
		ConfigSerializer.save(t, c, section, path, value);
	}

	static {
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
			return c.getBoolean(s);
		}, Boolean.class, boolean.class);

		addAll((c, s) -> {
			return c.getString(s);
		}, String.class);

		addAll((c, s) -> {
			Material m = Material.getMaterial(c.getString(s));
			if (m == null)
				new IllegalArgumentException(s + " is not a valid material or material id").printStackTrace();
			return m;
		}, Material.class);
	}

	private static void addAll(ConfigParser<?> parser, Class<?>... classes) {
		for (Class<?> c : classes) {
			ConfigParser.add(c, parser);
			ConfigSerializer.addGeneric(c);
		}
	}

}
