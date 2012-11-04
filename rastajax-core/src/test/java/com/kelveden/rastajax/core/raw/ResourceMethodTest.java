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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ResourceMethodTest {

    private static final String DUMMY_NAME = "somename";
    private static final String DUMMY_REQUEST_METHOD_DESIGNATOR = "GET";
    private static final List<ResourceClassMethodParameter> DUMMY_METHOD_PARAMETERS = new ArrayList<ResourceClassMethodParameter>();
    private static final List<String> DUMMY_PRODUCES = new ArrayList<String>();
    private static final List<String> DUMMY_CONSUMES = new ArrayList<String>();
    private static final Class<?> DUMMY_RETURN_TYPE = String.class;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);
    }

    @Test
    public void requestMethodDesignatorIsLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, "myrequestmethoddesignator", DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(resourceMethod.getRequestMethodDesignator(), is("myrequestmethoddesignator"));
    }

    @Test
    public void nameIsLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod("myname", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(resourceMethod.getName(), is("myname"));
    }

    @Test
    public void returnTypeIsLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, Integer.class);

        assertThat(resourceMethod.getReturnType().getName(), is("java.lang.Integer"));
    }

    @Test
    public void producesIsLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, Arrays.asList("produces1", "produces2"), DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(resourceMethod.getProduces(), contains("produces1", "produces2"));
    }

    @Test
    public void producesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        resourceMethod.getProduces().add("produces");
    }

    @Test
    public void consumesIsLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, Arrays.asList("consumes1", "consumes2"), DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(resourceMethod.getConsumes(), contains("consumes1", "consumes2"));
    }

    @Test
    public void consumesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        resourceMethod.getConsumes().add("consumes");
    }

    @Test
    public void methodParametersAreLoaded() {

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, Arrays.asList(dummyParameter(), dummyParameter()), DUMMY_RETURN_TYPE);

        assertThat(resourceMethod.getParameters(), hasSize(2));
    }

    @Test
    public void methodParametersIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final ResourceMethod resourceMethod = new ResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        resourceMethod.getParameters().add(dummyParameter());
    }

    private ResourceClassMethodParameter dummyParameter() {
        return new ResourceClassMethodParameter("somename", QueryParam.class, String.class);
    }
}
