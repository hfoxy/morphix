package me.hfox.morphix.query;

import com.mongodb.DBObject;

import java.util.Iterator;
import java.util.List;

public interface Query<T> extends Iterator<T> {

    public Class<T> getQueryType();

    public Query<T> where(String where);

    public FieldQuery<T> field(String name);

    public Query<T> order(String... fields);

    public Query<T> or(Query<T>... expressions);

    public Query<T> nor(Query<T>... expressions);

    public Query<T> and(Query<T>... expressions);

    public void delete();

    public void delete(boolean justOne);

    public void update();

    public T get();

    public DBObject getDB();

    public List<T> asList();

    public List<DBObject> asDBList();

    public DBObject toQueryObject();

}
