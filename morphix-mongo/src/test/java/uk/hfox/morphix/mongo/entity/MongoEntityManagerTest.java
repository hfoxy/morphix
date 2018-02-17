package uk.hfox.morphix.mongo.entity;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.mongo.TestMorphixMongoConnector;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoEntityManagerTest {

    @Test
    void getConnector() {
        TestMorphixMongoConnector connector = new TestMorphixMongoConnector();
        MongoEntityManager manager = new MongoEntityManager(connector);
        assertNotNull(manager.getConnector());
    }

}