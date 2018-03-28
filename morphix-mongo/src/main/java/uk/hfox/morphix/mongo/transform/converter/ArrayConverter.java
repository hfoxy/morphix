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
package uk.hfox.morphix.mongo.transform.converter;

import org.bson.Document;
import uk.hfox.morphix.transform.ConvertedType;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.transform.Transformer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ArrayConverter implements Converter<Document> {

    private Transformer<Document> transformer;

    public ArrayConverter(Transformer<Document> transformer) {
        this.transformer = transformer;
    }

    @Override
    public Object pull(Object value, Object current, Class<?> type) {


        return null;
    }

    @Override
    public Object pull(String key, Document entry, Object value, Class<?> type) {
        return null;
    }

    @Override
    public Object push(Object value, Field field) {
        int size = Array.getLength(value);
        List list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(null);
        }

        for (int i = 0; i < size; i++) {
            Object item = Array.get(value, i);
            if (item == null) {
                list.set(i, null);
                continue;
            }

            ConvertedType type = ConvertedType.findByField(field, item.getClass());
            list.set(i, this.transformer.getConverter(type).push(item, field));
        }

        return list;
    }

    @Override
    public void push(String key, Document entry, Object value, Field field) {
        if (value == null) {
            entry.put(key, null);
            return;
        }

        Object out = push(value, field);
        entry.put(key, out);
    }

}
