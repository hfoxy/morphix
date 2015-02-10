package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import me.hfox.morphix.Morphix;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ArrayMapper<T> extends FieldMapper<T> {

    private Class<?> type;
    private FieldMapper<?> mapper;
    private int dimensions;

    public ArrayMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    protected void discover() {
        super.discover();

        dimensions = 0;
        Class<?> type = super.type;
        while (type.isArray()) {
            type = type.getComponentType();
            dimensions++;
        }

        this.type = type;
        mapper = FieldMapper.create(type, parent, null, morphix);
    }

    @Override
    public Object marshal(Object obj) {
        if (obj == null || !(obj instanceof BasicDBList)) {
            return null;
        }

        int[] sizes = new int[dimensions];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = 0;
        }

        BasicDBList origin = (BasicDBList) obj;
        BasicDBList list = origin;
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = list.size();

            Object entry = list.get(i);
            if (entry == null || !(entry instanceof BasicDBList)) {
                break;
            }

            list = (BasicDBList) entry;
        }

        list = origin;
        Object array = Array.newInstance(type, sizes);
        array(array, list, sizes, true);

        return array;
    }

    public Object array(Object array, BasicDBList list, int[] sizes, boolean first) {
        if (!first) {
            int[] updated = new int[sizes.length - 1];
            for (int j = 1; j < sizes.length; j++) {
                updated[j - 1] = sizes[j];
            }

            sizes = updated;
        }

        for (int i = 0; i < list.size(); i++) {
            Object entry = list.get(i);

            Object value;
            if (entry instanceof BasicDBList) {
                BasicDBList entryList = (BasicDBList) entry;
                value = array(Array.newInstance(type, sizes), entryList, sizes, false);
            } else {
                value = mapper.marshal(entry);
            }

            Array.set(array, i, value);
        }

        return list;
    }

    @Override
    public BasicDBList unmarshal(Object obj) {
        if (obj == null || !obj.getClass().isArray()) {
            return null;
        }

        return array(obj);
    }

    public BasicDBList array(Object object) {
        BasicDBList list = new BasicDBList();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(object, i);
            if (item.getClass().isArray()) {
                list.addAll(array(item));
            } else {
                list.add(mapper.unmarshal(item));
            }
        }

        return list;
    }

}
