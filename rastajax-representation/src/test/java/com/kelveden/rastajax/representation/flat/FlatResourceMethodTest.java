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
package com.kelveden.rastajax.representation.flat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlatResourceMethodTest {

    private final String DUMMY_NAME = "somename";
    private final String DUMMY_REQUEST_METHOD_DESIGNATOR = "GET";
    private final Map<String, List<FlatResourceMethodParameter>> DUMMY_PARAMETERS = new HashMap<String, List<FlatResourceMethodParameter>>();
    private final List<String> DUMMY_CONSUMES = new ArrayList<String>();
    private final List<String> DUMMY_PRODUCES = new ArrayList<String>();
    private final String DUMMY_RESOURCE_CLASS = "SomeClass";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);
    }

    @Test
    public void nameIsLoaded() {

        final FlatResourceMethod method = new FlatResourceMethod("myname", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        assertThat(method.getName(), is("myname"));
    }

    @Test
    public void requestMethodDesignatorIsLoaded() {

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, "myrequestmethoddesignator", DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        assertThat(method.getRequestMethodDesignator(), is("myrequestmethoddesignator"));
    }

    @Test
    public void parametersIsLoaded() {

        final List<FlatResourceMethodParameter> parameters = Arrays.asList(dummyParameter(), dummyParameter());
        final Map<String, List<FlatResourceMethodParameter>> parametersByType = new HashMap<String, List<FlatResourceMethodParameter>>();
        parametersByType.put("myparametertype", parameters);

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, parametersByType, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        assertThat(method.getParameters().get("myparametertype"), hasSize(2));
    }

    @Test
    public void producesIsLoaded() {

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, Arrays.asList("p1", "p2"), DUMMY_RESOURCE_CLASS);

        assertThat(method.getProduces(), contains("p1", "p2"));
    }

    @Test
    public void consumesIsLoaded() {

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, Arrays.asList("c1", "c2"), DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        assertThat(method.getConsumes(), contains("c1", "c2"));
    }

    @Test
    public void parametersIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        method.getParameters().clear();
    }

    @Test
    public void producesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        method.getProduces().clear();
    }

    @Test
    public void consumesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final FlatResourceMethod method = new FlatResourceMethod(DUMMY_NAME, DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_RESOURCE_CLASS);

        method.getConsumes().clear();
    }

    @Test
    public void resourceClassIsLoaded() {

        final FlatResourceMethod method = new FlatResourceMethod("myname", DUMMY_REQUEST_METHOD_DESIGNATOR, DUMMY_PARAMETERS, DUMMY_CONSUMES, DUMMY_PRODUCES, "MyClass");

        assertThat(method.getResourceClass(), is("MyClass"));
    }

    private FlatResourceMethodParameter dummyParameter() {
        return new FlatResourceMethodParameter("somename", "sometype");
    }
}
