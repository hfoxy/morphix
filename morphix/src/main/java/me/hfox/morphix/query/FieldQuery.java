package me.hfox.morphix.query;

/**
 * Created by Harry on 28/11/2017.
 *
 * Interface used to build connector-specific field queries
 */
public interface FieldQuery<T> {

    /**
     * Check if the field data matches the specified object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> equal(Object object);

    /**
     * Flip if this field is an inverse query
     * For example !(x >= 2)
     * @return The current query with the not flipped
     */
    FieldQuery<T> not();

    /**
     * Set if this field is an inverse query
     * @see me.hfox.morphix.query.FieldQuery#not()
     * @param not true if this should be an inverse query, otherwise false
     * @return The current query with the not set
     */
    FieldQuery<T> not(boolean not);

    /**
     * Check if the field data doesn't match the specified object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> notEqual(Object object);

    /**
     * Check if the field value is greater than the supplied object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> greaterThan(Object object);

    /**
     * Check if the field value is greater than or equal to the supplied object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> greaterThanOrEqual(Object object);

    /**
     * Check if the field value is less than the supplied object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> lessThan(Object object);

    /**
     * Check if the field value is less than or equal to the supplied object
     * @param object The object to check against
     * @return The parent query that the field query belongs to
     */
    Query<T> lessThanOrEqual(Object object);

}
