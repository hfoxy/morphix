package uk.hfox.morphix.mapper.lifecycle;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.lifecycle.field.CreatedAt;
import uk.hfox.morphix.exception.connection.InvalidConfigurationException;

import java.lang.reflect.Field;
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
        assertThrows(InvalidConfigurationException.class, () -> CREATED_AT.isSupported(testClass.getDeclaredField("invalidStatic")));
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
    void isMethodSupported() {

    }

    @Test
    void populate() {

    }

    public static class LifecycleTestClass {

        @CreatedAt
        private static String invalidStatic;

        private String invalidNotAnnotated;

        @CreatedAt
        private String invalidType;

        @CreatedAt
        private LocalDateTime createdAt;

        public static void invalidStatic() {
        }

    }

    public static class CreatedAtTest {

        @CreatedAt
        private LocalDateTime createdAt;

    }

    public static class CreatedAtInheritedTest extends CreatedAtTest {
    }

}