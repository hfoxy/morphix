package me.hfox.morphix.mongo.query;

import com.mongodb.BasicDBList;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.mapping.MappingData;
import me.hfox.morphix.mongo.mapping.field.FieldMapper;

import java.util.Collections;

public class FieldQueryImpl<T> implements FieldQuery<T> {

    private Query<T> query;
    private Morphix morphix;

    private String operator;
    private Object object;

    public FieldQueryImpl(Query<T> query, Morphix morphix) {
        this.query = query;
        this.morphix = morphix;
    }

    public String getOperator() {
        return operator;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public Query<T> equal(Object object) {
        set(null, object);
        return query;
    }

    @Override
    public Query<T> not(Object object) {
        set("not", object);
        return query;
    }

    @Override
    public Query<T> notEqual(Object object) {
        set("ne", object);
        return query;
    }

    @Override
    public Query<T> greaterThan(Object object) {
        set("gt", object);
        return query;
    }

    @Override
    public Query<T> greaterThanOrEqual(Object object) {
        set("gte", object);
        return query;
    }

    @Override
    public Query<T> lessThan(Object object) {
        set("lt", object);
        return query;
    }

    @Override
    public Query<T> lessThanOrEqual(Object object) {
        set("lte", object);
        return query;
    }

    @Override
    public Query<T> exists() {
        set("exists", true);
        return query;
    }

    @Override
    public Query<T> doesNotExist() {
        set("exists", false);
        return query;
    }

    @Override
    public Query<T> modulo(int mod, int result) {
        BasicDBList list = new BasicDBList();
        list.add(mod);
        list.add(result);

        set("nin", list);
        return query;
    }

    @Override
    public Query<T> hasAll(Object... objects) {
        BasicDBList list = new BasicDBList();
        for (Object object : objects) {
            if (object == null) {
                list.add(null);
                continue;
            }

            FieldMapper mapper = FieldMapper.createFromField(object.getClass(), null, morphix);
            list.add(mapper.marshal(new MappingData(), object));
        }

        set("size", list);
        return query;
    }

    public Query<T> in(Object object) {
        set("in", object);
        return query;
    }

    public Query<T> notIn(Object object) {
        set("nin", object);
        return query;
    }

    @Override
    public Query<T> size(int size) {
        set("size", size);
        return query;
    }

    private void set(String operator, Object value) {
        this.operator = (operator == null ? null : "$" + operator);
        this.object = value;
    }

}
