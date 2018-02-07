package uk.hfox.morphix.mongo.query.raw.output;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MongoFindQueryTest {

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
        MongoCollection<Document> collection = this.connector.getDatabase().getCollection("find_query");
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        performFind(1, collection);
        performFind(5, collection);
        performFind(25, collection);
        performFind(100, collection);

        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());
    }

    void performFind(int entries, MongoCollection<Document> collection) {
        collection.deleteMany(new BsonDocument());
        assertEquals(0, collection.count());

        List<Document> samples = new ArrayList<>();
        for (int i = 0; i < entries; i++) {
            Document doc = new Document();
            doc.put("id", i);
            doc.put("find", "a");
            samples.add(doc);
        }

        MongoInsertQuery insert = new MongoInsertQuery(collection, samples, null);
        insert.performQuery();

        assertEquals(entries, collection.count());

        Document search = new Document();
        MongoFindQuery find = new MongoFindQuery(collection, search);
        find.performQuery();

        int count = 0;
        FindIterable<Document> iterable = find.getOutput();
        for (Document doc : iterable) {
            assertEquals("a", doc.getString("find"));
            count++;
        }

        assertEquals(count, entries);
    }

    @AfterAll
    void tearDown() {
        this.connector.disconnect();
    }

}