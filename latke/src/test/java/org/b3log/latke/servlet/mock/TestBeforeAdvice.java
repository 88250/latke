/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.servlet.mock;

import java.util.Map;


import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
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