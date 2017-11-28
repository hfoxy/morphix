package me.hfox.morphix.query;

/**
 * Created by Harry on 28/11/2017.
 *
 * Interface used to build connector-specific queries
 */
public interface Query<T> {

    /**
     * Gets the class output expected by this query
     * @return The generic class
     */
    Class<T> getQueryType();

    /**
     * Create a field query for the specified field
     * @param field The field to check
     * @return The FieldQuery representing the specified field
     */
    FieldQuery<T> where(String field);

    /*
     * Method not added until I can find a standard way between Mongo/MySQL to perform this
     * TODO: Design standard sorting method
     */
    // Query<T> order(String... fields);

    /**
     * Delete the resulting object(s) from the database
     */
    void delete();

    /**
     * Delete the resulting object(s) from the database, with the option of deleting just 1
     * @param justOne true if only 1 object should be deleted, false otherwise
     */
    void delete(boolean justOne);

    /**
     * Get an iterable copy of the results given by the query
     * @return An iterable set of results
     */
    QueryResult<T> result();

}
