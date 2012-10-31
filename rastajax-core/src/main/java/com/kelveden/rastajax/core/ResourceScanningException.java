package com.kelveden.rastajax.core;

/**
 * Thrown when a problem occurs scanning in the resource classes.
 */
public class ResourceScanningException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param cause
     *      The underlying cause of the exception.
     */
    public ResourceScanningException(final Throwable cause) {
        super("There was a problem scanning in the resources.", cause);
    }
}
