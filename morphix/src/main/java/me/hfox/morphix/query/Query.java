package me.hfox.morphix.query;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Harry on 28/11/2017.
 *
 * Interface used to build connector-specific queries
 */
public interface Query<T> extends Iterator<T> {

    Class<T> getQueryType();

    FieldQuery<T> where(String field);

    Query<T> order(String... fields);

    void delete();

    void delete(boolean justOne);

    T get();

    List<T> asList();

}
