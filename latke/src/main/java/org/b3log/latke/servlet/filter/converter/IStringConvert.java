package org.b3log.latke.servlet.filter.converter;

public interface IStringConvert<T> {
	
	T doConvert(String value);

}
