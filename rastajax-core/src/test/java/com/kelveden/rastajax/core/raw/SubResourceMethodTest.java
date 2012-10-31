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

public class SubResourceMethodTest {

    private static final String DUMMY_NAME = "somename";
    private static final String DUMMY_REQUEST_METHOD_DESIGNATOR = "GET";
    private static final String DUMMY_PATH = "somepath";
    private static final List<ResourceClassMethodParameter> DUMMY_METHOD_PARAMETERS = new ArrayList<ResourceClassMethodParameter>();
    private static final List<String> DUMMY_PRODUCES = new ArrayList<String>();
    private static final List<String> DUMMY_CONSUMES = new ArrayList<String>();
    private static final Class<?> DUMMY_RETURN_TYPE = String.class;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);
    }

    @Test
    public void pathIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, "mypath", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getUriTemplate(), is("mypath"));
    }

    @Test
    public void requestMethodDesignatorIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, "myrequestmethoddesignator", DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getRequestMethodDesignator(), is("myrequestmethoddesignator"));
    }

    @Test
    public void nameIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod("myname", DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getName(), is("myname"));
    }

    @Test
    public void returnTypeIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, Integer.class);

        assertThat(subResourceMethod.getReturnType().getName(), is("java.lang.Integer"));
    }

    @Test
    public void producesIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, Arrays.asList("produces1", "produces2"), DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getProduces(), contains("produces1", "produces2"));
    }

    @Test
    public void producesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        subResourceMethod.getProduces().add("produces");
    }

    @Test
    public void consumesIsLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, Arrays.asList("consumes1", "consumes2"), DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getConsumes(), contains("consumes1", "consumes2"));
    }

    @Test
    public void consumesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        subResourceMethod.getConsumes().add("consumes");
    }

    @Test
    public void methodParametersAreLoaded() {

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, Arrays.asList(dummyParameter(), dummyParameter()), DUMMY_RETURN_TYPE);

        assertThat(subResourceMethod.getParameters(), hasSize(2));
    }

    @Test
    public void methodParametersIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceMethod subResourceMethod = new SubResourceMethod(DUMMY_NAME, DUMMY_PATH, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_RETURN_TYPE);

        subResourceMethod.getParameters().add(dummyParameter());
    }

    private ResourceClassMethodParameter dummyParameter() {
        return new ResourceClassMethodParameter("somename", QueryParam.class, String.class);
    }
}
