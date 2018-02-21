package uk.hfox.morphix.mongo.entity;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.transform.FieldFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoEntityManagerTest {

    private MorphixMongoConnector connector;

    @BeforeAll
    void setUp() {
        MongoConnector connector = MongoConnector.builder().timeout(5000).database("morphix_test").build();
        this.connector = connector.build();
        this.connector.connect();
        assumeTrue(this.connector.isConnected(), "Database not provided");
    }

    @Test
    void getConnector() {
        assertNotNull(this.connector.getEntityManager().getConnector());
        assertEquals(this.connector, this.connector.getEntityManager().getConnector());
    }

    @Test
    void update() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            runTest(count);
            count *= 10;
        }
    }

    private void runTest(int count) {
        System.out.println("Running test on " + count + " items");
        String name = this.connector.getHelperManager().getCollectionHelper().getCollection(TestItem.class);
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection(name);
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        TestItem[] items = new TestItem[count];
        for (int i = 0; i < items.length; i++) {
            items[i] = new TestItem(i, "test");
        }

        this.connector.getEntityManager().save(items);

        for (TestItem item : items) {
            item.index++;
            item.msg = "updated";
        }

        this.connector.getEntityManager().update(items);

        for (int i = 0; i < items.length; i++) {
            TestItem item = items[i];
            assertEquals(i, item.index);
            assertEquals("test", item.msg);

            item.index++;
            item.msg = "updated";
        }

        this.connector.getEntityManager().save(new FieldFilter(false, "index"), items);
        this.connector.getEntityManager().update(items);

        for (int i = 0; i < items.length; i++) {
            TestItem item = items[i];
            assertEquals(i + 1, item.index);
            assertEquals("test", item.msg);

            item.index += 10;
            item.msg = "not updated";
        }

        this.connector.getEntityManager().update(new FieldFilter(false, "index"), items);

        for (int i = 0; i < items.length; i++) {
            TestItem item = items[i];
            assertEquals(i + 1, item.index);
            assertEquals("not updated", item.msg);
        }
    }

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

    @Entity
    private static class TestItem {

        private int index;
        private String msg;

        public TestItem(int index, String msg) {
            this.index = index;
            this.msg = msg;
        }

    }

}