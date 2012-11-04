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

import java.util.Set;

/**
 * Implemented by classes providing the ability to scan for root resources within a REST Application.
 */
public interface RootResourceScanner {

    /**
     * Scan for potential root REST resources. It does not have to be 100% accurate - it is simply a way of narrowing down
     * the number of classes that the {@link ResourceClassLoader} has to process. The {@link ResourceClassLoader} will discard
     * any classes that turn out not to be root resource classes.
     *
     * @return
     *      A {@link Set} of {@link Class}es that could potentially represent root REST resources.
     */
    Set<Class<?>> scan();
}
