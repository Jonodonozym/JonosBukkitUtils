package jdz.bukkitUtils.misc.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

public class ColorUtils {
	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static List<String> translate(List<String> string) {
		return string.stream().map(ColorUtils::translate).collect(Collectors.toCollection(ArrayList::new));
	}

	public static String[] translate(String[] strings) {
		String[] retString = new String[strings.length];
		int i = 0;
		for (String s : strings)
			retString[i++] = translate(s);
		return retString;
	}

	public static String[] colorizeLines(ChatColor color, String... lines) {
		String[] retLines = new String[lines.length];
		int i = 0;
		for (String s : lines)
			retLines[i++] = color + s;
		return retLines;
	}

	public static List<String> colorizeLines(ChatColor color, List<String> lines) {
		List<String> retLines = new ArrayList<>();
		for (String s : lines)
			retLines.add(color + s);
		return retLines;
	}

	public static ChatColor getLastColor(String text) {
		for (int i = text.length() - 2; i >= 0; i--) {
			char c = text.charAt(i);
			if (c != '&' && c != '\u00A7')
				continue;
			ChatColor color = ChatColor.getByChar(text.charAt(i + 1));
			if (color != null && color.isColor())
				return color;
		}
		return null;
	}

	public static ChatColor getLastFormat(String text) {
		for (int i = text.length() - 2; i >= 0; i--) {
			char c = text.charAt(i);
			if (c != '&' && c != '\u00A7')
				continue;
			ChatColor color = ChatColor.getByChar(text.charAt(i + 1));
			if (color != null && color.isFormat())
				return color;
		}
		return null;
	}
}
