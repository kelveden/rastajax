/**
 * Copyright 2012 Alistair Dutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kelveden.rastajax.core.raw;

import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ResourceClassMethodParameterTest {

    private static final String DUMMY_NAME = "somename";
    private static final Class<?> DUMMY_TYPE = String.class;
    private static final Class<? extends Annotation> DUMMY_PARAMETER_TYPE = QueryParam.class;

    @Test
    public void canInstantiate() {
        new ResourceClassMethodParameter(DUMMY_NAME, DUMMY_PARAMETER_TYPE, DUMMY_TYPE);
    }

    @Test
    public void nameIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter("myname", DUMMY_PARAMETER_TYPE, DUMMY_TYPE);

        assertThat(parameter.getName(), is("myname"));
    }

    @Test
    public void typeIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter(DUMMY_NAME, DUMMY_PARAMETER_TYPE, Integer.class);

        assertThat(parameter.getType().getName(), is("java.lang.Integer"));
    }

    @Test
    public void parameterTypeIsLoaded() {

        final ResourceClassMethodParameter parameter = new ResourceClassMethodParameter(DUMMY_NAME, FormParam.class, DUMMY_TYPE);

        assertThat(parameter.getJaxRsAnnotationType().getName(), is("javax.ws.rs.FormParam"));
    }
}
