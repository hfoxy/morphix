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
package uk.hfox.morphix.mongo.helper;

import uk.hfox.morphix.helper.Helper;
import uk.hfox.morphix.helper.HelperManager;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;

public class MongoHelperManager extends HelperManager<MorphixMongoConnector> {

    private static final String COLLECTION_HELPER = "collection";

    public CollectionHelper getCollectionHelper() {
        return getHelper(COLLECTION_HELPER, CollectionHelper.class);
    }

    public void setCollectionHelper(CollectionHelper helper) {
        setHelper(COLLECTION_HELPER, helper);
    }

    @Override
    public void setHelper(String name, Helper<MorphixMongoConnector> helper) {
        if (name.equals(COLLECTION_HELPER) && !(helper instanceof CollectionHelper)) {
            throw new IllegalArgumentException("invalid helper type for collection helper");
        }

        super.setHelper(name, helper);
    }

}