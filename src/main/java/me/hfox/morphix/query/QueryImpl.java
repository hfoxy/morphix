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
    private String where;
    private Map<String, List<FieldQueryImpl<T>>> fields;
    private List<Query<T>> orQueries;
    private List<Query<T>> norQueries;
    private List<Query<T>> andQueries;
    private String[] order;

    private DBCursor cursor;

    public QueryImpl(Morphix morphix, Class<T> cls) {
        this.morphix = morphix;
        this.cls = cls;
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
    public T update() {
        return null;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public List<T> asList() {
        return null;
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
                if (field.getObject() == null) {
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

}
