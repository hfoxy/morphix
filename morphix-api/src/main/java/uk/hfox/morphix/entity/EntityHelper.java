package uk.hfox.morphix.entity;

import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.transform.FieldFilter;

import java.util.Map;

public interface EntityHelper {

    MorphixConnector getConnector();

    /**
     * Updates this entity using the supplied map
     * Fields within this map are pushed to both this object and the database
     *
     * @param fields The map of fields to update
     */
    default void update(Map<String, Object> fields) {
        // TODO: update
    }

    /**
     * Updates fields within the entity which are accepted by the supplied filter.
     * If the update parameter is marked as true, fields within the entity will be updated after the update operation
     *
     * @param filter The filter to check against
     * @param update true if the entity should be updated after the operation completes, otherwise false
     */
    default void update(FieldFilter filter, boolean update) {
        // TODO: update
    }

    /**
     * Alias of {@link EntityHelper#update(FieldFilter, boolean)}
     *
     * @param filter The filter to check against
     */
    default void update(FieldFilter filter) {
        // TODO: update
    }

    /**
     * Saves all fields in the entity
     */
    default void save() {
        // TODO: save
    }

}
