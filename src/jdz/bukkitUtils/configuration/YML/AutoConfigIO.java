
package jdz.bukkitUtils.configuration.YML;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import jdz.bukkitUtils.utils.WorldUtils;

public class AutoConfigIO {
	public static <E> E parse(Type t, Class<E> c, ConfigurationSection section, String path) throws ParseException {
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

		DateFormat format = new SimpleDateFormat();
		addAll((c, s) -> {
			return format.parse(c.getString(s));
		}, (c, s, date) -> {
			c.set(s, format.format(date));
		}, Date.class);

		addAll((c, s) -> {
			Material m = Material.getMaterial(c.getString(s));
			if (m == null)
				new IllegalArgumentException(c.getString(s) + " is not a valid material or material id")
						.printStackTrace();
			return m;
		}, (c, s, mat) -> {
			c.set(s, ((Material) mat).name());
		}, Material.class);

		addAll((c, s) -> {
			return Bukkit.getOfflinePlayer(UUID.fromString(c.getString(s)));
		}, (c, s, player) -> {
			c.set(s, ((OfflinePlayer) player).getUniqueId().toString());
		}, OfflinePlayer.class);

		addAll((c, s) -> {
			return WorldUtils.locationFromString(c.getString(s));
		}, (c, s, loc) -> {
			c.set(s, WorldUtils.locationToString((Location) loc));
		}, Location.class);

		addAll((c, s) -> {
			return Bukkit.getWorld(c.getString(s));
		}, (c, s, world) -> {
			c.set(s, ((World) world).getName());
		}, World.class);

		addAll((c, s) -> {
			return WorldUtils.chunkFromString(c.getString(s));
		}, (c, s, chunk) -> {
			c.set(s, WorldUtils.chunkToString((Chunk) chunk));
		}, Chunk.class);
	}

	private static <T> void addAll(ConfigParser<T> parser, ConfigSerializer<T> serializer, Class<T> c) {
		ConfigParser.add(c, parser);
		ConfigSerializer.add(c, serializer);
	}

	private static void addAll(ConfigParser<?> parser, Class<?>... classes) {
		for (Class<?> c : classes) {
			ConfigParser.add(c, parser);
			ConfigSerializer.addGeneric(c);
		}
	}

}
