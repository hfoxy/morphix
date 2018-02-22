package uk.hfox.morphix.mongo.query;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.annotations.field.Id;
import uk.hfox.morphix.annotations.field.Reference;
import uk.hfox.morphix.annotations.field.Transient;
import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.entity.EntityHelper;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.query.raw.input.MongoInsertQuery;
import uk.hfox.morphix.mongo.query.raw.input.MongoUpdateQuery;
import uk.hfox.morphix.mongo.query.raw.output.MongoFindQuery;
import uk.hfox.morphix.transform.FieldFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoQueryBuilderTest {

    private static MongoQueryBuilderTest test;

    private MorphixMongoConnector connector;

    @BeforeAll
    void setUp() {
        MongoConnector connector = MongoConnector.builder().timeout(5000).database("morphix_test").build();
        this.connector = connector.build();
        this.connector.connect();
        assumeTrue(this.connector.isConnected(), "Database not provided");

        test = this;
    }

    @Test
    void getConnector() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("fake_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        assertEquals(this.connector, query.getConnector());
    }

    @Test
    void getQueryType() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("fake_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        assertEquals(Object.class, query.getQueryType());
    }

    @Test
    void deleteOne() {
        MongoCollection<Document> collection = populate("delete_one_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.where("type").matches(1);
        query.delete(true);
        assertEquals(99, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void deleteMany() {
        MongoCollection<Document> collection = populate("delete_many_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.where("type").matches(1);
        query.delete();
        assertEquals(75, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereLT() {
        MongoCollection<Document> collection = populate("lt_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.lessThan(1);

        assertThrows(IllegalStateException.class, () -> field.lessThan(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") < 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereLTE() {
        MongoCollection<Document> collection = populate("lte_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.lessThanOrEqual(1);

        assertThrows(IllegalStateException.class, () -> field.lessThanOrEqual(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") <= 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereGT() {
        MongoCollection<Document> collection = populate("gt_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.greaterThan(1);

        assertThrows(IllegalStateException.class, () -> field.greaterThan(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") > 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereGTE() {
        MongoCollection<Document> collection = populate("gte_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.greaterThanOrEqual(1);

        assertThrows(IllegalStateException.class, () -> field.greaterThanOrEqual(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") >= 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereNotMatches() {
        MongoCollection<Document> collection = populate("not_matches_query_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.not().matches(1);

        assertThrows(IllegalStateException.class, () -> field.matches(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") != 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereNotNotMatches() {
        MongoCollection<Document> collection = populate("not_matches_query_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.not().not().matches(1);

        assertThrows(IllegalStateException.class, () -> field.matches(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") == 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereMatches() {
        MongoCollection<Document> collection = populate("matches_query_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.matches(1);

        assertThrows(IllegalStateException.class, () -> field.matches(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") == 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void whereNotEqual() {
        MongoCollection<Document> collection = populate("matches_query_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        MongoFieldQueryBuilder<Object> field = query.where("type");
        field.notEqual(1);

        assertThrows(IllegalStateException.class, () -> field.notEqual(1));

        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            assertTrue(doc.getInteger("type") != 1);
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void sortCancel() {
        MongoCollection<Document> collection = populate("sort_cancel_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.sort().desc("uuid").cancel();
        assertNull(query.sort);

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void sortClear() {
        MongoCollection<Document> collection = populate("sort_cancel_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.sort().desc("uuid").done();

        assertEquals(1, query.sort().getElements().size());
        query.sort().clear();
        assertEquals(0, query.sort().getElements().size());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void sortDesc() {
        MongoCollection<Document> collection = populate("sort_desc_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.sort().desc("uuid").done();

        assertThrows(IllegalArgumentException.class, () -> query.sort().asc("uuid"));
        assertThrows(IllegalArgumentException.class, () -> query.sort().desc("uuid"));

        int uuid = -1;
        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            if (uuid != -1) {
                assertEquals(uuid - 1, (int) doc.getInteger("uuid"));
            }

            uuid = doc.getInteger("uuid");
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void sortAsc() {
        MongoCollection<Document> collection = populate("sort_asc_test");
        MongoQueryBuilder<Object> query = new MongoQueryBuilder<>(Object.class, collection, this.connector);
        query.sort().asc("uuid").done();

        assertThrows(IllegalArgumentException.class, () -> query.sort().asc("uuid"));
        assertThrows(IllegalArgumentException.class, () -> query.sort().desc("uuid"));

        int uuid = -1;
        MongoFindQuery find = query.find();
        FindIterable<Document> result = find.getOutput();
        for (Document doc : result) {
            if (uuid != -1) {
                assertEquals(uuid + 1, (int) doc.getInteger("uuid"));
            }

            uuid = doc.getInteger("uuid");
        }

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void results() {
        String name = this.connector.getHelperManager().getCollectionHelper().getCollection(Item.class);
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection(name);

        for (int k = 0; k < 5; k++) {
            int sum = 0;
            int runs = 100;
            long lowest = -1;
            long total = 0;

            long start = System.currentTimeMillis();
            for (int j = 0; j < runs; j++) {
                int records = 100;
                sum += records;

                collection.deleteMany(new BsonDocument());
                assertEquals(0, collection.count());

                Item[] items = new Item[records];
                for (int i = 0; i < items.length; i++) {
                    Item item = new Item(this.connector, i + 1, String.format("%05d", i + 1));
                    items[i] = item;
                    assertNull(item.objectId);
                }

                this.connector.getEntityManager().save(items);

                for (Item item : this.connector.createQuery(Item.class).result()) {
                    assertEquals(items[item.id - 1], item);
                    assertNotNull(item.objectId);
                }

                Document bson = new Document("$set", new Document("name", "updated"));
                new MongoUpdateQuery(collection, new Document(), bson, true, null).performQuery();

                this.connector.getEntityManager().update(new FieldFilter(false, "name"), items);
                for (Item item : items) {
                    assertEquals("updated", item.name);
                }

                long end = System.currentTimeMillis();
                long time = end - start;
                total += time;
                if (lowest == -1 || lowest > time) {
                    lowest = time;
                }

                StringBuilder builder = new StringBuilder();
                builder.append(records).append(" inserted (").append(String.format("%03d", j + 1)).append("): ");
                builder.append(time).append("ms");
                // System.out.println(builder.toString());

                start = System.currentTimeMillis();
            }

            double average = (double) total;
            average = average / runs;

            System.out.println("Records inserted: " + sum);
            System.out.println("Total time: " + total + "ms");
            System.out.println("Lowest time: " + lowest + "ms");
            System.out.println("Average time: " + String.format("%04f", average));
        }
    }

    @Test
    void references() {
        String name = this.connector.getHelperManager().getCollectionHelper().getCollection(Topic.class);
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection(name);
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        name = this.connector.getHelperManager().getCollectionHelper().getCollection(Index.class);
        collection = this.connector.getDatabase().getCollection(name);
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        Topic topic = new Topic("test");
        Index index = new Index(topic);
        topic.save();
        index.save();

        connector.getEntityManager().getCache().getIds().clear();
        connector.getEntityManager().getCache().getCache().clear();

        index = connector.createQuery(Index.class).result().first();
        assertEquals("test", index.topic.name);
    }

    private MongoCollection<Document> populate(String name) {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection(name);
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Document doc = new Document();
            doc.put("test", 1);
            doc.put("type", i % 4);
            doc.put("uuid", i);
            samples.add(doc);
        }

        new MongoInsertQuery(collection, samples, null).performQuery();

        assertEquals(100, collection.count());
        return collection;
    }

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

    @Entity
    public static class Item implements EntityHelper {

        @Id
        private Object objectId;

        @Transient
        private final MorphixMongoConnector connector;

        private int id;
        private String name;

        private Item() {
            // db only
            this.connector = null;
        }

        public Item(MorphixMongoConnector connector, int id, String name) {
            this.connector = connector;
            this.id = id;
            this.name = name;
        }

        @Override
        public MorphixMongoConnector getConnector() {
            return connector;
        }

    }

    @Entity
    private static class Topic implements EntityHelper {

        private String name;

        private Topic() {
            // db only
        }

        public Topic(String name) {
            this.name = name;
        }

        @Override
        public MorphixConnector getConnector() {
            return test.connector;
        }

    }

    @Entity
    private static class Index implements EntityHelper {

        @Reference
        private Topic topic;

        private Index() {
            // db only
        }

        public Index(Topic topic) {
            this.topic = topic;
        }

        @Override
        public MorphixConnector getConnector() {
            return test.connector;
        }

    }

}