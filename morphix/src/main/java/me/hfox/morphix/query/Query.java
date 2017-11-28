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

    Query<T> where(String where);

    FieldQuery<T> field(String name);

    Query<T> order(String... fields);

    Query<T> or(Query<T>... expressions);

    Query<T> nor(Query<T>... expressions);

    Query<T> and(Query<T>... expressions);

    void delete();

    void delete(boolean justOne);

    void update();

    T get();

    List<T> asList();

}
