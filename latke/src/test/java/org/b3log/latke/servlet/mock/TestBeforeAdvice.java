package org.b3log.latke.servlet.mock;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;

@Named
@Singleton
public class TestBeforeAdvice extends BeforeRequestProcessAdvice{

	@Override
	public void doAdvice(HTTPRequestContext context, Map<String, Object> args) throws RequestProcessAdviceException {
		
		Integer id = (Integer)args.get("id");
		args.put("id", id+1);
	}
	
}