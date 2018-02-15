package uk.hfox.morphix.mongo;

import com.mongodb.ServerAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.exception.connection.InvalidConfigurationException;
import uk.hfox.morphix.mongo.annotations.Collection;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.connection.PoolConnector;
import uk.hfox.morphix.mongo.connection.SingleNodeConnector;
import uk.hfox.morphix.query.QueryBuilder;

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
        assertNotNull(this.singleNode.getClient());
        assertNotNull(this.pooled.getClient());

        assertTrue(this.singleNode.isConnected());
        assertTrue(this.pooled.isConnected());
    }

    @AfterAll
    void disconnect() {
        this.singleNode.disconnect();
        this.pooled.disconnect();

        assertFalse(this.singleNode.isConnected());
        assertFalse(this.pooled.isConnected());

        assertNull(this.singleNode.getClient());
        assertNull(this.pooled.getClient());
    }

    @Test
    void createQuery() {
        QueryBuilder<TestEntity> query1 = this.singleNode.createQuery(TestEntity.class);
        assertNotNull(query1);
        assertEquals(this.singleNode, query1.getConnector());

        query1 = this.pooled.createQuery(TestEntity.class);
        assertNotNull(query1);
        assertEquals(this.pooled, query1.getConnector());

        QueryBuilder<TestCollectionEntity> query2 = this.singleNode.createQuery(TestCollectionEntity.class);
        assertNotNull(query2);
        assertEquals(this.singleNode, query2.getConnector());

        query2 = this.pooled.createQuery(TestCollectionEntity.class);
        assertNotNull(query2);
        assertEquals(this.pooled, query2.getConnector());
    }

    @Entity
    public static class TestEntity {

        private String string;

    }

    @Entity
    @Collection("test_collection")
    public static class TestCollectionEntity {

        private String string;

    }

}