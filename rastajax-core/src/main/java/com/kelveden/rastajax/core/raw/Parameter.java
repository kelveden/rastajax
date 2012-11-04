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

import java.lang.annotation.Annotation;

/**
 * Represents one out of:
 *
 * <ul>
 * <li>a parameter from a <a href="http://jsr311.java.net/">JAX-RS</a>  resource method, sub-resource method or sub-resource locator.</li>
 * <li>a field on a resource class that is injected via JAX-RS.</li>
 * <li>a property on a resource class that is injected via JAX-RS.</li>
 * </ul>
 */
public final class Parameter {

    private final String name;
    private final Class<?> type;
    private final Class<? extends Annotation> jaxRsAnnotationType;

    /**
     * Constructor.
     *
     * @param name
     *      The name of the parameter - i.e. how it is referred to in the JAX-RS annotation.
     * @param jaxRsAnnotationType
     *      The type of the JAX-RS annotation used to identify this parameter.
     * @param type
     *      The Java type of the parameter.
     */
    public Parameter(final String name, final Class<? extends Annotation> jaxRsAnnotationType, final Class<?> type) {
        this.name = name;
        this.type = type;
        this.jaxRsAnnotationType = jaxRsAnnotationType;
    }

    public String getName() {
        return name;
    }

    public Class<?>  getType() {
        return type;
    }

    public Class<? extends Annotation> getJaxRsAnnotationType() {
        return jaxRsAnnotationType;
    }
}
