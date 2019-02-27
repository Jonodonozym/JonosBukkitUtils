
package jdz.bukkitUtils.sql.ORM;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLDataSerialiser {
	private static final Map<Class<?>, ParserMethod<?>> parserMethods = new HashMap<>();
	@SuppressWarnings("rawtypes") private static final Map<Class<?>, Serialiser> serialiserMethods = new HashMap<>();

	public static boolean hasMethods(Class<?> c) {
		return parserMethods.containsKey(c) && serialiserMethods.containsKey(c);
	}

	@SuppressWarnings("unchecked")
	public static <T> ParserMethod<T> getParser(Class<T> c) {
		return (ParserMethod<T>) parserMethods.get(c);
	}

	@SuppressWarnings("rawtypes")
	public static Serialiser getSerialiser(Class<?> c) {
		return serialiserMethods.get(c);
	}

	public static <T> void addParser(Class<T> c, ParserMethod<T> method) {
		addParserSerialiser(c, method, (o) -> {
			if (o == null)
				return null;
			return o.toString();
		});
	}

	public static <T> void addParserSerialiser(Class<T> c, ParserMethod<T> method, Serialiser<T> serialiser) {
		parserMethods.put(c, method);
		serialiserMethods.put(c, serialiser);
	}

	public static interface ParserMethod<E> {
		public E parse(String s);
	}

	public static interface Serialiser<E> {
		public String serialise(E object);
	}

	static {
		addParser(int.class, (s) -> {
			return Integer.parseInt(s);
		});
		addParser(Integer.class, (s) -> {
			return Integer.parseInt(s);
		});

		addParser(double.class, (s) -> {
			return Double.parseDouble(s);
		});
		addParser(Double.class, (s) -> {
			return Double.parseDouble(s);
		});

		addParser(long.class, (s) -> {
			return Long.parseLong(s);
		});
		addParser(Long.class, (s) -> {
			return Long.parseLong(s);
		});

		addParser(float.class, (s) -> {
			return Float.parseFloat(s);
		});
		addParser(Float.class, (s) -> {
			return Float.parseFloat(s);
		});


		addParserSerialiser(boolean.class, (s) -> {
			return Integer.valueOf(s) == 1;
		}, (bool) -> {
			return "" + (bool.booleanValue() ? 1 : 0);
		});

		addParserSerialiser(Boolean.class, (s) -> {
			return Integer.valueOf(s) == 1;
		}, (bool) -> {
			return "" + (bool.booleanValue() ? 1 : 0);
		});

		addParser(String.class, (s) -> {
			return s.trim();
		});

		addParser(char.class, (s) -> {
			return s.charAt(0);
		});
		addParser(Character.class, (s) -> {
			return s.charAt(0);
		});

		addParser(short.class, (s) -> {
			return Short.valueOf(s);
		});
		addParser(Short.class, (s) -> {
			return Short.valueOf(s);
		});

		addParser(byte.class, (s) -> {
			return Byte.valueOf(s);
		});
		addParser(Byte.class, (s) -> {
			return Byte.valueOf(s);
		});

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		addParserSerialiser(java.util.Date.class, (s) -> {
			try {
				return new java.util.Date(format.parse(s).getTime());
			}
			catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}, (date) -> {
			return format.format(date);
		});

		addParser(UUID.class, (s) -> {
			return UUID.fromString(s);
		});

		addParser(Date.class, (s) -> {
			try {
				return new Date(format.parse(s).getTime());
			}
			catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
