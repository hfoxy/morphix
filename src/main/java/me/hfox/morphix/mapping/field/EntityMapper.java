package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.annotation.NotSaved;
import me.hfox.morphix.annotation.Reference;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.annotation.entity.Polymorph;
import me.hfox.morphix.annotation.entity.StoreEmpty;
import me.hfox.morphix.annotation.entity.StoreNull;
import me.hfox.morphix.annotation.lifecycle.PostLoad;
import me.hfox.morphix.annotation.lifecycle.PreLoad;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.util.AnnotationUtils;
import org.bson.types.ObjectId;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EntityMapper<T> extends FieldMapper<T> {

    private Class<T> cls;
    
    private Entity entity;
    private StoreEmpty storeEmpty;
    private StoreNull storeNull;
    private Polymorph polymorph;
    private boolean polymorphEnabled;

    private Reference reference;
    private NotSaved notSaved;

    private Map<Field, FieldMapper> fields;

    public EntityMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
        this.cls = type;
    }

    public Entity getEntity() {
        return entity;
    }

    public StoreEmpty getStoreEmpty() {
        return storeEmpty;
    }

    public StoreNull getStoreNull() {
        return storeNull;
    }

    public Polymorph getPolymorph() {
        return polymorph;
    }

    public boolean isPolymorphEnabled() {
        return polymorphEnabled;
    }

    public Reference getReference() {
        return reference;
    }

    public NotSaved getNotSaved() {
        return notSaved;
    }

    @Override
    protected void discover() {
        super.discover();
        entity = AnnotationUtils.getHierarchicalAnnotation(type, Entity.class);
        storeEmpty = AnnotationUtils.getHierarchicalAnnotation(type, StoreEmpty.class);
        storeNull = AnnotationUtils.getHierarchicalAnnotation(type, StoreNull.class);
        polymorph = AnnotationUtils.getHierarchicalAnnotation(type, Polymorph.class);
        polymorphEnabled = polymorph != null && polymorph.value();

        if (field != null) {
            reference = field.getAnnotation(Reference.class);
            notSaved = field.getAnnotation(NotSaved.class);
        }

        fields = getFields(type);
    }

    @Override
    public T unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        if (reference != null) {
            if (obj instanceof DBRef && reference.dbRef()) {
                DBRef ref = (DBRef) obj;
                return morphix.createQuery(cls, ref.getRef()).field("_id").equal(ref.getId()).get();
            } else if (obj instanceof ObjectId && !reference.dbRef()) {
                ObjectId id = (ObjectId) obj;
                return morphix.createQuery(cls).field("_id").equal(id).get();
            }

            return null;
        }

        if (!(obj instanceof DBObject)) {
            return null;
        }

        DBObject object = (DBObject) obj;
        Class<?> cls = polymorphEnabled ? morphix.getPolymorhpismHelper().generate(object) : type;
        if (cls == null) {
            cls = type;
        }

        Object result = null;
        try {
            result = cls.newInstance();
        } catch (Exception ex) {}

        if (result == null) {
            final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
            final Constructor constructor;
            try {
                // System.out.println("Class: " + cls);
                constructor = reflection.newConstructorForSerialization(cls, Object.class.getDeclaredConstructor());
                // System.out.println("Constructor: " + constructor);
                result = constructor.newInstance();
                // System.out.println("Result: " + result);
            } catch (Exception ex) {
                throw new MorphixException(ex);
            }
        }
        
        if (lifecycle) {
            morphix.getLifecycleHelper().call(PreLoad.class, result);
        }

        Map<Field, FieldMapper> fields = getFields(cls);
        for (Entry<Field, FieldMapper> entry : fields.entrySet()) {
            Field field = entry.getKey();
            // System.out.println("Attempting to pull " + field);

            field.setAccessible(true);

            FieldMapper mapper = entry.getValue();

            Object dbResult = object.get(mapper.fieldName);
            // System.out.println("Result: " + dbResult);
            // if (dbResult == null && (storeNull == null || !storeNull.value())) {
            //     continue;
            // }

            Object value = mapper.unmarshal(dbResult);
            // System.out.println("Mapped: " + value);
            if (storeEmpty == null || !storeEmpty.value()) {
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (collection.isEmpty()) {
                        value = null;
                    }
                } else if (value instanceof Map) {
                    Map map = (Map) value;
                    if (map.isEmpty()) {
                        value = null;
                    }
                }
            }
            
            try {
                field.set(result, value);
            } catch (IllegalAccessException ex) {
                throw new MorphixException(ex);
            }
        }

        if (lifecycle) {
            morphix.getLifecycleHelper().call(PostLoad.class, result);
        }

        return (T) result;
    }

    @Override
    public Object marshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        Class<?> cls = obj.getClass();
        if (reference != null) {
            ObjectId id = morphix.getEntityHelper().getObjectId(obj, true);
            // if (id == null) {
            //     throw new MorphixException("Can't reference an Entity with no id");
            // }

            if (reference.dbRef()) {
                return new DBRef(null, morphix.getEntityHelper().getCollectionName(obj.getClass()), id);
            }

            return id;
        }

        BasicDBObject document = new BasicDBObject();
        if (polymorphEnabled) {
            morphix.getPolymorhpismHelper().store(document, obj.getClass());
        }

        Map<Field, FieldMapper> fields = getFields(cls);
        for (Entry<Field, FieldMapper> entry : fields.entrySet()) {
            Field field = entry.getKey();
            field.setAccessible(true);

            FieldMapper mapper = entry.getValue();

            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException ex) {
                throw new MorphixException(ex);
            }

            if (value == null && (storeNull == null || !storeNull.value())) {
                continue;
            }

            if ((storeEmpty == null || !storeEmpty.value())) {
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (collection.isEmpty()) {
                        continue;
                    }
                } else if (value instanceof Map) {
                    Map map = (Map) value;
                    if (map.isEmpty()) {
                        continue;
                    }
                }
            }

            Object store = mapper.marshal(value);
            document.put(mapper.fieldName, store);
        }

        return document;
    }

    private Map<Field, FieldMapper> getFields(Class<?> cls) {
        if (cls.equals(type) && fields != null) {
            return fields;
        }

        Map<Field, FieldMapper> fields = new HashMap<>();
        for (Field field : morphix.getEntityHelper().getFields(cls)) {
            FieldMapper mapper = createFromField(cls, field, morphix);
            if (mapper != null) {
                fields.put(field, mapper);
            }
        }

        return fields;
    }

}
