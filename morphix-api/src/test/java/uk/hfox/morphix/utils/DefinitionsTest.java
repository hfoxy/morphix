package uk.hfox.morphix.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionsTest {

    @Test
    void constructor() {
        Class<Definitions> clazz = Definitions.class;

        assertTrue(Modifier.isFinal(clazz.getModifiers()));

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            assertFalse(constructor.isAccessible());

            assertThrows(InvocationTargetException.class, constructor::newInstance);
        }

        assertThrows(UnsupportedOperationException.class, Definitions::new);
    }

    @Test
    void defaultCollection() {
        assertEquals(".", Definitions.DEFAULT_COLLECTION);
    }

}