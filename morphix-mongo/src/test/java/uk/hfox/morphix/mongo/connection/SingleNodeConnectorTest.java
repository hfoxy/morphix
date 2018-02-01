package uk.hfox.morphix.mongo.connection;

import com.mongodb.MongoClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleNodeConnectorTest {

    @Test
    void getClient() {
        MongoConnector.Builder builder = MongoConnector.builder();
        builder.address("localhost", 27017);
        builder.timeout(100);

        SingleNodeConnector connector = new SingleNodeConnector(builder);
        try {
            connector.getClient();
        } catch (Exception ex) {
            // ignore
        }
    }

}