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

import java.util.List;

/**
 * Represents a JAX-RS sub-resource locator.
 */
public class SubResourceLocator extends ResourceClassMethod {

    private final String uriTemplate;
    private final ResourceClass subResource;

    public SubResourceLocator(final String name, final String uriTemplate, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters, final ResourceClass subResource) {
        super(name, consumes, produces, parameters);

        this.uriTemplate = uriTemplate;
        this.subResource = subResource;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public ResourceClass getSubResource() {
        return subResource;
    }
}
