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
import org.bson.types.ObjectId;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.entity.MongoCache;
import uk.hfox.morphix.mongo.mapper.MongoEntity;
import uk.hfox.morphix.mongo.mapper.MongoField;
import uk.hfox.morphix.mongo.transform.converter.*;
import uk.hfox.morphix.transform.*;
import uk.hfox.morphix.utils.Conditions;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static uk.hfox.morphix.transform.ConvertedType.*;

/**
 * Transforms MongoDB Bson document into an Object and vice versa
 */
public class MongoTransformer implements Transformer<Document> {

    private final MorphixMongoConnector connector;
    private final MongoCache cache;

    private final Map<ConvertedType, Converter<Document>> converters;
    private final Map<Class<?>, MongoEntity> entities;

    public MongoTransformer(MorphixMongoConnector connector, MongoCache cache) {
        this.connector = connector;
        this.cache = cache;
        this.converters = new HashMap<>();
        this.entities = new HashMap<>();

        setConverter(BYTE, new ByteConverter());
        setConverter(SHORT, new ShortConverter());
        setConverter(INTEGER, new IntegerConverter());
        setConverter(LONG, new LongConverter());
        setConverter(FLOAT, new FloatConverter());
        setConverter(DOUBLE, new DoubleConverter());
        setConverter(CHARACTER, new CharacterConverter());
        setConverter(BOOLEAN, new BooleanConverter());
        setConverter(STRING, new StringConverter());
        setConverter(DATETIME, new DateTimeConverter());
        setConverter(ENTITY, new EntityConverter(this));
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
    public Document toDB(Object object, Filter filter) {
        Conditions.notNull(object);
        Conditions.notNull(filter);

        Document document = new Document();
        MongoEntity entity = getOrMapEntity(object.getClass());
        Map<String, MongoField> fields = entity.getFields();

        for (Map.Entry<String, MongoField> entry : fields.entrySet()) {
            if (!filter.isAccepted(entry.getKey())) {
                continue;
            }

            MongoField field = entry.getValue();
            field.getConverter().push(field.getName(), document, field.getValue(object));
        }

        return document;
    }

    @Override
    public <O> O fromGenericDB(Document db, O entity, Class<O> cls) {
        return fromGenericDB(db, entity, cls, new FieldFilter(true));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O fromGenericDB(Document document, O entity, Class<O> cls, Filter filter) {
        Conditions.notNull(document, "document");
        Conditions.notNull(filter, "filter");

        if (entity == null) {
            ObjectId key = document.getObjectId("_id");
            if (key != null) {
                entity = (O) this.cache.getEntity(key);
            }
        }

        if (entity == null && cls == null) {
            return null;
        }

        if (entity == null) {
            try {
                Constructor<O> constructor = cls.getConstructor();
                entity = constructor.newInstance();
            } catch (Exception ex) {
                throw new MorphixEntityException("unable to construct entity", ex);
            }
        }

        MongoEntity mongoEntity = getOrMapEntity(entity.getClass());
        Map<String, MongoField> fields = mongoEntity.getFields();

        for (Map.Entry<String, MongoField> entry : fields.entrySet()) {
            if (!filter.isAccepted(entry.getKey())) {
                continue;
            }

            MongoField field = entry.getValue();
            Object value;

            if (field.getType() == ENTITY || field.getType() == REFERENCE) {
                value = field.getConverter().pull(field.getName(), document, field.getValue(entity));
            } else {
                value = field.getConverter().pull(field.getName(), document);
            }

            field.setValue(entity, value);
        }

        return entity;
    }

}
