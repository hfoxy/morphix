package uk.hfox.morphix.connector;

import uk.hfox.morphix.query.QueryBuilder;

public class TestConnector implements MorphixConnector {

    @Override
    public void connect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls, String collection) {
        throw new UnsupportedOperationException();
    }

}
