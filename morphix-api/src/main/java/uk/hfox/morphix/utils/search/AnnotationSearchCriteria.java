package uk.hfox.morphix.utils.search;

import java.lang.annotation.Annotation;

/**
 * Searches for a specific annotation and only returns true when that annotation is present
 */
public class AnnotationSearchCriteria implements SearchCriteria {

    private final Class<? extends Annotation> annotation;

    public AnnotationSearchCriteria(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isSatisfied(Class<?> cls) {
        return cls.getDeclaredAnnotation(annotation) != null;
    }

}
