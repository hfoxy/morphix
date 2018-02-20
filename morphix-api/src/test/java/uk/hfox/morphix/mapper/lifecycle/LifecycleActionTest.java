package uk.hfox.morphix.mapper.lifecycle;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.field.Transient;
import uk.hfox.morphix.annotations.lifecycle.field.AccessedAt;
import uk.hfox.morphix.annotations.lifecycle.field.CreatedAt;
import uk.hfox.morphix.annotations.lifecycle.field.UpdatedAt;
import uk.hfox.morphix.annotations.lifecycle.method.*;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static uk.hfox.morphix.mapper.lifecycle.LifecycleAction.BEFORE_CREATE;
import static uk.hfox.morphix.mapper.lifecycle.LifecycleAction.CREATED_AT;

class LifecycleActionTest {

    @Test
    void isFieldSupported() throws Exception {
        Class<LifecycleTestClass> testClass = LifecycleTestClass.class;
        assertFalse(BEFORE_CREATE.isSupported(testClass.getDeclaredField("invalidStatic")));
        assertFalse(CREATED_AT.isSupported(testClass.getDeclaredField("invalidNotAnnotated")));
        assertThrows(MorphixEntityException.class, () -> CREATED_AT.isSupported(testClass.getDeclaredField("invalidStatic")));
        assertThrows(MorphixEntityException.class, () -> CREATED_AT.isSupported(testClass.getDeclaredField("invalidTransient")));
        assertFalse(CREATED_AT.isSupported(testClass.getDeclaredField("invalidType")));
        assertTrue(CREATED_AT.isSupported(testClass.getDeclaredField("createdAt")));

        Map<LifecycleAction, List<Field>> populate = new HashMap<>();
        LifecycleAction.populateFields(CreatedAtTest.class, populate);

        List<Field> fields = populate.get(CREATED_AT);
        assertEquals(1, fields.size());
        assertEquals(CreatedAtTest.class.getDeclaredField("createdAt"), fields.get(0));

        populate = new HashMap<>();
        LifecycleAction.populateFields(CreatedAtInheritedTest.class, populate);

        fields = populate.get(CREATED_AT);
        assertEquals(1, fields.size());
        assertEquals(CreatedAtTest.class.getDeclaredField("createdAt"), fields.get(0));
    }

    @Test
    void isMethodSupported() throws Exception {
        Class<LifecycleTestClass> testClass = LifecycleTestClass.class;
        assertFalse(CREATED_AT.isSupported(testClass.getDeclaredMethod("invalidStatic")));
        assertFalse(BEFORE_CREATE.isSupported(testClass.getDeclaredMethod("invalidNotAnnotated")));
        assertThrows(MorphixEntityException.class, () -> BEFORE_CREATE.isSupported(testClass.getDeclaredMethod("invalidStatic")));
        assertTrue(BEFORE_CREATE.isSupported(testClass.getDeclaredMethod("beforeCreate")));
        assertThrows(MorphixEntityException.class, () -> BEFORE_CREATE.isSupported(testClass.getDeclaredMethod("invalidHasArguments", String.class)));

        Map<LifecycleAction, List<Method>> populate = new HashMap<>();
        LifecycleAction.populateMethods(CreatedAtTest.class, populate);

        List<Method> methods = populate.get(BEFORE_CREATE);
        assertEquals(1, methods.size());
        assertEquals(CreatedAtTest.class.getDeclaredMethod("beforeCreate"), methods.get(0));

        populate = new HashMap<>();
        LifecycleAction.populateMethods(CreatedAtInheritedTest.class, populate);

        methods = populate.get(BEFORE_CREATE);
        assertEquals(1, methods.size());
        assertEquals(CreatedAtTest.class.getDeclaredMethod("beforeCreate"), methods.get(0));
    }

    @Test
    void populateFields() {
        Map<LifecycleAction, List<Field>> populate = new HashMap<>();
        LifecycleAction.populateFields(SampleTest.class, populate);

        for (LifecycleAction action : LifecycleAction.values()) {
            if (action.isField()) {
                assertEquals(1, populate.get(action).size(), action.name() + " does not have the correct number of fields");
            }
        }
    }

    @Test
    void populateMethods() {
        Map<LifecycleAction, List<Method>> populate = new HashMap<>();
        LifecycleAction.populateMethods(SampleTest.class, populate);

        for (LifecycleAction action : LifecycleAction.values()) {
            if (!action.isField()) {
                assertEquals(1, populate.get(action).size(), action.name() + " does not have the correct number of methods");
            }
        }
    }

    public static class LifecycleTestClass {

        @CreatedAt
        @Transient
        private static String invalidTransient;

        @CreatedAt
        private static String invalidStatic;

        private String invalidNotAnnotated;

        @CreatedAt
        private String invalidType;

        @CreatedAt
        private LocalDateTime createdAt;

        @BeforeCreate
        public static void invalidStatic() {
        }

        public void invalidNotAnnotated() {
        }

        @BeforeCreate
        public void invalidHasArguments(String arg) {
        }

        @BeforeCreate
        public void beforeCreate() {
        }

    }

    public static class CreatedAtTest {

        @CreatedAt
        private LocalDateTime createdAt;

        @BeforeCreate
        public void beforeCreate() {
        }

    }

    public static class CreatedAtInheritedTest extends CreatedAtTest {
    }

    public static class SampleTest {

        @CreatedAt
        private LocalDateTime createdAt;

        @AccessedAt
        private LocalDateTime accessedAt;

        @UpdatedAt
        private LocalDateTime updatedAt;

        @BeforeCreate
        public void beforeCreate() {
        }

        @AfterCreate
        public void afterCreate() {
        }

        public void beforeAccess() {
        }

        @AfterAccess
        public void afterAccess() {
        }

        @BeforeUpdate
        public void beforeUpdate() {
        }

        @AfterUpdate
        public void afterUpdate() {
        }

        @BeforeInTransform
        public void beforeInTransform() {
        }

        @AfterInTransform
        public void afterInTransform() {
        }

        @BeforeOutTransform
        public void beforeOutTransform() {
        }

        @AfterOutTransform
        public void afterOutTransform() {
        }

    }

}