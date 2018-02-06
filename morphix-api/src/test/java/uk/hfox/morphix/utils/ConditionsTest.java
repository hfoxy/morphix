package uk.hfox.morphix.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
    void unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> {
            throw Conditions.unimplemented();
        });
    }

    @Test
    void notNull() {
        assertThrows(IllegalArgumentException.class, () -> Conditions.notNull(null));
        assertThrows(IllegalArgumentException.class, () -> Conditions.notNull(null, "arg"));

        assertTrue(Conditions.notNull("string"));
        assertTrue(Conditions.notNull("string", "mystring"));
    }

    @Test
    void notEmptyOrNullFilled() {
        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(null));
        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(null, "arg"));

        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(new ArrayList()));
        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(new ArrayList(), "arg"));

        List<Object> nullList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nullList.add(null);
        }

        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(nullList));
        assertThrows(IllegalArgumentException.class, () -> Conditions.notEmptyOrNullFilled(nullList, "arg"));

        List<Object> filledList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            filledList.add(new Object());
        }

        assertTrue(Conditions.notEmptyOrNullFilled(filledList));
        assertTrue(Conditions.notEmptyOrNullFilled(filledList, "mystring"));
    }

}