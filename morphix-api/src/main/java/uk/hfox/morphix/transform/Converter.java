package uk.hfox.morphix.transform;

/**
 * Converts a DB type into the correct type
 *
 * @param <T> The DB entry type
 */
public interface Converter<T> {

    /**
     * Pulls the object from the DB entry
     *
     * @param key   The key to pull
     * @param entry The DB entry to pull from
     *
     * @return The constructed object created from the entry
     */
    Object pull(String key, T entry);

    /**
     * Pushes the object to the DB entry
     *
     * @param key   The key to push to
     * @param entry The DB entry to push to
     * @param value The value to push
     */
    void push(String key, T entry, Object value);

}
