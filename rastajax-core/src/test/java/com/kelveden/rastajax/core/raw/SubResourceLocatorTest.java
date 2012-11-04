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

public class SubResourceLocatorTest {

    private static final String DUMMY_NAME = "somename";
    private static final String DUMMY_PATH = "somepath";
    private static final List<Parameter> DUMMY_METHOD_PARAMETERS = new ArrayList<Parameter>();
    private static final List<String> DUMMY_PRODUCES = new ArrayList<String>();
    private static final List<String> DUMMY_CONSUMES = new ArrayList<String>();
    private static final Class<?> DUMMY_RAW_CLASS = String.class;
    private static final ResourceClass DUMMY_SUB_RESOURCE = new ResourceClass(DUMMY_RAW_CLASS, "/some/path",
            new ArrayList<ResourceClassMethod>(), DUMMY_PRODUCES, DUMMY_CONSUMES, new ArrayList<Parameter>());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canInstantiate() {
        new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_PRODUCES, DUMMY_CONSUMES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);
    }

    @Test
    public void pathIsLoaded() {

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, "mypath", DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        assertThat(subResourceLocator.getUriTemplate(), is("mypath"));
    }

    @Test
    public void nameIsLoaded() {

        final SubResourceLocator subResourceLocator = new SubResourceLocator("myname", DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        assertThat(subResourceLocator.getName(), is("myname"));
    }

    @Test
    public void subResourceIsLoaded() {

        final ResourceClass subResource = new ResourceClass(DUMMY_RAW_CLASS, "/my/path", new ArrayList<ResourceClassMethod>(), DUMMY_PRODUCES, DUMMY_CONSUMES, new ArrayList<Parameter>());
        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, subResource);

        assertThat(subResourceLocator.getSubResource(), is(sameInstance(subResource)));
    }

    @Test
    public void producesIsLoaded() {

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, Arrays.asList("produces1", "produces2"), DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        assertThat(subResourceLocator.getProduces(), contains("produces1", "produces2"));
    }

    @Test
    public void producesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        subResourceLocator.getProduces().add("produces");
    }

    @Test
    public void consumesIsLoaded() {

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, Arrays.asList("consumes1", "consumes2"), DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        assertThat(subResourceLocator.getConsumes(), contains("consumes1", "consumes2"));
    }

    @Test
    public void consumesIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        subResourceLocator.getConsumes().add("consumes");
    }

    @Test
    public void methodParametersAreLoaded() {

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, Arrays.asList(dummyParameter(), dummyParameter()), DUMMY_SUB_RESOURCE);

        assertThat(subResourceLocator.getParameters(), hasSize(2));
    }

    @Test
    public void methodParametersIsImmutable() {

        thrown.expect(UnsupportedOperationException.class);

        final SubResourceLocator subResourceLocator = new SubResourceLocator(DUMMY_NAME, DUMMY_PATH, DUMMY_CONSUMES, DUMMY_PRODUCES, DUMMY_METHOD_PARAMETERS, DUMMY_SUB_RESOURCE);

        subResourceLocator.getParameters().add(dummyParameter());
    }

    private Parameter dummyParameter() {
        return new Parameter("somename", QueryParam.class, String.class);
    }
}
