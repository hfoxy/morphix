/*-
 * ========================LICENSE_START========================
 * Morphix MongoDB
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
package uk.hfox.morphix.mongo.query;

import uk.hfox.morphix.query.FieldQueryBuilder;

import java.util.HashMap;
import java.util.Map;

public class MongoFieldQueryBuilder<T> implements FieldQueryBuilder<T> {

    private final MongoQueryBuilder<T> builder;

    private boolean locked;

    private boolean not = false;
    private Object result = null;

    public MongoFieldQueryBuilder(MongoQueryBuilder<T> builder) {
        this.builder = builder;
    }

    private void checkLock() {
        if (this.locked) {
            throw new IllegalStateException("field query is locked");
        }
    }

    @Override
    public MongoQueryBuilder<T> matches(Object object) {
        checkLock();

        this.locked = true;
        setResult("eq", object);

        return builder;
    }

    @Override
    public MongoFieldQueryBuilder<T> not() {
        return not(!this.not);
    }

    @Override
    public MongoFieldQueryBuilder<T> not(boolean not) {
        this.not = not;
        return this;
    }

    @Override
    public MongoQueryBuilder<T> notEqual(Object object) {
        checkLock();

        this.locked = true;
        setResult("ne", object);

        return this.builder;
    }

    @Override
    public MongoQueryBuilder<T> greaterThan(Object object) {
        checkLock();

        this.locked = true;
        setResult("gt", object);

        return this.builder;
    }

    @Override
    public MongoQueryBuilder<T> greaterThanOrEqual(Object object) {
        checkLock();

        this.locked = true;
        setResult("gte", object);

        return this.builder;
    }

    @Override
    public MongoQueryBuilder<T> lessThan(Object object) {
        checkLock();

        this.locked = true;
        setResult("lt", object);

        return this.builder;
    }

    @Override
    public MongoQueryBuilder<T> lessThanOrEqual(Object object) {
        checkLock();

        this.locked = true;
        setResult("lte", object);

        return this.builder;
    }

    private void setResult(String operation, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("$" + operation, value);
        this.result = map;
    }

    public Object getBson() {
        if (not) {
            Map<String, Object> map = new HashMap<>();
            map.put("$not", result);
            return map;
        }

        return result;
    }

}
