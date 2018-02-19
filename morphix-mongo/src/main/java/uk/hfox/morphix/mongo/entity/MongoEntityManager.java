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
package uk.hfox.morphix.mongo.entity;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import uk.hfox.morphix.entity.EntityManager;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.exception.support.UnsupportedFeatureException;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.helper.CollectionHelper;
import uk.hfox.morphix.mongo.query.raw.input.MongoUpdateQuery;
import uk.hfox.morphix.mongo.query.raw.output.MongoFindQuery;
import uk.hfox.morphix.query.raw.FindQuery;
import uk.hfox.morphix.transform.Filter;
import uk.hfox.morphix.utils.Conditions;

public class MongoEntityManager implements EntityManager {

    private final MorphixMongoConnector connector;

    public MongoEntityManager(MorphixMongoConnector connector) {
        this.connector = connector;
    }

    /**
     * Gets the connector that this manager belongs to
     *
     * @return The connector
     */
    @Override
    public MorphixMongoConnector getConnector() {
        return this.connector;
    }

    /**
     * Gets a bson filter used to find/update documents
     *
     * @param object The object to create a filter for
     *
     * @return The bson filter
     */
    public Bson getFilters(Object object) {
        return Filters.eq("_id", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Object entity, Filter filter) {
        Conditions.notNull(entity);

        MongoCollection<Document> collection = getCollection(entity);
        MongoFindQuery find = new MongoFindQuery(collection, getFilters(entity));
        find.performQuery();

        getConnector().getTransformer().fromGenericDB(find.getOutput().first(), entity, null, filter);
    }

    @Override
    public void update(FindQuery query, Filter filter) {
        Conditions.notNull(query);

        if (!(query instanceof MongoFindQuery)) {
            throw new UnsupportedFeatureException(getConnector(), "mongo does not support any query responses except mongofind");
        }

        MongoFindQuery find = (MongoFindQuery) query;
        for (Document document : find.getResults()) {
            getConnector().getTransformer().fromGenericDB(document, null, null, filter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Object entity, Filter filter, boolean update) {
        Conditions.notNull(entity);

        MongoCollection<Document> collection = getCollection(entity);
        Document document = new Document("$set", getConnector().getTransformer().toDB(entity, filter));

        new MongoUpdateQuery(collection, getFilters(entity), document, false, new UpdateOptions()).performQuery();
    }

    /**
     * Gets the Mongo collection used by a specific entity
     *
     * @param entity The entity to lookup
     *
     * @return The collection
     */
    private MongoCollection<Document> getCollection(Object entity) {
        CollectionHelper collectionHelper = getConnector().getHelperManager().getCollectionHelper();
        String collectionName = collectionHelper.getCollection(entity.getClass());

        return getConnector().getDatabase().getCollection(collectionName);
    }

    @Override
    public Object getEntity(Object key) {
        if (!(key instanceof ObjectId)) {
            throw new MorphixEntityException("invalid key type, ObjectId required");
        }

        ObjectId id = (ObjectId) key;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked") // checked
    public <T> T getEntity(Object key, Class<T> cls) {
        Object entity = getEntity(key);
        if (cls.isAssignableFrom(entity.getClass())) {
            return (T) entity;
        }

        return null;
    }

}
