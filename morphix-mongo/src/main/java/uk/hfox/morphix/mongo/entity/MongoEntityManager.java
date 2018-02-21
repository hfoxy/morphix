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
import uk.hfox.morphix.mapper.lifecycle.LifecycleAction;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.helper.CollectionHelper;
import uk.hfox.morphix.mongo.mapper.MongoEntity;
import uk.hfox.morphix.mongo.query.raw.input.MongoInsertQuery;
import uk.hfox.morphix.mongo.query.raw.input.MongoUpdateQuery;
import uk.hfox.morphix.mongo.query.raw.output.MongoFindQuery;
import uk.hfox.morphix.transform.Filter;
import uk.hfox.morphix.utils.Conditions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoEntityManager implements EntityManager {

    private final MorphixMongoConnector connector;
    private final MongoCache cache;

    public MongoEntityManager(MorphixMongoConnector connector, MongoCache cache) {
        this.connector = connector;
        this.cache = cache;
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

    public MongoCache getCache() {
        return cache;
    }

    /**
     * Gets a bson filter used to find/update documents
     *
     * @param object The object to create a filter for
     *
     * @return The bson filter
     */
    public Bson getFilters(Object object) {
        return Filters.eq("_id", cache.getId(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Filter filter, Object[] entities) {
        Conditions.notNull(entities);
        if (entities.length == 0) {
            return;
        }

        MongoCollection<Document> collection = getCollection(entities[0]);
        for (Object entity : entities) {
            MongoFindQuery find = new MongoFindQuery(collection, getFilters(entity));
            find.performQuery();

            getConnector().getTransformer().fromGenericDB(find.getOutput().first(), entity, null, filter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Filter filter, boolean update, Object[] entities) {
        Conditions.notNull(entities);
        if (entities.length == 0) {
            return;
        }

        MongoCollection<Document> collection = getCollection(entities[0]);
        Map<Document, Object> inserts = new HashMap<>();
        List<LifecycleEntity> lifecycle = new ArrayList<>();
        populateOrUpdate(filter, entities, collection, inserts, lifecycle);
        performInsert(collection, inserts);

        for (LifecycleEntity entry : lifecycle) {
            MongoEntity mongoEntity = entry.mongoEntity;

            if (entry.update) {
                mongoEntity.call(LifecycleAction.AFTER_UPDATE, entry.entity);
            } else {
                mongoEntity.call(LifecycleAction.AFTER_CREATE, entry.entity);
            }
        }
    }

    private void populateOrUpdate(Filter filter, Object[] entities, MongoCollection<Document> collection,
                                  Map<Document, Object> inserts, List<LifecycleEntity> lifecycle) {
        for (Object entity : entities) {
            ObjectId id = this.cache.getId(entity);
            MongoEntity mongoEntity = this.connector.getTransformer().getOrMapEntity(entity.getClass());
            lifecycle.add(new LifecycleEntity(mongoEntity, id != null, entity));

            if (id != null) {
                mongoEntity.set(LifecycleAction.UPDATED_AT, entity, LocalDateTime.now());
                mongoEntity.call(LifecycleAction.BEFORE_UPDATE, entity);
            } else {
                mongoEntity.set(LifecycleAction.CREATED_AT, entity, LocalDateTime.now());
                mongoEntity.call(LifecycleAction.BEFORE_CREATE, entity);
            }

            Document document = getConnector().getTransformer().toDB(entity, filter);

            if (id != null) {
                document = new Document("$set", document);
                new MongoUpdateQuery(collection, getFilters(entity), document, false, new UpdateOptions()).performQuery();
            } else {
                inserts.put(document, entity);
            }
        }
    }

    private void performInsert(MongoCollection<Document> collection, Map<Document, Object> inserts) {
        List<Document> documents = new ArrayList<>(inserts.keySet());
        if (documents.size() == 1) {
            new MongoInsertQuery(collection, documents.get(0), null).performQuery();
        } else if (!documents.isEmpty()) {
            new MongoInsertQuery(collection, documents, null).performQuery();
        }

        for (Map.Entry<Document, Object> entry : inserts.entrySet()) {
            this.cache.put(entry.getKey().getObjectId("_id"), entry.getValue());
        }
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

    private static final class LifecycleEntity {

        private final MongoEntity mongoEntity;
        private final boolean update;
        private final Object entity;

        public LifecycleEntity(MongoEntity mongoEntity, boolean update, Object entity) {
            this.mongoEntity = mongoEntity;
            this.update = update;
            this.entity = entity;
        }

    }

}
