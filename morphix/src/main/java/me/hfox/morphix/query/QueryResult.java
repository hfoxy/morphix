package me.hfox.morphix.query;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Harry on 28/11/2017.
 *
 * An iterable set of results returned by a query object
 */
public interface QueryResult<T> extends Iterator<T> {

    /**
     * Alias of first();
     * @see me.hfox.morphix.query.QueryResult#first();
     * @return The first result returned by the query
     */
    T one();

    /**
     * Get the first result from the query
     * @return The first result returned by the query
     */
    T first();

    /**
     * Get the last result from the query
     * @return The last result returned by the query
     */
    T last();

    /**
     * Get the results given by the query as a List
     * @return An unmodifiable list of results
     */
    List<T> all();

}
