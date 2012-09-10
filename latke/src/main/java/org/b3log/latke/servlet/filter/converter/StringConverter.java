package org.b3log.latke.servlet.filter.converter;

import java.util.HashMap;
import java.util.Map;

public class StringConverter {

	private static Map<Class<?>, IStringConvert<?>> map = new HashMap<Class<?>, IStringConvert<?>>();

	static {
		registerConverter(String.class, new StringToStringConvert());
		registerConverter(Integer.class, new StringToIntergerConvert());
	}

	public static void registerConverter(Class<?> clazz,
			IStringConvert<?> convert) {
		map.put(clazz, convert);
	}

	@SuppressWarnings("unchecked")
	public static <T> T converter(String value, Class<T> clazz) {
		return (T) map.get(clazz).doConvert(value);
	}

}
