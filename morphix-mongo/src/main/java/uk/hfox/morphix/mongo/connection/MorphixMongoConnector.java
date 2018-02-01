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
import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.mongo.connection.MongoConnector;
import uk.hfox.morphix.query.QueryBuilder;

/**
 * MongoDB implementation of the Morphix connector
 */
public class MorphixMongoConnector implements MorphixConnector {

    private static final long serialVersionUID = -3349895475330867986L;

    private final MongoConnector builder;

    private MongoClient client;

    MorphixMongoConnector(MongoConnector builder) {
        this.builder = builder;
    }

    /**
     * Gets the client used by this Morphix connector
     *
     * @return The client used by this connector
     */
    public MongoClient getClient() {
        return client;
    }

    @Override
    public void connect() {
        if (this.client != null) {
            if (isConnected()) {
                throw new IllegalStateException("Client is already connected");
            } else {
                this.client = null;
            }
        }

        this.client = this.builder.getClient();
    }

    @Override
    public void disconnect() {
        if (this.client == null || !isConnected()) {
            throw new IllegalStateException("Client is not connected");
        }

        this.client.close();
    }

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

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls) {
        // TODO: create query
        return null;
    }

    @Override
    public <T> QueryBuilder<T> createQuery(Class<T> cls, String collection) {
        // TODO: create query
        return null;
    }

}
