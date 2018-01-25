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
package me.hfox.morphix.mongo.mapping.field;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.mapping.MappingData;

import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapMapper extends FieldMapper<Map> {

    protected Type[] types;
    protected FieldMapper[] mappers;

    public MapMapper(Class<?> parent, Field field, Morphix morphix) {
        super(Map.class, parent, field, morphix);
    }

    public MapMapper(MapMapper parent, ParameterizedType type) {
        super(Map.class, parent.parent, parent.field, parent.morphix, false);
        types = type.getActualTypeArguments();
        discover();
    }

    public MapMapper(CollectionMapper parent) {
        super(Map.class, parent.parent, parent.field, parent.morphix, false);
        types = ((ParameterizedType) parent.type).getActualTypeArguments();
        discover();
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null && types == null) {
            types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        }

        mappers = new FieldMapper[]{find(types[0]), find(types[1])};
    }

    @Override
    public Map unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map unmarshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        Map map = new HashMap<>();
        if (obj instanceof BasicDBList) {
            BasicDBList list = (BasicDBList) obj;
            for (Object object : list) {
                if (object instanceof DBObject) {
                    DBObject dbObject = (DBObject) object;
                    Object key = mappers[0].unmarshal(dbObject.get("key"), lifecycle);
                    Object value = mappers[1].unmarshal(dbObject.get("value"), lifecycle);
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        BasicDBList list = new BasicDBList();

        if (obj instanceof Map) {
            Map map = (Map) obj;
            for (Object obj2 : map.entrySet()) {
                if (obj2 instanceof Entry) {
                    Entry entry = (Entry) obj2;
                    BasicDBObject dbObject = new BasicDBObject();
                    dbObject.put("key", mappers[0].marshal(mappingData, entry.getKey()));
                    dbObject.put("value", mappers[1].marshal(mappingData, entry.getValue()));
                    list.add(dbObject);
                }
            }
        }

        return list;
    }

    private FieldMapper find(Type type) {
        FieldMapper mapper = null;
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            mapper = FieldMapper.createFromField(cls, parent, field, morphix);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) type;
            if (param.getRawType() instanceof Class) {
                Class<?> cls = (Class) param.getRawType();
                if (Collection.class.isAssignableFrom(cls)) {
                    mapper = new CollectionMapper(this, param);
                } else if (Map.class.isAssignableFrom(cls)) {
                    mapper = new MapMapper(this, param);
                }
            }
        } else if (type instanceof TypeVariable) {
            // System.out.println("Field: " + (field != null ? field : "null"));
            // System.out.println("Parent: " + parent);

            int position = 0;
            TypeVariable typeVar = (TypeVariable) type;
            // display(typeVar);
            GenericDeclaration declaration = typeVar.getGenericDeclaration();
            TypeVariable<?>[] typeParameters = declaration.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                TypeVariable parameter = typeParameters[i];
                // display("Parameter: ", parameter);
                if (type.equals(parameter)) {
                    // System.out.println(parameter + " matches");
                    position = i;
                    break;
                }
            }

            // System.out.println("Generic Superclass: " + parent.getGenericSuperclass());
            Type superclass = parent.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                ParameterizedType superParam = (ParameterizedType) superclass;
                if (superParam.getActualTypeArguments().length > 0) {
                    // System.out.println("Generic Superclass: Type Arguments: " + superParam.getActualTypeArguments().length);
                    Type[] actualTypeArguments = superParam.getActualTypeArguments();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        Type variable = actualTypeArguments[i];
                        if (i == position) {
                            return find(variable);
                        }
                    }
                }
            }

            // display((TypeVariable) type);
        }

        return mapper;
    }

    /*
    private void display(TypeVariable variable) {
        display("", variable);
    }

    private void display(String prefix, TypeVariable variable) {
        System.out.println(prefix + "TypeVariable: " + variable);
        System.out.println(prefix + "TypeVariable: Name: " + variable.getName());
        System.out.println(prefix + "TypeVariable: Type Name: " + variable.getTypeName());
        System.out.println(prefix + "TypeVariable: Bounds: " + Arrays.asList(variable.getBounds()));
        System.out.println(prefix + "TypeVariable: Generic Declaration: " + variable.getGenericDeclaration());

        GenericDeclaration declaration = variable.getGenericDeclaration();
        System.out.println(prefix + "TypeVariable: Generic Declaration: Type Parameters: " + Arrays.asList(declaration.getTypeParameters()));
    }
    */

}
