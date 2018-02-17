package uk.hfox.morphix.mongo;

import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.utils.Testing;

public class TestMorphixMongoConnector extends MorphixMongoConnector {

    public TestMorphixMongoConnector() {
        super(null);
    }

    @Override
    public void connect() {
        throw Testing.unsupported();
    }

    @Override
    public boolean isConnected() {
        throw Testing.unsupported();
    }

    @Override
    public void disconnect() {
        throw Testing.unsupported();
    }

}
