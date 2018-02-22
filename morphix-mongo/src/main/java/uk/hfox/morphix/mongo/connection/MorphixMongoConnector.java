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
package uk.hfox.morphix.mongo.connection;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.mongo.entity.MongoCache;
import uk.hfox.morphix.mongo.entity.MongoEntityManager;
import uk.hfox.morphix.mongo.helper.MongoHelperManager;
import uk.hfox.morphix.mongo.query.MongoQueryBuilder;
import uk.hfox.morphix.mongo.transform.MongoTransformer;
import uk.hfox.morphix.query.QueryBuilder;

/**
 * MongoDB implementation of the Morphix connector
 */
public class MorphixMongoConnector implements MorphixConnector {

    private final MongoConnector builder;

    private final MongoTransformer transformer;
    private final MongoEntityManager entityManager;
    private final MongoHelperManager helperManager;

    private MongoClient client;
    private MongoDatabase database;

    protected MorphixMongoConnector(MongoConnector builder) {
        this.builder = builder;

        MongoCache cache = new MongoCache(this);
        this.transformer = new MongoTransformer(this, cache);
        this.entityManager = new MongoEntityManager(this, cache);
        this.helperManager = new MongoHelperManager(this);
    }

    /**
     * Gets the client used by this Morphix connector
     *
     * @return The client used by this connector
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * Gets the mongo database used by this connector
     *
     * @return The mongo database used by this connector
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        if (this.client != null) {
            if (isConnected()) {
                throw new IllegalStateException("Client is already connected");
            } else {
                this.client = null;
                this.database = null;
            }
        }

        this.client = this.builder.getClient();
        this.database = this.client.getDatabase(this.builder.getDatabase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (this.client == null || !isConnected()) {
            throw new IllegalStateException("Client is not connected");
        }

        this.client.close();
        this.client = null;
        this.database = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        if (this.client == null) {
            return false;
        }

        try {
            this.client.getAddress();
        } catch (Exception ex) {
            this.client.close();
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoTransformer getTransformer() {
        return this.transformer;
    }

    @Override
    public MongoEntityManager getEntityManager() {
        return this.entityManager;
    }

    @Override
    public MongoHelperManager getHelperManager() {
        return this.helperManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls) {
        return createQuery(cls, getHelperManager()
                .getCollectionHelper()
                .getCollection(cls));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls, String collection) {
        return new MongoQueryBuilder<>(cls, this.database.getCollection(collection), this);
    }

}
