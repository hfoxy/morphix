package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.mapping.MappingData;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ArrayMapper<T> extends FieldMapper<T> {

    private Class<?> arrayType;
    private int dimensions;
    private FieldMapper<?> mapper;

    public ArrayMapper(MappingData mappingData, Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(mappingData, type, parent, field, morphix);
    }

    public ArrayMapper(MappingData mappingData, Class<T> type, ArrayMapper parent) {
        super(mappingData, type, parent.parent, parent.field, parent.morphix);
    }

    @Override
    protected void discover() {
        super.discover();

        dimensions = 0;
        Class<?> cls = type;
        while (cls.isArray()) {
            dimensions++;
            cls = cls.getComponentType();
        }

        arrayType = cls;
        Class<?> component = type.getComponentType();
        mapper = component.isArray() ? new ArrayMapper<>(mappingData, component, this) : FieldMapper.createFromField(mappingData, component, parent, field, morphix);
    }

    @Override
    public Object unmarshal(Object obj, boolean lifecycle) {
        if (obj == null || !(obj instanceof BasicDBList)) {
            return null;
        }

        BasicDBList list = (BasicDBList) obj;
        int[] sizes = sizes(list);

        Object array = Array.newInstance(arrayType, sizes);
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            Object result = mapper.unmarshal(value);
            Array.set(array, i, result);
        }

        return array;
    }

    private int[] sizes(BasicDBList list) {
        int[] sizes = new int[dimensions];

        int i = 0;
        while (list != null && i < sizes.length) {
            sizes[i] = list.size();
            for (int j = 0; j < list.size(); j++) {
                Object value = list.get(j);
                if (value instanceof BasicDBList) {
                    list = (BasicDBList) value;
                    break;
                }
            }

            i++;
        }

        return sizes;
    }

    @Override
    public BasicDBList marshal(Object obj) {
        return marshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public BasicDBList marshal(Object obj, boolean lifecycle) {
        if (obj == null || !obj.getClass().isArray()) {
            return null;
        }

        BasicDBList list = new BasicDBList();

        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(obj, i);
            Object result = mapper.marshal(value);
            list.add(result);
        }

        return list;
    }

}
