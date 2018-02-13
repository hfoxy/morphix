package uk.hfox.morphix.connector;

import uk.hfox.morphix.query.QueryBuilder;
import uk.hfox.morphix.transform.Transformer;
import uk.hfox.morphix.utils.Testing;

public class TestConnector implements MorphixConnector {

    @Override
    public void connect() {
        throw Testing.unsupported();
    }

    @Override
    public void disconnect() {
        throw Testing.unsupported();
    }

    @Override
    public boolean isConnected() {
        throw Testing.unsupported();
    }

    @Override
    public Transformer getTransformer() {
        throw Testing.unsupported();
    }

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls) {
        throw Testing.unsupported();
    }

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls, String collection) {
        throw Testing.unsupported();
    }

}
