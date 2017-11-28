package me.hfox.morphix.connector;

import me.hfox.morphix.query.Query;

/**
 * Created by Harry on 27/11/2017.
 *
 * Connector interface used by the API to perform queries on the database
 */
public interface MorphixConnector {

    /**
     * Connect to the database
     */
    void connect();

    /**
     * Disconnect from the database
     */
    void disconnect();

    /**
     * Query the database using the specified class as a collection and result reference
     * @param cls The expected resulting class, also used to find the collection
     * @param <T> The expected resulting type
     * @return A query based on the given class
     */
    <T> Query<T> createQuery(Class<T> cls);

    /**
     * Query the given collection with the specified class as an expected result
     * @param cls The expected resulting class
     * @param collection The collection to query
     * @param <T> The expected resulting type
     * @return A query based on the given class
     */
    <T> Query<T> createQuery(Class<T> cls, String collection);

}
