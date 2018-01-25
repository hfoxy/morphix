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
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.exception.MorphixException;
import me.hfox.morphix.mongo.mapping.MappingData;

import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CollectionMapper extends FieldMapper<Collection> {

    protected Type type;
    protected FieldMapper<?> mapper;

    public CollectionMapper(Class<?> parent, Field field, Morphix morphix) {
        super(Collection.class, parent, field, morphix);
    }

    public CollectionMapper(CollectionMapper parent) {
        super(Collection.class, parent.parent, parent.field, parent.morphix, false);
        this.type = ((ParameterizedType) parent.type).getActualTypeArguments()[0];
        discover();
    }

    public CollectionMapper(MapMapper parent, ParameterizedType type) {
        super(Collection.class, parent.parent, parent.field, parent.morphix, false);
        this.type = type.getActualTypeArguments()[0];
        discover();
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null && type == null) {
            type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }

        mapper = null;
        find(type);

        if (mapper == null) {
            // System.out.println("Field: " + (field != null ? field : "null"));
            // System.out.println("Type: " + type + ", Class: " + type.getClass());
            // System.out.println("Parent: " + parent);
            throw new MorphixException("Could not find suitable Mapper for " + type);
        }
    }

    private boolean find(Type type) {
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            mapper = FieldMapper.createFromField(cls, parent, field, morphix);
            return true;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) type;
            if (param.getRawType() instanceof Class) {
                Class<?> cls = (Class) param.getRawType();
                if (Collection.class.isAssignableFrom(cls)) {
                    mapper = new CollectionMapper(this);
                    return true;
                } else if (Map.class.isAssignableFrom(cls)) {
                    mapper = new MapMapper(this);
                    return true;
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
                    Type variable = actualTypeArguments[position];
                    // System.out.println(variable);

                    return find(variable);
                }
            }

            // display((TypeVariable) type);
        }

        return false;
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

    @Override
    public Collection unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection unmarshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        Collection collection = new ArrayList();
        if (obj instanceof BasicDBList) {
            BasicDBList list = (BasicDBList) obj;
            for (Object object : list) {
                collection.add(mapper.unmarshal(object, lifecycle));
            }
        }

        return collection;
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        BasicDBList list = new BasicDBList();

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            for (Object object : collection) {
                list.add(mapper.marshal(mappingData, object, lifecycle));
            }
        }

        return list;
    }

}
