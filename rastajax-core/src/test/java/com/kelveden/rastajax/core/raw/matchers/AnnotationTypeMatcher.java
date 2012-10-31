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
package com.kelveden.rastajax.core.raw.matchers;

import org.hamcrest.Description;

import java.lang.annotation.Annotation;

public class AnnotationTypeMatcher extends org.hamcrest.TypeSafeMatcher<Annotation> {

    private final String expectedAnnotationTypeName;

    public AnnotationTypeMatcher(final String annotationTypeName) {
        this.expectedAnnotationTypeName = annotationTypeName;
    }

    public static AnnotationTypeMatcher annotationTypeIs(final Class<?> expectedAnnotationType) {
        return new AnnotationTypeMatcher(expectedAnnotationType.getName());
    }

    @Override
    protected boolean matchesSafely(final Annotation item) {
        return item.annotationType().getName().equals(expectedAnnotationTypeName);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is annotation of type \"" + expectedAnnotationTypeName + "\"");
    }
}
