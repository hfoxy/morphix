package uk.hfox.morphix.utils;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test
    void constructor() {
        Class<Search> clazz = Search.class;

        assertTrue(Modifier.isFinal(clazz.getModifiers()));

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            assertFalse(constructor.isAccessible());

            assertThrows(InvocationTargetException.class, constructor::newInstance);
        }

        assertThrows(UnsupportedOperationException.class, Search::new);
    }

    @Test
    void getInheritedAnnotation() {
        assertThrows(IllegalArgumentException.class, () -> Search.getInheritedAnnotation(null, null));
        assertThrows(IllegalArgumentException.class, () -> Search.getInheritedAnnotation(SuperClass.class, null));
        assertThrows(IllegalArgumentException.class, () -> Search.getInheritedAnnotation(null, TestAnnotation.class));

        Class<TestAnnotation> cls = TestAnnotation.class;
        TestAnnotation anno;

        anno = Search.getInheritedAnnotation(BaseAnnotatedClass.class, cls);
        assertNotNull(anno);
        assertEquals(0, anno.value());

        anno = Search.getInheritedAnnotation(BaseEmptyClass.class, cls);
        assertNotNull(anno);
        assertEquals(1, anno.value());

        anno = Search.getInheritedAnnotation(SuperClass.class, cls);
        assertNotNull(anno);
        assertEquals(1, anno.value());

        anno = Search.getInheritedAnnotation(BaseSuperEmptyClass.class, cls);
        assertNull(anno);

        anno = Search.getInheritedAnnotation(SuperEmptyClass.class, cls);
        assertNull(anno);
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {

        int value();

    }

    @TestAnnotation(0)
    private static class BaseAnnotatedClass extends SuperClass {
    }

    private static class BaseEmptyClass extends SuperClass {
    }

    @TestAnnotation(1)
    private static class SuperClass {
    }

    private static class BaseSuperEmptyClass extends SuperEmptyClass {
    }

    private static class SuperEmptyClass {
    }

}