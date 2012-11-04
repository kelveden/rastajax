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
package com.kelveden.rastajax.core;

import com.kelveden.rastajax.core.raw.ResourceClass;

import java.util.Set;

/**
 * Implemented by any class providing functionality to transform {@link com.kelveden.rastajax.core.raw.ResourceClass}es into a single final serializable
 * representation.
 *
 * @param <T>
 *         The type of the representation.
 */
public interface RepresentationBuilder<T> {

    /**
     * Transforms the specified {@link com.kelveden.rastajax.core.raw.ResourceClass}es into a final representation.
     *
     * @param resourceClasses
     *         The {@link com.kelveden.rastajax.core.raw.ResourceClass}s to transform.
     * @return The representation of the resources.
     */
    T buildRepresentationFor(Set<ResourceClass> resourceClasses);

    /**
     * Transforms the specified {@link com.kelveden.rastajax.core.raw.ResourceClass} into a final representation.
     *
     * @param resourceClass
     *         The {@link com.kelveden.rastajax.core.raw.ResourceClass} to transform.
     * @return The representation of the resource.
     */
    T buildRepresentationFor(ResourceClass resourceClass);
}
