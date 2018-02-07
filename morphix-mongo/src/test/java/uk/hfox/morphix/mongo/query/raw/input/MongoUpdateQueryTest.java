package uk.hfox.morphix.mongo.query.raw.input;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoUpdateQueryTest {

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
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("update_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        Document sample = sample(0);

        MongoInsertQuery insert = new MongoInsertQuery(collection, sample, null);
        insert.performQuery();

        assertEquals(1, collection.count());

        Bson filters = Filters.eq("updated", false);
        Bson doc = Updates.combine(Updates.set("updated", true));

        MongoUpdateQuery updateQuery = new MongoUpdateQuery(collection, filters, doc, false, null);
        updateQuery.performQuery();
        assertTrue(checkForNonUpdated(collection));

        new MongoDeleteQuery(collection, new BsonDocument(), false, null).performQuery();
        assertEquals(0, collection.count());
    }

    @Test
    void performSingleOptionInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("update_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        Document sample = sample(0);

        MongoInsertQuery query = new MongoInsertQuery(collection, sample, new InsertOneOptions());
        query.performQuery();

        assertEquals(1, collection.count());

        Bson filters = Filters.eq("updated", false);
        Bson doc = Updates.combine(Updates.set("updated", true));

        MongoUpdateQuery updateQuery = new MongoUpdateQuery(collection, filters, doc, false, new UpdateOptions());
        updateQuery.performQuery();
        assertTrue(checkForNonUpdated(collection));

        new MongoDeleteQuery(collection, new BsonDocument(), false, new DeleteOptions()).performQuery();
        assertEquals(0, collection.count());
    }

    @Test
    void performMultiInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("update_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            samples.add(sample(i));
        }

        MongoInsertQuery query = new MongoInsertQuery(collection, samples, null);
        query.performQuery();

        assertEquals(100, collection.count());

        Bson filters = Filters.eq("updated", false);
        Bson doc = Updates.combine(Updates.set("updated", true));

        MongoUpdateQuery updateQuery = new MongoUpdateQuery(collection, filters, doc, true, null);
        updateQuery.performQuery();
        assertTrue(checkForNonUpdated(collection));

        new MongoDeleteQuery(collection, new BsonDocument(), true, null).performQuery();
        assertEquals(0, collection.count());
    }

    @Test
    void performMultiOptionInsert() {
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("update_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            samples.add(sample(i));
        }

        MongoInsertQuery query = new MongoInsertQuery(collection, samples, new InsertManyOptions());
        query.performQuery();

        assertEquals(100, collection.count());

        Bson filters = Filters.eq("updated", false);
        Bson doc = Updates.combine(Updates.set("updated", true));

        MongoUpdateQuery updateQuery = new MongoUpdateQuery(collection, filters, doc, true, new UpdateOptions());
        updateQuery.performQuery();
        assertTrue(checkForNonUpdated(collection));

        new MongoDeleteQuery(collection, new BsonDocument(), true, new DeleteOptions()).performQuery();
        assertEquals(0, collection.count());
    }

    private Document sample(int id) {
        Document sample = new Document();
        sample.put("id", id);
        sample.put("updated", false);
        return sample;
    }

    private boolean checkForNonUpdated(MongoCollection<Document> collection) {
        FindIterable<Document> iterable = collection.find();
        for (Document document : iterable) {
            if (!document.getBoolean("updated")) {
                return false;
            }
        }

        return true;
    }

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

}