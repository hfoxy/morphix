package me.hfox.morphix.mongo;

import me.hfox.morphix.connector.MorphixConnector;
import me.hfox.morphix.query.Query;

/**
 * Created by Harry on 27/11/2017.
 *
 * MongoDB implementation of the Morphix connector
 */
public class MorphixMongoConnector implements MorphixConnector {

    @Override
    public void connect() {
        // TODO: connect to database
    }

    @Override
    public void disconnect() {
        // TODO: disconnect from database
    }

    @Override
    public <T> Query<T> createQuery(Class<T> cls) {
        // TODO: create query
        return null;
    }

    @Override
    public <T> Query<T> createQuery(Class<T> cls, String collection) {
        // TODO: create query
        return null;
    }

}
