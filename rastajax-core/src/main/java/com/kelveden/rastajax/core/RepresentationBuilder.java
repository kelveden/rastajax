package com.kelveden.rastajax.core;

import com.kelveden.rastajax.core.raw.ResourceClass;

import java.util.Set;

/**
 * Implemented by any class providing functionality to transform {@link com.kelveden.rastajax.core.raw.ResourceClass}s into a single final serializable
 * representation.
 *
 * @param <T>
 *         The type of the representation.
 */
public interface RepresentationBuilder<T> {

    /**
     * Transforms the specified {@link com.kelveden.rastajax.core.raw.ResourceClass}es into a final representation.
     *
     * @param rawResources
     *         The {@link com.kelveden.rastajax.core.raw.ResourceClass}s to transform.
     * @return The representation of the resources.
     */
    T buildRepresentationFor(Set<ResourceClass> rawResources);

    /**
     * Transforms the specified {@link com.kelveden.rastajax.core.raw.ResourceClass} into a final representation.
     *
     * @param rawResource
     *         The {@link com.kelveden.rastajax.core.raw.ResourceClass} to transform.
     * @return The representation of the resource.
     */
    T buildRepresentationFor(ResourceClass rawResource);
}
