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
import uk.hfox.morphix.utils.Search;

public enum ConvertedType {

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
    ENTITY {
        @Override
        public boolean isSatisfied(Class<?> cls) {
            Entity entity = Search.getInheritedAnnotation(cls, Entity.class);
            return entity != null;
        }
    };

    public abstract boolean isSatisfied(Class<?> cls);

}
