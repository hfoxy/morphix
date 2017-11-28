package me.hfox.morphix.query;

/**
 * Created by Harry on 28/11/2017.
 *
 * Interface used to build connector-specific field queries
 */
public interface FieldQuery<T> {

    Query<T> equal(Object object);

    Query<T> not(Object object);

    Query<T> notEqual(Object object);

    Query<T> greaterThan(Object object);

    Query<T> greaterThanOrEqual(Object object);

    Query<T> lessThan(Object object);

    Query<T> lessThanOrEqual(Object object);

}
