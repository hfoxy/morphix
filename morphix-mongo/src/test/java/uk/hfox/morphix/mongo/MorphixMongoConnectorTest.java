package uk.hfox.morphix.mongo;

import com.mongodb.ServerAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.exception.connection.InvalidConfigurationException;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.connection.PoolConnector;
import uk.hfox.morphix.mongo.connection.SingleNodeConnector;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MorphixMongoConnectorTest {

    private MorphixMongoConnector singleNode;
    private MorphixMongoConnector pooled;

    @BeforeAll
    void connectFail() {
        assertThrows(InvalidConfigurationException.class, () -> MongoConnector.builder().timeout(5000).build());
    }

    @BeforeAll
    void connectSingle() {
        MongoConnector.Builder builder = MongoConnector.builder().timeout(5000).database("morphix_test");
        assertTrue(builder.isSingle());

        MongoConnector connector = builder.build();
        assertTrue(connector instanceof SingleNodeConnector);

        this.singleNode = connector.build();
        this.singleNode.connect();
        assumeTrue(this.singleNode.isConnected(), "Database not provided");

        assertThrows(IllegalStateException.class, this.singleNode::connect);
    }

    @BeforeAll
    void connectPool() {
        List<ServerAddress> hosts = new ArrayList<>();
        hosts.add(new ServerAddress("localhost", 27017));

        MongoConnector.Builder builder = MongoConnector.builder().pool(hosts).timeout(5000).database("morphix_test");
        assertFalse(builder.isSingle());

        MongoConnector connector = builder.build();
        assertTrue(connector instanceof PoolConnector);

        this.pooled = connector.build();
        this.pooled.connect();
        assumeTrue(this.pooled.isConnected(), "Database not provided");

        assertThrows(IllegalStateException.class, this.pooled::connect);
    }

    @Test
    void isConnected() {
        assertTrue(this.singleNode.isConnected());
        assertTrue(this.pooled.isConnected());
    }

    @AfterAll
    void disconnect() {
        this.singleNode.disconnect();
        this.pooled.disconnect();

        assertFalse(this.singleNode.isConnected());
        assertFalse(this.pooled.isConnected());
    }

    @Test
    void createQuery() {
        // some query
    }

}