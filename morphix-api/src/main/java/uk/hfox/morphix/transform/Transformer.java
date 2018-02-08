package uk.hfox.morphix.transform;

/**
 * Used to transform an Object into a given type and vice versa
 *
 * @param <T> The DB type to transform from or into
 */
public interface Transformer<T> {

    /**
     * Transforms the given Object into a database object
     *
     * @param object The object to transform
     *
     * @return The database object output
     */
    T toDB(Object object);

    /**
     * Transforms the given Object into a database object
     *
     * @param object The object to transform
     *
     * @return The database object output
     */
    T toDB(Object object, String... fields);

    /**
     * Transforms the database object into an Object of undefined type
     *
     * @param db The database object
     *
     * @return The undefined Object output
     */
    Object fromDB(T db);

}
