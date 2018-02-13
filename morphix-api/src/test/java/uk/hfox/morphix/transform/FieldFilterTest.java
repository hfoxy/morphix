package uk.hfox.morphix.transform;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FieldFilterTest {

    @Test
    void excludes() {
        FieldFilter filter = new FieldFilter(true);
        assertTrue(filter.isExcludes());
        assertHex(true, filter);

        filter = new FieldFilter(true, "excluded", "fields");
        assertTrue(filter.isExcludes());
        assertHex(true, filter);
        assertFalse(filter.isAccepted("excluded"));
        assertFalse(filter.isAccepted("fields"));

        assertEquals("excluded", filter.getFields()[0]);
        assertEquals("fields", filter.getFields()[1]);
    }

    @Test
    void includes() {
        FieldFilter filter = new FieldFilter(false);
        assertFalse(filter.isExcludes());
        assertHex(false, filter);

        filter = new FieldFilter(false, "included", "fields");
        assertFalse(filter.isExcludes());
        assertHex(false, filter);
        assertTrue(filter.isAccepted("included"));
        assertTrue(filter.isAccepted("fields"));

        assertEquals("included", filter.getFields()[0]);
        assertEquals("fields", filter.getFields()[1]);
    }

    void assertHex(boolean accepted, FieldFilter filter) {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String hex = Integer.toHexString(random.nextInt(10000));
            if (accepted) {
                assertTrue(filter.isAccepted(hex));
            } else {
                assertFalse(filter.isAccepted(hex));
            }
        }
    }

}