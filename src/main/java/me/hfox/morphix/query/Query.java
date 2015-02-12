package me.hfox.morphix.query;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;

import java.util.List;

public interface Query<T> {

    public Query<T> where(String where);

    public FieldQuery<T> field(String name);

    public Query<T> order(String... fields);

    public Query<T> or(Query<T>... expressions);

    public Query<T> nor(Query<T>... expressions);

    public Query<T> and(Query<T>... expressions);

    public T update();

    public T get();

    public List<T> asList();

    public DBObject toQueryObject();

}
