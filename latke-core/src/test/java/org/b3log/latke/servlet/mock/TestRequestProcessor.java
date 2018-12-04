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

import org.b3log.latke.servlet.annotation.*;
import org.b3log.latke.servlet.renderer.AbstractResponseRenderer;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.latke.testhelper.MockConverSupport;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Request processor for testing.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 25, 2013
 */
@RequestProcessor
public class TestRequestProcessor {

    @RequestProcessing(value = "/string")
    public String getString() {
        return "string";
    }

    @RequestProcessing(value = "/string/{id}/{name}")
    public String getString1(final String id, final String name) {
        return id + name;
    }

    @RequestProcessing(value = "/string/{id}p{name}")
    public String getString11(final String id, final String name) {
        return id + name;
    }

    @RequestProcessing(value = "/string/{a}*{b}")
    public Integer getString11(final Integer a, final Integer b) {
        return a * b;
    }

    @RequestProcessing(value = "/string/{name}+{password}")
    public String getString2(@PathVariable("password") final String name, @PathVariable("name") final String password) {
        return name + password;
    }

    @RequestProcessing(value = "/date/{id}/{date}", convertClass = MockConverSupport.class)
    public String getString2(final Integer id, final Date date) {
        return "" + id + date.getTime();
    }

    @Before(adviceClass = TestBeforeAdvice.class)
    @After
    @RequestProcessing(value = "/dobefore/{id}")
    public Integer getString3(final Integer id) {
        return id;
    }

    @RequestProcessing(value = "/do/render1")
    public List<AbstractResponseRenderer> testRender1(final JsonRenderer renderer1, final JsonRenderer renderer2) {
        final List<AbstractResponseRenderer> ret = new ArrayList<AbstractResponseRenderer>();
        ret.add(renderer1);
        ret.add(renderer2);
        return ret;
    }

    @RequestProcessing(value = "/json/{name}")
    public String testJson(JSONObject jsonObject) {
        return jsonObject.optString("name");
    }

    @RequestProcessing("/void")
    public void testRetVoid() {
        System.out.println("testRetVoid");
    }

    @RequestProcessing("/*.html")
    public void testAntPathMatch() {
        System.out.println("testAntPathMatch");
    }
}
