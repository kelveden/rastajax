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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlatResourceMethodParameterTest {

    private static final String DUMMY_NAME = "somename";
    private static final String DUMMY_TYPE = "sometype";

    @Test
    public void canInstantiate() {
        new FlatResourceMethodParameter(DUMMY_NAME, DUMMY_TYPE);
    }

    @Test
    public void nameIsLoaded() {

        final FlatResourceMethodParameter parameter = new FlatResourceMethodParameter("myname", DUMMY_TYPE);

        assertThat(parameter.getName(), is("myname"));
    }

    @Test
    public void typeIsLoaded() {

        final FlatResourceMethodParameter parameter = new FlatResourceMethodParameter(DUMMY_NAME, "mytype");

        assertThat(parameter.getType(), is("mytype"));
    }
}
