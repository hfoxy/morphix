package me.hfox.morphix.query;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.exception.query.IllegalQueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryImpl<T> implements Query<T> {

    private Morphix morphix;
    private Class<T> cls;
    private String collection;

    private String where;
    private Map<String, List<FieldQueryImpl<T>>> fields;
    private List<Query<T>> orQueries;
    private List<Query<T>> norQueries;
    private List<Query<T>> andQueries;
    private String[] order;

    private DBCursor cursor;

    public QueryImpl(Morphix morphix, Class<T> cls) {
        this(morphix, cls, morphix.getEntityHelper().getCollectionName(cls));
    }

    public QueryImpl(Morphix morphix, Class<T> cls, String collection) {
        this.morphix = morphix;
        this.cls = cls;
        this.collection = collection;
        this.fields = new HashMap<>();
        this.orQueries = new ArrayList<>();
        this.norQueries = new ArrayList<>();
        this.andQueries = new ArrayList<>();
    }

    @Override
    public Query<T> where(String where) {
        this.where = where;
        return this;
    }

    @Override
    public FieldQuery<T> field(String name) {
        FieldQueryImpl<T> query = new FieldQueryImpl<>(this, morphix);

        List<FieldQueryImpl<T>> queries = fields.get(name);
        if (queries == null) {
            queries = new ArrayList<>();
            fields.put(name, queries);
        }

        queries.add(query);
        if (cursor != null) {
            cursor = null;
        }

        return query;
    }

    @Override
    public Query<T> order(String... fields) {
        this.order = fields;
        if (cursor != null) {
            cursor = null;
        }

        return this;
    }

    @Override
    public Query<T> or(Query<T>... expressions) {
        orQueries.addAll(Arrays.asList(expressions));

        return this;
    }

    @Override
    public Query<T> nor(Query<T>... expressions) {
        norQueries.addAll(Arrays.asList(expressions));

        return this;
    }

    @Override
    public Query<T> and(Query<T>... expressions) {
        andQueries.addAll(Arrays.asList(expressions));

        return this;
    }

    @Override
    public void delete() {
        delete(false);
    }

    @Override
    public void delete(boolean justOne) {
        if (justOne) {
            if (getDB() == null) {
                return;
            }

            morphix.getDatabase().getCollection(collection).remove(new BasicDBObject("_id", getDB().get("_id")));
            return;
        }

        morphix.getDatabase().getCollection(collection).remove(toQueryObject());
    }

    @Override
    public void update() {
        List<DBObject> list = asDBList();
        for (DBObject object : list) {
            Class<?> cls = morphix.getPolymorhpismHelper().generate(object);
            if (cls == null) {
                cls = this.cls;
            }

            Object cached = morphix.getCache(cls).getEntity(object);
            if (cached == null) {
                continue;
            }

            morphix.getMapper().update(cached, object);
        }
    }

    @Override
    public T get() {
        return create(getDB());
    }

    public DBObject getDB() {
        DBCursor cursor = cursor();

        DBObject result = null;
        if (cursor.hasNext()) {
            result = cursor.next();
        }

        return result;
    }

    @Override
    public List<T> asList() {
        List<DBObject> objects = asDBList();
        List<T> results = new ArrayList<>();
        for (DBObject object : objects) {
            results.add(create(object));
        }

        return results;
    }

    public List<DBObject> asDBList() {
        DBCursor cursor = cursor();

        List<DBObject> objects = new ArrayList<>();
        while (cursor.hasNext()) {
            objects.add(cursor.next());
        }

        return objects;
    }

    public DBCursor cursor() {
        DBCursor cursor = morphix.getDatabase().getCollection(collection).find(toQueryObject());
        if (order != null) {
            BasicDBObject sort = new BasicDBObject();
            for (String string : order) {
                boolean desc = false;
                if (string.startsWith("-")) {
                    desc = true;
                    string = string.substring(1);
                }

                sort.put(string, desc ? -1 : 1);
            }

            cursor.sort(sort);
        }

        return (this.cursor = cursor);
    }

    @Override
    public boolean hasNext() {
        if (cursor == null) {
            cursor();
        }

        return cursor.hasNext();
    }

    @Override
    public T next() {
        if (cursor == null) {
            cursor();
        }

        return create(cursor.next());
    }

    @Override
    public DBObject toQueryObject() {
        BasicDBObject result = new BasicDBObject();
        if (where != null) {
            result.put("$where", where);
        }

        for (String name : fields.keySet()) {
            List<FieldQueryImpl<T>> queries = fields.get(name);
            for (FieldQueryImpl<T> field : queries) {
                Object object = result.get(name);
                if (field.getOperator() == null) {
                    if (object != null && object instanceof DBObject) {
                        throw new IllegalQueryException("Equals operators are standalone");
                    }

                    result.put(name, field.getObject());
                } else {
                    if (object != null && !(object instanceof DBObject)) {
                        throw new IllegalQueryException("Equals operators are standalone");
                    }

                    DBObject operators = object != null ? (DBObject) object : new BasicDBObject();
                    operators.put(field.getOperator(), field.getObject());
                    result.put(name, operators);
                }
            }
        }

        if (orQueries.size() > 0) {
            BasicDBList list = new BasicDBList();
            for (Query<T> query : orQueries) {
                list.add(query.toQueryObject());
            }

            result.put("$or", list);
        }

        if (norQueries.size() > 0) {
            BasicDBList list = new BasicDBList();
            for (Query<T> query : norQueries) {
                list.add(query.toQueryObject());
            }

            result.put("$nor", list);
        }

        if (andQueries.size() > 0) {
            BasicDBList list = new BasicDBList();
            for (Query<T> query : andQueries) {
                list.add(query.toQueryObject());
            }

            result.put("$and", list);
        }

        return result;
    }

    public T create(DBObject dbObject) {
        return morphix.getMapper().unmarshal(cls, dbObject);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
