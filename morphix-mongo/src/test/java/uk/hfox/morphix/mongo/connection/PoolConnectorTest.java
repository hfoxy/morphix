package uk.hfox.morphix.mongo.connection;

import com.mongodb.ServerAddress;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PoolConnectorTest {

    @Test
    void getClient() {
        List<ServerAddress> hosts = new ArrayList<>();
        hosts.add(new ServerAddress("localhost", 27017));

        MongoConnector.Builder builder = MongoConnector.builder();
        builder.pool(hosts);
        builder.timeout(100);

        PoolConnector connector = new PoolConnector(builder);
        try {
            connector.getClient();
        } catch (Exception ex) {
            // ignore
        }
    }

}