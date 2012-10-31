package com.kelveden.rastajax.core.raw;

/**
 * Unchecked exception thrown when an unexpected problem occurs loading in a raw JAX-RS resource class from the underlying class's reflected data.
 */
public class ResourceClassLoadingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message
     *      The error message.
     * @param cause
     *      The underlying cause.
     */
    public ResourceClassLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *      The error message.
     */
    public ResourceClassLoadingException(final String message) {
        super(message);
    }
}
