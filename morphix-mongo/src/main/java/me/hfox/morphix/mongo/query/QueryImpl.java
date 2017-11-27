package me.hfox.morphix.mongo.query;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.annotation.entity.Entity;
import me.hfox.morphix.mongo.annotation.lifecycle.AccessedAt;
import me.hfox.morphix.mongo.annotation.lifecycle.PostDelete;
import me.hfox.morphix.mongo.annotation.lifecycle.PostUpdate;
import me.hfox.morphix.mongo.annotation.lifecycle.PreDelete;
import me.hfox.morphix.mongo.annotation.lifecycle.PreUpdate;
import me.hfox.morphix.mongo.exception.query.IllegalQueryException;
import me.hfox.morphix.mongo.mapping.MappingData;
import me.hfox.morphix.mongo.mapping.field.FieldMapper;
import me.hfox.morphix.mongo.util.AnnotationUtils;

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

    public Class<T> getQueryType() {
        return cls;
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

            remove(new BasicDBObject("_id", getDB().get("_id")));
            return;
        }

        remove(toQueryObject());
    }

    private void remove(DBObject query) {
        Entity entity = AnnotationUtils.getHierarchicalAnnotation(cls, Entity.class);
        List<T> objects;

        if (entity.createOnDelete()) {
            QueryImpl<T> deleteQuery = new QueryImpl<>(morphix, cls);
            objects = deleteQuery.asList(morphix.getDatabase().getCollection(collection).find(query));
        } else {
            objects = asList(true);
        }

        for (T object : objects) {
            morphix.getLifecycleHelper().callMethod(PreDelete.class, object);
        }

        morphix.getDatabase().getCollection(collection).remove(query);

        for (T object : objects) {
            morphix.getCache(cls).remove(object);
            morphix.getLifecycleHelper().callMethod(PostDelete.class, object);
        }
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

            morphix.getLifecycleHelper().callField(AccessedAt.class, cached, true);
            morphix.getLifecycleHelper().callMethod(PreUpdate.class, cached);
            morphix.getMapper().update(cached, object, true);
            morphix.getLifecycleHelper().callMethod(PostUpdate.class, cached);
        }
    }

    @Override
    public T get() {
        return get(false);
    }

    public T get(boolean cacheOnly) {
        return create(getDB(), cacheOnly);
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
        return asList(false);
    }

    public List<T> asList(boolean cacheOnly) {
        return asList(cursor(), cacheOnly);
    }

    public List<T> asList(DBCursor cursor) {
        return asList(cursor, false);
    }

    public List<T> asList(DBCursor cursor, boolean cacheOnly) {
        List<DBObject> objects = asDBList(cursor);
        List<T> results = new ArrayList<>();
        for (DBObject object : objects) {
            T entity = create(object, cacheOnly);
            if (entity != null) {
                results.add(entity);
            }
        }

        return results;
    }

    @Override
    public List<DBObject> asDBList() {
        return asDBList(cursor());
    }

    public List<DBObject> asDBList(DBCursor cursor) {
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

        MappingData mappingData = new MappingData();
        for (String name : fields.keySet()) {
            List<FieldQueryImpl<T>> queries = fields.get(name);
            for (FieldQueryImpl<T> field : queries) {
                Object object = result.get(name);
                if (field.getOperator() == null) {
                    if (object != null && object instanceof DBObject) {
                        throw new IllegalQueryException("Equals operators are standalone");
                    }

                    Object store = field.getObject();
                    if (store != null) {
                        store = FieldMapper.createFromName(getQueryType(), store.getClass(), name, morphix).marshal(mappingData, store);
                        // System.out.println("Storing " + store + " instead of " + field.getObject());
                    }

                    result.put(name, store);
                } else {
                    if (object != null && !(object instanceof DBObject)) {
                        throw new IllegalQueryException("Equals operators are standalone");
                    }

                    Object store = field.getObject();
                    if (store != null) {
                        store = FieldMapper.createFromName(getQueryType(), store.getClass(), name, morphix).marshal(mappingData, store);
                        // System.out.println("Storing " + store + " instead of " + field.getObject());
                    }

                    DBObject operators = object != null ? (DBObject) object : new BasicDBObject();
                    operators.put(field.getOperator(), store);
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

    @SuppressWarnings("unchecked")
    public T create(DBObject dbObject) {
        return create(dbObject, false);
    }

    @SuppressWarnings("unchecked")
    public T create(DBObject dbObject, boolean cacheOnly) {
        if (dbObject == null) {
            return null;
        }

        Object result = morphix.getCache(cls).getEntity(dbObject);

        T entity = null;
        try {
            entity = (T) result;
        } catch (ClassCastException ex) {
            System.out.println("Cached entity could not be cast to " + cls.getName());
            ex.printStackTrace();
        }

        if (entity != null) {
            return entity;
        }

        Class<T> cls = morphix.getRemapHelper().remap(this.cls);
        if (!cacheOnly) {
            entity = morphix.getMapper().unmarshal(cls, dbObject, true);
            morphix.getLifecycleHelper().callField(AccessedAt.class, entity, false);
        }

        return entity;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
