package com.kelveden.rastajax.core;

import java.util.Set;

/**
 * Implemented by classes providing the ability to scan for REST resources within a JAX-RS Application.
 */
public interface RootResourceScanner {

    /**
     * Scan for potential JAX-RS resources.
     *
     * @return
     *      A {@link Set} of {@link Class}es that could potentially represent JAX-RS resources.
     */
    Set<Class<?>> scan();
}
