package uk.hfox.morphix.mongo.query;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.query.raw.input.MongoInsertQuery;
import uk.hfox.morphix.mongo.query.raw.output.MongoFindQuery;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoQueryBuilderTest {

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

}