package uk.hfox.morphix.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ConditionsTest {

    @Test
    void constructor() {
        Class<Conditions> clazz = Conditions.class;

        assertTrue(Modifier.isFinal(Conditions.class.getModifiers()));

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            assertFalse(constructor.isAccessible());

            assertThrows(InvocationTargetException.class, constructor::newInstance);
        }

        assertThrows(UnsupportedOperationException.class, Conditions::new);
    }

    @Test
    void notNull() {
        assertThrows(IllegalArgumentException.class, () -> Conditions.notNull(null));
        assertThrows(IllegalArgumentException.class, () -> Conditions.notNull(null, "arg"));

        assertTrue(Conditions.notNull("string"));
        assertTrue(Conditions.notNull("string", "mystring"));
    }

}