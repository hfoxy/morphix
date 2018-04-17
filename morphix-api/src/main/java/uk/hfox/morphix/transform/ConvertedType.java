/*-
 * ========================LICENSE_START========================
 * Morphix API
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
package uk.hfox.morphix.transform;

import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.annotations.field.Id;
import uk.hfox.morphix.annotations.field.Reference;
import uk.hfox.morphix.annotations.field.iterable.CollectionProperties;
import uk.hfox.morphix.annotations.field.iterable.MapProperties;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.exception.mapper.MorphixFieldException;
import uk.hfox.morphix.utils.Conditions;
import uk.hfox.morphix.utils.search.Search;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public enum ConvertedType {

    ARRAY(true, null) {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.isArray();
        }
    },
    COLLECTION(true, CollectionProperties.class) {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return Collection.class.isAssignableFrom(cls);
        }
    },
    MAP(true, MapProperties.class) {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return Map.class.isAssignableFrom(cls);
        }
    },
    BYTE {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(byte.class) || cls.equals(Byte.class);
        }
    },
    SHORT {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(short.class) || cls.equals(Short.class);
        }
    },
    INTEGER {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(int.class) || cls.equals(Integer.class);
        }
    },
    LONG {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(long.class) || cls.equals(Long.class);
        }
    },
    FLOAT {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(float.class) || cls.equals(Float.class);
        }
    },
    DOUBLE {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(double.class) || cls.equals(Double.class);
        }
    },
    CHARACTER {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(char.class) || cls.equals(Character.class);
        }
    },
    BOOLEAN {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(boolean.class) || cls.equals(Boolean.class);
        }
    },
    STRING {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(String.class);
        }
    },
    DATETIME {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return cls.equals(LocalDateTime.class);
        }
    },
    ENTITY {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            Entity entity = Search.getInheritedAnnotation(cls, Entity.class);
            return entity != null;
        }
    },
    REFERENCE {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return ENTITY.isSatisfied(cls);
        }
    },
    ID {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            return false;
        }
    };

    private boolean iterable;
    private Class<? extends Annotation> annotation;

    ConvertedType() {
        this(false, null);
    }

    ConvertedType(boolean iterable, Class<? extends Annotation> annotation) {
        this.iterable = iterable;
        this.annotation = annotation;

        if (iterable) {
            IterableType.types.add(this);
        }
    }

    public boolean isIterable() {
        return iterable;
    }

    public abstract boolean isSatisfied(Class<?> cls);

    public static ConvertedType findByField(Field field, Class<?> clazz) {
        Conditions.notNull(field);
        Conditions.notNull(clazz);

        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            return ID;
        }

        for (ConvertedType type : IterableType.getTypes()) {
            boolean required = type.annotation != null;
            boolean state = !required || field.getAnnotation(type.annotation) != null;
            if (type.isSatisfied(clazz) && state) {
                return type;
            }
        }

        Reference reference = field.getAnnotation(Reference.class);
        if (reference != null) {
            if (!REFERENCE.isSatisfied(clazz)) {
                String message = "field '" + field.getName() + "'" +
                        " in " + field.getDeclaringClass().getName() +
                        " declared as reference but was not an entity";

                throw new MorphixEntityException(message);
            }

            return REFERENCE;
        }

        for (ConvertedType type : values()) {
            if (type.isSatisfied(clazz)) {
                return type;
            }
        }

        String message = "field '" + field.getName() + "'" +
                " in " + field.getDeclaringClass().getName() +
                " has an invalid type";

        throw new MorphixFieldException(message);
    }

    public static ConvertedType findByField(Field field) {
        Class<?> type = null;
        if (field != null) {
            type = field.getType();
        }

        return findByField(field, type);
    }

}
