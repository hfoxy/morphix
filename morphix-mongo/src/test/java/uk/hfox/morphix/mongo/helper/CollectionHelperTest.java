package uk.hfox.morphix.mongo.helper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class CollectionHelperTest {

    private MorphixMongoConnector connector;

    @BeforeAll
    void setUp() {
        MongoConnector connector = MongoConnector.builder().timeout(5000).database("morphix_test").build();
        this.connector = connector.build();
        this.connector.connect();
        assumeTrue(this.connector.isConnected(), "Database not provided");
    }

    @Test
    void generate() {
        Class<CollectionHelper> cls = CollectionHelper.class;
        CollectionHelper helper = new CollectionHelper(this.connector);
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

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

}