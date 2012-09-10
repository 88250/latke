package org.b3log.latke.servlet.filter.converter;

public class StringToIntergerConvert implements IStringConvert<Integer> {

	@Override
	public Integer doConvert(String value) {
		if (value != null) {
			return Integer.valueOf(value);
		}
		return null;
	}

}
