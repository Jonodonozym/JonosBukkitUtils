
package jdz.bukkitUtils.misc;

import java.util.List;

public final class StringUtils {
	public static String listToString(List<String> list, String separator) {
		String returnString = "";
		for (String s : list)
			returnString += s + separator;
		if (returnString.length() > 0)
			returnString = returnString.substring(0, returnString.length() - separator.length());
		return returnString;
	}

	public static String arrayToString(String[] list, int startIndex, String separator) {
		String returnString = "";
		for (int i = startIndex; i < list.length - 1; i++)
			returnString += list[i] + separator;
		if (returnString.length() > 0)
			returnString = returnString.substring(0, returnString.length() - separator.length());
		return returnString;
	}
}
