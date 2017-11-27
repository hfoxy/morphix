package me.hfox.morphix.mongo.query;

public interface FieldQuery<T> {

    public Query<T> equal(Object object);

    public Query<T> not(Object object);

    public Query<T> notEqual(Object object);

    public Query<T> greaterThan(Object object);

    public Query<T> greaterThanOrEqual(Object object);

    public Query<T> lessThan(Object object);

    public Query<T> lessThanOrEqual(Object object);

    public Query<T> exists();

    public Query<T> doesNotExist();

    public Query<T> modulo(int mod, int result);

    public Query<T> hasAll(Object... objects);

    public Query<T> in(Object object);

    public Query<T> notIn(Object object);

    public Query<T> size(int size);

}
