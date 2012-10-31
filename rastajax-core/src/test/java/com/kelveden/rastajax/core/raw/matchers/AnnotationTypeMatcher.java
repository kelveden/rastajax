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
