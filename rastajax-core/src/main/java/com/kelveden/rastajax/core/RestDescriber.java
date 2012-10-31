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

import java.util.HashSet;
import java.util.Set;

/**
 * Scans for REST resources and loads them into a single serializable representation.
 */
public final class RestDescriber {

    private RestDescriber() {
    }

    /**
     * Loads REST resources representing a single REST application and builds them as a single serializable representation.
     *
     * @param rawResourceClasses
     *      The root resource classes to represent.
     * @param representationBuilder
     *      The {@link RepresentationBuilder} to use to build the representation.
     * @param <T>
     *      The type of the representation.
     * @return
     *      The representation.
     */
    public static <T> T describeApplication(final Iterable<Class<?>> rawResourceClasses, final RepresentationBuilder<?> representationBuilder) {

        final Set<ResourceClass> resourceClasses = loadResources(rawResourceClasses);

        return (T) representationBuilder.buildRepresentationFor(resourceClasses);
    }

    /**
     * Loads the REST resource(s) representing the specified resource class and builds them as a single serializable representation.
     *
     * @param rawResourceClass
     *      The resource class to represent.
     * @param representationBuilder
     *      The {@link RepresentationBuilder} to use to build the representation.
     * @param <T>
     *      The type of the representation.
     * @return
     *      The representation.
     */
    public static <T> T describeResource(final Class<?> rawResourceClass, final RepresentationBuilder<?> representationBuilder) {

        final ResourceClassLoader loader = new ResourceClassLoader();
        final ResourceClass resourceClass = loader.loadResourceClassFrom(rawResourceClass);

        return (T) representationBuilder.buildRepresentationFor(resourceClass);
    }

    private static Set<ResourceClass> loadResources(final Iterable<Class<?>> classes) {

        final Set<ResourceClass> results = new HashSet<ResourceClass>();

        final ResourceClassLoader loader = new ResourceClassLoader();

        for (Class<?> clazz : classes) {
            final ResourceClass resource = loader.loadResourceClassFrom(clazz);

            if (resource != null) {
                results.add(resource);
            }
        }

        return results;
    }
}
