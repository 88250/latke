package org.b3log.latke.servlet.converter;

public class StringToStringConvert implements IStringConvert<String> {

	@Override
	public String doConvert(String value) {
		if(value!=null){
			return value.trim();
		}
		return null;
	}

}
