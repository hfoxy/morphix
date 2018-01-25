/*-
 * ========================LICENSE_START========================
 * Morphix Mongo
 * %%
 * Copyright (C) 2017 - 2018 Harry Fox
 * %%
 * This file is part of Morphix, licensed under the MIT License (MIT).
 * 
 * Copyright 2018 Harry Fox <https://hfox.uk/>
 * Copyright 2018 contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ========================LICENSE_END========================
 */
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
