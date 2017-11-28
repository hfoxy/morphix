package me.hfox.morphix.postgre;

import me.hfox.morphix.connector.MorphixConnector;
import me.hfox.morphix.query.Query;

/**
 * Created by Harry on 28/11/2017.
 *
 * PostgreSQL implementation of the Morphix connector
 */
public class MorphixPostgreConnector implements MorphixConnector {

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
