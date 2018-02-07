package uk.hfox.morphix.mongo.query.raw.input;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoInsertQueryTest {

    private MorphixMongoConnector connector;

    @BeforeAll
    void setUp() {
        MongoConnector connector = MongoConnector.builder().timeout(5000).database("morphix_test").build();
        this.connector = connector.build();
        this.connector.connect();
        assumeTrue(this.connector.isConnected(), "Database not provided");
    }

    @Test
    void performSingleInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("insert_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        Document sample = new Document();
        sample.put("id", 0);

        MongoInsertQuery query = new MongoInsertQuery(collection, sample, null);
        query.performQuery();

        assertEquals(1, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void performSingleOptionInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("insert_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        Document sample = new Document();
        sample.put("id", 0);

        MongoInsertQuery query = new MongoInsertQuery(collection, sample, new InsertOneOptions());
        query.performQuery();

        assertEquals(1, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void performMultiInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("insert_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Document sample = new Document();
            sample.put("id", i);
            samples.add(sample);
        }

        MongoInsertQuery query = new MongoInsertQuery(collection, samples, null);
        query.performQuery();

        assertEquals(100, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @Test
    void performMultiOptionInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("insert_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Document sample = new Document();
            sample.put("id", i);
            samples.add(sample);
        }

        MongoInsertQuery query = new MongoInsertQuery(collection, samples, new InsertManyOptions());
        query.performQuery();

        assertEquals(100, collection.count());

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

}