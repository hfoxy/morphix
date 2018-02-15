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
package uk.hfox.morphix.mongo.transform;

import org.bson.Document;
import uk.hfox.morphix.mongo.mapper.MongoEntity;
import uk.hfox.morphix.transform.ConvertedType;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.transform.FieldFilter;
import uk.hfox.morphix.transform.Transformer;
import uk.hfox.morphix.utils.Conditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Transforms MongoDB Bson document into an Object and vice versa
 */
public class MongoTransformer implements Transformer<Document> {

    private final Map<ConvertedType, Converter<Document>> converters;
    private final Map<Class<?>, MongoEntity> entities;

    public MongoTransformer() {
        this.converters = new HashMap<>();
        this.entities = new HashMap<>();
    }

    /**
     * Gets the existing mapped entity, or creates a new mapped entity
     *
     * @param cls The class of mapped entity to get
     *
     * @return The mapped entity associated with this class
     */
    public MongoEntity getOrMapEntity(Class<?> cls) {
        MongoEntity entity = this.entities.get(cls);
        if (entity == null) {
            entity = new MongoEntity(this, cls);
            entity.map();

            this.entities.put(cls, entity);
        }

        return entity;
    }

    @Override
    public Converter<Document> getConverter(Class<?> cls) {
        ConvertedType type = null;
        for (ConvertedType value : ConvertedType.values()) {
            if (value.isSatisfied(cls)) {
                type = value;
                break;
            }
        }

        if (type == null) {
            return null;
        }

        return getConverter(type);
    }

    @Override
    public Converter<Document> getConverter(ConvertedType type) {
        return this.converters.get(type);
    }

    @Override
    public void setConverter(ConvertedType type, Converter<Document> converter) {
        this.converters.put(type, converter);
    }

    @Override
    public Document toDB(Object object) {
        return toDB(object, new FieldFilter(true));
    }

    @Override
    public Document toDB(Object object, FieldFilter filter) {
        Conditions.notNull(object);
        Conditions.notNull(filter);

        MongoEntity entity = getOrMapEntity(object.getClass());

        return null;
    }

    @Override
    public <O> O fromGenericDB(Document db, O entity) {
        return fromGenericDB(db, entity, new FieldFilter(true));
    }

    @Override
    public <O> O fromGenericDB(Document db, O entity, FieldFilter filter) {
        return null;
    }

}
