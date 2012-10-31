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
 * Represents a JAX-RS sub-resource method.
 */
public final class SubResourceMethod extends ResourceClassMethod {

    private final String uriTemplate;
    private final String requestMethodDesignator;
    private final Class<?> returnType;

    public SubResourceMethod(final String name, final String uriTemplate, final String requestMethodDesignator, final List<String> consumes, final List<String> produces, final List<ResourceClassMethodParameter> parameters, final Class<?> returnType) {
        super(name, consumes, produces, parameters);

        this.uriTemplate = uriTemplate;
        this.requestMethodDesignator = requestMethodDesignator;
        this.returnType = returnType;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public String getRequestMethodDesignator() {
        return requestMethodDesignator;
    }

    public Class<?> getReturnType() {
        return returnType;
    }
}
