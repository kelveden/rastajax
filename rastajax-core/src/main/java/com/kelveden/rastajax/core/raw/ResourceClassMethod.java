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

import java.util.Collections;
import java.util.List;

/**
 * Represents a <a href="http://jsr311.java.net/">JAX-RS</a>  resource method, sub-resource method or sub-resource locator on a {@link ResourceClass}.
 */
public abstract class ResourceClassMethod {

    private final String name;
    private final List<String> consumes;
    private final List<String> produces;
    private final List<Parameter> parameters;
    private ResourceClass resourceClass;

    protected ResourceClassMethod(final String name, final List<String> consumes, final List<String> produces, final List<Parameter> parameters) {
        this.name = name;
        this.consumes = Collections.unmodifiableList(consumes);
        this.produces = Collections.unmodifiableList(produces);
        this.parameters = Collections.unmodifiableList(parameters);
    }

    public String getName() {
        return name;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public ResourceClass getResourceClass() {
        return resourceClass;
    }

    void setResourceClass(final ResourceClass resourceClass) {
        this.resourceClass = resourceClass;
    }
}
