/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.intercept.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Indicates that the annotated method will be invoked before a method invocation.
 * 
 * <p>
 * For example, 
 * 
 * <pre>
 * package org.b3log;
 * 
 * publlic class A {
 *     // invoking method
 *     public void oneMethod() {} 
 * 
 *     // intercept method
 *     &#64;BeforeMethod("oneMethod")
 *     public void b() {} 
 * }
 * 
 * 
 * package org.b3log;
 * 
 * public class B {
 *     // intercept method
 *     &#64;BeforeMethod("org.b3log.A#oneMethod")
 *     public void c() {}
 * }
 * </pre>
 * 
 * Then the method org.b3log.A#b and org.b3log.B#c will be invoked before org.b3log.A#oneMethod, and the invocation of methods A#b and B#c
 * are with uncertain order. In addition, method annotated with this annotation has no transitivity, means that a method (e.g. oneMethod) 
 * be invoked in application code maybe trigger it's before/after method (if them exists), others before/after methods (if them exists) of 
 * theses before/after methods of the method (oneMethod) will not be invoked.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.8, Sep 29, 2013
 * @see AfterMethod
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface BeforeMethod {

    /**
     * The name of invoking method.
     * 
     * @return value
     */
    String value();
}
