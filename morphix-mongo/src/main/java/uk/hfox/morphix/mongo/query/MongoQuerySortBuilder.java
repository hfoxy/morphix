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

import uk.hfox.morphix.mongo.query.sort.MongoQuerySortElement;
import uk.hfox.morphix.query.QuerySortBuilder;
import uk.hfox.morphix.utils.Conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoQuerySortBuilder<R> implements QuerySortBuilder<R, MongoQuerySortElement> {

    private final MongoQueryBuilder<R> builder;

    private final List<MongoQuerySortElement> elements;
    private final Map<String, MongoQuerySortElement> fields;

    public MongoQuerySortBuilder(MongoQueryBuilder<R> builder) {
        this.builder = builder;
        this.elements = new ArrayList<>();
        this.fields = new HashMap<>();
    }

    @Override
    public List<MongoQuerySortElement> getElements() {
        return new ArrayList<>(elements);
    }

    @Override
    public MongoQuerySortBuilder<R> asc(String field) {
        add(field, 1);
        return this;
    }

    @Override
    public MongoQuerySortBuilder<R> desc(String field) {
        add(field, -1);
        return this;
    }

    private void add(String field, Object value) {
        Conditions.notNull(field);
        if (this.fields.containsKey(field)) {
            throw new IllegalArgumentException("field already exists");
        }

        MongoQuerySortElement element = new MongoQuerySortElement(field, value);
        this.elements.add(element);
        this.fields.put(field, element);
    }

    @Override
    public MongoQuerySortBuilder<R> clear() {
        this.elements.clear();
        this.fields.clear();
        return this;
    }

    @Override
    public MongoQueryBuilder<R> cancel() {
        return this.builder;
    }

    @Override
    public MongoQueryBuilder<R> done() {
        this.builder.sort = this;
        return this.builder;
    }

}
