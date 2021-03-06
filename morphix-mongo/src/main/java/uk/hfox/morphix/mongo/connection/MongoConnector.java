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
package uk.hfox.morphix.mongo.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import uk.hfox.morphix.connector.ConnectorBuilder;
import uk.hfox.morphix.exception.connection.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;

public abstract class MongoConnector implements ConnectorBuilder<MorphixMongoConnector> {

    protected final String database;
    protected final int timeout;
    protected final List<ServerAddress> addresses;

    MongoConnector(Builder builder) {
        this.database = builder.database;
        this.timeout = builder.timeout;
        this.addresses = builder.addresses;
    }

    /**
     * Builds the set of Mongo options used to build the MongoClient
     *
     * @return The set of options defined by this connector
     */
    protected MongoClientOptions options() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.connectTimeout(timeout);
        builder.socketTimeout(timeout);
        builder.serverSelectionTimeout(timeout);
        return builder.build();
    }

    @Override
    public MorphixMongoConnector build() {
        return new MorphixMongoConnector(this);
    }

    /**
     * Gets the name of the database used by this connector
     *
     * @return The name of the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the MongoClient based on what type of connector is being used
     * @return The completed Mongo client
     */
    public abstract MongoClient getClient();

    /**
     * Creates a new builder to start designing the connector
     * @return The new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String database;
        private boolean single;
        private List<ServerAddress> addresses = new ArrayList<>();

        private int timeout = 30000;

        private Builder() {
            // private constructor only!
            address("localhost", 27017);
        }

        /**
         * Sets the database that this connector will connect to.
         *
         * @param database The name of the Mongo database
         *
         * @return This builder instance
         */
        public Builder database(String database) {
            this.database = database;
            return this;
        }

        /**
         * Sets the builder to a "single" state and sets the specified host/port as the connection address
         * This method will clear the address pool before adding the supplied host/port
         *
         * @param host The host to connect to
         * @param port The port on the host
         *
         * @return This builder instance
         */
        public Builder address(String host, int port) {
            return address(new ServerAddress(host, port));
        }

        /**
         * Sets the builder to a "single" state and sets the specified address as the connection address
         * This method will clear the address pool before adding the supplied address
         *
         * @param address The address to connect to
         *
         * @return This builder instance
         */
        public Builder address(ServerAddress address) {
            this.single = true;
            this.addresses.clear();
            this.addresses.add(address);
            return this;
        }

        /**
         * Sets the builder to a "multi" state, and populates the address pool with the supplied addresses
         * This method will clear the current address pool before adding the supplied addresses
         *
         * @param addresses The pool of address to add to the address pool
         *
         * @return This builder instance
         */
        public Builder pool(List<ServerAddress> addresses) {
            this.single = false;
            this.addresses.clear();
            this.addresses.addAll(addresses);
            return this;
        }

        /**
         * Sets the connection timeout to the supplied value. This value is in ms, eg; 30000 would be 30 seconds
         *
         * @param timeout The timeout in ms
         *
         * @return This builder instance
         */
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Returns whether or not this builder currently represents a single or multi-node MongoDB connector
         *
         * @return true if this is a single node, false otherwise
         */
        public boolean isSingle() {
            return single;
        }

        /**
         * Builds the connector
         *
         * @return The new connector
         */
        public MongoConnector build() {
            if (this.database == null) {
                throw new InvalidConfigurationException("database cannot be null");
            }

            if (single) {
                return new SingleNodeConnector(this);
            } else {
                return new PoolConnector(this);
            }
        }

    }

}
