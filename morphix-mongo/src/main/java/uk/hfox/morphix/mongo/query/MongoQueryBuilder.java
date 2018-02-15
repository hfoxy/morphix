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

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.query.raw.input.MongoDeleteQuery;
import uk.hfox.morphix.mongo.query.raw.output.MongoFindQuery;
import uk.hfox.morphix.mongo.query.sort.MongoQuerySortElement;
import uk.hfox.morphix.query.QueryBuilder;
import uk.hfox.morphix.query.result.QueryResult;
import uk.hfox.morphix.utils.Conditions;

import java.util.HashMap;
import java.util.Map;

public class MongoQueryBuilder<R> implements QueryBuilder<R> {

    private final Class<R> clazz;

    private final MongoCollection<Document> collection;
    private final MorphixMongoConnector connector;

    private final Map<String, MongoFieldQueryBuilder<R>> fields;

    MongoQuerySortBuilder<R> sort;

    public MongoQueryBuilder(Class<R> clazz, MongoCollection<Document> collection, MorphixMongoConnector connector) {
        this.clazz = clazz;
        this.collection = collection;
        this.connector = connector;

        this.fields = new HashMap<>();
    }

    @Override
    public MorphixMongoConnector getConnector() {
        return connector;
    }

    @Override
    public Class<R> getQueryType() {
        return clazz;
    }

    @Override
    public MongoFieldQueryBuilder<R> where(String field) {
        MongoFieldQueryBuilder<R> query = new MongoFieldQueryBuilder<>(this);
        this.fields.put(field, query);

        return query;
    }

    @Override
    public MongoQuerySortBuilder<R> sort() {
        if (this.sort == null) {
            return new MongoQuerySortBuilder<>(this);
        }

        return this.sort;
    }

    @Override
    public void delete() {
        delete(false);
    }

    @Override
    public void delete(boolean justOne) {
        MongoDeleteQuery query = new MongoDeleteQuery(collection, getFilter(), !justOne, null);
        query.performQuery();
    }

    @Override
    public QueryResult<R> result() {
        throw Conditions.unimplemented();
    }

    private Document getFilter() {
        Document document = new Document();
        for (Map.Entry<String, MongoFieldQueryBuilder<R>> entry : this.fields.entrySet()) {
            MongoFieldQueryBuilder<R> field = entry.getValue();
            document.put(entry.getKey(), field.getBson());
        }

        return document;
    }

    @Override
    public MongoFindQuery find() {
        MongoFindQuery query = new MongoFindQuery(collection, getFilter());
        query.performQuery();

        if (this.sort != null) {
            Document bson = new Document();
            for (MongoQuerySortElement element : this.sort.getElements()) {
                element.append(bson);
            }

            query.getOutput().sort(bson);
        }

        return query;
    }

}
