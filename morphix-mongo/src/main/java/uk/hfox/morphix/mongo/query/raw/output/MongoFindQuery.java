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
package uk.hfox.morphix.mongo.query.raw.output;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import uk.hfox.morphix.mongo.query.raw.MongoOutputQuery;
import uk.hfox.morphix.query.raw.FindQuery;

/**
 * Performs a find query on the collection
 */
public class MongoFindQuery extends MongoOutputQuery<FindIterable<Document>> implements FindQuery<Document> {

    private final Bson search;

    public MongoFindQuery(MongoCollection<Document> collection, Bson search) {
        super(collection);
        this.search = search;
    }

    @Override
    protected void runQuery() {
        super.output = super.collection.find(search);
    }

    @Override
    public Iterable<Document> getResults() {
        return getOutput();
    }

}
