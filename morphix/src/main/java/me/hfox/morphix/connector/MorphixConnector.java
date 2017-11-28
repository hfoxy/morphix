package me.hfox.morphix.connector;

import me.hfox.morphix.query.Query;

/**
 * Created by Harry on 27/11/2017.
 *
 * Connector interface used by the API to perform queries on the database
 */
public interface MorphixConnector {

    void connect();

    void disconnect();

    <T> Query<T> createQuery(Class<T> cls);

    <T> Query<T> createQuery(Class<T> cls, String collection);

}
