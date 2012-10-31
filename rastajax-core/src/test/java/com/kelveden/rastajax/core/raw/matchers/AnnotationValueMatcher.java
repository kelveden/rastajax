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


import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;

public class AnnotationValueMatcher extends org.hamcrest.TypeSafeMatcher<Annotation> {

    private static final Object NO_VALUE = "<<<NO VALUE>>>";
    private final org.hamcrest.Matcher<?> valueMatcher;

    public AnnotationValueMatcher(final org.hamcrest.Matcher<?> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    public static AnnotationValueMatcher annotationValueIs(final org.hamcrest.Matcher<?> matcher) {
        return new AnnotationValueMatcher(matcher);
    }

    public static AnnotationValueMatcher annotationValueIs(final Object value) {
        return new AnnotationValueMatcher(is(value));
    }

    @Override
    protected void describeMismatchSafely(Annotation item, Description mismatchDescription) {
        mismatchDescription.appendText("was " + annotationValueToString(getAnnotationValue(item)) + "");
    }

    @Override
    protected boolean matchesSafely(final Annotation item) {
        return valueMatcher.matches(getAnnotationValue(item));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("is annotation with value that ");

        valueMatcher.describeTo(description);
    }

    private Object getAnnotationValue(final Annotation annotation)     {
        try {
            final Method method = annotation.getClass().getMethod("value");

            return method.invoke(annotation);

        } catch (final NoSuchMethodException e) {
            return NO_VALUE;
        } catch (final InvocationTargetException e) {
            return NO_VALUE;
        } catch (final IllegalAccessException e) {
            return NO_VALUE;
        }
    }

    private String annotationValueToString(final Object value) {

        if (value.equals(NO_VALUE)) {
            return "annotation with no value";
        } else if (value.getClass().isArray()) {
            return "[" + StringUtils.join((String[]) value, ", ") + "]";
        } else {
            return "\"" + value.toString() + "\"";
        }
    }
}
