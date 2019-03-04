
package jdz.bukkitUtils.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandArgumentParsers {
	public static interface ArgumentParser<E> {
		public E parse(String s);
	}

	private static final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

	public static <E> void addParser(Class<E> clazz, ArgumentParser<E> parser) {
		parsers.put(clazz, parser);
	}

	@SuppressWarnings("unchecked")
	public static <E> ArgumentParser<E> getParser(Class<E> clazz) {
		if (!parsers.containsKey(clazz))
			throw new IllegalArgumentException(
					"No parser defined for " + clazz.getSimpleName() + ", add it with ArgumentParsers.addParser()");
		return (ArgumentParser<E>) parsers.get(clazz);
	}

	@SuppressWarnings("deprecation")
	public static void initDefaults() {
		addParser(String.class, (s) -> {
			return s;
		});

		addNumberParser(int.class, (s) -> {
			return Integer.parseInt(s);
		});
		addNumberParser(Integer.class, (s) -> {
			return Integer.parseInt(s);
		});

		addNumberParser(short.class, (s) -> {
			return Short.parseShort(s);
		});
		addNumberParser(Short.class, (s) -> {
			return Short.parseShort(s);
		});

		addNumberParser(Long.class, (s) -> {
			return Long.parseLong(s);
		});
		addNumberParser(long.class, (s) -> {
			return Long.parseLong(s);
		});

		addNumberParser(Double.class, (s) -> {
			return Double.parseDouble(s);
		});
		addNumberParser(double.class, (s) -> {
			return Double.parseDouble(s);
		});

		addNumberParser(Float.class, (s) -> {
			return Float.parseFloat(s);
		});
		addNumberParser(float.class, (s) -> {
			return Float.parseFloat(s);
		});

		addParser(Player.class, (s) -> {
			Player player = Bukkit.getPlayer(s);
			if (player == null)
				throw new IllegalArgumentException("Player " + s + " is not online!");
			return player;
		});

		addParser(OfflinePlayer.class, (s) -> {
			OfflinePlayer player = Bukkit.getOfflinePlayer(s);
			if (player == null)
				throw new IllegalArgumentException("" + s + " is not a player");
			return player;
		});

		addParser(Material.class, (s) -> {
			Material m = Material.getMaterial(s);
			if (m == null)
				throw new IllegalArgumentException(s + " is not a valid material or material id");
			return m;
		});

		addParser(Plugin.class, (s) -> {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(s);
			if (plugin == null || !plugin.isEnabled())
				throw new IllegalArgumentException("'" + s + "' is not a valid plugin or is not enabled!");
			return plugin;
		});
	}

	private static <E> void addNumberParser(Class<E> clazz, ArgumentParser<E> parser) {
		addParser(clazz, (s) -> {
			try {
				return parser.parse(s);
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("'" + s + "' is not a valid number!");
			}
		});
	}
}
