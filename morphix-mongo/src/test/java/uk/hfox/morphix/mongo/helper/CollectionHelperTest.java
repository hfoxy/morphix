package uk.hfox.morphix.mongo.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.annotations.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class CollectionHelperTest {

    @Test
    void generate() {
        Class<CollectionHelper> cls = CollectionHelper.class;
        CollectionHelper helper = new CollectionHelper(null);
        helper.setSnakeCase(false);
        helper.setLowerCase(false);

        assertFalse(helper.isSnakeCase());
        assertFalse(helper.isLowerCase());
        assertEquals("CollectionHelper", helper.generate(cls));

        helper.setLowerCase(true);
        assertTrue(helper.isLowerCase());
        assertEquals("collectionhelper", helper.generate(cls));

        helper.setSnakeCase(true);
        assertTrue(helper.isSnakeCase());
        assertEquals("collection_helper", helper.generate(cls));

        helper.setLowerCase(false);
        assertFalse(helper.isLowerCase());
        assertEquals("Collection_Helper", helper.generate(cls));
    }

    @Test
    void getCollection() {
        CollectionHelper helper = new CollectionHelper(null);

        assertEquals("custom_name", helper.getCollection(MyClass.class));
        assertEquals("automated_class", helper.getCollection(AutomatedClass.class));
        assertEquals("basic_class", helper.getCollection(BasicClass.class));
    }

    @Collection("custom_name")
    public static class MyClass {
    }

    @Collection
    public static class AutomatedClass {
    }

    public static class BasicClass {
    }

}