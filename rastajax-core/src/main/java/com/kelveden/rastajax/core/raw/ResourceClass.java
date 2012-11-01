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
 * Represents the raw data from a JAX-RS resource.
 */
public final class ResourceClass {

    private final Class<?> rawClass;
    private final String uriTemplate;
    private final List<ResourceClassMethod> methods;
    private final List<String> consumes;
    private final List<String> produces;

    public ResourceClass(final Class<?> rawClass, final String uriTemplate, final List<ResourceClassMethod> methods, final List<String> consumes, final List<String> produces) {
        this.rawClass = rawClass;
        this.uriTemplate = uriTemplate;

        for (ResourceClassMethod method : methods) {
            method.setResourceClass(this);
        }

        this.methods = Collections.unmodifiableList(methods);
        this.consumes = Collections.unmodifiableList(consumes);
        this.produces = Collections.unmodifiableList(produces);
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public List<ResourceClassMethod> getMethods() {
        return methods;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public boolean isRootResource() {
        return (uriTemplate != null) && (methods.size() > 0) ;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }
}
