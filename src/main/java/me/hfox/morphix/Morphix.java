package me.hfox.morphix;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import me.hfox.morphix.annotation.entity.Cache;
import me.hfox.morphix.annotation.lifecycle.CreatedAt;
import me.hfox.morphix.annotation.lifecycle.PostCreate;
import me.hfox.morphix.annotation.lifecycle.PostSave;
import me.hfox.morphix.annotation.lifecycle.PreCreate;
import me.hfox.morphix.annotation.lifecycle.PreSave;
import me.hfox.morphix.annotation.lifecycle.UpdatedAt;
import me.hfox.morphix.cache.EntityCache;
import me.hfox.morphix.cache.EntityCacheImpl;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.helper.entity.DefaultEntityHelper;
import me.hfox.morphix.helper.entity.EntityHelper;
import me.hfox.morphix.helper.lifecycle.LifecycleHelper;
import me.hfox.morphix.helper.lifecycle.DefaultLifecycleHelper;
import me.hfox.morphix.helper.name.NameHelper;
import me.hfox.morphix.helper.polymorphism.DefaultPolymorhpismHelper;
import me.hfox.morphix.helper.polymorphism.PolymorhpismHelper;
import me.hfox.morphix.helper.remap.DefaultRemapHelper;
import me.hfox.morphix.helper.remap.RemapHelper;
import me.hfox.morphix.mapping.MappingData;
import me.hfox.morphix.mapping.ObjectMapper;
import me.hfox.morphix.mapping.ObjectMapperImpl;
import me.hfox.morphix.query.Query;
import me.hfox.morphix.query.QueryImpl;
import me.hfox.morphix.util.AnnotationUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Morphix {

    private MongoClient connection;
    private DB database;

    private MorphixOptions options;
    private ObjectMapper mapper;
    private Map<String, EntityCache> caches;

    private EntityHelper entityHelper;
    private Map<Class<? extends NameHelper>, NameHelper> nameHelpers;
    private PolymorhpismHelper polymorhpismHelper;
    private LifecycleHelper lifecycleHelper;
    private RemapHelper remapHelper;

    public Morphix(MongoClient client, String database) {
        this(client, database, null, null);
    }

    public Morphix(MongoClient client, String database, MorphixOptions options) {
        this(client, database, options, null);
    }

    public Morphix(MongoClient client, String database, ObjectMapper mapper) {
        this(client, database, null, mapper);
    }

    public Morphix(MongoClient client, String database, MorphixOptions options, ObjectMapper mapper) {
        this.connection = client;
        if (connection != null) {
            this.database = connection.getDB(database);
        }

        this.options = (options == null ? MorphixOptions.builder().build() : options);
        this.mapper = (mapper == null ? new ObjectMapperImpl(this) : mapper);
        this.caches = new HashMap<>();

        this.entityHelper = new DefaultEntityHelper(this);
        this.nameHelpers = new HashMap<>();
        getNameHelper(MorphixDefaults.DEFAULT_NAME_HELPER);

        this.polymorhpismHelper = new DefaultPolymorhpismHelper();
        this.lifecycleHelper = new DefaultLifecycleHelper(this);
        this.remapHelper = new DefaultRemapHelper();
    }

    public MongoClient getConnection() {
        return connection;
    }

    public DB getDatabase() {
        return database;
    }

    public MorphixOptions getOptions() {
        return options;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Map<String, EntityCache> getCaches() {
        return caches;
    }

    public EntityCache getCache() {
        return getCache(MorphixDefaults.DEFAULT_CACHE_NAME);
    }

    public EntityCache getCache(Class<?> cls) {
        Cache cache = AnnotationUtils.getHierarchicalAnnotation(cls, Cache.class);
        if (cache == null) {
            return getCache();
        }

        if (!cache.enabled()) {
            return null;
        }

        return getCache(cache.value());
    }

    public EntityCache getCache(String name) {
        EntityCache cache = caches.get(name);
        if (cache == null) {
            cache = new EntityCacheImpl(this);
            caches.put(name, cache);
        }

        return cache;
    }

    public boolean hasCache(String name) {
        return caches.get(name) != null;
    }

    public void setCache(String name, EntityCache cache) {
        caches.put(name, cache);
    }

    public EntityHelper getEntityHelper() {
        return entityHelper;
    }

    public void setEntityHelper(EntityHelper entityHelper) {
        this.entityHelper = entityHelper;
    }

    public Map<Class<? extends NameHelper>, NameHelper> getNameHelpers() {
        return nameHelpers;
    }

    public NameHelper getNameHelper(Class<? extends NameHelper> clazz) {
        NameHelper helper = nameHelpers.get(clazz);
        if (helper == null) {
            helper = createNameHelper(clazz);
            nameHelpers.put(clazz, helper);
        }

        return helper;
    }

    public NameHelper createNameHelper(Class<? extends NameHelper> clazz) {
        try {
            Constructor<? extends NameHelper> constructor = clazz.getConstructor(Morphix.class);
            return constructor.newInstance(this);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new MorphixException(ex);
        }
    }

    public PolymorhpismHelper getPolymorhpismHelper() {
        return polymorhpismHelper;
    }

    public void setPolymorhpismHelper(PolymorhpismHelper polymorhpismHelper) {
        this.polymorhpismHelper = polymorhpismHelper;
    }

    public LifecycleHelper getLifecycleHelper() {
        return lifecycleHelper;
    }

    public void setLifecycleHelper(LifecycleHelper lifecycleHelper) {
        this.lifecycleHelper = lifecycleHelper;
    }

    public RemapHelper getRemapHelper() {
        return remapHelper;
    }

    public void setRemapHelper(RemapHelper remapHelper) {
        this.remapHelper = remapHelper;
    }

    public Query<Object> createQuery() {
        return new QueryImpl<>(this, Object.class);
    }

    public <T> Query<T> createQuery(Class<T> cls) {
        return new QueryImpl<>(this, getRemapHelper().remap(cls));
    }

    public <T> Query<T> createQuery(Class<T> cls, String collection) {
        return new QueryImpl<>(this, getRemapHelper().remap(cls), collection);
    }

    public WriteResult store(Object object) {
        return store(object, getEntityHelper().getCollectionName(object.getClass()));
    }

    public WriteResult store(Object object, String collection) {
        return store(object, collection, connection.getWriteConcern());
    }

    public WriteResult store(Object object, String collection, WriteConcern concern) {
        if (object == null) {
            throw new MorphixException("Can't store null object in collection");
        }

        if (collection == null) {
            throw new MorphixException("Can't store object in a null collection");
        }

        if (concern == null) {
            throw new MorphixException("Can't store object with a null write concern");
        }

        WriteResult result;
        DBObject dbObject = getMapper().marshal(new MappingData(), object, true);
        if (dbObject.get("_id") == null) {
            getLifecycleHelper().callField(CreatedAt.class, object);
            dbObject = getMapper().marshal(new MappingData(), object, true);
            result = database.getCollection(collection).insert(dbObject, concern);
            // System.out.println("Inserted " + object + " (" + dbObject + ") into '" + getDatabase().getName() + "." + collection + "'");

            ObjectId id = (ObjectId) dbObject.get("_id");
            getEntityHelper().setObjectId(object, id);

            getLifecycleHelper().callMethod(PreCreate.class, object);
            getCache(object.getClass()).put(object);
            getLifecycleHelper().callMethod(PostCreate.class, object);
        } else {
            getLifecycleHelper().callField(UpdatedAt.class, object);
            getLifecycleHelper().callField(CreatedAt.class, object);
            dbObject = getMapper().marshal(new MappingData(), object, true);
            ObjectId id = (ObjectId) dbObject.get("_id");

            DBObject update = new BasicDBObject(dbObject.toMap());
            update.removeField("_id");

            getLifecycleHelper().callMethod(PreSave.class, object);
            result = database.getCollection(collection).update(new BasicDBObject("_id", id), update, false, false, concern);
            // System.out.println("Updated " + result.getN() + " documents (" + dbObject + ") inside '" + getDatabase().getName() + "." + collection + "'");
            getLifecycleHelper().callMethod(PostSave.class, object);
        }

        return result;
    }

}
