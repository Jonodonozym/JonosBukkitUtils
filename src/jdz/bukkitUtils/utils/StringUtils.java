
package jdz.bukkitUtils.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;

public final class StringUtils {
	public static String collectionToString(Collection<? extends Object> list, String separator) {
		String returnString = "";
		for (Object s : list)
			returnString += s.toString() + separator;
		if (returnString.length() > 0)
			returnString = returnString.substring(0, returnString.length() - separator.length());
		return returnString;
	}

	public static String listToString(List<? extends Object> list, String separator) {
		return collectionToString(list, separator);
	}

	public static String arrayToString(Object[] list, int startIndex, String separator) {
		String returnString = "";
		for (int i = startIndex; i < list.length - 1; i++)
			returnString += list[i].toString() + separator;
		if (returnString.length() > 0)
			returnString = returnString.substring(0, returnString.length() - separator.length());
		return returnString;
	}

	public static String repeat(String string, int numRepeats) {
		String returnString = "";
		for (int i = 0; i < numRepeats; i++)
			returnString += string;
		return returnString;
	}

	public static boolean isVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
	}

	public static String capitalizeWord(String word) {
		return word.toUpperCase().charAt(0) + word.toLowerCase().substring(1);
	}

	public static String capitalizeWords(String words) {
		String combined = "";
		String[] wordArray = words.split("//s+");
		for (String word : wordArray)
			combined += " " + capitalizeWord(word);
		return combined.replaceFirst(" ", "");
	}

	public static String[] splitIntoColorizedLines(String s, int charsPerLine) {
		String[] lines = StringUtils.splitIntoLines(s, charsPerLine);
		for (int i = 0; i < lines.length - 1; i++) {
			ChatColor lastColor = ColorUtils.getLastColor(lines[i]);
			ChatColor lastFormat = ColorUtils.getLastFormat(lines[i]);
			if (lastColor != null)
				lines[i + 1] = lastColor + lines[i + 1];
			if (lastFormat != null)
				lines[i + 1] = lastFormat + lines[i + 1];
		}
		return lines;
	}

	public static String[] splitIntoLines(String s, int charsPerLine) {
		if (s == null || s.equals(""))
			return new String[] { "" };

		List<String> lines = new ArrayList<>();

		for (String line : s.split("\n")) {
			String buffer = "";
			for (String word : line.split(" ")) {
				if (buffer.length() > charsPerLine) {
					lines.add(buffer.trim());
					buffer = "";
				}
				buffer += " " + word;
			}
			lines.add(buffer.trim());
		}
		return lines.toArray(new String[lines.size()]);
	}
}
