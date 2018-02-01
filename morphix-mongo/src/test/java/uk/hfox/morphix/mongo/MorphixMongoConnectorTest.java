package uk.hfox.morphix.mongo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MorphixMongoConnectorTest {

    private MorphixMongoConnector connector;

    @BeforeAll
    void connect() {
        this.connector = MongoConnector.builder().timeout(5000).build().build();
        this.connector.connect();
        assumeTrue(this.connector.isConnected(), "Database not provided");

        assertThrows(IllegalStateException.class, this.connector::connect);
    }

    @Test
    void isConnected() {
        assertTrue(this.connector.isConnected());
    }

    @AfterAll
    void disconnect() {
        this.connector.disconnect();
    }

    @Test
    void createQuery() {

    }

}