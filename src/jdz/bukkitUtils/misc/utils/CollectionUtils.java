
package jdz.bukkitUtils.misc.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {

	@SuppressWarnings("unchecked")
	public static <K, T extends Number> void addToAll(Map<K, T> map, T value) {
		for (K key : map.keySet())
			if (map.get(key) != null)
				map.replace(key, (T) addNumbers(map.get(key), value));
	}

	public static <K, T extends Number> Set<K> removeNonPositive(Map<K, T> map) {
		Set<K> toRemove = new HashSet<K>();
		for (K key : map.keySet())
			if (map.get(key) == null || map.get(key).doubleValue() <= 0)
				toRemove.add(key);
		for (K key : toRemove)
			map.remove(key);
		return toRemove;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] concatArrays(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest)
			totalLength += array.length;

		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	private static Number addNumbers(Number a, Number b) {
		if (a instanceof Double || b instanceof Double)
			return new Double(a.doubleValue() + b.doubleValue());
		else if (a instanceof Float || b instanceof Float)
			return new Float(a.floatValue() + b.floatValue());
		else if (a instanceof Long || b instanceof Long)
			return new Long(a.longValue() + b.longValue());
		else
			return new Integer(a.intValue() + b.intValue());
	}

	public static interface Condition<E> {
		public boolean isTrue(E object);
	}
}
