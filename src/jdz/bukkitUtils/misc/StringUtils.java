
package jdz.bukkitUtils.misc;

import java.util.Collection;
import java.util.List;

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
}
