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
package uk.hfox.morphix.mongo.query.raw.input;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.Document;
import uk.hfox.morphix.mongo.query.raw.MongoInputQuery;
import uk.hfox.morphix.utils.Conditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs an insert query on the database with the supplied options.
 * Supports inserting a single document and inserting multiple documents.
 */
public class MongoInsertQuery extends MongoInputQuery {

    private final List<Document> documents;

    private final boolean many;
    private final InsertOneOptions oneOptions;
    private final InsertManyOptions manyOptions;

    public MongoInsertQuery(MongoCollection collection, Document document, InsertOneOptions options) {
        super(collection);
        Conditions.notNull(document, "document");

        this.documents = new ArrayList<>();
        this.documents.add(document);

        this.many = false;
        this.oneOptions = options;
        this.manyOptions = null;
    }

    public MongoInsertQuery(MongoCollection collection, List<Document> documents, InsertManyOptions options) {
        super(collection);
        Conditions.notEmptyOrNullFilled(documents, "documents");

        this.documents = documents;

        this.many = true;
        this.oneOptions = null;
        this.manyOptions = options;
    }

    @Override
    public void performQuery() {
        if (many) {
            if (manyOptions != null) {
                super.collection.insertMany(this.documents, manyOptions);
            } else {
                super.collection.insertMany(this.documents);
            }
        } else {
            if (oneOptions != null) {
                super.collection.insertOne(this.documents.get(0), oneOptions);
            } else {
                super.collection.insertOne(this.documents.get(0));
            }
        }
    }

}
