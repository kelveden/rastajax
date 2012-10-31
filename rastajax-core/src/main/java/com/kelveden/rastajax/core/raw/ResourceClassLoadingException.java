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
