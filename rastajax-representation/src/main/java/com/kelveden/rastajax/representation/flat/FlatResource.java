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

import java.util.Collections;
import java.util.List;

/**
 * Represents the final "representable" version of a JAX-RS resource.
 */
public final class FlatResource {

    private final String uriTemplate;
    private final List<FlatResourceMethod> resourceMethods;

    FlatResource(final String path, final List<FlatResourceMethod> resourceMethods) {
        this.uriTemplate = path;
        this.resourceMethods = Collections.unmodifiableList(resourceMethods);
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public List<FlatResourceMethod> getResourceMethods() {
        return resourceMethods;
    }


}
