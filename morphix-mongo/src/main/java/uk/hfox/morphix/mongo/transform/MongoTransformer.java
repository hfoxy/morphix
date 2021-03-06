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
import uk.hfox.morphix.annotations.field.iterable.CollectionProperties;
import uk.hfox.morphix.annotations.field.iterable.MapProperties;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.mapper.lifecycle.LifecycleAction;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.entity.MongoCache;
import uk.hfox.morphix.mongo.mapper.MongoEntity;
import uk.hfox.morphix.mongo.mapper.MongoField;
import uk.hfox.morphix.mongo.transform.converter.*;
import uk.hfox.morphix.transform.*;
import uk.hfox.morphix.transform.data.TransformationData;
import uk.hfox.morphix.utils.Conditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static uk.hfox.morphix.transform.ConvertedType.*;

/**
 * Transforms MongoDB Bson document into an Object and vice versa
 */
public class MongoTransformer implements Transformer<Document> {

    private static final String POLYMORPHIC_KEY = "morphix_class";

    private final MongoCache cache;

    private final EnumMap<ConvertedType, Converter<Document>> converters;
    private final Map<Class<?>, MongoEntity> entities;

    public MongoTransformer(MorphixMongoConnector connector, MongoCache cache) {
        this.cache = cache;
        this.converters = new EnumMap<>(ConvertedType.class);
        this.entities = new HashMap<>();

        setConverter(ARRAY, new ArrayConverter(this));
        setConverter(BYTE, new ByteConverter());
        setConverter(SHORT, new ShortConverter());
        setConverter(INTEGER, new IntegerConverter());
        setConverter(LONG, new LongConverter());
        setConverter(FLOAT, new FloatConverter());
        setConverter(DOUBLE, new DoubleConverter());
        setConverter(CHARACTER, new CharacterConverter());
        setConverter(BOOLEAN, new BooleanConverter());
        setConverter(STRING, new StringConverter());
        setConverter(DATETIME, new LocalDateTimeConverter());
        setConverter(ENTITY, new EntityConverter(this));
        setConverter(REFERENCE, new ReferenceConverter(connector));
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
    public Converter<Document> getConverter(Field field) {
        ConvertedType type = ConvertedType.findByField(field);
        if (type.isIterable()) {
            switch (type) {
                case COLLECTION:
                    return new CollectionConverter(field.getAnnotation(CollectionProperties.class));
                case MAP:
                    return new MapConverter(field.getAnnotation(MapProperties.class));
            }

            throw new IllegalArgumentException("No supported converter for type '" + type.name() + "'");
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
        entity.call(LifecycleAction.BEFORE_OUT_TRANSFORM, object);

        if (entity.isPolymorphic()) {
            document.put(POLYMORPHIC_KEY, object.getClass().getName());
        }

        for (Map.Entry<String, MongoField> entry : fields.entrySet()) {
            if (!filter.isAccepted(entry.getValue().getName())) {
                continue;
            }

            MongoField field = entry.getValue();
            TransformationData data = new TransformationData(field.getValue(object), field.getField());
            field.getConverter().push(field.getName(), document, field.getValue(object), data);
        }

        entity.call(LifecycleAction.AFTER_OUT_TRANSFORM, object);
        return document;
    }

    @Override
    public <O> O fromGenericDB(Document db, Object entity, Class<O> cls) {
        return fromGenericDB(db, entity, cls, new FieldFilter(true));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O fromGenericDB(Document document, Object entity, Class<O> cls, Filter filter) {
        Conditions.notNull(document, "document");
        Conditions.notNull(filter, "filter");

        if (entity == null) {
            ObjectId key = document.getObjectId("_id");
            if (key != null) {
                entity = this.cache.getEntity(key);
            }
        }

        if (entity == null && cls == null) {
            return null;
        }

        MongoEntity mongoEntity = null;

        if (entity == null) {
            mongoEntity = getOrMapEntity(cls);

            Class<?> out = cls;
            if (mongoEntity.isPolymorphic()) {
                String className = document.getString(POLYMORPHIC_KEY);
                if (className == null) {
                    throw new MorphixEntityException("no class name field in polymorphic entity");
                }

                try {
                    out = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new MorphixEntityException("class not found for entity", e);
                }

                mongoEntity = getOrMapEntity(out);
            }

            try {
                Constructor<?> constructor = out.getDeclaredConstructor();
                constructor.setAccessible(true);

                entity = constructor.newInstance();
            } catch (Exception ex) {
                throw new MorphixEntityException("unable to construct entity (" + out.getName() + ")", ex);
            }
        }

        if (mongoEntity == null) {
            mongoEntity = getOrMapEntity(entity.getClass());
        }

        Map<String, MongoField> fields = mongoEntity.getFields();
        mongoEntity.call(LifecycleAction.BEFORE_IN_TRANSFORM, entity);

        for (Map.Entry<String, MongoField> entry : fields.entrySet()) {
            if (!filter.isAccepted(entry.getValue().getName())) {
                continue;
            }

            MongoField field = entry.getValue();
            Object value;

            TransformationData data;
            if (field.getType() == ENTITY || field.getType() == REFERENCE || field.getType().isIterable()) {
                data = new TransformationData(field.getValue(entity), field.getField());
            } else {
                data = new TransformationData(field.getValue(entity), null);
            }

            value = field.getConverter().pull(field.getName(), document, data);
            field.setValue(entity, value);
        }

        mongoEntity.call(LifecycleAction.AFTER_IN_TRANSFORM, entity);
        return (O) entity;
    }

}
