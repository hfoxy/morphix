package uk.hfox.morphix.utils;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.List;

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

    @Test
    void getAllFields() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> Search.getAllFields(null));

        List<Field> fields = Search.getAllFields(BaseReflectiveSearch.class);
        assertEquals(1, fields.size());
        assertEquals(BaseReflectiveSearch.class.getDeclaredField("topField"), fields.get(0));

        fields = Search.getAllFields(InheritedReflectiveSearch.class);
        assertEquals(2, fields.size());
        assertTrue(fields.contains(BaseReflectiveSearch.class.getDeclaredField("topField")));
        assertTrue(fields.contains(InheritedReflectiveSearch.class.getDeclaredField("bottomField")));

        fields = Search.getAllFields(OverridenReflectiveSearch.class);
        assertEquals(2, fields.size());
        assertTrue(fields.contains(BaseReflectiveSearch.class.getDeclaredField("topField")));
        assertTrue(fields.contains(InheritedReflectiveSearch.class.getDeclaredField("bottomField")));
    }

    @Test
    void getAllMethods() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> Search.getAllMethods(null));

        List<Method> methods = Search.getAllMethods(BaseReflectiveSearch.class);
        assertEquals(1, methods.size());
        assertEquals(BaseReflectiveSearch.class.getDeclaredMethod("topMethod"), methods.get(0));

        methods = Search.getAllMethods(InheritedReflectiveSearch.class);
        assertEquals(2, methods.size());
        assertTrue(methods.contains(BaseReflectiveSearch.class.getDeclaredMethod("topMethod")));
        assertTrue(methods.contains(InheritedReflectiveSearch.class.getDeclaredMethod("bottomMethod")));

        methods = Search.getAllMethods(OverridenReflectiveSearch.class);
        assertEquals(3, methods.size());
        assertTrue(methods.contains(BaseReflectiveSearch.class.getDeclaredMethod("topMethod")));
        assertTrue(methods.contains(InheritedReflectiveSearch.class.getDeclaredMethod("bottomMethod")));
        assertTrue(methods.contains(OverridenReflectiveSearch.class.getDeclaredMethod("topMethod")));
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

    private static class BaseReflectiveSearch {

        private String topField;

        public String topMethod() {
            return "";
        }

    }

    private static class InheritedReflectiveSearch extends BaseReflectiveSearch {

        private String bottomField;

        public String bottomMethod() {
            return "";
        }

    }

    private static class OverridenReflectiveSearch extends InheritedReflectiveSearch {

        @Override
        public String topMethod() {
            return "overriden";
        }

    }

}